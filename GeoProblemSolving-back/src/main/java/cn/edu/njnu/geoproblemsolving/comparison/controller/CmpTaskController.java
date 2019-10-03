package cn.edu.njnu.geoproblemsolving.comparison.controller;

import cn.edu.njnu.geoproblemsolving.comparison.bean.JsonResult;
import cn.edu.njnu.geoproblemsolving.comparison.dao.dataprocess_method.DataProcessMethodDaoImpl;
import cn.edu.njnu.geoproblemsolving.comparison.entity.DataProcessMethod;
import cn.edu.njnu.geoproblemsolving.comparison.enums.ResultEnum;
import cn.edu.njnu.geoproblemsolving.comparison.utils.ResultUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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


    @RequestMapping(value = "/uploadDataProcessMethod", method = RequestMethod.POST)
    public JsonResult uploadDataProcessMethod(@RequestBody DataProcessMethod dpm){
        if(dpm.getType().isEmpty()){
            return ResultUtils.error(ResultEnum.UNKNOWN_METHOD_TYPE_ERROR);
        }
        DataProcessMethodDaoImpl dataProcessMethodDao = new DataProcessMethodDaoImpl(mongoTemplate);
        DataProcessMethod dataProcessMethod = dataProcessMethodDao.createDataProcessMethod(dpm);
        return ResultUtils.success(dataProcessMethod);
    }

    //todo 做分页
    @RequestMapping(value = "/getDataProcessMethod", method = RequestMethod.GET)
    public JsonResult getDataProcessMethod(@RequestParam(value = "type", required = false) String type){
        DataProcessMethodDaoImpl dataProcessMethodDao = new DataProcessMethodDaoImpl(mongoTemplate);
        List<DataProcessMethod> dataProcessMethods;
        if(type.isEmpty()){
            dataProcessMethods = dataProcessMethodDao.getAllDataProcessMethod();
        }else{
            dataProcessMethods = dataProcessMethodDao.findDataProcessMethod("type", type);
        }
        if(dataProcessMethods==null){
            dataProcessMethods = new ArrayList<>();
        }
        return ResultUtils.success(dataProcessMethods);
    }
}
