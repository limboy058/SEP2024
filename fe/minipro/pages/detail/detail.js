// index.js
Page({
  data: {
    // 可以在这里定义需要的数据
    questions: [
      { id: 1, answer: "这是 AI 回答1", image: "https://via.placeholder.com/150" },
      { id: 2, answer: "这是 AI 回答2", image: "https://via.placeholder.com/150" },
      { id: 2, answer: "这是 AI 回答2", image: "https://via.placeholder.com/150" }
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