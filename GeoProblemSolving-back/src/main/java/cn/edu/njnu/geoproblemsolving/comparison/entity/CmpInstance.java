package cn.edu.njnu.geoproblemsolving.comparison.entity;

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
 * @Date: Created in 22:12 2019/9/20
 * @Modified By:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class CmpInstance {
    String instanceId;
    String name;
    String description;
    String userId;
    String userName;
    String type;
    String modelId;
    ModelRecord record;
    List<String> cmpDataList;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createdTime;
}
