package cn.edu.njnu.geoproblemsolving.comparison.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 9:23 2019/9/17
 * @Modified By:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelDataInfo {
    String stateName;
    String eventName;
    String fileName;
    String url;//数据容器下载路径
    String mcDataId;//模型容器 数据ID
    String dataResourceId;//比较平台 数据ID
    String md5;
}
