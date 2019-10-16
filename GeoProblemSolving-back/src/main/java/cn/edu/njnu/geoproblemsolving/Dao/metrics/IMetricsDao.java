package cn.edu.njnu.geoproblemsolving.Dao.metrics;

import cn.edu.njnu.geoproblemsolving.Entity.Metrics;
import cn.edu.njnu.geoproblemsolving.Entity.Template;

import java.util.List;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 20:05 2019/10/2
 * @Modified By:
 **/
public interface IMetricsDao {
    List<Metrics> findAll();

    Metrics findMetricsByOid(String oid);

    Metrics createMetrics(Metrics metrics);

    List<Metrics> findMetricsByIdList(List<String> idList);

    Metrics updateMetrics(Metrics mr);

    List<Metrics> getMetrics(String key, String value);
}
