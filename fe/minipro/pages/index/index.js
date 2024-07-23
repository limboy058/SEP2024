var startX, endX; //首先创建2个变量 来记录触摸时的原点
var startY, endY; //首先创建2个变量 来记录触摸时的原点
var moveFlag = true;// 判断执行滑动事件
 
Page({
  data: {
    pageCur: 'fun1',
  },
 
  navChange(e) {
    this.setData({
      pageCur: e.currentTarget.dataset.cur
    })
    if (this.data.pageCur=='fun3')
    {
      this.selectComponent("#id_of_fun3").get_all_name()
    }
  },

  touchStart: function (e) {
    startX = e.touches[0].pageX; // 获取触摸时的原点
    startY = e.touches[0].pageY; // 获取触摸时的原点
    moveFlag = true;
  },
  // 触摸移动事件
  touchMove: function (e) {
    endX = e.touches[0].pageX; // 获取触摸时的原点
    endY = e.touches[0].pageY;
    if (moveFlag) {
      if (endX - startX > 70 && Math.abs((endY-startY)/(endX-startX))<0.4 ) {
        this.move2right();
        moveFlag = false;
      }
      if (startX - endX > 70  && Math.abs((endY-startY)/(endX-startX))<0.4) {
        this.move2left();
        moveFlag = false;
      }
    }
  },
  // 触摸结束事件
  touchEnd: function (e) {
    moveFlag = true; // 回复滑动事件
  },

  move2left() {
    console.log("move to left");
    if (this.data.pageCur=='fun4')
    {
      this.setData({pageCur:'fun1'})
    }
    else if (this.data.pageCur=='fun1')
    {
      this.setData({pageCur:'fun2'})
    }
    else if (this.data.pageCur=='fun2')
    {
      this.setData({pageCur:'fun3'})
      this.selectComponent("#id_of_fun3").get_all_name()
    }
    else if (this.data.pageCur=='fun3')
    {
      this.setData({pageCur:'fun4'})
    }

  },

    // 将AI框体中滑动所产生的逻辑写到这里
  move2right() {
    console.log("move to right");
    if (this.data.pageCur=='fun4')
    {
      this.setData({pageCur:'fun3'})
      this.selectComponent("#id_of_fun3").get_all_name()
    }
    else if (this.data.pageCur=='fun1')
    {
      this.setData({pageCur:'fun4'})
    }
    else if (this.data.pageCur=='fun2')
    {
      this.setData({pageCur:'fun1'})
    }
    else if (this.data.pageCur=='fun3')
    {
      this.setData({pageCur:'fun2'})
    }
  },

})

