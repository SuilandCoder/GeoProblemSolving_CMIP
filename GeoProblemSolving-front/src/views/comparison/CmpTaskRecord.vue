<template>
  <div style="overflow:auto">
    <h1 style="text-align:center;margin-top:30px;">Comparison Task</h1>
    <div style="display:flex;width:1500px;margin:auto">
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
          <Button type="info" style="float: right;" icon="md-git-network" title="Reconfigure as a new task."
            @click="reconfigure">Reconfigure</Button>
          <div id="modelInfo_box">
            <div class="right_title">
              <Divider orientation="left" dashed>
                <h2>Instances Info</h2>
              </Divider>
            </div>
            <Table border :columns="createMetricsColumn" :data="metricsTableData">
              <template slot-scope="{row}" v-for="instanceCol of getInstanceCol(createMetricsColumn)"
                :slot="instanceCol.title">
                <Icon v-if="row[instanceCol.title]" type="md-checkmark" color="rgb(10, 171, 67)"
                  style="{color:green}" />
                <Icon v-else type="md-close" color="#f00" />
              </template>
            </Table>
          </div>
          <div id="taskConfig_box">
            <div class="right_title">
              <Divider orientation="left" dashed>
                <h2>Task Configuration</h2>
              </Divider>
            </div>
            <div id="graphContainer">
            </div>
          </div>
          <div id="taskResult_box">
            <div class="right_title">
              <Divider orientation="left" dashed>
                <h2>Comparison Result</h2>
              </Divider>
            </div>

            <div v-for="(cmpTaskModel,index) in cmpTaskModelList">
              <div style="margin-top:10px;" v-for="(cmpMethodInfo,index) in cmpTaskModel.cmpMethodInfoList">
                <h4 style="margin-top: 30px;  font-size: 18px;margin-left: 10px;">
                  <li>{{cmpMethodInfo.methodInfo?cmpMethodInfo.methodInfo.name:""}}</li>
                </h4>
                <!-- <img v-if="cmpMethodInfo.status==='1'" style="width:600px;display:block;margin:auto;"
                  :src="cmpMethodInfo.output.downloadUrl"
                  :alt="cmpMethodInfo.methodInfo?cmpMethodInfo.methodInfo.name:''"> -->

                <!-- 如果是表，则前端绘制 -->
                <async-table v-if="cmpMethodInfo.status==='1'&&cmpMethodInfo.output&&cmpMethodInfo.output.type=='table'"
                  :json-url="cmpMethodInfo.output.downloadUrl" :file-name="cmpMethodInfo.output.fileName"
                  :suffix="cmpMethodInfo.output.suffix" style="width:800px;display:block;margin:auto;margin-top:20px;">
                </async-table>

                <async-img v-if="cmpMethodInfo.status==='1'&&cmpMethodInfo.output&&cmpMethodInfo.output.type=='file'"
                  style="width:600px;display:block;margin:auto;" :img-url="cmpMethodInfo.output.downloadUrl">
                </async-img>
              </div>
            </div>
          </div>
        </Card>
      </div>
    </div>
    <Modal v-model="running_process_modal" title="Process Info">
      <div v-if="selectVertex.data">
        <div style="margin-top:10px;">
          <!-- <h3>Method Name:</h3> -->
          <h2 style="display:block;color:cadetblue;">
            {{getMethodRecordBySelectVertex.methodInfo?getMethodRecordBySelectVertex.methodInfo.name:""}}</h2>
        </div>

        <div style="margin-top:10px;">
          <h3 style="display:inline-block">Running Status:</h3>
          <Icon v-if="getMethodRecordBySelectVertex.status==='1'" type="md-checkmark" color="rgb(10, 171, 67)" />
          <Icon v-if="getMethodRecordBySelectVertex.status==='-1'" type="md-close" color="#f00" />
          <Icon v-if="getMethodRecordBySelectVertex.status==='0'" type="md-pulse" color="coral" />
          <Icon v-if="getMethodRecordBySelectVertex.status==='2'" type="md-pulse" color="#efae39" />
          <span
            :style="{'color':getColorofStatus(getMethodRecordBySelectVertex.status)}">{{getExcuteStatus(getMethodRecordBySelectVertex.status)}}</span>
        </div>

        <div style="margin-top:10px;">
          <h3>Description:</h3>
          <p style="margin:10px;">
            {{getMethodRecordBySelectVertex.methodInfo?getMethodRecordBySelectVertex.methodInfo.desc:""}}</p>
        </div>

        <div style="margin-top:10px;">
          <h3>Params:</h3>
        </div>
        <Form style="margin-left:60px;max-height:300px;overflow:auto;">
          <FormItem v-for="(param,index) in getMethodRecordBySelectVertex.params" :key="index" :label="param.name"
            :label-width="60">
            <DatePicker v-if="param.type==='date'" v-model="param.value" type="date" :start-date="new Date(1982, 0, 1)"
              placeholder="1982-2013" size="small" style="width:120px;margin-left:10px;" disabled>
            </DatePicker>
            <RadioGroup v-else-if="param.type==='time_unit'" v-model="param.value" disabled>
              <Radio key="day" label="D" disabled>day</Radio>
              <Radio key="month" label="M" disabled>month</Radio>
              <Radio key="year" label="Y" disabled>year</Radio>
            </RadioGroup>
            <input v-else v-model="param.value" :placeholder="param.type"
              style="width:100px;height:25px;margin-left:10px;" disabled />
          </FormItem>
        </Form>

        <Button :style="{float:'right'}" type="info" v-if="getMethodRecordBySelectVertex.status==='1'"
          @click="downData(getMethodRecordBySelectVertex.output)">Download Data</Button>
      </div>

      <div slot="footer"></div>
    </Modal>

  </div>
</template>
<script>
import mxgraph from "@/utils/comparison/graph/index";
import { genGraph, destroyGraph, Graph } from "@/utils/comparison/graph/Graph";
import AsyncImg from "@/components/comparison/AsyncImg";
import AsyncTable from "@/components/comparison/AsyncTable";
const {
  mxGraph,
  mxOutline,
  mxEvent,
  mxCell,
  mxGeometry,
  mxUtils,
  mxEventObject,
  mxConnectionHandler
  // mxGraphHandler
} = mxgraph;

Object.assign(mxEvent, {
  NORMAL_TYPE_CLICKED: "NORMAL_TYPE_CLICKED"
});
let px = "";
let py = "";

let graph = null;
let outline = null;
let idSeed = 0;
let ellipse = "ellipse;whiteSpace=wrap;html=1;";
let rounded = "rounded=1;whiteSpace=wrap;html=1;";
let hexagon = "shape=hexagon;whiteSpace=wrap;html=1;";
let rhombus = "rhombus;whiteSpace=wrap;html=1;";

const listenGraphEvent = () => {
  graph.addListener(mxEvent.CLICK, (sender, evt) => {
    const cell = evt.properties.cell;
    if (!cell) {
      return;
    }

    const clickNormalType = cell.style.includes("normalType");
    if (clickNormalType) {
      // 使用 mxGraph 事件中心，触发自定义事件
      graph.fireEvent(
        new mxEventObject(mxEvent.NORMAL_TYPE_CLICKED, "cell", cell)
      );
    }
  });
};

const setCursor = () => {
  const oldGetCursorForCell = mxGraph.prototype.getCursorForCell;
  graph.getCursorForCell = function (...args) {
    const [cell] = args;
    return cell.style.includes("normalType")
      ? "pointer"
      : oldGetCursorForCell.apply(this, args);
  };
};

const initGraph = () => {
  graph = genGraph(document.getElementById("graphContainer"));

  // 将外元素拖拽进画布参考这个例子
  // https://github.com/jinzhanye/mxgraph-demos/blob/master/src/07.drag.html
  // makeDraggable(document.getElementsByClassName("element-item"));
  listenGraphEvent();
  setCursor();
  // setConnectValidation();
  graph.setCellsResizable(true);
  graph.setConnectable(true);
  // mxGraphHandler.prototype.setMoveEnabled(true);
  // mxGraphHandler.prototype.guidesEnabled = true;
  graph.edgeLabelsMovable = true;
  // new mxCellTracker(graph,"#00FFff");
  // graph.setTooltips(true); // 是否显示提示,默认显示Cell的名称
  // graph.connectionArrowsEnabled = true;
};

export default {
  beforeRouteLeave(to, from, next) {
    // ...
    clearInterval(this.timer);
    next();
  },
  components: {
    "async-img": AsyncImg,
    "async-table": AsyncTable
  },
  created() {
    this.taskRecordId = this.$route.params.id;
    this.projectInfo = this.$route.params.projectInfo;
    // console.log("luyou:",this.projectInfo);
    this.getCmpTaskRecord();
    this.startTaskQueue();
  },
  data() {
    return {
      projectInfo: "",
      taskRecordId: "",
      taskRecord: {},
      cmpTaskModelList: [],
      // cmpMethodRecordList:[],
      // cmpTaskModel_solo: {},
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
      ],
      methodRequestList: [],
      timer: function () { },
      running_process_modal: false,
      selectVertex: {},
      excuteFailed: false,
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
        // this.cmpTaskModel_solo = this.cmpTaskModelList[0];
        //* 显示流程图
        graph.importModelXML(this.cmpTaskModelList[0].graphXML);
        //* 修改 instance 节点样式
        let vertices = graph.getChildVertices(graph.getDefaultParent());
        vertices.forEach(vert => {
          if (vert.data.type === "instance") {
            // graph.setStyle(vert, "strokeWidth=3;");
          }
        });

        this.cmpTaskModelList.forEach(cmpTaskModel => {
          //* 获取所有数据处理方法信息
          this.getCmpMethodRecordList(cmpTaskModel, "cmpMethod");
          //* 获取所有对比方法信息
          this.getCmpMethodRecordList(cmpTaskModel, "dpm");
        });
      } catch (err) {
        this.$Message.error("Failed to get records");
      }
    },
    getCmpMethodRecordList(taskModel, type) {
      let list = [];
      if (type === "cmpMethod") {
        list = taskModel.cmpMethodList;
      } else {
        list = taskModel.dataProcessMethodList;
      }
      let that = this;
      this.$api.cmp_task
        .getCmpMethodRecordList(list)
        .then(res => {
          res.forEach(record => {
            if (record.status === "0") {
              this.methodRequestList.push(record);
            }
            if (record.status === '-1') {
              this.excuteFailed = true;
            }
            this.changeVertexStyle(record);
          });
          if (type === "cmpMethod") {
            taskModel.cmpMethodInfoList = res;
            that.getMethodInfo(taskModel.cmpMethodInfoList);
          } else {
            taskModel.dataProcessMethodInfoList = res;
            that.getMethodInfo(taskModel.dataProcessMethodInfoList);
          }
        })
        .catch(err => {
          this.$Message.error(err);
        });
    },
    startTaskQueue() {
      let that = this;
      return this.timer = setInterval(() => {
        if (that.methodRequestList.length > 0) {
          if (that.excuteFailed) {
            //* 当有方法执行失败时 发送请求，将 methodRequestList(正在运行)中的记录状态设为 2(终止)；
            that.methodRequestList.forEach(mr => {
              mr.status = "2";
            })
            that.$api.cmp_task.updateCmpMethodRecord(that.methodRequestList);
          }
          //* 发送请求
          that.requestRunningMethod();
        }
      }, 5000);
    },
    requestRunningMethod() {
      // let requestList = [];
      // this.methodRequestList.forEach(req => {
      //   requestList.push(req.recordId);
      // });
      if (this.methodRequestList.length > 0) {
        this.methodRequestList = [];
        this.cmpTaskModelList.forEach(cmpTaskModel => {
          //* 获取所有数据处理方法信息
          this.getCmpMethodRecordList(cmpTaskModel, "cmpMethod");
          //* 获取所有对比方法信息
          this.getCmpMethodRecordList(cmpTaskModel, "dpm");
        });
      }
      // this.getCmpMethodRecordList_timer(requestList);
    },
    getCmpMethodRecordList_timer(list) {
      let that = this;
      this.$api.cmp_task
        .getCmpMethodRecordList(list)
        .then(res => {
          res.forEach(record => {
            if (record.status !== "0") {
              // this.methodRequestList.push(record);
              //todo 从列表中去除该record
              this.methodRequestList.splice(_.findIndex(this.methodRequestList, res), 1);
              //todo  更新 cmpTaskModelList cmpMethodInfoList 信息
              this.cmpTaskModelList.forEach(cmpTaskModel => {
                //* 更新 cmpMethodInfo 信息
                let cmpMethodInfoIndex = _.findIndex(cmpTaskModel.cmpMethodInfoList, function (o) { return o.recordId === record.recordId });
                if (cmpMethodInfoIndex >= 0) {
                  cmpTaskModel.cmpMethodInfoList.splice(cmpMethodInfoIndex, 1, record);
                }
                let dpmInfoIndex = _.findIndex(cmpTaskModel.dataProcessMethodInfoList, function (o) { return o.recordId === record.recordId });
                if (dpmInfoIndex >= 0) {
                  cmpTaskModel.dataProcessMethodInfoList.splice(dpmInfoIndex, 1, record);
                }
              });
            }
            this.changeVertexStyle(record);
          });
          console.log("this.cmpTaskModelList:", this.cmpTaskModelList);
        })
        .catch(err => {
          this.$Message.error(err);
        });
    },
    changeVertexStyle(record) {
      let vertices = graph.getChildVertices(graph.getDefaultParent());
      if (record.status === "-1") {
        vertices.forEach(vert => {
          if (
            vert.data.type !== "instance" &&
            vert.data.oid === record.methodId
          ) {
            graph.setStyle(vert, "fillColor=#FF6666;shadow=0;strokeWidth=3;");
          }
        });
      } else if (record.status === "1") {
        vertices.forEach(vert => {
          if (vert.data.type !== "instance") {
            if (
              vert.data.type !== "instance" &&
              vert.data.oid === record.methodId
            ) {
              graph.setStyle(vert, "strokeWidth=3;");
            }
          }
        });
      } else if (record.status === "2") {
        vertices.forEach(vert => {
          if (
            vert.data.type !== "instance" &&
            vert.data.oid === record.methodId
          ) {
            graph.setStyle(vert, "fillColor=#FFE599;strokeWidth=3;");
          }
        });
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
    reconfigure() {
      if (this.checkPermission()) {
        //* 跳转 createTask 页面 ，并将 taskRecord作为参数
        if (JSON.stringify(this.taskRecord) !== '{}') {
          // let taskRecord = ;
          this.$router.push({
            path: `/create-cmp-task`,
            name: "create-cmp-task",
            params: {
              id: this.taskRecord.projectId,
              taskRecord: this.taskRecord
            }
          })
        } else {
          this.$Message.info("Task Record has not been fetched.");
        }
      }
    },
    showNormalTypeSelect(sender, evt) {
      const normalTypeDom = graph.getDom(evt.getProperty("cell"));
      const { left, top } = normalTypeDom.getBoundingClientRect();
    },
    hideTypeSelect() {
      this.normalTypeSelectVisible = false;
    },
    handleSelectionChange(selectModel) {
      this.selectVertex = {};
      this.selectEdge = {};
      if (!selectModel.cells.length) {
        return;
      }

      const cell = selectModel.cells[0];

      if (cell.vertex) {
        this.selectVertex = cell;
        // console.log("selectVertex id:", this.selectVertex.data.id);
        console.log("selectVertex:", this.selectVertex.data);
        // console.log("style:",this.selectVertex.getStyle());
        // graph.setStyle(this.selectVertex, "fillColor=#B9E0A5;shadow=0;");
        if (cell.data.type !== "instance") {
          this.running_process_modal = true;
        }
        if (this.$refs.inputParam) {
          this.$refs.inputParam.blur();
        }
      } else {
        this.selectEdge = cell;
      }
    },
    ChangeEdgeStyle(key, value) {
      graph.setStyle(this.selectEdge, key, value);
    },
    _listenEvent() {
      // 监听自定义事件
      graph.addListener(mxEvent.NORMAL_TYPE_CLICKED, this.showNormalTypeSelect);
      graph.addListener(mxEvent.VERTEX_START_MOVE, this.hideTypeSelect);

      // 监听 mxGraph 事件
      const mxGraphSelectionModel = graph.getSelectionModel();
      mxGraphSelectionModel.addListener(
        mxEvent.CHANGE,
        this.handleSelectionChange
      );

      const vm = this;
      graph.addListener(mxEvent.MOVE_CELLS, (sender, evt) => {
        const cell = evt.properties.cells[0];
        const position = Graph.getCellPosition(cell);
        // setTimeout(() => {
        //   vm.$Message.info(`节点被移动到 ${JSON.stringify(position)}`);
        // }, 1000);
      });

      graph.addListener(mxEvent.CELLS_ADDED, (sender, evt) => {
        const cell = evt.properties.cells[0];
        if (graph.isPart(cell)) {
          return;
        }

        if (cell.vertex) {
          let cellData = cell.data;
          let data = null;
          if (cellData.type === "instance") {
            // data = this.instances[cellData.dataIndex];
            let instanceInfo = this.getObjByid(
              this.taskInfo.targetInstanceList,
              cell.data.instanceId,
              "instance"
            );
            let cmpDataInfo = this.getObjByid(
              this.cmpDataList,
              cell.data.cmpDataId,
              "cmpData"
            );
            // cell.data.instanceName = instanceInfo.name;
            // cell.data.cmpDataName = cmpDataInfo.name;
            cell.setValue(instanceInfo.name + "_" + cmpDataInfo.name);
          } else if (cellData.type === "dataProcess") {
            data = this.dataProcess[cellData.dataIndex];
            cell.setValue(data.name);
            //! 此处需要深拷贝
            let cellData_copyed = _.cloneDeep(data);
            //! 一个方法可以使用多次所以不能公用一个 oid，此处给 methodId 加后缀
            cellData_copyed.oid = cellData_copyed.oid + "_" + cell.data.id;
            Object.assign(cell.data, cellData_copyed);
          } else {
            data = this.comparisonMethods[cellData.dataIndex];
            cell.setValue(data.name);
            //! 此处需要深拷贝
            let cellData_copyed = _.cloneDeep(data);
            //! 一个方法可以使用多次所以不能公用一个oid，此处给 oid 加后缀
            cellData_copyed.oid = cellData_copyed.oid + "_" + cell.data.id;
            Object.assign(cell.data, cellData_copyed);
          }

          console.log(JSON.stringify(cell.data));
          // vm.$Message.info("添加了一个节点");
        } else if (cell.edge) {
          // vm.$Message.info("添加了一条线");
        }
      });

      graph.addListener(mxEvent.LABEL_CHANGED, (sender, evt) => {
        // vm.$Message.info(`内容改变为：${evt.getProperty("value")}`);
      });

      graph.addListener(mxEvent.CONNECT_CELL, (sender, evt) => {
        // vm.$Message.info(`改变了连线`);
      });
    },
    downData(output) {
      // this.$Message.info(event);
      // window.open(path, "_self");
      let fileName = output.fileName + "." + output.suffix;
      let reqJson = { dataUrl: output.downloadUrl, fileName: fileName };
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
      return function (metricsColumn) {
        if (metricsColumn) {
          return metricsColumn.filter(col => {
            return col.key != "metric" && col.slot != "check";
          });
        }
      };
    },
    getExcuteStatus() {
      return function (status) {
        return status === "1"
          ? "Success"
          : status === "0"
            ? "Running"
            : status === "2"
              ? "Abort"
              : "Failed";
      };
    },
    getColorofStatus() {
      return function (status) {
        return status === "1"
          ? "rgb(10, 171, 67)"
          : status === "0"
            ? "coral"
            : status === "2"
              ? "#efae39"
              : "#f00";
      };
    },

    getRandomProcess() {
      return _.random(30, 99, false);
    },
    getMethodRecordBySelectVertex() {
      let record = {};
      this.cmpTaskModelList.forEach(cmpTaskModel => {
        let vertexData = this.selectVertex.data;
        if (vertexData.type === "comparison") {
          //* 更新 cmpMethodInfo 信息
          let cmpMethodInfoIndex = _.findIndex(cmpTaskModel.cmpMethodInfoList, function (o) { return o.methodId === vertexData.oid });
          if (cmpMethodInfoIndex >= 0) {
            record = cmpTaskModel.cmpMethodInfoList[cmpMethodInfoIndex];
          }
        } else if (vertexData.type === "normal") {
          let dpmInfoIndex = _.findIndex(cmpTaskModel.dataProcessMethodInfoList, function (o) { return o.methodId === vertexData.oid });
          if (dpmInfoIndex >= 0) {
            record = cmpTaskModel.dataProcessMethodInfoList[dpmInfoIndex];
          }
        }
      });
      return record;
    },

  },
  mounted() {
    initGraph();
    // this.changeTab();
    this._listenEvent();
  },
  beforeDestroy() {
    // window.removeEventListener("resize", this.getGraphHeight);
    clearInterval(this.timer);
    destroyGraph();
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
  width: 1000px;
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

#modelInfo_box {
  margin-bottom: 50px;
}

#taskConfig_box {
  margin-bottom: 50px;
}

#graphContainer {
  position: relative;
  overflow: hidden;
  width: 100%;
  height: 500px;
  background: #eee url("../../assets/images/comparison/grid.gif") 0 0 repeat;
  border-radius: 4px;
}
</style>