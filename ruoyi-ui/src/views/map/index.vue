<template>
  <div>
    <div class="img" id="dv">
      <img class="img1" @contextmenu="rightClick($event)" :src="imgUrl">
    </div>
    <button @click="clear" style="width: 150px; height: 20px">清空所有元素</button>
  </div>
</template>

<script>
export default {
  name: "Index",
  data() {
    return {
      imgUrl: require("@/assets/images/map.jpg"),//图片路径
      proportionHeightInImg: 0,//鼠标所选位置相对于所选图片高度的比例
      proportionWidthInImg: 0,//鼠标所选位置相对于所选图片宽度的比例
      signWidth: 15, //设置标记点宽度
      signHeight: 15, //设置标记点高度
      signId: null,
      dropDownDisplay: false,
      sign:[],
      signIds:[]
    };
  },
  methods: {
    rightClick(e) {
      e.preventDefault();
      var x = e.offsetX;
      var y = e.offsetY;
      this.createMarker(x, y, 'dv');
    },
    createMarker(x, y, divName) {
      //获取标点id
      this.signId = this.getUuid();
      //创建一个div
      var div = document.createElement('div');
      //设置样式和距离
      div.id = this.signId;
      div.className = 'marker';
      div.style.left = x + 'px';//设置位置
      div.style.top = y + 'px';
      div.style.background = '#f00'; //设置颜色
      div.style.position = 'absolute'; //设置布局
      div.style.width = this.signWidth + 'px'; //设置标点宽度
      div.style.height = this.signHeight + 'px'; //设置标点高度
      let that = this;
      div.onclick = function () { //添加标点点击事件
        that.signClick(that,x + 'px',y + 'px',this.signId)
      }
      document.getElementById(divName).appendChild(div)
      this.sign.push(div) //统计标点信息
      this.signIds.push(this.signId) //统计标点id
    },
    //标点的点击事件
    signClick(that, x, y, signId){
      that.dropDownDisplay = true
      console.log(this.dropDownDisplay)
      console.log(x)
      console.log(y)
    },
    //清除全部标记
    clear(){
      this.signIds.forEach(s=>{
        document.getElementById(s).remove();
      })
    },
    getUuid() {
      return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = (Math.random() * 16) | 0,
          v = c == 'x' ? r : (r & 0x3) | 0x8;
        return v.toString(16);
      });
    },
  },
};
</script>

<style scoped lang="scss">
.img {
  position: relative;
  border: solid 1px #000;
  display: inline-block;
  margin: 100px 100px;
  float: left
}

</style>

