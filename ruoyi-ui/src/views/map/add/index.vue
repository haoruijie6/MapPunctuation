<template>
  <div>
    <div style="margin:30px 0px 80px 50px;">
      <el-form label-width="68px" style="margin-bottom: 20px;" :inline="true">
        <el-form-item label="线路图名">
          <el-input
            v-model="queryParam.imageName"
            placeholder="请输入线路图名称"
            clearable
            style="width: 240px;margin-left: 20px"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="queryImage">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
        <el-col>
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
            <el-button
              slot="trigger"
              icon="el-icon-circle-plus-outline"
              type="warning">制作线路图
            </el-button>
          </el-upload>
        </el-col>
      </el-form>
    </div>
    <div style="margin:30px 30px 20px 30px">
      <el-empty description="暂无线路图" v-if="queryImageObjects.length === 0"></el-empty>
      <el-row :gutter="20" v-loading="loadingImageList" v-else>
        <el-col style="float: left;" :span="6" v-for="(item ,index) in queryImageObjects" :key="index">
          <el-card>
            <img style="width: 100%; height: 250px;" :src="item.imageUrl" class="image multi-content"/>
            <div style="padding: 27px;">
              <span>线路图名称:{{ item.imageName }}</span>
              <div class="bottom card-header">
                <span class="time">创建时间:{{ item.createTime }}</span>
                <div style="margin: 20px 0px 0px 0px; font-size: 20px; float: right;">
                  <el-button type="text" size="medium" class="button" @click="queryImageMarker(index)">查看线路图</el-button>
                  <el-button type="text" style="color: red" size="medium" class="button"
                             @click="deleteImageMarker(index)">删除线路图
                  </el-button>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    <!--  线路图标点  -->
    <el-dialog :width="signImageWidth+50+'px'" @close="clearImage" title="线路图标点" :visible.sync="zhiZuoOpen">
      <el-form :inline="true">
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
        <el-form-item label="线路图名">
          <el-input
            v-model="imageName"
            placeholder="请输入线路图名称"
            clearable
            style="width: 240px"/>
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
              icon="el-icon-upload2"
              @click="addImageAndSgin">上传
            </el-button>
          </el-form-item>
          <el-form-item>
            <el-button
              type="danger"
              plain
              icon="el-icon-circle-close"
              @click="clearSign">清空所有元素
            </el-button>
          </el-form-item>
          <el-form-item>
            <el-button
              type="danger"
              plain
              icon="el-icon-delete"
              @click="clearImage">删除图片
            </el-button>
          </el-form-item>
        </el-row>
        <el-form-item>
          <div>
            <div class="signImg" id="dv" @mouseleave="leavePicture" v-show="signImageUrl != ''">
              <div style="position: relative">
                <img id="signImage" @click="leavePicture" @contextmenu="rightClick($event)" :src="signImageUrl" alt="">
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
                  <el-button style="z-index: 3" v-show="deleteOneSignView" id="deleteOneSign" type="danger"
                             slot="reference">删除
                  </el-button>
                  <el-button style="z-index: 3" v-show="leftClickDialogBox" @click="addSignTaskShow = true"
                             type="success" id="addSignTask" slot="reference">添加巡检点任务
                  </el-button>
                </el-popover>
              </div>
            </div>
          </div>
        </el-form-item>
      </el-form>
    </el-dialog>
    <!-- 查看线路图详情  -->
    <el-dialog :width="queryImageWidth+50+'px'" title="线路图详情" :visible.sync="queryOpen" @close="removeConnect">
      <el-form :inline="true">
        <el-form-item>
          <el-button
            type="success"
            plain
            icon="el-icon-share"
            @click="queryCreateMarker()">显示标记点
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button
            type="danger"
            plain
            icon="el-icon-circle-close"
            @click="cleanSgin()">隐藏标记点
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button
            type="success"
            plain
            icon="el-icon-share"
            @click="connectLine()">连接标记点
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button
            type="danger"
            plain
            icon="el-icon-circle-close"
            @click="cleanConnect()">隐藏连线
          </el-button>
        </el-form-item>
        <el-form-item>
          <div style="width: 100%;">
            <div style="text-align:center;">
              <div class="signImg" id="queryDv">
                <img :src="querySignImageUrl" alt="">
              </div>
            </div>
          </div>
        </el-form-item>
      </el-form>
    </el-dialog>
    <!-- 添加标点任务 -->
    <el-drawer
      :title="addSignTaskTitle"
      :before-close="handleClose"
      :visible.sync="addSignTaskShow"
      direction="rtl"
      ref="drawer">
      <div class="demo-drawer__content">
        <el-form :model="punctuationTask">
          <el-form-item label="活动名称" label-width="80px">
            <el-input v-model="punctuationTask.name" autocomplete="off"></el-input>
          </el-form-item>
          <el-form-item label="活动区域" label-width="80px">
            <el-select v-model="punctuationTask.region" placeholder="请选择活动区域">
              <el-option label="区域一" value="shanghai"></el-option>
              <el-option label="区域二" value="beijing"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <div class="demo-drawer__footer">
          <el-button @click="cancelForm" style="width: 200px;">取 消</el-button>
          <el-button type="primary" style="width: 200px;" @click="$refs.drawer.closeDrawer()" :loading="punctuationTaskLoading">
            {{ punctuationTaskLoading ? '提交中 ...' : '确 定' }}
          </el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script>
import {listInformation, deleteInformation} from "@/api/map/map";
import axios from 'axios'
import {getToken} from "@/utils/auth";
import {Loading} from 'element-ui';

export default {
  name: "MapAdda",
  data() {
    return {
      zhiZuoOpen: false, //制作线路图弹窗状态
      queryOpen: false, //查看线路图弹窗状态
      visible: false, //删除框显示状态
      imageName: '',//线路图名称
      imgId: '', //图片id
      queryImageObjects: [], //所有线路图与标点信息
      querySignObject: [], //查询的标点对象集合
      querySignImageUrl: '', //用户上传标点图片
      lineIds: [],//线id集合
      queryImageWidth: 0, //查看线路图的宽度
      queryImageHeight: 0, //查看线路图的高度
      signImageWidth: 0, //线路图宽
      signImageHeight: 0, //线路图高
      signWidth: 35, //设置标记点宽度
      signHeight: 35, //设置标记点高度
      signId: null, //标点id
      sign: [], //标点div信息
      signIds: [], //标点id集合
      signObject: [], //标点对象集合
      signColor: '#f00', //标点颜色 默认红色
      leftClickDialogBox: false, //左击标点对话框显示状态
      signImageUrl: '', //用户上传标点图片
      signVisible: false, //图片详情
      file: null,
      deleteOneSignView: false, //右击删除标记
      deleteSignId: null,//右击删除是或其标识点id
      queryParam: { //查询线路图参数
        imageName: null
      },
      addSignTaskShow: false, //标点添加任务框显示状态
      addSignTaskTitle: '',//添加标点任务框的title
      loadingImageList: false,//列表加载loading状态
      punctuationTask: {},//标点任务对象
      punctuationTaskLoading: false,//保存标点任务loading
    }
  },
  created() {
    this.queryImage();
  },
  methods: {
    queryImage() {
      //开启loading
      this.loadingImageList = true;
      listInformation(this.queryParam).then(response => {
        this.queryImageObjects = response.data
      });
      setTimeout(() => {
        this.loadingImageList = false;
      }, 500);
    },
    resetQuery() {
      this.queryParam.imageName = null;
      this.queryImage();
    },
    rightClick(e) {
      e.preventDefault();
      e = e || window.event
      let x = e.offsetX;
      let y = e.offsetY;
      //创建标点
      this.createMarker(x, y, 'dv');
    },
    createMarker(x, y, divName) {
      //获取标点id
      let signId = this.getUuid();
      //创建一个div
      let div = document.createElement('div');
      //标点内容
      let signContent = this.signObject.length + 1;
      //设置样式和距离
      div.id = signId;
      div.className = 'marker';
      div.style.position = 'absolute'; //设置布局
      div.style.left = x + 'px';//设置位置
      div.style.top = y + 'px';
      let textNode = document.createTextNode(signContent + '');
      div.appendChild(textNode);
      div.style.width = this.signWidth + 'px'; //设置标点宽度
      div.style.height = this.signHeight + 'px'; //设置标点高度
      div.style.backgroundColor = this.signColor;//设置背景颜色
      div.style.zIndex = "2"
      let that = this;
      //添加标点左击事件
      div.onclick = function () {
        that.signClick(that, x, y, signId, signContent)
      }
      //添加标点右击删除事件
      div.oncontextmenu = function (e) {
        e.preventDefault();
        that.deleteOneSign(that, x, y, signId)
      }
      //生成标点
      document.getElementById(divName).appendChild(div)
      //创建标点对象
      let signObject = {
        id: signId, //标点id
        signName: null, //标点名称
        imageId: this.imgId, //图片id
        relativeToPictureX: x, //标点相对于图片的x轴距离
        relativeToPictureY: y, //标点相对于图片的y轴距离
        signWidth: this.signWidth, //标点宽度
        signHeight: this.signHeight, //标点高度
        punctuationElement: div, //标点元素
        signContent: signContent,//标点内容
        signShape: null,//标点形状 1正方形 2圆形 3三角形
        signColor: this.signColor,
        sort: signContent, //设置排序
        signTask: {}, //标点任务
      }
      //将标点对象存入集合
      this.signObject.push(signObject);
      //统计标点id
      this.signIds.push(signId)
    },
    //标点的点击事件
    signClick(that, x, y, signId, signContent) {
      that.deleteOneSignView = false;
      that.leftClickDialogBox = true;
      this.addSignTaskTitle = "设置" + signContent + "号标点任务";

      // this.add
      x = x + this.signHeight + 'px'
      y = y + 'px'
      //设置消息框位置
      document.getElementById('addSignTask').setAttribute('style', 'left: ' + x + ';top:' + y + ';position: absolute;')
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
      if (this.file == null) {
        this.$modal.msgError("请选择线路图");
        return
      }
      if (this.signObject.length === 0) {
        this.$modal.msgError("线路图未标点");
        return
      }
      if (this.imageName === '' || this.imageName == null) {
        this.$message.error('请输入图片名称!');
        return
      }
      const loading = this.$loading({
        lock: true,
        text: '上传线路图中...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });

      let imageObject = {
        id: this.imgId,
        imageName: this.imageName,
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
          this.zhiZuoOpen = false;
          //初始化数据
          this.clearImage();
          loading.close();
          this.queryImageList();
        } else {
          this.$message.error('提交失败');
        }
      })

    },
    //图片操作
    handleRemove() {
      this.signImageUrl = '';
    },
    handlePictureCardPreview() {
      this.signVisible = true;
    },
    addSignImageUrl(file) {
      this.file = file.raw;
      const isJPG = this.file.type === 'image/jpg'
      const isPNG = this.file.type === 'image/png'
      const isJPEG = this.file.type === 'image/jpeg'
      const isLt2M = this.file.size / 1024 / 1024 < 5
      if (!isJPG && !isPNG && !isJPEG) {
        this.$message.error('请上传符合格式的图片!');
        this.file = null;
        return;
      }
      if (!isLt2M) {
        this.$message.error('上传图片大小不能超过 5MB!');
        this.file = null;
        return;
      }
      let reader = new FileReader();
      reader.readAsDataURL(this.file);
      let that = this;
      reader.onload = e => {
        //设置图片
        that.signImageUrl = e.target.result;
        let img = new Image();
        img.src = e.target.result;
        //解析图片获取图片宽度
        img.onload = function () {
          that.signImageWidth = this.width //设置图片宽度
          that.signImageHeight = this.height //设置图片高度
        }
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
      this.zhiZuoOpen = false;
      this.signImageUrl = ''; //删除图片url
      this.file = null; //清除线路图文件
      this.imgId = '' //清除图片id
      this.imageName = null //清除图片名称
      this.signImageWidth = null //删除图片宽度
      this.signImageHeight = null //删除图片高度
      this.clearSign();
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
      try {
        //清除之前全部标签
        this.querySignObject.forEach(s => {
          document.getElementById(s.id).remove();
        })
      } catch (e) {
      }
      for (let i = 0; i < this.querySignObject.length; i++) {
        let signObject = this.querySignObject[i];
        //创建一个div
        let div = document.createElement('div');
        //设置样式和距离
        div.id = signObject.id;
        div.className = 'marker';
        div.style.backgroundColor = signObject.signColor; //设置背景颜色
        div.style.position = 'absolute'; //设置布局
        div.style.left = signObject.relativeToPictureX + 'px';//设置位置
        div.style.top = signObject.relativeToPictureY + 'px';
        let textNode = document.createTextNode(signObject.signContent);
        div.appendChild(textNode);
        div.style.width = signObject.signWidth + 'px'; //设置标点宽度
        div.style.height = signObject.signHeight + 'px'; //设置标点高度
        div.style.zIndex = '999';//设置z轴排序
        //生成标点
        document.getElementById("queryDv").appendChild(div)
      }
    },
    //测试连接点
    connectLine() {
      //判断有没有标点
      if (document.getElementById(this.querySignObject[0].id) == null) {
        this.$message.error('没找到标点!');
        return;
      }
      //判断标点个数
      if (this.querySignObject.length < 1) {
        this.$message.error('当前线路图只有一个点!');
        return
      }
      //初始化数据
      if (this.lineIds.length > 0) {
        //清除之前的连线
        this.lineIds.forEach(s => {
          document.getElementById(s).remove();
        })
        this.lineIds = []
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
        let div = document.createElement('div');
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
      let that = this;
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
    //清除点和线
    removeConnect() {
      try {
        //清除之前全部标签
        this.querySignObject.forEach(s => {
          document.getElementById(s.id).remove();
        })
      } catch (e) {
      } finally {
        //清除上一张图线
        if (this.lineIds.length > 0) {
          //清除之前的连线
          this.lineIds.forEach(s => {
            document.getElementById(s).remove();
          })
        }
      }
      //初始化数据
      this.lineIds = [];
      this.querySignObject = [];
    },
    //清除线
    cleanConnect() {
      if (this.lineIds.length > 0) {
        //清除之前的连线
        this.lineIds.forEach(s => {
          document.getElementById(s).remove();
        })
        this.$modal.msgSuccess("清除成功")
      } else {
        this.$message.error('未连线');
      }
      this.lineIds = [];
    },
    //清除点
    cleanSgin() {
      try {
        //清除之前全部标签
        this.querySignObject.forEach(s => {
          document.getElementById(s.id).remove();
        })
        //清除之前的连线
        this.lineIds.forEach(s => {
          document.getElementById(s).remove();
        })
        this.lineIds = [];
        this.$modal.msgSuccess("清除成功");
      } catch (e) {
        this.$message.error('暂无标点');
      }
    },
    //标点任务关闭时触发
    handleClose(done) {
      if (this.punctuationTaskLoading) {
        return;
      }
      this.$confirm('确定保存此标点任务吗？')
        .then(_ => {
          this.punctuationTaskLoading = true;
          this.timer = setTimeout(() => {
            done();
            // 动画关闭需要一定的时间
            setTimeout(() => {
              this.punctuationTaskLoading = false;
            }, 200);
          }, 200);
        })
        .catch(_ => {
        });
    },
    //判断存在那个象限
    connect(x1, y1, x2, y2) {
      //转换类型
      x1 = Number(x1);
      y1 = Number(y1);
      x2 = Number(x2);
      y2 = Number(y2);
      // 获得人物中心和鼠标坐标连线，与y轴正半轴之间的夹角
      let x = x1 - x2;
      let y = y1 - y2;
      let z = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
      let cos = y / z;
      let radina = Math.acos(cos); // 用反三角函数求弧度
      let angle = 180 / (Math.PI / radina); // 将弧度转换成角度
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
    getUuid() {
      return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        let r = (Math.random() * 16) | 0,
          v = c === 'x' ? r : (r & 0x3) | 0x8;
        return v.toString(16);
      });
    },
    //标点任务弹出框取消按钮事件
    cancelForm() {
      this.punctuationTaskLoading = false;
      this.addSignTaskShow = false;
      clearTimeout(this.timer);
    }
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
  width: 300px;
  height: 240px;
  float: left;
  margin: 35px 100px 50px 30px;
}

.el-row {
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
}

.el-row .el-card {
  min-width: 100%;
  height: 100%;
  margin-right: 20px;
  transition: all .5s;
}

.demo-drawer__footer {
  margin-top: 90%;
  margin-left: 5%;
}
</style>
