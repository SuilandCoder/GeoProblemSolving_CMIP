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
 * @Date: Created in 16:26 2019/10/15
 * @Modified By:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Template {
    String oid;
    String name;
    List<String> classifications;
    String description;
    String detail;
    String author;
    String image;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date lastModifyTime;
    int loadCount;
    String xml;
    boolean lock;

}
