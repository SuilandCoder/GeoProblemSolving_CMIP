package cn.edu.njnu.geoproblemsolving.comparison.controller;

import cn.edu.njnu.geoproblemsolving.comparison.bean.JsonResult;
import cn.edu.njnu.geoproblemsolving.comparison.dao.dataresource.DataResourceDaoImpl;
import cn.edu.njnu.geoproblemsolving.comparison.entity.CmpItem;
import cn.edu.njnu.geoproblemsolving.comparison.entity.DataResource;
import cn.edu.njnu.geoproblemsolving.comparison.enums.ResultEnum;
import cn.edu.njnu.geoproblemsolving.comparison.utils.MyHttpUtils;
import cn.edu.njnu.geoproblemsolving.comparison.utils.ResultUtils;
import com.alibaba.fastjson.JSONObject;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
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


    @RequestMapping(value = "/getImgFromDataContainer", method = RequestMethod.GET)
    public JsonResult getImgFromDataContainer(@RequestParam("imgUrl") String imgUrl){
        try {
            String object = MyHttpUtils.myImgBase64(imgUrl);
            if (null != object) {
                return ResultUtils.success(object);
            }
            return ResultUtils.error(ResultEnum.FAILED);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error(ResultEnum.FAILED);
        }
    }


    @RequestMapping(value = "/downloadDataFromDataContainer", method = RequestMethod.POST)
    public ResponseEntity<byte[]> downloadDataFromDataContainer(@RequestBody JSONObject reqJson){
        try {
            String dataUrl = reqJson.getString("dataUrl");
            String fileName = reqJson.getString("fileName");
            byte[] dataBlob = MyHttpUtils.getDataBlob(dataUrl);
            if (null != dataBlob) {
                HttpHeaders headers=new HttpHeaders();
                headers.add("Content-Disposition", "attachment;filename="+fileName);

                return new ResponseEntity(dataBlob, headers, HttpStatus.OK);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
