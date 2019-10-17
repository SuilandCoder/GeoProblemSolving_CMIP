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
      <!-- <FormItem prop="extent" label="Spatial Coverage" :label-width="150">
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
      </FormItem> -->
      <FormItem prop="metrics" label="Bind Metric" :label-width="150">
        <Input enter-button placeholder="Enter metric name" v-model="dataInfo.metrics.alias" style="width: auto"
          disabled>
        <Button slot="append" icon="ios-search" @click="search"></Button>
        </Input>
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
    <Modal v-model="modal13" draggable scrollable title="Create Metric">
      <create-metrics-form></create-metrics-form>
    </Modal>

    <Modal v-model="modal12" draggable scrollable title="Search Metric">
      <div slot="header" style="display:flex;align-items:center">
        <h3 style="display:inline;margin-right:20px;">Search Metric:</h3>
        <Input enter-button placeholder="Enter metric name" v-model="metricAlias"
          style="width: auto; display:inline-table">
        <Button slot="append" icon="ios-search" @click="search"></Button>
        <Button slot="append" icon="md-add" @click="createMetric"></Button>
        </Input>
      </div>

      <CellGroup>
        <Cell v-for="(metric,index) of metrics" :title="metric.wkName? metric.wkName: metric.alias" :key="metric.oid"
          @click="chooseMetric(metric)">
          <Button icon="ios-add" type="dashed" size="small" @click="chooseMetric(metric)" slot="extra"></Button>
        </Cell>
      </CellGroup>
    </Modal>

  </div>

</template>
<script>
import Util from "@/utils/comparison/cmpUtils";
import CreateMetricForm from "@/components/comparison/CreateMetricsForm";
export default {
  name: "specific-data-form",
  components: {
    "create-metrics-form": CreateMetricForm
  },
  data() {
    return {
      modal12: false,
      modal13: false,
      metricAlias: "",
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
        privacy: "private",
        metrics: {}
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
      loading: false,
      metrics: []
    };
  },
  created() {
    this.getMetrics();
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
              console.log("返回的数据信息：", res);
              this.$emit("createSuccess", res);
            })
            .catch(err => {
              this.loading = false;
              this.$Message.error(err);
            });
        }
      });
    },
    search() {
      this.findMetric();
      this.modal12 = true;
    },
    createMetric() {
      this.modal12 = false;
      this.modal13 = true;
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
    chooseMetric(metric) {
      this.dataInfo.metrics = metric;
      this.metricAlias = metric.alias;
      this.modal12 = false;
    }
  },
  computed: {}
};
</script>
<style scoped>
.inline {
  display: inline;
  margin-left: 10px;
}
</style>