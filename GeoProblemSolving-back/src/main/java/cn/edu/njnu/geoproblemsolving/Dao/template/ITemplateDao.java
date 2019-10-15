package cn.edu.njnu.geoproblemsolving.Dao.template;

import cn.edu.njnu.geoproblemsolving.Entity.Concept;
import cn.edu.njnu.geoproblemsolving.Entity.SpatialReference;
import cn.edu.njnu.geoproblemsolving.Entity.Template;

import java.util.List;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 16:29 2019/10/15
 * @Modified By:
 **/
public interface ITemplateDao {
    List<Template> findAll();

    Template findTemplateByOid(String oid);

    Template createTemplate(Template template);

    List<Template> findTemplateByIdList(List<String> idList);

    Template updateTemplate(Template template);

    List<Template> getTemplates(String key, String value);
}
