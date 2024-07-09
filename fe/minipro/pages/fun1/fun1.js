Page({
  /**
   * 页面的初始数据
   */
  data: {
    aiResponse: '提交后，此处将显示AI回复',
    voiceInput: ''
  },

  // Function to handle "拍照" button click
  button1: function () {
    const that = this;
    wx.chooseMedia({
      count: 1, //只能选择一张
      success: (res) => {
        const tempFilePaths = res.tempFilePaths;
        that.setData({
          aiResponse: "思考中......请等候约5秒"
        });
        wx.request({
          url: 'http://10.9.176.40:8123',//? unknown
          method: 'POST',
          data: {
            userid: 'yourUserIdHere',
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
              aiResponse: "网络连接异常"
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

  // Function to handle "详细询问" button click
  button2: function () {
    wx.navigateTo({
      url: '/pages/chat/chat'  // 指定目标页面路径
    });
  },

  onInputChange: function (e) {
    this.setData({
      voiceInput: e.detail.value
    });
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {},

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {},

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {},

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {},

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {},

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {},

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {},

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {}
});
