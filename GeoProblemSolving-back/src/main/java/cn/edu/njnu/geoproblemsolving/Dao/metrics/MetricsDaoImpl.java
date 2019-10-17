package cn.edu.njnu.geoproblemsolving.Dao.metrics;

import cn.edu.njnu.geoproblemsolving.Entity.Metrics;
import cn.edu.njnu.geoproblemsolving.Entity.Template;
import jdk.nashorn.internal.objects.annotations.Where;
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
 * @Date: Created in 20:09 2019/10/2
 * @Modified By:
 **/
@Service
public class MetricsDaoImpl implements IMetricsDao {
    private final MongoTemplate mongoTemplate;

    public MetricsDaoImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Metrics> findAll() {
        List<Metrics> all = mongoTemplate.findAll(Metrics.class);
        return all;
    }

    @Override
    public Metrics findMetricsByOid(String oid) {
        Query q = Query.query(Criteria.where("oid").is(oid));
        Metrics metric = mongoTemplate.findOne(q,Metrics.class);
        return metric;
    }

    @Override
    public Metrics createMetrics(Metrics metrics) {
        String oid = UUID.randomUUID().toString();
        metrics.setOid(oid);
        Date date = new Date();
        metrics.setUpdateTime(date);
        mongoTemplate.save(metrics);
        return metrics;
    }

    @Override
    public List<Metrics> findMetricsByIdList(List<String> idList) {
        return null;
    }

    @Override
    public Metrics updateMetrics(Metrics mr) {
        return null;
    }

    @Override
    public List<Metrics> getMetrics(String key, String value) {
        Query query = Query.query(Criteria.where(key).regex(value));
        List<Metrics> metrics = mongoTemplate.find(query, Metrics.class);
        return metrics;
    }
}
