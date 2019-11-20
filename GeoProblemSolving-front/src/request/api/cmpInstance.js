import base from "./base"
import axios from "../http"

const cmp_instance={
  createInstance(instance){
    return axios.post(`${base.cmpInstance}/createInstance`,instance);
  },
  getInstanceList(projectId){
    return axios.get(`${base.cmpInstance}/getInstanceList?projectId=${projectId}`)
  },
  findInstanceByInstanceId(instanceId){
    return axios.get(`${base.cmpInstance}/findInstanceByInstanceId?instanceId=${instanceId}`)
  },
  updateInstance(instance){
    return axios.post(`${base.cmpInstance}/updateInstance`,instance);
  },
  updateInstanceCmpData(jsonData){
    return axios.post(`${base.cmpInstance}/updateInstanceCmpData`,jsonData);
  }
}

export default cmp_instance;