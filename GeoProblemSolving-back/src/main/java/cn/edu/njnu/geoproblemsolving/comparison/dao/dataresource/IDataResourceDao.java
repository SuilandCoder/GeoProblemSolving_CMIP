package cn.edu.njnu.geoproblemsolving.comparison.dao.dataresource;

import cn.edu.njnu.geoproblemsolving.Entity.UserEntity;
import cn.edu.njnu.geoproblemsolving.comparison.entity.DataResource;
import cn.edu.njnu.geoproblemsolving.comparison.entity.ModelRecord;

import java.util.List;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 16:41 2019/7/19
 * @Modified By:
 **/
public interface IDataResourceDao {

    DataResource findDataResourceById(String id);

    DataResource createDataResource(DataResource dr);

    List<DataResource> findDataResourceByIdList(List<String> idList);

    DataResource updateDataResource(DataResource dr);

    List<String> createDataResByNameList(List<String> nameList, UserEntity user);
}
