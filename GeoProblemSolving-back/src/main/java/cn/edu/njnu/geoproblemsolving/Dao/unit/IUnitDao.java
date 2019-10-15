package cn.edu.njnu.geoproblemsolving.Dao.unit;

import cn.edu.njnu.geoproblemsolving.Entity.Metrics;
import cn.edu.njnu.geoproblemsolving.Entity.SpatialReference;
import cn.edu.njnu.geoproblemsolving.Entity.Unit;

import java.util.List;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 20:16 2019/10/2
 * @Modified By:
 **/
public interface IUnitDao {

    List<Unit> findAll();

    Unit findUnitByOid(String oid);

    Unit createUnit(Unit unit);

    List<Unit> findUnitByIdList(List<String> idList);

    Unit updateUnit(Unit unit);

    List<Unit> getUnits(String key, String value);
}
