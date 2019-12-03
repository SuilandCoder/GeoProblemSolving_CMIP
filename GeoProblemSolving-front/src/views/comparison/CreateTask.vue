<template>
  <div style="overflow:auto">
    <h2 style="text-align:center;margin:20px;">Create Comparison Task</h2>
    <Steps :current="currentStep" style="width:800px;margin-left:38%">
      <Step title="Step 1">
      </Step>
      <Step title="Step 2">
      </Step>
    </Steps>
    <div v-show="currentStep===0" class="box">
      <Form ref="step1Form" :model="taskInfo" style="margin-top:30px;" :rules="step1Rules">
        <FormItem prop="name" label="Task Name" :label-width="150">
          <Input v-model="taskInfo.name" style="width: 300px" placeholder="Enter task name" />
        </FormItem>
        <FormItem prop="description" label="Description" :label-width="150">
          <div>
            <Input type="textarea" v-model="taskInfo.description" placeholder="Enter description about this task"
              style="width: 500px" />
          </div>
        </FormItem>

        <FormItem prop="targetInstanceList" label="Target Instance" :label-width="150">
          <Transfer :data="getTransferInstanceList" :target-keys="targetInstanceKeys" :render-format="renderTransfer"
            @on-change="handleTransferChange"></Transfer>
        </FormItem>

        <FormItem prop="checkedMetrics" label="Target Metrics" :label-width="150">
          <Table border :columns="metricsColumn" :data="metricsTableData">
            <template slot-scope="{row}" v-for="instanceCol of getInstanceCol" :slot="instanceCol.title">
              <Icon v-if="row[instanceCol.title]" type="md-checkmark" color="rgb(10, 171, 67)" style="{color:green}" />
              <Icon v-else type="md-close" color="#f00" />
            </template>
            <template slot-scope="{row,index}" slot="check">
              <input type="checkbox" :checked="row.checked" @click="checkedChange_single(index)">
            </template>
          </Table>
        </FormItem>
      </Form>

      <Button class="stepBtn" type="primary" v-if="currentStep>0" @click="previous">Previous</Button>
      <Button class="stepBtn" type="primary" @click="next">Next step</Button>
    </div>

    <div v-show="currentStep===1" style="margin-top:20px;">
      <!-- <h2 class="title">Title</h2> -->
      <Divider style="margin-top:5px;margin-bottom:5px;" dashed />
      <div class="split" :style="{height:splitHeight}">
        <Split v-model="split">
          <div slot="left" class="split-pane">
            <Tabs size="small" @on-click="changeTab" :value="leftTabValue">
              <!-- <TabPane label="Metrics" name="metrics">
                <CellGroup>
                  <Cell class="element-item" v-for="(metricsInfo,idx) of taskInfo.checkedMetrics"
                    :key="metricsInfo.metric.name" type="instance" :id="idx" :title="metricsInfo.metric.alias" />
                </CellGroup>
              </TabPane> -->
              <TabPane label="Data Process" name="dataProcess">
                <Button style="float:right;margin-bottom:5px;margin-right:10px;" size="small"
                  @click="createDPM('normal')">Create</Button>
                <CellGroup>
                  <Cell class="element-item" v-for="(dp,idx) of dataProcess" :key="dp.oid" type="dataProcess"
                    :title="dp.name" :id="idx" />
                </CellGroup>
              </TabPane>
              <TabPane label="Comparison Methods" name="cmpMethods">
                <Button style="float:right;margin-bottom:5px;margin-right:10px;" size="small"
                  @click="createDPM('comparison')">Create</Button>
                <CellGroup>
                  <Cell class="element-item" v-for="(cm,idx) of comparisonMethods" :key="cm.oid"
                    type="comparisonMethods" :title="cm.name" :id="idx" />
                </CellGroup>
              </TabPane>
            </Tabs>
          </div>
          <div slot="right" class="split-pane">
            <Split v-model="split2" :style="{height:splitHeight}">
              <div slot="left">
                <div id="graphContainer" :style="{height:splitHeight}" @mousemove="mMove($event)"
                  @mousedown="mDown($event)" @mouseup="mUp($event)">
                  <ButtonGroup size="small" style="position:absolute;margin-left:10px;margin-top:10px;">
                    <!-- <Button @click="importXML">import</Button> -->
                    <Button @click="delCell"
                      :disabled="_.isEmpty(selectVertex) && _.isEmpty(selectEdge)">Delete</Button>
                  </ButtonGroup>
                  <!-- <Button style="position:absolute; bottom:5px; right:5px;" @click.stop="saveTask">Save</Button> -->
                  <!-- <Button style="position:absolute; bottom:5px; right:5px;">Save</Button> -->
                  <div id="container" style="background-color:#31676f;" :style="mouseBoxStyle"></div>
                </div>
              </div>
              <div slot='right' ref="right_info">
                <div v-if="selectVertex.data&& selectVertex.data.type==='instance'"
                  :style="{'margin-left':'20px','margin-right':'20px','overflow':'auto',height:splitHeight}">
                  <div style="margin-top:10px;">
                    <!-- <h3>Method Name:</h3> -->
                    <h2 style="display:block;color:cadetblue;">{{getInstance(selectVertex.data.instanceId).name}}</h2>
                  </div>

                  <div style="margin-top:10px;">
                    <h3>Description:</h3>
                    <p style="margin:10px;">{{getInstance(selectVertex.data.instanceId).description}}</p>
                  </div>
                </div>
                <div v-if="selectVertex.data&& selectVertex.data.type!=='instance'"
                  :style="{'margin-left':'20px','overflow':'auto',height:splitHeight}">
                  <div style="margin-top:10px;">
                    <!-- <h3>Method Name:</h3> -->
                    <h2 style="display:block;color:cadetblue;">{{selectVertex.data.name}}</h2>
                  </div>

                  <div style="margin-top:10px;">
                    <h3>Description:</h3>
                    <p style="margin:10px;">{{selectVertex.data.desc}}</p>
                  </div>

                  <div style="margin-top:10px;">
                    <h3>Params:</h3>
                  </div>
                  <Form v-if="hackReset">
                    <FormItem v-for="(param,index) in selectVertex.data.parameterList" :key="index" :label="param.name"
                      :label-width="100">
                      <!-- <Input ref="inputParam" v-model="param.value" :placeholder="param.type" style="width:100px" /> -->
                      <DatePicker v-if="param.type==='date'" v-model="param.value" type="date"
                        :start-date="new Date(1982, 0, 1)" :options="model_time_32" placeholder="1982-2013" size="small"
                        style="width:120px;margin-left:10px;"></DatePicker>
                      <RadioGroup v-else-if="param.type==='time_unit'" v-model="param.value">
                        <!-- <Radio v-for="type of itemTypeList" :key="type.name" :label="type.label">{{type.name}}</Radio> -->
                        <Radio label="D" border>day</Radio>
                        <Radio label="M" border>month</Radio>
                        <Radio label="Y" border>year</Radio>
                      </RadioGroup>
                      <input v-else v-model="param.value" :placeholder="param.type"
                        style="width:100px;height:25px;margin-left:10px;" />
                    </FormItem>
                  </Form>
                </div>
              </div>
            </Split>
          </div>
        </Split>
      </div>
      <Divider style="margin-top:10px;margin-bottom:10px;" dashed />
      <Button class="stepBtn" type="primary" v-if="currentStep===0" @click="next">Next step</Button>
      <Button class="stepBtn" type="primary" v-if="currentStep===1" @click="createTask">Create</Button>
      <Button class="stepBtn" type="primary" v-if="currentStep===1" @click="previous">Previous</Button>
    </div>
  </div>
</template>
<script>
import mxgraph from "@/utils/comparison/graph/index";
import { genGraph, destroyGraph, Graph } from "@/utils/comparison/graph/Graph";

const {
  mxGraph,
  mxOutline,
  mxEvent,
  mxCell,
  mxGeometry,
  mxUtils,
  mxEventObject,
  mxConnectionHandler,
  mxStencilRegistry
  // mxGraphHandler
} = mxgraph;



Date.prototype.format = function (fmt) { //author: meizz
  var o = {
    "M+": this.getMonth() + 1, //月份
    "d+": this.getDate(), //日
    "h+": this.getHours(), //小时
    "m+": this.getMinutes(), //分
    "s+": this.getSeconds(), //秒
    "q+": Math.floor((this.getMonth() + 3) / 3), //季度
    "S": this.getMilliseconds() //毫秒
  };
  if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
  for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
  return fmt;
}

Object.assign(mxEvent, {
  NORMAL_TYPE_CLICKED: "NORMAL_TYPE_CLICKED"
});

let px = "";
let py = "";

let graph = null;
let outline = null;
let idSeed = 0;
let ellipse = "ellipse;whiteSpace=wrap;html=1;";
let rounded = "rounded;whiteSpace=wrap;html=1;rounded=1;shadow=0;comic=0;strokeColor=#000000;fillColor=#FFE599;";
let hexagon = "shape=hexagon;whiteSpace=wrap;html=1;rounded=0;shadow=0;comic=0;fontSize=18;strokeColor=#000000;fillColor=#F8CECC;";
// let rhombus = "rhombus;whiteSpace=wrap;html=1;";// 菱形
let data_Cell = "shape=Parallelepiped;whiteSpace=wrap;html=1;fillColor=#ffffff;strokeColor=#000000;strokeWidth=2"
// const vm = this;

const insertVertex = (type, idx, target, x, y, instance) => {
  var parent = graph.getDefaultParent();
  // graph.insertVertex(parent, null, '演员', 50, 300, 150, 150, "shape=parallelogram;perimeter=ellipsePerimeter;");
  const id = idx;
  let nodeRootVertex = null;
  if (type === "instance") {
    nodeRootVertex = new mxCell("", new mxGeometry(0, 0, 130, 85), "img;whiteSpace=wrap;html=1;fillColor=#ffffff;strokeColor=#000000;strokeWidth=2");
  } else if (type === "dataProcess") {
    nodeRootVertex = new mxCell("", new mxGeometry(0, 0, 120, 80), hexagon);
  } else {
    nodeRootVertex = new mxCell("", new mxGeometry(0, 0, 120, 80), rounded);
  }

  nodeRootVertex.vertex = true;
  // 自定义的业务数据放在 data 属性
  idSeed++;

  nodeRootVertex.data = {
    id: idSeed,
    type: type,
    dataIndex: id
  };

  if (type === "instance") {
    Object.assign(nodeRootVertex.data, instance);
  }

  const cells = graph.importCells([nodeRootVertex], x, y, target);
  if (cells != null && cells.length > 0) {
    graph.setSelectionCells(cells);
  }
};

const makeDraggable = (sourceEles, checkedMetrics) => {
  const dropValidate = function (evt) {
    const x = mxEvent.getClientX(evt);
    const y = mxEvent.getClientY(evt);
    // console.log("x:", x);
    // console.log("y:", y);
    // 获取 x,y 所在的元素
    const elt = document.elementFromPoint(x, y);
    // 如果鼠标落在graph容器
    if (mxUtils.isAncestorNode(graph.container, elt)) {
      return graph;
    }
    // 鼠标落在其他地方
    return null;
  };

  // drop成功后新建一个节点
  const dropSuccessCb = function (_graph, evt, target, x, y) {
    // console.log("这里放一个节点");
    let type = this.element.getAttribute("type");
    let idx = this.element.getAttribute("id");
    if (type === "instance") {
      let relatedInstances = checkedMetrics[idx].releatedInfo;
      relatedInstances.forEach((instance, index) => {
        insertVertex(type, idx, target, x, y + 100 * index, instance);
      });
    } else {
      insertVertex(type, idx, target, x, y, null);
    }
  };

  Array.from(sourceEles).forEach(ele => {
    const dragElt = document.createElement("img");
    // dragElt.setAttribute("src", ele.getAttribute("src"));
    dragElt.setAttribute("style", "width:120px;height:120px;");

    mxUtils.makeDraggable(
      ele,
      dropValidate,
      dropSuccessCb,
      dragElt,
      null,
      null,
      null,
      true
    );
  });
};

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

const setConnectValidation = () => {
  // 连接边校验
  mxGraph.prototype.isValidConnection = (source, target) => {
    /**
     * 校验规则：
     * 1. instance 只能作为 source
     * 2. comparison 只能作为 target
     */
    if (target.data.type === "instance" || source.data.type === "comparison") {
      return false;
    }

    return true;
  };
};

const initGraph = () => {
  graph = genGraph(document.getElementById("graphContainer"));

  // 将外元素拖拽进画布参考这个例子
  // https://github.com/jinzhanye/mxgraph-demos/blob/master/src/07.drag.html
  // makeDraggable(document.getElementsByClassName("element-item"));
  listenGraphEvent();
  setCursor();
  setConnectValidation();
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
    // 设置下一个路由的 meta
    if (to.name != "create-dataprocess-method") {
      from.meta.keepAlive = false; // 让 A 缓存，即不刷新
    }
    next();
  },
  beforeRouteEnter: (to, from, next) => {
    next(vm => {
      $.ajax({
        url: "/GeoProblemSolving/user/state",
        type: "GET",
        async: false,
        success: function (data) {
          if (!data) {
            vm.$store.commit("userLogout");
            vm.$router.push({ name: "Login" });
          }
        },
        error: function (err) {
          console.log("Get user state fail.");
        }
      });
    });
  },
  created() {
    window.addEventListener("resize", this.getGraphHeight);
    this.getGraphHeight();

    this.projectId = this.$route.params.id;
    this.taskInfo.projectId = this.projectId;
    //* 获取 instance 信息
    this.getInstanceList();
    //* 获取数据处理方法 和 对比方法；
    this.getDataProcessMethod();
  },
  data() {
    return {
      split: 0.2,
      split2: 0.7,
      splitHeight: "",
      // splitHeight: window.screen.availHeight - 300 + "px",
      graphBoxHeight: $(document).height() - 200 + "px",
      projectId: "",
      taskInfo: {
        projectId: this.projectId,
        name: "",
        description: "",
        userName: this.$store.getters.userName,
        userId: this.$store.getters.userId,
        targetInstanceList: [],
        checkedMetrics: [],
        computableModels: []
      },
      selectEdge: {},
      selectVertex: {},
      mouseBoxStyle: {},
      dataProcess: [],
      comparisonMethods: [],
      elementItem: [],
      currentStep: 0,
      instanceList: [],
      targetInstanceKeys: [],
      cmpDataList: [],
      metricList: [],
      metricsColumn: [],
      metricsTableData: [],
      metricsAllInfo: [],
      step1Rules: {
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
        ],
        targetInstanceList: [
          {
            required: true,
            message: "Please select instances"
          }
        ],
        checkedMetrics: [
          {
            required: true,
            message: "Please select metrics"
          }
        ]
      },
      leftTabValue: "dataProcess",
      model_time_32: {
        disabledDate(date) {
          // print(date);
          let time_1982 = new Date("1982-01-01 00:00:00");
          let time_2013 = new Date("2013-12-31 23:59:59");
          return date && (date < time_1982 || date > time_2013);
        }
      },
      startTime_1982: "1982-01-01",
      hackReset: true,
    };
  },
  methods: {
    renderTransfer(item) {
      return item.label;
    },
    handleTransferChange(newTargetKeys, direction, moveKeys) {
      this.targetInstanceKeys = newTargetKeys;
      this.metricsColumn = [];
      this.getMetricsInfo();
    },
    getInstanceList() {
      this.$api.cmp_instance
        .getInstanceList(this.projectId)
        .then(res => {
          this.instanceList = res;
        })
        .catch(err => {
          this.$Message.error(err);
        });
    },
    getMetricsInfo() {
      let vm = this;
      this.metricList = [];
      this.taskInfo.targetInstanceList = this.instanceList.filter(instance => {
        let index = _.indexOf(vm.targetInstanceKeys, instance.instanceId);
        return index >= 0;
      });
      if (this.taskInfo.targetInstanceList.length > 0) {
        this.createMetricsColumn();
      }
      // console.log(JSON.stringify(this.targetInstanceList));
      //* 求所有 instance 中 cmpDataList 的并集
      let cmpDataIdList = [];
      this.taskInfo.targetInstanceList.forEach(instance => {
        cmpDataIdList = _.union(cmpDataIdList, instance.cmpDataList);
      });
      // console.log("idList:", JSON.stringify(cmpDataIdList));
      this.getCmpDataList(cmpDataIdList);
    },
    getCmpDataList(idList) {
      this.$api.cmp_data
        .getDataResourceByIdList(idList)
        .then(res => {
          this.cmpDataList = res;
          let vm = this;
          res.forEach(item => {
            let index = _.findIndex(vm.metricList, item.metrics);
            if (index == -1) {
              vm.metricList.push(item.metrics);
            }
          });
          // console.log("metricList:", JSON.stringify(this.metricList));
          this.createMetricsDataInfo();
        })
        .catch(err => {
          this.$Message.error(err);
        });
    },
    createMetricsColumn() {
      this.metricsColumn = [
        { title: "Metrics", key: "metric", width: "100px", align: "center" }
      ];
      this.taskInfo.targetInstanceList.forEach(instance => {
        let item = {
          title: instance.name,
          slot: instance.name,
          align: "center"
        };
        this.metricsColumn.push(item);
      });
      let action = {
        title: "Check Metrics",
        slot: "check",
        width: "150",
        align: "center"
      };
      this.metricsColumn.push(action);
    },
    createMetricsDataInfo() {
      this.metricsTableData = [];
      this.metricsAllInfo = [];
      this.metricList.forEach(metric => {
        let item = { metric: metric.alias };
        let infoArr = this.getMetricInfo(metric);
        let obj = infoArr[0];
        Object.assign(item, obj);
        this.metricsTableData.push(item);
        this.metricsAllInfo.push(infoArr[1]);
      });
      // console.log("metricAllInfo:",JSON.stringify(this.metricsAllInfo));
    },
    getMetricInfo(metric) {
      let obj = {};
      let metricAllInfo = { metric: metric };
      let releatedInfo = [];
      this.taskInfo.targetInstanceList.forEach(instance => {
        let name = instance.name;
        let contain = false;
        instance.cmpDataList.forEach(cmpDataId => {
          let data = this.cmpDataList.filter(cmpData => {
            return cmpData.dataId === cmpDataId;
          });
          if (data.length > 0 && data[0].metrics.oid === metric.oid) {
            contain = true;
            let info = {
              instanceId: instance.instanceId,
              cmpDataId: data[0].dataId
            };
            releatedInfo.push(info);
          }
        });
        obj[name] = contain;
      });
      metricAllInfo.releatedInfo = releatedInfo;
      obj.checked = false;
      return [obj, metricAllInfo];
    },
    checkedChange(index) {
      this.taskInfo.checkedMetrics = [];
      this.metricsTableData[index].checked = !this.metricsTableData[index].checked;
      this.metricsTableData.forEach((data, index) => {
        if (data.checked) {
          this.taskInfo.checkedMetrics.push(this.metricsAllInfo[index]);
        }
      });
      // console.log("checkedMetrics:",JSON.stringify(this.checkedMetrics));
    },
    checkedChange_single(index) {
      this.taskInfo.checkedMetrics = [];
      this.metricsTableData.forEach(data => {
        data.checked = false;
      });
      this.metricsTableData[index].checked = !this.metricsTableData[index].checked;
      this.metricsTableData.forEach((data, index) => {
        if (data.checked) {
          this.taskInfo.checkedMetrics.push(this.metricsAllInfo[index]);
        }
      });
    },
    getDataProcessMethod() {
      this.$api.cmp_task
        .getDataProcessMethod("comparison")
        .then(res => {
          this.comparisonMethods = res;
        })
        .catch(err => {
          this.$Message.error(err);
        });
      this.$api.cmp_task
        .getDataProcessMethod("normal")
        .then(res => {
          this.dataProcess = res;
        })
        .catch(err => {
          this.$Message.error(err);
        });
    },
    changeTab() {
      let newItem = document.getElementsByClassName("element-item");
      let draggableItem = _.difference(newItem, this.elementItem);
      makeDraggable(draggableItem, this.taskInfo.checkedMetrics);
      this.elementItem = _.union(this.elementItem, draggableItem);
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
        this.hackReset = false;
        this.$nextTick(() => {
          this.hackReset = true;
        })
        this.selectVertex = cell;
        console.log("selectVertex id:", this.selectVertex.data.id);
        // console.log("selectVertex",this.selectVertex);
        if (this.$refs.inputParam) {
          this.$refs.inputParam.blur();
        }
        // this.$refs["right_info"].$forceUpdate();
      } else {
        this.selectEdge = cell;
      }
    },
    ChangeEdgeStyle(key, value) {
      graph.setStyle(this.selectEdge, key, value);
    },
    delCell() {
      if (!_.isEmpty(this.selectVertex)) {
        graph.deleteSubtree(this.selectVertex);
      } else {
        graph.removeCells([this.selectEdge]);
      }
    },
    removeAllCells() {
      graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
      this.elementItem = [];
    },
    createTask() {
      // console.log("taskInfo:", this.taskInfo);

      let taskReady = this.saveTask();
      // let formData = new FormData();
      // formData.set("taskInfo", JSON.stringify(this.taskInfo));
      if (taskReady) {
        this.$api.cmp_task
          .createTask(JSON.stringify(this.taskInfo))
          .then(res => {
            let recordId = res.recordId;
            this.$router.push({
              path: `/cmp-task-record/${recordId}`
            });
          })
          .catch(err => {
            this.$Message.error(err);
          });
      }
    },
    saveTask(event) {
      const xml = graph.exportModelXML();
      // console.log(xml);
      console.log("model:", graph.getModel());
      let computableModelInfo = this.parseGraphModel(graph.getModel());
      if (!computableModelInfo) {
        return false;
      }
      computableModelInfo.graphXML = xml;
      //* push 前先判断computableModels中是否已有该对象，如有则覆盖。
      let index = _.findIndex(this.taskInfo.computableModels, function (model) {
        return model.metricId === computableModelInfo.metricId;
      });
      if (index >= 0) {
        this.taskInfo.computableModels.splice(index, 1);
      }
      this.taskInfo.computableModels.push(computableModelInfo);
      //* 清空画布并跳转第一个tab页；
      graph.removeCells(graph.getChildVertices(graph.getDefaultParent()));
      return true;
      // this.leftTabValue = "metrics";
    },
    parseGraphModel(model) {
      let cells = model.cells;
      let computableModelInfo = {};
      let cmpMethodList = [];
      let dataProcessMethodList = [];
      let metricId = "";
      let metricName = "";
      for (let i in cells) {
        let cell = cells[i];
        if (cell.vertex && cell.data.type === "comparison") {
          let cmpMethodInfo = {};
          let inputList = [];
          cmpMethodInfo.methodId = cell.data.oid;
          cmpMethodInfo.methodSourceId = cell.data.scriptSourceId;
          //* 判断数据处理方法参数是否设置
          for (let param of cell.data.parameterList) {
            if (!param.optional && !param.value) {
              this.$Message.error(
                "Please enter the parameters of the comparison method."
              );
              return;
            }
          }
          for (let j in cell.edges) {
            let edge = cell.edges[j];
            let inputInfo = {};
            let source = edge.source;
            if (source.data.type === "instance") {
              inputInfo.type = "instance";
              inputInfo.instanceId = source.data.instanceId;
              inputInfo.cmpDataId = source.data.cmpDataId;
              let cmpDataInfo = this.getObjByid(
                this.cmpDataList,
                source.data.cmpDataId,
                "cmpData"
              );
              inputInfo.dataStoreId = cmpDataInfo.dcSourceStoreId;
            } else if (source.data.type === "normal") {
              inputInfo.type = "normal";
              inputInfo.oid = source.data.oid;
            }
            inputList.push(inputInfo);
          }
          //* 对比方法最少有两个输入；
          if (inputList.length < 2) {
            this.$Message.error(
              "The comparison method must have at least two inputs."
            );
            return;
          }

          cmpMethodInfo.params = _.cloneDeep(cell.data.parameterList);
          cmpMethodInfo.input = cell.data.inputList[0];
          cmpMethodInfo.output = cell.data.outputList[0];
          cmpMethodInfo.dependencyLibs = cell.data.dependencyItemList;
          cmpMethodInfo.interpretor = cell.data.interpretor;
          // Object.assign(dpmInfo.params,cell.data.parameterList);
          // cell.data.parameterList.forEach(param=>{
          //   param.value = "";
          // });

          // cmpMethodInfo.params = cell.data.parameterList;
          cmpMethodInfo.inputList = inputList;
          cmpMethodList.push(cmpMethodInfo);
        } else if (cell.vertex && cell.data.type === "normal") {
          let dpmInfo = {};
          let inputList = [];
          dpmInfo.methodId = cell.data.oid;
          dpmInfo.methodSourceId = cell.data.scriptSourceId;
          //* 判断数据处理方法参数是否设置
          for (let param of cell.data.parameterList) {
            //* 将 {} 转换为 ""
            if (JSON.stringify(param.value) === '{}' || param.value === null) {
              param.value = "";
            }
            //* 将date类型的数据格式化为：YYYY-MM-DD
            if (param.type === 'date' && param.value) {
              param.value = new Date(param.value).format('yyyy-MM-dd');
            }
            if (!param.optional && !param.value) {
              this.$Message.error(
                "Please enter the parameters of the data processing method."
              );
              return;
            }
          }
          for (let j in cell.edges) {
            let edge = cell.edges[j];
            let source = edge.source;
            //* 排除以自身为 source 的边
            if (source.id != cell.id) {
              let inputInfo = {};
              if (source.data.type === "instance") {
                inputInfo.type = "instance";
                inputInfo.instanceId = source.data.instanceId;
                inputInfo.cmpDataId = source.data.cmpDataId;
                let cmpDataInfo = this.getObjByid(
                  this.cmpDataList,
                  source.data.cmpDataId,
                  "cmpData"
                );
                inputInfo.dataStoreId = cmpDataInfo.dcSourceStoreId;
              } else if (source.data.type === "normal") {
                inputInfo.type = "normal";
                inputInfo.oid = source.data.oid;
              }
              inputList.push(inputInfo);
            }
          }

          //* 数据处理方法最少有一个输入
          if (inputList.length < 1) {
            this.$Message.error(
              "The data processing method must have one inputs."
            );
            return;
          }
          dpmInfo.params = _.cloneDeep(cell.data.parameterList);
          dpmInfo.input = cell.data.inputList[0];
          dpmInfo.output = cell.data.outputList[0];
          dpmInfo.dependencyLibs = cell.data.dependencyItemList;
          dpmInfo.interpretor = cell.data.interpretor;
          // Object.assign(dpmInfo.params,cell.data.parameterList);
          // cell.data.parameterList.forEach(param=>{
          //   param.value = "";
          // });
          dpmInfo.inputList = inputList;
          dataProcessMethodList.push(dpmInfo);
        } else if (cell.vertex && cell.data.type === "instance") {
          let cmpDataInfo = this.getObjByid(
            this.cmpDataList,
            cell.data.cmpDataId,
            "cmpData"
          );
          let metricId_new = cmpDataInfo.metrics.oid;
          if (metricId != "" && metricId !== metricId_new) {
            this.$Message.error("Only one metric can be configured at a time");
            return;
          }
          metricId = metricId_new;
          metricName = cmpDataInfo.metrics.name;
        }
      }
      //* 对比方法不能没有
      if (cmpMethodList.length < 1) {
        this.$Message.error("Please choose a comparison method");
        return;
      }

      Object.keys(cells).forEach(key => {
        let cell = cells[key];
        if (
          cell.vertex &&
          (cell.data.type === "comparison" || cell.data.type === "normal")
        )
          cell.data.parameterList.forEach(param => {
            param.value = "";
          });
      });
      //* for in 第二次循环时 每次都是最后一个元素！！！
      // for (let k in cells) {
      //   let cell = cells[k];
      //   if (
      //     cell.vertex &&
      //     (cell.data.type === "comparison" && cell.data.type === "normal")
      //   )
      //     cell.data.parameterList.forEach(param => {
      //       param.value = "";
      //     });
      // }

      computableModelInfo.metricId = metricId;
      computableModelInfo.metricName = metricName;
      computableModelInfo.cmpMethodList = cmpMethodList;
      computableModelInfo.dataProcessMethodList = dataProcessMethodList;
      return computableModelInfo;
    },
    previous() {
      if (this.currentStep > 0) {
        this.currentStep -= 1;
      }
    },
    next() {
      if (this.currentStep === 0) {
        this.$refs["step1Form"].validate(valid => {
          if (valid) {
            this.currentStep += 1;
            //* 将 instance 节点绘制到画布上
            let relatedInstances = this.taskInfo.checkedMetrics[0].releatedInfo;
            relatedInstances.forEach((instance, index) => {
              insertVertex("instance", 0, null, 200, 50 + 100 * index, instance);
            });
            this.changeTab();
          }
        });
        // this.changeTab();
      } else if (this.currentStep === 1) {
        this.currentStep += 1;
      } else {
      }
    },
    importXML(txt) {
      graph.importModelXML(txt);
    },
    getGraphHeight() {
      this.splitHeight = window.screen.availHeight - 300 + "px";
    },
    createDPM(type) {
      this.$router.push({
        path: `/create-dataprocess-method`,
        name: "create-dataprocess-method"
      });
      sessionStorage.setItem("dpmType", type);
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
    mDown(e) {
      px = e.pageX;
      py = e.pageY;
      console.log("父组件捕获");
      var el = e.target;
      // if (el.innerHTML === 'Save') {
      //   //do something
      //   this.saveTask(el);
      // }
    },
    mMove(e) {
      if (px == "" || py == "") {
        return;
      }
      // $("#graphContainer").unbind("mouseover");
      var pxx = e.pageX;
      var pyy = e.pageY;
      // console.log("pxx:", pxx);
      // console.log("pyy:", pyy);
      var h = pyy - py;
      var w = pxx - px;
      //画出矩形选中框
      if (h < 0 && w >= 0) {
        this.mouseBoxStyle = {
          height: -h + "px",
          width: w + "px",
          position: "fixed",
          left: px + "px",
          top: pyy + "px",
          opacity: "0.2",
          border: "1px dashed #000"
        };
      } else if (h >= 0 && w < 0) {
        this.mouseBoxStyle = {
          height: h + "px",
          width: -w + "px",
          position: "fixed",
          left: pxx + "px",
          top: py + "px",
          opacity: "0.2",
          border: "1px dashed #000"
        };
      } else if (h < 0 && w < 0) {
        this.mouseBoxStyle = {
          height: -h + "px",
          width: -w + "px",
          position: "fixed",
          left: pxx + "px",
          top: pyy + "px",
          opacity: "0.2",
          border: "1px dashed #000"
        };
      } else {
        this.mouseBoxStyle = {
          height: h + "px",
          width: w + "px",
          position: "fixed",
          left: px + "px",
          top: py + "px",
          opacity: "0.2",
          border: "1px dashed #000"
        };
      }
    },
    mUp(e) {
      px = "";
      py = "";
      this.mouseBoxStyle = { height: "0", width: "0", border: "0px" };
    }
  },
  computed: {
    selectEdgeStyle() {
      if (!_.isEmpty(this.selectEdge)) {
        return graph.getCellStyle(this.selectEdge);
      }
      return {};
    },
    getTransferInstanceList() {
      return this.instanceList.map(instance => {
        instance.key = instance.instanceId;
        instance.label = instance.name;
        return instance;
      });
    },
    getInstanceCol() {
      return this.metricsColumn.filter(col => {
        return col.key != "metric" && col.slot != "check";
      });
    },
    getInstance() {
      return function (instanceId) {
        let result = this.taskInfo.targetInstanceList.filter(instance => {
          return instance.instanceId === instanceId;
        });
        return result[0];
      }
    }
  },
  mounted() {
    initGraph();
    // this.changeTab();
    this._listenEvent();
    let taskRecord = this.$route.params.taskRecord;
    if (taskRecord) {
      console.log("taskRecord:", taskRecord);
      this.taskInfo.name = taskRecord.name;
      this.taskInfo.description = taskRecord.desc;
      this.taskInfo.targetInstanceList = taskRecord.instanceList;
      this.taskInfo.checkedMetrics = taskRecord.metrics;
      this.taskInfo.computableModels = taskRecord.cmpTaskModelList;
      this.currentStep = 1;
      graph.importModelXML(this.taskInfo.computableModels[0].graphXML);

      //* 求所有 instance 中 cmpDataList 的并集
      let cmpDataIdList = [];
      this.taskInfo.targetInstanceList.forEach(instance => {
        cmpDataIdList = _.union(cmpDataIdList, instance.cmpDataList);
      });
      // console.log("idList:", JSON.stringify(cmpDataIdList));
      this.getCmpDataList(cmpDataIdList);
    }
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.getGraphHeight);
    destroyGraph();
  }
};
</script>
<style scoped>
.title {
  text-align: center;
  margin-top: 20px;
  margin-bottom: 20px;
}
.split {
  width: 100%;
  /* height: 700px; */
  margin-left: 10px;
  margin-right: 10px;
  /* border-bottom: 1px solid #000; */
}

#graphContainer {
  position: relative;
  overflow: hidden;
  width: 100%;
  background: #eee url("../../assets/images/comparison/grid.gif") 0 0 repeat;
  border-radius: 4px;
}

.element-item {
  cursor: pointer;
  font-family: PingF;
}

#toolBar {
  background: #eee;
  padding-left: 10px;
  border-radius: 4px;
  margin-bottom: 10px;
}
.box {
  width: 800px;
  margin-left: auto;
  margin-right: auto;
}
.stepBtn {
  float: right;
  margin-right: 20px;
}
</style>