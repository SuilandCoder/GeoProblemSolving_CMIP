package cn.edu.njnu.geoproblemsolving.Dao.metrics;

import cn.edu.njnu.geoproblemsolving.Entity.Metrics;
import jdk.nashorn.internal.objects.annotations.Where;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 20:09 2019/10/2
 * @Modified By:
 **/
public class MetricsDaoImpl implements IMetricsDao {
    private final MongoTemplate mongoTemplate;

    public MetricsDaoImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
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
}
