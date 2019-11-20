package cn.edu.njnu.geoproblemsolving.comparison.dao.cmpinstance;

import cn.edu.njnu.geoproblemsolving.comparison.entity.CmpInstance;
import cn.edu.njnu.geoproblemsolving.comparison.entity.DataProcessMethod;
import cn.edu.njnu.geoproblemsolving.comparison.entity.ModelResource;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 14:33 2019/9/21
 * @Modified By:
 **/
@Service
public class CmpInstanceDaoImpl implements ICmpInstanceDao {

    private final MongoTemplate mongoTemplate;

    public CmpInstanceDaoImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public CmpInstance findInstanceByInstanceId(String instanceId) {
        Query q = Query.query(Criteria.where("instanceId").is(instanceId));
        CmpInstance instance = mongoTemplate.findOne(q,CmpInstance.class);
        return instance;
    }

    @Override
    public CmpInstance createInstance(CmpInstance instance) {
        String dataId = UUID.randomUUID().toString();
        instance.setInstanceId(dataId);
        Date date = new Date();
        instance.setCreatedTime(date);
        mongoTemplate.save(instance);
        return instance;
    }

    @Override
    public List<CmpInstance> findInstanceByIdList(List<String> idList) {
        Query query = Query.query(Criteria.where("instanceId").in(idList));
        List<CmpInstance> cmpInstanceList = mongoTemplate.find(query, CmpInstance.class);
        return cmpInstanceList;
    }

    @Override
    public CmpInstance updateInstance(CmpInstance cmpInstance) {
        Query query = Query.query(Criteria.where("instanceId").is(cmpInstance.getInstanceId()));
        Document document = new Document();
        mongoTemplate.getConverter().write(cmpInstance, document);
        Update update = Update.fromDocument(document);
        mongoTemplate.upsert(query,update, CmpInstance.class);
        return cmpInstance;
    }
}
