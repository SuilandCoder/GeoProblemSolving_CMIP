package cn.edu.njnu.geoproblemsolving.comparison.dao.dataresource;

import cn.edu.njnu.geoproblemsolving.Entity.UserEntity;
import cn.edu.njnu.geoproblemsolving.comparison.entity.DataResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 16:43 2019/7/19
 * @Modified By:
 **/
public class DataResourceDaoImpl implements IDataResourceDao {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public DataResourceDaoImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public DataResource findDataResourceById(String id) {
        return null;
    }

    @Override
    public DataResource createDataResource(DataResource dr) {
        String dataId = UUID.randomUUID().toString();
        dr.setDataId(dataId);
        Date date = new Date();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        dr.setCreatedTime(date);
        mongoTemplate.save(dr);
        return dr;
    }

    @Override
    public List<DataResource> findDataResourceByIdList(List<String> idList) {
        return null;
    }

    @Override
    public DataResource updateDataResource(DataResource dr) {
        return null;
    }

    @Override
    public List<String> createDataResByNameList(List<String> nameList, UserEntity user) {
        if(nameList.size()>0){
            int len = nameList.size();
            List<String> idList = new ArrayList<>();
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            for(int i=0;i<len;i++){
                String name = nameList.get(i);
                String dataId = UUID.randomUUID().toString();
                DataResource dataResource = new DataResource(name,dataId, user.getUserId(), user.getUserName(),date);
                mongoTemplate.save(dataResource);
                idList.add(dataId);
            }
            return idList;
        }else{
            return null;
        }
    }
}
