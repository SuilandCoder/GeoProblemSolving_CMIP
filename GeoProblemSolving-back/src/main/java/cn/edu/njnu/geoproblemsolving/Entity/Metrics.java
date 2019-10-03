package cn.edu.njnu.geoproblemsolving.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 19:52 2019/10/2
 * @Modified By:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Metrics {
    String oid;
    String name;
    String alias;
    String description;
    String min;
    String max;
    Unit unit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date updateTime;
}
