package cn.edu.njnu.geoproblemsolving.comparison.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 14:35 2019/9/30
 * @Modified By:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DependencyItem {
    String item;
    String version;
    String downloadUrl;
}
