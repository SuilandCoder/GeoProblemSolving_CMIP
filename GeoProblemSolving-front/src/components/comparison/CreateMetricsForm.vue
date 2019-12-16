<template>
  <div>
    <Form ref="metricInfo" :model="metricInfo" :rules="rules">

      <FormItem prop="alias" label="WkName" :label-width="150">
        <Input v-model="metricInfo.alias" placeholder="" style="width: 300px" />
      </FormItem>
      <FormItem prop="name" label="Full Name" :label-width="150">
        <Input v-model="metricInfo.name" placeholder="" style="width: 300px" />
      </FormItem>

      <FormItem prop="description" label="Description" :label-width="150" style="margin-top:10px">
        <div>
          <Input type="textarea" v-model="metricInfo.description" placeholder="Enter abstract about this data" />
        </div>
      </FormItem>

      <FormItem prop="unit" label="Unit" :label-width="150">
        <Input enter-button placeholder="Enter unit" v-model="metricInfo.unit" style="width: auto">
        </Input>
      </FormItem>
      <FormItem prop="srf" label="Spatial Reference" :label-width="150">
        <Input enter-button placeholder="Enter spatial reference" v-model="metricInfo.srf" style="width: auto">
        </Input>
      </FormItem>
      <FormItem prop="template" label="Template" :label-width="150">
        <Input enter-button placeholder="Enter template" v-model="metricInfo.template" style="width: auto">
        </Input>
      </FormItem>
    </Form>
    <Button style="float:right" type="primary" @click="createMetrics" :loading="loading">
      create
    </Button>
  </div>
</template>
<script>
export default {
  name: "create-metrics-form",
  data() {
    return {
      loading: false,
      metricInfo: {
        name: "",
        alias: "",
        description: "",
        unit: "",
        srf: "",
        template: ""
      },
      units: [],
      srfs: [],
      templates: [],
      rules: {
        alias: [
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
        ]
      }
    };
  },
  methods: {
    createMetrics() {
      this.loading = true;
      let obj = {
        type: "metrics",
        data: this.metricInfo
      };
      this.$api.common
        .createItem(obj)
        .then(res => {
          this.loading = false;
          console.log("返回的数据信息：", res);
          this.$emit("createMetricSuccess", res);
        })
        .catch(err => {
          this.loading = false;
          this.$Message.error(err);
        });
    }
  }
};
</script>
<style scoped>
</style>