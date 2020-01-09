package cn.edu.njnu.geoproblemsolving.comparison.dao.project;

import cn.edu.njnu.geoproblemsolving.comparison.entity.CmpProject;

import java.util.List;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 9:46 2019/7/10
 * @Modified By:
 **/
public interface ICmpProjectDao {
    CmpProject addProject(CmpProject project);

    String deleteProject(CmpProject project);

    CmpProject updateProject(CmpProject project);

    CmpProject updateCmpItems(String projectId,String cmpItemId);

    CmpProject updateSubprojects(String parentId,String projectId,boolean isAdd);

    CmpProject updateSolutionList(String projectId,String solutionId,boolean isAdd);

    CmpProject updateInstanceList(String projectId,String instanceId,boolean isAdd);

    CmpProject updateTaskList(String projectId,String taskId,boolean isAdd);

    void updateModelList(String projectId,String modelId,boolean isAdd);

    void updateList(String projectId,String listName,String id,boolean isAdd);

    List<CmpProject> getAllProject();

    CmpProject findProjectById(String id);

    List<CmpProject> findByProjectIdList(List<String> projectIdList);

    CmpProject findByProjectId(String projectId);

    List<CmpProject> getProjects(String key, String value);

    String joinProject(String projectId,String userId);

}
