package cn.edu.njnu.geoproblemsolving.comparison.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 10:51 2019/10/14
 * @Modified By:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class TaskOutputItem extends DataItem {
    String fileName;
    String suffix;
    String downloadUrl;
    String dataStoreId;
}
