<style scoped>
.main {
  display: flex;
}
.sidebarTree {
  min-width: 250px;
  margin-top: 20px;
  border: 1px solid lightgray;
  z-index: 0;
  height: fit-content;
}
.resourcePanel {
  flex: 1;
}

.btnPanel {
  flex: 1;
}
.btnPanel button {
  margin-left: 20px;
}
.searchPanel {
  display: flex;
  float: right;
}
.ivu-table td,
.ivu-table th {
  height: 40px !important;
}
</style>

<template>
  <Row>
    <Col span="23" offset="1">
    <div style="position:relative">
      <h2 style="margin-top: 20px;">Here are public resources shared by users. We will NEVER make your resources public
        until you share it.</h2>
      <Button type="info" style="position:absolute; right:65px;top:0px;" @click="showUploadFileModal"
        title="Upload resource">
        <Icon type="md-cloud-upload" size="20" />
      </Button>
    </div>

    <div class="main" :style="{height:contentHeight}">
      <div class="sidebarTree">
        <Menu theme="light" active-name="all" width="auto" @on-select="onMenuSelect">
          <MenuGroup title="File type">
            <MenuItem name="all">
            <Icon type="ios-list-box-outline" />All
            </MenuItem>
            <MenuItem name="image">
            <Icon type="md-image" />Images
            </MenuItem>
            <MenuItem name="video">
            <Icon type="md-videocam" />Videos
            </MenuItem>
            <MenuItem name="data">
            <Icon type="md-analytics" />Data
            </MenuItem>
            <MenuItem name="paper">
            <Icon type="md-paper" />Papers
            </MenuItem>
            <MenuItem name="document">
            <Icon type="md-document" />Documents
            </MenuItem>
            <MenuItem name="model">
            <Icon type="logo-dropbox" />Models
            </MenuItem>
            <MenuItem name="others">
            <Icon type="logo-xbox" />Others
            </MenuItem>
          </MenuGroup>
        </Menu>
      </div>
      <div class="resourcePanel" style="margin-left:-20px">
        <div class="resourcePanel" style="margin-top: 20px">
          <Row style="height:100%; overflow-y:auto">
            <template v-if="$store.getters.userState">
              <Col span="22" offset="1">
              <Table :columns="resourceColumn" :data="showList" border>
                <template slot-scope="{ row, index }" slot="action" v-show="showList.length>0">
                  <a :href="showList[index].pathURL" :download="showList[index].name" title="Download">
                    <Icon type="md-download" :size="20" color="yellowgreen" />
                  </a>
                  <a @click="show(index)" style="margin-left: 10px" title="Preview">
                    <Icon type="md-eye" :size="20" color="orange" />
                  </a>
                </template>
              </Table>
              </Col>
            </template>
            <template v-else>
              <Col span="22" offset="1">
              <Table :columns="resourceColumn" :data="showList" border>
                <template slot-scope="{ row, index }" slot="action" v-show="showList.length>0">
                  <a title="Please download after login">
                    <Icon type="md-download" :size="20" color="gray" />
                  </a>
                  <a style="margin-left: 10px" title="Please preview after login">
                    <Icon type="md-eye" :size="20" color="gray" />
                  </a>
                </template>
              </Table>
              </Col>
            </template>
          </Row>
          <div style="display:flex;justify-content:center">
            <Page :total="dataCount" :page-size="pageSize" show-total @on-change="changepage" show-elevator
              style="position: absolute;top:600px" />
          </div>
        </div>
      </div>
    </div>
    </Col>
    <Modal v-model="uploadModal" title="Upload resource" @on-ok="submitFile()" ok-text="Submit" cancel-text="Cancel"
      :mask-closable="false" width="600px">
      <div style="display:flex;text-align:center;align-items:center;justify-content:center">
        <!-- 这里定义上传的几种资源类型供用户选择 -->
        <span style="width:20%">Type</span>
        <RadioGroup v-model="fileType" style="width:80%">
          <Radio label="data">Data</Radio>
          <Radio label="image">Images</Radio>
          <Radio label="video">Videos</Radio>
          <Radio label="paper">Papers</Radio>
          <Radio label="document">Documents</Radio>
          <Radio label="model">Models</Radio>
          <Radio label="others">Others</Radio>
        </RadioGroup>
        <!-- 结束 -->
      </div>
      <br>
      <div style="display:flex;text-align:center;align-items:center;justify-content:center">
        <span style="width:20%">Description</span>
        <Input type="textarea" :rows="2" v-model="fileDescription" />
      </div>
      <br>
      <input type="file" @change="getFile($event)" style="margin-left:20%" multiple="multiple">
    </Modal>
    <!-- 上传文件按钮的模态框 -->
    <Modal v-model="uploadFileModal" title="Upload file" width="800px" :mask-closable="false"
      @on-ok="filesUpload('fileUploadForm')" ok-text="Submit" cancel-text="Cancel">
      <div>
        <Form ref="fileUploadForm" :model="fileUploadForm" :rules="fileUploadFormRuleValidate" :label-width="100">
          <FormItem label="Privacy" prop="privacy">
            <RadioGroup v-model="fileUploadForm.privacy" style="width:80%">
              <Radio label="private">Private</Radio>
              <Radio label="public">Public</Radio>
            </RadioGroup>
          </FormItem>
          <FormItem label="Type" prop="type">
            <RadioGroup v-model="fileUploadForm.type">
              <Radio label="data">Data</Radio>
              <Radio label="image">Images</Radio>
              <Radio label="video">Videos</Radio>
              <Radio label="paper">Papers</Radio>
              <Radio label="document">Documents</Radio>
              <Radio label="model">Models</Radio>
              <Radio label="others">Others</Radio>
            </RadioGroup>
          </FormItem>
          <FormItem label="Description" prop="description">
            <Input type="textarea" :rows="4" v-model="fileUploadForm.description"
              placeholder="Enter file description" />
          </FormItem>
        </Form>
      </div>
      <Upload :max-size="1024*1024" multiple type="drag" :before-upload="gatherFile" action="-">
        <div style="padding: 20px 0">
          <Icon type="ios-cloud-upload" size="52" style="color: #3399ff"></Icon>
          <p>
            Click or drag files here to upload(The file size must control in
            <span style="color:red">1GB</span>)
          </p>
        </div>
      </Upload>

      <div style="padding:0 10px 0 10px">
        <ul v-for="(list,index) in file" :key="index">
          <li style="display:flex">
            File name:
            <span style="font-size:10px;margin: 0 5px 0 5px" :title="list.name">{{ list.name }}</span>
            (Size:
            <span v-if="list.size<(1024*1024)"
              style="font-size:10px;margin-right:10px">{{(list.size/1024).toFixed(2)}}KB)</span>
            <span v-else style="font-size:10px;margin-right:10px">{{(list.size/1024/1024).toFixed(2)}}MB)</span>
            <Icon type="ios-close" size="20" @click="delFileList(index)"
              style="display:flex;justify-content:flex-end;cursor:pointer" title="Cancel"></Icon>
          </li>
        </ul>
      </div>
      <!-- <h6 style="text-align:center;color:red">The file's size must control smaller than 1 GB.</h6> -->
    </Modal>
  </Row>
</template>
<script>
export default {
  data() {
    return {
      uploadFileModal: false,
      fileUploadForm: {
        type: "data",
        privacy: "private",
        description: ""
      },
      fileUploadFormRuleValidate: {
        type: [
          {
            required: true,
            message: "The type cannot be empty",
            trigger: "blur"
          }
        ],
        privacy: [
          {
            required: true,
            message: "The privacy cannot be empty",
            trigger: "blur"
          }
        ],
        description: [
          {
            required: true,
            type: "string",
            message: "Please inpput file description",
            trigger: "blur"
          }
        ]
      },
      // 显示进度条的模态框
      progressModalShow: false,
      // 文件上传的进度
      uploadProgress: 0,
      // 上传的文件数组（支持多文件）
      file: [],
      // 侧边栏的颜色主题
      resourceColumn: [
        {
          type: "index",
          maxWidth: 50,
          align: "center"
        },
        {
          title: "Name",
          key: "name",
          minWidth: 10,
          tooltip: true,
          sortable: true
        },
        {
          title: "Type",
          key: "type",
          width: 100,
          sortable: true
        },
        {
          title: "Size",
          key: "fileSize",
          width: 100,
          sortable: true
        },
        {
          title: "Description",
          key: "description",
          minWidth: 30,
          tooltip: true
        },
        {
          title: "Provider",
          key: "uploaderName",
          width: 150,
          tooltip: true,
          align: "center"
        },
        {
          title: "Upload time",
          key: "uploadTime",
          width: 150,
          sortable: true
        },
        {
          title: "Action",
          slot: "action",
          width: 125,
          align: "center"
        }
      ],
      allResourceList: [],
      allSelectedList: [],
      showList: [],
      dataCount: 0,
      pageSize: 10,
      // 上传文件的模态框
      uploadModal: false,
      // file: "",
      fileDescription: "",
      fileType: "data",
      contentHeight: "",
      panel: null
    };
  },
  mounted() {
    this.initLayout();
    this.readResource();
  },
  beforeRouteLeave(to, from, next) {
    if (this.panel != null) {
      this.panel.close();
    }
    next();
  },
  methods: {
    showUploadFileModal() {
      //* 先登录！
      if (this.$store.getters.userState) {
        this.uploadFileModal = true;
      } else {
        this.$Message.error("you must have an account before you upload files");
        this.$router.push({ name: "Login" });
      }
    },
    filesUpload(form) {
      this.uploadProgress = 0;
      this.$refs[form].validate(valid => {
        if (valid) {
          let that = this;
          if (that.file.length != 0) {
            var formData = new FormData();
            var total = 0;
            for (var i = 0; i < that.file.length; i++) {
              if (that.file[i].fileSize < Math.pow(1024, 2)) {
                formData.append("file", that.file[i]); // 文件对象
              } else {
              }
              total += that.file[i].fileSize;
            }
            if (total < Math.pow(1024, 2)) {
              let userInfo = JSON.parse(sessionStorage.getItem("userInfo"));
              formData.append("description", this.fileUploadForm.description);
              formData.append("type", this.fileUploadForm.type);
              formData.append(
                "uploaderId",
                this.$store.getters.userInfo.userId
              );
              // formData.append("belong", this.currentProjectDetail.title);
              // let scopeObject = {
              //   projectId: this.currentProjectDetail.projectId,
              //   subprojectId: "",
              //   moduleId: ""
              // };
              // formData.append("scope", JSON.stringify(scopeObject));
              formData.append("privacy", this.fileUploadForm.privacy);
              this.progressModalShow = true;
              this.axios({
                url: "/GeoProblemSolving_Backend/resource/upload",
                method: "post",
                onUploadProgress: progressEvent => {
                  this.uploadProgress =
                    ((progressEvent.loaded / progressEvent.total) * 100) | 0;
                },
                data: formData
              })
                .then(res => {
                  if (res.data == "Offline") {
                    this.$store.commit("userLogout");
                    this.$router.push({ name: "Login" });
                  } else if (res.data) {
                    this.$Notice.open({
                      title: "Success",
                      desc: "File uploaded successfully."
                    });
                  } else {
                    this.$Notice.error({
                      title: "Error",
                      desc: "File uploaded fail."
                    });
                  }
                  //这里重新获取一次该项目下的全部资源
                  // this.addUploadEvent(this.currentProjectDetail.projectId);
                  // this.getAllResource();
                  this.readResource();
                  this.file = [];
                  this.fileUploadForm.description = "";
                  this.fileUploadForm.privacy = "private";
                  this.fileUploadForm.type = "data";
                  // 创建一个函数根据pid去后台查询该项目下的资源
                  this.progressModalShow = false;
                  this.uploadProgress = 0;
                })
                .catch(err => {
                  this.progressModalShow = false;
                  this.uploadProgress = 0;
                });
            } else {
              this.$Message.error(
                "size over,all the file size must smaller than 1 GB one time!"
              );
            }
          }
        } else {
          this.$Message.warning("please check your form!");
        }
      });
    },
    addUploadEvent(scopeId) {
      let form = {};
      let description =
        this.$store.getters.userName +
        " uploaded a " +
        this.fileUploadForm.type +
        " file in " +
        " project called " +
        this.currentProjectDetail.title;
      form["description"] = description;
      form["scopeId"] = scopeId;
      form["eventType"] = "project";
      form["userId"] = this.$store.getters.userId;
      this.axios
        .post("/GeoProblemSolving/history/save", form)
        .then(res => {
          console.log(res.data);
        })
        .catch(err => {
          console.log(err.data);
        });
    },
    gatherFile(file) {
      let that = this;
      if (that.file.length >= 5) {
        if (this.fileCountTimer != null) {
          clearTimeout(this.fileCountTimer);
        }
        this.fileCountTimer = setTimeout(() => {
          this.$Message.info("you can upload 5 files one time!");
        }, 500);
      } else {
        that.file.push(file);
        that.file.map(element => {
          element["fileSize"] = Math.round((element.size / 1024) * 100) / 100;
        });
      }
      return false;
    },
    delFileList(index) {
      let that = this;
      that.file.splice(index, 1);
    },
    initLayout() {
      this.contentHeight = window.innerHeight - 180 + "px";
    },
    readResource() {
      this.allResourceList = [];
      this.showList = [];
      this.axios.get("/GeoProblemSolving_Backend/resource/allPublic").then(res => {
        if (res.data != "None") {
          var tempResourceList = res.data;
          tempResourceList.forEach(function (list) {
            var time = list.uploadTime;
            list.uploadTime = time;
          });
          this.dataCount = tempResourceList.length;
          this.$set(this, "allResourceList", tempResourceList);
          this.$set(this, "allSelectedList", tempResourceList);
          this.sliceList();
        }
      });
    },
    sliceList() {
      var tempResourceList = this.allSelectedList;
      if (this.dataCount < this.pageSize) {
        this.$set(this, "showList", tempResourceList);
      } else {
        this.$set(this, "showList", tempResourceList.slice(0, this.pageSize));
      }
    },
    onMenuSelect(name) {
      if (name == "all") {
        this.$set(this, "allSelectedList", this.allResourceList);
      } else {
        this.mapResourceList(name);
      }
      this.dataCount = this.allSelectedList.length;
      this.sliceList();
    },
    mapResourceList(name) {
      var newList = [];
      newList = this.allResourceList.filter(function (e) {
        if (e.type == name) {
          return e;
        }
      });
      this.$set(this, "allSelectedList", newList);
    },
    changepage(index) {
      var _start = (index - 1) * this.pageSize;
      var _end = index * this.pageSize;
      this.showList = this.allSelectedList.slice(_start, _end);
    },
    show(index) {
      let name = this.showList[index].name;
      if (/\.(doc|docx|xls|xlsx|csv|ppt|pptx|zip)$/.test(name.toLowerCase())) {
        if (this.panel != null) {
          this.panel.close();
        }
        var url =
          "http://172.21.212.72:8012/previewFile?url=" + 'http://' + this.$store.state.IP_Port +
          this.showList[index].pathURL;
        var toolURL =
          '<iframe src=' + url + ' style="width: 100%;height:100%"></iframe>';
        this.panel = jsPanel.create({
          headerControls: {
            smallify: "remove"
          },
          theme: "primary",
          footerToolbar: '<p style="height:5px"></p>',
          headerTitle: "Preview",
          contentSize: "800 600",
          content: toolURL,
          disableOnMaximized: true,
          dragit: {
            containment: 5
          },
          closeOnEscape: true,
        });
        $(".jsPanel-content").css("font-size", "0");
      }
      else if (/\.(mp4)$/.test(name.toLowerCase())) {
        if (this.panel != null) {
          this.panel.close();
        }
        var url =
          'http://' + this.$store.state.IP_Port + this.showList[index].pathURL;
        var toolURL =
          '<video src=' + url + ' style="width: 100%;height:100%" controls></video>';
        this.panel = jsPanel.create({
          headerControls: {
            smallify: "remove"
          },
          theme: "primary",
          footerToolbar: '<p style="height:10px"></p>',
          headerTitle: "Preview",
          contentSize: "800 600",
          content: toolURL,
          disableOnMaximized: true,
          dragit: {
            containment: 5
          },
          closeOnEscape: true
        });
        $(".jsPanel-content").css("font-size", "0");
      }
      else if (/\.(pdf|xml|json|md|gif|jpg|png)$/.test(name.toLowerCase())) {
        if (this.panel != null) {
          this.panel.close();
        }
        var url =
          'http://' + this.$store.state.IP_Port + this.showList[index].pathURL;
        var toolURL =
          '<iframe src=' + url + ' style="width: 100%;height:100%" controls></iframe>';
        this.panel = jsPanel.create({
          headerControls: {
            smallify: "remove"
          },
          theme: "primary",
          footerToolbar: '<p style="height:10px"></p>',
          headerTitle: "Preview",
          contentSize: "800 600",
          content: toolURL,
          disableOnMaximized: true,
          dragit: {
            containment: 5
          },
          closeOnEscape: true
        });
        $(".jsPanel-content").css("font-size", "0");
      }
      else {
        this.$Notice.error({
          title: "Open failed",
          desc: "Not supported file format."
        });
        return false;
      }
    }
  }
};
</script>
