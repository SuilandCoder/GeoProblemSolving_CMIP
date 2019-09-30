package cn.edu.njnu.geoproblemsolving.comparison.dao.modelrecord;

import cn.edu.njnu.geoproblemsolving.comparison.entity.CmpProject;
import cn.edu.njnu.geoproblemsolving.comparison.entity.ModelRecord;
import cn.edu.njnu.geoproblemsolving.comparison.entity.ModelResource;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 16:40 2019/9/17
 * @Modified By:
 **/
public class ModelRecordDaoImpl implements IModelRecordDao {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public ModelRecordDaoImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public ModelRecord findModelByMsrId(String msrId) {
        Query query = Query.query(Criteria.where("msrId").is(msrId));
        ModelRecord record = mongoTemplate.findOne(query, ModelRecord.class);
        return record;
    }

    @Override
    public ModelRecord createModelRecord(ModelRecord mr) {
        mongoTemplate.save(mr);
        return mr;
    }

    @Override
    public List<ModelRecord> findModelRecordByIdList(List<String> idList) {
        Query query = Query.query(Criteria.where("msrId").in(idList));
        List<ModelRecord> cmpModelRecord = mongoTemplate.find(query, ModelRecord.class);
        return cmpModelRecord;
    }

    @Override
    public ModelRecord updateRecord(ModelRecord mr) {
        Query query = Query.query(Criteria.where("msrId").is(mr.getMsrId()));
        Document document = new Document();
        mongoTemplate.getConverter().write(mr, document);
        Update update = Update.fromDocument(document);
        mongoTemplate.upsert(query,update,ModelRecord.class);
        return mr;
    }
}
