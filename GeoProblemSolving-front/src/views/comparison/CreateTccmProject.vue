<template>
  <div>
    <h2 style="text-align:center;margin:20px;">Create TCCM Project</h2>
    <Steps :current="currentStep" style="width:1000px;margin:auto;text-align: center;">
      <Step title="Basic Info">
      </Step>
      <Step title="Protocol Info">
      </Step>
    </Steps>
    <div v-show="currentStep===0" class="box">
      <Form ref="step1Form" :model="projectInfo" style="margin-top:30px;" :rules="step1Rules">
        <FormItem prop="title" label="Title" :label-width="150">
          <Input v-model="projectInfo.title" style="width: 500px" placeholder="Enter project title." />
        </FormItem>

        <FormItem prop="description" label="Description" :label-width="150">
          <div>
            <Input type="textarea" v-model="projectInfo.description" placeholder="Enter description about this project."
              style="width: 500px" />
          </div>
        </FormItem>

        <FormItem prop="background" label="Background" :label-width="150">
          <div>
            <Input type="textarea" v-model="projectInfo.background" placeholder="Enter background about this project."
              style="width: 500px" />
          </div>
        </FormItem>

        <FormItem prop="goals" label="Goals" :label-width="150">
          <div>
            <Input type="textarea" v-model="projectInfo.goals" placeholder="Enter goals about this project."
              style="width: 500px" />
          </div>
        </FormItem>

        <Button class="stepBtn" type="primary" v-if="currentStep>0" @click="previous">Previous</Button>
        <Button class="stepBtn" type="primary" @click="next">Next step</Button>
      </Form>
    </div>
    <div v-show="currentStep===1" class="box">
      <div>
        <div style="margin-top:10px;margin-bottom:10px;">
          <h3 style="display:inline">1. Data Protocol</h3>
          <Button @click="addProtocol('data')" type="dashed" size="small" style="float: right;">
            Add Item
          </Button>
        </div>
        <protocol-table :protocolItems="dataProtocol.protocolItems"></protocol-table>
      </div>

      <div style="margin-top:50px;">
        <div style="margin-top:10px;margin-bottom:10px;">
          <h3 style="display:inline">1. Data Protocol</h3>
          <Button @click="addProtocol('process')" type="dashed" size="small" style="float: right;">
            Add Item
          </Button>
        </div>
        <protocol-table :protocolItems="processProtocol.protocolItems"></protocol-table>
      </div>
      <div style="margin-top:50px;">
        <div style="margin-top:10px;margin-bottom:10px;">
          <h3 style="display:inline">1. Data Protocol</h3>
          <Button @click="addProtocol('output')" type="dashed" size="small" style="float: right;">
            Add Item
          </Button>
        </div>
        <protocol-table :protocolItems="outputProtocol.protocolItems"></protocol-table>
      </div>
      <div style="margin-top:20px;">
        <Button class="stepBtn" type="primary" @click="create">Create</Button>
        <Button class="stepBtn" type="primary" @click="previous">Previous</Button>
      </div>

    </div>

    <Modal v-model="modal1" title="Add Protocol" @on-ok="ok" @on-cancel="cancel" draggable scrollable>
      <Form ref="protocolItem" :model="protocolItem" style="margin-top:30px;" :rules="modalRule" inline>
        <FormItem prop="name" label="Name" :label-width="100">
          <Input v-model="protocolItem.name" style="width: 300px" placeholder="Enter item name." />
        </FormItem>

        <FormItem prop="description" label="Description" :label-width="100">
          <div>
            <Input type="textarea" v-model="protocolItem.extendDoc" placeholder="Enter description about this item."
              style="width: 300px" />
          </div>
        </FormItem>
        <FormItem prop="metric" label="Bind Metric" :label-width="100">
          <Input enter-button placeholder="Enter metric name" v-model="metricAlias" style="width: auto" on-search="">
          <Button slot="append" icon="ios-search" @click="search"></Button>
          </Input>
        </FormItem>
        <h3 style="margin-left:30px;">Meta Info:</h3>
        <div v-for="(meta,index) of protocolItem.metaDoc">
          <FormItem prop="key" label="Key" :label-width="100">
            <div>
              <Input style="width: 120px" v-model="meta.key" />
            </div>
          </FormItem>
          <FormItem prop="value" label="Value" :label-width="50">
            <div>
              <Input style="width: 120px" v-model="meta.value" />
              <Button shape="circle" type="dashed" icon="md-close" size="small" style="color:#f00"
                @click="deleteItem(index,protocolItem.metaDoc)"></Button>
            </div>
          </FormItem>
        </div>
      </Form>
      <Button icon="ios-add" type="dashed" size="small" @click="addItem(protocolItem.metaDoc)"
        style="float: right;bottom: 15px;position: relative;">Add Item</Button>
    </Modal>

    <Drawer title="Choose Item" :closable="false" v-model="drawerShow" style="z-index:1005">
      <CellGroup>
        <Cell v-for="(metric,index) of metrics" :title="metric.wkName? metric.wkName: metric.alias" :key="metric.oid"
          @click="chooseMetric(metric)">
          <Button icon="ios-add" type="dashed" size="small" @click="chooseMetric(metric)" slot="extra"></Button>
        </Cell>
      </CellGroup>
    </Drawer>
  </div>
</template>
<script>
import ProtocolTable from "@/components/comparison/ProtocolTable";

var uuid = function() {
  var s = [];
  var hexDigits = "0123456789abcdef";
  for (var i = 0; i < 36; i++) {
    s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
  }
  s[14] = "4"; // bits 12-15 of the time_hi_and_version field to 0010
  s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1); // bits 6-7 of the clock_seq_hi_and_reserved to 01
  s[8] = s[13] = s[18] = s[23] = "-";

  var uuid = s.join("");
  return uuid;
};
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
  components: {
    "protocol-table": ProtocolTable
  },
  created() {
    this.getMetrics();
  },
  data() {
    return {
      projectInfo: {
        title: "",
        description: "",
        background: "",
        goals: "",
        projectType: "TCCM",
        managerId: this.$store.getters.userId,
        managerName: this.$store.getters.userName,
        protocols: []
      },
      drawerShow: false,
      currentStep: 0,
      currentType: "",
      modal1: false,
      dataProtocol: {
        oid: uuid(),
        type: "data",
        protocolItems: []
      },
      processProtocol: {
        oid: uuid(),
        type: "process",
        protocolItems: []
      },
      outputProtocol: {
        oid: uuid(),
        type: "output",
        protocolItems: []
      },
      protocolItem: {
        name: "",
        extendDoc: "",
        metaDoc: [
          {
            key: "",
            value: ""
          }
        ],
        metrics: {}
      },
      metrics: [],
      metricAlias: "",
      constantItem: {
        name: "",
        extendDoc: "",
        metaDoc: [
          {
            key: "",
            value: ""
          }
        ]
      },
      modalRule: {
        name: [
          {
            required: true,
            message: "Cannot be empty and no more than 100 characters",
            trigger: "blur",
            max: 100
          }
        ]
      },
      step1Rules: {
        title: [
          {
            required: true,
            message:
              "The title cannot be empty and no more than 100 characters",
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
        background: [
          {
            required: true,
            message: "Cannot be empty and no more than 300 characters",
            trigger: "blur",
            max: 300
          }
        ],
        goals: [
          {
            required: true,
            message: "Cannot be empty and no more than 300 characters",
            trigger: "blur",
            max: 300
          }
        ]
      }
    };
  },
  methods: {
    create() {
      this.projectInfo.protocols.push(this.dataProtocol);
      this.projectInfo.protocols.push(this.processProtocol);
      this.projectInfo.protocols.push(this.outputProtocol);

      this.$api.cmp_project
        .create(this.projectInfo)
        .then(res => {
          this.$router.push({
            path: `/cmp-project/tccm/${res.projectId}`
          });
        })
        .catch(err => {
          this.projectInfo.protocols = [];
          this.$Message.error(err);
        });
      // this.projectInfo.protocols = [];
    },
    chooseMetric(metric) {
      this.protocolItem.metrics = metric;
      this.metricAlias = metric.alias;
      console.log(metric);
    },
    search() {
      this.findMetric();
      this.drawerShow = true;
    },
    getMetrics() {
      this.$api.common
        .findAllItem("metrics")
        .then(res => {
          this.metrics = res;
        })
        .catch(err => {
          this.$Message.error(err);
        });
    },
    findMetric() {
      this.$api.common
        .findByX("metrics", "alias", this.metricAlias)
        .then(res => {
          this.metrics = res;
        })
        .catch(error => {
          this.$Message.error(error);
        });
    },
    addProtocol(type) {
      this.modal1 = true;
      this.currentType = type;
    },
    addItem(metaDoc) {
      let item = {
        key: "",
        value: ""
      };
      metaDoc.push(item);
    },
    deleteItem(index, metaDoc) {
      metaDoc = metaDoc.splice(index, 1);
    },
    ok() {
      this.$refs["protocolItem"].validate(valid => {
        if (valid) {
          // let item = this.protocolItem;
          let item = _.cloneDeep(this.protocolItem);
          if (this.currentType === "data") {
            this.dataProtocol.protocolItems.push(item);
          } else if (this.currentType === "process") {
            this.processProtocol.protocolItems.push(item);
          } else if (this.currentType === "output") {
            this.outputProtocol.protocolItems.push(item);
          }
          this.protocolItem = this.constantItem;
        } else {
          this.$Message.error("Item info is invalide.");
        }
      });
    },
    cancel() {
      this.protocolItem = this.constantItem;
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
          }
        });
        // this.changeTab();
      } else if (this.currentStep === 1) {
        this.currentStep += 1;
      } else {
      }
    },
    searchMetric() {
      console.log("search");
    }
  }
};
</script>
<style scoped>
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