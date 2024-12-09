<!--购买页-->
<template>
  <view class="customer-box">
    <view class="wrap">
      <view class="contion">
        <view class="orderPay">
          <view>
            <view v-if="timeout">订单已超时</view>
            <view v-else
              >支付剩余时间<text>{{ rocallTime }}</text></view
            >
          </view>
          <view class="money"
            >￥<text>{{ orderDataInfo.orderAmount }}</text></view
          >
          <view>订单号-{{ orderDataInfo.orderNumber }}</view>
        </view>
      </view>
      <view class="box payBox">
        <view class="contion">
          <view class="example-body">
            <radio-group class="uni-list" @change="handleChange" >
              <view class="uni-list-item">
                <view class="uni-list-item__container" v-for="(item,index) in payMethodList" :key="item">
                  <view class="uni-list-item__content">
                    <icon :class="'payIcon'+index"></icon>
                    <text class="uni-list-item__content-title">{{item}}</text>
                  </view>
                  <view class="uni-list-item__extra">
                    <radio :value="index" color="#FFC200" :checked="index === 0" class="radioIcon" />
                  </view>
                </view>
              </view>
            </radio-group>
          </view>
        </view>
      </view>
      <view class="bottomBox btnBox">
        <button class="add_btn" type="primary" plain="true" @click="handleSave">
          确认支付
        </button>
      </view>
      
    </view>
    <uni-popup ref="popup" type="center" :mask-click="false" class="kefu">
      <view class="popup">
        <view class="icon-close">
          <uni-icons type="close" size="30" @click="close" color="#fff"></uni-icons>
        </view>
        <image :src=imgUrl mode="widthFix" class="QRcode"></image>
        <view class="tip">请保存图片,识别二维码付款</view>
        <button type="warn" size="default" class="btn-savecode" @click="saveQRcode">保存图片</button>
        <button type="warn" size="default" class="btn-savecode" @click="selectResult">完成支付</button>
      </view>
    </uni-popup>
  </view>
</template>

<script>
import { mapState } from "vuex";
import { paymentOrder, cancelOrder,queryOrdersStatus } from "@/pages/api/api.js";
export default {
  data() {
    return {
      timeout: false,
      rocallTime: "",
      orderId: null,
      orderDataInfo: {},
      activeRadio: 1,
      payMethodList: ["微信支付"],
      times: null,
      imgUrl: null,
    };
  },
  created() {
    this.orderDataInfo = this.orderData();
  },
  mounted() {
    this.runTimeBack();
  },
  onLoad(options) {
    this.orderId = options.orderId;
  },
  methods: {
    ...mapState(["orderData", "shopInfo"]),
    //打开弹出层
    open(){
        // 通过组件定义的ref调用uni-popup方法 ,如果传入参数 ，type 属性将失效 ，仅支持 ['top','left','bottom','right','center']
        this.$refs.popup.open('center')
      },
    //关闭弹出层
    close(){
        this.$refs.popup.close()
      },

    handleChange(e){
        console.log(e);
        this.activeRadio = Number(e.detail.value)+1;
    },
    // 支付详情
    handleSave() {
      if (this.timeout) {
        cancelOrder(this.orderId).then((res) => {});
        uni.redirectTo({
          url: "/pages/details/index?orderId=" + this.orderId,
        });
      } else {
        // 如果支付成功进入成功页
        clearTimeout(this.times);
        const params = {
          orderNumber: this.orderDataInfo.orderNumber,
          payMethod: this.activeRadio,
        };
        paymentOrder(params).then(async (res) => {
          if (res.code === 1) {
            if(this.activeRadio===1){
              // const [err, payRes] = await uni.requestPayment({
            //   ...res.data,
            //   package: res.data.packageStr, // package 为微信支付必须的字段
            // });
            // console.log(err, payRes);
            // if (err) {
            //   await uni.showToast({ title: "支付失败", icon: "error" });
            //   setTimeout(() => {
            //     // 下单失败!!
            //     uni.redirectTo({
            //       url: "/pages/details/index?orderId=" + this.orderId,
            //     });
            //   }, 1500);
            // } else {
            //   await uni.showToast({ title: "支付成功", icon: "success" });
            //   setTimeout(() => {
            //     // 下单成功!!
            //     uni.redirectTo({
            //       url: "/pages/success/index?orderId=" + this.orderId,
            //     });
            //   }, 1500);
            // }
            console.log('支付成功!')
            await uni.showToast({ title: "支付成功", icon: "success" });
              setTimeout(() => {
                // 下单成功!!
                uni.redirectTo({
                  url: "/pages/success/index?orderId=" + this.orderId,
                });
              }, 1500);
            }else{
              this.imgUrl=res.data.qrcode;
              this.open();
            }
            
          } else {
            console.log('支付失败!')
            uni.showToast({
              title: res.msg,
              duration: 1000,
              icon: "error",
            });
          }
        });
      }
    },
    //授权并保存相册
    saveQRcode(){
    uni.getSetting({
      success:(res)=>{
        if(res.authSetting['scope.writePhotosAlbum']){ //验证用户是否授权可以访问相册
          this.saveQRcodeToPhotosAlbum();
        }else{
          uni.authorize({
            scope:'scope.writePhotosAlbum',
            success:(res)=>{
              console.log('有授权的信息：',res);
              this.saveQRcodeToPhotosAlbum();
            },
            fail:(err)=>{ //取消授权后再获取授权，需手动设置
              uni.showModal({
                content:'检测到你没打开保存相册权限，是否去设置打开',
                confirmText:'确认',
                cancelText:'取消',
                success(res){
                  if(res.confirm){
                    uni.openSetting({
                      success(res) {
                        console.log('授权成功');
                      }
                    })
                  }else{
                    console.log('取消授权');
                  }
                }
              })
            }
          })
        }
      }
    })
  },


// 保存 base64 图片到相册
saveQRcodeToPhotosAlbum() {
  let that = this
  const path = `${wx.env.USER_DATA_PATH}/qrcode.png`; // 指定图片的临时路径
  const fsm = wx.getFileSystemManager(); // 获取小程序的文件系统
  // 把数据写入到临时目录中
  fsm.writeFile({
    filePath: path,
    data: that.imgUrl.replace(/^data:image\/\w+;base64,/, ''),
    encoding: 'base64',
    success: () => {
       uni.saveImageToPhotosAlbum({
              filePath: path,
              success(res) {
                uni.showToast({
                title:'保存成功',
                icon:'success'
              })
            that.close()
          },
          fail(err){
            console.log(err);
          }
        })
    }
  })
},

//查询支付结果
selectResult() {
    queryOrdersStatus().then((res) => {
        if (res.code === 1) {
          uni.showToast({ title: "支付成功", icon: "success" });
          setTimeout(() => {
            uni.redirectTo({
              url: "/pages/success/index?orderId=" + this.orderId,
            });
          }, 1500);
        } else if (res.code === 0 && res.msg === "订单未付款") {
          uni.showToast({
            title: res.msg,
            duration: 1000,
            icon: "error",
          });
        } else {
          // 处理其他错误情况
          uni.showToast({
            title: "请求失败，请稍后重试",
            icon: "none",
          });
        }
      })
},



    // // 订单倒计时
    runTimeBack() {
      const end = Date.parse(this.orderDataInfo.orderTime.replace(/-/g, "/"));
      const now = Date.parse(new Date());
      const m15 = 15 * 60 * 1000;
      const msec = m15 - (now - end);
      if (msec < 0) {
        this.timeout = true;
        clearTimeout(this.times);
      } else {
        let min = parseInt((msec / 1000 / 60) % 60);
        let sec = parseInt((msec / 1000) % 60);
        if (min < 10) {
          min = "0" + min;
        } else {
          min = min;
        }
        if (sec < 10) {
          sec = "0" + sec;
        } else {
          sec = sec;
        }
        this.rocallTime = min + ":" + sec;
        let that = this;
        if (min >= 0 && sec >= 0) {
          if (min === 0 && sec === 0) {
            this.timeout = true;
            clearTimeout(this.times);
            return;
          }
          this.times = setTimeout(function () {
            that.runTimeBack();
          }, 1000);
        }
      }
    },
  },
};
</script>
<style src="./../common/Navbar/navbar.scss" lang="scss" scoped></style>
<style src="./../order/style.scss" lang="scss"></style>
<style>
</style>
