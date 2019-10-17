package cn.edu.njnu.geoproblemsolving.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
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
    @Id
    String id;
    String oid;
    String name;
    String wkname;
    String wkt;
    List<String> classifications;
    String image;
    String description;
    String detail;
    Date createTime;
    Date lastModifyTime;
    String author;
    int loadCount;

    String type;
    String parentId;
    String xml;
    String name_EN;
    String description_EN;

    //版本
    String lastModifier;
    List<String> contributors;
    List<String> versions;

    boolean lock;
}
