using System;
using UnityEngine;

#if !UNITY_EDITOR && UNITY_WSA
using Windows.Foundation;
using Windows.Media.SpeechSynthesis;
using Windows.Storage.Streams;
using System.Linq;
using System.Threading.Tasks;
#endif

namespace HoloToolkit.Unity
{
    /// <summary>
    /// 知名的可用声音.
    /// </summary>
    public enum TextToSpeechVoice
    {
        /// <summary>
        ///系统默认声音.
        /// </summary>
        Default,

        /// <summary>
        /// 大卫 移动
        /// </summary>
        David,

        /// <summary>
        ///马克 移动
        /// </summary>
        Mark,

        /// <summary>
        /// 兹拉 移动
        /// </summary>
        Zira,

        /// <summary>
        /// 瑶瑶（谐音） 中文
        /// </summary>
        Yaoyao,

        /// <summary>
        /// 灰灰（谐音） 中文
        /// </summary>
        Huihui,

        /// <summary>
        /// 康康（谐音） 中文
        /// </summary>
        Kangkang,
    }

    /// <remarks>
    /// <see cref="SpeechSynthesizer"/>生成语音<see cref="SpeechSynthesisStream"/>. 
    /// 这个类将流转换为UnityAudioClip 并使用你在inspector中提供的AudioSource播放
    /// 这可以让你在3D空间中定位声音。推荐的方法是将AudioSource放置在空对象上，
    /// 并将它设为主摄像头的子对象，将其放置在相机上方的大约0.6个单位。
    /// 这个听起来类似于Cortana在操作系统中的讲话。
    /// </remarks>
    [RequireComponent(typeof(AudioSource))]
    public class TextToSpeech : MonoBehaviour
    {
        [Tooltip("播放语音的AudioSource")]
        [SerializeField]
        private AudioSource audioSource;
        public static TextToSpeech Instance = null;
        /// <summary>
        /// 获取或设置播放语音的AudioSource.
        /// </summary>
        public AudioSource AudioSource { get { return audioSource; } set { audioSource = value; } }

        /// <summary>
        ///获取或设置用于生成语音的声音.
        /// </summary>
        public TextToSpeechVoice Voice { get { return voice; } set { voice = value; } }

        [Tooltip("生成语音的声音")]
        [SerializeField]
        private TextToSpeechVoice voice;

#if !UNITY_EDITOR && UNITY_WSA
        private SpeechSynthesizer synthesizer;
        private VoiceInformation voiceInfo;
        private bool speechTextInQueue = false;
#endif

        /// <summary>
        /// 转换2个字节为-1至1的浮点数
        /// </summary>
        /// <param name="firstByte">第一个字节</param>
        /// <param name="secondByte">第二个字节</param>
        /// <returns>转换的值</returns>
        private static float BytesToFloat(byte firstByte, byte secondByte)
        {
            // 转换两个字节为short（从小到大）
            short s = (short)((secondByte << 8) | firstByte);

            // 转换为 -1 至 (略低于) 1
            return s / 32768.0F;
        }

        /// <summary>
        /// 转换字节数组为int.
        /// </summary>
        /// <param name="bytes"> 字节数组</param>
        /// <param name="offset"> 读取偏移.</param>
        /// <returns>转换后的int.</returns>
        private static int BytesToInt(byte[] bytes, int offset = 0)
        {
            int value = 0;
            for (int i = 0; i < 4; i++)
            {
                value |= ((int)bytes[offset + i]) << (i * 8);
            }
            return value;
        }

        /// <summary>
        /// 动态创建一个AudioClip音频数据。
        /// </summary>
        /// <param name="name"> 动态生成的AudioClip的名称。</param>
        /// <param name="audioData">音频数据.</param>
        /// <param name="sampleCount">音频数据中的样本数。</param>
        /// <param name="frequency">音频数据的频率。</param>
        /// <returns>AudioClip</returns>
        private static AudioClip ToClip(string name, float[] audioData, int sampleCount, int frequency)
        {
            var clip = AudioClip.Create(name, sampleCount, 1, frequency, false);
            clip.SetData(audioData, 0);
            return clip;
        }

        /// <summary>
        /// 转换原始WAV数据为统一格式的音频数据。
        /// </summary>
        /// <param name="wavAudio">WAV数据.</param>
        /// <param name="sampleCount">音频数据中的样本数.</param>
        /// <param name="frequency">音频数据的频率.</param>
        /// <returns>统一格式的音频数据. </returns>
        private static float[] ToUnityAudio(byte[] wavAudio, out int sampleCount, out int frequency)
        {
            // 确定是单声道还是立体声
            int channelCount = wavAudio[22];

            // 获取频率
            frequency = BytesToInt(wavAudio, 24);

            // 通过所有其他子块，以获得数据子块:
            int pos = 12; // 第一个子块ID从12到16

            // 不断迭代，直到找到数据块 (即 64 61 74 61 ...... (即 100 97 116 97 十进制))
            while (!(wavAudio[pos] == 100 && wavAudio[pos + 1] == 97 && wavAudio[pos + 2] == 116 && wavAudio[pos + 3] == 97))
            {
                pos += 4;
                int chunkSize = wavAudio[pos] + wavAudio[pos + 1] * 256 + wavAudio[pos + 2] * 65536 + wavAudio[pos + 3] * 16777216;
                pos += 4 + chunkSize;
            }
            pos += 8;

            // Pos现在被定位为开始实际声音数据。
            sampleCount = (wavAudio.Length - pos) / 2;    // 每个样本2字节(16位单声道)
            if (channelCount == 2) { sampleCount /= 2; }  // 每个样本4字节(16位立体声)

            // 分配内存(仅支持左通道)
            var unityData = new float[sampleCount];

            //写入数组:
            int i = 0;
            while (pos < wavAudio.Length)
            {
                unityData[i] = BytesToFloat(wavAudio[pos], wavAudio[pos + 1]);
                pos += 2;
                if (channelCount == 2)
                {
                    pos += 2;
                }
                i++;
            }

            return unityData;
        }

#if !UNITY_EDITOR && UNITY_WSA
        /// <summary>
        /// 执行一个生成语音流的函数，然后在Unity中转换并播放它。
        /// </summary>
        /// <param name="text">
        /// 内容.
        /// </param>
        /// <param name="speakFunc">
        /// 执行以生成语音的实际函数
        /// </param>
        private void PlaySpeech(string text, Func<IAsyncOperation<SpeechSynthesisStream>> speakFunc)
        {
            //确保有内容
            if (speakFunc == null) throw new ArgumentNullException(nameof(speakFunc));
            //语速
            synthesizer.Options.SpeakingRate = 2.0;

            if (synthesizer != null)
            {
                try
                {
                    speechTextInQueue = true;
                    // 需要await，因此大部分将作为一个新任务在自己的线程中运行。
                    // 这是件好事，因为它解放了Unity，让它可以继续运行。
                    Task.Run(async () =>
                    {
                        // 换声?
                        if (voice != TextToSpeechVoice.Default)
                        {
                            // 获得名称
                            var voiceName = Enum.GetName(typeof(TextToSpeechVoice), voice);

                            // 查看它是一直没被找到还是有改变
                            if ((voiceInfo == null) || (!voiceInfo.DisplayName.Contains(voiceName)))
                            {
                                // 搜索声音信息
                                voiceInfo = SpeechSynthesizer.AllVoices.Where(v => v.DisplayName.Contains(voiceName)).FirstOrDefault();

                                // 如果找到则选中
                                if (voiceInfo != null)
                                {
                                    synthesizer.Voice = voiceInfo;
                                }
                                else
                                {
                                    Debug.LogErrorFormat("TTS 无法找到声音 {0}。", voiceName);
                                }
                            }
                        }

                        // 播放语音并获得流
                        var speechStream = await speakFunc();

                        // 获取原始流的大小
                        var size = speechStream.Size;

                        // 创建 buffer
                        byte[] buffer = new byte[(int)size];

                        // 获取输入流和原始流的大小
                        using (var inputStream = speechStream.GetInputStreamAt(0))
                        {
                            // 关闭原始的语音流，释放内存
                            speechStream.Dispose();

                            // 从输入流创建一个新的DataReader
                            using (var dataReader = new DataReader(inputStream))
                            {
                                //将所有字节加载到reader
                                await dataReader.LoadAsync((uint)size);

                                // 复制reader到buffer
                                dataReader.ReadBytes(buffer);
                            }
                        }

                        // 转换原始WAV数据为统一格式的音频数据
                        int sampleCount = 0;
                        int frequency = 0;
                        var unityData = ToUnityAudio(buffer, out sampleCount, out frequency);

                        // 剩下的工作须在Unity的主线程中完成
                        UnityEngine.WSA.Application.InvokeOnAppThread(() =>
                        {
                            // 转换为audio clip
                            var clip = ToClip("Speech", unityData, sampleCount, frequency);

                            // 设置audio clip的语音
                            audioSource.clip = clip;

                            // 播放声音
                            audioSource.Play();
                            speechTextInQueue = false;
                        }, false);
                    });
                }
                catch (Exception ex)
                {
                    speechTextInQueue = false;
                    Debug.LogErrorFormat("语音生成错误: \"{0}\"", ex.Message);
                }
            }
            else
            {
                Debug.LogErrorFormat("语音合成器未初始化. \"{0}\"", text);
            }
        }
#endif

        private void Awake()
        {
            try
            {
                Debug.Log("TextToSpeech awaking");

                if (audioSource == null)
                {
                    audioSource = GetComponent<AudioSource>();
                }
#if !UNITY_EDITOR && UNITY_WSA
        synthesizer = new SpeechSynthesizer();
#endif
                Instance = this;
                Debug.Log("TextToSpeech instance initialized successfully.");
            }
            catch (Exception ex)
            {
                Debug.LogError("Cannot start TextToSpeech: " + ex.Message);
            }
        }


        // 公共方法

        /// <summary>
        /// 播放指定SSML标记语音.
        /// </summary>
        /// <param name="ssml">SSML标记</param>
        public void SpeakSsml(string ssml)
        {
            // 确保内容不为空
            if (string.IsNullOrEmpty(ssml)) { return; }

            // 传递给辅助方法
#if !UNITY_EDITOR && UNITY_WSA
            PlaySpeech(ssml, () => synthesizer.SynthesizeSsmlToStreamAsync(ssml));
#else
            Debug.LogWarningFormat("文字转语音在编辑器下不支持.\n\"{0}\"", ssml);
#endif
        }

        /// <summary>
        /// 播放指定文本语音.
        /// </summary>
        /// <param name="text">文本内容</param>
        public void StartSpeaking(string text)
        {
            // 确保内容不为空
            if (string.IsNullOrEmpty(text)) { return; }

            // 传递给辅助方法
#if !UNITY_EDITOR && UNITY_WSA
            PlaySpeech(text, ()=> synthesizer.SynthesizeTextToStreamAsync(text));
#else
            Debug.LogWarningFormat("文字转语音在编辑器下不支持.\n\"{0}\"", text);
#endif
        }

        /// <summary>
        /// 返回一个文本是否被提交并被PlaySpeech方法处理
        /// 方便避免当文本提交，但音频剪辑还没有准备好，因为音频源还没有播放的情况。
        /// </summary>
        /// <returns></returns>
        public bool SpeechTextInQueue()
        {
#if !UNITY_EDITOR && UNITY_WSA
            return speechTextInQueue;
#else
            return false;
#endif
        }

        /// <summary>
        /// 是否在播放语音.
        /// </summary>
        /// <returns>
        /// True, 在播放. False,未播放.
        /// </returns>
        public bool IsSpeaking()
        {
            if (audioSource != null)
            {
                return audioSource.isPlaying;
            }

            return false;
        }

        /// <summary>
        /// 停止播放语音.
        /// </summary>
        public void StopSpeaking()
        {
            if (IsSpeaking())
            {
                audioSource.Stop();
            }
        }
    }
}

