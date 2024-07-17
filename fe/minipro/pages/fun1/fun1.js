
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
    aiResponse: '提交后，此处将显示AI回复',
    voiceInput: '',
    send_to_detail:'',
    fun_text:'实景识别',
    fun_id:'1',
    pic_path: ''
  },


  // Function to handle "拍照" button click
  button1: function () {
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
            that.setData({
              pic_path: res.tempFilePath
            });
            wx.getImageInfo({
              src: my_path,
              success (res) {
                console.log(res.width)
                console.log(res.height)
              }
            })
            that.setData({
              aiResponse: "思考中......请等候约3秒"
            });
            let user_input=this.data.voiceInput 
            console.log(user_input)
    
            wx.uploadFile({
              url: 'http://101.132.112.59:8123/AITalk', 
              filePath: my_path,
              name: 'photo',
              
              formData: {
                loginCode: app.globalData.loginCode,
                newTalk: '1', // 或者 false，根据实际情况设置
                kind: this.data.fun_id,
                //question:replaceNewlines(user_input)
                question:user_input
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
                  send_to_detail: user_input,
                  voiceInput:''
                });
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

  // Function to handle "详细询问" button click
  button2: function () {
    if(this.data.pic_path!=''){
      wx.navigateTo({
        url: '/pages/detail/detail?pic_path='+this.data.pic_path+'&que='+this.data.send_to_detail+'&ai_res='+this.data.aiResponse+'&fun_id='+this.data.fun_id  // 指定目标页面路径
      });
    }
    else{
      this.setData({
        send_to_detail: ''
      });
      wx.navigateTo({
        url: '/pages/detail/detail?pic_path='+this.data.pic_path+'&que='+this.data.voiceInput+'&ai_res='+this.data.aiResponse+'&fun_id='+this.data.fun_id // 指定目标页面路径
      });
    }
    
    this.setData({
      pic_path: ''
    });
    this.setData({
      aiResponse: '提交后，此处将显示AI回复'
    });
    this.setData({
      send_to_detail: ''
    });

  },

  onInputChange: function (e) {
    this.setData({
      voiceInput: e.detail.value
    });
  },
 
  //what is it???
//   clicktab1:function(e){
//     this.move2right();
//    this.setData({
//      currentab:0
//    })
// console.log('详情')
//   },


 
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
