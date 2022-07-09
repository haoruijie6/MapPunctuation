<template>
  <div class="app-container">
<!--    <el-row :gutter="10" class="mb8">-->
<!--      <el-col :span="2">-->
<!--        <el-button type="danger"-->
<!--                   plain-->
<!--                   size="mini"-->
<!--                   icon="el-icon-delete"-->
<!--                   @click="clearImage">删除图片-->
<!--        </el-button>-->
<!--      </el-col>-->
<!--      <el-col :span="2">-->
<!--        <el-button type="danger"-->
<!--                   plain-->
<!--                   size="mini"-->
<!--                   icon="el-icon-delete"-->
<!--                   @click="clearSign">清空所有元素-->
<!--        </el-button>-->
<!--      </el-col>-->
<!--    </el-row>-->
    <div>

    </div>
    <div>
      <div v-show="signImageUrl == ''">
        <el-upload
          style="display: inline-block"
          action="https://jsonplaceholder.typicode.com/posts/"
          list-type="picture-card"
          :on-preview="handlePictureCardPreview"
          :on-remove="handleRemove"
          :on-change="addSignImageUrl"
          :limit="1"
          multiple
          accept="image/jpeg,image/jpg,image/png"
          :auto-upload="false">
          <i class="el-icon-plus"></i>
        </el-upload>
      </div>
    </div>
    <div class="signImg" id="dv" @mouseleave="leavePicture()" v-show="signImageUrl != ''">
      <div style="position: relative">
        <img id="signImage" style="height: 100%;width: 100%;" @contextmenu="rightClick($event)" :src="signImageUrl">
        <div id="leftClickDialogBox" v-show="leftClickDialogBox" @mouseover="leaveDialogBox()">
          消息框
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "MapView",
  data() {
    return {
      imgUrl: require("@/assets/images/upoload.jpg"),//默认本地上传图片路径
      proportionHeightInImg: 0,//鼠标所选位置相对于所选图片高度的比例
      proportionWidthInImg: 0,//鼠标所选位置相对于所选图片宽度的比例
      signWidth: 25, //设置标记点宽度
      signHeight: 25, //设置标记点高度
      signId: null,
      sign: [], //标点div信息
      signIds: [], //标点id集合
      signXYArray: [], //标点xy位置的对象集合
      triangleBorder: false,//开启三角边框
      circularBorder: false, //圆边框
      squareBorder: true,//方形
      signColor: '#f00',
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
      leftClickDialogBox: false, //左击标点对话框显示状态
      signImageUrl: '', //用户上传标点图片
      signVisible: false, //图片详情
      disabled: false,
    };
  },
  methods: {
    rightClick(e) {
      e.preventDefault();
      e = e || window.event
      var x = e.offsetX || e.layerX;
      var y = e.offsetY || e.layerY;
      //将标点添加到对象中
      let xyObject = {
        x: x,
        y: y
      }
      //将标点对象添加到集合中
      this.signXYArray.push(xyObject)
      //创建标点
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
      div.style.position = 'absolute'; //设置布局
      div.style.left = x + 'px';//设置位置
      div.style.top = y + 'px';
      let textNode=document.createTextNode(this.signXYArray.length);
      div.appendChild(textNode);
      div.style.width = this.signWidth + 'px'; //设置标点宽度
      div.style.height = this.signHeight + 'px'; //设置标点高度
      if (this.squareBorder) {//是否开启正方形
        div.style.background = this.signColor; //设置颜色
      }
      if (this.circularBorder) {//是否开启圆边框
        div.style.background = this.signColor; //设置颜色
        div.style.borderRadius = '50%';
      }
      if (this.triangleBorder) {//是否开启三角边框
        div.style.background = 'transparent'; //设置颜色
        div.style.borderBottom = '10px solid blue';
        div.style.borderTop = '10px solid transparent';
        div.style.borderRight = '10px solid transparent';
        div.style.borderLeft = '10px solid transparent';
      }
      let that = this;
      //添加标点左击事件
      div.onclick = function () {
        that.signClick(that, x, y, this.signId)
      }
      //添加标点右击事件
      div.oncontextmenu = function (e) {
        e.preventDefault();
        that.signClick(that, x, y, this.signId)
      }
      //生成标点
      document.getElementById(divName).appendChild(div)
      //统计标点信息
      this.sign.push(div)
      //统计标点id
      this.signIds.push(this.signId)
    },
    //标点的点击事件
    signClick(that, x, y, signId) {
      x = x + 29 + 'px'
      y = y - 50 + 'px'
      that.leftClickDialogBox = true
      //设置消息框位置
      document.getElementById('leftClickDialogBox').setAttribute('style', 'left: ' + x + ';top:' + y + ';position: absolute;')
    },
    //图片操作
    handleRemove(file, fileList) {
      this.signImageUrl = '';
    },
    handlePictureCardPreview(file) {
      this.signVisible = true;
    },
    addSignImageUrl(file) {
      this.signImageUrl = file.url;
    },
    //清除全部标记
    clearSign() {
      this.signIds.forEach(s => {
        document.getElementById(s).remove();
      })
      this.sign = [] //清除标点div信息
      this.signIds = [] //清除标点id
      this.signXYArray = [] //清除标点在图片上的xy
    },
    //清除图片1000
    clearImage() {
      this.signImageUrl = '';
      this.clearSign();
    },
    //设置标点形状
    judgeShape(signBorder) {
      if (signBorder == '圆形') {
        this.triangleBorder = false;//开启三角边框
        this.circularBorder = true; //圆边框
        this.squareBorder = false;//方形
      } else if (signBorder == '三角形') {
        this.triangleBorder = true;//开启三角边框
        this.circularBorder = false; //圆边框
        this.squareBorder = false;//方形
      } else if (signBorder == '正方形') {
        this.triangleBorder = false;//开启三角边框
        this.circularBorder = false; //圆边框
        this.squareBorder = true;//方形
      }
    },
    //关闭消息框
    leavePicture() {
      this.leftClickDialogBox = false
    },
    //移入对话框不关闭
    leaveDialogBox() {
      this.leftClickDialogBox = true
    },
    //将所有点连在一起
    connectSgin() {
      console.log(this.signXYArray)
      this.signXYArray.map((dot, index) => {
        // 最后一个点没有连线
        if (!dot[index + 1]) return;1000
        const AB = {
          x: dots[index + 1].x - dot.x,
          y: dots[index + 1].y - dot.y,
        }
        const BC = {
          x: 0,
          y: 1,
        }
        // 向量的模
        const a = Math.sqrt(Math.pow(AB.x, 2) + Math.pow(AB.y, 2));
        const b = Math.sqrt(Math.pow(BC.x, 2) + Math.pow(BC.y, 2));
        const aXb = (AB.x * BC.x) + (AB.y * BC.y);
        const cos_ab = aXb / (a * b);
        // 求出偏转角度
        const angle_1 = Math.acos(cos_ab) * (180 / Math.PI);
        // 10 是点的半径, 根据点的大小修改
        lines.push({
          x: dot.x + 10,
          y: dot.y + 10,
          width: a,
          angel: AB.x > 0 ? Math.sqrt(Math.pow(angle_1, 2)) : -Math.sqrt(Math.pow(angle_1, 2))
        })
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

<style>
.marker .signImg {
  /*position: absolute;*/
}

.marker {
  border-radius: 30px;
  color: white;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;

}

.signImg {
  position: relative;
  border: solid 1px #000;
  display: inline-block;
  margin: 100px 100px;
  float: left
}

#leftClickDialogBox {
  width: 150px;
  height: 75px;
  border: 1px solid #333;
  border-radius: 10px;
  filter: drop-shadow(0 0 4px #999);
  background-color: #fff;
}

#leftClickDialogBox::before {
  position: absolute;
  content: '';
  top: 40px;
  left: -10px;
  border-top: 10px solid transparent;
  border-bottom: 10px solid transparent;
  border-right: 10px solid #333;
}

#leftClickDialogBox::after {
  position: absolute;
  content: '';
  border-top: 10px solid transparent;
  border-bottom: 10px solid transparent;
  border-right: 10px solid #fff;
}

</style>

