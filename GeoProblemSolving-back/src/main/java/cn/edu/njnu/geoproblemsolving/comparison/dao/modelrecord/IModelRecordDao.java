package cn.edu.njnu.geoproblemsolving.comparison.dao.modelrecord;

import cn.edu.njnu.geoproblemsolving.Entity.UserEntity;
import cn.edu.njnu.geoproblemsolving.comparison.entity.ModelRecord;

import java.util.List;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 16:35 2019/9/17
 * @Modified By:
 **/
public interface IModelRecordDao {
    ModelRecord findModelByMsrId(String msrId);

    ModelRecord createModelRecord(ModelRecord mr);

    List<ModelRecord> findModelRecordByIdList(List<String> idList);

    ModelRecord updateRecord(ModelRecord mr);
}
