
 
Page({
  data: {
    pageCur: 'index',
  },
 
  navChange(e) {
    this.setData({
      pageCur: e.currentTarget.dataset.cur
    })
  }
})