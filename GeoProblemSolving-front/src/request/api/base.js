const appName="/GeoProblemSolving_Backend";
const cmpProject = "/cmp_project";
const cmpItem = "/cmp_item";
const cmpSolution = "/cmp_solution";
const cmpModel = "/cmp_model";
const cmpData = "/cmp_data";
const cmpInstance = "/cmp_instance";
const cmpTask = "/cmp_task";
const common = "/common";
/**
 * 接口域名管理
 */

 const base ={
   cmpProjcet: `${appName}${cmpProject}`,
   cmpItem:`${appName}${cmpItem}`,
   cmpSolution:`${appName}${cmpSolution}`,
   cmpModel:`${appName}${cmpModel}`,
   cmpData:`${appName}${cmpData}`,
   cmpInstance:`${appName}${cmpInstance}`,
   cmpTask:`${appName}${cmpTask}`,
   common:`${appName}${common}`,
 }

 export default base;