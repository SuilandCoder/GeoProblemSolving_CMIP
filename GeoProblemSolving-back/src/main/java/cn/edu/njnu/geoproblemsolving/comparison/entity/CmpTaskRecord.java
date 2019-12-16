package cn.edu.njnu.geoproblemsolving.comparison.entity;

import cn.edu.njnu.geoproblemsolving.Entity.Metrics;
import cn.edu.njnu.geoproblemsolving.comparison.bean.CmpMetirc;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 10:33 2019/10/14
 * @Modified By:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class CmpTaskRecord {
    String recordId;
    String projectId;
    String name;
    String desc;
    String userId;
    String userName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createdTime;
    List<CmpInstance> instanceList;
    List<CmpMetirc> metrics;
    List<CmpTaskModel> cmpTaskModelList;

    public CmpTaskRecord(String recordId, String userId, String userName,String name, String description, List<CmpInstance> cmpInstanceList, List<CmpMetirc> cmpMetircs) {
        this.recordId = recordId;
        this.userId = userId;
        this.userName = userName;
        this.name = name;
        this.desc = description;
        this.instanceList = cmpInstanceList;
        this.metrics = cmpMetircs;
    }

    public CmpTaskRecord(String recordId,String projectId, String userId, String userName,String name, String description, List<CmpInstance> cmpInstanceList, List<CmpMetirc> cmpMetircs) {
        this.recordId = recordId;
        this.projectId = projectId;
        this.userId = userId;
        this.userName = userName;
        this.name = name;
        this.desc = description;
        this.instanceList = cmpInstanceList;
        this.metrics = cmpMetircs;
    }
}

