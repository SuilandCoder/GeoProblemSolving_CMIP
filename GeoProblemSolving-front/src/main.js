// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import Vuex from 'vuex';
import App from './App'
import router from './router'
import iView from 'iview'
import axios from 'axios'
import VueAxios from 'vue-axios'
// 滚轮样式
import vuescroll from 'vuescroll'
// import $ from 'jquery'
import 'iview/dist/styles/iview.css'
import store from './store/store.js'
//引入index.js文件夹
// import './mock/index'
import * as socketApi from './api/socket'
// import VCharts from 'v-charts';

//set globe CSS
import './assets/css/style.css'

//*********add by songjie******/
import mavonEditor from 'mavon-editor';
import 'mavon-editor/dist/css/index.css';
import api from "./request/api";
import _ from 'lodash';
import preview from 'vue-photo-preview'
import 'vue-photo-preview/dist/skin.css'

Vue.prototype.$api = api; //* 封装的 api，设置为Vue原型可避免每次使用都 import
Vue.prototype._ = _;
Vue.use(mavonEditor);
Vue.use(preview)
//*****************************/

axios.defaults.withCredentials=true;
Vue.prototype.socketApi = socketApi
Vue.config.productionTip = false

Vue.use(iView)
Vue.use(Vuex)
Vue.use(VueAxios,axios)
Vue.use(vuescroll)
// Vue.use(VCharts)

axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';
Vue.prototype.$axios = axios;
// 实例化对象
new Vue({
  el: '#app',
  store,
  router,
  components: { App },
  template: '<App/>',
  // components: { App },
  //   mounted () {
  //   document.dispatchEvent(new Event('render-event'))
  // }
})
