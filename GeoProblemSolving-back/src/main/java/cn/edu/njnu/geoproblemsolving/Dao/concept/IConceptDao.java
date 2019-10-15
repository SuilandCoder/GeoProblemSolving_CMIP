package cn.edu.njnu.geoproblemsolving.Dao.concept;

import cn.edu.njnu.geoproblemsolving.Entity.Concept;

import java.util.List;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 16:21 2019/10/15
 * @Modified By:
 **/
public interface IConceptDao {
    List<Concept> findAll();

    Concept findConceptByOid(String oid);

    Concept createConcept(Concept concept);

    List<Concept> findConceptByIdList(List<String> idList);

    Concept updateConcept(Concept concept);

    List<Concept> getConcepts(String key, String value);
}
