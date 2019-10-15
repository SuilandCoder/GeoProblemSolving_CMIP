package cn.edu.njnu.geoproblemsolving.Dao.unit;

import cn.edu.njnu.geoproblemsolving.Entity.Metrics;
import cn.edu.njnu.geoproblemsolving.Entity.SpatialReference;
import cn.edu.njnu.geoproblemsolving.Entity.Unit;
import cn.edu.njnu.geoproblemsolving.comparison.entity.CmpProject;
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
 * @Date: Created in 20:17 2019/10/2
 * @Modified By:
 **/
@Service
public class UnitDaoImpl implements IUnitDao {

    private final MongoTemplate mongoTemplate;

    public UnitDaoImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Unit> findAll() {
        List<Unit> all = mongoTemplate.findAll(Unit.class);
        return all;
    }

    @Override
    public Unit findUnitByOid(String oid) {
        Query q = Query.query(Criteria.where("oid").is(oid));
        Unit unit = mongoTemplate.findOne(q, Unit.class);
        return unit;
    }

    @Override
    public Unit createUnit(Unit unit) {
        String oid = UUID.randomUUID().toString();
        unit.setOid(oid);
        Date date = new Date();
        unit.setCreateTime(date);
        mongoTemplate.save(unit);
        return unit;
    }

    @Override
    public List<Unit> findUnitByIdList(List<String> idList) {
        Query query = Query.query(Criteria.where("oid").in(idList));
        List<Unit> units = mongoTemplate.find(query, Unit.class);
        return units;
    }

    @Override
    public Unit updateUnit(Unit unit) {
        Query query = Query.query(Criteria.where("oid").is(unit.getOid()));
        Document document = new Document();
        mongoTemplate.getConverter().write(unit, document);
        Update update = Update.fromDocument(document);
        mongoTemplate.upsert(query, update, Unit.class);
        return unit;
    }

    // 模糊查询
    @Override
    public List<Unit> getUnits(String key, String value) {
        Query query = Query.query(Criteria.where(key).regex(".*?\\"+value+".*"));
        List<Unit> units = mongoTemplate.find(query, Unit.class);
        return units;
    }
}
