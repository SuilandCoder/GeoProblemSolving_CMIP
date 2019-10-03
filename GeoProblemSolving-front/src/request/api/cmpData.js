import base from "./base"
import axios from "../http"

const cmp_data={
  createDataResource(dataInfo){
    return axios.post(`${base.cmpData}/createDataResource`,dataInfo);
  },
  getDataResourceByIdList(idList){
    return axios.post(`${base.cmpData}/getDataResourceByIdList`,idList);
  }
}

export default cmp_data;