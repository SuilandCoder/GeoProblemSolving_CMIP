<template>
  <div>
    <Form ref="metricInfo" :model="metricInfo" :rules="rules">

      <FormItem prop="name" label="Name" :label-width="150">
        <Input v-model="metricInfo.name" placeholder="" style="width: 300px" />
      </FormItem>
      <FormItem prop="alias" label="Alias" :label-width="150">
        <Input v-model="metricInfo.alias" placeholder="" style="width: 300px" />
      </FormItem>

      <FormItem prop="description" label="Description" :label-width="150" style="margin-top:10px">
        <div>
          <Input type="textarea" v-model="metricInfo.description" placeholder="Enter abstract about this data" />
        </div>
      </FormItem>

      <FormItem prop="unit" label="Unit" :label-width="150">
        <Input enter-button placeholder="Enter unit" v-model="metricInfo.unit.alias" style="width: auto" disabled>
        <Button slot="append" icon="ios-search" @click="search('unit')"></Button>
        </Input>
      </FormItem>
      <FormItem prop="srf" label="Spatial Reference" :label-width="150">
        <Input enter-button placeholder="Enter spatial reference" v-model="metricInfo.srf.name" style="width: auto"
          disabled>
        <Button slot="append" icon="ios-search" @click="search('srf')"></Button>
        </Input>
      </FormItem>
      <FormItem prop="template" label="Template" :label-width="150">
        <Input enter-button placeholder="Enter template" v-model="metricInfo.template.name" style="width: auto"
          disabled>
        <Button slot="append" icon="ios-search" @click="search('template')"></Button>
        </Input>
      </FormItem>
    </Form>

    <Modal v-model="searchModal" >
      <div slot="header" style="display:flex;align-items:center">
        <h3 style="display:inline;margin-right:20px;">Search :</h3>
        <Input enter-button placeholder="Enter metric name" v-model="searchValue"
          style="width: auto; display:inline-table">
        <Button slot="append" icon="ios-search" @click="searchList"></Button>
        </Input>
      </div>

      <CellGroup>
        <Cell v-for="(item,index) of getList" :title="item.alias? item.alias: item.name" :key="item.oid">
          <Button icon="ios-add" type="dashed" size="small" @click="chooseMetric(item)" slot="extra"></Button>
        </Cell>
      </CellGroup>
    </Modal>

  </div>
</template>
<script>
export default {
  name: "create-metrics-form",
  data() {
    return {
      searchModal: false,
      searchType: "",
      searchValue: "",
      metricInfo: {
        name: "",
        alias: "",
        description: "",
        unit: {},
        srf: {},
        template: {}
      },
      units: [],
      srfs: [],
      templates: [],
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
        ]
      }
    };
  },
  methods: {
    search(type) {
      this.searchModal = true;
      this.searchType = type;
    },
    getList() {
      if (type === "unit") {
        return this.units;
      } else if (type === "srf") {
        return this.srfs;
      } else if (type === "template") {
        return this.templates;
      } else {
        return [];
      }
    },
    chooseMetric(item) {
      if (type === "unit") {
        this.metricInfo.unit = item;
      } else if (type === "srf") {
        this.metricInfo.srf = item;
      } else if (type === "template") {
        this.metricInfo.template = item;
      }
    },
    searchList(){
      this.$api.common
        .findByX("metrics", "alias", this.metricAlias)
        .then(res => {
          this.metrics = res;
        })
        .catch(error => {
          this.$Message.error(error);
        });
    },
  }
};
</script>
<style scoped>
</style>