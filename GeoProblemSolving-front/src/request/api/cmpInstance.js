import base from "./base"
import axios from "../http"

const cmp_instance={
  createInstance(instance){
    return axios.post(`${base.cmpInstance}/createInstance`,instance);
  },
  getInstanceList(projectId){
    return axios.get(`${base.cmpInstance}/getInstanceList?projectId=${projectId}`)
  }
}

export default cmp_instance;