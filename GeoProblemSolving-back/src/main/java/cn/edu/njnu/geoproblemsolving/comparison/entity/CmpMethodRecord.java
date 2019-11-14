package cn.edu.njnu.geoproblemsolving.comparison.entity;

import cn.edu.njnu.geoproblemsolving.comparison.bean.DataItem;
import cn.edu.njnu.geoproblemsolving.comparison.bean.DependencyItem;
import cn.edu.njnu.geoproblemsolving.comparison.bean.TaskInputEntity;
import cn.edu.njnu.geoproblemsolving.comparison.bean.TaskOutputItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 10:53 2019/10/14
 * @Modified By:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class CmpMethodRecord {
    String recordId;
    TaskOutputItem output;
    DataItem input; // 创建对比方法时的输入数据配置信息
    String interpretor;
    String status; // 0 运行中 1 成功 -1 失败 2 终止
    String metricName;
    String metricId;
    String methodId;
    String methodSourceId;
    List<DependencyItem> dependencyLibs;
    List<DataItem> params;
    List<TaskInputEntity> inputList; // 对比任务接收的输入数据
}


