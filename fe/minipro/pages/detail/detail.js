// index.js
Page({
  data: {
    // 可以在这里定义需要的数据
    questions: [
     
    ],
    questionInput: ''
  },
  onLoad: function () {
    // 页面加载时的逻辑
    wx.request({
      url: 'https://your-api-url.com/questions', // 替换为你的后端接口地址
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
      }
    });
  },
  onBackButtonTap: function () {
    // 返回按钮点击事件逻辑
    wx.navigateBack();
  },
  onInput: function (e) {
    this.setData({
      questionInput: e.detail.value
    });
  },
  onTakePhoto: function () {
    // 拍照逻辑
    const that = this;
    wx.chooseMedia({
      count: 1, //只能选择一张
      success: (res) => {
        const tempFilePaths = res.tempFilePaths;
        that.setData({
          aiResponse: "思考中......请等候约5秒"
        });
        console.log(this.data.loginCode)
        wx.request({
          url: 'http://10.9.176.40:8123',//? unknown
          method: 'POST',
          data: {
            logincode: this.data.loginCode,
            newtalk: true, // 或者 false，根据实际情况设置
            kind: '1',
            picture: 'base64编码的图片数据', // 如果是base64编码的图片数据，可以直接传字符串
            question: 'yourQuestionHere'
          },
          // header: {
          //   'content-type': 'application/json' // 根据你的后端要求设置合适的content-type
          // },
          success: (res) => {
            console.log(res.data); // 请求成功后的处理逻辑
            that.setData({
              aiResponse: res.data
            });
          },
          
          fail: (error) => {
            console.error('请求失败', error); // 请求失败时的处理逻辑
            that.setData({
              aiResponse: "网络连接异常"+this.data.loginCode
            });
          }
        });
        // You can add more code here to handle the photo, e.g., upload to server, analyze, etc.
      },
      fail: () => {
        that.setData({
          aiResponse: '未上传图片'
        });
      }
    });
  },
  onSend: function () {
    // 发送逻辑
    const question = this.data.questionInput;
    wx.request({
      url: 'https://your-api-url.com/questions', // 替换为你的后端接口地址
      method: 'POST',
      data: { question },
      success: (res) => {
        if (res.statusCode === 201) {
          wx.showToast({
            title: '发送成功',
            icon: 'success'
          });
        }
      },
      fail: (err) => {
        console.error('Failed to send question:', err);
      }
    });
  }
});