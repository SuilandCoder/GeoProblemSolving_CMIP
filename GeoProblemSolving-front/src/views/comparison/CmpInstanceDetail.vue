<template>
  <div style="overflow:auto">
    <div style="position:relative">
      <Button type="info" style="position:absolute; left:65px" @click="routerBack">
        <Icon type="ios-arrow-back" />
        Backward
      </Button>
      <h1 style="text-align:center;margin-top:30px;">Comparison Instance
        <span :style="{color:getTitleColor}">({{instanceInfo.type}})</span>
      </h1>
    </div>

    <div style="display:flex;width:1500px;margin:auto">
      <div class="left">
        <Card>
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

          <!-- <div class="display_flex mb_5">
            <span class="w_100 fw_600">Description:</span>
            <p class="desc">
              {{instanceInfo.description}}
            </p>
          </div> -->

          <Collapse v-model="collapseVal">
            <Panel name="description" :hide-arrow="true">
              <strong> Description </strong>
              <p slot="content"> {{instanceInfo.description}}</p>
            </Panel>
          </Collapse>

        </Card>
        <Divider v-if="modelId" />
        <Card v-if="modelId">
          <Button v-if="modelInfo.computableModels&&modelInfo.computableModels.length>0" id="fork_btn" size="small"
            type="dashed" shape="circle" title="fork as a new instance" @click="showForkModal">
            <Icon size="18" type="md-git-network" /></Button>
          <div style="display:flex;">
            <img id="model_img" src="@/assets/images/comparison/model.png" alt="model img">
            <div id="model_btn_box">
              <span :title="modelInfo.modelName" id="modelName">{{modelInfo.modelName}}</span>
              <Upload v-if="modelInfo.computableModels&&modelInfo.computableModels.length<=0" :max-size="1024*1024"
                :before-upload="beforeModelUpload" :data="deployRequestInfo"
                accept="application/zip,application/x-zip,application/x-zip-compressed"
                action="/GeoProblemSolving_Backend/cmp_model/deployModel" :disabled="creatable" :on-remove="removeFile"
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
          <div class="display_flex mb_5" v-if="JSON.stringify(this.recordInfo) !== '{}'">
            <span class="w_100 fw_600">Invoked Time:</span>
            <p class="desc">
              {{this.recordInfo.startTime}}
            </p>
          </div>
          <div class="display_flex mb_5" v-if="JSON.stringify(this.recordInfo) !== '{}'">
            <span class="w_100 fw_600">Running Status:</span>
            <p class="desc" :style="{'color':getColorofStatus}">
              {{getStatus}}
            </p>
          </div>

        </Card>
      </div>
      <div class="right">
        <Card>
          <h3>Data List</h3>
          <Table border :max-height="800" :columns="dataColumn" :data="dataList" no-data-text="-">
            <template slot-scope="{ row, index }" slot="name">
              <strong>{{ row.name }}</strong>
            </template>
            <template slot-scope="{ row, index }" slot="metric">
              <span>{{ row.metrics?row.metrics.alias:"-" }}</span>
            </template>
            <template slot-scope="{row,index}" slot="fileSize">
              <span>{{row.fileSize===0?"-":formateFileSize(row.fileSize)}}</span>
            </template>
          </Table>
        </Card>

        <!-- <article v-html="instanceInfo.descMarkDown"></article> -->
        <div>
          <mavon-editor v-if="instanceInfo.descMarkDown" :subfield="false" defaultOpen="preview" :toolbarsFlag="false"
            v-model="instanceInfo.descMarkDown" style="margin-top:20px;" />
        </div>

        <Card class="stateCard" v-if="modelId">
          <h2 slot="title">
            Model Configuration
          </h2>
          <div v-for="(state,stateIndex) of states" :key="state.stateId">
            <h3>{{state.stateName}}</h3>
            <Table border :columns="eventColumn" :data="state.events">
              <template slot-scope="{ row, index }" slot="eventName">
                <strong>{{ row.name }}</strong>
              </template>
              <template slot-scope="{ row, index }" slot="type">
                <span>{{row.type==='response'?'input':'output'}}</span>
              </template>
              <template slot-scope="{ row, index }" slot="desc">
                <span>{{ row.description }}</span>
              </template>
              <template slot-scope="{ row, index }" slot="data">
                <Input style="width: 300px" v-model="row.fileName" :ref="row.name" disabled>
                <Upload v-if="row.type==='response'" :max-size="1024*1024" :before-upload="beforeDataUpload"
                  slot="append" :state="state.stateName" :event="row.name"
                  action="/GeoProblemSolving_Backend/cmp_model/uploadData_DC" style="display:inline-block;"
                  :show-upload-list="false" :data="uploadDataInfo" :on-success="dataUploadSuccess"
                  :on-error="dataUploadError">
                  <Button title="Upload Data">
                    <Icon type="md-cloud-upload" size="18" />
                  </Button>
                </Upload>
                <Button slot="append" :disabled="!row.fileName" title="Download Data" @click="downloadData(row)">
                  <Icon type="md-cloud-download" size="18" /> </Button>
                </Input>

                <!-- <Input v-if="row.type==='noresponse'" enter-button placeholder="Enter metric name"  :value="row.metrics?row.metrics.alise:''"
                  style="width: auto" disabled>
                <Button slot="append" icon="ios-search" @click="search(row.name)"></Button>
                </Input> -->
              </template>
            </Table>
          </div>

        </Card>
        <Button v-if="modelInfo.computableModels&&modelInfo.computableModels.length>0"
          style="width:100px;float:right;margin-top:20px;text-align:center;" type="success"
          @click="showInvokeModal">Invoke</Button>
      </div>
    </div>
    <Modal v-model="modal13" draggable scrollable title="Create Metric">
      <create-metrics-form v-on:createMetricSuccess="onCreateSuccess"></create-metrics-form>
      <span slot="footer"></span>
    </Modal>

    <Modal v-model="modal12" draggable scrollable title="Search Metric">
      <div slot="header" style="display:flex;align-items:center">
        <h3 style="display:inline;margin-right:20px;">Search Metric:</h3>
        <Input enter-button placeholder="Enter metric name" v-model="metricAlias"
          style="width: auto; display:inline-table">
        <Button slot="append" icon="ios-search" @click="search(selectEventName)"></Button>
        <Button slot="append" icon="md-add" @click="createMetric"></Button>
        </Input>
      </div>

      <CellGroup>
        <Cell v-for="(metric,index) of metrics" :title="metric.wkName? metric.wkName: metric.alias" :key="metric.oid"
          @click="chooseMetric(metric)">
          <Button icon="ios-add" type="dashed" size="small" @click="chooseMetric(metric)" slot="extra"></Button>
        </Cell>
      </CellGroup>
    </Modal>

    <Modal v-model="invokeModal" title="Notice" @on-ok="invokeModel">
      <h2>This operation will reset the data of this instance.</h2>
    </Modal>

    <Modal v-model="forkInstanceModal" title="Fork this model to a new instance：" @on-ok="forkNewInstance">
      <Form ref="newInstanceInfo" :model="newInstanceInfo" :rules="rules">
        <FormItem prop="name" label="Instance Name" :label-width="150">
          <Input v-model="newInstanceInfo.name" style="width: 300px" placeholder="Enter instance name" />
        </FormItem>
        <FormItem prop="description" label="Instance Description" :label-width="150">
          <div>
            <Input type="textarea" v-model="newInstanceInfo.description"
              placeholder="Enter description about this instance" />
          </div>
        </FormItem>
      </Form>
    </Modal>
  </div>
</template>
<script>
import Util from "@/utils/comparison/cmpUtils";
import CreateMetricForm from "@/components/comparison/CreateMetricsForm";
export default {
  created() {
    this.instanceId = this.$route.params.id;
    this.projectInfo = this.$route.params.projectInfo;
    // console.log("router params:", this.projectInfo);
    this.getInstance();
  },
  components: {
    "create-metrics-form": CreateMetricForm
  },
  data() {
    return {
      projectInfo: "",
      collapseVal: "description",
      instanceId: "",
      instanceInfo: {},
      newInstanceInfo: {},
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
          align: "center",
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
          slot: "fileSize",
          align: "center"
        }
      ],
      eventColumn: [
        {
          title: "Event Name",
          slot: 'eventName',
          align: "center",
          width: "180px"
        }, {
          title: "Type",
          slot: 'type',
          align: "center",
          width: "100px"
        }, {
          title: "Description",
          slot: "desc",
          align: "center",
        }, {
          title: "Data",
          slot: "data",
          align: "center",
          width: "350px"
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
      forkInstanceModal: false,
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
      eventColumn_old: [
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
      ],
      metrics: [],
      modal12: false,
      modal13: false,
      metricAlias: "",
      selectEventName: {},
      rules: {
        name: [
          {
            required: true,
            message: "Cannot be empty and no more than 100 characters",
            trigger: "blur",
            max: 100
          }
        ],
        description: [
          {
            required: true,
            message: "Cannot be empty and no more than 800 characters",
            trigger: "blur",
            max: 800
          }
        ]
      }
    };
  },
  methods: {
    routerBack() {
      this.$router.back(-1);
    },
    forkNewInstance() {
      this.$refs["newInstanceInfo"].validate(valid => {
        if (valid) {
          this.newInstanceInfo.projectId = this.instanceInfo.projectId;
          this.newInstanceInfo.userId = this.$store.getters.userId;
          this.newInstanceInfo.userName = this.$store.getters.userName;
          this.newInstanceInfo.type = this.instanceInfo.type;
          this.newInstanceInfo.modelId = this.instanceInfo.modelId;
          this.newInstanceInfo.recordId = this.instanceInfo.recordId;
          this.newInstanceInfo.cmpDataList = [];
          this.$api.cmp_instance
            .createInstance(JSON.stringify(this.newInstanceInfo))
            .then(res => {
              console.log("返回的数据信息：", res);
              this.$router.replace({
                path: `/cmp-instance-detail/${res.instanceId}`
              });
              location.reload();
            })
            .catch(err => {
              this.$Message.error(err);
            });
        }
      });


    },
    chooseMetric(metric) {
      this.states.forEach(state => {
        return state.events.forEach(event => {
          if (event.name === this.selectEventName && event.type === 'noresponse') {
            event.metrics = metric;
          }
        });
      });
      // this.metricAlias = metric.alias;
      this.modal12 = false;
    },
    search(eventName) {
      this.selectEventName = eventName;
      this.findMetric(this.metricAlias);

      this.modal12 = true;
    },
    findMetric(item) {
      this.$api.common
        .findByX("metrics", "alias", item)
        .then(res => {
          this.metrics = res;
          this.metricAlias = "";
        })
        .catch(error => {
          this.$Message.error(error);
        });
    },
    createMetric() {
      this.modal12 = false;
      this.modal13 = true;
    },
    onCreateSuccess(data) {
      this.modal13 = false;
      this.metrics.push(data);
      console.log("metric创建成功:", data);
    },
    downloadData(event) {
      // console.log("download:", event);
      // window.open(event.url, "_self");
      let reqJson = { dataUrl: event.url, fileName: event.fileName };
      this.axios
        .post(`/GeoProblemSolving_Backend/cmp_data/downloadDataFromDataContainer`, reqJson)
        .then(res => {
          if (res.data) {
            let content = res.headers["content-disposition"];
            let fileName = content.substring(content.indexOf("filename=") + 9);
            this.downloadLink(res.data, fileName);
          } else {
            this.$Message.error("Failed to download data");
          }
        })
        .catch(err => {
          this.$Message.error(err);
        })
    },
    downloadLink(data, fileName) {
      let url = window.URL.createObjectURL(new Blob([data]))
      let link = document.createElement('a')
      link.style.display = 'none'
      link.href = url
      link.setAttribute('download', fileName)

      document.body.appendChild(link)
      link.click()
    },
    updateInstance(instance) {
      this.$api.cmp_instance
        .updateInstance(instance)
        .then(res => { })
        .catch(err => {
          this.$Message.error(err);
        });
    },
    getComputableModelInfo(modelId) {
      this.$api.cmp_model
        .getComputableModelInfo(modelId)
        .then(res => {
          this.computableModelInfo = res;
          if (JSON.stringify(this.recordInfo) === '{}') {
            this.states = res.states;
          }
        })
        .catch(err => {
          this.$Message.error(err);
        });
    },
    checkPermission() {
      //* 判断是否登录
      if (!this.$store.getters.userState) {
        this.$router.push({
          path: `/login`
        });
      } else {
        //* 判断是否有权限
        let userId = this.$store.getters.userId;
        let index = _.findIndex(this.projectInfo.members, function (member) { return member.userId == userId });
        if (index >= 0 || userId === this.projectInfo.managerId) {
          return true;
        }
        this.$Message.info("No permission, Please apply first.");
        return false;
      }
    },
    showInvokeModal() {
      //* 检查权限
      if (this.checkPermission()) {
        this.invokeModal = true;
      }
    },
    showForkModal() {
      if (this.checkPermission()) {
        this.forkInstanceModal = true;
      }
    },
    cancel() { },
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

          // if (this.modelInfo.modelRecordId) {
          //   this.getModelRecordInfo(this.modelInfo.modelRecordId);
          // }
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
          this.states = res.states;
          if (res.status === 1 && this.instanceInfo.cmpDataList.length == 0) {
            //* 更新 instance cmpData
            let jsonData = {
              ownerId: this.$store.getters.userId,
              ownerName: this.$store.getters.userName,
              action: "update",
              instanceId: this.instanceId,
              states: this.states
            }
            this.updateInstanceCmpData(jsonData);
          }
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
                this.uploadModel();
                this.getComputableModelById(res);
                //* update model info
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
        this.uploadModel();
        this.getComputableModelById(response.data.oid);
      } else {
        this.$Message.error(response.msg);
      }
    },
    uploadError() {
      this.$Message.error("upload model failed!");
      this.creatable = false;
      this.modelInfo.computableModels = [];
    },
    uploadModel() {
      this.$api.cmp_model
        .updateModelResource(this.modelInfo)
        .then(res => {
          this.modelInfo = res;
        })
        .catch(err => {
          this.$Message.error(response.msg);
        });
    },
    getComputableModelById(oid) {
      this.$api.cmp_model
        .getComputableModelById(oid)
        .then(res => {
          this.computableModelInfo = res;
        })
        .catch(err => {
          this.$Message.error(response.msg);
        });
    },
    invokeModel() {
      //* 检查数据是否准备完毕
      let dataReady = this.checkInputData();
      if (!dataReady) {
        this.$Message.error("data not ready");
        return;
      }
      // this.inputData = [];
      this.states.forEach(state => {
        return state.events.forEach(event => {
          if (event.url && event.type === 'noresponse') {
            event.fileName = "";
            event.url = "";
            event.mcDataId = "";
          }
        });
      });

      let reqJson = {};
      reqJson["ip"] = this.computableModelInfo.serviceNode.host;
      reqJson["port"] = this.computableModelInfo.serviceNode.port;
      reqJson["msid"] = this.computableModelInfo.serviceNode.msid;
      reqJson["userId"] = this.$store.getters.userId;
      reqJson["username"] = this.$store.getters.userName;
      reqJson["instanceId"] = this.instanceId;
      reqJson["inputs"] = JSON.stringify(this.states);
      reqJson["modelId"] = this.modelId;
      reqJson["modelName"] = this.modelInfo.modelName;
      reqJson["computableModelId"] = this.computableModelInfo.oid;

      this.$api.cmp_model
        .invokeModel_MC(JSON.stringify(reqJson))
        .then(res => {
          console.log("record:", res);
          this.getModelRecordInfo(res.msrId);
          //* 更新 instance 信息：
          this.instanceInfo.recordId = res.msrId;
          //   this.updateInstance(this.instanceInfo);
          this.$Message.info("Invoke Model Success.");
          //* 更新 instance cmpData 和 recordId
          let jsonData = {
            ownerId: this.$store.getters.userId,
            ownerName: this.$store.getters.userName,
            action: "reset",
            instanceId: this.instanceId,
            recordId: this.instanceInfo.recordId
          }
          this.updateInstanceCmpData(jsonData);
          this.instanceInfo.cmpDataList = [];
        })
        .catch(err => {
          if (err == "Failed to get record") {
            this.$router.back(-1);
          }
          this.$Message.error(err);
        });
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
      window.open(event.url, "_self");
    },
    updateInstanceCmpData(jsonData) {
      this.$api.cmp_instance.updateInstanceCmpData(jsonData)
        .then(res => {
          this.instanceInfo = res;
          if (this.instanceInfo.cmpDataList.length >= 0) {
            this.getCmpDataList(this.instanceInfo.cmpDataList);
          }
        }).catch(err => {
          this.$Message.error(err);
        });
    }
  },
  computed: {
    getMaxHeight() {
      return this.modelId ? "200" : "400";
    },
    getInputEvent() {
      return function (events) {
        return events.filter(event => {
          return event.type === "response";
        });
      };
    },
    getOutputEvent() {
      return function (events) {
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
      return function (state) {
        return state.events.filter(event => {
          return event.type === "response" && event.url;
        });
      };
    },
    getOutputData() {
      return function (state) {
        return state.events.filter(event => {
          return event.type === "noresponse" && event.url;
        });
      };
    },
    formateFileSize() {
      return function (fileSize) {
        let kb = fileSize / 1024;
        if (kb < 0) {
          return "1Kb";
        } else if (kb > 0 && kb < 1024) {
          return kb.toFixed(2) + "Kb";
        }

        let mb = kb / 1024;
        if (mb < 1024) {
          return mb.toFixed(2) + "Mb";
        }

        let gb = mb / 1024;
        return gb.toFixed(2) + 'G';
      }
    },
    getColorofStatus() {
      return this.recordInfo.status === 1
        ? "rgb(10, 171, 67)"
        : this.recordInfo.status === 0
          ? "coral"
          : "#f00";
    },
    getTitleColor() {
      return this.instanceInfo.type === "observation" ? "#20b2aa" : this.instanceInfo.type === "benchmark" ? "#daa520" : "#d2691e";
    }
  }
};
</script>
<style scoped>
#fork_btn {
  position: absolute;
  right: 3px;
  top: 3px;
}
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
  width: 1200px;
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
  width: 120px;
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
  height: 80px;
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
  width: 100%;
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