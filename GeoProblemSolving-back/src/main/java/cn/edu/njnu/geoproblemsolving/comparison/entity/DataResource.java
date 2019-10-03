package cn.edu.njnu.geoproblemsolving.comparison.entity;

import cn.edu.njnu.geoproblemsolving.Entity.Metrics;
import cn.edu.njnu.geoproblemsolving.comparison.enums.DataResourceTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 20:22 2019/7/8
 * @Modified By:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class DataResource {
    // 基本信息
    private String dataId;
    private String name;
    private String ownerName;
    private String ownerId;
    private String fileName;
    private long fileSize;
    private DataResourceTypeEnum fileType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdTime;
    private List<String> imgPaths;
    private String privacy; //public/private
    private String md5;
    private String dcSourceStoreId; // 数据容器 sourceStoreId
    private String url;
    private Metrics metrics;

    // 地理信息
    private float[] extent; // west east south north
    private String location;
    private String scale;
    private String resolution;
    private String hight;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
    private String geometryInfo;
    private String proj;

    //语义信息
    private String abstractInfo;
    private String desc;
    private String[] keywords;
    private String categoryId;
    private String categoryName;

    public DataResource(String fileName,String dataId,String ownerId,String ownerName,Date createdTime){
        this.fileName = fileName;
        this.dataId = dataId;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.createdTime = createdTime;
    }

}
