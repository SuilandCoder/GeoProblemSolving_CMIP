<style scoped>
.detailSidebar {
  margin-right: 20px;
}
.rightContent {
  margin-top: 20px;
  flex: 1;
}

/* 用户头像结束 */
body {
  overflow-x: hidden;
}
/* 侧边用户信息的显示样式 */
.single-info {
  padding: 5px;
  height: 30px;
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
  /* margin-top:5%; */
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
  font-size: 16px;
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
</style>
<template>
  <div class="mainContent">
    <Row>
      <Col span="22" offset="1">
      <Row>
        <Col :lg="5" :md="8" :sm="10" :xs="12">
        <Card class="detailSidebar">
          <div class="topic-title">
            {{projectInfo.title}}
          </div>

          <div class="single-info">
            <Icon type="ios-leaf" :size="20" />
            <span>Background</span>
            <p>{{projectInfo.background}}</p>
          </div>

          <div class="single-info">
            <Icon type="ios-leaf" :size="20" />
            <span>Goals</span>
            <p>{{projectInfo.goals}}</p>
          </div>

          <div class="single-info">
            <Icon type="md-people" :size="20" />
            <span>Member</span>
          </div>
          <div class="single-info">
            <Icon type="md-person" :size="20" />
            <span>Creator</span>
          </div>
          <div class="single-info">
            <Icon type="md-time" :size="20" />
            <span>Created Time</span>
          </div>

        </Card>

        <Divider />
        <Card>
          <div class="sub-title">
            Information
          </div>
          <Tabs size="small">
            <TabPane label="Vocabulary">Vocabulary</TabPane>
            <TabPane label="Data template">Data template</TabPane>
            <TabPane label="Unit">Unit</TabPane>
            <TabPane label="Reference">Spatio-temporal reference</TabPane>
          </Tabs>
        </Card>

        </Col>
        <!-- <Divider  type="vertical"/> -->
        <Col :lg="{span:18,offset:1}" :md="{span:15,offset:1}" :sm="{span:13,offset:1}" :xs="{span:11,offset:1}">
        <div class="rightContent">

          <Card>
            <Col v-show="false" offset="1">
            <!-- <p>当前正在进行第 {{ currentStep + 1 }} 步</p> -->
            <Steps :current="currentStep">
              <Step title="Data protocal"></Step>
              <Step title="Model process protocal"></Step>
              <Step title="Model output protocal"></Step>
            </Steps>
            <div class="stepBtn">
              <Button @click="lastStep">Last Protocal</Button>
              <Button type="primary" @click="nextStep">Next Protocal</Button>
            </div>
            </Col>
            <Tabs :animated="false">
              <TabPane label="Data Protocol">
                <protocol-table :protocolItems="getProtocol('data')"></protocol-table>
              </TabPane>
              <TabPane label="Process Protocol">
                <protocol-table :protocolItems="getProtocol('process')"></protocol-table>
              </TabPane>
              <TabPane label="Output Protocol">
                <protocol-table :protocolItems="getProtocol('output')"></protocol-table>
              </TabPane>
            </Tabs>

          </Card>
          <Col :lg="11" :md="{span:11,offset:1}" :sm="{span:13,offset:1}" :xs="{span:11,offset:1}">
          <Card v-show="false">
            <template>
              instance
            </template>
          </Card>
          </Col>
          <Col :lg="{span:11,offset:1}" :md="{span:11,offset:1}" :sm="{span:13,offset:1}" :xs="{span:11,offset:1}">
          <Card v-show="false">
            <template>
              task
            </template>
          </Card>
          </Col>
          <Card style="margin-top:50px;">
            <Tabs :animated="false">
              <TabPane label="Instance List">
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
                          <div v-if="instance.type==='observation'" style = "float:right; margin-top:-10px;">
                            <Icon type="md-eye" color="#20b2aa"/>
                            <span style="font-size:10px;color:#20b2aa">Observation</span>
                          </div>
                          <div v-if="instance.type==='benchmark'" style = "float:right; margin-top:-10px;">
                            <Icon type="md-speedometer" color="#daa520"/>
                            <span style="font-size:10px;color:#daa520">Benchmark</span>
                          </div>
                          <div v-if="instance.type==='model'" style = "float:right; margin-top:-10px;">
                            <Icon type="md-planet" color="#d2691e"/>
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
              <TabPane label="Comparison Task">
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

  </div>
</template>
<script>
import Avatar from "vue-avatar";
import ProtocolTable from "@/components/comparison/ProtocolTable";
import BlankBox from "@/components/comparison/BlankBox";
export default {
  beforeRouteEnter: (to, from, next) => {
    next(vm => {
      if (!vm.$store.getters.userState) {
        next("/login");
      } else {
        next();
      }
    });
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
  },
  components: {
    Avatar,
    "protocol-table": ProtocolTable,
    "blank-box": BlankBox
  },
  data() {
    var validatePass = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("Please enter your password again"));
      } else if (value !== this.formValidate.password) {
        callback(new Error("The two passwords are inconsistent!"));
      } else {
        callback();
      }
    };
    return {
      //走马灯属性
      carouseValue: 0,
      dataProtocal: {
        input1: "",
        input2: "",
        input3: ""
      },
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
      }
    };
  },
  methods: {
    // 初始化侧边栏样式
    initSize() {
      this.sidebarHeight = window.innerHeight - 290;
    },
    nextStep() {
      if (this.currentStep == 2) {
        this.$Notice.info({
          desc: "This is the final protocal !"
        });
      } else {
        this.currentStep += 1;
      }
    },
    lastStep() {
      if (this.currentStep == 0) {
        this.$Notice.info({
          desc: "This is the first protocal !"
        });
      } else {
        this.currentStep -= 1;
      }
    },
    getProjectInfo() {
      this.$api.cmp_project
        .getProjectAllInfo(this.projectId)
        .then(res => {
          console.log(res);
          this.projectInfo = res.project;
          this.modelList = res.model;
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
      this.$router.push({
        path: `/create-cmp-instance/${this.projectInfo.projectId}`
      });
    },
    createTask() {
      this.$router.push({
        path: `/create-cmp-task/${this.projectInfo.projectId}`
      });
    },
    instanceDetail(instance) {
      this.$router.push({
        path: `/cmp-instance-detail/${instance.instanceId}`
      });
    }
  },
  computed: {
    getProtocol() {
      return function(type) {
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
      return function(time) {
        return time ? time.split(" ")[0] : "";
      };
    }
  }
};
</script>