<template>
  <div >
    <Table  class="table" size="small"  :max-height="400" border :columns="getCol" :data="protocolItems" >
      <template slot-scope="{row,index}" slot="name">
        <strong style="word-break:keep-all;font-size:16px">{{row.name}}</strong>
      </template>
      <template slot-scope="{row,index}" v-for="metaCol of getItemCol(getCol)" :slot="metaCol.slot">
        <span style="word-break:keep-all;font-size:16px">{{getMetaValue(metaCol.slot,row.metaDoc)}}</span>
      </template>

      <template slot-scope="{row,index}" slot="action">
        <Button size="small">Edit</Button>
      </template>
    </Table>
  </div>
</template>
<script>
export default {
  name: "protocol-table",
  data() {
    return {
    };
  },
  props: {
    protocolItems: {
      type: Array
    }
  },
  computed: {
    getCol() {
      if (this.protocolItems.length > 0) {
        let col = [
          {
            title: "Name",
            slot: "name",
            align: "center",
            fixed: "left",
            width: "150px",
            className: "font_size_18",
          }
        ];
        this.protocolItems.forEach(item => {
          item.metaDoc.forEach(meta => {
            let obj = {};
            obj.title = meta.key;
            obj.slot = meta.key;
            obj.align = "center";
            obj.className = "font_size_18";
            obj.minWidth = 150;
            // obj.resizable = true;
            let index = _.findIndex(col, function(o) {
              return o.title === obj.title;
            });
            if (index < 0) {
              col.push(obj);
            }
          });
        });
        let action = {
          title: "Action",
          slot: "action",
          align: "center",
          width: "100px",
          fixed: "right",
          "font-size":"18px"
        };
        col.push(action);
        return col;
      }
      return [];
    },
    getItemCol() {
      return function(cols) {
        if (cols) {
          return cols.filter(col => {
            return col.slot != "name" && col.slot != "action";
          });
        }
      };
    },
    getMetaValue() {
      return function(colName, metaDoc) {
        let index = _.findIndex(metaDoc, function(o) {
          return o.key === colName;
        });
        if (index >= 0) {
          return metaDoc[index].value;
        } else {
          return "-";
        }
      };
    }
  }
};
</script>
<style scoped>

.table{
  width: 100% !important;
}
</style>