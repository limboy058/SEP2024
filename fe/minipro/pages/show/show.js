// pages/show.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    fun_text:"Aeye\n",
    tea_text:"\n友情提示：\n",
    de_text:"   1.本程序主要提供：实景识别，人脸识别，文字识别以及自定义识别。对上传的图片进行基本的描述。\n2.详细询问功能可以对同一张图片做出多次的追问。\n3.在进行人脸识别前，请先上传您熟人的照片，以供比对。\n"
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