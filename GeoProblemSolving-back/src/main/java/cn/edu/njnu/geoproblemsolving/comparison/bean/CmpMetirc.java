package cn.edu.njnu.geoproblemsolving.comparison.bean;

import cn.edu.njnu.geoproblemsolving.Entity.Metrics;
import com.alibaba.fastjson.JSONArray;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 14:10 2019/10/14
 * @Modified By:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CmpMetirc{
    Metrics metric;
    JSONArray releatedInfo; // 存储与加入对比的 metric 相关的 instance 及 cmpData;
}
