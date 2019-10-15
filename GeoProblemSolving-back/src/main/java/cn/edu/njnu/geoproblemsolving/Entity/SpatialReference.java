package cn.edu.njnu.geoproblemsolving.Entity;

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
 * @Date: Created in 14:34 2019/10/15
 * @Modified By:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class SpatialReference {
    String oid;
    String name;
    String wkname;
    String wkt;
    List<String> classifications;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date lastModifyTime;
    String author;
    int loadCount;
    String type;
    String parentId;
    String xml;
    String name_EN;
    String description_EN;
    boolean lock;
}
