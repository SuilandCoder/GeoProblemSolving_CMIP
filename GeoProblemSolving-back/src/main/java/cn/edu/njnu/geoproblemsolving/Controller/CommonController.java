package cn.edu.njnu.geoproblemsolving.Controller;

import cn.edu.njnu.geoproblemsolving.Dao.concept.ConceptImpl;
import cn.edu.njnu.geoproblemsolving.Dao.metrics.MetricsDaoImpl;
import cn.edu.njnu.geoproblemsolving.Dao.spatialref.SpatialRefImpl;
import cn.edu.njnu.geoproblemsolving.Dao.template.TemplateImpl;
import cn.edu.njnu.geoproblemsolving.Dao.unit.UnitDaoImpl;
import cn.edu.njnu.geoproblemsolving.Entity.*;
import cn.edu.njnu.geoproblemsolving.comparison.bean.JsonResult;
import cn.edu.njnu.geoproblemsolving.comparison.enums.ResultEnum;
import cn.edu.njnu.geoproblemsolving.comparison.utils.ResultUtils;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 20:21 2019/10/2
 * @Modified By:
 **/
@CrossOrigin(origins = "*",allowCredentials = "true")
@RestController
@RequestMapping("/common")
public class CommonController {
    @Resource
    private MongoTemplate mongoTemplate;

    @Autowired
    TemplateImpl templateDao;

    @Autowired
    SpatialRefImpl spatialRefDao;

    @Autowired
    ConceptImpl conceptDao;

    @Autowired
    UnitDaoImpl unitDao;

    @Autowired
    MetricsDaoImpl metricsDao;

    @RequestMapping(value = "/createItem",method = RequestMethod.POST)
    public JsonResult createItem(@RequestBody JSONObject itemJson) {
        //json 数据格式为 {type:"",data:JSONObject};
        String type = itemJson.getString("type");
        JSONObject data = itemJson.getJSONObject("data");
        if ("unit".equals(type)) {
            Unit unit = data.toJavaObject(Unit.class);
            unitDao.createUnit(unit);
            return ResultUtils.success(unit);
        } else if ("template".equals(type)) {
            Template template = data.toJavaObject(Template.class);
            templateDao.createTemplate(template);
            return ResultUtils.success(template);
        } else if ("concept".equals(type)) {
            Concept concept = data.toJavaObject(Concept.class);
            conceptDao.createConcept(concept);
            return ResultUtils.success(concept);
        }else if("spatialref".equals(type)){
            SpatialReference spatialReference = data.toJavaObject(SpatialReference.class);
            spatialRefDao.createSpatialReference(spatialReference);
            return ResultUtils.success(spatialReference);
        } else if ("metrics".equals(type)) {
            Metrics metrics = data.toJavaObject(Metrics.class);
            return ResultUtils.success(metrics);
        }
        return ResultUtils.error(ResultEnum.NO_OBJECT);
    }


    @RequestMapping(value = "/findAllItem",method = RequestMethod.GET)
    public JsonResult findAllItem(@RequestParam(value = "type") String type) {
        if ("unit".equals(type)) {
            List<Unit> units = unitDao.findAll();
            return ResultUtils.success(units);
        } else if ("template".equals(type)) {
            List<Template> all = templateDao.findAll();
            return ResultUtils.success(all);
        } else if ("concept".equals(type)) {
            List<Concept> all = conceptDao.findAll();
            return ResultUtils.success(all);
        }else if("spatialref".equals(type)){
            List<SpatialReference> all = spatialRefDao.findAll();
            return ResultUtils.success(all);
        }else if ("metrics".equals(type)) {
            List<Metrics> metrics = metricsDao.findAll();
            return ResultUtils.success(metrics);
        }
        return ResultUtils.error(ResultEnum.NO_OBJECT);
    }


    @RequestMapping(value = "/findByX",method = RequestMethod.GET)
    public JsonResult findByX(@RequestParam(value = "type") String type,@RequestParam(value = "key") String key,@RequestParam(value = "value") String value){
        if ("unit".equals(type)) {
            List<Unit> units = unitDao.getUnits(key,value);
            return ResultUtils.success(units);
        } else if ("template".equals(type)) {
            List<Template> all = templateDao.getTemplates(key,value);
            return ResultUtils.success(all);
        } else if ("concept".equals(type)) {
            List<Concept> all = conceptDao.getConcepts(key,value);
            return ResultUtils.success(all);
        }else if("spatialref".equals(type)){
            List<SpatialReference> all = spatialRefDao.getSpatialReferences(key,value);
            return ResultUtils.success(all);
        } else if ("metrics".equals(type)) {
            List<Metrics> metrics = metricsDao.getMetrics(key, value);
            return ResultUtils.success(metrics);
        }
        return ResultUtils.error(ResultEnum.NO_OBJECT);
    }

}
