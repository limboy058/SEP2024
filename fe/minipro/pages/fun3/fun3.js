// 用来处理滑动逻辑的变量
var startX, endX; //首先创建2个变量 来记录触摸时的原点
var moveFlag = true;// 判断执行滑动事件

function replaceNewlines(input) {
  let result = '';
  for (let i = 0; i < input.length; i++) {
      if (input[i] === '\n') {
          result += '\\n';
      } else if (input[i] === '\\') {
          result += '\\\\';
          //i++; // Skip the 'n' after the backslash
      } else {
          result += input[i];
      }
  }
  return result;
}


Page({
  /**
   * 页面的初始数据
   */
  data: {
    aiResponse: '提交后，此处将显示回复',
    voiceInput: '',
    fun_text:'人脸识别',
    fun_id:'3',
    all_face_name:''
  },

  get_all_name:function(){
    var app = getApp()
    wx.request({
      url: 'http://101.132.112.59:8123/getPersonImg', // 替换为你的后端接口地址
      method: 'POST',
      header: {
        'content-type': 'multipart/form-data;boundary=XXX' // 默认值
      },
      data:'\r\n--XXX' +
        '\r\nContent-Disposition: form-data; name="loginCode"' +
        '\r\n' +
        '\r\n' + app.globalData.loginCode+
        '\r\n--XXX--',

      success: (res) => {
        if (res.statusCode == 200) {
          this.setData({
            all_face_name: '您已上传:\n'+res.data
          });
        }
        else{
          console.error('err', res.data);
          this.setData({
            all_face_name: '获取已上传的人脸信息失败'
          });
        }
      },
      fail: (err) => {
        console.error('Failed to send question:', err);
        this.setData({
          all_face_name: '获取已上传的人脸信息失败'
        });
      }
    });
  },


  // Function to handle "拍照" button click
  button1: function () {
    const app = getApp()
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
            that.setData({
              aiResponse: "上传中......请等候约1秒"
            });
    
            wx.uploadFile({
              url: 'http://101.132.112.59:8123/faceDetect', 
              filePath: my_path,
              name: 'photo',  
              formData: {
                loginCode: app.globalData.loginCode
              },
              success: (res) => {
                console.log(res); // 请求成功后的处理逻辑
                if (res.statusCode!=200){
                  that.setData({
                    aiResponse: "网络连接异常"
                  });
                  return
                }  
                this.setData({
                  aiResponse: res.data,
                  voiceInput:''
                });
              },
              
              fail: (error) => {
                console.error('请求失败', error); // 请求失败时的处理逻辑
                that.setData({
                  aiResponse: "网络连接异常"
                });
              }
            });
          
          }
        })
      },
        
      fail: () => {
        that.setData({
          aiResponse: '未上传图片'
        });
      }
    });
  },


  // Function to handle "拍照" button click
  button2: function () {
    if (this.data.voiceInput =='')
    {
      this.setData({
        aiResponse: "请输入名称"
      });
      return
    }
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
            that.setData({
              aiResponse: "上传中......请稍等"
            });
            let user_input=this.data.voiceInput 
            console.log(user_input)
    
            wx.uploadFile({
              url: 'http://101.132.112.59:8123/addPersonImg', 
              filePath: my_path,
              name: 'photo',
              
              formData: {
                loginCode: app.globalData.loginCode,
                name:user_input
              },
              success: (res) => {
                console.log(res); // 请求成功后的处理逻辑
                if (res.statusCode!=200){
                  that.setData({
                    aiResponse: "网络连接异常"
                  });
                  return
                }  
                that.setData({
                  aiResponse: res.data
                });
                this.get_all_name()
              },
              
              fail: (error) => {
                console.error('请求失败', error); // 请求失败时的处理逻辑
                that.setData({
                  aiResponse: "网络连接异常"
                });
              }
            });
          
          }
        })
      },
        
      fail: () => {
        that.setData({
          aiResponse: '未上传图片'
        });
      }
    });
  },

  // Function to handle "拍照" button click
  button3: function () {
    if (this.data.voiceInput =='')
    {
      this.setData({
        aiResponse: "请输入名称"
      });
      return
    }
    const app = getApp()
    this.setData({
      aiResponse: "删除中......请稍等"
    });
    wx.request({
      url: 'http://101.132.112.59:8123/deletePersonImg', // 替换为你的后端接口地址
      method: 'POST',
      header: {
        'content-type': 'multipart/form-data;boundary=XXX' // 默认值
      },
      data:'\r\n--XXX' +
        '\r\nContent-Disposition: form-data; name="loginCode"' +
        '\r\n' +
        '\r\n' + app.globalData.loginCode+
        '\r\n--XXX' +
        '\r\nContent-Disposition: form-data; name="name"' +
        '\r\n' +
        '\r\n' + this.data.voiceInput+
        '\r\n--XXX--',
        

      success: (res) => {
        if (res.statusCode == 200) {
          this.setData({
            aiResponse: res.data,
            voiceInput: ''
          });
          this.get_all_name()
        }
        else{
          this.setData({
            aiResponse: '网络连接异常'
          });
        }
      },
      fail: (err) => {
        console.error('Failed to send question:', err);
        that.setData({
          aiResponse: '网络连接异常'
        });
      }
    });
  },


  
  // 下面三个函数是实现在AI对话框中捕捉滑动的逻辑
  onInputChange: function (e) {
    this.setData({
      voiceInput: e.detail.value
    });
  },
  touchStart: function (e) {
    startX = e.touches[0].pageX; // 获取触摸时的原点
    moveFlag = true;
  },
  // 触摸移动事件
  touchMove: function (e) {
    endX = e.touches[0].pageX; // 获取触摸时的原点
    if (moveFlag) {
      if (endX - startX > 50) {
        this.move2right();
        moveFlag = false;
      }
      if (startX - endX > 50) {
        this.move2left();
        moveFlag = false;
      }
    }
  },
  // 触摸结束事件
  touchEnd: function (e) {
    moveFlag = true; // 回复滑动事件
  },
  clicktab1:function(e){
    this.move2right();
   this.setData({
     currentab:0
   })
console.log('详情')
  },


  // 将AI框体中滑动所产生的逻辑写到这里
  move2left() {
    console.log("move to left");
    if (this.data.fun_id=='1')
    {
      this.setData({
        fun_id:'2',
        fun_text:'文字识别'
      });
    }
    else if (this.data.fun_id=='2')
    {
      this.setData({
        fun_id:'3',
        fun_text:'人脸识别'
      });
    }
    else if (this.data.fun_id=='3')
    {
      this.setData({
        fun_id:'1',
        fun_text:'实景识别'
      });
    }

  },

    // 将AI框体中滑动所产生的逻辑写到这里
  move2right() {
    console.log("move to right");
    if (this.data.fun_id=='3')
    {
      this.setData({
        fun_id:'2',
        fun_text:'文字识别'
      });
    }
    else if (this.data.fun_id=='1')
    {
      this.setData({
        fun_id:'3',
        fun_text:'人脸识别'
      });
    }
    else if (this.data.fun_id=='2')
    {
      this.setData({
        fun_id:'1',
        fun_text:'实景识别'
      });
    }
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
    this.get_all_name()
  },

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
