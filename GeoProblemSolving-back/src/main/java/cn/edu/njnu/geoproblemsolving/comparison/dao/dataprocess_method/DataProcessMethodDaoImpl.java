package cn.edu.njnu.geoproblemsolving.comparison.dao.dataprocess_method;

import cn.edu.njnu.geoproblemsolving.comparison.entity.DataProcessMethod;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 15:05 2019/9/30
 * @Modified By:
 **/
public class DataProcessMethodDaoImpl implements IDataProcessMethodDao {

    private final MongoTemplate mongoTemplate;

    public DataProcessMethodDaoImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public DataProcessMethod findDataProcessMethodById(String id) {
        return null;
    }

    @Override
    public DataProcessMethod createDataProcessMethod(DataProcessMethod dpm) {
        String oid = UUID.randomUUID().toString();
        dpm.setOid(oid);
        Date date = new Date();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        dpm.setCreatedTime(date);
        mongoTemplate.save(dpm);
        return dpm;
    }

    @Override
    public List<DataProcessMethod> findDataProcessMethodByIdList(List<String> idList) {
        return null;
    }

    @Override
    public DataProcessMethod updateDataProcessMethod(DataProcessMethod dpm) {
        return null;
    }
}
