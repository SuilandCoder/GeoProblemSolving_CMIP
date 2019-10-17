package cn.edu.njnu.geoproblemsolving.comparison.dao.cmpinstance;

import cn.edu.njnu.geoproblemsolving.comparison.entity.CmpInstance;
import cn.edu.njnu.geoproblemsolving.comparison.entity.ModelRecord;

import java.util.List;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 14:28 2019/9/21
 * @Modified By:
 **/
public interface ICmpInstanceDao {
    CmpInstance findInstanceByInstanceId(String instanceId);

    CmpInstance createInstance(CmpInstance instance);

    List<CmpInstance> findInstanceByIdList(List<String> idList);

    CmpInstance updateInstance(CmpInstance cmpInstance);
}
