<template>
  <div>
    <div style="width: 100%;">
      <div class="signImageTables" v-for="(item, index) in imageObjects ">
        <img style="height: 100%;width: 100%;" :src="item.imageUrl"/>
        <div style="text-align:center;">
          <el-button type="success"
                     plain
                     icon="el-icon-view"
                     @click="queryImageMarker(index)">查看线路图
          </el-button>
          <el-button type="danger"
                     plain
                     icon="el-icon-delete"
                     @click="deleteImageMarker(index)">删除线路图
          </el-button>
        </div>
      </div>
    </div>
    <!--    <div style="width: 100%;">-->
    <!--      <div v-show="signImageUrl !== ''" style="text-align:center;">-->
    <!--        <div class="signImg" id="dv">-->
    <!--          <img :src="signImageUrl" style="padding-bottom: 25px">-->
    <!--          <div>-->
    <!--            <el-button type="danger"-->
    <!--                       plain-->
    <!--                       icon="el-icon-share"-->
    <!--                       @click="connectLine()">连接点-->
    <!--            </el-button>-->
    <!--            <el-button type="danger"-->
    <!--                       plain-->
    <!--                       v-if="lineIds.length !== 0"-->
    <!--                       @click="cleanConnect()">清除连线-->
    <!--            </el-button>-->
    <!--          </div>-->
    <!--        </div>-->
    <!--      </div>-->
    <!--    </div>-->
    <!-- 用户导入对话框 -->
    <el-dialog :width="imageWidth+50+'px'" :visible.sync="open" :hide="removeConnect">
      <div style="width: 100%;">
        <div style="text-align:center;">
          <div class="signImg" id="dv">
            <img :src="signImageUrl" style="padding-bottom: 25px;background-color: white;">
            <div style="background-color: white;">
              <el-button type="danger"
                         plain
                         icon="el-icon-share"
                         @click="createMarker()">查看标记点
              </el-button>
              <el-button
                type="danger"
                plain
                icon="el-icon-share"
                @click="connectLine()">连接点
              </el-button>
              <el-button type="danger"
                         plain
                         @click="cleanConnect()">清除连线
              </el-button>
              <el-button type="danger"
                         plain
                         @click="cleanSgin()">清除点
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>


  </div>
</template>

<script>
import {listInformation, deleteInformation} from "@/api/map/information";

export default {
  name: "MapViews",
  data() {
    return {
      title: "线路图详细",
      open: false,
      imageObjects: [], //所有线路图与标点信息
      signObject: [], //标点对象集合
      signImageUrl: '', //用户上传标点图片
      lineIds: [],//线id集合
      imageWidth: 0,
      imageHeight: 0 ,
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
      //清理之前数据
      this.removeConnect();
      //打开弹窗
      this.open = true;
      //获取选择的线路图
      let imageObject = this.imageObjects[index]
      this.imageWidth = Number(imageObject.imageWidth) ;
      this.imageHeight = Number(imageObject.imageHeigth) ;
      this.signImageUrl = imageObject.imageUrl; //设置图片url
      this.signObject = imageObject.tPunctuationInformationPoList//设置当前图片的标点信息

    },
    //创建点
    createMarker() {
      for (let i = 0; i < this.signObject.length; i++) {
        var signObject = this.signObject[i];
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
        div.style.zIndex = '999';//设置z轴排序
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
        //生成标点
        document.getElementById("dv").appendChild(div)
      }
    },
    //测试连接点
    connectLine() {
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
        let distance = this.connect( //计算角度
          this.signObject[i].relativeToPictureX,
          this.signObject[i].relativeToPictureY,
          this.signObject[i + 1].relativeToPictureX,
          this.signObject[i + 1].relativeToPictureY);
        //计算线长度
        let line = Math.sqrt(Math.pow((this.signObject[i].relativeToPictureX - this.signObject[i + 1].relativeToPictureX), 2) + Math.pow((this.signObject[i].relativeToPictureY - this.signObject[i + 1].relativeToPictureY), 2))
        //生成线id
        let lineId = this.getUuid();
        //计算线位置
        let lineWidth = Number(this.signObject[i].relativeToPictureY) + Number(this.signObject[i].signHeight / 2);
        let lineHeigth = Number(this.signObject[i].relativeToPictureX) + Number(this.signObject[i].signWidth / 2);
        var div = document.createElement('div');
        div.id = lineId;
        div.style.position = "absolute";
        div.style.borderTop = "3px solid red"; //设置样式
        div.style.width = line + "px"; //设置宽度
        div.style.top = lineWidth + "px"; // 设置位置
        div.style.left = lineHeigth + "px";
        div.style.transform = "rotate(" + distance + "deg)"; //设置角度
        div.style.transformOrigin = "0 50%"; //设置z轴排序
        div.style.zIndex = '1';//设置z轴排序
        //存入线id
        this.lineIds.push(lineId);
        // 添加到body 后面
        document.getElementById("dv").appendChild(div)
      }
    },
    //判断存在那个象限
    connect(x1, y1, x2, y2) {
      //转换类型
      x1 = Number(x1);
      y1 = Number(y1);
      x2 = Number(x2);
      y2 = Number(y2);
      // 获得人物中心和鼠标坐标连线，与y轴正半轴之间的夹角
      var x = x1 - x2;
      var y = y1 - y2;
      var z = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
      var cos = y / z;
      var radina = Math.acos(cos); // 用反三角函数求弧度
      var angle = 180 / (Math.PI / radina); // 将弧度转换成角度
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
    //删除线路图
    deleteImageMarker(index) {
      var that = this;
      this.$modal.confirm('确认要删除这张线路图吗').then(function () {
        return deleteInformation(that.imageObjects[index].id);
      }).then(() => {
        this.$modal.msgSuccess("删除成功");
        this.queryImageList();
        //初始化数据
        this.signObject = [];
      }).catch(() => {
      });
    },
    //remove元素
    removeConnect() {
      try {
        //清除之前全部标签
        this.signObject.forEach(s => {
          document.getElementById(s.id).remove();
        })
      } catch (e) {
      } finally {
        //清除上一张图线
        try {
          //清除之前的连线
          this.lineIds.forEach(s => {
            document.getElementById(s).remove();
          })
        } catch (e) {
        } finally {
          this.lineIds = [];
        }
        //初始化数据
        this.signObject = [];
        this.lineIds = [];
      }
    },
    cleanConnect() {
      try {
        //清除之前的连线
        this.lineIds.forEach(s => {
          document.getElementById(s).remove();
        })
        this.$modal.msgSuccess("清除成功")
      } catch (e) {
        this.$modal.msgSuccess("未连线")
      } finally {
        this.lineIds = [];
      }
    },
    cleanSgin() {
      try {
        //清除之前全部标签
        this.signObject.forEach(s => {
          document.getElementById(s.id).remove();
        })
        this.$modal.msgSuccess("清除成功");
      } catch (e) {
        this.$modal.msgSuccess("暂无标点");
      }
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
.signImageTables {
  position: relative;
  width: 280px;
  height: 230px;
  float: left;
  margin: 45px 0px 100px 20px;
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
  float: left;
  display: block;
  margin: 0 auto;
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

