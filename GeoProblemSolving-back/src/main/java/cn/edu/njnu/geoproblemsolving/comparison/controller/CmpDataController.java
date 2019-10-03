package cn.edu.njnu.geoproblemsolving.comparison.controller;

import cn.edu.njnu.geoproblemsolving.comparison.bean.JsonResult;
import cn.edu.njnu.geoproblemsolving.comparison.dao.dataresource.DataResourceDaoImpl;
import cn.edu.njnu.geoproblemsolving.comparison.entity.CmpItem;
import cn.edu.njnu.geoproblemsolving.comparison.entity.DataResource;
import cn.edu.njnu.geoproblemsolving.comparison.enums.ResultEnum;
import cn.edu.njnu.geoproblemsolving.comparison.utils.ResultUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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

    @RequestMapping(value = "/getDataResourceByIdList", method = RequestMethod.POST)
    public JsonResult getDataResourceByIdList(@RequestBody List<String> idList){
        DataResourceDaoImpl dataResourceDao = new DataResourceDaoImpl(mongoTemplate);
        try{
            List<DataResource> dataResources = dataResourceDao.findDataResourceByIdList(idList);
            return ResultUtils.success(dataResources);
        }catch (Exception e){
            return ResultUtils.error(ResultEnum.FAILED);
        }
    }
}
