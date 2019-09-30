package cn.edu.njnu.geoproblemsolving.comparison.controller;

import cn.edu.njnu.geoproblemsolving.comparison.bean.JsonResult;
import cn.edu.njnu.geoproblemsolving.comparison.dao.dataresource.DataResourceDaoImpl;
import cn.edu.njnu.geoproblemsolving.comparison.entity.DataResource;
import cn.edu.njnu.geoproblemsolving.comparison.utils.ResultUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 11:05 2019/9/24
 * @Modified By:
 **/
@CrossOrigin
@RestController
@RequestMapping("/cmp_data")
public class CmpDataController {
    @Resource
    private MongoTemplate mongoTemplate;

    @RequestMapping(value = "/createDataResource", method = RequestMethod.POST)
    public JsonResult createDataResource(@RequestBody DataResource dr){
        DataResourceDaoImpl dataResourceDao = new DataResourceDaoImpl(mongoTemplate);
        DataResource dataResource = dataResourceDao.createDataResource(dr);
        return ResultUtils.success(dataResource);
    }
}
