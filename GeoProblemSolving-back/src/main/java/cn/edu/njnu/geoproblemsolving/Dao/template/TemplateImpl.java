package cn.edu.njnu.geoproblemsolving.Dao.template;

import cn.edu.njnu.geoproblemsolving.Entity.Concept;
import cn.edu.njnu.geoproblemsolving.Entity.SpatialReference;
import cn.edu.njnu.geoproblemsolving.Entity.Template;
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
 * @Date: Created in 16:30 2019/10/15
 * @Modified By:
 **/
@Service
public class TemplateImpl implements ITemplateDao {

    private final MongoTemplate mongoTemplate;

    public TemplateImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Template> findAll() {
        List<Template> all = mongoTemplate.findAll(Template.class);
        return all;
    }

    @Override
    public Template findTemplateByOid(String oid) {
        Query q = Query.query(Criteria.where("oid").is(oid));
        Template t = mongoTemplate.findOne(q, Template.class);
        return t;
    }

    @Override
    public Template createTemplate(Template template) {
        String oid = UUID.randomUUID().toString();
        template.setOid(oid);
        Date date = new Date();
        template.setCreateTime(date);
        template.setLastModifyTime(date);
        mongoTemplate.save(template);
        return template;
    }

    @Override
    public List<Template> findTemplateByIdList(List<String> idList) {
        Query query = Query.query(Criteria.where("oid").in(idList));
        List<Template> templates = mongoTemplate.find(query, Template.class);
        return templates;
    }

    @Override
    public Template updateTemplate(Template template) {
        Query query = Query.query(Criteria.where("oid").is(template.getOid()));
        Document document = new Document();
        mongoTemplate.getConverter().write(template, document);
        Update update = Update.fromDocument(document);
        mongoTemplate.upsert(query,update, Template.class);
        return template;
    }

    @Override
    public List<Template> getTemplates(String key, String value) {
        Query query = Query.query(Criteria.where(key).regex(value));
        List<Template> templates = mongoTemplate.find(query, Template.class);
        return templates;
    }
}
