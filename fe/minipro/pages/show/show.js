// pages/show.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    fun_text:"Aeye\n",
    tea_text:" \n友情提示：\n",
    de_text:"\
    1. 本程序提供：实景识别、文字识别、人脸识别，以及自定义识别。\n\n\
    2. 实景识别功能可以通用地为您描述出图片的内容\n\
    3. 文字识别功能可以给出图片中的文字信息\n\
    4. 人脸识别功能可以在图片中识别出您预先上传的人脸\n\
    5. 自定义识别下，您可以设置提示词（如果您不清楚提示词是什么，请忽略这个功能）\n\n\
    6. 您可以点击详细询问，对多张图片进行多次的追问，获得更具体的信息。\n\
    "
  },
  button1: function () {
    wx.navigateTo({
      url: '/pages/index/index'
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {

  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {

  }
})