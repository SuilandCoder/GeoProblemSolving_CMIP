package cn.edu.njnu.geoproblemsolving.comparison.dao.dataprocess_method;

import cn.edu.njnu.geoproblemsolving.Entity.UserEntity;
import cn.edu.njnu.geoproblemsolving.comparison.entity.DataProcessMethod;
import cn.edu.njnu.geoproblemsolving.comparison.entity.DataResource;

import java.util.List;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 15:05 2019/9/30
 * @Modified By:
 **/
public interface IDataProcessMethodDao {
    DataProcessMethod findDataProcessMethodById(String id);

    DataProcessMethod createDataProcessMethod(DataProcessMethod dpm);

    List<DataProcessMethod> findDataProcessMethodByIdList(List<String> idList);

    DataProcessMethod updateDataProcessMethod(DataProcessMethod dpm);
}
