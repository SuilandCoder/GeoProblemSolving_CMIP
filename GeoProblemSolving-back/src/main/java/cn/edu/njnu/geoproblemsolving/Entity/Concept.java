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
 * @Date: Created in 15:00 2019/10/15
 * @Modified By:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Concept{
    @Id
    String id;
    String oid;
    String name;
    List<String> classifications;
    String image;
    String description;
    String detail;
    Date createTime;
    Date lastModifyTime;
    List<String> related;
    String author;
    int loadCount;

    String alias;
    String parentId;
    String xml;
    String description_ZH;
    String description_EN;
    String name_ZH;
    String name_EN;

    //版本
    String lastModifier;
    List<String> contributors;
    List<String> versions;

    boolean lock;
}
