package cn.edu.njnu.geoproblemsolving.comparison.dao.dataprocess_method;

import cn.edu.njnu.geoproblemsolving.Entity.Metrics;
import cn.edu.njnu.geoproblemsolving.comparison.entity.CmpProject;
import cn.edu.njnu.geoproblemsolving.comparison.entity.DataProcessMethod;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 15:05 2019/9/30
 * @Modified By:
 **/
@Service
public class DataProcessMethodDaoImpl implements IDataProcessMethodDao {

    private final MongoTemplate mongoTemplate;

    public DataProcessMethodDaoImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public DataProcessMethod findDataProcessMethodByOid(String oid) {
        Query q = Query.query(Criteria.where("oid").is(oid));
        DataProcessMethod method = mongoTemplate.findOne(q,DataProcessMethod.class);
        return method;
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
    public List<DataProcessMethod> getAllDataProcessMethod() {
        List<DataProcessMethod> dataProcessMethods = mongoTemplate.findAll(DataProcessMethod.class);
        return dataProcessMethods;
    }

    @Override
    public List<DataProcessMethod> findDataProcessMethod(String key, String value) {
        Query query = Query.query(Criteria.where(key).is(value));
        if (mongoTemplate.find(query, DataProcessMethod.class).isEmpty()) {
            return null;
        } else {
            List<DataProcessMethod> dataProcessMethods = mongoTemplate.find(query, DataProcessMethod.class);
            return dataProcessMethods;
        }
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
