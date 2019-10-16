package cn.edu.njnu.geoproblemsolving.comparison.bean;

import cn.edu.njnu.geoproblemsolving.Entity.Metrics;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 19:44 2019/10/15
 * @Modified By:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolItem {
    String name;
    List<KVBean> metaDoc;
    String extendDoc; // markdown 描述信息
    Metrics metrics;
}
