<template>
  <div class="box">
    <h2 style="text-align:center;margin:20px;">Create Instance</h2>
    <Form ref="instanceInfo" :model="instanceInfo" :rules="rules">
      <FormItem prop="name" label="Name" :label-width="150">
        <Input v-model="instanceInfo.name"  style="width: 300px" placeholder="Enter instance name" />
      </FormItem>
      <FormItem prop="description" label="Description" :label-width="150">
        <div>
          <Input type="textarea" v-model="instanceInfo.description" placeholder="Enter description about this instance"
            style="width: 500px" />
        </div>
      </FormItem>
      <FormItem prop="type" label="Type" :label-width="150">
        <RadioGroup v-model="instanceInfo.type">
          <Radio label="observation">Observation Dataset</Radio>
          <Radio label="benchmark">Benchmark Dataset</Radio>
          <Radio label="model">Model Experiment</Radio>
        </RadioGroup>
      </FormItem>
    </Form>
    <!-- <specific-data-form></specific-data-form> -->
    <div style="height:30px; display: flex; justify-content: space-between; margin-left:100px">
      <h3 style="display:inline">Data List:</h3>
      <Button style="float:right" @click="showUploadDialog()" type="primary">Add Data</Button>
    </div>
    <Table border :columns="dataColumn" :data="dataList" style="margin-left:150px;">
      <template slot-scope="{row,index}" slot="action">
        <Button v-if="row.fileName" @click="deleteDataItem(index)">Delete</Button>
        <Button v-else @click="showUploadDialog(row)">Upload</Button>
      </template>
    </Table>
    <Button style="margin-top: 50px;  margin-bottom: 20px; margin-left: 150px;" type="primary" :loading="loading"
      @click="creataInstance">Create</Button>

    <Modal ref="modal" title="Upload Data" v-model="uploadmodal" @on-ok="upload" :mask-closable="false">
      <specific-data-form ref="dataForm" v-on:createSuccess="onCreateSuccess"></specific-data-form>
      <span slot="footer"></span>
    </Modal>
  </div>
</template>
<script>
import SpecificDataForm from "@/components/comparison/SpecificDataForm";
export default {
  components: {
    "specific-data-form": SpecificDataForm
  },
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
  created: function() {
    //获取 project 指定的输出数据信息
    this.instanceInfo.projectId = this.$route.params.id;
  },
  data() {
    return {
      instanceInfo: {
        name: "",
        description: "",
        type: "observation",
        cmpDataList: [],
        userName: this.$store.getters.userName,
        userId: this.$store.getters.userId,
        projectId: ""
      },
      dataList: [],
      dataColumn: [
        {
          title: "Name",
          key: "name",
          align: "center"
        },
        {
          title: "Type",
          key: "fileType",
          align: "center"
        },
        {
          title: "AbstractInfo",
          key: "abstractInfo",
          align: "center"
        },
        {
          title: "File Name",
          key: "fileName",
          align: "center"
        },
        {
          title: "File Size",
          key: "fileSize",
          align: "center"
        },
        {
          title: "Action",
          slot: "action",
          align: "center"
        }
      ],
      rules: {
        type: [
          {
            required: true,
            message: "Please select instance type",
            trigger: "change"
          }
        ],
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
        ]
      },
      uploadmodal: false,
      dataInfo: {},
      loading: false
    };
  },
  methods: {
    showUploadDialog() {
      this.uploadmodal = true;
    },
    deleteDataItem(index) {
      this.dataList.splice(index, 1);
    },
    upload() {
      this.$refs.dataForm.createDataResource();
    },
    onCreateSuccess(data) {
      this.dataList.push(data);
      console.log(data);
      this.uploadmodal = false;
    },
    creataInstance() {
      this.$refs["instanceInfo"].validate(valid => {
        if (valid) {
          //* 判断 datalist
          this.instanceInfo.cmpDataList = this.dataList.map(data => {
            if (data.fileName) {
              return data.dataId;
            }
          });
          if (this.instanceInfo.cmpDataList.length > 0) {
            this.loading = true;
            this.$api.cmp_instance
              .createInstance(this.instanceInfo)
              .then(res => {
                this.loading = false;
                console.log("返回的数据信息：", res);
                this.$router.back(-1);
              })
              .catch(err => {
                this.loading = false;
                this.$Message.error(err);
              });
          } else {
            this.$Message.error("please upload data");
          }
        }
      });
    }
  }
};
</script>
<style scoped>
.box {
  width: 1000px;
  margin-left: auto;
  margin-right: auto;
}
</style>