package cn.edu.njnu.geoproblemsolving.comparison.controller;

import cn.edu.njnu.geoproblemsolving.comparison.bean.JsonResult;
import cn.edu.njnu.geoproblemsolving.comparison.dao.cmpinstance.CmpInstanceDaoImpl;
import cn.edu.njnu.geoproblemsolving.comparison.dao.dataresource.DataResourceDaoImpl;
import cn.edu.njnu.geoproblemsolving.comparison.dao.modelrecord.ModelRecordDaoImpl;
import cn.edu.njnu.geoproblemsolving.comparison.dao.project.CmpProjectDaoImpl;
import cn.edu.njnu.geoproblemsolving.comparison.entity.CmpInstance;
import cn.edu.njnu.geoproblemsolving.comparison.entity.CmpProject;
import cn.edu.njnu.geoproblemsolving.comparison.entity.DataResource;
import cn.edu.njnu.geoproblemsolving.comparison.entity.ModelRecord;
import cn.edu.njnu.geoproblemsolving.comparison.utils.ResultUtils;
import com.alibaba.fastjson.JSONObject;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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

    @RequestMapping(value = "/createInstance", method = RequestMethod.POST)
    public JsonResult createInstance(@RequestBody String jsonData){
        JSONObject requstJSON = JSONObject.parseObject(jsonData);
        String projectId = requstJSON.getString("projectId");
        CmpInstance ci = requstJSON.toJavaObject(CmpInstance.class);
        CmpInstanceDaoImpl dataResourceDao = new CmpInstanceDaoImpl(mongoTemplate);
        CmpInstance instance = dataResourceDao.createInstance(ci);

        //更新 project 信息
        CmpProjectDaoImpl cmpProjectDao = new CmpProjectDaoImpl(mongoTemplate);
        cmpProjectDao.updateInstanceList(projectId, instance.getInstanceId(),true);
        return ResultUtils.success(instance);
    }

    @RequestMapping(value = "getInstanceList",method = RequestMethod.GET)
    public JsonResult getInstanceList(@RequestParam("projectId") String projectId){
        CmpProjectDaoImpl cmpProjectDao = new CmpProjectDaoImpl(mongoTemplate);
        CmpProject project = cmpProjectDao.findByProjectId(projectId);
        CmpInstanceDaoImpl cmpInstanceDao = new CmpInstanceDaoImpl(mongoTemplate);
        List<CmpInstance> instanceByIdList = cmpInstanceDao.findInstanceByIdList(project.getInstanceList());
        return ResultUtils.success(instanceByIdList);
    }
}
