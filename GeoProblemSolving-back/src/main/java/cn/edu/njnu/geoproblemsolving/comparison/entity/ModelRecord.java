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
 * @Date: Created in 19:59 2019/9/16
 * @Modified By:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class ModelRecord {
//    String recordId;
    String msrId;//模型容器 模型运行记录ID
    String recordName;
    String description;
    String userId;
    String userName;
    String modelId;
    String modelName;
    int status; // 0:正在运行 1:成功 -1:失败
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date startTime;
    double time_span;
    List<ModelState> states;
    String ip;
    String port;
    String instanceId;
}
