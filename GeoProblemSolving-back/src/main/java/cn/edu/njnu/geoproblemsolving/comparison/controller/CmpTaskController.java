package cn.edu.njnu.geoproblemsolving.comparison.controller;

import cn.edu.njnu.geoproblemsolving.Dao.metrics.MetricsDaoImpl;
import cn.edu.njnu.geoproblemsolving.Entity.Metrics;
import cn.edu.njnu.geoproblemsolving.comparison.bean.JsonResult;
import cn.edu.njnu.geoproblemsolving.comparison.dao.dataprocess_method.DataProcessMethodDaoImpl;
import cn.edu.njnu.geoproblemsolving.comparison.entity.DataProcessMethod;
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
import java.util.ArrayList;
import java.util.List;
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

    // 创建比较任务
    @RequestMapping(value = "/createTask", method = RequestMethod.POST)
    public JsonResult createTask(@RequestBody JSONObject tasksInfo) {
        System.out.println(tasksInfo);
        try {
            JSONArray computableModels = tasksInfo.getJSONArray("computableModels");
            for (int i = 0; i < computableModels.size(); i++) {
                JSONObject computableModel = computableModels.getJSONObject(i);

                String metricId = computableModel.getString("metricId");
                String metricName = computableModel.getString("metricName");
                JSONArray cmpMethodList = computableModel.getJSONArray("cmpMethodList");
                JSONArray dataProcessMethodList = computableModel.getJSONArray("dataProcessMethodList");
                for (int j = 0; j < cmpMethodList.size(); j++) {
                    JSONObject cmpMethodItem = cmpMethodList.getJSONObject(j);
//                    cmpMethodItem = cmpTaskService.runTask(cmpMethodItem, metricId, metricName, dataProcessMethodList);
                    JSONObject res = cmpTaskService.runTaskTest(cmpMethodItem, metricId, metricName, dataProcessMethodList);

                    computableModel.getJSONArray("cmpMethodList").set(j, res.getJSONObject("cmpMethodInfo"));
                    //todo 如果改成异步的，这里不能直接赋值。
                    dataProcessMethodList = res.getJSONArray("dataProcessMethodList");
                }
                computableModel.put("dataProcessMethodList", dataProcessMethodList);
                tasksInfo.getJSONArray("computableModels").set(i,computableModel);
            }
            return ResultUtils.success(tasksInfo);
        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResultUtils.error(ResultEnum.FAILED_TO_COMPARING);
        }
    }

    // 创建比较任务
    @RequestMapping(value = "/testAsync", method = RequestMethod.GET)
    public JsonResult testAsync() {
        long currentTimeMillis = System.currentTimeMillis();
        try {
            Future<String> task1 = cmpTaskService.task1();
            Future<String> task2 = cmpTaskService.task2();
            Future<String> task3 = cmpTaskService.task3();

            String result = null;
            for (; ; ) {
                if (task1.isDone() && task2.isDone() && task3.isDone()) {
                    break;
                }
//                if(task1.isDone()){
//                    break;
//                }
                Thread.sleep(1000);
            }
            long currentTimeMillis1 = System.currentTimeMillis();
            result = "task总耗时：" + (currentTimeMillis1 - currentTimeMillis) + "ms";
            return ResultUtils.success(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return ResultUtils.error(ResultEnum.FAILED);
        }
    }

}
