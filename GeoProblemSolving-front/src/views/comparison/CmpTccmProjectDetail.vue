<template>
  <div class="mainContent">
    <h1 class="cmpTitle">{{this.projectInfo.title}}</h1>
    <Button type="info" v-if="!isMember" class="apply_btn" @click="applyJoinModal=true">Apply</Button>
    <Row>
      <Col span="22" offset="1">
      <Row>
        <!-- Left -->
        <Col :lg="5">
        <Card class="detailSidebar">
          <div class="single-info">
            <div class="info-tag">
              <Icon type="md-person" :size="20" />
              <strong>Creator</strong>
            </div>
            <span>{{this.projectInfo.managerName}}</span>
          </div>
          <div class="single-info">
            <div class="info-tag">
              <Icon type="md-time" :size="20" />
              <strong>Created Time</strong>
            </div>
            <span>{{getCreatedTime(this.projectInfo.createTime)}}</span>
          </div>

          <div class="single-info">
            <div class="info-tag">
              <Icon type="md-people" :size="20" />
              <span>Members</span>

            </div>
            <div style="display:flex;flex-wrap:wrap;">
              <span class="member" v-for="member of this.projectInfo.members">{{member.userName}}</span>

            </div>
          </div>

          <Collapse v-model="collapseVal">
            <Panel name="goals" :hide-arrow="true">
              <strong> Goals </strong>
              <p slot="content">{{this.projectInfo.goals}}</p>
            </Panel>
            <!-- <Panel name="description" :hide-arrow="true">
              <strong> Description </strong>
              <p slot="content">{{this.projectInfo.description}}</p>
            </Panel> -->
            <Panel name="background" :hide-arrow="true">
              <strong> Background </strong>
              <p slot="content">{{this.projectInfo.background}}</p>
            </Panel>

          </Collapse>
        </Card>

        <!-- <Divider /> -->

        <!-- modal/drawer -->
        <Drawer title="Concepts" :closable="false" v-model="vocabularyValue" width="300">
          <div slot="header" style="display:flex;align-items:center">
            <h3 style="display:inline;margin-right:20px;">Search:</h3>
            <Input enter-button placeholder="Enter by name" v-model="searchVal"
              style="width: auto; display:inline-table">
            <Button slot="append" icon="ios-search" @click="searchInfo"></Button>
            </Input>
          </div>
          <CellGroup>
            <Cell v-for="list in typeResources" :key="list.oid">
              <strong class="list-item-text">{{list.name}}</strong>
              <span class="list-item-text"
                slot="label">{{list.description?list.description:list.description_EN?list.description_EN:list.description_ZH}}</span>
              <Button icon="ios-add" type="dashed" size="small" @click="chooseInfo(list)" slot="extra"></Button>
            </Cell>
          </CellGroup>
        </Drawer>

        <!-- create concept -->
        <Modal v-model="modalCreate">
          <p slot="header" style="text-align:center">
            <Icon type="ios-information-circle"></Icon>
            <span>Create name and description</span>
          </p>
          <Form :label-width="120" label-position="left" :model="createForm">
            <FormItem label="Type" prop="type">
              <Select v-model="createForm.type" style="width:200px">
                <Option v-for="item in modalType" :value="item.value" :key="item.value">{{ item.label }}</Option>
              </Select>
            </FormItem>

            <FormItem label="Name" prop="name">
              <Input v-model="createForm.name" type="textarea" :autosize="{minRows: 1,maxRows: 3}" clearable />
            </FormItem>
            <FormItem label="Description" prop="description">
              <Input v-model="createForm.description" type="textarea" :autosize="{minRows: 1,maxRows: 3}" clearable />
            </FormItem>
          </Form>
          <div slot="footer">
            <Button @click="cancelCreate">Cancel</Button>
            <Button type="primary" @click="submitCreate">Create</Button>
          </div>
        </Modal>
        </Col>

        <!-- Right -->
        <Col :lg="{span:18,offset:1}" :md="{span:15,offset:1}" :sm="{span:13,offset:1}" :xs="{span:11,offset:1}">
        <div class="rightContent">
          <Card>
            <Collapse v-model="collapseRight">
              <Panel name="protocol">
                <strong style="font-size:18px;"> Protocols </strong>
                <Tabs slot="content" :animated="false">
                  <TabPane label="Data Protocols" style="font-size:18px;">
                    <div>
                      <Button @click="addProtocol('data')" type="dashed" size="small" style="margin-bottom:10px;">
                        Add Item
                      </Button>
                    </div>

                    <protocol-table :protocolItems="getProtocol('data')"></protocol-table>
                  </TabPane>
                  <TabPane label="Process Protocols">
                    <div>
                      <Button @click="addProtocol('process')" type="dashed" size="small" style="margin-bottom:10px;">
                        Add Item
                      </Button>
                    </div>
                    <protocol-table :protocolItems="getProtocol('process')"></protocol-table>
                  </TabPane>
                  <TabPane label="Output Protocols">
                    <div>
                      <Button @click="addProtocol('output')" type="dashed" size="small" style="margin-bottom:10px;">
                        Add Item
                      </Button>
                    </div>
                    <protocol-table :protocolItems="getProtocol('output')"></protocol-table>
                  </TabPane>
                </Tabs>
              </Panel>
              <Panel name="repositories">
                <strong style="font-size:18px;"> Basic Repositories </strong>
                <Tabs slot="content" @on-click="getAllConcept" :animated="false" :style="{overflow:'unset'}">
                  <TabPane label="Concept" name="concept">
                    <div v-for="sellist in selectCardContent" :key="sellist.id">
                      <Tag color="primary">{{sellist.name}}</Tag>
                      <Icon type="md-close" @click="clearInput(sellist.id)" />
                    </div>
                    <Button @click="vocabularyValue = true">Choose concepts</Button>
                    <Table border :columns="resColumns" :data="conceptList" :max-height="300">
                      <template slot-scope="{row,index}" slot="name">
                        <span style="font-size:16px;">{{row.name}}</span>
                      </template>
                      <template slot-scope="{row,index}" slot="description">
                        <span style="font-size:16px;">{{row.description?row.description:"-"}}</span>
                      </template>
                    </Table>
                  </TabPane>
                  <TabPane label="Data template" name="template">
                    <div v-for="sellist in selectCardContent" :key="sellist.id">
                      <Tag color="primary">{{sellist.name}}</Tag>
                      <Icon type="md-close" @click="clearInput(sellist.id)" />
                    </div>
                    <Button @click="vocabularyValue = true">Choose templates</Button>
                    <Table border :columns="resColumns" :data="templateList" :max-height="300">
                      <template slot-scope="{row,index}" slot="name">
                        <span style="font-size:16px;">{{row.name}}</span>
                      </template>
                      <template slot-scope="{row,index}" slot="description">
                        <span style="font-size:16px;">{{row.description?row.description:"-"}}</span>
                      </template>
                    </Table>
                  </TabPane>
                  <TabPane label="Unit" name="unit">
                    <div v-for="sellist in selectCardContent" :key="sellist.id">
                      <Tag color="primary">{{sellist.name}}</Tag>
                      <Icon type="md-close" @click="clearInput(sellist.id)" />
                    </div>
                    <Button @click="vocabularyValue = true">Choose unit</Button>
                    <Table border :columns="resColumns" :data="unitList" :max-height="300">
                      <template slot-scope="{row,index}" slot="name">
                        <span style="font-size:16px;">{{row.name}}</span>
                      </template>
                      <template slot-scope="{row,index}" slot="description">
                        <span
                          style="font-size:16px;">{{row.description?row.description:row.description_EN?row.description_EN:"-"}}</span>
                        <!-- {{row.description?row.description:row.description_EN?row.description_EN:list.description_ZH}} -->
                      </template>
                    </Table>
                  </TabPane>
                  <TabPane label="Spatiotemporal Reference" name="spatialref">
                    <div v-for="sellist in selectCardContent" :key="sellist.id">
                      <Tag color="primary">{{sellist.name}}</Tag>
                      <Icon type="md-close" @click="clearInput(sellist.id)" />
                    </div>
                    <Button @click="vocabularyValue = true">Choose reference</Button>
                    <Table border :columns="resColumns" :data="spatialRefList" :max-height="300">
                      <template slot-scope="{row,index}" slot="name">
                        <span style="font-size:16px;">{{row.name}}</span>
                      </template>
                      <template slot-scope="{row,index}" slot="description">
                        <span
                          style="font-size:16px;">{{row.description?row.description:row.description_EN?row.description_EN:"-"}}</span>
                      </template>
                    </Table>
                  </TabPane>
                  <Button style="float:right" type="primary" ghost @click="modalCreate = true"
                    slot="extra">Create</Button>
                </Tabs>
              </Panel>
            </Collapse>

            <Modal v-model="modal1" title="Add Protocol" @on-ok="ok" @on-cancel="cancel" draggable scrollable>
              <Form ref="protocolItem" :model="protocolItem" style="margin-top:30px;" :rules="modalRule" inline>
                <FormItem prop="name" label="Name" :label-width="100">
                  <Input v-model="protocolItem.name" style="width: 300px" placeholder="Enter item name." />
                </FormItem>

                <FormItem prop="description" label="Description" :label-width="100">
                  <div>
                    <Input type="textarea" v-model="protocolItem.extendDoc"
                      placeholder="Enter description about this item." style="width: 300px" />
                  </div>
                </FormItem>
                <FormItem prop="metrics" label="Bind Metric" :label-width="100">
                  <Input enter-button placeholder="Enter metric name" v-model="metricAlias" style="width: auto"
                    on-search="">
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

            <Drawer :closable="false" v-model="drawerShow" style="z-index:1005">
              <div slot="header" style="display:flex;align-items:center">
                <h3>Choose Metric</h3>
                <Button slot="append" @click="modal13 = true" size="small" style="margin-left: 60px;"> Create</Button>
              </div>
              <CellGroup>
                <Cell v-for="(metric,index) of metrics" :title="metric.wkName? metric.wkName: metric.alias"
                  :key="metric.oid" @click="chooseMetric(metric)">
                  <Button icon="ios-add" type="dashed" size="small" @click="chooseMetric(metric)" slot="extra"></Button>
                </Cell>
              </CellGroup>
            </Drawer>
            <Modal v-model="modal13" draggable scrollable title="Create Metric">
              <create-metrics-form v-on:createMetricSuccess="onCreateSuccess"></create-metrics-form>
              <span slot="footer"></span>
            </Modal>
          </Card>
          <Card style="margin-top:50px;">
            <Tabs :animated="false">
              <TabPane label="PIC Instance List">
                <div v-if="instanceList.length>0">
                  <div class="cmpModelBox">
                    <Row>
                      <Col :xs="{ span: 21, offset: 1 }" :md="{ span: 11, offset: 1 }" :lg="{ span: 6 }">
                      <div @click="createInstance">
                        <Card style="height:150px;margin:10px -15px">
                          <div style="display:flex; justify-content: center;  height: 120px; align-items: center;">
                            <img style="width:70px" src="@/assets/images/comparison/add.png" alt="add instance">
                          </div>
                        </Card>
                      </div>
                      </Col>
                      <Col :xs="{ span: 21, offset: 1 }" :md="{ span: 11, offset: 1 }" :lg="{ span: 6 }"
                        v-for="instance of instanceList" :key="instance.instanceId">
                      <Card style="height:150px;margin:10px -15px">
                        <div>
                          <div v-if="instance.type==='observation'" style="float:right; margin-top:-10px;">
                            <Icon type="md-eye" color="#20b2aa" />
                            <span style="font-size:10px;color:#20b2aa">Observation</span>
                          </div>
                          <div v-if="instance.type==='benchmark'" style="float:right; margin-top:-10px;">
                            <Icon type="md-speedometer" color="#daa520" />
                            <span style="font-size:10px;color:#daa520">Benchmark</span>
                          </div>
                          <div v-if="instance.type==='model'" style="float:right; margin-top:-10px;">
                            <Icon type="md-planet" color="#d2691e" />
                            <span style="font-size:10px;color:#d2691e">Model</span>
                          </div>
                          <div class="cmpItemTitle">
                            <a href="#" @click.prevent="instanceDetail(instance)">{{instance.name}}</a>
                          </div>
                          <p class="cmpItemDesc">{{instance.description}}</p>
                          <div id="bottom-info">
                            <div class="info">
                              <Icon type="md-body" :size="15" />
                              <span style="margin-left:10px; color:#2b85e4">{{instance.userName}}</span>
                            </div>
                            <div class="info">
                              <!-- <div class="info"> -->
                              <Icon type="md-clock" :size="15" />
                              <span style="margin-left:10px">{{getCreatedTime(instance.createdTime)}}</span>
                              <!-- </div> -->
                              <!-- <div class="info">
                                <Icon type="md-clock" :size="15" />
                                <span style="margin-left:10px">{{getCreatedTime(instance.createdTime)}}</span>
                              </div> -->

                            </div>
                          </div>
                        </div>
                      </Card>
                      </Col>
                    </Row>
                  </div>
                </div>

                <blank-box v-else v-bind="blankInstanceInfo" v-on:linkClicked="createInstance"></blank-box>
              </TabPane>
              <TabPane label="PIC Task List">
                <div v-if="taskList.length>0">
                  <div class="cmpModelBox">
                    <Row>
                      <Col :xs="{ span: 21, offset: 1 }" :md="{ span: 11, offset: 1 }" :lg="{ span: 6 }">
                      <div @click="createTask">
                        <Card style="height:150px;margin:10px -15px">
                          <div style="display:flex; justify-content: center;  height: 120px; align-items: center;">
                            <img style="width:70px" src="@/assets/images/comparison/add.png" alt="add instance">
                          </div>
                        </Card>
                      </div>
                      </Col>

                      <Col :xs="{ span: 21, offset: 1 }" :md="{ span: 11, offset: 1 }" :lg="{ span: 6 }"
                        v-for="task of taskList" :key="task.recordId">
                      <Card style="height:150px;margin:10px -15px">
                        <div>
                          <div class="cmpItemTitle">
                            <a href="#" @click.prevent="taskDetail(task)">{{task.name}}</a>
                          </div>
                          <p class="cmpItemDesc">{{task.desc}}</p>
                          <div id="bottom-info">
                            <div class="info">
                              <Icon type="md-body" :size="15" />
                              <span style="margin-left:10px; color:#2b85e4">{{task.userName}}</span>
                            </div>
                            <div class="info">
                              <Icon type="md-clock" :size="15" />
                              <span style="margin-left:10px">{{getCreatedTime(task.createdTime)}}</span>
                            </div>
                          </div>
                        </div>
                      </Card>
                      </Col>
                    </Row>
                  </div>
                </div>
                <blank-box v-else v-bind="blankTaskInfo" v-on:linkClicked="createTask"></blank-box>
              </TabPane>

            </Tabs>
          </Card>
        </div>
        </Col>

      </Row>
      </Col>
    </Row>

    <Modal v-model="applyJoinModal" title="Apply to join the project">
      <Form ref="applyValidate" :model="applyValidate" :rules="applyRuleValidate" :label-width="80">
        <FormItem label="Reason" prop="reason">
          <Input v-model="applyValidate.reason" type="textarea" :rows="4"
            placeholder="Enter The Reason For Application ..." />
        </FormItem>
      </Form>
      <div slot="footer">
        <Button @click="applyJoinModal=false">Cancel</Button>
        <Button type="success" @click="joinApply('applyValidate')">Apply</Button>
      </div>
    </Modal>

  </div>

</template>
<script>
import Avatar from "vue-avatar";
import ProtocolTable from "@/components/comparison/ProtocolTable";
import BlankBox from "@/components/comparison/BlankBox";
import CreateMetricForm from "@/components/comparison/CreateMetricsForm";
var uuid = function () {
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
  // beforeRouteEnter: (to, from, next) => {
  //   if (from.name === "cmp-projectlist") {
  //     to.meta.keepAlive = false;
  //   } else {
  //     to.meta.keepAlive = true;
  //   }
  //   next(vm => {
  //     if (!vm.$store.getters.userState) {
  //       next("/login");
  //     } else {
  //       next();
  //     }
  //   });
  // },
  beforeRouteLeave(to, from, next) {
    // ...
    // from.meta.keepAlive = true;
    next();
  },
  created() {
    this.initSize();
    this.projectId = this.$route.params.id;

    //* 获取项目信息
    this.getProjectInfo();
    //* 获取instance
    this.getInstanceList();
    //* 获取 task
    this.getTaskList();
    this.getAllConcept("concept");
    this.getMetrics();
  },
  components: {
    Avatar,
    "protocol-table": ProtocolTable,
    "blank-box": BlankBox,
    "create-metrics-form": CreateMetricForm
  },
  data() {
    return {
      isMember: false,
      haveApplied: false,
      applyValidate: {
        reason: ""
      },
      applyRuleValidate: {
        reason: [
          {
            required: true,
            message: "Please enter the reason for application",
            trigger: "blur"
          },
          {
            type: "string",
            min: 5,
            message: "The reason no less than 10 characters",
            trigger: "blur"
          }
        ]
      },
      applyJoinModal: false,
      modal1: false,
      collapseVal: ["goals", "background"],
      collapseRight: ["protocol"],
      searchVal: "",
      //走马灯属性
      carouseValue: 0,
      currentStep: 0,
      projectId: "",
      projectInfo: {},
      instanceList: [],
      taskList: [],
      blankInfo: {
        welcomeTitle: "Welcome to Comparison!",
        welcomeInfo:
          "Comprehensive comparison of simulation capabilities from multiple perspectives improving our knowledge and understanding of models. To get started, you should",
        linkInfo: "create an comparison task."
      },
      blankTaskInfo: {
        welcomeTitle: "Welcome to Comparison!",
        welcomeInfo:
          "Comprehensive comparison of simulation capabilities from multiple perspectives improving our knowledge and understanding of models. To get started, you should",
        linkInfo: "create an comparison Task."
      },
      blankInstanceInfo: {
        welcomeTitle: "Welcome to Comparison!",
        welcomeInfo:
          "Comprehensive comparison of simulation capabilities from multiple perspectives improving our knowledge and understanding of models. To get started, you should",
        linkInfo: "create an comparison instance."
      },
      currentStep: 0,
      vocabularyValue: false,
      resourceType: "concept",
      typeResources: [],
      selectCardContent: [],
      searchCardContent: [],
      showTag: true,
      modalCreate: false,
      createForm: {
        name: "",
        description: "",
        type: ""
      },
      modalType: [
        {
          value: "concept",
          label: "Concept"
        },
        {
          value: "template",
          label: "Data template"
        },
        {
          value: "unit",
          label: "Unit"
        },
        {
          value: "spatialref",
          label: "Spatio-temporal reference"
        }
      ],
      modelType1: "concept",
      inputValue: "",
      unitList: [],
      conceptList: [],
      templateList: [],
      spatialRefList: [],
      resColumns: [
        {
          title: "Name",
          slot: "name",
          align: "center",
          width: "200px",
          className: "font_size_18"
        },
        {
          title: "Description",
          slot: "description",
          align: "center",
          minWidth: 180,
          className: "font_size_18",
        }
      ],
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
      metricAlias: "",
      drawerShow: false,
      metrics: [],
      currentType: "",
      modal13: false
    };
  },
  methods: {
    joinApply(name) {
      this.$refs[name].validate(valid => {
        if (valid) {
          this.applyJoinModal = false;
          if (
            this.haveApplied == true &&
            this.projectInfo.projectId == sessionStorage.getItem("applyId")
          ) {
            this.$Notice.warning({
              title: "repeat apply warning",
              desc: "You have apply success, no need to click again!"
            });
          } else {
            if (this.$store.getters.userState) {
              let userDetail = this.$store.getters.userInfo;
              let joinForm = {};
              sessionStorage.setItem("applyId", this.projectInfo.projectId);
              joinForm["recipientId"] = this.projectInfo.managerId;
              joinForm["type"] = "apply";
              joinForm["content"] = {
                userEmail: userDetail.email,
                userName: this.$store.getters.userName,
                userId: this.$store.getters.userId,
                title: "Group application",
                description:
                  "User " +
                  this.$store.getters.userName +
                  " apply to join your project: " +
                  this.projectInfo.title +
                  " ." +
                  " The reason for application is: " +
                  this.applyValidate.reason,
                projectId: this.projectInfo.projectId,
                projectTitle: this.projectInfo.title,
                scope: "cmp_project",
                approve: "unknow"
              };
              this.axios
                .post("/GeoProblemSolving_Backend/notice/save", joinForm)
                .then(res => {
                  if (res.data == "Success") {
                    this.$Notice.open({
                      title: "Apply Successfully",
                      desc:
                        "The project's manager will process your apply in time,you can get a notification later to tell you the result."
                    });
                    this.$emit("sendNotice", this.projectInfo.managerId);
                    this.haveApplied = true;
                  } else {
                    this.$Notice.open({
                      desc: "Apply failed"
                    });
                  }
                })
                .catch(err => {
                  console.log("申请失败的原因是：" + err.data);
                });
              let emailObject = {
                recipient: joinForm.recipientId,
                mailTitle: "Group application",
                mailContent:
                  joinForm.content.description +
                  "<br>" +
                  "You can click this url and enter the site to process this application: " +
                  "http://" +
                  this.$store.state.IP_Port +
                  "/GeoProblemSolving/home"
              };
              this.axios
                .post("/GeoProblemSolving_Backend/project/applyByMail", emailObject)
                .then(res => {
                  if (res.data == "Success") {
                    console.log("Email Success.");
                  } else {
                    console.log("Email fail.");
                  }
                })
                .catch(err => {
                  console.log("Email fail.");
                });
            } else {
              this.$Message.error("you must have an account before you apply");
              this.$router.push({ name: "Login" });
            }
          }
        }
      })
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
    onCreateSuccess(data) {
      this.metrics.push(data);
      this.modal13 = false;
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
      if (this.checkPermission()) {
        this.modal1 = true;
        this.currentType = type;
      }
    },
    chooseMetric(metric) {
      this.protocolItem.metrics = metric;
      this.metricAlias = metric.alias;
      console.log(metric);
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
    deleteItem(index, metaDoc) {
      metaDoc = metaDoc.splice(index, 1);
    },
    search() {
      this.findMetric();
      this.drawerShow = true;
    },
    updateProject() {
      this.$api.cmp_project
        .updateProject(this.projectInfo)
        .then(res => {
          this.projectInfo = res;
        })
        .catch(err => {
          this.$Message.error(err);
        });
    },
    ok() {
      this.$refs["protocolItem"].validate(valid => {
        if (valid) {
          //* 更新 project 中的 protocol;
          let item = _.cloneDeep(this.protocolItem);
          let protocol = [];
          let obj = null;
          if (this.projectInfo.protocols) {
            protocol = this.projectInfo.protocols.filter(protocol => {
              return protocol.type === this.currentType;
            });
          }
          if (protocol.length == 0) {
            //* 没有 protocol 需要新建
            obj = {
              oid: uuid(),
              type: this.currentType,
              protocolItems: []
            };
            protocol.push(obj);
          }
          protocol[0].protocolItems.push(item);
          let vm = this;
          let index = _.findIndex(this.projectInfo.protocols, function (o) {
            return o.type === vm.currentType;
          });
          if (index >= 0) {
            this.projectInfo.protocols.splice(index, 1, protocol[0]);
          } else {
            this.projectInfo.protocols.push(protocol[0]);
          }
          //* 更新 project:
          this.updateProject(this.projectInfo);
          this.protocolItem = this.constantItem;
          this.metricAlias = "";
        } else {
          this.$Message.error("Item info is invalide.");
        }
      });
    },
    cancel() {
      this.protocolItem = this.constantItem;
    },
    taskDetail(task) {
      this.$router.push({
        path: `/cmp-task-record/${task.recordId}`
      });
    },
    getResourceList(type, list) {
      let obj = {
        type: type,
        idList: list
      };
      this.$api.common
        .findResList(obj)
        .then(res => {
          if (type === "concept") {
            this.conceptList = res;
          } else if (type === "template") {
            this.templateList = res;
          } else if (type === "unit") {
            this.unitList = res;
          } else if (type === "spatialref") {
            this.spatialRefList = res;
          }
        })
        .catch(err => {
          this.$Message.error(err);
        });
    },
    // 初始化侧边栏样式
    initSize() {
      this.sidebarHeight = window.innerHeight - 290;
    },

    // 获得后台所有的vocabulary
    getAllConcept(type) {
      this.selectCardContent = [];
      this.resourceType = type;
      this.axios
        .get("/GeoProblemSolving_Backend/common/findAllItem?type=" + this.resourceType)
        .then(res => {
          this.typeResources = res.data.data; //异步
        });
    },

    selectCard(selected, oid) {
      let list = { name: selected, id: oid };
      // 检查重复的tag
      if (this.selectCardContent.length == 0) {
        this.selectCardContent.push(list);
      } else {
        for (let i = 0; i < this.selectCardContent.length; i++) {
          if (this.selectCardContent[i].id == oid) {
            this.$Notice.info({
              desc: "The tag has been exist!"
            });
            break;
          } else if (i == this.selectCardContent.length - 1) {
            this.selectCardContent.push(list);
            break;
          }
        }
      }
      console.log(this.selectCardContent);
    },
    searchInfo() {
      this.$api.common
        .findByX(this.resourceType, "name", this.searchVal)
        .then(res => {
          this.typeResources = res;
        })
        .catch(err => {
          this.$Message.error(err);
        });
    },

    // 模糊搜索card
    searchCard(value) {
      console.log(value);
      this.axios
        .get(
          "/GeoProblemSolving/common/findByX?type=" +
          this.resourceType +
          "&key=name" +
          "&value=" +
          value
        )
        .then(res => {
          console.log(res.data);
          this.searchCardContent = res.data.data; //异步
        });
    },

    // 清除选择的内容
    clearInput(id) {
      console.log(id);
      // this.selectCardContent = "";
      for (let i = 0; i < this.selectCardContent.length; i++) {
        if (this.selectCardContent[i].id == id) {
          this.selectCardContent.splice(i, 1);
        }
      }
    },

    // modal
    submitCreate() {
      let obj = {};
      let objData = {};
      obj.type = this.createForm.type;
      objData.name = this.createForm.name;
      objData.description = this.createForm.description;
      objData.author = this.$store.getters.userName;
      objData.authorId = this.$store.getters.userId;
      obj.data = objData;

      this.axios
        .post("/GeoProblemSolving/common/createItem", obj)
        .then(res => {
          console.log(res);
          if (obj.type === "unit") {
            this.projectInfo.unitList.push(res);
          } else if (obj.type === "concept") {
            this.projectInfo.conceptList.push(res);
          } else if (obj.type === "template") {
            this.projectInfo.templateList.push(res);
          } else if (obj.type === "spatialref") {
            this.projectInfo.spatialRefList.push(res);
          }
        })
        .catch(err => {
          this.$Message.error(err);
        });
      this.modalCreate = false;
    },

    getProjectInfo() {
      this.$api.cmp_project
        .getProjectAllInfo(this.projectId)
        .then(res => {
          console.log(res);
          this.projectInfo = res.project;
          this.modelList = res.model;
          let userId = this.$store.getters.userId;
          let index = _.findIndex(this.projectInfo.members, function (member) { return member.userId == userId });
          if (index >= 0 || userId == this.projectInfo.managerId) {
            this.isMember = true;
          }
          if (
            this.projectInfo.unitList &&
            this.projectInfo.unitList.length > 0
          ) {
            this.getResourceList("unit", this.projectInfo.unitList);
          }
          if (
            this.projectInfo.conceptList &&
            this.projectInfo.conceptList.length > 0
          ) {
            this.getResourceList("concept", this.projectInfo.conceptList);
          }
          if (
            this.projectInfo.templateList &&
            this.projectInfo.templateList.length > 0
          ) {
            this.getResourceList("template", this.projectInfo.templateList);
          }
          if (
            this.projectInfo.spatialRefList &&
            this.projectInfo.spatialRefList.length > 0
          ) {
            this.getResourceList("spatialref", this.projectInfo.spatialRefList);
          }
          console.log(this.projectInfo);
        })
        .catch(err => {
          this.$Message.error(err);
        });
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
    getTaskList() {
      this.$api.cmp_task
        .getCmpTasks(this.projectId)
        .then(res => {
          this.taskList = res;
        })
        .catch(err => {
          this.$Message.error(err);
        });
    },
    createInstance() {
      if(this.checkPermission()){
        this.$router.push({
          path: `/create-cmp-instance/${this.projectInfo.projectId}`
        });
      }
    },
    createTask() {
      if (this.checkPermission()) {
        this.$router.push({
          path: `/create-cmp-task/${this.projectInfo.projectId}`
        });
      }

    },
    instanceDetail(instance) {
      this.$router.push({
        path: `/cmp-instance-detail/${instance.instanceId}`
      });
    },
    cancelCreate() {
      this.modalCreate = false;
    },
    chooseInfo(info) {
      //* 更新 project 信息
      let obj = {
        projectId: this.projectId,
        type: this.resourceType,
        isAdd: true,
        id: info.oid
      };
      this.updateResList(obj);
      if (this.resourceType === "concept") {
        this.conceptList.push(info);
      } else if (this.resourceType === "template") {
        this.templateList.push(info);
      } else if (this.resourceType === "unit") {
        this.unitList.push(info);
      } else if (this.resourceType === "spatialref") {
        this.spatialRefList.push(info);
      }
    },
    updateResList(obj) {
      this.$api.cmp_project
        .updateList(obj)
        .then(res => { })
        .catch(error => {
          this.$Message.error(error);
        });
    }
  },
  computed: {
    getProtocol() {
      return function (type) {
        if (this.projectInfo.protocols) {
          let res = this.projectInfo.protocols.filter(protocol => {
            return protocol.type === type;
          });
          if (res.length > 0) {
            return res[0].protocolItems;
          }
        }
        return [];
      };
    },
    getCreatedTime() {
      return function (time) {
        return time ? time.split(" ")[0] : "";
      };
    },
    handleClose() {
      this.showTag = false;
    }
  }
};
</script>
<style scoped>
.list-item-text {
  display: block;
  width: 200px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.member {
  font-size: 12px;
  border: 1px solid #d1d5da;
  border-radius: 8px;
  padding: 3px;
  margin-right: 5px;
  margin-left: 5px;
  margin-bottom: 5px;
}

.info-tag {
  display: flex;
  width: 120px;
  align-items: center;
}

.cmpTitle {
  text-align: center;
  margin-top: 30px;
  margin-bottom: 20px;
  max-width: 800px;
  margin-left: auto;
  margin-right: auto;
  color: #0366d6;
}

.detailSidebar {
  /* margin-right: 20px; */
}
.rightContent {
  /* margin-top: 20px; */
  flex: 1;
}

/* 用户头像结束 */
body {
  overflow-x: hidden;
}
/* 侧边用户信息的显示样式 */
.single-info {
  display: flex;
  align-items: flex-start;
  padding: 5px;
  /* height: 30px; */
  font-size: 12px;
  line-height: 15px;
}

.topic-title {
  font-size: 25px;
  color: black;
  font-weight: 700;
  /* margin-top:5%; */
}

.sub-title {
  font-size: 18px;
  color: black;
  font-weight: 400;
  margin-bottom: 2%;
}
.stepBtn {
  margin-left: 20%;
  margin-top: 20%;
}
.mainContent {
  /* background-color: rgba(116, 111, 111, 0.2); */
  height: 700px;
}
.infoName {
  color: #0366d6;
  font-size: 16px;
}

.basicInfo p {
  margin-left: 20px;
  margin-top: 5px;
  margin-bottom: 5px;
  letindefont-size: 16px;
}

.members {
  display: flex;
  width: 100%;
  flex-wrap: wrap;
  margin-top: 5px;
}

.member {
  font-size: 12px;
  border: 1px solid #d1d5da;
  border-radius: 8px;
  padding: 3px;
  margin-right: 5px;
  margin-left: 5px;
  margin-bottom: 5px;
}

.constrainsContent {
  display: flex;
  margin-top: 10px;
  margin-bottom: 10px;
  border-bottom: 1px solid #d1d5da;
}

.resourceContent {
  flex: 3 0 auto;
  /* flex: auto; */
  border-right: 1px solid #d1d5da;
  margin-top: 5px;
  margin-bottom: 5px;
  margin-right: 10px;
}

.constrainsInfo {
  flex: 1 0 auto;
}

.box {
  background-color: #fff;
  border: 1px solid #d1d5da;
  border-radius: 3px;
}

.blankslate {
  padding: 80px 40px;
  background-color: #fafbfc;
  border-radius: 3px;
  box-shadow: inset 0 0 10px rgba(27, 31, 35, 0.05);
  text-align: center;
}

.blankslate-icon {
  color: #a3aab1;
  margin-bottom: 8px;
  margin-left: 4px;
  margin-right: 4px;
  vertical-align: text-bottom;
  display: inline-block;
  fill: currentColor;
}

.blankslate h3 {
  font-size: 20px;
  margin: 16px 0;
  font-weight: 600;
}

.blankslate a {
  color: #0366d6;
  text-decoration: none;
}

.blankslate p {
  font-size: 16px;
  margin: 0px 50px;
}

.comparisonContent {
  margin-bottom: 10px;
}

.createTime {
  display: flex;
  flex-direction: row-reverse;
}

.createTime span {
  color: #586069;
  font-size: 14px;
  line-height: 20px;
}

.cmpItemTitle {
  /* height: 3em; */
  overflow: hidden;
  /* text-overflow: ellipsis; */
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
}
.cmpItemTitle a {
  color: #2d8cf0;
  text-decoration: none;
  font-weight: 600;
  font-size: 16px;
  line-height: 1em;
}

.cmpItemDesc {
  font-size: 14px;
  height: 80px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
}

#bottom-info {
  display: flex;
  margin-top: 5px;
  justify-content: space-between;
}

.info {
  display: flex;
  align-items: center;
  margin-right: 10px;
}

.apply_btn {
  position: absolute;
  right: 100px;
  top: 100px;
}
</style>