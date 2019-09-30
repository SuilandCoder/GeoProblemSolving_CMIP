<template>
  <Row id="container">
    <Col span="6" id="left">
    <img id="model_img" src="@/assets/images/comparison/model.png" alt="model img">
    <span id="model_name">{{modelInfo.modelName}}</span>
    <p id="model_desc">{{modelInfo.description}}</p>
    </Col>

    <Col span="18" id="right">
    <h2 style="text-align:center;margin-top:30px;">Record</h2>
    <div id="recordRoom">
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

      <div class="infoBox">
        <span class="infoTag">Running state:</span>
        <span class="infoContent">{{getStatus}}</span>
      </div>

      <div class="infoBox" v-if="recordInfo.time_span>0">
        <span class="infoTag">Time Span:</span>
        <span class="infoContent">{{this.recordInfo.time_span}}s</span>
      </div>

      <div id="stateRoom">
        <Card v-for="(state,stateIndex) of recordInfo.states" :bordered="false" :key="state.stateId" class="stateCard">
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
            <Table border :columns="eventColumn" :data="getOutputData(state)"></Table>
          </div>
        </Card>
      </div>
    </div>
    </Col>
  </Row>
</template>
<script>
export default {
  created: function() {
    let recordId = this.$route.params.id;
    this.getRecordInfo(recordId);
  },
  data() {
    return {
      recordInfo: "",
      modelInfo: "",
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
                    type: "info",
                    size: "small"
                  },
                  style: {
                    marginRight: "5px"
                  },
                  on: {
                    click: () => {
                      this.showDataInfo(params.row);
                    }
                  }
                },
                "View"
              ),
              h(
                "Button",
                {
                  props: {
                    type: "primary",
                    size: "small"
                  },
                  style: {
                    marginRight: "5px"
                  },
                  on: {
                    click: () => {
                      this.editDataInfo(params.row);
                    }
                  }
                },
                "Edit"
              ),
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
    getRecordInfo(recordId) {
      this.$api.cmp_model
        .getRecordInfo(recordId)
        .then(res => {
          this.recordInfo = res;
          this.getModelInfo();
        })
        .catch(err => {
          this.$Message.error(err);
        });
    },
    getModelInfo() {
      this.$api.cmp_model
        .getModelInfo(this.recordInfo.modelId)
        .then(res => {
          this.modelInfo = res.model;
        })
        .catch(err => {
          this.$Message.error(err);
        });
    },
    showDataInfo(event) {
      console.log(event);
      this.$Message.info(event);
    },
    editDataInfo(event) {
      this.$Message.info(event);
    },
    downData(event) {
      // this.$Message.info(event);
      window.open(event.url, "_self");
    }
  },
  computed: {
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
#stateRoom {
  margin-top: 20px;
}

.recordRoom {
  margin-top: 30px;
  margin-left: 30px;
}
.infoTag {
  font-size: 14px;
  font-weight: 600;
}

.infoBox {
  margin-top: 10px;
  margin-left: 15px;
}

.infoContent {
  font-size: 14px;
  margin-left: 10px;
}


</style>