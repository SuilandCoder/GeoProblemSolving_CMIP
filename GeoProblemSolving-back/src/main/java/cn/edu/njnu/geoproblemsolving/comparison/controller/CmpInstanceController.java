package cn.edu.njnu.geoproblemsolving.comparison.controller;

import cn.edu.njnu.geoproblemsolving.Entity.Template;
import cn.edu.njnu.geoproblemsolving.comparison.bean.JsonResult;
import cn.edu.njnu.geoproblemsolving.comparison.dao.cmpinstance.CmpInstanceDaoImpl;
import cn.edu.njnu.geoproblemsolving.comparison.dao.dataresource.DataResourceDaoImpl;
import cn.edu.njnu.geoproblemsolving.comparison.dao.modelrecord.ModelRecordDaoImpl;
import cn.edu.njnu.geoproblemsolving.comparison.dao.project.CmpProjectDaoImpl;
import cn.edu.njnu.geoproblemsolving.comparison.entity.*;
import cn.edu.njnu.geoproblemsolving.comparison.enums.ResultEnum;
import cn.edu.njnu.geoproblemsolving.comparison.utils.ResultUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 19:37 2019/9/24
 * @Modified By:
 **/
@CrossOrigin
@RestController
@RequestMapping("/cmp_instance")
public class CmpInstanceController {
    @Resource
    private MongoTemplate mongoTemplate;

    @Autowired
    CmpInstanceDaoImpl cmpInstanceDao;

    @Autowired
    DataResourceDaoImpl dataResourceDao;

    @RequestMapping(value = "/createInstance", method = RequestMethod.POST)
    public JsonResult createInstance(@RequestBody String jsonData) {
        JSONObject requstJSON = JSONObject.parseObject(jsonData);
        String projectId = requstJSON.getString("projectId");
        CmpInstance ci = requstJSON.toJavaObject(CmpInstance.class);
        CmpInstanceDaoImpl dataResourceDao = new CmpInstanceDaoImpl(mongoTemplate);
        CmpInstance instance = dataResourceDao.createInstance(ci);

        //更新 project 信息
        CmpProjectDaoImpl cmpProjectDao = new CmpProjectDaoImpl(mongoTemplate);
        cmpProjectDao.updateInstanceList(projectId, instance.getInstanceId(), true);
        return ResultUtils.success(instance);
    }

    @RequestMapping(value = "/getInstanceList", method = RequestMethod.GET)
    public JsonResult getInstanceList(@RequestParam("projectId") String projectId) {
        CmpProjectDaoImpl cmpProjectDao = new CmpProjectDaoImpl(mongoTemplate);
        CmpProject project = cmpProjectDao.findByProjectId(projectId);
        CmpInstanceDaoImpl cmpInstanceDao = new CmpInstanceDaoImpl(mongoTemplate);
        List<CmpInstance> instanceByIdList = cmpInstanceDao.findInstanceByIdList(project.getInstanceList());
        return ResultUtils.success(instanceByIdList);
    }


    @RequestMapping(value = "/findInstanceByInstanceId", method = RequestMethod.GET)
    public JsonResult findInstanceByInstanceId(@RequestParam("instanceId") String instanceId) {
        CmpInstanceDaoImpl cmpInstanceDao = new CmpInstanceDaoImpl(mongoTemplate);
        CmpInstance instanceByInstanceId = cmpInstanceDao.findInstanceByInstanceId(instanceId);
        return ResultUtils.success(instanceByInstanceId);
    }

    @RequestMapping(value = "/updateInstance", method = RequestMethod.POST)
    public JsonResult updateInstance(@RequestBody CmpInstance cmpInstance) {
        CmpInstanceDaoImpl cmpInstanceDao = new CmpInstanceDaoImpl(mongoTemplate);
        CmpInstance instance = cmpInstanceDao.updateInstance(cmpInstance);
        return ResultUtils.success(instance);
    }

    @RequestMapping(value = "/updateInstanceCmpData", method = RequestMethod.POST)
    public JsonResult updateInstanceCmpData(@RequestBody JSONObject jsonData) {
        String instanceId = jsonData.getString("instanceId");
        CmpInstance instance = cmpInstanceDao.findInstanceByInstanceId(instanceId);
        String action = jsonData.getString("action");
        String ownerName = jsonData.getString("ownerName");
        String ownerId = jsonData.getString("ownerId");
        if ("update".equals(action)) {
            JSONArray states = jsonData.getJSONArray("states");
            List<ModelState> modelStates = states.toJavaList(ModelState.class);
            List<String> cmdDataId = new ArrayList<>();
            for (ModelState state : modelStates) {
                for (ModelEvent event : state.getEvents()) {
                    if ("noresponse".equals(event.getType())) {
                        if (event.getDcSourceStoreId() != null && !event.getDcSourceStoreId().isEmpty()) {
                            if (event.getMetrics() == null) {
                                return ResultUtils.error(ResultEnum.EVENT_NOT_BIND_METRICS);
                            }
                            // 创建 DataResource
                            DataResource dataResource = new DataResource();
                            dataResource.setName(event.getName());
                            dataResource.setOwnerName(ownerName);
                            dataResource.setOwnerId(ownerId);
                            dataResource.setFileName(event.getFileName());
                            dataResource.setPrivacy("public");
                            dataResource.setMd5(event.getMd5());
                            dataResource.setDcSourceStoreId(event.getDcSourceStoreId());
                            dataResource.setUrl(event.getUrl());
                            dataResource.setMetrics(event.getMetrics());
                            dataResource = dataResourceDao.createDataResource(dataResource);
                            cmdDataId.add(dataResource.getDataId());
                        }
                    }
                }
            }
            instance.setCmpDataList(cmdDataId);
        } else if ("reset".equals(action)) {
            instance.setCmpDataList(new ArrayList<String>());
        }
        cmpInstanceDao.updateInstance(instance);
        return  ResultUtils.success(instance);
    }

}
