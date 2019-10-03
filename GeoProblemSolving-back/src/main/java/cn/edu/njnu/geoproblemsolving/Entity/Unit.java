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
 * @Date: Created in 19:59 2019/10/2
 * @Modified By:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Unit {
    String oid;
    String name;
    String alias;
    List<String> classifications;
    String detail;
    String author;
    String type;
    String parentId;
    String xml;
    String name_EN;
    String name_ZH;
    String description_EN;
    String description_ZH;
    String image;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date lastModifyTime;
    String loadCount;
    String lock;
}
