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

.topic-title{
    font-size: 25px;
    color: black;
    font-weight:700;
    /* margin-top:5%; */

}

.sub-title{
    font-size: 18px;
    color: black;
    font-weight:400;
    /* margin-top:5%; */

}
.stepBtn{
  margin-left: 20%;
  margin-top: 20%
}
.mainContent{
  /* background-color: rgba(116, 111, 111, 0.2); */
  height: 700px;
}
</style>
<template>
  <div  class="mainContent">
    <Row >
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
              </div>
              <Tabs size="small">
                <TabPane label="Vocabulary">
                  <div>
                    <Input v-model="selectCardContent" disabled style="width: 200px" />
                    <Icon type="md-close" @click="clearInput" />
                    <Button @click="vocabularyValue = true">Select a concept</Button>
                  </div>
                </TabPane>
                <TabPane label="Data template">Data template</TabPane>
                <TabPane label="Unit">Unit</TabPane>
                <TabPane label="Reference">Spatio-temporal reference</TabPane>
              </Tabs>
              </Card>

              <!-- modal/drawer -->
              <Drawer title="Concepts" :closable="false" v-model="vocabularyValue">
                <Button style="margin-right: 8px" @click="vocabularyValue = false">Cancel</Button>
                <Button type="primary" @click="test">Submit</Button>
                <div  v-for="list in conceptResources" :key="list.oid">
                  <Card>
                    <div class="cardContent" @click="selectCard(list.name)">
                      {{list.name}}
                    </div>
                  </Card>
                </div>
              </Drawer>

          </Col>
          <!-- <Divider  type="vertical"/> -->
          <Col :lg="{span:18,offset:1}" :md="{span:15,offset:1}" :sm="{span:13,offset:1}" :xs="{span:11,offset:1}">
          <div class="rightContent">

            <Card>
              <Col offset="1">
                <!-- <p>当前正在进行第 {{ currentStep + 1 }} 步</p> -->
                <Steps :current="currentStep">
                  <Step title="Data protocal"></Step>
                  <Step title="Model process protocal"></Step>
                  <Step title="Model output protocal"></Step>
                </Steps>
                <div class="stepBtn">
                  <Button  @click="lastStep">Last Protocal</Button>
                  <Button type="primary" @click="nextStep">Next Protocal</Button>
                </div>
              </Col>
            </Card>
            <Col  :lg="11" :md="{span:11,offset:1}" :sm="{span:13,offset:1}" :xs="{span:11,offset:1}">
              <Card>
                <template>
                  instance
                </template>
              </Card>
            </Col>
            <Col  :lg="{span:11,offset:1}" :md="{span:11,offset:1}" :sm="{span:13,offset:1}" :xs="{span:11,offset:1}">
              <Card>
                <template>
                  task
                </template>
              </Card>
            </Col>
          </div>
          </Col>
        </Row>
      </Col>
    </Row>


  </div>
</template>
<script>
import Avatar from "vue-avatar";
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
  
  components: {
    Avatar
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
      carouseValue: 0 ,
      dataProtocal: {
        input1: '',
        input2: '',
        input3: ''
      },
       currentStep: 0,
       vocabularyValue :false,
       resourceType:"concept",
       conceptResources:[],
       selectCardContent:"",
    };
  },

  created() {
    this.initSize();
  },
  mounted(){
    this.getAllConcept();
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
    // 获得后台所有的vocabulary
    getAllConcept() {
      this.resourceType = "concept";
      this.axios
        .get("/GeoProblemSolving/common/findAllItem?type=" + this.resourceType)
        .then(res => {        
            this.conceptResources = res.data.data;//异步
        });
    },

    selectCard(selected){
      this.selectCardContent = selected;
      console.log(id);
      console.log(123);
    },

    // 清除选择的内容
    clearInput(){
      this.selectCardContent = "";
    },

    
  }
};
</script>