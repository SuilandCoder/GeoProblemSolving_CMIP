<template>
  <div class="projectCreatForm">
    <h1>Create Comparison Projcet</h1>
    <Content :style="{padding: '24px 0', minHeight: '280px', background: '#fff', 'margin-left': '10%'}">
      <Layout>
        <Sider hide-trigger :style="{background: '#fff'}">
          <Menu active-name="1" @on-select="showTypeOne($event)" theme="light" width="auto" :open-names="['1']"
            :style="{'z-index':1}">
            <MenuItem name="1">Comprehensive Project</MenuItem>
            <MenuItem name="2">Specific Project</MenuItem>
          </Menu>
        </Sider>
        <Content :style="{padding: '10px', minHeight: '280px', background: '#fff'}">
          <div v-show="projectType==1">
            <Form ref="project1" :rules="createProject1Rule" :model="project1" class="projectForm">
              <!-- 选择类别 -->
              <FormItem prop="category" label="Category" :label-width="150">
                <RadioGroup v-model="project1.category" style="width:80%">
                  <Radio label="Terrestrial">Terrestrial System</Radio>
                  <Radio label="Coastal">Coastal System</Radio>
                  <Radio label="Marine">Marine System</Radio>
                  <Radio label="Climate">Climate System</Radio>
                  <Radio label="Ecological">Ecological System</Radio>
                  <Radio label="Geological">Geological System</Radio>
                  <Radio label="Human">Human-Activity</Radio>
                  <Radio label="GISRS">GIS & RS</Radio>
                  <Radio label="General">General</Radio>
                </RadioGroup>
              </FormItem>

              <FormItem prop="title" label="Title" :label-width="150">
                <Input v-model="project1.title" placeholder="Enter Title ..." />
              </FormItem>

              <FormItem prop="description" label="Description" :label-width="150">
                <div>
                  <Input type="textarea" v-model="project1.description"
                    placeholder="Enter detailed introduction about this project" />
                </div>
              </FormItem>

              <FormItem>
                <div class="inline_style">
                  <Button type="success" @click='createProject("project1")' class="create">Create</Button>
                </div>
              </FormItem>
            </Form>
          </div>

          <div v-show="projectType==2">
            <Form ref="project" :rules="createProjectRule" :model="project" class="projectForm">
              <!-- 选择类别 -->
              <FormItem prop="category" label="Category" :label-width="150">
                <RadioGroup v-model="project.category" style="width:80%">
                  <Radio label="Terrestrial">Terrestrial System</Radio>
                  <Radio label="Coastal">Coastal System</Radio>
                  <Radio label="Marine">Marine System</Radio>
                  <Radio label="Climate">Climate System</Radio>
                  <Radio label="Ecological">Ecological System</Radio>
                  <Radio label="Geological">Geological System</Radio>
                  <Radio label="Human">Human-Activity</Radio>
                  <Radio label="GISRS">GIS & RS</Radio>
                  <Radio label="General">General</Radio>
                </RadioGroup>
              </FormItem>

              <FormItem prop="title" label="Title" :label-width="150">
                <Input v-model="project.title" placeholder="Enter Title ..." />
              </FormItem>

              <FormItem prop="description" label="Description" :label-width="150">
                <div>
                  <Input type="textarea" v-model="project.description"
                    placeholder="Enter detailed introduction about this project" />
                </div>
              </FormItem>

              <FormItem prop="evaluationRules" label="Evaluation Rules" :label-width="150">
                <div>
                  <Input type="textarea" v-model="project.evaluationRules"
                    placeholder="Enter the evaluation rules of this project" />
                </div>
              </FormItem>

              <FormItem prop="specificModel" label="Specify Model" :label-width="150">
                <Input v-model="modelTag" placeholder="Specify comparison model" style="width: 400px"
                  @keyup.enter.native="addModel(modelTag)" @on-blur="addModel(modelTag)"/>
                <Button icon="ios-add" type="dashed" size="small" @click="addModel(modelTag)"
                  style="margin-left:2.5%">Add Model</Button>
                <div>
                  <Tag color="primary" v-for="(item,index) in this.project.specificModel" :key="index" closable
                    @on-close="deleteModel(index)">{{item}}</Tag>
                </div>
              </FormItem>

              <FormItem prop="outputDataList" label="Specify Output Data" :label-width="150">
                <Input v-model="outputDataTag" placeholder="Specify output data" style="width: 400px"
                  @keyup.enter.native="addData(outputDataTag)" @on-blur="addData(outputDataTag)"/>
                <Button icon="ios-add" type="dashed" size="small" @click="addData(outputDataTag)"
                  style="margin-left:2.5%">Add Data</Button>
                <div>
                  <Tag color="primary" v-for="(item,index) in this.project.outputDataList" :key="index" closable
                    @on-close="deleteData(index)">{{item}}</Tag>
                </div>
              </FormItem>

              <FormItem prop="haveStandardInputData" label="Standard Input Data" :label-width="150">
                <RadioGroup v-model="project.standardInputData" style="width:80%;margin-left:10px">
                  <Radio label="Provide" value="Provide"
                    title="The project creator needs to upload standard input data"></Radio>
                  <Radio label="NotProvide" value="NotProvide"
                    title="The project creator will not prepare standard input data.">Not Provide</Radio>
                  <div style="color:cornflowerblue">To ensure the fairness of the comparison results, please provide
                    standard input data.</div>
                </RadioGroup>
              </FormItem>
              <FormItem prop="haveEvaluatedData" label="Evaluated Data" :label-width="150">
                <RadioGroup v-model="project.evaluatedData" style="width:80%;margin-left:10px">
                  <Radio label="Provide" value='Provide' title="The project creator needs to upload evaluated data">
                  </Radio>
                  <Radio label="NotProvide" value='NotProvide'
                    title="The project creator will not prepare evaluated data.">Not Provide</Radio>
                  <div style="color:cornflowerblue">Evaluated data will be used to verify the model's output data. Could
                    be observation data.</div>
                </RadioGroup>

              </FormItem>

              <FormItem>
                <div class="inline_style">
                  <Button type="success" @click='createProject("project")' class="create">Create</Button>
                </div>
              </FormItem>
            </Form>
          </div>
        </Content>
      </Layout>
    </Content>

  </div>
</template>

<script>
export default {
  beforeRouteEnter: (to, from, next) => {
    next(vm => {
      $.ajax({
        url: "/GeoProblemSolving_Backend/user/state",
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
  data() {
    return {
      projectType: 1,
      project1: {
        title: "",
        description: "",
        category: "",
        projectType: "COMPREHENSIVE"
      },
      project: {
        title: "",
        description: "",
        category: "",
        projectType: "SPECIFIC",
        evaluationRules: "",
        //model
        specificModel:[],
        //output data
        outputDataList: [],
        standardInputData: "Provide",
        evaluatedData: "Provide",
        startTime: "",
        endTime: "",
        location: "",
        timeInterval: "",
        scale: "",
        resolution: ""
      },
      createProjectRule: {
        title: [
          {
            required: true,
            message:
              "The title cannot be empty and no more than 200 characters",
            trigger: "blur",
            max: 200
          }
        ],
        category: [
          {
            required: true,
            message: "Please select category",
            trigger: "change"
          }
        ],
        description: [
          {
            required: true,
            message:
              "The description cannot be empty and no more than 600 characters",
            trigger: "blur",
            max: 600
          }
        ],
        evaluationRules: [
          {
            required: true,
            message:
              "The evaluation rules cannot be empty and no more than 300 characters",
            trigger: "blur",
            max: 300
          }
        ]
      },
      createProject1Rule: {
        title: [
          {
            required: true,
            message:
              "The title cannot be empty and no more than 200 characters",
            trigger: "blur",
            max: 200
          }
        ],
        category: [
          {
            required: true,
            message: "Please select category",
            trigger: "change"
          }
        ],
        description: [
          {
            required: true,
            message:
              "The description cannot be empty and no more than 600 characters",
            trigger: "blur",
            max: 600
          }
        ]
      },
      //用来存储模型标签
      modelTag: "",
      //用来存储输出数据标签
      outputDataTag: ""
    };
  },
  methods: {
    createProject(name) {
      this.$refs[name].validate(valid => {
        if (valid) {
          let projectInfo = {};
          if (this.projectType == 1) {
            projectInfo = this.project1;
          } else {
            projectInfo = this.project;
          }
          projectInfo["managerId"] = this.$store.getters.userId;
          // console.log(projectInfo);
          this.$api.cmp_project
            .create(projectInfo)
            .then(res => {
              // this.$Message.info(res);
              console.log(res);
              this.$store.commit("comparison/xSet", { projectInfo: res });
              // console.log(this.$store.state.comparison.projectInfo);
              if (this.projectType == 1) {
                this.$router.push({
                  path: `/cmp-project/comprehensive/${res.projectId}`
                });
              } else {
                this.$router.push({
                  path: `/cmp-project/specific/${res.projectId}`
                });
              }
            })
            .catch(error => {
              this.$Message.error(error);
            });
        }
      });
    },
    addModel(tag) {
      if (tag != "") {
        this.project.specificModel.push(tag);
        this.modelTag = "";
      }
    },
    addData(tag) {
      if (tag != "") {
        this.project.outputDataList.push(tag);
        this.outputDataTag = "";
      }
    },
    deleteModel(index) {
      this.project.specificModel.splice(index, 1);
    },
    deleteData(index) {
      this.project.outputDataList.splice(index, 1);
    },
    showTypeOne($event) {
      this.projectType = $event;
    }
  }
};
</script>

<style scoped>
.projectCreatForm {
  margin-left: 10%;
}
h1 {
  margin-top: 2.5%;
  text-align: center;
}
.projectForm {
  width: 80%;
}

.create {
  width: 20%;
  margin-right: 40%;
  margin-left: 40%;
}
</style>
