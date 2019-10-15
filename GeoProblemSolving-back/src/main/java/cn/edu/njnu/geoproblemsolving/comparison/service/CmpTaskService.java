package cn.edu.njnu.geoproblemsolving.comparison.service;

import cn.edu.njnu.geoproblemsolving.comparison.bean.TaskOutputItem;
import cn.edu.njnu.geoproblemsolving.comparison.constant.HttpContant;
import cn.edu.njnu.geoproblemsolving.comparison.dao.cmpinstance.CmpInstanceDaoImpl;
import cn.edu.njnu.geoproblemsolving.comparison.dao.dataprocess_method.DataProcessMethodDaoImpl;
import cn.edu.njnu.geoproblemsolving.comparison.dao.dataresource.DataResourceDaoImpl;
import cn.edu.njnu.geoproblemsolving.comparison.dao.taskrecord.CmpMethodRecordImpl;
import cn.edu.njnu.geoproblemsolving.comparison.entity.CmpInstance;
import cn.edu.njnu.geoproblemsolving.comparison.entity.CmpMethodRecord;
import cn.edu.njnu.geoproblemsolving.comparison.entity.DataProcessMethod;
import cn.edu.njnu.geoproblemsolving.comparison.entity.DataResource;
import cn.edu.njnu.geoproblemsolving.comparison.enums.ResultEnum;
import cn.edu.njnu.geoproblemsolving.comparison.exception.MyException;
import cn.edu.njnu.geoproblemsolving.comparison.utils.MyHttpUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.xml.internal.ws.util.CompletedFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 22:13 2019/10/11
 * @Modified By:
 **/
@Service
public class CmpTaskService {

    @Resource
    private MongoTemplate mongoTemplate;

    @Autowired
    CmpMethodRecordImpl cmpMethodRecordDao;

    @Async
    public void runTask(JSONObject cmpMethodItem, String metricId, String metricName, JSONArray dataProcessMethodList) throws IOException, ExecutionException, InterruptedException {
        cmpMethodItem.put("metricId", metricId);
        cmpMethodItem.put("metricName", metricName);
        JSONArray inputList = cmpMethodItem.getJSONArray("inputList");
        List<Stack<JSONObject>> stackList = new ArrayList<>();
        for (int i = 0; i < inputList.size(); i++) {
            //建栈：
            JSONObject inputData = inputList.getJSONObject(i);
            String type = inputData.getString("type");
            if ("normal".equals(type) && inputData.getString("dataStoreId")==null) {
                String dependencyMethodId = inputData.getString("oid");
                Stack<JSONObject> stack = new Stack<>();
                Stack<JSONObject> taskStack = createTaskStack(dependencyMethodId, dataProcessMethodList, stack);
                stackList.add(taskStack);
            }
        }

        //执行栈中的任务
        if(stackList.size()>0){
            List<Future<List<JSONObject>>> tasks = new ArrayList<>();
            for(int j=0;j<stackList.size();j++){
                Stack<JSONObject> stack = stackList.get(j);
                Future<List<JSONObject>> taskFuture = excuteTaskInStack(stack);
                tasks.add(taskFuture);
            }
            //等待任务执行完
            while (true){
                boolean taskComplete = true;
                for(int k=0;k<tasks.size();k++){
                    Future<List<JSONObject>> task = tasks.get(k);
                    if(!task.isDone()){
                        taskComplete = false;
                    }
                }
                if(taskComplete) break;
            }
            //任务执行成功 更新 dataProcessMethodList
            for (Future<List<JSONObject>> task : tasks) {
                List<JSONObject> taskResList_Stack = task.get();
                for(JSONObject taskRes:taskResList_Stack){
                    for(int h=0;h<dataProcessMethodList.size();h++){
                        JSONObject dpm = dataProcessMethodList.getJSONObject(h);
                        String methodId = dpm.getString("methodId");
                        if (methodId.equals(taskRes.getString("methodId"))) {
                            dpm.put("inputList", taskRes.getJSONArray("inputList"));
                            dpm.put("output", taskRes.getJSONObject("output"));
                        }
                    }
                }
            }
            //更新 对比方法 的inputList,并判断输入是否完备
            for (int i = 0; i < inputList.size(); i++) {
                JSONObject inputData = inputList.getJSONObject(i);
                String type = inputData.getString("type");
                if ("normal".equals(type) && inputData.getString("dataStoreId")==null) {
                    String dependencyMethodId = inputData.getString("oid");
                    for(int h=0;h<dataProcessMethodList.size();h++){
                        JSONObject dpm = dataProcessMethodList.getJSONObject(h);
                        String methodId = dpm.getString("methodId");
                        if (methodId.equals(dependencyMethodId)) {
                            inputData.put("dataStoreId", dpm.getJSONObject("output").getString("dataStoreId"));
                            cmpMethodItem.getJSONArray("inputList").set(i, inputData);
                        }
                    }
                }
                if (inputData.getString("dataStoreId")==null) {
                    // 将脚本运行结果保存数据库
                    CmpMethodRecord cmr = cmpMethodItem.toJavaObject(CmpMethodRecord.class);
                    cmr.setStatus("-1");
                    cmpMethodRecordDao.updateRecord(cmr);
                    throw new MyException(ResultEnum.COMPARISON_METHOD_LACKS_INPUT_DATA);
                }
            }
        }
        JSONObject cmpMethodTaskRes = runScript(cmpMethodItem);
        JSONObject res = new JSONObject();
        res.put("cmpMethodInfo", cmpMethodTaskRes);
        res.put("dataProcessMethodList", dataProcessMethodList);

//        return res;
    }


    // 为对比方法的每一个输入创建一个任务栈
    private Stack<JSONObject> createTaskStack(String dependencyMethodId, JSONArray dataProcessMethodList, Stack<JSONObject> stack) {
        for (int i = 0; i < dataProcessMethodList.size(); i++) {
            JSONObject dpm = dataProcessMethodList.getJSONObject(i);
            if (dependencyMethodId.equals(dpm.getString("methodId"))) {
                stack.push(dpm);
                // 数据处理方法默认只有一个输入
                JSONObject input = dpm.getJSONArray("inputList").getJSONObject(0);
                String type = input.getString("type");
                String dataStoreId = input.getString("dataStoreId");
                if ("normal".equals(type) && dataStoreId==null) {
                    input.getString("oid");
                    stack = createTaskStack(input.getString("oid"),dataProcessMethodList,stack);
                }
            }
        }
        return stack;
    }

    @Async
    Future<List<JSONObject>> excuteTaskInStack(Stack<JSONObject> stack) throws IOException {
        List<JSONObject> res = new ArrayList<>();
        while (!stack.empty()) {
            JSONObject methodTask = stack.pop();
            // 获取上个任务返回的结果，更新input
            if(res.size()>0){
                JSONObject lastTaskRes = res.get(res.size()-1);
                JSONObject output = lastTaskRes.getJSONObject("output");
                String dataStoreId = output.getString("dataStoreId");
                JSONObject input = methodTask.getJSONArray("inputList").getJSONObject(0);
                input.put("dataStoreId", dataStoreId);
                methodTask.getJSONArray("inputList").set(0, input);
            }
            JSONObject methodRes = runScript(methodTask);
            res.add(methodRes);
        }
        return  new AsyncResult<>(res);
    }



    public JSONObject runScript(JSONObject cmpMethodItem) throws IOException {
        String url = "http://" + HttpContant.DATA_CONTAINER_IP + ":" + HttpContant.DATA_CONTAINER_PORT + "/cmpTask/runCmpTask";
        String res = MyHttpUtils.post_RESCODE(url, cmpMethodItem,"0");
//        System.out.println(res);
        // 将脚本运行结果保存数据库
        if(res==null){
            CmpMethodRecord cmr = cmpMethodItem.toJavaObject(CmpMethodRecord.class);
            cmr.setStatus("-1");
            cmpMethodRecordDao.updateRecord(cmr);
            throw new MyException(ResultEnum.FAILED_TO_EXCUTE_SCRIPT);
        }else{
            JSONObject resJSON = JSONObject.parseObject(res);
            CmpMethodRecord cmr = resJSON.toJavaObject(CmpMethodRecord.class);
            cmr.setStatus("1");
            //更新 output downloadUrl
            TaskOutputItem output = cmr.getOutput();
            String downLoadUrl = "http://" + HttpContant.DATA_CONTAINER_IP + ":" + HttpContant.DATA_CONTAINER_PORT + "/dataResource/getResource?sourceStoreId="+output.getDataStoreId();
            output.setDownloadUrl(downLoadUrl);
            cmr.setOutput(output);
            cmpMethodRecordDao.updateRecord(cmr);
            return resJSON;
        }
    }


    @Async
    public Future<String> task1() throws InterruptedException {
        long currentTimeMillis = System.currentTimeMillis();
        Thread.sleep(1000);
        long currentTimeMillis1 = System.currentTimeMillis();
        System.out.println("task1任务耗时：" + (currentTimeMillis1 - currentTimeMillis) + "ms");
        return new AsyncResult<>("task1执行完毕");
    }

    @Async
    public Future<String> task2() throws InterruptedException {
        long currentTimeMillis = System.currentTimeMillis();
        Thread.sleep(2000);
        long currentTimeMillis1 = System.currentTimeMillis();
        System.out.println("task2任务耗时：" + (currentTimeMillis1 - currentTimeMillis) + "ms");
        return new AsyncResult<>("task2执行完毕");
    }

    @Async
    public Future<String> task3() throws InterruptedException {
        long currentTimeMillis = System.currentTimeMillis();
        Thread.sleep(3000);
        long currentTimeMillis1 = System.currentTimeMillis();
        System.out.println("task3任务耗时：" + (currentTimeMillis1 - currentTimeMillis) + "ms");
        return new AsyncResult<>("task3执行完毕");
    }

    //弃用
    public JSONObject parseTaskItem(JSONObject cmpMethodItem, String metricId, String metricName, MongoTemplate mongoTemplate) {
        cmpMethodItem.put("metricId", metricId);
        cmpMethodItem.put("metricName", metricName);
        DataProcessMethodDaoImpl dataProcessMethodDao = new DataProcessMethodDaoImpl(mongoTemplate);
        DataProcessMethod method = dataProcessMethodDao.findDataProcessMethodByOid(cmpMethodItem.getString("methodId"));

        cmpMethodItem.put("methodSourceId", method.getScriptSourceId());
        JSONArray inputList = cmpMethodItem.getJSONArray("inputList");
        for (int i = 0; i < inputList.size(); i++) {
            JSONObject input = (JSONObject) inputList.get(i);
            String type = input.getString("type");
            if (type.equals("instance")) {
                CmpInstanceDaoImpl cmpInstanceDao = new CmpInstanceDaoImpl(mongoTemplate);
                CmpInstance instance = cmpInstanceDao.findInstanceByMsrId(input.getString("instanceId"));
                input.put("instanceName", instance.getName());

                DataResourceDaoImpl dataResourceDao = new DataResourceDaoImpl(mongoTemplate);
                DataResource cmpData = dataResourceDao.findDataResourceById(input.getString("cmpDataId"));
                input.put("dataStoreId", cmpData.getDcSourceStoreId());
            }
        }
        cmpMethodItem.put("inputList", inputList);
        return cmpMethodItem;
    }
}
