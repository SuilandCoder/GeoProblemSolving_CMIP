<template>
  <div>
    <Form ref="dataInfo" :model="dataInfo" :rules="rules">
      <FormItem prop="fileType" label="FileType" :label-width="150">
        <RadioGroup v-model="dataInfo.fileType">
          <Radio v-for="type of dataTypeList" :key="type.name" :label="type.label">{{type.name}}</Radio>
        </RadioGroup>
      </FormItem>

      <FormItem prop="name" label="Name" :label-width="150">
        <Input v-model="dataInfo.name" placeholder="" style="width: 300px" />
      </FormItem>

      <FormItem prop="abstractInfo" label="Abstract" :label-width="150" style="margin-top:10px">
        <div>
          <Input type="textarea" v-model="dataInfo.abstractInfo" placeholder="Enter abstract about this data" />
        </div>
      </FormItem>
      <FormItem prop="extent" label="Spatial Coverage" :label-width="150">
        <div>
          <label style="font-family: Courier;">W:</label>
          <Input v-model="dataInfo.extent[0]" style="width: 100px" number />
          <label style="font-family: Courier;">E:</label>
          <Input v-model="dataInfo.extent[1]" style="width: 100px" number />
        </div>
        <div style="margin-top:5px">
          <label style="font-family: Courier;">S:</label>
          <Input v-model="dataInfo.extent[2]" style="width: 100px" number />
          <label style="font-family: Courier;">N:</label>
          <Input v-model="dataInfo.extent[3]" style="width: 100px" number />
        </div>
      </FormItem>
      <FormItem prop="temporalCoverage" label="Temporal Coverage" :label-width="150">
        <DatePicker type="date" placeholder="Start time" style="width: 150px" v-model="dataInfo.temporalCoverage[0]">
        </DatePicker>
        <DatePicker type="date" placeholder="End time" style="width: 150px" v-model="dataInfo.temporalCoverage[1]">
        </DatePicker>
      </FormItem>
      <FormItem prop="dcSourceStoreId" label="File" :label-width="150">
        <Upload action="/GeoProblemSolving/cmp_model/uploadData_DC" :before-upload="beforeUpload" :data="uploadDataInfo"
          :on-success="uploadSuccess" :on-error="uploadError" :on-remove="removeData" ref="upload">
          <Button icon="ios-cloud-upload-outline">Upload files</Button>
        </Upload>
      </FormItem>
    </Form>
    <Button style="float:right" type="primary" @click="createDataResource" :loading="loading">
      create
    </Button>
  </div>

</template>
<script>
import Util from "@/utils/comparison/cmpUtils";
export default {
  name: "specific-data-form",
  data() {
    return {
      dataInfo: {
        name: "",
        fileType: "",
        abstractInfo: "",
        extent: new Array(4),
        temporalCoverage: new Array(2),
        dcSourceStoreId: "",
        url: "",
        md5: "",
        ownerName: this.$store.getters.userName,
        ownerId: this.$store.getters.userId,
        fileName: "",
        fileSize: "",
        privacy: "private"
      },
      uploadDataInfo: {
        stateName: "",
        eventName: "",
        md5: "",
        userName: this.$store.getters.userName,
        fileName: "",
        sourceStoreId: "",
        desc: "",
        type: ""
      },
      rules: {
        fileType: [
          {
            required: true,
            message: "Please select file type",
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
        abstractInfo: [
          {
            required: true,
            message: "Cannot be empty and no more than 300 characters",
            trigger: "blur",
            max: 300
          }
        ],
        dcSourceStoreId: [
          {
            required: true,
            message: "Please upload data"
          }
        ]
      },
      dataTypeList: this.$store.state.comparison.dataType,
      loading: false
    };
  },
  methods: {
    beforeUpload(file, $event) {
      this.dataInfo.dcSourceStoreId = "";
      this.$refs.upload.clearFiles();
      this.dataInfo.fileSize = file.size;
      this.uploadDataInfo.fileName = file.name;
      this.uploadDataInfo.sourceStoreId = "";
      this.uploadDataInfo.md5 = "";
      //计算 md5 值
      return new Promise((resolve, reject) => {
        //* 计算 md5
        Util.getFileMD5(file, md5 => {
          console.log(md5);
          this.uploadDataInfo.md5 = md5;
          //* 后台查询是否已经上传过。
          this.$api.cmp_model
            .matchDataMd5(md5)
            .then(res => {
              if (res.code == -9999) {
                //* 没有该资源  需要上传
                resolve();
              } else {
                //* 已有该资源
                this.uploadDataInfo.sourceStoreId = res.data;
                file = null;
                resolve();
              }
              console.log("md5:", res);
            })
            .catch(err => {
              this.$Message.error(err);
              reject();
            });
        });
      });
    },
    uploadSuccess(response) {
      if (response.code == 0) {
        let data = response.data;
        this.dataInfo.fileName = data.fileName;
        this.dataInfo.url = data.dataUrl;
        this.dataInfo.md5 = data.md5;
        this.dataInfo.dcSourceStoreId = data.sourceStoreId;
        console.log(this.dataInfo);
      } else {
        console.log(this.$refs.upload);
        this.$refs.upload.clearFiles();
        this.$Message.error(response.msg);
      }
    },
    uploadError() {
      console.log(this.$refs.upload);
      this.$refs.upload.clearFiles();
      this.$Message.error("upload data failed!");
    },
    removeData() {
      this.dataInfo.dcSourceStoreId = "";
    },
    createDataResource() {
      // console.log("dataInfo:", this.dataInfo);
      this.dataInfo.startTime = this.dataInfo.temporalCoverage[0];
      this.dataInfo.endTime = this.dataInfo.temporalCoverage[1];
      this.$refs["dataInfo"].validate(valid => {
        if (valid) {
          this.loading = true;
          this.$api.cmp_data
            .createDataResource(this.dataInfo)
            .then(res => {
              this.loading = false;
              console.log("返回的数据信息：",res);
              this.$emit("createSuccess",res);
            })
            .catch(err => {
              this.loading = false;
              this.$Message.error(err);
            });
        }
      });
      
    }
  },
  computed: {
  }
};
</script>
<style scoped>
.inline {
  display: inline;
  margin-left: 10px;
}
</style>