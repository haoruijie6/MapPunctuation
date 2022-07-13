<template>
  <div>
    <div style="margin-top: 30px; margin-left: 50px;">
      <el-upload
        class="upload-demo"
        ref="upload"
        action="https://jsonplaceholder.typicode.com/posts/"
        :on-preview="handlePictureCardPreview"
        :on-remove="handleRemove"
        :on-change="addSignImageUrl"
        :show-file-list="false"
        :multiple="false"
        name="file"
        :auto-upload="false">
        <el-button slot="trigger"
                   icon="el-icon-circle-plus-outline"
                   type="primary">制作线路图
        </el-button>
      </el-upload>
    </div>

    <div style="width: 100%;">
      <div class="signImageTables" v-for="(item, index) in queryImageObjects ">
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

    <el-dialog @close="clearImage" width="80%" title="线路图标点" :visible.sync="zhiZuoOpen">
      <el-form label-width="68px" style="margin-bottom: 20px;" :inline="true">
        <el-form-item label="宽度">
          <el-input
            v-model="signWidth"
            placeholder="设置标点宽度"
            clearable
            style="width: 240px"/>
        </el-form-item>
        <el-form-item label="高度">
          <el-input
            v-model="signHeight"
            placeholder="设置标点高度"
            clearable
            style="width: 240px"/>
        </el-form-item>
        <el-form-item label="形状">
          <el-select v-model="signBorder" placeholder="请选择标点形状" style="width: 240px;">
            <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value"
              :change="judgeShape(signBorder)">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设置颜色">
          <div class="block">
            <el-color-picker v-model="signColor"></el-color-picker>
          </div>
        </el-form-item>
        <el-row>
          <el-form-item>
            <el-button
              type="success"
              plain
              size="mini"
              icon="el-icon-upload2"
              @click="addImageAndSgin">上传
            </el-button>
          </el-form-item>
          <el-form-item>
            <el-button
              type="danger"
              plain
              size="mini"
              icon="el-icon-delete"
              @click="clearSign">清空所有元素
            </el-button>
          </el-form-item>
          <el-form-item>
            <el-button
              type="danger"
              plain
              size="mini"
              icon="el-icon-delete"
              @click="clearImage">删除图片
            </el-button>
          </el-form-item>
        </el-row>
        <el-form-item>
          <div>
            <div class="signImg" id="dv" @mouseleave="leavePicture" v-show="signImageUrl != ''">
              <div style="position: relative">
                <img id="signImage" @click="leavePicture" @contextmenu="rightClick($event)" :src="signImageUrl">
                <div id="leftClickDialogBox" v-show="leftClickDialogBox" @mouseover="leaveDialogBox">
                  消息框
                </div>
                <el-popover
                  placement="top"
                  width="160"
                  v-model="visible"
                  @mouseover="leaveDialogBox">
                  <p>确定删除这个标点吗？</p>
                  <div style="text-align: right; margin: 0">
                    <el-button size="mini" type="text" @click="visible = false">取消</el-button>
                    <el-button type="primary" size="mini" @click="deleteOneSignAndObject">确定</el-button>
                  </div>
                  <el-button v-show="deleteOneSignView" id="deleteOneSign" slot="reference">删除</el-button>
                </el-popover>
              </div>
            </div>
          </div>
        </el-form-item>
      </el-form>

    </el-dialog>
    <el-dialog :width="queryImageWidth+50+'px'" title="线路图详情" :visible.sync="queryOpen" @close="removeConnect">
      <el-form label-width="68px" style="margin-bottom: 20px;" :inline="true">
        <el-form-item>
          <el-button type="danger"
                     plain
                     icon="el-icon-share"
                     @click="queryCreateMarker()">查看标记点
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button
            type="danger"
            plain
            icon="el-icon-share"
            @click="connectLine()">连接点
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button
            type="danger"
            plain
            @click="cleanConnect()">清除连线
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button
            type="danger"
            plain
            @click="cleanSgin()">清除点
          </el-button>
        </el-form-item>
        <el-form-item>
          <div style="width: 100%;">
            <div style="text-align:center;">
              <div class="signImg" id="queryDv">
                <img :src="querySignImageUrl" style="padding-bottom: 25px;background-color: white;">
              </div>
            </div>
          </div>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import {
  listInformation, deleteInformation
} from "@/api/map/information";
import axios from 'axios'
import {getToken} from "@/utils/auth";

export default {
  name: "MapAdda",
  data() {
    return {
      zhiZuoOpen: false,
      queryOpen: false,
      visible: false,
      imgId: '',
      queryImageObjects: [], //所有线路图与标点信息
      querySignObject: [], //查询的标点对象集合
      querySignImageUrl: '', //用户上传标点图片
      lineIds: [],//线id集合
      queryImageWidth: 0,
      queryImageHeight: 0,
      signImageWidth: 0, //线路图宽
      signImageHeight: 0, //线路图高
      signWidth: 35, //设置标记点宽度
      signHeight: 35, //设置标记点高度
      signId: null,
      sign: [], //标点div信息
      signIds: [], //标点id集合
      signObject: [], //标点对象集合
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
      file: null,
      deleteOneSignView: false, //右击删除标记
      deleteSignId: null,//右击删除是或其标识点id
    };
  },
  created() {
    listInformation().then(response => {
      this.queryImageObjects = response.data
    });
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
      //创建标点
      this.createMarker(x, y, 'dv');
    },
    createMarker(x, y, divName) {
      //获取标点id
      var signId = this.getUuid();
      //创建一个div
      var div = document.createElement('div');
      //设置样式和距离
      div.id = signId;
      div.className = 'marker';
      div.style.position = 'absolute'; //设置布局
      div.style.left = x + 'px';//设置位置
      div.style.top = y + 'px';
      let textNode = document.createTextNode(this.signObject.length + 1 + '');
      div.appendChild(textNode);
      div.style.width = this.signWidth + 'px'; //设置标点宽度
      div.style.height = this.signHeight + 'px'; //设置标点高度
      //判断标点形状
      let shape = 1;
      if (this.squareBorder) {//是否开启正方形
        div.style.background = this.signColor; //设置颜色
        let shape = 1
      }
      if (this.circularBorder) {//是否开启圆边框
        div.style.background = this.signColor; //设置颜色
        div.style.borderRadius = '50%';
        let shape = 2
      }
      if (this.triangleBorder) {//是否开启三角边框
        div.style.background = 'transparent'; //设置颜色
        div.style.borderBottom = '10px solid ' + this.signColor;
        div.style.borderTop = '10px solid transparent';
        div.style.borderRight = '10px solid transparent';
        div.style.borderLeft = '10px solid transparent';
        let shape = 3
      }
      let that = this;
      //添加标点左击事件
      div.onclick = function () {
        that.signClick(that, x, y, signId)
      }
      //添加标点右击删除事件
      div.oncontextmenu = function (e) {
        e.preventDefault();
        that.deleteOneSign(that, x, y, signId)
      }
      //生成标点
      document.getElementById(divName).appendChild(div)
      //创建标点对象
      var signObject = {
        id: signId, //标点id
        imageId: this.imgId, //图片id
        relativeToPictureX: x, //标点相对于图片的x轴距离
        relativeToPictureY: y, //标点相对于图片的y轴距离
        signWidth: this.signWidth, //标点宽度
        signHeight: this.signHeight, //标点高度
        punctuationElement: div, //标点元素
        signContent: this.signObject.length + 1,//标点内容
        signShape: shape,//标点形状 1正方形 2圆形 3三角形
        signColor: this.signColor,
        sort: this.signObject.length + 1 //设置排序
      }
      //将标点对象存入集合
      this.signObject.push(signObject);
      //统计标点id
      this.signIds.push(signId)
    },
    //标点的点击事件
    signClick(that, x, y, signId) {
      that.deleteOneSignView = false;
      that.leftClickDialogBox = true;
      x = x + 2 * this.signHeight + 'px';
      y = y - 50 + 'px';
      //设置消息框位置
      document.getElementById('leftClickDialogBox').setAttribute('style', 'left: ' + x + ';top:' + y + ';position: absolute;')
    },
    //标点的点击事件
    deleteOneSign(that, x, y, signId) {
      that.deleteSignId = signId;
      that.deleteOneSignView = true
      that.leftClickDialogBox = false;
      x = x + this.signHeight + 'px'
      y = y + 'px'
      //设置消息框位置
      document.getElementById('deleteOneSign').setAttribute('style', 'left: ' + x + ';top:' + y + ';position: absolute;')
    },
    //删除标点
    deleteOneSignAndObject() {
      //删除元素
      document.getElementById(this.deleteSignId).remove();
      let arrLength;
      //删除元素数据修改其他元素
      for (let i = 0; i < this.signIds.length; i++) {
        if (this.signIds[i] === this.deleteSignId) {
          arrLength = i; //获取元素下标
          break;
        }
      }
      //将后续元素放入临时对象数组中
      let temporary = [];
      for (let j = arrLength + 1; j < this.signObject.length; j++) {
        temporary.push(this.signObject[j])
      }
      //修改后面元素的值
      for (let k = 0; k < temporary.length; k++) {
        let div = document.getElementById(temporary[k].id);
        //删除之前标点中node
        div.removeChild(div.childNodes[0]);
        let updateNextNode = document.createTextNode(temporary[k].sort - 1 + '');
        //重新赋值
        div.appendChild(updateNextNode);
        temporary[k].signContent = temporary[k].sort - 1;
        temporary[k].sort = temporary[k].sort - 1;
      }
      this.signIds.splice(arrLength, 1);
      this.signObject.splice(arrLength, this.signObject.length);
      //重组标点数组对象
      temporary.forEach(t => {
        this.signObject.push(t)
      })
      this.deleteSignId = null;
      this.visible = false
    },
    addImageAndSgin() {
      this.signImageWidth = document.getElementById("dv").clientWidth;
      this.signImageHeight = document.getElementById("dv").clientHeight;
      if (this.file == null) {
        this.$modal.msgError("请选择线路图");
        return
      }
      if (this.signObject.length === 0) {
        this.$modal.msgError("线路图未标点");
        return
      }
      var imageObject = {
        id: this.imgId,
        imageName: null,
        imageWidth: this.signImageWidth,
        imageHeigth: this.signImageHeight,
        tPunctuationInformationPoList: this.signObject
      }
      let formData = new FormData();
      formData.append('image', JSON.stringify(imageObject));
      if (this.signImageUrl.indexOf("data:image") !== -1) {
        formData.append('file', this.file);
      }
      axios.post(process.env.VUE_APP_BASE_API + "/map/add", formData,
        {
          headers: {
            'Authorization': 'Bearer ' + getToken(),
            "Content-Type": 'multipart/form-data;'
          }
        }
      ).then(res => {
        if (res.data.code === 200) {
          this.$modal.msgSuccess("新增成功");
          this.queryImageList();
          this.zhiZuoOpen = false;
          //初始化数据
          this.clearImage();
        } else {
          this.$message.error('提交失败');
        }
      })

    },
    //图片操作
    handleRemove(file, fileList) {
      this.signImageUrl = '';
    },
    handlePictureCardPreview(file) {
      this.signVisible = true;
    },
    addSignImageUrl(file, fileList) {
      this.file = file.raw;
      const isJPG = this.file.type == 'image/jpg'
      const isPNG = this.file.type == 'image/png'
      const isJPEG = this.file.type == 'image/jpeg'
      const isLt2M = this.file.size / 1024 / 1024 < 5
      if (!isJPG && !isPNG && !isJPEG) {
        this.$message.error('请上传符合格式的图片!');
        this.item = [];
        return;
      }
      if (!isLt2M) {
        this.$message.error('上传图片大小不能超过 5MB!');
        this.item = [];
        return;
      }
      let reader = new FileReader();
      reader.readAsDataURL(this.file);
      let that = this;
      reader.onload = e => {
        //设置图片
        that.signImageUrl = e.target.result;
      }
      //设置图片id
      this.imgId = this.getUuid();
      this.zhiZuoOpen = true;
    },
    //清除全部标记
    clearSign() {
      this.signIds.forEach(s => {
        document.getElementById(s).remove();
      })
      this.signIds = []; //清除标点id
      this.signObject = []; //清除标点对象集合
    },
    //清除图片1000
    clearImage() {
      this.signImageUrl = ''; //删除图片url
      this.file = null; //清除线路图文件
      this.imgId = '' //清除图片id
      this.clearSign();
      this.zhiZuoOpen = false;
    },
    //关闭消息框
    leavePicture() {
      this.leftClickDialogBox = false
      this.deleteOneSignView = false
    },
    //移入对话框不关闭
    leaveDialogBox() {
      this.leftClickDialogBox = true
    },
    //查询的创建点
    queryCreateMarker() {
      for (let i = 0; i < this.querySignObject.length; i++) {
        var signObject = this.querySignObject[i];
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
        document.getElementById("queryDv").appendChild(div)
      }
    },
    //测试连接点
    connectLine() {
      //初始化数据
      this.lineIds = [];
      if (this.querySignObject.length < 1) {
        this.$message.error('当前线路图只有一个点!');
        return
      }
      for (let i = 0; i < this.querySignObject.length; i++) {
        if (i === this.querySignObject.length - 1) {
          return;
        }
        let distance = this.connect( //计算角度
          this.querySignObject[i].relativeToPictureX,
          this.querySignObject[i].relativeToPictureY,
          this.querySignObject[i + 1].relativeToPictureX,
          this.querySignObject[i + 1].relativeToPictureY);
        //计算线长度
        let line = Math.sqrt(Math.pow((this.querySignObject[i].relativeToPictureX - this.querySignObject[i + 1].relativeToPictureX), 2) + Math.pow((this.querySignObject[i].relativeToPictureY - this.querySignObject[i + 1].relativeToPictureY), 2))
        //生成线id
        let lineId = this.getUuid();
        //计算线位置
        let lineWidth = Number(this.querySignObject[i].relativeToPictureY) + Number(this.querySignObject[i].signHeight / 2);
        let lineHeigth = Number(this.querySignObject[i].relativeToPictureX) + Number(this.querySignObject[i].signWidth / 2);
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
        document.getElementById("queryDv").appendChild(div)
      }
    },
    queryImageList() {
      listInformation().then(response => {
        this.queryImageObjects = response.data
      });
    },
    //查看线路图详情
    queryImageMarker(index) {
      //打开弹窗
      this.queryOpen = true;
      //获取选择的线路图
      let imageObject = this.queryImageObjects[index]
      this.queryImageWidth = Number(imageObject.imageWidth);
      this.queryImageHeight = Number(imageObject.imageHeigth);
      this.querySignImageUrl = imageObject.imageUrl; //设置图片url
      this.querySignObject = imageObject.tPunctuationInformationPoList//设置当前图片的标点信息
    },
    //删除线路图
    deleteImageMarker(index) {
      var that = this;
      this.$modal.confirm('确认要删除这张线路图吗').then(function () {
        return deleteInformation(that.queryImageObjects[index].id);
      }).then(() => {
        this.$modal.msgSuccess("删除成功");
        this.queryImageList();
        //初始化数据
        this.querySignObject = [];
      }).catch(() => {
      });
    },
    //remove元素
    removeConnect() {
      try {
        //清除之前全部标签
        this.querySignObject.forEach(s => {
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
        this.querySignObject.forEach(s => {
          document.getElementById(s.id).remove();
        })
        this.$modal.msgSuccess("清除成功");
      } catch (e) {
        this.$modal.msgSuccess("暂无标点");
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

.signImageTables {
  position: relative;
  width: 280px;
  height: 230px;
  float: left;
  margin: 45px 20px 100px 40px;
}
</style>

