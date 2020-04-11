package cn.edu.njnu.geoproblemsolving.comparison.entity;

import cn.edu.njnu.geoproblemsolving.Entity.ProjectEntity;
import cn.edu.njnu.geoproblemsolving.comparison.enums.ProjectType;
import com.alibaba.fastjson.JSONArray;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 19:42 2019/7/8
 * @Modified By:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class CmpProject{
    String projectId;
    String parentId;
    ProjectType projectType;
    String category;
    List<String> subprojects = new ArrayList<>();
    String title;
    String description;
    String background;
    String goals;
    String managerId;
    String managerName;
    JSONArray members;
    String createTime;
    String privacy;
    List<Protocol> protocols;
    List<String> unitList;
    List<String> conceptList;
    List<String> templateList;
    List<String> spatialRefList;
    List<String> modelList = new ArrayList<>(); //todo 模型信息 此处不应该是 String 类型
    List<String> instanceList = new ArrayList<>(); // instance
    List<String> taskList = new ArrayList<>();     // task
}
