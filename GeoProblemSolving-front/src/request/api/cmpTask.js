import base from "./base"
import axios from "../http"

const cmp_task = {
  uploadDataProcessMethod(dpm) {
    return axios.post(`${base.cmpTask}/uploadDataProcessMethod`, dpm);
  },
  getDataProcessMethod(type) {
    if (type) {
      return axios.get(`${base.cmpTask}/getDataProcessMethod?type=${type}`);
    } else {
      return axios.get(`${base.cmpTask}/getDataProcessMethod`);
    }
  }

}

export default cmp_task;