<template>
  <div class="box">
    <h2 style="text-align:center;margin:20px;">{{getTitle(dpmInfo.type)}}</h2>
    <Form ref="dpmInfo" :model="dpmInfo" :rules="rules">
      <FormItem prop="scriptSourceId" label="Script" :label-width="150" l>
        <Upload action="/GeoProblemSolving/cmp_model/uploadData_DC" :before-upload="beforeUpload"
          :data="uploadScriptInfo" :on-success="uploadSuccess" :on-error="uploadError" :on-remove="removeData"
          ref="upload">
          <Button icon="ios-cloud-upload-outline">Upload files</Button>
        </Upload>
      </FormItem>
      <FormItem prop="name" label="Name" :label-width="150">
        <Input v-model="dpmInfo.name" style="width: 300px" placeholder="Enter Method name..." />
      </FormItem>
      <FormItem prop="desc" label="Description" :label-width="150">
        <Input type="textarea" v-model="dpmInfo.desc" placeholder="Enter description about this method..."
          style="width: 500px" />
      </FormItem>

      <FormItem prop="inputList" label="Input" :label-width="150">
        <Table border :columns="dataItemColumn" :data="dpmInfo.inputList" size="small"
          style="width: 650px;display: inline-block;">
        </Table>
        <Button icon="ios-add" type="dashed" size="small" style="margin-left:2.5%;margin-bottom:20px"
          @click="addItem('input')">Add
          Input</Button>
      </FormItem>

      <FormItem prop="outputList" label="Output" :label-width="150">
        <Table border :columns="dataItemColumn" :data="dpmInfo.outputList" size="small"
          style="width: 650px;display: inline-block;">
        </Table>
        <Button icon="ios-add" type="dashed" size="small" style="margin-left:2.5%;margin-bottom:20px"
          @click="addItem('output')">Add
          Output</Button>
      </FormItem>

      <FormItem prop="dependencyItemList" label="Dependency" :label-width="150">
        <Table border :columns="dependencyColumn" :data="dpmInfo.dependencyItemList"
          style="width: 650px;display: inline-block;" size="small">
        </Table>
        <Button icon="ios-add" type="dashed" size="small" style="margin-left:2.5%;margin-bottom:20px"
          @click="addItem('dependency')">Add
          Dependency</Button>
      </FormItem>

      <FormItem prop="parameterList" label="Parameter" :label-width="150">
        <Table border :columns="dataItemColumn" :data="dpmInfo.parameterList" size="small"
          style="width: 650px;display: inline-block;">
        </Table>
        <Button icon="ios-add" type="dashed" size="small" style="margin-left:2.5%;margin-bottom:20px"
          @click="addItem('parameter')">Add
          Parameter</Button>
      </FormItem>

    </Form>
    <Button style="margin-left:150px;margin-bottom:20px;" type="primary" @click="uploadCM" :loading="loading">
      create
    </Button>
    <Modal v-model="modal" :title="modalTitle" @on-ok="modalOk" @on-cancel="modalCancel">
      <Form>
        <FormItem label="Name" :label-width="150">
          <Input v-model="item.name" style="width: 300px" placeholder="" />
        </FormItem>
        <FormItem label="Version" v-if="modalType==='dependency'" :label-width="150">
          <Input v-model="item.version" style="width: 300px" placeholder="" />
        </FormItem>
        <FormItem label="DownloadUrl" v-if="modalType==='dependency'" :label-width="150">
          <Input v-model="item.downloadUrl" style="width: 300px" placeholder="" />
        </FormItem>
        <FormItem label="Type" v-if="modalType==='parameter'" :label-width="150">
          <RadioGroup v-model="item.type" n-if="modalType==='dependency'">
            <Radio v-for="type of itemTypeList" :key="type.name" :label="type.label">{{type.name}}</Radio>
          </RadioGroup>
        </FormItem>
        <FormItem label="Description" v-if="modalType!='dependency'" :label-width="150">
          <Input v-model="item.desc" style="width: 300px" placeholder="" />
        </FormItem>
      </Form>
    </Modal>

  </div>

</template>
<script>
import Util from "@/utils/comparison/cmpUtils";
export default {
  beforeRouteEnter: (to, from, next) => {
    next(vm => {
      $.ajax({
        url: "/GeoProblemSolving/user/state",
        type: "GET",
        async: false,
        success: function(data) {
          if (!data) {
            vm.$store.commit("userLogout");
            vm.$router.push({ name: "Login" });
          }
        },
        error: function(err) {
          console.log("Get user state fail.");
        }
      });
    });
  },
  created() {
    this.dpmInfo.type = sessionStorage.getItem("dpmType");
  },
  data() {
    return {
      dpmInfo: {
        name: "",
        type: "",
        desc: "",
        userName: this.$store.getters.userName,
        userId: this.$store.getters.userId,
        dependencyItemList: [],
        inputList: [],
        parameterList: [],
        outputList: [],
        scriptSourceId: "",
        interpretor: "python"
      },
      dependencyColumn: [
        {
          title: "Name",
          key: "name",
          align: "center"
        },
        {
          title: "Version",
          key: "version",
          align: "center"
        },
        {
          title: "Download Url",
          key: "downloadUrl"
        }
      ],
      dataItemColumn: [
        {
          title: "Name",
          key: "name",
          align: "center"
        },
        {
          title: "Type",
          key: "type",
          align: "center"
        },
        {
          title: "Description",
          key: "desc"
        }
      ],
      uploadScriptInfo: {
        stateName: "",
        eventName: "",
        md5: "",
        userName: this.$store.getters.userName,
        fileName: "",
        sourceStoreId: "",
        desc: "",
        type: ""
      },
      modal: false,
      modalTitle: "",
      modalType: "dependency",
      item: {},
      itemTypeList: this.$store.state.comparison.itemType,
      rules: {
        name: [
          {
            required: true,
            message: "The name cannot be empty and no more than 100 characters",
            trigger: "blur",
            max: 100
          }
        ],
        desc: [
          {
            required: true,
            message: "Cannot be empty and no more than 300 characters",
            trigger: "blur",
            max: 300
          }
        ],
        scriptSourceId: [
          {
            required: true,
            message: "Please upload script"
          }
        ],
        inputList: [
          {
            required: true,
            message: "Please provide input file information"
          }
        ],
        outputList: [
          {
            required: true,
            message: "Please provide output file information"
          }
        ]
      },
      loading: false
    };
  },
  methods: {
    beforeUpload(file, $event) {
      this.dpmInfo.scriptSourceId = "";
      this.$refs.upload.clearFiles();
      this.uploadScriptInfo.fileName = file.name;
      this.uploadScriptInfo.sourceStoreId = "";
      this.uploadScriptInfo.md5 = "";
      //计算 md5 值
      return new Promise((resolve, reject) => {
        //* 计算 md5
        Util.getFileMD5(file, md5 => {
          console.log(md5);
          this.uploadScriptInfo.md5 = md5;
          //* 后台查询是否已经上传过。
          this.$api.cmp_model
            .matchDataMd5(md5)
            .then(res => {
              if (res.code == -9999) {
                //* 没有该资源  需要上传
                resolve();
              } else {
                //* 已有该资源
                this.uploadScriptInfo.sourceStoreId = res.data;
                file = null;
                resolve();
              }
              console.log("md5:", res);
            })
            .catch(err => {
              this.$Message.error(err);
              reject();
            });
        });
      });
    },
    uploadSuccess(response) {
      if (response.code == 0) {
        let data = response.data;
        this.dpmInfo.scriptSourceId = data.sourceStoreId;
        console.log(this.dataInfo);
      } else {
        this.$refs.upload.clearFiles();
        this.$Message.error(response.msg);
      }
    },
    uploadError() {
      console.log(this.$refs.upload);
      this.$refs.upload.clearFiles();
      this.$Message.error("upload data failed!");
    },
    removeData() {
      this.dpmInfo.scriptSourceId = "";
    },
    addItem(item) {
      this.modal = true;
      this.item = {};
      let title = "";
      if (item === "dependency") {
        title = "Add Dependency Item Info";
      } else if (item === "input") {
        title = "Add Input Item Info";
      } else if (item === "parameter") {
        title = "Add Parameter Item Info";
      } else {
        title = "Add Output Item Info";
      }
      this.modalTitle = title;
      this.modalType = item;
    },
    modalOk() {
      if (!this.item.name) {
        this.$Message.error("Please provide item information");
        return;
      }
      if (this.modalType === "dependency") {
        this.dpmInfo.dependencyItemList.push(this.item);
      } else if (this.modalType === "input") {
        this.item.type = "file";
        this.dpmInfo.inputList.push(this.item);
      } else if (this.modalType === "parameter") {
        this.dpmInfo.parameterList.push(this.item);
      } else {
        this.item.type = "file";
        this.dpmInfo.outputList.push(this.item);
      }
    },
    modalCancel() {},
    uploadCM() {
      console.log(JSON.stringify(this.dpmInfo));
      this.$refs["dpmInfo"].validate(valid => {
        if (valid) {
          this.$api.cmp_task
            .uploadDataProcessMethod(this.dpmInfo)
            .then(res => {
              this.$router.go(-1);
            })
            .catch(err => {
              this.$Message.error(err);
            });
        }
      });
    }
  },
  computed: {
    getTitle() {
      return function(type) {
        return type === "normal"
          ? "Upload Data Process Method"
          : "Upload Comparison Method";
      };
    }
  }
};
</script>
<style scoped>
.box {
  width: 1000px;
  margin-left: auto;
  margin-right: auto;
}
</style>