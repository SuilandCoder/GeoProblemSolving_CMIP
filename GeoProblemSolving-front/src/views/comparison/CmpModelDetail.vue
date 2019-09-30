<template>

  <div class="modelContent">
    <div class="topContent">
      <div class="modelInfo">
        <div class="leftInfo">
          <div id="titleInfo">
            <h1 class="public">
              <span style="display:flex">
                <Icon type="md-planet" class="titleIcon" color="#959da5" size="22" />
                <span id="projectName"><a href="#">{{projectTitle}}</a></span>
                <span style="margin:0 .25em">/</span>
                <strong><a href="#">{{modelInfo.modelName}}</a></strong>
              </span>
              <span class="fromFlag">
                <span>
                  upload by <a href="#">{{modelInfo.ownerName}}</a>
                </span>
                <span>{{getCreatedTime(modelInfo)}}</span>
              </span>
            </h1>
          </div>
          <div id="desc">
            <h3>Description:</h3>
            <p>{{modelInfo.description}}</p>
          </div>
        </div>
        <div class="rightInfo">
          <Tabs class="tabContent" value="MetaInfo" :animated="false">
            <Button @click="runModel" slot="extra">Run Model</Button>
            <TabPane label="Meta Info" name="MetaInfo">Meta Info</TabPane>
            <TabPane label="Input Data" name="InputData">Input Data</TabPane>
            <TabPane label="Output Data" name="OutputData">Output Data</TabPane>
          </Tabs>
        </div>
      </div>

    </div>

    <div class="instanceContent">
      <h2>Record:</h2>
      <div class="listBox">
        <!-- <div class="item" v-for="record of recordList" :key="record.msrId">
          <div id="topInfo">
            <span>{{record.recordName}}</span>
            <div id="topBtn">
              <button>Edit</button>
              <button>Public</button>
            </div>
          </div>
          <p>{{record.description}}</p>
          <div id="bottomInfo">
            <div>
              <span>Status:{{getStatus(record)}}</span>
              <span>Time Span:{{record.time_span}}s</span>
            </div>
            <div>
              <span>Created Time:{{record.startTime}}</span>
            </div>
          </div>
        </div> -->
        <Table border :columns="recordColumn" :data="getRecordShowList"></Table>
      </div>
    </div>

  </div>
</template>
<script>
export default {
  created: function() {
    //* 获取路由信息
    this.projectTitle = this.$route.params.projectTitle;
    this.modelId = this.$route.params.id;
    // console.log("parent: ",this.projectInfo);
    //* 获取模型信息
    this.getModelInfo(this.modelId);
    //* 获取记录信息
    this.getRecordList(this.modelId);
  },
  data() {
    return {
      modelInfo: {
        modelName: ""
      },
      projectTitle: "",
      modelId: "",
      recordList: [],
      recordColumn: [
        {
          title: "Name",
          key: "recordName"
        },
        {
          title: "Description",
          key: "description",
          align: "center",
          width: 250
        },
        {
          title: "Created Time",
          key: "startTime",
          sortable: true,
          align: "center",
          sortType: "desc"
        },
        {
          title: "Status",
          key: "status",
          sortable: true,
          align: "center",
          width: 100
        },
        {
          title: "Time Span",
          key: "time_span",
          align: "center",
          width: 100
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
                      this.showRecordInfo(params.index);
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
                      this.editRecord(params.index);
                    }
                  }
                },
                "Eidt"
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
                      this.publicRecord(params.index);
                    }
                  }
                },
                "Publish"
              )
            ]);
          }
        }
      ]
    };
  },
  computed: {
    getCreatedTime() {
      return function(model) {
        return model.createTime ? model.createTime.split(" ")[0] : "";
      };
    },
    getStatus() {
      return function(recordInfo) {
        if (recordInfo.status === 0) {
          return "Running";
        } else if (recordInfo.status === 1) {
          return "Success";
        } else {
          return "Failed";
        }
      };
    },
    getRecordShowList() {
      return this.recordList.map(record => {
        if (record.status === 0) {
          record.status = "Running";
        } else if (record.status === 1) {
          record.status = "Success";
        } else {
          record.status = "Failed";
        }
        if (record.description === null) {
          record.description = "-";
        }
        if (record.time_span === 0) {
          record.time_span = "-";
        }
        return record;
      });
    }
  },
  methods: {
    getModelInfo(modelId) {
      this.$api.cmp_model
        .getModelInfo(modelId)
        .then(res => {
          console.log(res);
          this.modelInfo = res.model;
        })
        .catch(err => {
          this.$Message.error(err);
        });
    },
    runModel() {
      sessionStorage.setItem("modelInfo", JSON.stringify(this.modelInfo));
      this.$router.push({
        path: `/cmp-invoke-model`,
        name: "cmp-invoke-model",
        params: {
          id: this.modelId
        }
      });
    },
    async getRecordList(modelId) {
      try {
        //* 先获取计算模型信息
        let cmi = await this.$api.cmp_model.getComputableModelInfo(modelId);
        //* 通过 recordList 获取所有记录信息
        this.recordList = await this.$api.cmp_model.getRecordList(
          cmi.recordList
        );
      } catch (err) {
        this.$Message.error("Failed to get records");
      }
    },
    showRecordInfo(index) {
      this.$router.push({
        path: `/cmp-model-record`,
        name: "cmp-model-record",
        params: {
          id: this.recordList[index].msrId
        }
      });
    },
    editRecord(index) {
      console.log(index);
    },
    publicRecord(record) {}
  }
};
</script>
<style scoped>


.topContent {
  display: flex;
  flex-wrap: wrap;
  background-color: #fafbfc;
}

.modelInfo {
  padding-left: 20px;
  padding-right: 20px;
  width: 1060px;
  margin-top: 20px;
  margin-left: auto;
  margin-right: auto;

  padding: auto;
  background-color: #fafbfc;
  display: flex;
  flex-wrap: wrap;
}

.leftInfo {
  flex: 1 0 400px;
  border-right: 1px solid #d1d5da;
  margin-right: 20px;
  margin-bottom: 20px;
}

.rightInfo {
  flex: 1 0 500px;
}

.instanceContent {
  width: 1060px;
  margin: auto;
  margin-top: 20px;
}

#projectName {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
}

#titleInfo {
  display: inline-block;
}

#desc {
  margin-top: 10px;
}

#desc p {
  margin-top: 5px;
  margin-left: 20px;
  margin-bottom: 20px;
}

.public {
  color: #586069;
  float: left;
  font-size: 18px;
  line-height: 26px;
  max-width: 635px;
  padding-left: 18px;
  position: relative;
  font-weight: 400;
  margin-bottom: 0;
  margin-top: 0;
}

.titleIcon {
  color: #959da5;
  left: -10px;
  margin-bottom: 5px;
  position: absolute;
  top: 0;
}

.tabContent {
  /* width: 960px; */
  margin-left: auto;
  margin-right: auto;
  max-height: 300px;
}

.fromFlag {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  line-height: 10px;
  width: 200px;
  white-space: nowrap;
}

.listBox{
  margin: 20px;
}
</style>