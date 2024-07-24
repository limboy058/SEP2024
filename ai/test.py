# this file is just for test!


import os,glob
import sys
import requests
from PIL import Image
import numpy as np
import cv2
from io import BytesIO
sys.path.append(r'/home/wy/miniCPM/fr_main')#todo
from fr_main.retinaface import Retinaface

retinaface_encoder = Retinaface(1)
retinaface_tester = Retinaface()


def encod(user_id):
    list_dir = os.listdir("fr_main/face_dataset/"+user_id+'/img')
    image_paths = []
    names = []
    for name in list_dir:
        
        image_paths.append("fr_main/face_dataset/"+user_id+'/img/'+name)
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
    except Exception as e:
        # print(e)
        return ['error']



# 增加user_id数据集下的一张图片，url,以name命名 name 的格式为name_x
# return 0(yes) or 1(err)
def adpic(user_id,name,url):
    try:
        response = requests.get(url)
        response.raise_for_status()
        image = Image.open(BytesIO(response.content)).convert('RGB')
        pth='./fr_main/face_dataset/'+user_id
        os.makedirs(pth+'/img', exist_ok=True)
        os.makedirs(pth+'/npy', exist_ok=True)

        image.save(pth+'/img/'+name+'.jpg')
        encod(user_id)
        return 0
    except:
        #print("err!")
        return 1


# 删除user_id下所有name的图片
# return 0 or 1
def depic(user_id,name):
    try:
        pth='./fr_main/face_dataset/'+user_id+'/img/'
        files_to_delete = glob.glob(os.path.join(pth, name+'_*'))
        for p in files_to_delete:
            os.remove(p)
        encod(user_id)
        return 0
    except:
        #print("err!")
        return 1

if __name__=='__main__':
    print (adpic('10222','公子_1','http://101.132.112.59/R-C.jpg'))

    print (teste('10222','http://101.132.112.59/R-C.jpg'))

    print (teste('10224','http://101.132.112.59/R-C.jpg'))

    print (depic('10222','公子'))

    print (depic('10222','公子'))
    