package cn.edu.njnu.geoproblemsolving.comparison.controller;

import cn.edu.njnu.geoproblemsolving.Dao.metrics.MetricsDaoImpl;
import cn.edu.njnu.geoproblemsolving.Entity.Metrics;
import cn.edu.njnu.geoproblemsolving.comparison.bean.CmpMetirc;
import cn.edu.njnu.geoproblemsolving.comparison.bean.JsonResult;
import cn.edu.njnu.geoproblemsolving.comparison.dao.cmpinstance.CmpInstanceDaoImpl;
import cn.edu.njnu.geoproblemsolving.comparison.dao.dataprocess_method.DataProcessMethodDaoImpl;
import cn.edu.njnu.geoproblemsolving.comparison.dao.project.CmpProjectDaoImpl;
import cn.edu.njnu.geoproblemsolving.comparison.dao.taskrecord.CmpMethodRecordImpl;
import cn.edu.njnu.geoproblemsolving.comparison.dao.taskrecord.CmpTaskRecordImpl;
import cn.edu.njnu.geoproblemsolving.comparison.entity.*;
import cn.edu.njnu.geoproblemsolving.comparison.enums.ResultEnum;
import cn.edu.njnu.geoproblemsolving.comparison.service.CmpTaskService;
import cn.edu.njnu.geoproblemsolving.comparison.utils.ResultUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 21:55 2019/10/1
 * @Modified By:
 **/
@CrossOrigin
@RestController
@RequestMapping("/cmp_task")
public class CmpTaskController {
    @Resource
    private MongoTemplate mongoTemplate;

    @Autowired
    CmpTaskService cmpTaskService;

    @Autowired
    CmpMethodRecordImpl cmpMethodRecordDao;

    @Autowired
    CmpTaskRecordImpl cmpTaskRecordDao;

    @Autowired
    CmpProjectDaoImpl cmpProjectDao;

    @Autowired
    DataProcessMethodDaoImpl dataProcessMethodDao;


    @RequestMapping(value = "/uploadDataProcessMethod", method = RequestMethod.POST)
    public JsonResult uploadDataProcessMethod(@RequestBody DataProcessMethod dpm) {
        if (dpm.getType().isEmpty()) {
            return ResultUtils.error(ResultEnum.UNKNOWN_METHOD_TYPE_ERROR);
        }
        DataProcessMethodDaoImpl dataProcessMethodDao = new DataProcessMethodDaoImpl(mongoTemplate);
        DataProcessMethod dataProcessMethod = dataProcessMethodDao.createDataProcessMethod(dpm);
        return ResultUtils.success(dataProcessMethod);
    }

    //todo 做分页
    @RequestMapping(value = "/getDataProcessMethod", method = RequestMethod.GET)
    public JsonResult getDataProcessMethod(@RequestParam(value = "type", required = false) String type) {
        DataProcessMethodDaoImpl dataProcessMethodDao = new DataProcessMethodDaoImpl(mongoTemplate);
        List<DataProcessMethod> dataProcessMethods;
        if (type.isEmpty()) {
            dataProcessMethods = dataProcessMethodDao.getAllDataProcessMethod();
        } else {
            dataProcessMethods = dataProcessMethodDao.findDataProcessMethod("type", type);
        }
        if (dataProcessMethods == null) {
            dataProcessMethods = new ArrayList<>();
        }
        return ResultUtils.success(dataProcessMethods);
    }

    @RequestMapping(value = "/getDataProcessMethodById", method = RequestMethod.GET)
    public JsonResult getDataProcessMethodById(@RequestParam("methodId") String methodId){
        DataProcessMethod dpm = dataProcessMethodDao.findDataProcessMethodByOid(methodId);
        return ResultUtils.success(dpm);
    }



    // 创建对比任务
    @RequestMapping(value = "/createTask", method = RequestMethod.POST)
    public JsonResult createTask(@RequestBody JSONObject tasksInfo) {
        System.out.println(tasksInfo);
        try {
            String projectId = tasksInfo.getString("projectId");
            JSONArray computableModels = tasksInfo.getJSONArray("computableModels");
            JSONArray targetInstanceList = tasksInfo.getJSONArray("targetInstanceList");
            JSONArray checkedMetrics = tasksInfo.getJSONArray("checkedMetrics");

            List<CmpMetirc> cmpMetircs = checkedMetrics.toJavaList(CmpMetirc.class);
            List<CmpInstance> cmpInstanceList = targetInstanceList.toJavaList(CmpInstance.class);
            //创建比较任务记录
            CmpTaskRecord cmpTaskRecord = new CmpTaskRecord(UUID.randomUUID().toString(),projectId, tasksInfo.getString("userId"), tasksInfo.getString("userName"), tasksInfo.getString("name"), tasksInfo.getString("description"), cmpInstanceList, cmpMetircs);
            List<CmpTaskModel> cmpTaskModelList = new ArrayList<>();
            for (int i = 0; i < computableModels.size(); i++) {
                JSONObject computableModel = computableModels.getJSONObject(i);

                String metricId = computableModel.getString("metricId");
                String metricName = computableModel.getString("metricName");
                JSONArray cmpMethodList = computableModel.getJSONArray("cmpMethodList");
                JSONArray dataProcessMethodList = computableModel.getJSONArray("dataProcessMethodList");

                List<String> cmpMethodIdList = new ArrayList<>();
                List<String> dataProcessMethodIdList = new ArrayList<>();
                for (int k = 0; k < dataProcessMethodList.size(); k++) {
                    JSONObject dpm = dataProcessMethodList.getJSONObject(k);
                    String recordId = UUID.randomUUID().toString();
                    dpm.put("recordId", recordId);
                    dataProcessMethodIdList.add(recordId);

                    //生成记录并入库；
                    CmpMethodRecord cmr = dpm.toJavaObject(CmpMethodRecord.class);
                    cmr.setStatus("0");
                    cmpMethodRecordDao.createCmpMethodRecord(cmr);
                }
                for (int j = 0; j < cmpMethodList.size(); j++) {
                    JSONObject cmpMethodItem = cmpMethodList.getJSONObject(j);
                    String recordId = UUID.randomUUID().toString();
                    cmpMethodItem.put("recordId", recordId);
                    cmpMethodIdList.add(recordId);
                    //生成记录并入库；
                    CmpMethodRecord cmr = cmpMethodItem.toJavaObject(CmpMethodRecord.class);
                    cmr.setStatus("0");
                    cmpMethodRecordDao.createCmpMethodRecord(cmr);
                    cmpTaskService.runTask(cmpMethodItem, metricId, metricName, dataProcessMethodList);
                }
                CmpTaskModel cmpTaskModel = new CmpTaskModel(metricId, metricName, computableModel.getString("graphXML"), cmpMethodIdList, dataProcessMethodIdList);
                cmpTaskModelList.add(cmpTaskModel);
            }
            cmpTaskRecord.setCmpTaskModelList(cmpTaskModelList);
            //记录进数据库
//            CmpTaskRecordImpl cmpTaskRecordDao = new CmpTaskRecordImpl(mongoTemplate);
            cmpTaskRecordDao.createCmpTaskRecord(projectId,cmpTaskRecord);
            return ResultUtils.success(cmpTaskRecord);
        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResultUtils.error(ResultEnum.FAILED_TO_COMPARING);
        }
    }


    //查询所有对比任务
    @RequestMapping(value = "/getCmpTasks", method = RequestMethod.GET)
    public JsonResult getCmpTasks(@RequestParam("projectId") String projectId) {
        CmpProject project = cmpProjectDao.findByProjectId(projectId);
        List<CmpTaskRecord> taskRecordList = cmpTaskRecordDao.findByRecordIdList(project.getTaskList());
        return ResultUtils.success(taskRecordList);
    }

    //查询对比任务记录
    @RequestMapping(value = "/getCmpTaskRecord", method = RequestMethod.GET)
    public JsonResult getCmpTaskRecord(@RequestParam("recordId") String recordId) {
        CmpTaskRecord record = cmpTaskRecordDao.findByRecordId(recordId);
        return ResultUtils.success(record);
    }

    //查询对比任务中方法执行记录
    @RequestMapping(value = "/getCmpMethodRecord", method = RequestMethod.GET)
    public JsonResult getCmpMethodRecord(@RequestParam("recordId") String recordId){
        CmpMethodRecord record = cmpMethodRecordDao.findByRecordId(recordId);
        return ResultUtils.success(record);
    }

    @RequestMapping(value = "/getCmpMethodRecordList", method = RequestMethod.POST)
    public JsonResult getCmpMethodRecordList(@RequestBody List<String> recordList){
        List<CmpMethodRecord> cmpMethodRecordList = cmpMethodRecordDao.findByRecordIdList(recordList);
        return ResultUtils.success(cmpMethodRecordList);
    }


    @RequestMapping(value="/updateCmpMethodRecord",method = RequestMethod.POST)
    public JsonResult updateCmpMethodRecord(@RequestBody JSONArray recordsJSONArr){
        List<CmpMethodRecord> cmrList = recordsJSONArr.toJavaList(CmpMethodRecord.class);
        for(CmpMethodRecord cmr:cmrList){
            cmpMethodRecordDao.updateRecord(cmr);
        }
//        System.out.println(recordsJSONArr);
        return ResultUtils.success();
    }
}
