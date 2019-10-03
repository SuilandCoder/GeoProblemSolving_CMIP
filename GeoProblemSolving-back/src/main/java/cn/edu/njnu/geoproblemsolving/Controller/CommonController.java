package cn.edu.njnu.geoproblemsolving.Controller;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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


}
