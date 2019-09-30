<template>
  <div>
    <h2 class="title">Title</h2>
    <Divider style="margin-top:5px;margin-bottom:5px;" dashed />
    <div class="split" :style="{height:splitHeight}">
      <Split v-model="split">
        <div slot="left" class="split-pane">
          <Tabs value="instances" size="small">
            <TabPane label="Instances" name="instances">
              <ul>
                <li class="element-item" v-for="(instance,idx) of instances" :key="instance.name" type="instance"
                  :id="idx">{{instance.name}}
                </li>
              </ul>
            </TabPane>
            <TabPane label="Data Process" name="dataProcess">
              <ul>
                <li class="element-item" v-for="dp of dataProcess" :key="dp.name" type="dataProcess">{{dp.name}}</li>
              </ul>
            </TabPane>
            <TabPane label="Comparison Methods" name="cmpMethods">
              <ul>
                <li class="element-item" v-for="cm of comparisonMethods" :key="cm.name" type="comparisonMethods">
                  {{cm.name}}
                </li>
              </ul>
            </TabPane>
          </Tabs>
        </div>
        <div slot="right" class="split-pane">
          <Split v-model="split2" :style="{height:splitHeight}">
            <div slot="left">
              <div id="graphContainer" :style="{height:splitHeight}" @mousemove="mMove($event)"
                @mousedown="mDown($event)" @mouseup="mUp($event)">
                <ButtonGroup size="small" style="position:absolute;margin-left:10px;margin-top:10px;">
                  <Button @click="importXML">import</Button>
                  <Button @click="delCell" :disabled="_.isEmpty(selectVertex) && _.isEmpty(selectEdge)">Delete</Button>
                </ButtonGroup>
                <Button style="position:absolute; bottom:5px; right:5px;"  @click.stop.prevent="createTask">Create</Button>
                <div id="container" style="background-color:#31676f;" :style="mouseBoxStyle"></div>
              </div>
            </div>
            <div slot='right'>
              <div v-if="selectVertex.data" style="margin-left:20px">
                <span>{{selectVertex.data.name}}</span>
                <p>{{selectVertex.data.desc}}</p>
              </div>
            </div>
          </Split>
        </div>
      </Split>
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
// const vm = this;

const insertVertex = (dom, target, x, y) => {
  const type = dom.getAttribute("type");
  const id = Number(dom.getAttribute("id"));
  let nodeRootVertex = null;
  if (type === "instance") {
    nodeRootVertex = new mxCell("", new mxGeometry(0, 0, 120, 80), ellipse);
  } else if (type === "dataProcess") {
    nodeRootVertex = new mxCell("", new mxGeometry(0, 0, 120, 80), rhombus);
  } else {
    nodeRootVertex = new mxCell("", new mxGeometry(0, 0, 120, 80), rounded);
  }

  nodeRootVertex.vertex = true;
  // 自定义的业务数据放在 data 属性
  // idSeed++;
  nodeRootVertex.data = {
    id: idSeed,
    type: type,
    dataIndex: id
  };

  const cells = graph.importCells([nodeRootVertex], x, y, target);
  if (cells != null && cells.length > 0) {
    graph.setSelectionCells(cells);
  }
};

const makeDraggable = sourceEles => {
  const dropValidate = function(evt) {
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
  const dropSuccessCb = function(_graph, evt, target, x, y) {
    console.log("这里放一个节点");
    insertVertex(this.element, target, x, y);
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
  graph.getCursorForCell = function(...args) {
    const [cell] = args;
    return cell.style.includes("normalType")
      ? "pointer"
      : oldGetCursorForCell.apply(this, args);
  };
};

const setConnectValidation = () => {
  // 连接边校验
  mxGraph.prototype.isValidConnection = (source, target) => {
    // const sourceElementId = source.data.element.id;
    // const targetElementId = target.data.element.id;
    // 如果源点是智爷，终点必须是 皮卡丘 或 我是皮卡丘的超级超级进化
    // if (sourceElementId === 1) {
    //   return targetElementId === 2 || targetElementId === 3;
    // }

    // // 如果终点是智爷同理
    // if (targetElementId === 1) {
    //   return sourceElementId === 2 || sourceElementId === 3;
    // }

    return true;
  };
};

const initGraph = () => {
  graph = genGraph(document.getElementById("graphContainer"));

  // 将外元素拖拽进画布参考这个例子
  // https://github.com/jinzhanye/mxgraph-demos/blob/master/src/07.drag.html
  makeDraggable(document.getElementsByClassName("element-item"));
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
  created() {
    window.addEventListener("resize", this.getGraphHeight);
    this.getGraphHeight();
  },
  data() {
    return {
      split: 0.2,
      split2: 0.8,
      splitHeight: $(document).height() - 200 + "px",
      // splitHeight: window.screen.availHeight - 300 + "px",
      graphBoxHeight: $(document).height() - 230 + "px",
      // elements,
      selectEdge: {},
      selectVertex: {},
      mouseBoxStyle: {},
      instances: [
        {
          name: "实例1",
          desc: "asdfadf"
        },
        {
          name: "实例2",
          desc: "asdgasowieh"
        },
        {
          name: "实例3",
          desc: "asgoiklnninoi"
        }
      ],
      dataProcess: [
        {
          name: "数据处理方法1",
          desc: "asdfadf"
        },
        {
          name: "数据处理方法2",
          desc: "asdgasowieh"
        },
        {
          name: "数据处理方法3",
          desc: "asgoiklnninoi"
        }
      ],
      comparisonMethods: [
        {
          name: "对比方法1",
          desc: "asdfadf"
        },
        {
          name: "对比方法2",
          desc: "asdgasowieh"
        },
        {
          name: "对比方法3",
          desc: "asgoiklnninoi"
        }
      ]
    };
  },
  methods: {
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
    createTask(event) {
      // event.stopPropagation();
      const xml = graph.exportModelXML();
      console.log(xml);
      console.log("mode:", graph.getModel());
    },
    importXML() {
      let txt = `<mxGraphModel>
  <root>
    <mxCell id="0"/>
    <mxCell id="1" parent="0"/>
    <mxCell id="2" value="实例1" style="ellipse;whiteSpace=wrap;html=1;" vertex="1" data="{&quot;id&quot;:0,&quot;type&quot;:&quot;instance&quot;,&quot;dataIndex&quot;:0,&quot;name&quot;:&quot;实例1&quot;,&quot;desc&quot;:&quot;asdfadf&quot;}" parent="1">
      <mxGeometry x="60" y="80" width="120" height="80" as="geometry"/>
    </mxCell>
    <mxCell id="3" value="数据处理方法1" style="rhombus;whiteSpace=wrap;html=1;" vertex="1" data="{&quot;id&quot;:0,&quot;type&quot;:&quot;dataProcess&quot;,&quot;dataIndex&quot;:0,&quot;name&quot;:&quot;数据处理方法1&quot;,&quot;desc&quot;:&quot;asdfadf&quot;}" parent="1">
      <mxGeometry x="320" y="80" width="120" height="80" as="geometry"/>
    </mxCell>
    <mxCell id="4" value="对比方法1" style="rounded=1;whiteSpace=wrap;html=1;" vertex="1" data="{&quot;id&quot;:0,&quot;type&quot;:&quot;comparisonMethods&quot;,&quot;dataIndex&quot;:0,&quot;name&quot;:&quot;对比方法1&quot;,&quot;desc&quot;:&quot;asdfadf&quot;}" parent="1">
      <mxGeometry x="610" y="160" width="120" height="80" as="geometry"/>
    </mxCell>
    <mxCell id="5" value="实例2" style="ellipse;whiteSpace=wrap;html=1;" vertex="1" data="{&quot;id&quot;:0,&quot;type&quot;:&quot;instance&quot;,&quot;dataIndex&quot;:1,&quot;name&quot;:&quot;实例2&quot;,&quot;desc&quot;:&quot;asdgasowieh&quot;}" parent="1">
      <mxGeometry x="60" y="230" width="120" height="80" as="geometry"/>
    </mxCell>
    <mxCell id="6" value="数据处理方法1" style="rhombus;whiteSpace=wrap;html=1;" vertex="1" data="{&quot;id&quot;:0,&quot;type&quot;:&quot;dataProcess&quot;,&quot;dataIndex&quot;:0,&quot;name&quot;:&quot;数据处理方法1&quot;,&quot;desc&quot;:&quot;asdfadf&quot;}" parent="1">
      <mxGeometry x="320" y="230" width="120" height="80" as="geometry"/>
    </mxCell>
    <mxCell id="7" style="exitX=1;exitY=0.5;exitDx=0;exitDy=0;entryX=0;entryY=0.5;entryDx=0;entryDy=0;" edge="1" parent="1" source="2" target="3">
      <mxGeometry relative="1" as="geometry"/>
    </mxCell>
    <mxCell id="8" style="exitX=1;exitY=0.5;exitDx=0;exitDy=0;entryX=0;entryY=0.5;entryDx=0;entryDy=0;" edge="1" parent="1" source="5" target="6">
      <mxGeometry relative="1" as="geometry"/>
    </mxCell>
    <mxCell id="9" style="exitX=1;exitY=0.5;exitDx=0;exitDy=0;entryX=0;entryY=0.5;entryDx=0;entryDy=0;" edge="1" parent="1" source="3" target="4">
      <mxGeometry relative="1" as="geometry"/>
    </mxCell>
    <mxCell id="10" style="exitX=1;exitY=0.5;exitDx=0;exitDy=0;entryX=0;entryY=0.5;entryDx=0;entryDy=0;" edge="1" parent="1" source="6" target="4">
      <mxGeometry relative="1" as="geometry"/>
    </mxCell>
  </root>
</mxGraphModel>`;
      graph.importModelXML(txt);
    },
    getGraphHeight() {
      this.splitHeight = window.screen.availHeight - 300 + "px";
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
        setTimeout(() => {
          vm.$Message.info(`节点被移动到 ${JSON.stringify(position)}`);
        }, 1000);
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
            data = this.instances[cellData.dataIndex];
          } else if (cellData.type === "dataProcess") {
            data = this.dataProcess[cellData.dataIndex];
          } else {
            data = this.comparisonMethods[cellData.dataIndex];
          }
          cell.setValue(data.name);
          Object.assign(cell.data, data);
          console.log(JSON.stringify(cell.data));
          vm.$Message.info("添加了一个节点");
        } else if (cell.edge) {
          vm.$Message.info("添加了一条线");
        }
      });

      graph.addListener(mxEvent.LABEL_CHANGED, (sender, evt) => {
        vm.$Message.info(`内容改变为：${evt.getProperty("value")}`);
      });

      graph.addListener(mxEvent.CONNECT_CELL, (sender, evt) => {
        vm.$Message.info(`改变了连线`);
      });
    },
    mDown(e) {
      px = e.pageX;
      py = e.pageY;
      console.log("父组件捕获");
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
    }
  },
  mounted() {
    initGraph();
    this._listenEvent();
  },
  beforeDestroy() {
     window.removeEventListener('resize', this.getGraphHeight)
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
  font-family: PingF
}

#toolBar {
  background: #eee;
  padding-left: 10px;
  border-radius: 4px;
  margin-bottom: 10px;
}
</style>