// app.js
App({
  onLaunch() {
    // 展示本地存储能力
    const logs = wx.getStorageSync('logs') || []
    logs.unshift(Date.now())
    wx.setStorageSync('logs', logs)

    wx.login({
      success: res => {
        // 发送 res.code 到后台换取 openId, sessionKey, unionId
        console.log(res);
        this.globalData.loginCode = res.code
      }
    })
    //this.globalData.loginCode = '0f1Gtm000clytS1T1I200fx1Q43Gtm0T'
  },
  globalData: {
    loginCode:'null'
  }
})
