// index.js
Page({
  data: {
    // 可以在这里定义需要的数据
    questions: [{ id: 1, answer: "你好", image: "" }],
    questionInput: ''
  },
  onLoad: function () {
    // 页面加载时的逻辑
    const that = this;
    wx.request({
      url: 'https://8629896bylf7.vicp.fun/AITalk', // 替换成实际的服务器地址
      method: 'POST', // 请求方法：GET, POST等
      success: (res) => {
        console.log(res.data); // 打印服务器返回的数据
        // 这里可以对返回的数据进行处理，更新页面数据等
        if (res.statusCode === 201) {
          wx.showToast({
            title: '发送成功',
            icon: 'success'
          });
          that.setData({
            aiResponse: "思考中......请等候约5秒"
          });}
        that.setData({
          
          questions: res.data.questions
          // unescape(res.data.replace(/\\u/g, '%u'))+app.globalData.loginCode
        });
      },
      fail: (error) => {
        console.error('请求失败', error);
        // 处理请求失败的情况，例如显示错误信息给用户
      }
    });
    /*
    wx.request({
      url: 'https://8629896bylf7.vicp.fun/AITalk', // 替换为你的后端接口地址
      method: 'GET',
      success: (res) => {
        if (res.statusCode === 200) {
          this.setData({
            questions: res.data.questions
          });
        }
      },
      fail: (err) => {
        console.error('Failed to fetch data:', err);
        this.setData({
          questions: [this.data.questions, {id: 0, answer: "网络错误",image:""}]
        });
      }
    });*/
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
        const my_FilePath = res.tempFiles[0].tempFilePath;
        that.setData({
          aiResponse: "思考中......请等候约5秒"
        });
        // console.log(app.globalData.loginCode);
        wx.uploadFile({
          url: 'https://8629896bylf7.vicp.fun/AITalk', //仅为示例，非真实的接口地址
          //url: 'http://124.71.204.55/aitalk',
          filePath: my_FilePath,
          // header: {
          //   'content-type': 'application/json' // 默认值
          //   // 'content-type': 'application/x-www-form-urlencoded;charset=utf-8'
          // },
          name: 'photo',
          
          formData: {
            loginCode: app.globalData.loginCode,
            newTalk: '1', // 或者 false，根据实际情况设置
            kind: this.data.fun_id,
            question: this.data.questionInput
          },
          success: (res) => {
            console.log(res.data); // 请求成功后的处理逻辑
            
          
            const currentData = that.data.questions; // 获取当前的对话数据
            const newMessage = { id: 1, answer: JSON.parse(res.data).response, image: my_FilePath }; // 新的对话消息
            //currentData.length+1
            currentData.push(newMessage);
            this.setData({
              questions: currentData
            });
            /*
            that.setData({
              questions: [{ id: 1, answer: JSON.parse(res.data).response, image: my_FilePath }]
              // unescape(res.data.replace(/\\u/g, '%u'))+app.globalData.loginCode
            });*/
          },
          
          fail: (error) => {
            console.error('请求失败', error); // 请求失败时的处理逻辑
            that.setData({
              questions: [{ id: 1, answer: "网络连接异常"+app.globalData.loginCode, image: " " }]
            });
          }
        });
        // You can add more code here to handle the photo, e.g., upload to server, analyze, etc.
      },
      fail: () => {
        that.setData({
          questions: [{ id: 1, answer: "未上传图片", image: "https://via.placeholder.com/150" }]
        });
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
    const that = this;
    const question = this.data.questionInput;

    wx.request({
      url: 'https://8629896bylf7.vicp.fun/AITalk', // 替换为你的后端接口地址
      method: 'POST',
      data: { question },
      success: (res) => {
        
        //if (res.statusCode === 201) {
          wx.showToast({
            title: '发送成功',
            icon: 'success'
          });
          that.setData({
            aiResponse: "思考中......请等候约5秒"
          });
        const currentData = that.data.questions; // 获取当前的对话数据
        const newMessage = { id: currentData.length+1, answer: JSON.parse(res.data).response, image: ""};// 新的对话消息

        currentData.push(newMessage);
        that.setData({
          questions: currentData
        });
        that.setData({
          aiResponse: " "
        });
        //}
      },
      fail: (err) => {
        console.error('Failed to send question:', err);
        const currentData = that.data.questions; // 获取当前的对话数据
        const newMessage = { id: 4, answer: "寄了", image: "" }; // 新的对话消息

        currentData.push(newMessage);
        this.setData({
          questions: currentData
        });
      }
    });
    
  }
});