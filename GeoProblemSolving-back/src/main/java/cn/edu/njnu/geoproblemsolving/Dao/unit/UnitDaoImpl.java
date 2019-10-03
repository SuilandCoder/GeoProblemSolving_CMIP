package cn.edu.njnu.geoproblemsolving.Dao.unit;

import cn.edu.njnu.geoproblemsolving.Entity.Metrics;
import cn.edu.njnu.geoproblemsolving.Entity.Unit;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 20:17 2019/10/2
 * @Modified By:
 **/
public class UnitDaoImpl implements IUnitDao {

    private final MongoTemplate mongoTemplate;

    public UnitDaoImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Unit findUnitByOid(String oid) {
        Query q = Query.query(Criteria.where("oid").is(oid));
        Unit unit = mongoTemplate.findOne(q,Unit.class);
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
        return null;
    }

    @Override
    public Unit updateUnit(Unit unit) {
        return null;
    }
}
