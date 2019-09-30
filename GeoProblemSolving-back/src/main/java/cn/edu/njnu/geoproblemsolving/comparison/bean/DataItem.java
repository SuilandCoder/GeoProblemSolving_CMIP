package cn.edu.njnu.geoproblemsolving.comparison.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 14:38 2019/9/30
 * @Modified By:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataItem {
    String name;
    String type; // file | double | int | String | boolean
    String desc;
    String value;
}
