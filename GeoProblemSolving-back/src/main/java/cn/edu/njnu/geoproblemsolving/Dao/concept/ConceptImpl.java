package cn.edu.njnu.geoproblemsolving.Dao.concept;

import cn.edu.njnu.geoproblemsolving.Entity.Concept;
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
 * @Date: Created in 16:21 2019/10/15
 * @Modified By:
 **/
@Service
public class ConceptImpl implements IConceptDao{

    private final MongoTemplate mongoTemplate;

    public ConceptImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public List<Concept> findAll() {
        List<Concept> all = mongoTemplate.findAll(Concept.class);
        return all;
    }

    @Override
    public Concept findConceptByOid(String oid) {
        Query q = Query.query(Criteria.where("oid").is(oid));
        Concept c = mongoTemplate.findOne(q, Concept.class);
        return c;
    }

    @Override
    public Concept createConcept(Concept concept) {
        String oid = UUID.randomUUID().toString();
        concept.setOid(oid);
        Date date = new Date();
        concept.setCreateTime(date);
        concept.setLastModifyTime(date);
        mongoTemplate.save(concept);
        return concept;
    }

    @Override
    public List<Concept> findConceptByIdList(List<String> idList) {
        Query query = Query.query(Criteria.where("oid").in(idList));
        List<Concept> concepts = mongoTemplate.find(query, Concept.class);
        return concepts;
    }

    @Override
    public Concept updateConcept(Concept concept) {
        Query query = Query.query(Criteria.where("oid").is(concept.getOid()));
        Document document = new Document();
        mongoTemplate.getConverter().write(concept, document);
        Update update = Update.fromDocument(document);
        mongoTemplate.upsert(query,update, Concept.class);
        return concept;
    }

    @Override
    public List<Concept> getConcepts(String key, String value) {
        Query query = Query.query(Criteria.where(key).regex(value));
        List<Concept> concepts = mongoTemplate.find(query, Concept.class);
        return concepts;
    }
}
