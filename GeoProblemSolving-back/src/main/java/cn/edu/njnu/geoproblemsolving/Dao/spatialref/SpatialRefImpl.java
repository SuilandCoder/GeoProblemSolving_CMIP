package cn.edu.njnu.geoproblemsolving.Dao.spatialref;

import cn.edu.njnu.geoproblemsolving.Entity.SpatialReference;
import cn.edu.njnu.geoproblemsolving.Entity.Unit;
import cn.edu.njnu.geoproblemsolving.comparison.entity.CmpMethodRecord;
import cn.edu.njnu.geoproblemsolving.comparison.entity.CmpTaskRecord;
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
 * @Date: Created in 14:45 2019/10/15
 * @Modified By:
 **/
@Service
public class SpatialRefImpl implements ISpatialRefDao {

    private final MongoTemplate mongoTemplate;

    public SpatialRefImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public SpatialReference findSpatialReferenceByOid(String oid) {
        Query q = Query.query(Criteria.where("oid").is(oid));
        SpatialReference sr = mongoTemplate.findOne(q,SpatialReference.class);
        return sr;
    }

    @Override
    public SpatialReference createSpatialReference(SpatialReference sr) {
        String oid = UUID.randomUUID().toString();
        sr.setOid(oid);
        Date date = new Date();
        sr.setCreateTime(date);
        sr.setLastModifyTime(date);
        mongoTemplate.save(sr);
        return sr;
    }

    @Override
    public List<SpatialReference> findSpatialReferenceByIdList(List<String> idList) {
        Query query = Query.query(Criteria.where("oid").in(idList));
        List<SpatialReference> spatialReferences = mongoTemplate.find(query, SpatialReference.class);
        return spatialReferences;
    }

    @Override
    public SpatialReference updateSpatialReference(SpatialReference sr) {
        Query query = Query.query(Criteria.where("oid").is(sr.getOid()));
        Document document = new Document();
        mongoTemplate.getConverter().write(sr, document);
        Update update = Update.fromDocument(document);
        mongoTemplate.upsert(query,update, SpatialReference.class);
        return sr;
    }

    @Override
    public List<SpatialReference> getSpatialReferences(String key, String value) {
        Query query = Query.query(Criteria.where(key).regex(".*?\\"+value+".*"));
        List<SpatialReference> spatialReferences = mongoTemplate.find(query, SpatialReference.class);
        return spatialReferences;
    }
}
