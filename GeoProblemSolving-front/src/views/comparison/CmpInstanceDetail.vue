<template>
  <div>
    <h1 style="text-align:center;margin-top:30px;">Comparison Instance</h1>
    <div style="display:flex;width:1200px;margin:auto">
      <div class="left">
        <Card style="height:260px;">
          <p slot="title" style=" font-size: 20px;">
            <Icon type="md-analytics" />
            <span style="margin-left:10px;"></span>
            {{instanceInfo.name}}
          </p>
          <div class="display_flex mb_5">
            <span class="w_100 fw_600">Owner:</span>
            <span>
              {{instanceInfo.userName}}
            </span>
          </div>
          <div class="display_flex mb_5">
            <span class="w_100 fw_600">Type:</span>
            <span>
              {{instanceInfo.type}}
            </span>
          </div>
          <div class="display_flex mb_5">
            <span class="w_100 fw_600">Create Time:</span>
            <span>
              {{instanceInfo.createdTime}}
            </span>
          </div>

          <div class="display_flex mb_5">
            <span class="w_100 fw_600">Description:</span>
            <p class="desc">
              {{instanceInfo.description}}
            </p>
          </div>

        </Card>
        <Divider v-if="modelId" />
        <Card v-if="modelId">
          <div style="display:flex;">
            <img id="model_img" src="@/assets/images/comparison/model.png" alt="model img">
            <div id="model_btn_box">
              <span :title="modelInfo.modelName" id="modelName">{{modelInfo.modelName}}</span>
              <Upload v-if="modelInfo.computableModels&&modelInfo.computableModels.length<=0" :max-size="1024*1024"
                :before-upload="beforeModelUpload" :data="deployRequestInfo"
                accept="application/zip,application/x-zip,application/x-zip-compressed"
                action="/GeoProblemSolving/cmp_model/deployModel" :disabled="creatable" :on-remove="removeFile"
                :on-success="uploadSuccess" :on-error="uploadError">
                <Button icon="ios-cloud-upload-outline">Upload Model</Button>
              </Upload>
              <Button v-if="modelInfo.computableModels&&modelInfo.computableModels.length>0"
                style="width:100px;text-align:center;" type="success" @click="showInvokeModal">Invoke</Button>
            </div>
          </div>
          <Divider />
          <div class="display_flex mb_5">
            <span class="w_100 fw_600">Description:</span>
            <p class="desc">
              {{modelInfo.description}}
            </p>
          </div>
        </Card>
      </div>
      <div class="right">
        <Card>
          <h3>Data List</h3>
          <Table border :max-height="getMaxHeight" :columns="dataColumn" :data="dataList">
            <template slot-scope="{ row, index }" slot="name">
              <strong>{{ row.name }}</strong>
            </template>
            <template slot-scope="{ row, index }" slot="metric">
              <span>{{ row.metrics?row.metrics.alias:"-" }}</span>
            </template>
          </Table>
        </Card>
        <Card style="margin-top:20px;">
          <h3>Model Record Info</h3>
          <p style="margin-top:20px;text-align:center;" v-if="!instanceInfo.recordId">No Record</p>
          <div v-else id="recordRoom">
            <div class="infoBox">
              <span class="infoTag">Name:</span>
              <span class="infoContent">{{this.recordInfo.recordName}}</span>
            </div>
            <div class="infoBox" v-if="this.recordInfo.description">
              <span class="infoTag">Description:</span>
              <span class="infoContent">{{this.recordInfo.description}}</span>
            </div>

            <div class="infoBox">
              <span class="infoTag">Start Time:</span>
              <span class="infoContent">{{this.recordInfo.startTime}}</span>
            </div>

            <div class="infoBox" v-if="recordInfo.time_span>0">
              <span class="infoTag">Time Span:</span>
              <span class="infoContent">{{this.recordInfo.time_span}}s</span>
            </div>
            <div class="infoBox">
              <span class="infoTag">Running state:</span>
              <span class="infoContent">{{getStatus}}</span>
            </div>

            <div id="stateRoom">
              <Card v-for="(state,stateIndex) of recordInfo.states" :bordered="false" :key="state.stateId"
                class="stateCard">
                <p slot="title">
                  {{state.stateName}}
                </p>
                <span slot="title">
                  <Icon type="md-nutrition" />
                  {{state.description}}
                </span>
                <h3>Inputs:</h3>
                <Table border :columns="eventColumn" :data="getInputData(state)"></Table>
                <div style="margin-top:20px" v-if="recordInfo.status===1">
                  <h3>Outputs:</h3>
                  <Table border :columns="eventColumn" :data="getOutputData(state)">

                  </Table>
                </div>
              </Card>
            </div>
          </div>
        </Card>
      </div>
    </div>

    <Modal title="Invoke Model" v-model="invokeModal" @on-ok="invokeModel" @on-cancel="cancel">
      <Card v-for="(state,stateIndex) of states" :bordered="false" :key="state.stateId" class="stateCard">
        <p slot="title">
          {{state.stateName}}
        </p>
        <span slot="title">
          <Icon type="md-nutrition" />
          {{state.description}}
        </span>
        <Form label-position="right" :label-width="150" :ref="state.stateName">
          <h4>Input:</h4>
          <FormItem v-for="(event,eventIndex) of getInputEvent(state.events)" :key="event.name"
            :required="!event.optional">
            <span slot="label">{{event.name}}</span>
            <Input style="width: 200px" v-model="event.fileName" placeholder="Please upload or choose input date…"
              :ref="event.name">

            <Upload :max-size="1024*1024" :before-upload="beforeDataUpload" slot="append" :state="state.stateName"
              :event="event.name" action="/GeoProblemSolving/cmp_model/uploadData_DC" style="display:inline-block;"
              :show-upload-list="false" :data="uploadDataInfo" :on-success="dataUploadSuccess"
              :on-error="dataUploadError">
              <Button>
                <Icon type="md-cloud-upload" size="18" />
              </Button>
            </Upload>
            <!-- <Button slot="append">
              <Icon type="md-cloud-done" size="18" /> </Button>

            <Button slot="append">
              <Icon type="md-folder" size="18" /> </Button> -->
            </Input>
            <span style="color:#6a737d">{{event.description}}</span>
          </FormItem>
          <!-- <h4>Output:</h4>
          <FormItem v-for="(event,eventIndex) of getOutputEvent(state.events)" :key="event.name">
            <span slot="label">{{event.name}}</span>
            <Input style="width: 200px" v-model="event.fileName" :ref="event.name"></Input>
            <span style="color:#6a737d">{{event.description}}</span>
          </FormItem> -->
        </Form>
      </Card>
    </Modal>
  </div>
</template>
<script>
import Util from "@/utils/comparison/cmpUtils";
export default {
  created() {
    this.instanceId = this.$route.params.id;
    this.getInstance();
  },
  data() {
    return {
      instanceId: "",
      instanceInfo: {},
      modelId: "",
      modelInfo: {},
      dataColumn: [
        {
          title: "Name",
          slot: "name",
          align: "center"
        },
        {
          title: "Metric",
          slot: "metric",
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
        }
      ],
      dataList: [],
      deployRequestInfo: {
        md5: "",
        ownerId: this.$store.getters.userId,
        ownerName: this.$store.getters.userName
      },
      modelFile: "",
      fileMd5: "",
      computableModelOid: "",
      creatable: false,
      recordInfo: {},
      invokeModal: false,
      computableModelInfo: {},
      states: [],
      uploadDataInfo: {
        stateName: "",
        eventName: "",
        md5: "",
        userName: this.$store.getters.userName,
        fileName: "",
        sourceStoreId: "",
        desc: "",
        type: ""
      },
      inputData: [],
      outputBindMetric: [],
      eventColumn: [
        {
          title: "Event",
          key: "name"
        },
        {
          title: "FileName",
          key: "fileName"
        },
        {
          title: "Description",
          key: "description"
        },
        {
          title: "Action",
          key: "action",
          width: 200,
          align: "center",
          render: (h, params) => {
            return h("div", [
              h(
                "Button",
                {
                  props: {
                    type: "success",
                    size: "small"
                  },
                  style: {
                    marginRight: "5px"
                  },
                  on: {
                    click: () => {
                      this.downData(params.row);
                    }
                  }
                },
                "Download"
              )
            ]);
          }
        }
      ]
    };
  },
  methods: {
    updateInstance(instance) {
      this.$api.cmp_instance
        .updateInstance(instance)
        .then(res => {})
        .catch(err => {
          this.$Message.error(err);
        });
    },
    getComputableModelInfo(modelId) {
      this.$api.cmp_model
        .getComputableModelInfo(modelId)
        .then(res => {
          this.computableModelInfo = res;
          this.states = res.states;
        })
        .catch(err => {
          this.$Message.error(err);
        });
    },
    showInvokeModal() {
      this.invokeModal = true;
    },
    invokeModel() {},
    cancel() {},
    getInstance() {
      this.$api.cmp_instance
        .findInstanceByInstanceId(this.instanceId)
        .then(res => {
          this.instanceInfo = res;
          this.modelId = res.modelId;
          if (res.cmpDataList.length > 0) {
            this.getCmpDataList(res.cmpDataList);
          }
          if (res.modelId) {
            this.getModelInfo();
          }
          if (res.recordId) {
            this.getModelRecordInfo(res.recordId);
          }
        })
        .catch(err => {
          this.$Message.error(err);
        });
    },
    getCmpDataList(idList) {
      this.$api.cmp_data
        .getDataResourceByIdList(idList)
        .then(res => {
          this.dataList = res;
        })
        .catch(err => {
          this.$Message.error(err);
        });
    },
    getModelInfo() {
      this.$api.cmp_model
        .getModelInfo(this.modelId)
        .then(res => {
          this.modelInfo = res.model;
          if (
            this.modelInfo.computableModels &&
            this.modelInfo.computableModels.length > 0
          ) {
            this.getComputableModelInfo(this.modelId);
          }

          if (this.modelInfo.modelRecordId) {
            this.getModelRecordInfo(this.modelInfo.modelRecordId);
          }
        })
        .catch(err => {
          this.$Message.error(err);
        });
    },
    getModelRecordInfo(recordId) {
      this.$api.cmp_model
        .getRecordInfo(recordId)
        .then(res => {
          this.recordInfo = res;
        })
        .catch(err => {
          this.$Message.error(err);
        });
    },

    beforeDataUpload(file, $event) {
      let infoDiv = event.currentTarget.parentElement.parentElement;
      let stateName = infoDiv.getAttribute("state");
      let eventName = infoDiv.getAttribute("event");
      this.uploadDataInfo.stateName = stateName;
      this.uploadDataInfo.eventName = eventName;
      this.uploadDataInfo.fileName = file.name;
      this.uploadDataInfo.sourceStoreId = "";
      this.uploadDataInfo.md5 = "";
      //计算 md5 值
      return new Promise((resolve, reject) => {
        //* 计算 md5
        Util.getFileMD5(file, md5 => {
          console.log(md5);
          this.uploadDataInfo.md5 = md5;
          //* 后台查询是否已经上传过。
          this.$api.cmp_model
            .matchDataMd5(md5)
            .then(res => {
              if (res.code == -9999) {
                //* 没有该资源  需要上传
                resolve();
              } else {
                //* 已有该资源
                this.uploadDataInfo.sourceStoreId = res.data;
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
    dataUploadSuccess(response) {
      if (response.code == 0) {
        let data = response.data;
        let stateName = data.stateName;
        let eventName = data.eventName;
        let fileName = data.fileName;
        let url = data.dataUrl;
        let md5 = data.md5;
        let sourceStoreId = data.sourceStoreId;

        // let input = {"statename":stateName,"event":eventName,"url":url,"tag":""};
        // this.inputData.push(input);
        // console.log("inputData:",this.inputData);

        let self = this;
        self.states.map(state => {
          if (state.stateName === stateName) {
            state.events.map(event => {
              if (event.name === eventName) {
                self.$set(event, "fileName", fileName);
                self.$set(event, "url", url);
                self.$set(event, "md5", md5);
                self.$set(event, "dcSourceStoreId", sourceStoreId);
              }
            });
          }
        });
        console.log(this.states);
      } else {
        this.$Message.error(response.msg);
      }
    },
    dataUploadError() {
      this.$Message.error("upload data failed!");
    },
    beforeModelUpload(file) {
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
    },
    invokeModel() {
      //* 检查数据是否准备完毕
      let dataReady = this.checkInputData();
      if (!dataReady) {
        this.$Message.error("data not ready");
        return;
      }
      this.inputData = [];
      this.states.forEach(state => {
        return state.events.forEach(event => {
          if (event.url) {
            this.inputData.push({
              statename: state.stateName,
              event: event.name,
              url: event.url,
              md5: event.md5,
              fileName: event.fileName,
              sourceStoreId: event.sourceStoreId
            });
          }
        });
      });

      let formData = new FormData();
      formData.set("ip", this.computableModelInfo.serviceNode.host);
      formData.set("port", this.computableModelInfo.serviceNode.port);
      formData.set("msid", this.computableModelInfo.serviceNode.msid);
      formData.set("userId", this.$store.getters.userId);
      formData.set("username", this.$store.getters.userName);
      formData.set("instanceId", this.instanceId);
      // formData.set("inputs", JSON.stringify(this.inputData));
      formData.set("inputs", JSON.stringify(this.states));
      formData.set("modelId", this.modelId);
      formData.set("modelName", this.modelInfo.modelName);
      formData.set("computableModelId", this.computableModelInfo.oid);
      this.$api.cmp_model
        .invokeModel_MC(formData)
        .then(res => {
          console.log("record:", res);
          // sessionStorage.setItem("recordInfo", JSON.stringify(res));
          // this.$router.push({
          //   path: `/cmp-model-record`,
          //   name: "cmp-model-record",
          //   params: {
          //     id: res.msrId
          //   }
          // });

          this.getModelRecordInfo(res.msrId);
          //* 更新 instance 信息：
          this.instanceInfo.recordId = res.msrId;
          this.updateInstance(this.instanceInfo);
        })
        .catch(err => {
          if (err == "Failed to get record") {
            this.$router.back(-1);
          }
          this.$Message.error(err);
        });

      console.log("inputData:", this.inputData);
    },
    checkInputData() {
      return this.states.every(state => {
        return state.events.every(event => {
          if (event.type === "response" && !event.optional && !event.url) {
            return false;
          } else {
            return true;
          }
        });
      });
    },
    downData(event) {
      // this.$Message.info(event);
      window.open(event.url, "_self");
    }
  },
  computed: {
    getMaxHeight() {
      return this.modelId ? "200" : "400";
    },
    getInputEvent() {
      return function(events) {
        return events.filter(event => {
          return event.type === "response";
        });
      };
    },
    getOutputEvent() {
      return function(events) {
        return events.filter(event => {
          return event.type === "noresponse";
        });
      };
    },
    getStatus() {
      if (this.recordInfo.status === 0) {
        return "Running";
      } else if (this.recordInfo.status === 1) {
        return "Success";
      } else {
        return "Failed";
      }
    },
    getInputData() {
      return function(state) {
        return state.events.filter(event => {
          return event.type === "response" && event.url;
        });
      };
    },
    getOutputData() {
      return function(state) {
        return state.events.filter(event => {
          return event.type === "noresponse" && event.url;
        });
      };
    }
  }
};
</script>
<style scoped>
.recordRoom {
  margin-top: 30px;
  margin-left: 30px;
}
.infoTag {
  font-size: 14px;
  font-weight: 600;
  width: 100px;
  display: inline-block;
}

.infoBox {
  margin-top: 10px;
  margin-left: 15px;
}

.infoContent {
  font-size: 14px;
  margin-left: 10px;
}

.left {
  width: 350px;
  margin-top: 20px;
  margin-left: 50px;
}

.right {
  margin-top: 20px;
  width: 800px;
  margin-left: 20px;
  margin-right: 20px;
  margin-bottom: 20px;
}

.display_flex {
  display: flex;
}
.mb_5 {
  margin-bottom: 5px;
}
.w_100 {
  width: 100px;
}
.fw_600 {
  font-weight: 600;
}

.desc {
  /* font-size: 14px; */
  max-height: 100px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
}

#model_img {
  width: 80px;
  margin-left: 10px;
}

#model_btn_box {
  display: flex;
  flex-direction: column;
  margin-left: 20px;
  width: 200px;
  align-items: center;
}

#modelName {
  font-size: 20px;
  color: #0366d6;
  font-weight: 600;
  text-align: center;
  margin-bottom: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.stateCard {
  margin-top: 20px;
  width: 90%;
  margin-left: auto;
  margin-right: auto;
}

.file Icon {
  cursor: pointer;
}

.file input {
  position: absolute;
  margin-left: -30px;
  width: 30px;
  opacity: 0;
  cursor: pointer;
}
</style>