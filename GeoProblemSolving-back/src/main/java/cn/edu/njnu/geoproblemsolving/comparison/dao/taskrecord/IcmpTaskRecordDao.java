package cn.edu.njnu.geoproblemsolving.comparison.dao.taskrecord;

import cn.edu.njnu.geoproblemsolving.comparison.entity.CmpTaskRecord;
import cn.edu.njnu.geoproblemsolving.comparison.entity.ModelRecord;

import java.util.List;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 14:36 2019/10/14
 * @Modified By:
 **/
public interface IcmpTaskRecordDao {
    CmpTaskRecord findByRecordId(String recordId);

    CmpTaskRecord createCmpTaskRecord(String projcetId,CmpTaskRecord ctr);

    List<CmpTaskRecord> findByRecordIdList(List<String> idList);

    CmpTaskRecord updateRecord(CmpTaskRecord ctr);
}
