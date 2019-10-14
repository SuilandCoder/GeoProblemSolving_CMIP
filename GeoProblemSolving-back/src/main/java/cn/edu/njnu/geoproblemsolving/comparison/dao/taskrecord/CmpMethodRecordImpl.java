package cn.edu.njnu.geoproblemsolving.comparison.dao.taskrecord;

import cn.edu.njnu.geoproblemsolving.comparison.entity.CmpMethodRecord;
import cn.edu.njnu.geoproblemsolving.comparison.entity.ModelRecord;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 15:03 2019/10/14
 * @Modified By:
 **/
@Service
public class CmpMethodRecordImpl implements IcmpMethodRecordDao {

    private final MongoTemplate mongoTemplate;

    public CmpMethodRecordImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public CmpMethodRecord findByRecordId(String recordId) {
        Query query = Query.query(Criteria.where("recordId").is(recordId));
        CmpMethodRecord cmr = mongoTemplate.findOne(query, CmpMethodRecord.class);
        return cmr;
    }

    @Override
    public CmpMethodRecord createCmpMethodRecord(CmpMethodRecord cmr) {
        mongoTemplate.save(cmr);
        return cmr;
    }

    @Override
    public List<CmpMethodRecord> findByRecordIdList(List<String> idList) {
        Query query = Query.query(Criteria.where("recordId").in(idList));
        List<CmpMethodRecord> cmpMethodRecords = mongoTemplate.find(query, CmpMethodRecord.class);
        return cmpMethodRecords;
    }

    @Override
    public CmpMethodRecord updateRecord(CmpMethodRecord cmr) {
        Query query = Query.query(Criteria.where("recordId").is(cmr.getRecordId()));
        Document document = new Document();
        mongoTemplate.getConverter().write(cmr, document);
        Update update = Update.fromDocument(document);
        mongoTemplate.upsert(query,update,CmpMethodRecord.class);
        return cmr;
    }
}
