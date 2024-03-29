<style scoped>
.folderContent {
  overflow-y: auto;
  padding: 5px;
  margin: 3px;
}
.resourceTitle {
  font-size: 18px;
  height: 20px;
  line-height: 20px;
}
.resourceBtnDiv {
  display: flex;
  align-items: center;
  height: 20px;
  padding: 5px;
}
.fileBtn {
  margin: 0px 3px;
}
.itemIcon {
  margin-right: 5px;
}
.fileItemName {
  width: 35%;
  margin-right: 5%;
  display: inline-block;
  text-overflow: ellipsis;
  white-space: nowrap;
  vertical-align: top;
  cursor: pointer;
  overflow: hidden;
}
.fileItemSize {
  width: 10%;
  margin-right: 5%;
  display: inline-block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  height: 16px;
}
.demo-spin-icon-load {
  animation: ani-demo-spin 1s linear infinite;
}
@keyframes ani-demo-spin {
  from {
    transform: rotate(0deg);
  }
  50% {
    transform: rotate(180deg);
  }
  to {
    transform: rotate(360deg);
  }
}
.demo-spin-col {
  height: 100px;
  position: relative;
  /* border: 1px solid #eee; */
}
.personalFileLabel {
  width: 250px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.personalFileDes {
  display: inline-block;
  margin: 0 5px;
  width: 250px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.fileBtnHoverBlue:hover {
  background-color: #2db7f5;
  color: white;
}
.fileBtnHoverGreen:hover {
  background-color: #19be6b;
  color: white;
}
.fileBtnHoverOrange:hover {
  background-color: #ff9900;
  color: white;
}
.fileBtnHoverRed:hover {
  background-color: #ed4014;
  color: white;
}
.fileBtnHoverGray:hover {
  background-color: #808695;
  color: white;
}
</style>
<template>
  <div class="fileSpace">
    <Card :padding="1">
      <div slot="title" class="resourceTitle">
        <strong>Resources</strong>
      </div>
      <div slot="extra" class="resourceBtnDiv" v-show="role != 'Visitor'">
        <Tooltip content="Back" placement="bottom" class="fileBtn">
          <Button @click="backforeFolder" class="fileBtnHoverGray">
            <Icon type="md-arrow-round-back" size="20" />
          </Button>
        </Tooltip>
        <Tooltip content="New folder" placement="bottom" class="fileBtn">
          <Button @click="addFolderModalShow" class="fileBtnHoverGreen">
            <Icon type="ios-folder" size="20" />
          </Button>
        </Tooltip>
      </div>
      <div class="folderContent">
        <Card v-if="folderNameStack.length>0" :padding="5" dis-hover>
          <div style="display:flex;align-items:center">
            <div style="min-width:60px" v-show="currentFolder.files.length>0">
              <Checkbox
                :indeterminate="indeterminate"
                :value="checkAll"
                @click.prevent.native="handleCheckAll"
                v-show="currentFolder.files.length>0"
                style="align-items:center"
              >All files</Checkbox>
            </div>
            <div style="flex:1;margin-left:3px;">
              <Breadcrumb>
                <BreadcrumbItem>
                  <Icon type="md-folder" />
                </BreadcrumbItem>
                <BreadcrumbItem
                  v-for="(folderName,index) in folderNameStack"
                  :key="index"
                  v-if="index!=0"
                >{{folderName}}</BreadcrumbItem>
              </Breadcrumb>
            </div>
            <div style="align-items:flex-end">
              <Tooltip content="Download" placement="bottom" class="fileBtn">
                <Button
                  @click="downloadSelectFile"
                  v-show="currentFolder.files.length>0"
                  shape="circle"
                  icon="md-cloud-download"
                  class="fileBtnHoverGray"
                ></Button>
              </Tooltip>
              <Tooltip content="Upload files" placement="bottom" class="fileBtn">
                <Button
                  @click="uploadModalShow"
                  shape="circle"
                  icon="md-cloud-upload"
                  class="fileBtnHoverGreen"
                ></Button>
              </Tooltip>
              <Tooltip content="Share personal files" placement="left" class="fileBtn">
                <Button
                  @click="shareModalShow"
                  shape="circle"
                  icon="ios-copy"
                  class="fileBtnHoverOrange"
                ></Button>
              </Tooltip>
            </div>
          </div>
        </Card>
        <div v-if="currentFolder.folders.length>0 || currentFolder.files.length>0">
          <vue-scroll :ops="ops" :style="{height:contentHeight+'px'}">
            <Card v-for="(folder, index) in currentFolder.folders" :key="folder.index" :padding="5">
              <div>
                <Icon type="ios-folder-open" class="itemIcon" size="25" />
                <a
                  @click="enterFolder(folder.uid)"
                  class="fileItemName"
                  :title="folder.name"
                >{{folder.name}}</a>
                <div style="float:right">
                  <Button
                    @click="renameFolderModalShow(folder)"
                    class="fileBtnHoverBlue"
                    shape="circle"
                    icon="ios-create"
                    title="Rename"
                    size="small"
                    type="text"
                  ></Button>
                  <Button
                    @click="deleteFolder(folder)"
                    class="fileBtnHoverRed"
                    shape="circle"
                    icon="ios-trash"
                    title="Delete"
                    size="small"
                    style="margin-left:5px"
                    type="text"
                  ></Button>
                </div>
              </div>
            </Card>
            <CheckboxGroup v-model="chooseFilesArray" @on-change="checkAllGroupChange">
              <Card v-for="(file,index) in currentFolder.files" :key="file.index" :padding="5">
                <Checkbox :label="file.pathURL">&nbsp;</Checkbox>
                <Icon type="ios-document-outline" class="itemIcon" size="25" />
                <span
                  @click="getFileInfo(file)"
                  class="fileItemName"
                  :title="file.name"
                >{{file.name}}</span>
                <span class="fileItemSize">{{file.fileSize}}</span>
                <span style="width:20%;margin-right:5%">{{file.uploadTime.substring(0,10)}}</span>
                <div style="float:right">
                  <Button
                    @click="filePreview(file)"
                    shape="circle"
                    icon="md-eye"
                    title="Preview"
                    size="small"
                    class="fileBtnHoverGreen"
                    type="text"
                  ></Button>
                  <Button
                    @click="fileDownload(file)"
                    shape="circle"
                    icon="ios-cloud-download"
                    title="Download"
                    size="small"
                    class="fileBtnHoverGray"
                    type="text"
                  ></Button>
                  <Button
                    @click="fileRenameModalShow(file)"
                    shape="circle"
                    icon="md-create"
                    title="Rename"
                    size="small"
                    class="fileBtnHoverBlue"
                    type="text"
                  ></Button>
                  <Button
                    @click="fileDelete(file)"
                    shape="circle"
                    icon="ios-trash"
                    title="Remove"
                    size="small"
                    class="fileBtnHoverRed"
                    type="text"
                  ></Button>
                </div>
              </Card>
            </CheckboxGroup>
          </vue-scroll>
        </div>
        <div v-else style="text-align:center">
          <div style="color:lightgray;font-size:2em; font-weight:bold">No file or folder</div>
        </div>
        <div>
          <Row>
            <Col class="demo-spin-col" span="8" offset="8" v-show="spinAnimate==true">
              <Spin fix v-show="spinAnimate==true">
                <Icon type="ios-loading" size="40" class="demo-spin-icon-load"></Icon>
                <div>Loading</div>
              </Spin>
            </Col>
          </Row>
        </div>
      </div>
    </Card>
    <Modal v-model="renameFolderModal" title="Rename folder" ok-text="Assure" cancel-text="Cancel">
      <Form
        ref="renameValidate"
        :model="renameValidate"
        :rules="renameRuleValidate"
        :label-width="80"
      >
        <FormItem label="New name" prop="newName">
          <Input
            v-model="renameValidate.newName"
            :rows="4"
            placeholder="Enter the name for folder..."
          />
        </FormItem>
      </Form>
      <div slot="footer">
        <Button @click="renameFolderModal=false">Cancel</Button>
        <Button type="success" @click="renameFolder('renameValidate')">Rename</Button>
      </div>
    </Modal>
    <Modal v-model="newFolderModal" title="New folder">
      <Form
        ref="newValidate"
        :model="newValidate"
        :rules="newRuleValidate"
        :label-width="80"
        @submit.native.prevent
      >
        <FormItem label="Set name" prop="setName">
          <Input
            v-model="newValidate.setName"
            :rows="4"
            placeholder="Enter the name for folder..."
          />
        </FormItem>
      </Form>
      <div slot="footer">
        <Button @click="newFolderModal=false">Cancel</Button>
        <Button type="success" @click="addFolder('newValidate')">New</Button>
      </div>
    </Modal>
    <Modal v-model="uploadModal" title="Upload file" width="600">
      <Form
        ref="uploadValidate"
        :model="uploadValidate"
        :rules="uploadRuleValidate"
        :label-width="100"
        label-position="left"
      >
        <FormItem label="Privacy" prop="privacy">
          <RadioGroup v-model="uploadValidate.privacy" style="width:80%">
            <Radio label="private">Private</Radio>
            <Radio label="public">Public</Radio>
          </RadioGroup>
        </FormItem>
        <FormItem label="Type" prop="type">
          <RadioGroup v-model="uploadValidate.type">
            <Radio label="data"></Radio>
            <Radio label="paper"></Radio>
            <Radio label="document"></Radio>
            <Radio label="model"></Radio>
            <Radio label="image"></Radio>
            <Radio label="video"></Radio>
            <Radio label="others"></Radio>
          </RadioGroup>
        </FormItem>
        <FormItem label="Description" prop="description">
          <Input type="textarea" :rows="4" v-model="uploadValidate.description" />
        </FormItem>
      </Form>
      <Upload :max-size="1024*1024" multiple type="drag" :before-upload="gatherFile" action="-">
        <div style="padding: 20px 0">
          <Icon type="ios-cloud-upload" size="52" style="color: #3399ff"></Icon>
          <p>
            Click or drag files here to upload (The file size must control in
            <span
              style="color:red"
            >1GB</span>)
          </p>
        </div>
      </Upload>
      <div style="padding:0 10px 0 10px;max-height:200px;overflow-y:auto">
        <ul v-for="(list,index) in toUploadFiles" :key="index">
          <li style="display:flex">
            File name:
            <span
              style="font-size:10px;margin: 0 5px 0 5px"
            >{{ list.name }} ( {{list.fileSize}} )</span>
            <Icon
              type="ios-close"
              size="20"
              @click="delFileList(index)"
              style="display:flex;justify-content:flex-end;cursor:pointer"
            ></Icon>
          </li>
        </ul>
      </div>
      <div slot="footer">
        <Button @click="uploadModal=false">Cancel</Button>
        <Button type="success" @click="folderUpload('uploadValidate')">Upload</Button>
      </div>
    </Modal>
    <Modal
      v-model="progressModalShow"
      title="Upload Progress"
      :mask-closable="false"
      :closable="false"
    >
      <Progress :percent="uploadProgress"></Progress>
      <div slot="footer"></div>
    </Modal>
    <Modal v-model="fileInfoModal" title="File Info">
      <Table
        :columns="selectedFileColumns"
        :data="selectedFileData"
        stripe
        border
        :show-header="false"
      ></Table>
      <div slot="footer">
        <Button type="primary" @click="fileInfoModal=false">OK</Button>
      </div>
    </Modal>
    <Modal
      v-model="shareModal"
      title="Share file from personal center"
      width="600"
      :mask-closable="false"
    >
      <div>
        <vue-scroll :ops="ops" style="height:300px;">
          <CheckboxGroup v-model="selectedFilesToShare">
            <Card dis-hover v-for="file in userResourceList" :key="file.index">
              <Checkbox
                :label="file.resourceId"
                class="personalFileLabel"
                :title="file.name"
                v-if="canBeShare(file.resourceId)"
              >
                <strong>{{file.name}}</strong>
              </Checkbox>
              <Checkbox
                :label="file.resourceId"
                class="personalFileLabel"
                :title="file.name"
                disabled
                v-else
              >
                <strong>{{file.name}}</strong>
              </Checkbox>
              <span
                class="personalFileDes"
                style="width:150px;"
                :title="file.description"
              >{{file.description}}</span>
              <span style="display: inline-block;vertical-align: top;">{{file.fileSize}}</span>
            </Card>
          </CheckboxGroup>
        </vue-scroll>
      </div>
      <div slot="footer" style="display: inline-block">
        <i-button type="primary" @click="shareFile()" style="float:right;">Submit</i-button>
        <i-button @click="closeshareModel()" style="float:right;margin-right: 15px;">Cancel</i-button>
      </div>
    </Modal>
    <Modal v-model="renameFileModal" title="Rename file" ok-text="Assure" cancel-text="Cancel">
      <Form
        ref="renameValidate"
        :model="renameValidate"
        :rules="renameRuleValidate"
        :label-width="80"
      >
        <FormItem label="New name" prop="newName">
          <Input
            v-model="renameValidate.newName"
            :rows="4"
            placeholder="Enter the name for folder..."
          />
        </FormItem>
      </Form>
      <div slot="footer">
        <Button @click="renameFileModal=false">Cancel</Button>
        <Button type="success" @click="renameFile('renameValidate')">Rename</Button>
      </div>
    </Modal>
  </div>
</template>
<script>
export default {
  props: ["rootFolderId", "role"],
  data() {
    return {
      currentFolder: {
        folders: [],
        files: []
      },
      folderNameStack: [],
      newFolderModal: false,
      setFolderName: "",
      newValidate: {
        setName: ""
      },
      newRuleValidate: {
        setName: [
          {
            required: true,
            message: "The name can't be null.",
            trigger: "blur"
          }
        ]
      },
      renameForeInfo: {},
      renameFolderModal: false,
      renameValidate: {
        newName: ""
      },
      renameRuleValidate: {
        newName: [
          {
            required: true,
            message: "The name can't be null.",
            trigger: "blur"
          }
        ]
      },
      uploadModal: false,
      uploadValidate: {
        privacy: "private",
        type: "data",
        description: ""
      },
      uploadRuleValidate: {
        privacy: [
          {
            required: true,
            message: "file privacy cannot be empty",
            trigger: "blur"
          }
        ],
        type: [
          {
            required: true,
            message: "file type cannot be empty",
            trigger: "blur"
          }
        ],
        description: [
          {
            required: true,
            message: "file description cannot be empty",
            trigger: "blur"
          }
        ]
      },
      toUploadFiles: [],
      fileCountTimer: null,
      progressModalShow: false,
      uploadProgress: 0,
      fileInfoModal: false,
      selectedFileColumns: [
        {
          title: "key",
          key: "key",
          minWidth: 10,
          width: 110
        },
        {
          title: "value",
          key: "value"
        }
      ],
      selectedFileData: [],
      panel: null,
      // 单选选中的名称数组
      chooseFilesArray: [],
      // loading动画
      spinAnimate: false,
      // 关于单选多选的按钮
      indeterminate: true,
      checkAll: false,
      shareModal: false,
      renameFileModal: false,
      userResourceList: [],
      selectedFilesToShare: [],
      ops: {
        bar: {
          background: "#808695"
        }
      },
      contentHeight: 100
    };
  },
  mounted() {
    this.initSize();
    this.enterFolder(this.rootFolderId);
    window.addEventListener("resize", this.initSize);
  },
  beforeDestroy: function() {
    window.removeEventListener("resize", this.initSize);
  },
  methods: {
    initSize() {
      this.contentHeight = window.innerHeight - 350;
    },
    enterFolder(currentFolderId) {
      this.chooseFilesArray = [];
      this.checkAll = false;
      this.indeterminate = false;
      this.changeFolder(currentFolderId, "enter");
    },
    backforeFolder() {
      this.chooseFilesArray = [];
      this.checkAll = false;
      this.indeterminate = false;
      if (this.currentFolder.parentId != "") {
        this.changeFolder(this.currentFolder.parentId, "back");
      } else {
        this.$Message.warning("This is the root folder.");
      }
    },
    changeFolder(folderId, type) {
      this.axios
        .get("/GeoProblemSolving/folder/inquiry" + "?folderId=" + folderId)
        .then(res => {
          if (res.data == "Offline") {
            this.$store.commit("userLogout");
            this.$router.push({ name: "Login" });
          } else if (res.data != "Fail") {
            var folderInfo = res.data;
            this.currentFolder = res.data;
            if (type == "enter") {
              this.folderNameStack.push(folderInfo.folderName);
            } else if (type == "back") {
              this.folderNameStack.pop();
            }
          } else {
            this.$Message.warning("Get folder info fail.");
          }
        })
        .catch(err => {
          this.$Message.warning("Get folder info fail.");
        });
    },
    getFileInfo(file) {
      this.selectedFileData = [
        {
          key: "File name",
          value: file.name
        },
        {
          key: "Description",
          value: file.description
        },
        {
          key: "Type",
          value: file.type
        },
        {
          key: "File size",
          value: file.fileSize
        },
        {
          key: "Uploader",
          value: file.uploaderName
        },
        {
          key: "Upload Time",
          value: file.uploadTime
        }
      ];
      this.fileInfoModal = true;
    },
    addFolderModalShow() {
      this.newValidate.setName = "";
      this.newFolderModal = true;
    },
    addFolder(name) {
      this.$refs[name].validate(valid => {
        if (valid) {
          var parentId = this.currentFolder.folderId;
          var newFolderName = this.newValidate.setName;
          this.axios
            .post(
              "/GeoProblemSolving/folder/new" +
                "?folderName=" +
                newFolderName +
                "&parentId=" +
                parentId
            )
            .then(res => {
              if (res.data == "Offline") {
                this.$store.commit("userLogout");
                this.$router.push({ name: "Login" });
              } else if (res.data != "Fail") {
                this.currentFolder.folders.push(res.data);
                this.newFolderModal = false;
              } else {
                this.$Message.warning("New folder fail.");
              }
            })
            .catch(err => {
              this.$Message.warning("New folder fail.");
            });
        }
      });
    },
    deleteFolder(folder) {
      if (confirm("Are you sure to delete this folder?")) {
        var folderId = folder.uid;
        var parentId = this.currentFolder.folderId;
        this.axios
          .get(
            "/GeoProblemSolving/folder/removeFolder" +
              "?folderId=" +
              folderId +
              "&parentId=" +
              parentId
          )
          .then(res => {
            if (res.data == "Offline") {
              this.$store.commit("userLogout");
              this.$router.push({ name: "Login" });
            } else if (res.data != "Fail") {
              for (var i = 0; i < this.currentFolder.folders.length; i++) {
                if (this.currentFolder.folders[i].uid == folderId) {
                  this.currentFolder.folders.splice(i, 1);
                  break;
                }
              }
            } else {
              this.$Message.warning("Delete folder fail.");
            }
          })
          .catch(err => {
            this.$Message.warning("Delete folder fail.");
          });
      }
    },
    renameFolderModalShow(folder) {
      this.renameForeInfo = folder;
      this.renameValidate.newName = "";
      this.renameFolderModal = true;
    },
    renameFolder(name) {
      this.$refs[name].validate(valid => {
        if (valid) {
          var parentId = this.currentFolder.folderId;
          var folderId = this.renameForeInfo.uid;
          var newName = this.renameValidate.newName;
          this.axios
            .get(
              "/GeoProblemSolving/folder/renameFolder" +
                "?newName=" +
                newName +
                "&folderId=" +
                folderId +
                "&parentId=" +
                parentId
            )
            .then(res => {
              if (res.data == "Offline") {
                this.$store.commit("userLogout");
                this.$router.push({ name: "Login" });
              } else if (res.data != "Fail") {
                var newNameFolder = {
                  uid: folderId,
                  name: newName
                };
                for (var i = 0; i < this.currentFolder.folders.length; i++) {
                  if (this.currentFolder.folders[i].uid == folderId) {
                    this.currentFolder.folders.splice(i, 1, newNameFolder);
                    break;
                  }
                }
              } else {
                this.$Message.warning("Rename fail.");
              }
            })
            .catch(err => {
              this.$Message.warning("Rename fail.");
            });
          this.renameFolderModal = false;
        }
      });
    },
    uploadModalShow() {
      this.uploadValidate = {
        privacy: "private",
        type: "data",
        description: ""
      };
      this.toUploadFiles = [];
      this.uploadModal = true;
    },
    gatherFile(file) {
      let that = this;
      if (that.toUploadFiles.length >= 5) {
        if (this.fileCountTimer != null) {
          clearTimeout(this.fileCountTimer);
        }
        this.fileCountTimer = setTimeout(() => {
          this.$Message.info("最多只能上传5个文件");
        }, 500);
      } else {
        var fileSize = file.size;
        if (fileSize < 1024) {
          file.fileSize = fileSize + "b";
        } else if (fileSize < 1024 * 1024) {
          file.fileSize = Math.round((fileSize / 1024) * 100) / 100 + "Kb";
        } else {
          file.fileSize =
            Math.round((fileSize / (1024 * 1024)) * 100) / 100 + "Mb";
        }
        that.toUploadFiles.push(file);
      }
      return false;
    },
    delFileList(index) {
      this.toUploadFiles.splice(index, 1);
    },
    folderUpload(name) {
      this.$refs[name].validate(valid => {
        if (valid) {
          var uploadFiles = this.toUploadFiles;
          if (uploadFiles.length > 0) {
            this.uploadModal = false;
            var formData = new FormData();
            for (var i = 0; i < uploadFiles.length; i++) {
              formData.append("file", uploadFiles[i]);
            }
            formData.append("description", this.uploadValidate.description);
            formData.append("type", this.uploadValidate.type);
            formData.append("uploaderId", this.$store.getters.userInfo.userId);
            formData.append("privacy", this.uploadValidate.privacy);
            formData.append("folderId", this.currentFolder.folderId);
            this.progressModalShow = true;
            this.axios({
              url: "/GeoProblemSolving/folder/uploadToFolder",
              method: "post",
              onUploadProgress: progressEvent => {
                this.uploadProgress =
                  ((progressEvent.loaded / progressEvent.total) * 100) | 0;
              },
              data: formData
            })
              .then(res => {
                if (res.data != "Fail") {
                  var uploadedList = res.data.uploaded;
                  var failedList = res.data.failed;
                  var sizeOverList = res.data.sizeOver;
                  for (var i = 0; i < uploadedList.length; i++) {
                    this.currentFolder.files.push(uploadedList[i]);
                  }
                  if (sizeOverList.length > 0) {
                    this.$Notice.warning({
                      title: "Files too large.",
                      render: h => {
                        return h("span", sizeOverList.join(";"));
                      }
                    });
                  }
                  if (failedList.length > 0) {
                    this.$Notice.error({
                      title: "Upload fail.",
                      render: h => {
                        return h("span", failedList.join(";"));
                      }
                    });
                  }
                } else {
                  this.$Message.warning("Upload fail.");
                }
                this.progressModalShow = false;
                this.uploadProgress = 0;
              })
              .catch(err => {
                this.progressModalShow = false;
                this.$Message.warning("Upload fail.");
                this.uploadProgress = 0;
              });
          } else {
            this.$Message.warning("Upload file is null.");
          }
        }
      });
    },
    filePreview(fileInfo) {
      this.$Message.info("under construction...");
    },
    fileDelete(fileInfo) {
      if (confirm("Are you sure to delete this file?")) {
        var folderId = this.currentFolder.folderId;
        var fileId = fileInfo.resourceId;
        this.axios
          .get(
            "/GeoProblemSolving/folder/removeFile" +
              "?folderId=" +
              folderId +
              "&fileId=" +
              fileId
          )
          .then(res => {
            if (res.data == "Offline") {
              this.$store.commit("userLogout");
              this.$router.push({ name: "Login" });
            } else if (res.data != "Fail") {
              for (var i = 0; i < this.currentFolder.files.length; i++) {
                if (this.currentFolder.files[i].resourceId == fileId) {
                  this.currentFolder.files.splice(i, 1);
                  break;
                }
              }
            } else {
              this.$Message.warning("Delete file fail.");
            }
          })
          .catch(err => {
            this.$Message.warning("Delete file fail.");
          });
      }
    },
    download(blobUrl) {
      const a = document.createElement("a");
      a.style.display = "none";
      a.download = "package.zip";
      a.href = blobUrl;
      a.click();
      document.body.removeChild(a);
    },
    handleCheckAll() {
      if (this.indeterminate) {
        this.checkAll = false;
      } else {
        this.checkAll = !this.checkAll;
      }
      this.indeterminate = false;
      if (this.checkAll) {
        this.currentFolder.files.forEach(item => {
          this.chooseFilesArray.push(item["pathURL"]);
        });
      } else {
        this.chooseFilesArray = [];
      }
    },
    checkAllGroupChange(data) {
      if (data.length == this.currentFolder.files.length) {
        this.indeterminate = false;
        this.checkAll = true;
      } else if (data.length > 0) {
        this.indeterminate = true;
        this.checkAll = false;
      } else {
        this.indeterminate = false;
        this.checkAll = false;
      }
    },
    downloadSelectFile() {
      let choosefileUrls = this.chooseFilesArray.toString();
      if (choosefileUrls != "") {
        this.spinAnimate = true;
        this.axios({
          method: "post",
          url:
            "/GeoProblemSolving/resource/packageZIP?fileURLs=" + choosefileUrls,
          responseType: "blob"
        })
          .then(res => {
            if (res.status == 200) {
              this.spinAnimate = false;
              const blobUrl = window.URL.createObjectURL(res.data);
              if (blobUrl != "") {
                this.download(blobUrl);
              }
            }
          })
          .catch(err => {});
      } else {
        alert("you don't choose any file!");
      }
    },
    shareModalShow() {
      this.axios
        .get(
          "/GeoProblemSolving/resource/inquiry" +
            "?key=uploaderId" +
            "&value=" +
            this.$store.getters.userId
        )
        .then(res => {
          if (res.data == "Offline") {
            this.$store.commit("userLogout");
            this.$router.push({ name: "Login" });
          } else if (res.data != "None" && res.data != "Fail") {
            this.userResourceList = res.data;
            this.shareModal = true;
          } else if (res.data == "None") {
            this.userResourceList = [];
          }
        })
        .catch(err => {
          console.log(err.data);
        });
    },
    closeshareModel() {
      this.shareModal = false;
    },
    shareFile() {
      var addFileList = this.selectedFilesToShare;
      var addFileListStr = addFileList.toString();
      this.axios
        .get(
          "/GeoProblemSolving/folder/shareToFolder" +
            "?addFileList=" +
            addFileListStr +
            "&folderId=" +
            this.currentFolder.folderId
        )
        .then(res => {
          this.shareModal = false;
          if (res.data == "Offline") {
            this.$store.commit("userLogout");
            this.$router.push({ name: "Login" });
          } else if (res.data != "Fail") {
            var addFileInfoList = this.userResourceList.filter(file => {
              for (var i = 0; i < addFileList.length; i++) {
                if (file.resourceId == addFileList[i]) {
                  return true;
                }
              }
              return false;
            });
            var foreFiles = Object.assign([], this.currentFolder.files);
            this.currentFolder.files = foreFiles.concat(addFileInfoList);
            this.selectedFilesToShare = [];
            this.shareModal = false;
          } else {
            console.log(res.data);
          }
        })
        .catch(err => {
          console.log(err.data);
        });
    },
    fileDownload(fileInfo) {
      window.open(fileInfo.pathURL);
    },
    fileRenameModalShow(fileInfo) {
      this.renameForeInfo = fileInfo;
      this.renameValidate.newName = "";
      this.renameFileModal = true;
    },
    renameFile(name) {
      this.$refs[name].validate(valid => {
        if (valid) {
          var folderId = this.currentFolder.folderId;
          var fileId = this.renameForeInfo.resourceId;
          var newName = this.renameValidate.newName;
          this.axios
            .get(
              "/GeoProblemSolving/folder/renameFile" +
                "?newName=" +
                newName +
                "&fileId=" +
                fileId +
                "&folderId=" +
                folderId
            )
            .then(res => {
              this.renameFileModal = false;
              if (res.data == "Offline") {
                this.$store.commit("userLogout");
                this.$router.push({ name: "Login" });
              } else if (res.data != "Fail") {
                var newFileInfo = Object.assign({}, this.renameForeInfo);
                newFileInfo.name = newName;
                for (var i = 0; i < this.currentFolder.files.length; i++) {
                  if (this.currentFolder.files[i].resourceId == fileId) {
                    this.currentFolder.files.splice(i, 1, newFileInfo);
                    break;
                  }
                }
              } else {
                this.$Message.warning("Rename fail.");
              }
            })
            .catch(err => {
              this.$Message.warning("Rename fail.");
            });
          this.renameFolderModal = false;
        }
      });
    },
    canBeShare(fileId) {
      var result = true;
      for (var i = 0; i < this.currentFolder.files.length; i++) {
        if (this.currentFolder.files[i].resourceId == fileId) {
          result = false;
        }
      }
      return result;
    }
  }
};
</script>
