<template>
  <Row id="container">
    <Col span="6" id="left">
    <img id="model_img" src="@/assets/images/comparison/model.png" alt="model img">
    <span id="model_name">{{modelInfo.modelName}}</span>
    <p id="model_desc">{{modelInfo.description}}</p>
    </Col>

    <Col span="18" id="right">
    <h2 style="text-align:center;margin-top:30px;">Model Input</h2>
    <div id="stateRoom">
      <Card v-for="(state,stateIndex) of states" :bordered="false" :key="state.stateId" class="stateCard">
        <p slot="title">
          {{state.stateName}}
        </p>
        <span slot="title">
          <Icon type="md-nutrition" />
          {{state.description}}
        </span>
        <Form label-position="right" :label-width="150" :ref="state.stateName">
          <FormItem v-for="(event,eventIndex) of getInputEvent(state.events)" :key="event.name"
            :required="!event.optional">
            <span slot="label">{{event.name}}</span>
            <Input style="width: 500px" v-model="event.fileName" placeholder="Please upload or choose input date…"
              :ref="event.name">

            <Upload :max-size="1024*1024" :before-upload="beforeUpload" slot="append" :state="state.stateName"
              :event="event.name" action="/GeoProblemSolving/cmp_model/uploadData_DC" style="display:inline-block;"
              :show-upload-list="false" :data="uploadDataInfo" :on-success="uploadSuccess" :on-error="uploadError">
              <Button>
                <Icon type="md-cloud-upload" size="18" />
              </Button>
            </Upload>

            <Button slot="append">
              <Icon type="md-cloud-done" size="18" /> </Button>

            <Button slot="append">
              <Icon type="md-folder" size="18" /> </Button>
            </Input>

            <span style="color:#6a737d">{{event.description}}</span>
          </FormItem>
        </Form>
      </Card>
    </div>
    <Button type="success" id="invokeBtn" @click="invokeModel">Invoke</Button>
    </Col>
  </Row>
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
  created: function() {
    //* 获取路由信息
    // this.modelInfo = this.$route.params.modelInfo;
    this.modelId = this.$route.params.id;
    // this.modelInfo = sessionStorage.getItem("modelInfo");
    this.modelInfo = JSON.parse(sessionStorage.getItem("modelInfo"));
    console.log("modelInfo: ", this.modelInfo);

    //* 获取计算模型信息
    this.getComputableModelInfo(this.modelId);
  },
  data() {
    return {
      modelInfo: {},
      modelId: "",
      dataTypeList: this.$store.state.comparison.dataType,
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
      inputText: {}
    };
  },
  methods: {
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
    beforeUpload(file, $event) {
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
    uploadSuccess(response) {
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
    uploadError() {
      this.$Message.error("upload data failed!");
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
          this.$router.push({
            path: `/cmp-model-record`,
            name: "cmp-model-record",
            params: {
              id: res.msrId
            }
          });
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
    }
  },
  computed: {
    getInputEvent() {
      return function(events) {
        return events.filter(event => {
          return event.type === "response";
        });
      };
    }
  }
};
</script>
<style scoped>
#container {
  width: 1200px;
  margin-left: auto;
  margin-right: auto;
}
#left {
  margin-top: 50px;
  display: flex;
  flex-direction: column;
  align-items: center;
  border-right: 1px solid #e1e4e8;
}
#right {
  padding-left: 20px;
}
#model_img {
  width: 80px;
}
#model_name {
  font-size: 28px;
  color: #0366d6;
  margin-top: 10px;
}
#model_desc {
  font-size: 18px;
  margin-top: 5px;
  margin-left: 10px;
  color: #666;
}

#invokeBtn {
  float: right;
  margin: 20px;
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