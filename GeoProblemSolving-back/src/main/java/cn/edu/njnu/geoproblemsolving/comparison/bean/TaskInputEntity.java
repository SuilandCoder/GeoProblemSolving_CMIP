package cn.edu.njnu.geoproblemsolving.comparison.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 14:59 2019/10/14
 * @Modified By:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskInputEntity{
    String type;
    String oid; // type 为 normal 时
    String instanceId;      // type 为 instance 时
    String instanceName;    // type 为 instance 时
    String cmpDataId;       // type 为 instance 时
    String dataStoreId;
}
