import base from "./base"
import axios from "../http"

const common = {
  createItem(itemJson) {
    return axios.post(`${base.common}/createItem`, itemJson);
  },
  findAllItem(type) {
    return axios.get(`${base.common}/findAllItem?type=${type}`);
  },
  findByX(type,key,value){
    return axios.get(`${base.common}/findByX?type=${type}&key=${key}&value=${value}`);
  },
  findResList(reqJson){
    return axios.post(`${base.common}/findResList`, reqJson);
  }
}

export default common;