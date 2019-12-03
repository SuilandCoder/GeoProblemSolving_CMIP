<template>
  <img :src="base64" alt="">
  <!-- <div>{{imgUrl}}</div> -->
</template>
<script>
export default {
  name: "async-img",
  created() {
    // * 向后台请求图片
    // console.log("img url:", this.imgUrl);
    this.getImgFromDataContainer(this.imgUrl);
  },
  data() {
    return {
      base64:"",
    };
  },
  props: ["imgUrl"],
  methods: {
    getImgFromDataContainer(imgUrl) {
      this.$api.cmp_data.getImgFromDataContainer(imgUrl)
        .then(res => {
          // console.log("base64:", res);
          this.base64 = "data:image/png;base64,"+res;
        })
        .catch(err => {
          this.$Message.error(err);
        });
    }
  }
}
</script>
<style scoped>
</style>