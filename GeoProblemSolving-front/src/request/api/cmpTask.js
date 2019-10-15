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
  },
  getDataProcessMethodById(methodId){
    return axios.get(`${base.cmpTask}/getDataProcessMethodById?methodId=${methodId}`);
  },
  createTask(taskJsonInfo){
    return axios.post(`${base.cmpTask}/createTask`, taskJsonInfo);
  },
  getCmpTasks(projectId){
    return axios.get(`${base.cmpTask}/getCmpTasks?projectId=${projectId}`);
  },
  getCmpTaskRecord(recordId){
    return axios.get(`${base.cmpTask}/getCmpTaskRecord?recordId=${recordId}`);
  },
  getCmpMethodRecord(recordId){
    return axios.get(`${base.cmpTask}/getCmpMethodRecord?recordId=${recordId}`);
  },
  getCmpMethodRecordList(recordList){
    return axios.post(`${base.cmpTask}/getCmpMethodRecordList`, recordList);
  }

}

export default cmp_task;