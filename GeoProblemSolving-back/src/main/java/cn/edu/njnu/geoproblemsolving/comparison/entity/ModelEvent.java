package cn.edu.njnu.geoproblemsolving.comparison.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 10:34 2019/9/6
 * @Modified By:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class ModelEvent {
    public String name;
    public String type;
    public String description;
    public boolean optional;
    public String fileName;
    public String url;//数据容器下载路径
    public String dcSourceStoreId; // 数据容器 sourceStoreId
    public String mcDataId;//模型容器 数据ID
    public String dataResourceId;//比较平台 数据ID
    public String md5;
}
