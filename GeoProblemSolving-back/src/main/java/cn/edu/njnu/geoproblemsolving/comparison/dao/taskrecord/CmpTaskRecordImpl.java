package cn.edu.njnu.geoproblemsolving.comparison.dao.taskrecord;

import cn.edu.njnu.geoproblemsolving.comparison.dao.project.CmpProjectDaoImpl;
import cn.edu.njnu.geoproblemsolving.comparison.entity.CmpTaskRecord;
import cn.edu.njnu.geoproblemsolving.comparison.entity.ModelRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 14:40 2019/10/14
 * @Modified By:
 **/
@Service
public class CmpTaskRecordImpl implements IcmpTaskRecordDao {

    private final MongoTemplate mongoTemplate;

    @Autowired
    CmpProjectDaoImpl cmpProjectDao;

    public CmpTaskRecordImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public CmpTaskRecord findByRecordId(String recordId) {
        Query query = Query.query(Criteria.where("recordId").is(recordId));
        CmpTaskRecord ctr = mongoTemplate.findOne(query, CmpTaskRecord.class);
        return ctr;
    }

    @Override
    public CmpTaskRecord createCmpTaskRecord(String projcetId,CmpTaskRecord ctr) {
        ctr.setCreatedTime(new Date());
        mongoTemplate.save(ctr);
        cmpProjectDao.updateTaskList(projcetId, ctr.getRecordId(), true);
        return ctr;
    }

    @Override
    public List<CmpTaskRecord> findByRecordIdList(List<String> idList) {
        Query query = Query.query(Criteria.where("recordId").in(idList));
        List<CmpTaskRecord> cmpTaskRecords = mongoTemplate.find(query, CmpTaskRecord.class);
        return cmpTaskRecords;
    }

    @Override
    public CmpTaskRecord updateRecord(CmpTaskRecord ctr) {
        return null;
    }
}
