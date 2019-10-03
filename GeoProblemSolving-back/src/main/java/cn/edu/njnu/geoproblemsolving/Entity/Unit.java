package cn.edu.njnu.geoproblemsolving.comparison.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

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
    String description;
    String detail;
    String author;
    String image;
    String createTime;
    String lastModifyTime;
    String loadCount;
    String lock;

}
