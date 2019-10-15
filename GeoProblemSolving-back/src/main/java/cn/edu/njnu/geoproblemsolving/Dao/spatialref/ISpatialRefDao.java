package cn.edu.njnu.geoproblemsolving.Dao.spatialref;

import cn.edu.njnu.geoproblemsolving.Entity.Concept;
import cn.edu.njnu.geoproblemsolving.Entity.SpatialReference;
import cn.edu.njnu.geoproblemsolving.Entity.Unit;

import java.util.List;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 14:44 2019/10/15
 * @Modified By:
 **/
public interface ISpatialRefDao {
    List<SpatialReference> findAll();

    SpatialReference findSpatialReferenceByOid(String oid);

    SpatialReference createSpatialReference(SpatialReference sr);

    List<SpatialReference> findSpatialReferenceByIdList(List<String> idList);

    SpatialReference updateSpatialReference(SpatialReference unit);

    List<SpatialReference> getSpatialReferences(String key, String value);
}
