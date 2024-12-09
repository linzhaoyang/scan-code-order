<!--配送信息-->
<template>
  <view class="box">
    <view class="orderBaseInfo">
      <view>
        <view>期望时间</view>
        <view>{{
          orderDetailsData.deliveryStatus === 1
          ? "立即完成"
          : orderDetailsData.estimatedDeliveryTime
        }}</view>
      </view>
    </view>
  </view>
</template>
<script>
export default {
  // 获取父级传的数据
  props: {
    // 订单详情
    orderDetailsData: {
      type: Object,
      default: () => ({}),
    },
  },
  computed: {
    // 处理姓名 如x先生
    cryptoName() {
      if (!this.orderDetailsData.consignee) return "";
      if (this.orderDetailsData.sex == 0) {
        // 男
        return this.orderDetailsData.consignee.charAt(0) + "先生";
      } else {
        // 女
        return this.orderDetailsData.consignee.charAt(0) + "女士";
      }
    },
  },
};
</script>
<style src="../../order/style.scss" lang="scss"></style>