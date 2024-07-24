#this file is important for service


from flask import Flask, request, jsonify  # 导入Flask、request和jsonify模块，用于创建和处理Flask应用程序
import os
import torch
from PIL import Image  # 导入PIL库的Image模块，用于处理图像
from transformers import AutoConfig, AutoModel, AutoTokenizer  # 导入transformers库的相关模块，用于加载模型和分词器
from accelerate import init_empty_weights, infer_auto_device_map, load_checkpoint_in_model, dispatch_model  # 导入accelerate库的相关模块，用于模型分布和加载
import logging  # 导入logging模块，用于日志记录
import requests  # 导入requests模块，用于处理HTTP请求
from io import BytesIO  # 导入io模块的BytesIO，用于处理字节数据流
import json
import redis
import time
import glob
import sys

import numpy as np
import cv2
from io import BytesIO
sys.path.append(r'/home/wy/miniCPM/fr_main')#todo
from fr_main.retinaface import Retinaface


retinaface_encoder = Retinaface(1)
retinaface_tester = Retinaface()

# 初始化Flask应用程序
#app = Flask(__name__)

# 配置日志记录
logging.basicConfig(level=logging.INFO)

MODEL_PATH = 'MiniCPM-Llama3-V-2_5'  # 指定模型路径，可以预先下载或者使用远程模型路径
max_memory_each_gpu = '12GiB'  # 定义每个GPU使用的最大内存
gpu_device_ids = [0, 1]  # 定义使用的GPU设备ID
no_split_module_classes = ["LlamaDecoderLayer"]  # 指定不分割的模块类

max_memory = {device_id: max_memory_each_gpu for device_id in gpu_device_ids}  # 构建每个GPU的最大内存字典
config = AutoConfig.from_pretrained(MODEL_PATH, trust_remote_code=True)  # 从预训练模型路径加载配置
tokenizer = AutoTokenizer.from_pretrained(MODEL_PATH, trust_remote_code=True)  # 从预训练模型路径加载分词器

# 初始化空权重
with init_empty_weights():
    model = AutoModel.from_config(config, torch_dtype=torch.float16, trust_remote_code=True)  # 从配置中加载模型，使用float16数据类型

# 推断设备映射
device_map = infer_auto_device_map(model, max_memory=max_memory, no_split_module_classes=no_split_module_classes)

# 手动指定设备映射
device_map["llm.model.embed_tokens"] = 0
device_map["llm.model.layers.0"] = 0
device_map["llm.lm_head"] = 0
device_map["vpm"] = 0
device_map["resampler"] = 0

# 加载模型检查点到模型中
load_checkpoint_in_model(model, MODEL_PATH, device_map=device_map)
# 分布模型到设备映射
model = dispatch_model(model, device_map=device_map)

# 设置不启用梯度计算
torch.set_grad_enabled(False)
model.eval()  # 设置模型为评估模式






# @app.route('/chat', methods=['POST'])
# def chat():
#     """
#     处理/chat路由的POST请求

#     请求数据:
#     - image_url: 图像的URL地址
#     - content: 用户输入的文本内容

#     返回值:
#     - JSON响应,包含生成的文本
#     """

#     data = request.json

#     logging.info(f"Connection received with data: \n---\n{data}---\n")

#     # todo :根据json加载历史消息
#     msgs = []
#     #msgs like [{'role': 'user', 'content': content},{"role": "assistant", "content": answer}]

#     content = []

#     chat_type = data.get("f","1")
#     chats=json.loads(data.get("chat"))
#     for json_data in chats:
#         # 解析 JSON 字符串为 Python 字典
#         # 获取第一个键
#         first_key = next(iter(json_data))
#         if(first_key=="user"):
#             content.append(json_data[first_key])
#             msgs.append({'role':'user','content':content})
#             content=[]
#             print("user")
#         elif (first_key=="url"):
#             print("url")
#             try:
#                 response = requests.get(json_data[first_key])
#                 response.raise_for_status()
#                 image = Image.open(BytesIO(response.content)).convert('RGB')
#                 content.append(image)
#             except Exception as e:
#                 logging.error(f"Failed to download image: {e}")
#                 return jsonify({"error": "Invalid image URL"}), 400
#         elif (first_key=="assistant"):
#             print("assistant")
#             if(len(content)>0):
#                 msgs.append({'role':'user','content':content})
#                 content=[]
#             content.append(json_data[first_key])
#             msgs.append({'role':'assistant','content':content})
#             content=[]

#     if(len(content)>0):
#         msgs.append({'role':'user','content':content})
#         content=[]


#     # if image_url:
#     #     try:
#     #         response = requests.get(image_url)
#     #         response.raise_for_status()
#     #         image = Image.open(BytesIO(response.content)).convert('RGB')
#     #         content.append(image)
#     #     except Exception as e:
#     #         logging.error(f"Failed to download image: {e}")
#     #         return jsonify({"error": "Invalid image URL"}), 400


#     # msgs.append [{'role': 'user', 'content': content}]  # 构建用户输入消息
    
#     logging.info(f"msgs: {msgs}")


#     generated_text = model.chat(
#         image=None,
#         msgs=msgs,
#         tokenizer=tokenizer,
#         sampling=True,
#         temperature=0.7,
#         system_prompt='''
#         你是一位乐于助人的视觉识别专家,盲人用户会拍摄照片并向你询问照片中的内容,并可能提出问题.
#         你的任务是结合问题,简要准确地描述照片中的内容.
#         一定要分条描述,例如1.2.3.4.
#         '''
#     )
#     logging.info(f"Generated text: {generated_text}")


#     return jsonify({"response": generated_text})



def encod(user_id):
    list_dir = os.listdir("./fr_main/face_dataset/"+user_id+'/img')
    image_paths = []
    names = []
    for name in list_dir:
        
        image_paths.append("./fr_main/face_dataset/"+user_id+'/img/'+name)
        names.append(name.split("_")[0])
    #print(image_paths)
    retinaface_encoder.encode_face_dataset(user_id,image_paths,names)




# 测试user_id数据集下的url
# return 一个list，包含照片里的人脸的名字
def teste(user_id,url):
    try:
        retinaface_tester.known_face_encodings = np.load("./fr_main/face_dataset/"+user_id+"/npy/face_encoding.npy")
        retinaface_tester.known_face_names     = np.load("./fr_main/face_dataset/"+user_id+"/npy/face_names.npy")
        response = requests.get(url)
        response.raise_for_status()
        image = Image.open(BytesIO(response.content)).convert('RGB')
        image = np.array(image)
        ret = retinaface_tester.detect_image(image)
        r_image,face_names=ret[0],ret[1]
        return face_names
        # r_image = cv2.cvtColor(r_imPage,cv2.COLOR_RGB2BGR)
        # cv2.imshow("after",r_image)
        # cv2.waitKey(0)
    except:
        #print("err!")
        return ['error']



# 增加user_id数据集下的一张图片，url,以name命名 name 的格式为name_x
# return 0(yes) or 1(err)
def adpic(user_id,name,url):
    
    pth='./fr_main/face_dataset/'+user_id
    p=pth+'/img/'+name+'.jpg'
    try:
        response = requests.get(url)
        response.raise_for_status()
        image = Image.open(BytesIO(response.content)).convert('RGB')
       
        os.makedirs(pth+'/img', exist_ok=True)
        os.makedirs(pth+'/npy', exist_ok=True)

        image.save(pth+'/img/'+name+'.jpg')
        encod(user_id)
        return 0
    except ValueError as e:
        logging.info(str(e))
        os.remove(p)
        encod(user_id)
        return str(e)
    except Exception as e:
        logging.info(str(e))
        return str(e)


# 删除user_id下所有name的图片
# return 0 or 1
def depic(user_id,name):
    try:
        pth='./fr_main/face_dataset/'+user_id+'/img/'
        files_to_delete = glob.glob(os.path.join(pth, name+'_*'))
        for p in files_to_delete:
            os.remove(p)
        encod(user_id)
        return len(files_to_delete)
    except ValueError as e:
        logging.info(str(e))
        return str(e)
    except Exception as e:
        logging.info(str(e))
        return str(e)




def chat_in_polling_mode():
    # 连接到远程 Redis 服务器
    client = redis.StrictRedis(
        host='101.132.112.59',  # 替换为实际的远程主机IP地址
        port=6379,  # 默认 Redis 端口
        db=2,
        # password='1357924680xry',
        decode_responses=True  # 自动将 bytes 解码为 str
    )

    res_client = redis.StrictRedis(
        host='101.132.112.59',  # 替换为实际的远程主机IP地址
        port=6379,  # 默认 Redis 端口
        db=3,
        # password='1357924680xry',
        decode_responses=True  # 自动将 bytes 解码为 str
    )

    client.flushdb()
    while(1):
        print("hey")
        time.sleep(0.1)
        # 获取所有的键
        keys = client.keys()
        openId=0
        data=dict()
        if keys:
            # 获取第一条记录的键和值
            first_key = keys[0]
            first_value = client.get(first_key)
            openId=first_key
            client.delete(first_key)
            data=json.loads(first_value)
        else:
            continue

        logging.info(f"Connection received with data: \n---\n{data}\n---\n")

        # todo :根据json加载历史消息
        msgs = []
        #msgs like [{'role': 'user', 'content': content},{"role": "assistant", "content": answer}]

        content = []
        data=json.loads(data)
        print(openId)
        print(first_value)

        if(data["type"]=="add"):
            url_=data["url"]
            name=data["name"]
            res=adpic(openId,name,url_)
            res_client.set(openId,str(res))
            continue
        if(data["type"]=="delete"):
            name=data["name"]
            res=depic(openId,name)
            res_client.set(openId,str(res))
            continue
        if(data["type"]=="faceDetect"):
            url=data["url"]
            lst=teste(openId,url)
            if len(lst)>0 and lst[0]=='error':
                lst=[]
            res="检测到"+str(len(lst))+"个人物:\n"
            for person in lst:
                res+=person+','
            #res[len(res)-1]='.'
            res_client.set(openId,str(res))
            continue



        chat_type = data["f"]
        chats=json.loads(data["chat"])
        for json_data in chats:
            # 解析 JSON 字符串为 Python 字典
            # 获取第一个键
            first_key = next(iter(json_data))
            if(first_key=="user"):
                content.append(json_data[first_key])
                msgs.append({'role':'user','content':content})
                content=[]
                print("user")
            elif (first_key=="url"):
                print("url")
                try:
                    response = requests.get(json_data[first_key])
                    response.raise_for_status()
                    image = Image.open(BytesIO(response.content)).convert('RGB')
                    content.append(image)
                except Exception as e:
                    logging.error(f"Failed to download image: {e}")
                    return jsonify({"error": "Invalid image URL"}), 400
            elif (first_key=="assistant"):
                print("assistant")
                if(len(content)>0):
                    msgs.append({'role':'user','content':content})
                    content=[]
                content.append(json_data[first_key])
                msgs.append({'role':'assistant','content':content})
                content=[]

        if(len(content)>0):
            msgs.append({'role':'user','content':content})
            content=[]
        
        logging.info(f"type:{chat_type}, msgs: {msgs}")
        sys_prompt=chat_type
        if chat_type=='1':
            sys_prompt='''
            你是一位乐于助人的视觉识别专家,盲人用户会拍摄照片并向你询问照片中的内容.
            你要准确地描述照片中的内容.
            一定要分条描述,例如1.2.3.4.
            '''
        elif chat_type=='2':
            sys_prompt='''
            准确描述照片中的所有文字
            '''
        logging.info(f"System Prompt: {sys_prompt}")
        generated_text = model.chat(
            image=None,
            msgs=msgs,
            tokenizer=tokenizer,
            sampling=True,
            temperature=0.7,
            system_prompt=sys_prompt
        )
        logging.info(f"Generated text: {generated_text}")


        res=generated_text
        res_client.set(openId,res)


if __name__ == '__main__':


    # print (adpic('10222','公子_1','http://101.132.112.59/R-C.jpg'))

    # print (teste('oLPca7SG_IpEOf7SVV9CoX62B60Q','http://101.132.112.59/1721231824043.jpg'))

    # print (teste('10224','http://101.132.112.59/R-C.jpg'))

    # print (depic('10222','公子'))

    # print (depic('10222','公子'))


    chat_in_polling_mode()
    #app.run(host='0.0.0.0', port=19999)  # 启动Flask应用程序，监听所有主机地址的19999端口
