<template>
  <div class="box">
    <h2 style="text-align:center;margin:20px;">Create Instance</h2>
    <Form ref="instanceInfo" :model="instanceInfo" :rules="rules">
      <FormItem prop="name" label="Name" :label-width="150">
        <Input v-model="instanceInfo.name" style="width: 300px" placeholder="Enter instance name" />
      </FormItem>
      <FormItem prop="description" label="Description" :label-width="150">
        <div>
          <Input type="textarea" v-model="instanceInfo.description" placeholder="Enter description about this instance"
            style="width: 500px" />
        </div>
      </FormItem>
      <FormItem prop="type" label="Type" :label-width="150">
        <RadioGroup v-model="instanceInfo.type">
          <Radio label="observation">Observation Dataset</Radio>
          <Radio label="benchmark">Benchmark Dataset</Radio>
          <Radio label="model">Model Experiment</Radio>
        </RadioGroup>
      </FormItem>
    </Form>
    <!-- <specific-data-form></specific-data-form> -->

    <div v-if="instanceInfo.type === 'model'">
      <Form ref="modelInfo" :rules="modelRules" :model="modelInfo">
        <FormItem prop="modelName" label="Name" :label-width="150">
          <Input v-model="modelInfo.modelName" placeholder="Enter Model Name ..." />
        </FormItem>

        <FormItem prop="description" label="Description" :label-width="150">
          <div>
            <Input type="textarea" v-model="modelInfo.description"
              placeholder="Enter detailed introduction about this model" />
          </div>
        </FormItem>

        <FormItem label="Model" :label-width="150">
          <Upload :max-size="1024*1024" :before-upload="beforeUpload" :data="deployRequestInfo"
            accept="application/zip,application/x-zip,application/x-zip-compressed"
            action="/GeoProblemSolving/cmp_model/deployModel" :disabled="creatable" :on-remove="removeFile"
            :on-success="uploadSuccess" :on-error="uploadError">
            <Button icon="ios-cloud-upload-outline">Upload Model</Button>
          </Upload>
          <div>
            <span v-show="creatable" size="52" style="color: #3399ff">Upload Success.</span>
          </div>
        </FormItem>
      </Form>
    </div>
    <div>
      <div style="height:30px; display: flex; justify-content: space-between; margin-left:100px">
        <h3 style="display:inline">Data List:</h3>
        <Button style="float:right" @click="showUploadDialog()" type="primary">Add Data</Button>
      </div>
      <Table border :columns="dataColumn" :data="dataList" style="margin-left:150px;">
        <template slot-scope="{row,index}" slot="action">
          <Button v-if="row.fileName" @click="deleteDataItem(index)">Delete</Button>
          <Button v-else @click="showUploadDialog(row)">Upload</Button>
        </template>
      </Table>
    </div>

    <Button style="margin-top: 50px;  margin-bottom: 20px; margin-left: 150px;" type="primary" :loading="loading"
      @click="creataInstance">Create</Button>

    <Modal ref="modal" title="Upload Data" v-model="uploadmodal" @on-ok="upload" :mask-closable="false">
      <specific-data-form ref="dataForm" v-on:createSuccess="onCreateSuccess"></specific-data-form>
      <span slot="footer"></span>
    </Modal>
  </div>
</template>
<script>
import SpecificDataForm from "@/components/comparison/SpecificDataForm";
import Util from "@/utils/comparison/cmpUtils";
import base from "@/request/api/base";
export default {
  components: {
    "specific-data-form": SpecificDataForm
  },
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
  created: function() {
    this.baseUrl = base.cmpSolution;
    //获取 project 指定的输出数据信息
    this.instanceInfo.projectId = this.$route.params.id;
  },
  data() {
    return {
      instanceInfo: {
        name: "",
        description: "",
        type: "observation",
        cmpDataList: [],
        userName: this.$store.getters.userName,
        userId: this.$store.getters.userId,
        projectId: "",
        modelId: ""
      },
      dataList: [],
      dataColumn: [
        {
          title: "Name",
          key: "name",
          align: "center"
        },
        {
          title: "Type",
          key: "fileType",
          align: "center"
        },
        {
          title: "AbstractInfo",
          key: "abstractInfo",
          align: "center"
        },
        {
          title: "File Name",
          key: "fileName",
          align: "center"
        },
        {
          title: "File Size",
          key: "fileSize",
          align: "center"
        },
        {
          title: "Action",
          slot: "action",
          align: "center"
        }
      ],
      rules: {
        type: [
          {
            required: true,
            message: "Please select instance type",
            trigger: "change"
          }
        ],
        name: [
          {
            required: true,
            message: "The name cannot be empty and no more than 100 characters",
            trigger: "blur",
            max: 100
          }
        ],
        description: [
          {
            required: true,
            message: "Cannot be empty and no more than 300 characters",
            trigger: "blur",
            max: 300
          }
        ]
      },
      uploadmodal: false,
      dataInfo: {},
      loading: false,
      modelInfo: {
        ownerId: this.$store.getters.userId,
        ownerName: this.$store.getters.userName,
        modelName: "",
        description: "",
        privacy: "Public",
        computableModels: []
      },
      modelRules: {
        modelName: [
          {
            required: true,
            message: "The name cannot be empty and no more than 100 characters",
            trigger: "blur",
            max: 100
          }
        ],
        description: [
          {
            required: true,
            message:
              "The description cannot be empty and no more than 600 characters",
            trigger: "blur",
            max: 600
          }
        ]
      },
      deployRequestInfo: {
        md5: "",
        ownerId: this.$store.getters.userId,
        ownerName: this.$store.getters.userName
      },
      modelFile: "",
      fileMd5: "",
      computableModelOid: "",
      creatable: false,
      baseUrl: ""
    };
  },
  methods: {
    showUploadDialog() {
      this.uploadmodal = true;
    },
    deleteDataItem(index) {
      this.dataList.splice(index, 1);
    },
    upload() {
      this.$refs.dataForm.createDataResource();
    },
    onCreateSuccess(data) {
      this.dataList.push(data);
      console.log(data);
      this.uploadmodal = false;
    },
    creataInstance() {
      this.$refs["instanceInfo"].validate(valid => {
        if (valid) {
          this.instanceInfo.cmpDataList = this.dataList.map(data => {
            if (data.fileName) {
              return data.dataId;
            }
          });
          //* 判断 type
          if (this.instanceInfo.type === "model") {
            this.$refs["modelInfo"].validate(valid => {
              if (valid) {
                this.$api.cmp_model
                  .create(this.modelInfo)
                  .then(res => {
                    // console.log(res);
                    let modelId = res.oid;
                    this.instanceInfo.modelId = modelId;

                    this.createInstanceApi();
                  })
                  .catch(err => {
                    this.$Message.error(err);
                  });
              }
            });
          } else {
            if (this.instanceInfo.cmpDataList.length > 0) {
              this.createInstanceApi();
            } else {
              this.$Message.error("please upload data");
            }
          }
        }
      });
    },
    createInstanceApi() {
      this.loading = true;
      this.$api.cmp_instance
        .createInstance(this.instanceInfo)
        .then(res => {
          this.loading = false;
          console.log("返回的数据信息：", res);
          this.$router.back(-1);
        })
        .catch(err => {
          this.loading = false;
          this.$Message.error(err);
        });
    },
    beforeUpload(file) {
      this.creatable = false;
      this.computableModelOid = "";
      console.log("getMD5");
      this.fileMd5 = "";
      this.deployRequestInfo.md5 = "";

      return new Promise((resolve, reject) => {
        //* 获取 MD5 值
        Util.getFileMD5(file, md5 => {
          console.log(md5);
          this.fileMd5 = md5;
          this.deployRequestInfo.md5 = md5;
          //* 后台查询是否已经上传过。
          this.$api.cmp_model
            .matchMd5(md5)
            .then(res => {
              if (res == null) {
                console.log("第一次上传");
                this.modelFile = file;
                this.modelFile["fileSize"] =
                  Math.round((this.modelFile.size / 1024) * 100) / 100;
                resolve();
              } else {
                console.log("该文件已存在：", res);
                this.computableModelOid = res;
                this.modelInfo.computableModels.push(res);
                this.creatable = true;
                reject();
              }
            })
            .catch(err => {
              this.$Message.error(err);
              reject();
            });
        });
      });
    },
    delFile() {
      this.modelFile = "";
      this.creatable = false;
    },
    removeFile() {
      console.log("delete");
      this.creatable = false;
      this.modelInfo.computableModels = [];
    },
    uploadSuccess(response) {
      // console.log(response);
      if (response.code == 0) {
        this.creatable = true;
        this.modelInfo.computableModels.push(response.data.oid);
      } else {
        this.$Message.error(response.msg);
      }
    },
    uploadError() {
      this.$Message.error("upload model failed!");
      this.creatable = false;
      this.modelInfo.computableModels = [];
    }
  },
  computed: {
    getFileSizeStr() {
      let str = "";
      let tmp = this.modelFile.size / 1024;
      if (this.modelFile.size < Math.pow(1024, 2)) {
        str = tmp.toFixed(2) + "KB";
      } else {
        tmp = tmp / 1024;
        str = tmp.toFixed(2) + "MB";
      }
      return str;
    },
    getDeployModelUrl() {
      return `${this.baseUrl}/deployModel`;
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