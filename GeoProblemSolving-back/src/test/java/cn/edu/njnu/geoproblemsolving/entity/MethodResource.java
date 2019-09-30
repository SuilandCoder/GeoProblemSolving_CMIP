package cn.edu.njnu.geoproblemsolving.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 14:20 2019/9/30
 * @Modified By:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class MethodResource {
    String id;
    String name;
    List<DependencyItem> diList;
    List<DataItem> dataItemList;
}

class DependencyItem{
    String name;
    String version;
    String url;
}

class DataItem{
    String name;
    String type;
    String desc;
}
