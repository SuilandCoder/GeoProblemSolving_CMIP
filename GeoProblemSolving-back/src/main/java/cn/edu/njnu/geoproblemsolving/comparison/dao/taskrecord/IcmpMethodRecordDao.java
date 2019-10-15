package cn.edu.njnu.geoproblemsolving.comparison.dao.taskrecord;

import cn.edu.njnu.geoproblemsolving.comparison.entity.CmpMethodRecord;
import cn.edu.njnu.geoproblemsolving.comparison.entity.CmpTaskRecord;

import java.util.List;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 15:01 2019/10/14
 * @Modified By:
 **/
public interface IcmpMethodRecordDao {
    CmpMethodRecord findByRecordId(String recordId);

    CmpMethodRecord createCmpMethodRecord(CmpMethodRecord cmr);

    List<CmpMethodRecord> findByRecordIdList(List<String> idList);

    CmpMethodRecord updateRecord(CmpMethodRecord cmr);
}
