import Vue from 'vue'
import Router from 'vue-router'
// import 'mavon-editor/dist/css/index.css'
// import mavonEditor from 'mavon-editor'
Vue.use(Router)
// Vue.use(mavonEditor)


const routes = [{
  path: '/', name: 'Navigation', component: resolve => (require(["@/components/navigation"], resolve)), children: [
    { path: '', redirect: 'home' },
    { path: 'home', name: 'Home', component: resolve => (require(["@/components/navigationContent/home"], resolve)) },
    { path: 'projectlist', name: 'Projects', component: resolve => (require(["@/components/navigationContent/projectList"], resolve)) },
    { path: 'project/:id', name: 'ProjectDetail', component: resolve => (require(["@/components/projects/projectDetail"], resolve)) },
    { path: 'project/:id/workspace', name: 'workspace', component: resolve => (require(["@/components/workingSpace/moduleList"], resolve)) },
    { path: 'project/:id/stepCreation', name: 'stepCreation', component: resolve => (require(["@/components/workingSpace/stepCreation"], resolve)) },
    {
      path: 'project/:id/subproject/', name: 'subproject', component: resolve => (require(["@/components/subProject/subprojectNav"], resolve)), children: [
        { path: '', redirect: 'info' },
        { path: 'info', name: 'info', component: resolve => (require(["@/components/subProject/subprojectDetail"], resolve)) },
        { path: 'resource', name: 'resource', component: resolve => (require(["@/components/subProject/subResources"], resolve)) },
        { path: 'process', name: 'process', component: resolve => (require(["@/components/subProject/solvingStep"], resolve)) },
        { path: 'task', name: 'task', component: resolve => (require(["@/components/subProject/taskArrangement"], resolve)) },
      ]
    },
    { path: 'newproject', name: 'NewProject', component: resolve => (require(["@/components/projects/newProject"], resolve)) },
    { path: 'participants', name: 'Participants', component: resolve => (require(["@/components/navigationContent/participants"], resolve)) },
    { path: 'community', name: 'Community', component: resolve => (require(["@/components/community/community"], resolve)) },
    { path: 'community/:id', name: 'Communityreply', component: resolve => (require(["@/components/community/communityReply"], resolve)) },
    { path: 'help', name: 'Help', component: resolve => (require(["@/components/navigationContent/help"], resolve)) },
    { path: 'personalPage', name: 'PersonalPage', component: resolve => (require(["@/components/userPage/personalPage"], resolve)) },
    { path: 'notifications', name: 'Notifications', component: resolve => (require(["@/components/userState/notifications"], resolve)) },
    { path: 'memberPage/:id', name: 'MemberDetailPage', component: resolve => (require(["@/components/userPage/memberDetailPage"], resolve)) },
    { path: 'resourceList', name: 'resourceList', component: resolve => (require(["@/components/resources/resourceList"], resolve)) },
    { path: 'publicResource', name: 'PublicResource', component: resolve => (require(["@/components/resources/publicResourceList"], resolve)) },
    { path: 'join/:id/:email', name: 'joinProject', component: resolve => (require(["@/components/projects/joinNewProject"], resolve)) },
    { path: 'login', name: 'Login', component: resolve => (require(["@/components/userState/login"], resolve)) },
    { path: 'register', name: 'Register', component: resolve => (require(["@/components/userState/register"], resolve)) },
    { path: 'resetPassword/:email', name: 'resetPassword', component: resolve => (require(["@/components/userState/resetPwd"], resolve)) },


    { path: 'cmp-projectlist', name: 'cmp-projectlist', component: resolve => (require(["@/views/comparison/CmpProjectList"], resolve)) },
    { path: 'create-cmp-project', name: 'create-cmp-project', component: resolve => (require(["@/views/comparison/CreateProject"], resolve)) },
    { path: 'cmp-project/:id', name: 'cmp-project-detail', component: resolve => (require(["@/views/comparison/CmpProjectDetail"], resolve))},
    { path: 'cmp-project/comprehensive/:id', name: 'cmp-project-comprehensive', component: resolve => (require(["@/views/comparison/ComprehensiveProject"], resolve)) },
    { path: 'cmp-project/specific/:id', name: 'cmp-project-specific', component: resolve => (require(["@/views/comparison/SpecificProject"], resolve)) },
    { path: 'cmp-subproject/:id', name: 'cmp-subproject', component: resolve => (require(["@/views/comparison/SpecificProject"], resolve)) },
    { path: 'create-cmp-item/:id', name: 'create-cmp-item', component: resolve => (require(["@/views/comparison/CreateCmpItem"], resolve)) },
    { path: 'cmp-item/:id', name: 'cmp-item-detail', component: resolve => (require(["@/views/comparison/CmpItemDetail"], resolve)) },
    { path: 'create-cmp-solution/:id', name: "create-cmp-solution", component: resolve => (require(["@/views/comparison/CreateSolution"], resolve)) },
    { path: 'create-cmp-model/:id', name: "create-cmp-model", component: resolve => (require(["@/views/comparison/CreateModel"], resolve)) },
    { path: 'cmp-model/:projectTitle/:id', name: 'cmp-model-detail', component: resolve => (require(["@/views/comparison/CmpModelDetail"], resolve)) },
    { path: 'cmp-invoke-model/:id', name: 'cmp-invoke-model', component: resolve => (require(["@/views/comparison/InvokeModel"], resolve)) },
    { path: 'cmp-model-record/:id', name: 'cmp-model-record', component: resolve => (require(["@/views/comparison/CmpModelRecord"], resolve)) },
    { path: 'create-cmp-instance/:id', name:"create-cmp-instance", component:resolve=>(require(["@/views/comparison/CreateInstance"],resolve))},
    { path: 'create-cmp-task/:id', name:"create-cmp-task", component:resolve=>(require(["@/views/comparison/CreateTask"],resolve))},
    { path: 'create-dataprocess-method', name:"create-dataprocess-method", component:resolve=>(require(["@/views/comparison/CreateDataProcessMethod"],resolve))},
    { path: 'cmp-task-record/:id', name:"cmp-task-record", component:resolve=>(require(["@/views/comparison/CmpTaskRecord"],resolve))},
    { path: 'topic', name: 'topic', component: resolve => (require(["@/views/comparison/topic"], resolve)) },
    { path: 'create-tccm-project', name: 'create-tccm-project', component: resolve => (require(["@/views/comparison/CreateTccmProject"], resolve)) },
    { path: 'cmp-project/tccm/:id', name: 'cmp-project-tccm', component: resolve => (require(["@/views/comparison/CmpTccmProjectDetail"], resolve)) ,meta:{keepAlive: true,isBack:false, } },
    { path: 'cmp-instance-detail/:id', name: 'cmp-instance-detail', component: resolve => (require(["@/views/comparison/CmpInstanceDetail"], resolve)) },
  ]
},
// { path: '/chat', name: 'chatUtil', component: resolve => (require(["@/components/utils/chatroom"], resolve)) },
{ path: '/chat', name: 'chatUtil', component: resolve => (require(["@/components/comparison/chatroom"], resolve)) },
{ path: '/draw', name: 'drawUtil', component: resolve => (require(["@/components/utils/drawBoard"], resolve)) },
{ path: '/map', name: 'mapTool', component: resolve => (require(["@/components/utils/mapTool"], resolve)) },
{ path: '/charts', name: 'dataCharts', component: resolve => (require(["@/components/utils/charts"], resolve)) },
{ path: '/nc/draw', name: 'drawUtilNC', component: resolve => (require(["@/components/utils/singleUtils/ncDrawBoard"], resolve)) },
{ path: '/nc/map', name: 'mapToolNC', component: resolve => (require(["@/components/utils/singleUtils/ncMapTool"], resolve)) },
{ path: '/nc/charts', name: 'dataChartsNC', component: resolve => (require(["@/components/utils/singleUtils/ncCharts"], resolve)) },
{ path: '/video', name: 'videoViewer', component: resolve => (require(["@/components/utils/videoViewer"], resolve)) },
{ path: '/preview', name: 'pdfViewer', component: resolve => (require(["@/components/utils/filePreview"], resolve)) },
{ path: '/tinymce', name: 'tinymce', component: resolve => (require(["@/components/utils/singleUtils/tinymce"], resolve)) },
]
export default new Router({
  routes,
  mode: 'history',
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    } else {
      return { x: 0, y: 0 }
    }
  }
})
