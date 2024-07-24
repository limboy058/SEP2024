using System;
using System.IO;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using UnityEngine;
using UnityEngine.Networking;
using UnityEngine.Windows.Speech;
using UnityEngine.XR.WSA.WebCam;
using System.Text.RegularExpressions;
//using SampleSynthesis;
//using SpeechLib;
using HoloToolkit.Unity;

public class SpeechManager : MonoBehaviour
{
    KeywordRecognizer keywordRecognizer = null;
    Dictionary<string, System.Action> keywords = new Dictionary<string, System.Action>();

    private PhotoCapture photoCaptureObject = null;
    private string savedPhotoFilePath;

    int kind = 0;

    void OnPhotoCaptureCreated(PhotoCapture captureObject)
    {
        photoCaptureObject = captureObject;

        Resolution cameraResolution = PhotoCapture.SupportedResolutions.OrderByDescending((res) => res.width * res.height).First();

        CameraParameters c = new CameraParameters();
        c.hologramOpacity = 0.0f;
        c.cameraResolutionWidth = cameraResolution.width;
        c.cameraResolutionHeight = cameraResolution.height;
        c.pixelFormat = CapturePixelFormat.BGRA32;

        captureObject.StartPhotoModeAsync(c, OnPhotoModeStarted);
    }

    void OnCapturedPhotoToDisk(PhotoCapture.PhotoCaptureResult result)
    {
        if (result.success)
        {
            Debug.Log("Saved Photo to disk!");
            StartCoroutine(UploadPhotoToServer(savedPhotoFilePath));
            Debug.Log("upload Photo to server!");

            photoCaptureObject.StopPhotoModeAsync(OnStoppedPhotoMode);
        }
        else
        {
            Debug.Log("Failed to save Photo to disk");
        }
    }

    private void OnPhotoModeStarted(PhotoCapture.PhotoCaptureResult result)
    {
        if (result.success)
        {
            string filename = string.Format(@"CapturedImage{0}.jpg", Time.time);
            savedPhotoFilePath = Path.Combine(Application.persistentDataPath, filename);
            photoCaptureObject.TakePhotoAsync(savedPhotoFilePath, PhotoCaptureFileOutputFormat.JPG, OnCapturedPhotoToDisk);
            Debug.Log("photo saved to: " + savedPhotoFilePath);
        }
        else
        {
            Debug.LogError("Unable to start photo mode!");
        }
    }

    void OnStoppedPhotoMode(PhotoCapture.PhotoCaptureResult result)
    {
        photoCaptureObject.Dispose();
        photoCaptureObject = null;
    }

    void Start()
    {
        keywords.Add("拍照", () =>
        {
            Debug.Log("识别到拍照！");
            Debug.Log(TextToSpeech.Instance);
            kind = 1;
            if (TextToSpeech.Instance)
                TextToSpeech.Instance.StartSpeaking("现在开始拍照");
            PhotoCapture.CreateAsync(false, OnPhotoCaptureCreated);

            Debug.Log("TextToSpeech结束！");

        });

        keywords.Add("读文字", () =>
        {
            Debug.Log("识别到读文字！");
            Debug.Log(TextToSpeech.Instance);
            kind=2;
            if (TextToSpeech.Instance)
                TextToSpeech.Instance.StartSpeaking("现在开始文字识别");
            PhotoCapture.CreateAsync(false, OnPhotoCaptureCreated);

            Debug.Log("TextToSpeech结束！");

        });

        keywordRecognizer = new KeywordRecognizer(keywords.Keys.ToArray());
        keywordRecognizer.OnPhraseRecognized += KeywordRecognizer_OnPhraseRecognized;
        keywordRecognizer.Start();
    }

    private void KeywordRecognizer_OnPhraseRecognized(PhraseRecognizedEventArgs args)
    {
        System.Action keywordAction;
        if (keywords.TryGetValue(args.text, out keywordAction))
        {
            keywordAction.Invoke();
        }
    }


    private IEnumerator UploadPhotoToServer(string filePath)
    {
        Debug.Log("Start uploading photo!");
        // Check file path
        if (!File.Exists(filePath))
        {
            Debug.LogError("File not found at: " + filePath);
            yield break;
        }

        byte[] fileData = File.ReadAllBytes(filePath);
        Texture2D texture = new Texture2D(2, 2);
        if (!texture.LoadImage(fileData))
        {
            Debug.LogError("Failed to load image.");
            yield break;
        }

        // Compress the image to JPEG format with quality set to 75%
        byte[] compressedFileData = texture.EncodeToJPG(100);
        Debug.Log("File loaded and compressed into byte array.");

        WWWForm form = new WWWForm();
        form.AddBinaryData("photo", compressedFileData, Path.GetFileName(filePath), "image/jpeg");
        form.AddField("kind", kind.ToString());
        Debug.Log("Photo added to form.");

        UnityWebRequest www = UnityWebRequest.Post("http://101.132.112.59:8123/VisionTalk", form);
        Debug.Log("Attempting to connect to server.");
        //www.SetRequestHeader("kind", kind.ToString());
        yield return www.SendWebRequest();

#if UNITY_2020_2_OR_NEWER
    if (www.result != UnityWebRequest.Result.Success)
#else
        if (www.isNetworkError || www.isHttpError)
#endif
        {
            Debug.LogError("Error uploading file: " + www.error);
        }
        else
        {
            Debug.Log("File uploaded successfully: " + www.downloadHandler.text);

            // Parse JSON response
            string jsonResponse = www.downloadHandler.text;
            //string message = ParseMessageFromJson(jsonResponse);
            //if (TextToSpeech.Instance)
            //    TextToSpeech.Instance.StartSpeaking(message);
            //Debug.Log(message);、
            if (TextToSpeech.Instance)
                TextToSpeech.Instance.StartSpeaking(jsonResponse);
            Debug.Log(jsonResponse);

            // Initiate text-to-speech
            //yield return SpeakText(message);

        }
    }

    private string ParseMessageFromJson(string jsonResponse)
    {
        if (string.IsNullOrEmpty(jsonResponse))
        {
            Debug.LogError("jsonResponse is null or empty");
            return "No message found.";
        }

        try
        {
            // 调试日志
            Debug.Log("Parsing JSON response: " + jsonResponse);


            string pattern = @"""response""\s*:\s*""([^""]*)""";
            Match match = Regex.Match(jsonResponse, pattern);

            if (match.Success)
            {
                string response = match.Groups[1].Value;

                // 调试日志
                Debug.Log("Parsed response: " + response);

                string decodedResponse = DecodeUnicodeString(response);

                // 调试日志
                Debug.Log("Decoded response: " + decodedResponse);

                return decodedResponse;
            }
            else
            {
                Debug.LogError("JSON parsing failed or 'response' key not found");
                return "No message found.";
            }

        }
        catch (System.Exception e)
        {
            Debug.LogError("Error parsing JSON: " + e.Message);
            return "No message found.";
        }
    }

    private string DecodeUnicodeString(string unicodeString)
    {
        if (string.IsNullOrEmpty(unicodeString))
        {
            Debug.LogError("Input string is null or empty");
            return "No message found.";
        }

        try
        {
            // 正则表达式匹配Unicode编码的字符
            string pattern = @"\\u([0-9A-Fa-f]{4})";
            string decodedString = Regex.Replace(unicodeString, pattern, match =>
            {
                // 将每个匹配的Unicode编码转为对应的字符
                string unicodeHex = match.Groups[1].Value;
                int code = Convert.ToInt32(unicodeHex, 16);
                return ((char)code).ToString();
            });

            return decodedString;
        }
        catch (Exception e)
        {
            Debug.LogError("Error decoding Unicode string: " + e.Message);
            return "Error decoding string.";
        }
    }



}

