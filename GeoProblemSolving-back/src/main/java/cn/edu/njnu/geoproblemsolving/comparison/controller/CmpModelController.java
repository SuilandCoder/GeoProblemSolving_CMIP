package cn.edu.njnu.geoproblemsolving.comparison.controller;

import cn.edu.njnu.geoproblemsolving.comparison.bean.JsonResult;
import cn.edu.njnu.geoproblemsolving.comparison.constant.HttpContant;
import cn.edu.njnu.geoproblemsolving.comparison.dao.computablemodel.ComputableModelImpl;
import cn.edu.njnu.geoproblemsolving.comparison.dao.modelrecord.ModelRecordDaoImpl;
import cn.edu.njnu.geoproblemsolving.comparison.dao.modelresource.ModelResourceDaoImpl;
import cn.edu.njnu.geoproblemsolving.comparison.dao.solution.SolutionImpl;
import cn.edu.njnu.geoproblemsolving.comparison.entity.*;
import cn.edu.njnu.geoproblemsolving.comparison.enums.ResultEnum;
import cn.edu.njnu.geoproblemsolving.comparison.service.CmpDataService;
import cn.edu.njnu.geoproblemsolving.comparison.service.CmpModelService;
import cn.edu.njnu.geoproblemsolving.comparison.utils.ResultUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 21:51 2019/8/8
 * @Modified By:
 **/
@CrossOrigin
@RestController
@RequestMapping("/cmp_model")
public class CmpModelController {

    @Resource
    private MongoTemplate mongoTemplate;

    @Autowired
    ModelResourceDaoImpl modelResourceDao;

    @Autowired
    ComputableModelImpl computableModel;

    @RequestMapping(value = "/getModelInfo", method = RequestMethod.GET)
    public JsonResult getModelInfo(@RequestParam("modelId") String modelId) {
        ModelResourceDaoImpl modelResourceDao = new ModelResourceDaoImpl(mongoTemplate);
        try {
            ModelResource model = modelResourceDao.findModelByOid(modelId);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("model", model);
            //MDL信息
            return ResultUtils.success(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error(ResultEnum.FAILED);
        }
    }

    @RequestMapping(value = "/getRecordInfo",method = RequestMethod.GET)
    public JsonResult getRecordInfo(@RequestParam("recordId") String recordId){
        ModelRecordDaoImpl modelRecordDao = new ModelRecordDaoImpl(mongoTemplate);
        ModelRecord modelRecord = modelRecordDao.findModelByMsrId(recordId);
        // todo 如果 status 为 0 更新记录信息
        if(modelRecord.getStatus()==0){
            try {
                modelRecord = CmpModelService.getModelInstanceRecord(modelRecord.getIp(), modelRecord.getPort(), modelRecord.getMsrId(), null, modelRecord.getUserId(), modelRecord.getUserName(), modelRecord.getModelId(), modelRecord.getModelName(), modelRecord.getRecordName(),modelRecord.getStates(),modelRecord.getInstanceId());
                modelRecordDao.updateRecord(modelRecord);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ResultUtils.success(modelRecord);
    }


    @RequestMapping(value = "/createModel", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.POST)
    public JsonResult createModel(@RequestBody ModelResource model) {
        ModelResourceDaoImpl modelResourceDao = new ModelResourceDaoImpl(mongoTemplate);
        try {
            model = modelResourceDao.createModel(model);
            return ResultUtils.success(model);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error(ResultEnum.FAILED);
        }
    }


    @RequestMapping(value = "/deployModel", method = RequestMethod.POST)
    public JsonResult deployModel(HttpServletRequest request) {
        System.out.println(request);
        try {
            // 获取MD5值
            String md5 = request.getParameter("md5");
            String ownerName = request.getParameter("ownerName");
            String ownerId = request.getParameter("ownerId");
            // 获取文件信息
            Part filePart = request.getPart("file");
            String fileFullName = filePart.getSubmittedFileName();
            String fileName = fileFullName.substring(0, fileFullName.lastIndexOf('.'));

            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String createTime = dateFormat.format(date);

            String oid = UUID.randomUUID().toString();
            //发送部署请求
            Map<String, Object> deployInfo = CmpModelService.deployModel(filePart);

            //信息存库
            ComputableModel computableModel = new ComputableModel(oid, fileName, ownerName, ownerId, md5, createTime);
            ComputableModelImpl computableModelImpl = new ComputableModelImpl(mongoTemplate);
            if (null != deployInfo) {
                ModelServiceNode serviceNode = (ModelServiceNode) deployInfo.get("modelservice");
                computableModel.setServiceNode(serviceNode);
                if (null != deployInfo.get("modelStates")) {
                    List<ModelState> modelStates = (List<ModelState>) deployInfo.get("modelStates");
                    if (modelStates.size() > 0) {
                        computableModel.setStates(modelStates);
                    }
                }
                computableModelImpl.addComputableModel(computableModel);
                return ResultUtils.success(computableModel);
            } else {
                return ResultUtils.error(ResultEnum.DEPLOY_MODEL_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error(ResultEnum.FAILED);
        }
    }


    @RequestMapping(value = "/matchMd5", method = RequestMethod.GET)
    public JsonResult matchMd5(@RequestParam("md5") String md5) {
        try {
            ComputableModelImpl computableModelDao = new ComputableModelImpl(mongoTemplate);
            ComputableModel cm = computableModelDao.findItemByMd5(md5);
            if (cm == null) {
                return ResultUtils.success();
            } else {
                return ResultUtils.success(cm.getOid());
            }
        } catch (Exception e) {
            return ResultUtils.error(ResultEnum.FAILED);
        }
    }

    @RequestMapping(value = "/getComputableModelInfo", method = RequestMethod.GET)
    public JsonResult getComputableModelInfo(@RequestParam("modelId") String modelId) {
        ModelResourceDaoImpl modelResourceDao = new ModelResourceDaoImpl(mongoTemplate);
        ModelResource model = modelResourceDao.findModelByOid(modelId);
        List<String> computableModels = model.getComputableModels();
        if (computableModels == null || computableModels.size() == 0) {
            return ResultUtils.error(ResultEnum.NO_COMPUTABLE_MODEL);
        }
        String cmId = computableModels.get(0);
        ComputableModelImpl computableModelDao = new ComputableModelImpl(mongoTemplate);
        ComputableModel cm = computableModelDao.findItemByOid(cmId);
        ModelServiceNode serviceNode = cm.getServiceNode();
        //如果模型服务节点地址为空，表示未部署成功
        if (serviceNode == null) {
            return ResultUtils.error(ResultEnum.NO_COMPUTABLE_MODEL);
        }
        //获取 state 信息
        try {
            List<ModelState> modelState = CmpModelService.getModelState(cm, mongoTemplate);
            cm.setStates(modelState);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error(ResultEnum.FAILED_TO_GET_INFO);
        }
        return ResultUtils.success(cm);
    }

    @RequestMapping(value = "/getComputableModelById", method = RequestMethod.GET)
    public JsonResult getComputableModelById(@RequestParam("oid")String oid){
        ComputableModel cm = computableModel.findItemByOid(oid);
        ModelServiceNode serviceNode = cm.getServiceNode();
        //如果模型服务节点地址为空，表示未部署成功
        if (serviceNode == null) {
            return ResultUtils.error(ResultEnum.NO_COMPUTABLE_MODEL);
        }
        //获取 state 信息
        try {
            List<ModelState> modelState = CmpModelService.getModelState(cm, mongoTemplate);
            cm.setStates(modelState);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error(ResultEnum.FAILED_TO_GET_INFO);
        }
        return ResultUtils.success(cm);
    }

    @RequestMapping(value = "getRecordList",method = RequestMethod.GET)
    public JsonResult getRecordList(@RequestParam("recordList") List<String> recordList){
        ModelRecordDaoImpl modelRecordDao = new ModelRecordDaoImpl(mongoTemplate);
        List<ModelRecord> modelRecordByIdList = modelRecordDao.findModelRecordByIdList(recordList);
        return ResultUtils.success(modelRecordByIdList);
    }


    @RequestMapping(value = "/getTaskServer", method = RequestMethod.GET)
    public JsonResult getTaskServer(@RequestParam("pid") String pid) {
        try {
            Map<String, String> taskNode = CmpModelService.getTaskNode(pid);
            if (taskNode == null) {
                return ResultUtils.error(ResultEnum.FAILED_TO_GET_INFO);
            }
            return ResultUtils.success(taskNode);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error(ResultEnum.FAILED_TO_GET_INFO);
        }
    }

    @RequestMapping(value = "/getDataServerNode", method = RequestMethod.POST)
    public JsonResult getDataServerNode(HttpServletRequest request) {
        try {
            String host = request.getParameter("host");
            String port = request.getParameter("port");
            String pid = request.getParameter("pid");
            String username = request.getParameter("username");
            String dataNode = CmpModelService.getDataServer(host, port, pid, username);
            if (dataNode == null) {
                return ResultUtils.error(ResultEnum.FAILED_TO_GET_INFO);
            }
            JSONObject dataNodeObj = JSONObject.parseObject(dataNode);
            dataNodeObj.put("host", dataNodeObj.getString("ip"));
            dataNodeObj.remove("ip");
            return ResultUtils.success(dataNodeObj);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error(ResultEnum.FAILED_TO_GET_INFO);
        }
    }


    @RequestMapping(value = "/uploadData", method = RequestMethod.POST)
    public JsonResult uploadData(HttpServletRequest request) {
        try {
            String host = request.getParameter("host");
            String port = request.getParameter("port");
            String type = request.getParameter("type");
            String userName = request.getParameter("userName");
            String stateName = request.getParameter("stateName");
            String eventName = request.getParameter("eventName");
            String fileName = request.getParameter("fileName");
            Part file = request.getPart("file");
            JSONObject data = CmpModelService.uploadData(host, port, type, userName, file);
            if (data == null) {
                return ResultUtils.error(ResultEnum.FAILED_TO_UPLOAD_DATA);
            }
            data.put("statename", stateName);
            data.put("event", eventName);
            data.put("fileName", fileName);
            return ResultUtils.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error(ResultEnum.FAILED_TO_UPLOAD_DATA);
        }
    }


    @RequestMapping(value = "/invokeModel", method = RequestMethod.POST)
    public JsonResult invokeModel(HttpServletRequest request) {
        try {
            String ip = request.getParameter("ip");
            String port = request.getParameter("port");
            String pid = request.getParameter("pid");
            String username = request.getParameter("username");
            String inputs = request.getParameter("inputs");

            JSONObject recordJson = CmpModelService.invokeModel(ip, port, pid, username, inputs);
            if (recordJson == null) {
                return ResultUtils.error(ResultEnum.FAILED_TO_INVOKE_MODEL);
            }
            // todo 记录信息存库
            String tid = recordJson.getString("tid");
            return ResultUtils.success(recordJson);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error(ResultEnum.FAILED_TO_INVOKE_MODEL);
        }
    }


    // 调用模型容器的接口 运行模型
    // 输入参数为 模型容器 ip 和 端口
    @RequestMapping(value = "/invokeModel_MC", method = RequestMethod.POST)
    public JsonResult invokeModel_MC(HttpServletRequest request){
        try {
            ServletInputStream inputStream = request.getInputStream();
            String jsonData = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer buffer = new StringBuffer();
            String str = "";
            while(!StringUtils.isEmpty(str = reader.readLine())) {
                buffer.append(str);
            }
            jsonData = buffer.toString();
            JSONObject requstJSON = JSONObject.parseObject(jsonData);

            String ip = requstJSON.getString("ip");
            String port = requstJSON.getString("port");
            String msid = requstJSON.getString("msid");
            String inputs = requstJSON.getString("inputs");
            String userId = requstJSON.getString("userId");
            String username = requstJSON.getString("username");
            String modelId = requstJSON.getString("modelId");
            String modelName = requstJSON.getString("modelName");
            String computableModelId = requstJSON.getString("computableModelId");
            String instanceId = requstJSON.getString("instanceId");


            //调用模型
//            String recordId = CmpModelService.invokeModel_MC(ip, port, msid, inputs);
            String recordId = CmpModelService.invokeModel_MC(ip, port, msid, inputs);
            if (recordId == null) {
                return ResultUtils.error(ResultEnum.FAILED_TO_INVOKE_MODEL);
            }
            // 模型运行成功，获取模型记录
            ModelRecord record = CmpModelService.getModelInstanceRecord(ip, port, recordId, inputs, userId, username, modelId, modelName,null,null,instanceId);
            if(record==null){
                return ResultUtils.error(ResultEnum.FAILED_TO_GET_RECORD);
            }
            //模型记录入库
            ModelRecordDaoImpl modelRecordDao = new ModelRecordDaoImpl(mongoTemplate);
            modelRecordDao.createModelRecord(record);

            //更新计算模型信息
            ComputableModelImpl computableModelDao = new ComputableModelImpl(mongoTemplate);
            computableModelDao.updateRecordList(computableModelId, record.getMsrId(), true);
            return ResultUtils.success(record);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error(ResultEnum.FAILED_TO_INVOKE_MODEL);
        }
    }


    @RequestMapping(value = "matchDataMd5", method = RequestMethod.GET)
    public JsonResult matchDataMd5(@RequestParam(value = "md5") String md5) {
        try {
            String jsonStr = CmpDataService.matchDataMd5(md5);
            if (jsonStr == null) {
                return ResultUtils.error(ResultEnum.FAILED_TO_GET_INFO);
            }
            return ResultUtils.success(JSONObject.parseObject(jsonStr));
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtils.error(ResultEnum.FAILED_TO_GET_INFO);
        }
    }


    @RequestMapping(value = "uploadData_DC", method = RequestMethod.POST)
    public JsonResult uploadData_DC(HttpServletRequest request) {
        try {
            String userName = request.getParameter("userName");
            String stateName = request.getParameter("stateName");
            String eventName = request.getParameter("eventName");
            String fileName = request.getParameter("fileName");
            String md5 = request.getParameter("md5");
            String sourceStoreId = request.getParameter("sourceStoreId");
            Part file = request.getPart("file");
            String type = "OTHER";
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            String fileNameNoSuffix = fileName.substring(0,fileName.lastIndexOf("."));
            String res = "";
            if(sourceStoreId.isEmpty()){
                res = CmpDataService.uploadDataToDC(md5, file, userName, type);
            }else{
                res = CmpDataService.uploadDataToDCFast(md5, fileNameNoSuffix,userName,sourceStoreId,suffix, type);
            }
            if(res==null){
                return ResultUtils.error(ResultEnum.FAILED_TO_UPLOAD_DATA);
            }
            JSONObject dcDataRes = JSONObject.parseObject(res);
            JSONObject dcDataInfo = dcDataRes.getJSONObject("data");
            String dataUrl = "http://" + HttpContant.DATA_CONTAINER_IP+":"+ HttpContant.DATA_CONTAINER_PORT+HttpContant.DOWNLOAD_ONE_DATA_ROUTER+"?fileName="+dcDataInfo.getString("fileName")+"&sourceStoreId="+dcDataInfo.getString("sourceStoreId")+"&suffix="+dcDataInfo.getString("suffix");
            JSONObject resObj = new JSONObject();
            resObj.put("dataUrl",dataUrl);
            resObj.put("stateName",stateName);
            resObj.put("eventName",eventName);
            resObj.put("fileName",fileName);
            resObj.put("md5", md5);
            resObj.put("sourceStoreId",dcDataInfo.getString("sourceStoreId"));
            return ResultUtils.success(resObj);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error(ResultEnum.FAILED_TO_UPLOAD_DATA);
        }
    }

    @RequestMapping(value = "uploadData_DC_Fast", method = RequestMethod.POST)
    public JsonResult uploadData_DC_Fast(HttpServletRequest request) {
        try {
            String userName = request.getParameter("userName");
            String stateName = request.getParameter("stateName");
            String eventName = request.getParameter("eventName");
            String fileName = request.getParameter("fileName");
            String sourceStoreId = request.getParameter("sourceStoreId");
            String md5 = request.getParameter("md5");
            String type = "OTHER";
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            String res = CmpDataService.uploadDataToDCFast(md5, fileName,userName,sourceStoreId,suffix, type);
            if(res==null){
                return ResultUtils.error(ResultEnum.FAILED_TO_UPLOAD_DATA);
            }
            return ResultUtils.success(res);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error(ResultEnum.FAILED_TO_UPLOAD_DATA);
        }
    }


    @RequestMapping(value="/updateModelResource",method = RequestMethod.POST)
    public JsonResult updateModelResource(@RequestBody ModelResource mr){
        ModelResource modelResource = modelResourceDao.updateModel(mr);
        return ResultUtils.success(modelResource);
    }


}
