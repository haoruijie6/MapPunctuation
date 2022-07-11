<template>
  <div class="app-container">
    <div>

      <div style="position: relative" v-for="(item, index) in imageObjects ">
        <img id="signImage" style="height: 20%;width: 20%;" :src="item.imageUrl">
        <el-row :gutter="20">
          <el-col :span="2">
            <el-button type="success"
                       plain
                       size="mini"
                       icon="el-icon-view"
                       @click="queryImageMarker(index)">查看线路图
            </el-button>
          </el-col>
          <el-col :span="2">
            <el-button type="danger"
                       plain
                       size="mini"
                       icon="el-icon-delete"
                       @click="deleteImageMarker(index)">删除线路图
            </el-button>
          </el-col>
        </el-row>
      </div>
      <div>
        <div v-show="signImageUrl != ''">
          <div class="signImg" id="dv">
            <img id="signImage" :src="signImageUrl">
          </div>
          <div style="margin-top: 10px">
            <el-button type="danger"
                       plain
                       size="mini"
                       icon="el-icon-delete"
                       @click="connectTest()">连接点
            </el-button>
          </div>
          <div style="margin-top: 10px">
            <el-button type="danger"
                       plain
                       size="mini"
                       icon="el-icon-delete"
                       v-if="lineIds.length != 0"
                       @click="cleanConnect()">清除连线
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {listInformation, deleteInformation} from "@/api/map/information";
import {updateConfig} from "@/api/system/config";
import {jsPlumb} from "jsplumb";

export default {
  name: "MapViews",
  data() {
    return {
      imageObjects: [], //所有线路图与标点信息
      signObject: [], //标点对象集合
      signImageUrl: '', //用户上传标点图片
      lineIds:[]//线id集合
    };
  },
  created() {
    listInformation().then(response => {
      this.imageObjects = response.data
    });
  },
  methods: {
    //查看线路图详情
    queryImageMarker(index) {
      //清除之前全部标签
      this.signObject.forEach(s => {
        document.getElementById(s.id).remove();
      })
      //清除上一张图线
      this.cleanConnect()
      //初始化数据
      this.signObject = [];
      this.lineIds = [];

      let imageObject = this.imageObjects[index]
      this.signImageUrl = imageObject.imageUrl //设置图片url
      this.signObject = imageObject.tPunctuationInformationPoList//设置当前图片的标点信息
      for (let i = 0; i < this.signObject.length; i++) {
        //创建标点
        let div = this.createMarker(this.signObject[i])
        //生成标点
        document.getElementById("dv").appendChild(div)
      }
      console.log(this.signObject)
    },
    createMarker(signObject) {//获取标点id
      //创建一个div
      var div = document.createElement('div');
      //设置样式和距离
      div.id = signObject.id;
      div.className = 'marker';
      div.style.position = 'absolute'; //设置布局
      div.style.left = signObject.relativeToPictureX + 'px';//设置位置
      div.style.top = signObject.relativeToPictureY + 'px';
      let textNode = document.createTextNode(signObject.signContent);
      div.appendChild(textNode);
      div.style.width = signObject.signWidth + 'px'; //设置标点宽度
      div.style.height = signObject.signHeight + 'px'; //设置标点高度
      if (signObject.signShape) {//是否开启正方形
        div.style.background = signObject.signColor; //设置颜色
      }
      if (this.circularBorder) {//是否开启圆边框
        div.style.background = signObject.signColor; //设置颜色
        div.style.borderRadius = '50%';
      }
      if (this.triangleBorder) {//是否开启三角边框
        div.style.background = 'transparent'; //设置颜色
        div.style.borderBottom = '10px solid ' + signObject.signColor;
        div.style.borderTop = '10px solid transparent';
        div.style.borderRight = '10px solid transparent';
        div.style.borderLeft = '10px solid transparent';
      }
      return div;
    },
    //测试连接点
    connectTest() {
      //初始化数据
      this.lineIds = [];
      if (this.signObject.length < 1) {
        this.$message.error('当前线路图只有一个点!');
        return
      }
      for (let i = 0; i < this.signObject.length; i++) {
        if (i === this.signObject.length - 1) {
          return;
        }
        let distance = this.connect(
          this.signObject[i].relativeToPictureX,
          this.signObject[i].relativeToPictureY,
          this.signObject[i + 1].relativeToPictureX,
          this.signObject[i + 1].relativeToPictureY);
        let line = Math.sqrt(Math.pow((this.signObject[i].relativeToPictureX - this.signObject[i + 1].relativeToPictureX), 2) + Math.pow((this.signObject[i].relativeToPictureY - this.signObject[i + 1].relativeToPictureY), 2))
        console.log(distance)
        console.log(this.signObject[i])
        console.log(this.signObject[i].relativeToPictureX)
        console.log(this.signObject[i + 1].relativeToPictureX)
        console.log(this.signObject[i].relativeToPictureY)
        console.log(this.signObject[i + 1].relativeToPictureY)
        //生成线id
        let lineId = this.getUuid();
        // 设置一个div 宽度为 两点之间的距离，并且以 transform-origin: 0 50% 为圆心旋转，角度已经算出来
        var div = document.createElement('div');
        div.id = lineId;
        div.style.position = "absolute";
        div.style.borderTop = "1px solid black";
        div.style.width = line + "px";
        div.style.top = this.signObject[i].relativeToPictureY + "px";
        div.style.left = this.signObject[i].relativeToPictureX + "px";
        div.style.transform = "rotate(" + distance + "deg)";
        div.style.transformOrigin = "0 50%";
        //存入线id
        this.lineIds.push(lineId);
        // 添加到body 后面
        document.getElementById("dv").appendChild(div)
      }
    },
    //判断存在那个象限
    connect(x1, y1, x2, y2) {
      // 获得人物中心和鼠标坐标连线，与y轴正半轴之间的夹角
      var x = x1 - x2;
      var y = y1 - y2;
      var z = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
      var cos = y / z;
      var radina = Math.acos(cos); // 用反三角函数求弧度
      var angle = 180 / (Math.PI / radina); // 将弧度转换成角度
      console.log(angle)
      if (x2 > x1 && y2 === y1) {
        // 在x轴正方向上
        angle = 0;
      }
      if (x2 > x1 && y2 < y1) {
        // 在第一象限;
        angle = angle - 90;
      }
      if (x2 === x1 && y1 > y2) {
        // 在y轴正方向上
        angle = -90;
      }
      if (x2 < x1 && y2 < y1) {
        console.log(21122111111111)
        // 在第二象限
        angle = 270 - angle;
      }
      if (x2 < x1 && y2 === y1) {
        // 在x轴负方向
        angle = 180;
      }
      if (x2 < x1 && y2 > y1) {
        // 在第三象限
        angle = 270 - angle;
      }
      if (x2 === x1 && y2 > y1) {
        // 在y轴负方向上
        angle = 90;
      }
      if (x2 > x1 && y2 > y1) {
        // 在四象限
        angle = angle - 90;
      }
      return angle;
    },
    queryImageList() {
      listInformation().then(response => {
        this.imageObjects = response.data
      });
    },
    deleteImageMarker(index) {
      deleteInformation(this.imageObjects[index].id).then(res => {
        if (res.code === 200) {
          this.$modal.msgSuccess("删除成功!");
          this.queryImageList();
          //初始化数据
          this.signObject = [];
        } else {
          this.$message.error('删除失败,请重试!');
        }
      });
    },
    cleanConnect(){
      //清除之前的连线
      this.lineIds.forEach(s => {
        document.getElementById(s).remove();
      })
      this.lineIds = [];
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

