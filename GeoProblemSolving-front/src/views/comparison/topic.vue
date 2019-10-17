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
  margin-bottom:2%;
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
                Title
              </div>

              <div class="single-info">
                  <Icon type="ios-leaf" :size="20" />
                  <span>Background</span>
              </div>

              <div class="single-info" >
                  <Icon type="md-people" :size="20" />
                  <span>Member</span>
              </div>
              <div class="single-info" >
                  <Icon type="md-person" :size="20" />
                  <span>Creator</span>
              </div>
              <div class="single-info" >
                  <Icon type="md-time" :size="20" />
                  <span>Created Time</span>
              </div>

            </Card>

              <Divider />
              <Card>
              <div class="sub-title">
                Information
                <Button style="float:right" type="primary" ghost @click="modalCreate = true">Create</Button>
              </div>
              <Tabs size="small" @on-click="getAllConcept">
                <TabPane label="Vocabulary"  name="concept">                   
                    <div v-for="sellist in selectCardContent" :key="sellist.id">
                      <Tag color="primary" >{{sellist.name}}</Tag>
                      <Icon type="md-close" @click="clearInput(sellist.id)" />
                    </div>
                    <Button @click="vocabularyValue = true" >Choose concepts</Button>
                </TabPane>
                <TabPane label="Data template"  name="template">
                   <div v-for="sellist in selectCardContent" :key="sellist.id">
                      <Tag color="primary" >{{sellist.name}}</Tag>
                      <Icon type="md-close" @click="clearInput(sellist.id)" />
                    </div>
                    <Button @click="vocabularyValue = true">Choose templates</Button>
                </TabPane>
                <TabPane label="Unit"  name="unit">
                  <div v-for="sellist in selectCardContent" :key="sellist.id">
                      <Tag color="primary" >{{sellist.name}}</Tag>
                      <Icon type="md-close" @click="clearInput(sellist.id)" />
                    </div>
                    <Button @click="vocabularyValue = true">Choose templates</Button>
                </TabPane>
                <TabPane label="Reference"  name="spatialref">
                  <div v-for="sellist in selectCardContent" :key="sellist.id">
                      <Tag color="primary" >{{sellist.name}}</Tag>
                      <Icon type="md-close" @click="clearInput(sellist.id)" />
                    </div>
                    <Button @click="vocabularyValue = true">Choose templates</Button>
                </TabPane>
              </Tabs>
              </Card>

              <!-- modal/drawer -->
              <Drawer title="Concepts" :closable="false" v-model="vocabularyValue">
                <!-- <Input search enter-button placeholder="Search" style="width: auto"   @on-search="searchCard"/> -->
                 <Input placeholder="Enter text" style="width: auto" v-model="inputValue"/> 
                <Button  @click="searchCard(inputValue)"></Button>
                
                <div v-for="list in typeResources" :key="list.oid">
                  <Card>
                    <div class="cardContent" @click="selectCard(list.name,list.oid)">
                      {{list.name}}
                    </div>
                  </Card>
                </div>
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
                    <Input v-model="createForm.description" type="textarea" :autosize="{minRows: 1,maxRows: 3}"
                      clearable />
                  </FormItem>
                </Form>
                <div slot="footer">
                  <Button @click="cancelCreate">Cancel</Button>
                  <Button type="primary" @click="submitCreate">Create</Button>
                </div>
              </Modal>

          </Col>


        <!-- right -->
        <Col :lg="{span:18,offset:1}" :md="{span:15,offset:1}" :sm="{span:13,offset:1}" :xs="{span:11,offset:1}">
          <div class="rightContent">

            <Card >
              
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
      projectId: this.$route.params.id,
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
       vocabularyValue :false,
       resourceType:"concept",
       typeResources:[],
       selectCardContent:[],
       searchCardContent:[],
       showTag:true,
       modalCreate:false,
       createForm:{
        name: "",
        description: "",
        type:""
      },
      modalType:[
        {
          value:"concept",
          label:"Vocabulary"
        },{
          value:"template",
          label:"Data template"
        },{
          value:"unit",
          label:"Unit"
        },{
          value:"spatialref",
          label:"Spatio-temporal reference"
        },
        ],
        modelType1:"concept",
        inputValue:""
    };
  },

  created() {
    this.initSize();
  },
  mounted(){
    this.getAllConcept("concept");
    this.getProjectInfo();
    // this.selectCardContent=[];
  },

  methods: {
    // 初始化侧边栏样式
    initSize() {
      this.sidebarHeight = window.innerHeight - 290;
    },
    
    // 获得后台所有的vocabulary
    getAllConcept(type) {
      this.selectCardContent=[];
      this.resourceType = type;
      this.axios
        .get("/GeoProblemSolving/common/findAllItem?type=" + this.resourceType)
        .then(res => {        
            this.typeResources = res.data.data;//异步
        });
    },

    selectCard(selected,oid){
      let list = {"name":selected,"id":oid};
      // 检查重复的tag
      if(this.selectCardContent.length == 0){
        this.selectCardContent.push(list);
      }else{
        for(let i= 0; i < this.selectCardContent.length ; i++ ){
          if(this.selectCardContent[i].id == oid)
          {
            this.$Notice.info({
                desc: "The tag has been exist!"
            });
            break;
          }else if(i == this.selectCardContent.length-1 ){
            this.selectCardContent.push(list);
            break;
          } 
        }  
      }   
      console.log( this.selectCardContent);
    },

    // 模糊搜索card
    searchCard(value){
      console.log(value);
       this.axios
        .get("/GeoProblemSolving/common/findByX?type=" + this.resourceType+
          "&key=name" +
            "&value=" +
            value
        )
        .then(res => {  
            console.log(res.data);
            this.searchCardContent = res.data.data;//异步
      });
    },

    // 清除选择的内容
    clearInput(id){
      console.log(id)
      // this.selectCardContent = "";
      for(let i= 0; i<this.selectCardContent.length ; i++ ){
        if(this.selectCardContent[i].id == id)
        {
          this.selectCardContent.splice(i,1);
        }
      }
    },

    // modal
    submitCreate(){
      let obj = {};
      let objData = {};
      obj.type = this.createForm.type;
      objData.name = this.createForm.name;
      objData.description = this.createForm.description;
      objData.author = this.$store.getters.userName;
      objData.authorId = this.$store.getters.userId;
      obj.data = objData;
      // obj.append("data.name", this.createForm.name);
      // obj.append("data.description", this.createForm.description);
      // obj.append("type", this.createForm.type);

       this.axios
        .post("/GeoProblemSolving/common/createItem", obj)
        .then(res => {
          console.log(res);
          // if (res.data != "Fail") {
          //   this.$Notice.info({
          //     desc: "Update successfully!"
          //   });
          //   this.stepContent.name = this.stepForm.name;
          //   this.stepContent.description = this.stepForm.description;
          // } else {
          //   this.$Message.error("Update step failed.");
          // }
        })
        .catch(err => {
          console.log(err.data);
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
    },
     handleClose() {
       this.showTag = false;
     }
  }
};
</script>