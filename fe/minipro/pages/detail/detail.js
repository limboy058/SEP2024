// index.js

Page({
  data: {
    // 可以在这里定义需要的数据
    questions: [],
    questionInput: '',
    fun_id:'1',

  },

  append_msg: function (q,p,r) {
    let currentData = this.data.questions; // 获取当前的对话数据
    let newMessage = { id: currentData[currentData.length-1].id+1, que:q,image: p ,answer: r};
    let new_talk = '0'
    if (currentData[currentData.length-1].answer=='提交后，此处将显示AI回复' ||currentData[currentData.length-1].answer=='未上传图片')
    {
      new_talk='1'
    }
    currentData.push(newMessage);
    this.setData({
      questions: currentData,

    });
    return new_talk
  },

  set_last_msg: function (q,p,r) {
    let currentData = this.data.questions; // 获取当前的对话数据
    currentData[currentData.length-1] = { id: currentData[currentData.length-1].id, que:q, image: p ,answer: r};
    this.setData({
      questions: currentData
    });
  },

  onLoad: function (options) {
    // 页面加载时的逻辑
      const { pic_path,que,ai_res ,fun_id} = options;
      this.setData({
        fun_id: fun_id
      });
      let currentData = this.data.questions; // 获取当前的对话数据
      let newMessage = { id: 0, que:que,image: pic_path ,answer: ai_res};
      currentData.push(newMessage);
      this.setData({
        questions: currentData
      });
  },

  button1: function () {
    const app = getApp()
    console.log(app.globalData.loginCode)
    const that = this;
    wx.chooseMedia({
      count: 1, //只能选择一张
      mediaType:['image'],
      sizeType:['compressed'],
      success: (res) => {
        wx.compressImage({
          src: res.tempFiles[0].tempFilePath, 
          quality: 100 ,
          compressedHeight: 800,//调整图片siz
          success: (res) => {
            let my_path=res.tempFilePath
            wx.getImageInfo({
              src: my_path,
              success (res) {
                console.log(res.width)
                console.log(res.height)
              }
            })
            let user_input=this.data.questionInput

            let new_talk_tmp=this.append_msg(user_input,my_path,'思考中......请等候约3秒') 
    
            wx.uploadFile({
              url: 'http://101.132.112.59:8123/AITalk', 
              filePath: my_path,
              name: 'photo',
              
              formData: {
                loginCode: app.globalData.loginCode,
                newTalk: new_talk_tmp, // 或者 false，根据实际情况设置
                kind: this.data.fun_id,
                //question:replaceNewlines(user_input)
                question:user_input
              },
              success: (res) => {
                console.log(res); // 请求成功后的处理逻辑
                if (res.statusCode!=200){
                  this.set_last_msg(user_input,my_path,'网络连接异常') 
                  return
                }  
                this.setData({
                  questionInput: ''
                });
                this.set_last_msg(user_input,my_path,res.data) 
              },
              
              fail: (error) => {
                console.error('请求失败', error); // 请求失败时的处理逻辑
                this.set_last_msg(user_input,my_path,'网络连接异常') 
              }
            });
          }
        })
      },
        
      fail: () => {

      }
    });
  },


  onInput: function (e) {
    this.setData({
      questionInput: e.detail.value
    });
  },


  onSend: function () {
    // 发送逻辑
    const question = this.data.questionInput;
    if(question=='')
    {
      return
    }
    
    const app = getApp();
    let new_talk_tmp=this.append_msg(question,'','思考中......请等候约1秒');
    wx.request({
      url: 'http://101.132.112.59:8123/AITalk', // 替换为你的后端接口地址
      method: 'POST',
      header: {
        'content-type': 'multipart/form-data;boundary=XXX' // 默认值
      },
      data:'\r\n--XXX' +
        '\r\nContent-Disposition: form-data; name="loginCode"' +
        '\r\n' +
        '\r\n' + app.globalData.loginCode+
        '\r\n--XXX' +
        '\r\nContent-Disposition: form-data; name="newTalk"' +
        '\r\n' +
        '\r\n' +new_talk_tmp+
        '\r\n--XXX'+
        '\r\nContent-Disposition: form-data; name="kind"' +
        '\r\n' +
        '\r\n' + this.data.fun_id+
        '\r\n--XXX'+
        '\r\nContent-Disposition: form-data; name="question"' +
        '\r\n' +
        '\r\n' +question+
        '\r\n--XXX--',

      success: (res) => {
        if (res.statusCode == 200) {
          // wx.showToast({
          //   title: '发送成功',
          //   icon: 'success'
          // });
          this.set_last_msg(question,'',res.data) 
          this.setData({
            questionInput: ''
          });
        }
        else{
          this.set_last_msg(question,'','网络连接异常') 
        }
      },
      fail: (err) => {
        console.error('Failed to send question:', err);
        this.set_last_msg(question,'','网络连接异常') 
      }
    });
    
  }
});
