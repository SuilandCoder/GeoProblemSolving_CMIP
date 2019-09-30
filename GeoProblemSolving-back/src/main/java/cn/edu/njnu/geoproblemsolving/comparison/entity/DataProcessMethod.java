package cn.edu.njnu.geoproblemsolving.comparison.entity;

import cn.edu.njnu.geoproblemsolving.comparison.bean.DataItem;
import cn.edu.njnu.geoproblemsolving.comparison.bean.DependencyItem;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 14:33 2019/9/30
 * @Modified By:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class DataProcessMethod {
    String oid;
    String name;
    String type; // normal | comparison
    String desc;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createdTime;
    List<DependencyItem> dependencyItemList;
    List<DataItem> inputList;
    List<DataItem> parameterList;
    List<DataItem> outputList;
}
