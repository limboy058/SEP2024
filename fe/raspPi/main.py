from aip import AipSpeech
import speech_recognition as sr
import time, requests, base64
from playsound import playsound
from pydub import AudioSegment
from pydub.playback import play
import subprocess
from picamera2 import Picamera2, Preview
from libcamera import Transform

# temporary file paths
TMP_AUDIO_PATH = "//tmp//audio.mp3"
TMP_IMAGE_PATH = "//tmp//image.jpg"

# awaking mode
MODE_ON = 1
MODE_OFF = 0
SYSTEM_MODE = MODE_OFF
LAST_CALL_TIME = 0

# function mode
FUNC_MODE_QINGJING = 1
FUNC_MODE_WENZI = 2
FUNC_MODE = FUNC_MODE_QINGJING

# url for backend server
url = r"http://101.132.112.59:8123/VisionTalk"

# 配置badiu API接口所需要的参数
APP_ID = '__baidu app id__'  
API_KEY = '__baidu AI key__'  
SECRET_KEY = '__baidu AI secret key__'  

# client for listening speech
client = AipSpeech(APP_ID, API_KEY, SECRET_KEY)  

# camera set up
picam2 = Picamera2()
config = picam2.create_preview_configuration(transform = Transform(vflip = True, hflip = True))
picam2.configure(config) 
picam2.start()


# somehow the function keeps failing to run in this process
# and I don't know why.
# maybe it conflicts with speech_recognition or something else
# playsound(TMP_AUDIO_PATH).

# so I ended up running it in another process.
# and it worked.
def play_voice(file_path):
    child = subprocess.Popen(['python3', './PlaySound.py', file_path])
    child.wait()


def STT(au):
    """
    sound to text
    """
    result = client.asr(au, 'wav', 16000, {'dev_pid': 1537})  
    result_text = result["result"][0]  
    return result_text
    
def TTS(tex): 
    """
    text to sound
    """
    result = client.synthesis(tex, 'zh', 1, {'vol': 9, 'per':1,})  
    return result

def get_ans(question: str):
    files = {'photo': open(TMP_IMAGE_PATH, 'rb')}
    data = {'kind': str(FUNC_MODE), 'question': question}
    response = requests.post(url, files=files, data=data)
    return response.text
    
def waitAndAnswerQuestion():
    r = sr.Recognizer()
    with sr.Microphone(sample_rate = 16000) as source:
        print("I'm waiting!")
        audio = r.listen(source)
        try:
            #convert speech to text
            text = STT(audio.frame_data)
            print("your question: " + text)
            
            if text == "没事。":
                play_voice("./voices/o.mp3")
                return
                
            if text == "切换到情景识别。" or text == "切换到石井识别。" or text == "切换到实景识别。":
                FUNC_MODE = FUNC_MODE_QINGJING
                play_voice("./voices/qingjingshibie.mp3")
                return

            if text == "切换到文字识别。":
                FUNC_MODE = FUNC_MODE_QINGJING
                play_voice("./voices/wenzishibie.mp3")
                return
                
            # take photo
            image = picam2.capture_image("main")
            image.save(TMP_IMAGE_PATH)

            # get the answer from backend
            answer = get_ans(text)
            # answer = text
            print(answer)

            # convert text to audio and save as a tmp file
            ret_audio = TTS(answer)
            if not isinstance(audio, dict):
                with open(TMP_AUDIO_PATH, 'wb') as f:
                    f.write(ret_audio)
            
            # play the audio just saved
            play_voice(TMP_AUDIO_PATH)
            
            time.sleep(1)
        except BaseException as e:
            print(e)
            
            
# obtain audio from the microphone
r = sr.Recognizer()

def main():
    while True:
        r = sr.Recognizer()
        # wait for speech
        with sr.Microphone(sample_rate = 16000) as source:
            print("Say something!")
            audio = r.listen(source)
            try:
                #convert speech to text
                text = STT(audio.frame_data)
                print("you said " + text)
                
                if text == "我不知道。":
                    continue
                    
                if text =="小梅小梅。":
                    play_voice("./voices/ninshuo.mp3")
                    SYSTEM_MODE = MODE_ON
                    LAST_CALL_TIME = time.time()
                    
                if time.time() - LAST_CALL_TIME > 3:
                    SYSTEM_MODE = MODE_OFF
                
                if (SYSTEM_MODE == MODE_OFF):
                    continue
                
                waitAndAnswerQuestion()

            except BaseException as e:
                print(e)
                
if __name__ == "__main__":
    main()
