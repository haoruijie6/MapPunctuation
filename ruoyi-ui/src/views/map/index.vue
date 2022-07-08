<template>
<div>
  <div>
    <el-header style="height: 200px;box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04)">
      <el-select v-model="signBorder" placeholder="请选择标点形状">
        <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value"
          :change="judgeShape(signBorder)">
        </el-option>
      </el-select>
      <el-button type="danger" @click="clear">清空所有元素</el-button>
    </el-header>
  </div>
  <div>
    <div class="signImg" id="dv" @mouseleave="leavePicture()">
      <img style="height: 100%;width: 100%;" @contextmenu="rightClick($event)" :src="imgUrl">
    </div>
    <div id="leftClickDialogBox" v-show="leftClickDialogBox" @mouseover="leaveDialogBox()">
      你好，亲在吗？
    </div>
  </div>
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
      sign:[],
      signIds:[],
      triangleBorder: false,//开启三角边框
      circularBorder: false, //圆边框
      squareBorder: true,//方形
      options: [{ //标点形状
        value: '正方形',
        label: '正方形'
      }, {
        value: '三角形',
        label: '三角形'
      }, {
        value: '圆形',
        label: '圆形'
      },],
      signBorder: '',
      leftClickDialogBox: false //左击标点对话框显示状态
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

      div.style.position = 'absolute'; //设置布局
      div.style.width = this.signWidth + 'px'; //设置标点宽度
      div.style.height = this.signHeight + 'px'; //设置标点高度
      if (this.squareBorder){//是否开启正方形
        div.style.background = '#f00'; //设置颜色
      }
      if (this.circularBorder){//是否开启圆边框
        div.style.background = '#f00'; //设置颜色
        div.style.borderRadius = '50%';
      }
      if (this.triangleBorder){//是否开启三角边框
        div.style.background = 'transparent'; //设置颜色
        div.style.borderBottom = '10px solid blue';
        div.style.borderTop = '10px solid transparent';
        div.style.borderRight = '10px solid transparent';
        div.style.borderLeft = '10px solid transparent';
      }
      let that = this;
      div.onclick = function () { //添加标点点击事件
        that.signClick(that,x,y,this.signId)
      }
      document.getElementById(divName).appendChild(div)
      this.sign.push(div) //统计标点信息
      this.signIds.push(this.signId) //统计标点id
    },
    //标点的点击事件
    signClick(that, x, y, signId){
      x = x + 150/3 + 'px'
      y = y - 75/3 + 'px'
      that.leftClickDialogBox = true
      console.log(document.getElementById('leftClickDialogBox'))
      //设置消息框位置
      document.getElementById('leftClickDialogBox').setAttribute('style','left: '+x+';top:'+y+';')

    },
    //清除全部标记
    clear(){
      this.signIds.forEach(s=>{
        document.getElementById(s).remove();
      })
      this.signIds = []
    },
    //设置标点形状
    judgeShape(signBorder){
      console.log(signBorder)
      if(signBorder == '圆形'){
        this.triangleBorder = false;//开启三角边框
        this.circularBorder = true; //圆边框
        this.squareBorder = false;//方形
      }else if(signBorder == '三角形'){
        this.triangleBorder = true;//开启三角边框
        this.circularBorder = false; //圆边框
        this.squareBorder = false;//方形
      }else if(signBorder == '正方形'){
        this.triangleBorder = false;//开启三角边框
        this.circularBorder = false; //圆边框
        this.squareBorder = true;//方形
      }
    },
    //关闭消息框
    leavePicture(){
      this.leftClickDialogBox = false
    },
    //移入对话框不关闭
    leaveDialogBox(){
      this.leftClickDialogBox = true
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
.signImg {
  width: 80%;
  height: 500px;
  position: relative;
  border: solid 1px #000;
  display: inline-block;
  margin: 20px 20px 20px 20px;
  float: left
}
#leftClickDialogBox{
  width: 150px;
  height: 75px;
  border: 1px solid #333;
  position: relative;
  border-radius: 10px;
  filter: drop-shadow(0 0 4px #999);
  background-color: #fff;
}
#leftClickDialogBox::before{
  position: absolute;
  content: '';
  top:40px;
  left: -10px;
  border-top:10px solid transparent;
  border-bottom:10px solid transparent;
  border-right:10px solid #333;
}
#leftClickDialogBox::after{
  position: absolute;
  content: '';
  border-top:10px solid transparent;
  border-bottom:10px solid transparent;
  border-right:10px solid #fff;
}

</style>

