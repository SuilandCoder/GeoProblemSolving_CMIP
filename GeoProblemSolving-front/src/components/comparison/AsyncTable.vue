<template>
  <Table border :columns="tableColumn" :data="tableData">
  </Table>
</template>
<script>
export default {
  name: "async-table",
  created() {
    // * 向后台请求json数据
    // console.log("img url:", this.imgUrl);
    this.getTableJsonFromDataContainer(this.jsonUrl, this.fileName, this.suffix);
  },
  data() {
    return {
      tableColumn: [],
      tableData: []
    };
  },
  props: ["jsonUrl", "fileName", "suffix"],
  methods: {
    getTableJsonFromDataContainer(downloadUrl, fileName, suffix) {
      fileName = fileName + "." + suffix;
      let reqJson = { dataUrl: downloadUrl, fileName: fileName };
      this.axios
        .post(`/GeoProblemSolving_Backend/cmp_data/downloadDataFromDataContainer`, reqJson)
        .then(res => {
          if (res.data) {
            console.log("table json:", res.data)
            this.tableColumn = [
              { title: "Facors", key: "facors", align: "center" }
            ];
            for (let key in res.data) {
              if (key === 'labels') {
                res.data[key].forEach(label => {
                  let item = {
                    title: label,
                    key: label,
                    align: "center"
                  };

                  this.tableColumn.push(item);
                });
              } else {
                let item = {
                  facors: key,
                }
                this.tableData.push(item);
              }
            }

            res.data['labels'].forEach((label, index) => {
              this.tableData.forEach(data=>{
                let number = new Number(res.data[data.facors][index]);
                data[label] = number.toFixed(2);
              })
            })
          } else {
            this.$Message.error("Failed to download data");
          }
        })
        .catch(err => {
          this.$Message.error(err);
        })
    }
  }
}
</script>
<style scoped>
</style>