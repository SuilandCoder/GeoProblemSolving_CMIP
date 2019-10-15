<template>
  <div>
    <h1 style="text-align:center;margin-top:30px;">Comparison Task</h1>
    <div style="display:flex;width:1200px;margin:auto">
      <div class="left">
        <Card style="width:350px">
          <p slot="title" style="    font-size: 20px;">
            <Icon type="md-planet" />
            {{taskRecord.name}}
          </p>
          <div class="display_flex mb_10">
            <span class="w_100 fw_600">Description:</span>
            <p>
              {{taskRecord.desc}}
            </p>
          </div>

          <div class="display_flex mb_10">
            <span class="w_100 fw_600">Create Time:</span>
            <span>
              {{taskRecord.createdTime}}
            </span>
          </div>
          <div class="display_flex mb_10">
            <span class="w_100 fw_600">Owner:</span>
            <span>
              {{taskRecord.userName}}
            </span>
          </div>
        </Card>
      </div>
      <div class="right">
        <Card>
          <Tabs value="name1" :animated="false">
            <TabPane label="Data configuration" name="name1">
              <h4 style="margin:10px;">Comparison configuration:</h4>
              <Table border :columns="createMetricsColumn" :data="metricsTableData">
                <template slot-scope="{row}" v-for="instanceCol of getInstanceCol(createMetricsColumn)"
                  :slot="instanceCol.title">
                  <Icon v-if="row[instanceCol.title]" type="md-checkmark" color="rgb(10, 171, 67)"
                    style="{color:green}" />
                  <Icon v-else type="md-close" color="#f00" />
                </template>
              </Table>
              <!-- todo 将 instance 信息展示在这里 -->
            </TabPane>

            <TabPane v-for="(cmpTaskModel,index) in cmpTaskModelList" :key="cmpTaskModel.recordId"
              :label="cmpTaskModel.metricName" :name="cmpTaskModel.recordId">
              <h4 style="margin:10px;">Comparison progress:</h4>
              <Table :columns="cmpMethodRecordCol" :data="cmpTaskModel.cmpMethodInfoList">
                <template slot-scope="{ row, index }" slot="name">
                  <strong>{{ row.methodInfo.name }}</strong>
                </template>
                <template slot-scope="{ row, index }" slot="status">
                  <Icon v-if="row.status==='1'" type="md-checkmark" color="rgb(10, 171, 67)" />
                  <Icon v-if="row.status==='-1'" type="md-close" color="#f00" />
                  <Icon v-if="row.status==='0'" type="md-pulse" color = "coral"/> 
                  <span>{{getExcuteStatus(row.status)}}</span>
                </template>
                <template slot-scope="{ row, index }" slot="progress">
                  <Progress v-if="row.status==='1'" :percent="100" :stroke-color="['#108ee9', '#87d068']" />
                  <Progress v-if="row.status==='-1'" :percent="0" :stroke-color="['#108ee9', '#87d068']" />
                  <Progress v-if="row.status==='0'" :percent="getRandomProcess"
                    :stroke-color="['#108ee9', '#87d068']" />
                </template>
                <template slot-scope="{ row, index }" slot="action">
                  <Button>
                    Show Steps
                  </Button>
                </template>
              </Table>
              <div style="margin-top:10px;" v-for="(cmpMethodInfo,index) in cmpTaskModel.cmpMethodInfoList"
                :key="cmpMethodInfo.methodId">
                <h4 style="margin-top: 30px;  font-size: 18px;margin-left: 10px;">
                  {{index+1}}.{{cmpMethodInfo.methodInfo.name}}</h4>
                <img v-if="cmpMethodInfo.status==='1'" style="width:600px;display:block;margin:auto;" :src="cmpMethodInfo.output.downloadUrl"
                  :alt="cmpMethodInfo.methodInfo.name">
                <!-- <img style="" src="http://127.0.0.1:8082/dataResource/getResource?sourceStoreId=e541ba3f-05b2-4c5a-b1a3-848f19c8b770" alt="sd"> -->
              </div>
            </TabPane>
          </Tabs>
        </Card>
      </div>
    </div>

  </div>
</template>
<script>
export default {
  created() {
    this.taskRecordId = this.$route.params.id;

    this.getCmpTaskRecord();
  },
  data() {
    return {
      taskRecordId: "",
      taskRecord: {},
      cmpTaskModelList: [],
      cmpMethodRecordCol: [
        {
          title: "Name",
          slot: "name",
          width: "100px",
          align: "center"
        },
        {
          title: "State",
          slot: "status",
          width: "150px",
          align: "center"
        },
        {
          title: "Progress",
          slot: "progress",
          align: "center"
        },
        {
          title: "Action",
          slot: "action",
          width: "150px",
          align: "center"
        }
      ]
    };
  },
  methods: {
    async getCmpTaskRecord() {
      try {
        //* 获取对比任务信息
        this.taskRecord = await this.$api.cmp_task.getCmpTaskRecord(
          this.taskRecordId
        );
        this.cmpTaskModelList = await this.taskRecord.cmpTaskModelList;
        // Vue.set(this.cmpTaskModelList,cmpTaskModelList);
        //* 获取所有对比方法信息
        this.cmpTaskModelList.forEach(cmpTaskModel => {
          this.$api.cmp_task
            .getCmpMethodRecordList(cmpTaskModel.cmpMethodList)
            .then(res => {
              // cmpTaskModel.cmpMethodList = res;
              cmpTaskModel.cmpMethodInfoList = res;
              this.getMethodInfo(cmpTaskModel.cmpMethodInfoList);
            })
            .catch(err => {
              this.$Message.error(err);
            });
          this.$api.cmp_task
            .getCmpMethodRecordList(cmpTaskModel.dataProcessMethodList)
            .then(res => {
              // cmpTaskModel.dataProcessMethodList = res;
              cmpTaskModel.dataProcessMethodInfoList = res;
              this.getMethodInfo(cmpTaskModel.dataProcessMethodInfoList);
            })
            .catch(err => {
              this.$Message.error(err);
            });
        });
      } catch (err) {
        this.$Message.error("Failed to get records");
      }
    },
    getObjByid(list, id, type) {
      if (type === "instance") {
        return list.filter(item => {
          return item.instanceId === id;
        })[0];
      } else if (type === "cmpData") {
        return list.filter(item => {
          return item.dataId === id;
        })[0];
      } else if (type === "metric") {
        return list.filter(item => {
          return item.metric.oid === id;
        })[0];
      }
    },
    getMethodInfo(methodModelList) {
      methodModelList.forEach(methodModel => {
        let methodId = "";
        let index = methodModel.methodId.lastIndexOf("_");
        if (index >= 0) {
          methodId = methodModel.methodId.substring(0, index);
        } else {
          methodId = methodModel.methodId;
        }
        this.$api.cmp_task
          .getDataProcessMethodById(methodId)
          .then(res => {
            methodModel.methodInfo = res;
            // this.$set(this,"cmpTaskModelList",this.cmpTaskModelList);

            let tmp = this.cmpTaskModelList.splice(
              this.cmpTaskModelList.length - 1,
              1
            );
            if (tmp.length > 0) {
              this.cmpTaskModelList.push(tmp[0]);
            }

            console.log("list:", this.cmpTaskModelList);
          })
          .catch(err => {
            this.$Message.error(err);
          });
      });
    }
  },
  computed: {
    createMetricsColumn() {
      let metricsColumn = [
        { title: "Metrics", key: "metric", width: "100px", align: "center" }
      ];
      if (this.taskRecord.instanceList) {
        this.taskRecord.instanceList.forEach(instance => {
          let item = {
            title: instance.name,
            slot: instance.name,
            align: "center"
          };
          metricsColumn.push(item);
        });
        return metricsColumn;
      }
    },
    metricsTableData() {
      let metricTableData = [];
      if (this.taskRecord.metrics) {
        this.taskRecord.metrics.forEach(({ metric, releatedInfo }) => {
          let item = { metric: metric.alias };
          releatedInfo.forEach(info => {
            let instanceId = info.instanceId;
            let instance = this.getObjByid(
              this.taskRecord.instanceList,
              instanceId,
              "instance"
            );
            if (instance) {
              item[instance.name] = true;
            }
          });
          metricTableData.push(item);
        });

        return metricTableData;
      }
    },
    getInstanceCol() {
      return function(metricsColumn) {
        if (metricsColumn) {
          return metricsColumn.filter(col => {
            return col.key != "metric" && col.slot != "check";
          });
        }
      };
    },
    getExcuteStatus() {
      return function(status) {
        return status === "1"
          ? "Success"
          : status === "0"
          ? "Running"
          : "Failed";
      };
    },
    getRandomProcess() {
      return _.random(30, 99, false);
    }
  }
};
</script>
<style scoped>
.left {
  width: 400px;
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
.mb_10 {
  margin-bottom: 10px;
}
.w_100 {
  width: 100px;
}
.fw_600 {
  font-weight: 600;
}
</style>