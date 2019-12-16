import base from "./base"
import axios from "../http"

const cmp_model = {
  getModelInfo(modelId) {
    return axios.get(`${base.cmpModel}/getModelInfo?modelId=${modelId}`);
  },
  create(model) {
    return axios.post(`${base.cmpModel}/createModel`, model);
  },
  deployModel(formData) {
    return axios.post(`${base.cmpModel}/deployModel`, formData);
  },
  matchMd5(md5) {
    return axios.get(`${base.cmpModel}/matchMd5?md5=${md5}`);
  },
  getComputableModelInfo(modelId) {
    return axios.get(`${base.cmpModel}/getComputableModelInfo?modelId=${modelId}`);
  },
  getComputableModelById(oid) {
    return axios.get(`${base.cmpModel}/getComputableModelById?oid=${oid}`);
  },
  getTaskServer(pid) {
    return axios.get(`${base.cmpModel}/getTaskServer?pid=${pid}`);
  },
  getDataServerNode(formData) {
    return axios.post(`${base.cmpModel}/getDataServerNode`, formData);
  },
  invokeModel(formData) {
    return axios.post(`${base.cmpModel}/invokeModel`, formData);
  },
  matchDataMd5(md5) {
    return axios.get(`${base.cmpModel}/matchDataMd5?md5=${md5}`);
  },
  uploadData_DC(formData) {
    return axios.post(`${base.cmpModel}/uploadData_DC`, formData);
  },
  uploadData_DC_Fast(formData) {
    return axios.post(`${base.cmpModel}/uploadData_DC_Fast`, formData);
  },
  invokeModel_MC(formData) {
    return axios.post(`${base.cmpModel}/invokeModel_MC`, formData,
      {
        headers: {
          "Content-Type": 'text/plain'
        }
      });
  },
  getRecordInfo(recordId) {
    return axios.get(`${base.cmpModel}/getRecordInfo?recordId=${recordId}`);
  },
  getRecordList(recordList) {
    return axios.get(`${base.cmpModel}/getRecordList?recordList=${recordList}`);
  },
  updateModelResource(modelInfo) {
    return axios.post(`${base.cmpModel}/updateModelResource`, modelInfo);
  }

}

export default cmp_model;