package cn.edu.njnu.geoproblemsolving.comparison.entity;

import cn.edu.njnu.geoproblemsolving.comparison.bean.ProtocolItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 18:54 2019/10/15
 * @Modified By:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Protocol {
    @Id
    String oid;
    String type; // data process output
    List<ProtocolItem> protocolItems;
}
