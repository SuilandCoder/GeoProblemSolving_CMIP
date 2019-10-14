package cn.edu.njnu.geoproblemsolving.comparison.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 10:45 2019/10/14
 * @Modified By:
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class CmpTaskModel {
    String metricId;
    String metricName;
    String graphXML;
    List<String> cmpMethodList;
    List<String> dataProcessMethodList;
}
