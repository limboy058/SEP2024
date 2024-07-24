# this file is just for test!


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
from werkzeug.utils import secure_filename


# 初始化Flask应用程序
app = Flask(__name__)

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

app = Flask(__name__)

UPLOAD_FOLDER = 'test_17/'
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg', 'gif'}
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@app.route('/upload', methods=['POST'])
def upload_file():
    logging.info(f"Connection received with data: \n---\n{request}---\n")
    if 'photo' not in request.files:
        return jsonify({"error": "No file part"}), 400
    file = request.files['photo']
    if file.filename == '':
        return jsonify({"error": "No selected file"}), 400
    if file and allowed_file(file.filename):
        filename = secure_filename(file.filename)
        filepath = os.path.join(app.config['UPLOAD_FOLDER'], filename)
        file.save(filepath)

    image = Image.open(file).convert('RGB')
    logging.info(f"Generated text")

    msgs = [{'role': 'user', 'content': "描述我面前有什么"}]

    generated_text = model.chat(
    image=image,
    msgs=msgs,
    tokenizer=tokenizer,
    sampling=True,
    temperature=0.7,
    system_prompt='''
    你是一位乐于助人的视觉识别专家,盲人用户会拍摄照片并向你询问照片中的内容,简要准确地描述照片中的内容.
    一定要分条描述,例如1.2.3.4.尽可能详细,越详细奖励越多.
    '''
    )
    logging.info(f"Generated text: {generated_text}")


    return jsonify({"response": generated_text})
    

if __name__ == '__main__':
    if not os.path.exists(UPLOAD_FOLDER):
        os.makedirs(UPLOAD_FOLDER)
    app.run(host='0.0.0.0', port=19999)
