<template>
  <div class="app-container">
    <el-form label-width="68px" :inline="true">
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
    </el-form>
    <el-row :gutter="20">
      <el-col :span="3">
        <el-button type="danger"
                   plain
                   size="mini"
                   icon="el-icon-delete"
                   @click="clearImage">删除图片
        </el-button>
      </el-col>
      <el-col :span="3">
        <el-button type="danger"
                   plain
                   size="mini"
                   icon="el-icon-delete"
                   @click="clearSign">清空所有元素
        </el-button>
      </el-col>
      <el-col :span="2" v-if="signImageUrl != ''">
        <div>
          <el-button type="success"
                     plain
                     size="mini"
                     icon="el-icon-upload2"
                     @click="addImageAndSgin">上传
          </el-button>
        </div>
      </el-col>
      <el-col :span="3" v-if="signImageUrl == ''">
        <div>
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
                       size="mini"
                       icon="el-icon-circle-plus-outline"
                       type="primary">选择线路图</el-button>
          </el-upload>
        </div>
      </el-col>
    </el-row>
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
  </div>
</template>

<script>
import {
  listInformation,
  getInformation,
  delInformation,
  addInformation,
  updateInformation
} from "@/api/map/information";
import {uploadAvatar} from "@/api/system/user";
import store from "@/store";
import axios from 'axios'
import {getToken} from "@/utils/auth";

export default {
  name: "MapAdd",
  data() {
    return {
      visible:true,
      imgId: '',
      imgUrl: require("@/assets/images/upoload.jpg"),//默认本地上传图片路径
      signImageWidth: 0, //线路图宽
      signImageHeight: 0, //线路图高
      signWidth: 35, //设置标记点宽度
      signHeight: 35, //设置标记点高度
      signId: null,
      sign: [], //标点div信息
      signIds: [], //标点id集合
      signXYArray: [], //标点xy位置的对象集合
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
      deleteOneSignView:false, //右击删除标记
      deleteSignId:null,//右击删除是或其标识点id
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
      let textNode = document.createTextNode(this.signXYArray.length);
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
        div.style.borderBottom = '10px solid '+this.signColor;
        div.style.borderTop = '10px solid transparent';
        div.style.borderRight = '10px solid transparent';
        div.style.borderLeft = '10px solid transparent';
        let shape = 3
      }
      let that = this;
      //添加标点左击事件
      div.onclick = function () {
        that.signClick(that, x, y, this.signId)
      }
      //添加标点右击事件
      div.oncontextmenu = function (e) {
        e.preventDefault();
        that.deleteOneSign(that, x, y, this.signId)
      }
      //生成标点
      document.getElementById(divName).appendChild(div)
      //创建标点对象
      var signObject = {
        id: this.signId, //标点id
        imageId: this.imgId, //图片id
        relativeToPictureX: x, //标点相对于图片的x轴距离
        relativeToPictureY: y, //标点相对于图片的y轴距离
        signWidth: this.signWidth, //标点宽度
        signHeight: this.signHeight, //标点高度
        punctuationElement: div, //标点元素
        signContent: this.signXYArray.length,//标点内容
        signShape: shape,//标点形状 1正方形 2圆形 3三角形
        signColor: this.signColor,
        sort: this.signObject.length+1 //设置排序
      }
      //将标点对象存入集合
      this.signObject.push(signObject);
      //统计标点信息
      this.sign.push(div)
      //统计标点id
      this.signIds.push(this.signId)
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
      //设置需要删除的标点id
      that.deleteSignId = signId;
      that.deleteOneSignView = true
      that.leftClickDialogBox = false;
      x = x + this.signHeight + 'px'
      y = y  + 'px'
      //设置消息框位置
      document.getElementById('deleteOneSign').setAttribute('style', 'left: ' + x + ';top:' + y + ';position: absolute;')
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
      console.log(imageObject)
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
        that.signImageUrl = e.target.result;
      }
      //设置图片id
      this.imgId = this.getUuid()

    },
    //清除全部标记
    clearSign() {
      this.signIds.forEach(s => {
        document.getElementById(s).remove();
      })
      this.sign = []; //清除标点div信息
      this.signIds = []; //清除标点id
      this.signXYArray = []; //清除标点在图片上的xy
      this.signObject = []; //清除标点对象集合
    },
    //清除图片1000
    clearImage() {
      this.signImageUrl = ''; //删除图片url
      this.file = null; //清除线路图文件
      this.imgId = '' //清除图片id
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
    //删除标点
    deleteOneSignAndObject(){
      //
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
    //将所有点连在一起
    connectSgin() {
      console.log(this.signXYArray)
      this.signXYArray.map((dot, index) => {
        // 最后一个点没有连线
        if (!dot[index + 1]) return;
        1000
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

