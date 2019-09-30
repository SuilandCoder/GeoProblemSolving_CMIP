package cn.edu.njnu.geoproblemsolving.comparison.service;

import cn.edu.njnu.geoproblemsolving.comparison.constant.HttpContant;
import cn.edu.njnu.geoproblemsolving.comparison.dao.computablemodel.ComputableModelImpl;
import cn.edu.njnu.geoproblemsolving.comparison.entity.*;
import cn.edu.njnu.geoproblemsolving.comparison.utils.MyHttpUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.tomcat.util.modeler.modules.ModelerSource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.StringUtils;

import javax.servlet.http.Part;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * @Author: SongJie
 * @Description:
 * @Date: Created in 10:37 2019/9/5
 * @Modified By:
 **/
public class CmpModelService {

    public static Map<String,Object> deployModel(Part filePart) throws IOException, URISyntaxException {
        // 通过管理服务器获取用于部署的任务服务器
        String serviceTaskInfo = getServiceTask();
        if("".equals(serviceTaskInfo)){
            return null;
        }
        JSONObject serviceTaskObj = JSONObject.parseObject(serviceTaskInfo);
        if(!"1".equals(serviceTaskObj.getString("code"))){
            return null;
        }
        JSONObject taskObj = serviceTaskObj.getJSONObject("data");
        String host = taskObj.getString("host");
        String port = taskObj.getString("port");

        // 部署模型
        String deployRes = deployModel(host, port, filePart);
        if("".equals(deployRes)){
            return null;
        }
        JSONObject deployObj = JSONObject.parseObject(deployRes);
        if(!"1".equals(deployObj.getString("code"))){
            return null;
        }
        JSONObject deployData = deployObj.getJSONObject("data");
        String ms_host = deployData.getString("host");
        String ms_port = deployData.getString("port");
        String msid = deployData.getString("msid");
        ModelServiceNode modelServiceNode = new ModelServiceNode(ms_host, ms_port, msid);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("modelservice",modelServiceNode);

        // 获取模型MDL信息
        String mdlInfo = getModelInfo(ms_host, ms_port, msid);
        if("".equals(mdlInfo)){
            return resultMap;
        }
        JSONObject mdlObj = JSONObject.parseObject(mdlInfo);
        if(!"1".equals(mdlObj.getString("code"))){
            return resultMap;
        }
        JSONObject mdlData = mdlObj.getJSONObject("data");
        String ms_xml = mdlData.getString("ms_xml");
        System.out.println(ms_xml);
        List<ModelState> modelStates = parseMdl(ms_xml);

        resultMap.put("modelStates",modelStates);
        return resultMap;
    }

    public static List<ModelState> getModelState(ComputableModel cm, MongoTemplate mongoTemplate) throws IOException, URISyntaxException {
        List<ModelState> states = cm.getStates();
        if(states==null||states.size()==0){
            // 重新获取模型MDL信息
            ModelServiceNode serviceNode = cm.getServiceNode();
            String mdlInfo = getModelInfo(serviceNode.getHost(), serviceNode.getPort(), serviceNode.getMsid());
            if("".equals(mdlInfo)){
                return null;
            }
            JSONObject mdlObj = JSONObject.parseObject(mdlInfo);
            if(!"1".equals(mdlObj.getString("code"))){
                return null;
            }
            JSONObject mdlData = mdlObj.getJSONObject("data");
            String ms_xml = mdlData.getString("ms_xml");
            System.out.println(ms_xml);
            states = parseMdl(ms_xml);
            // 更新计算模型中 state 信息
            ComputableModelImpl cmDao = new ComputableModelImpl(mongoTemplate);
            cmDao.updateStates(cm.getOid(),states);
        }
        return states;
    }

    // 获取数据服务容器信息
    public static String getDataServer(String host,String port,String pid, String username) throws IOException {
        String body = "";
        CloseableHttpClient client = HttpClients.createDefault();
        String requestUrl ="http://"+ HttpContant.MANAGER_SERVER_IP+":"+HttpContant.MANAGER_SERVER_PORT+"/GeoModeling/computableModel/createTask";
        HttpPost httpPost = new HttpPost(requestUrl);
        httpPost.setHeader("Content-Type","application/json");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ip",host);
        jsonObject.put("port",port);
        jsonObject.put("pid",pid);
        jsonObject.put("username",username);
        StringEntity stringEntity = new StringEntity(jsonObject.toJSONString(),"UTF-8");
        httpPost.setEntity(stringEntity);
        CloseableHttpResponse httpResponse = client.execute(httpPost);
        HttpEntity responseEntity = httpResponse.getEntity();
        int statusCode= httpResponse.getStatusLine().getStatusCode();
        if(statusCode == 200){
            BufferedReader reader = new BufferedReader(new InputStreamReader(responseEntity.getContent()));
            StringBuffer buffer = new StringBuffer();
            String str = "";
            while(!StringUtils.isEmpty(str = reader.readLine())) {
                buffer.append(str);
            }
            body = buffer.toString();
        }
        //释放链接
        httpResponse.close();
        client.close();
        if("".equals(body)){
            return null;
        }
        JSONObject taskNodeObj = JSONObject.parseObject(body);
        if(!"1".equals(taskNodeObj.getString("code"))){
            return null;
        }
        JSONObject dataObj = taskNodeObj.getJSONObject("data");
        JSONObject dxServer = dataObj.getJSONObject("dxServer");
        return dxServer.toJSONString();
    }

    // 根据pid获取可用的任务服务器
    public static Map<String,String> getTaskNode(String pid) throws IOException, URISyntaxException {
        String body="";
        CloseableHttpClient client = HttpClients.createDefault();
        String requestUrl = "http://"+ HttpContant.MANAGER_SERVER_IP+":"+HttpContant.MANAGER_SERVER_PORT+"/GeoModeling/taskNode/getServiceTask/"+pid;
        URL url=new URL(requestUrl);
        URI uri=new URI(url.getProtocol(), url.getHost()+":"+url.getPort(), url.getPath(), url.getQuery(),null);
        HttpGet httpGet = new HttpGet(uri);
        CloseableHttpResponse response = client.execute(httpGet);
        HttpEntity responseEntity = response.getEntity();
        int statusCode= response.getStatusLine().getStatusCode();
        if(statusCode == 200){
            BufferedReader reader = new BufferedReader(new InputStreamReader(responseEntity.getContent()));
            StringBuffer buffer = new StringBuffer();
            String str = "";

            while(!StringUtils.isEmpty(str = reader.readLine())) {
                buffer.append(str);
            }
            body = buffer.toString();
        }
        //释放链接
        response.close();
        client.close();
        if("".equals(body)){
            return null;
        }
        JSONObject taskNodeObj = JSONObject.parseObject(body);
        if(!"1".equals(taskNodeObj.getString("code"))){
            return null;
        }
        JSONObject dataObj = taskNodeObj.getJSONObject("data");
        String host = dataObj.getString("host");
        String port = dataObj.getString("port");
        HashMap<String, String> map = new HashMap<>();
        map.put("host",host);
        map.put("port",port);
        return map;
    }

    //上传数据
    public static JSONObject uploadData(String host, String port, String type, String userName, Part filePart) throws IOException {
        String body = "";
        CloseableHttpClient client = HttpClients.createDefault();
        String requestUrl ="http://"+ HttpContant.MANAGER_SERVER_IP+":"+HttpContant.MANAGER_SERVER_PORT+"/GeoModeling/computableModel/uploadData";
        HttpPost httpPost = new HttpPost(requestUrl);
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.addBinaryBody("file",filePart.getInputStream(),ContentType.MULTIPART_FORM_DATA,filePart.getSubmittedFileName());
//        multipartEntityBuilder.addBinaryBody("file_model",new File("F:\\workspace\\workspace_cpp\\model_zip_light\\climate_tools_10.zip"), ContentType.MULTIPART_FORM_DATA,"climate_tools_10");

        multipartEntityBuilder.addTextBody("host", host);
        multipartEntityBuilder.addTextBody("port",port);
        multipartEntityBuilder.addTextBody("type",type);
        multipartEntityBuilder.addTextBody("userName",userName);
        HttpEntity httpEntity = multipartEntityBuilder.build();
        httpPost.setEntity(httpEntity);
        CloseableHttpResponse httpRes = client.execute(httpPost);
        HttpEntity responseEntity = httpRes.getEntity();
        int statusCode= httpRes.getStatusLine().getStatusCode();
        if(statusCode == 200){
            BufferedReader reader = new BufferedReader(new InputStreamReader(responseEntity.getContent()));
            StringBuffer buffer = new StringBuffer();
            String str = "";

            while(!StringUtils.isEmpty(str = reader.readLine())) {
                buffer.append(str);
            }
            body = buffer.toString();
        }
        //释放链接
        httpRes.close();
        client.close();
        if("".equals(body)){
            return null;
        }
        JSONObject taskNodeObj = JSONObject.parseObject(body);
        if(!"1".equals(taskNodeObj.getString("code"))){
            return null;
        }
        JSONObject data = taskNodeObj.getJSONObject("data");
        return data;
    }

    public static JSONObject invokeModel(String ip,String port,String pid,String username,String inputs) throws IOException {
        String body = "";
        CloseableHttpClient client = HttpClients.createDefault();
        String requestUrl = "http://"+ HttpContant.MANAGER_SERVER_IP+":"+HttpContant.MANAGER_SERVER_PORT+"/GeoModeling/computableModel/invoke";
        HttpPost httpPost = new HttpPost(requestUrl);
        httpPost.setHeader("Content-Type","application/json");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ip",ip);
        jsonObject.put("port",port);
        jsonObject.put("pid",pid);
        jsonObject.put("username",username);
        jsonObject.put("inputs",JSONArray.parseArray(inputs));
        StringEntity stringEntity = new StringEntity(jsonObject.toJSONString(),"UTF-8");
        httpPost.setEntity(stringEntity);
        CloseableHttpResponse httpResponse = client.execute(httpPost);
        HttpEntity responseEntity = httpResponse.getEntity();
        int statusCode= httpResponse.getStatusLine().getStatusCode();
        if(statusCode == 200){
            BufferedReader reader = new BufferedReader(new InputStreamReader(responseEntity.getContent()));
            StringBuffer buffer = new StringBuffer();
            String str = "";
            while(!StringUtils.isEmpty(str = reader.readLine())) {
                buffer.append(str);
            }
            body = buffer.toString();
        }
        //释放链接
        httpResponse.close();
        client.close();
        if("".equals(body)){
            return null;
        }
        JSONObject recordInfoObj = JSONObject.parseObject(body);
        if(!"1".equals(recordInfoObj.getString("code"))){
            return null;
        }
        JSONObject data = recordInfoObj.getJSONObject("data");
        return data;
    }


    public static String invokeModel_MC(String ip,String port,String msid,String inputs) throws IOException, URISyntaxException {
        String url = "http://"+ ip+":"+port+"/modelser/"+msid+"?ac=run&inputdata=";
        JSONArray objects = JSONArray.parseArray(inputs);
        JSONArray inputDataArr = new JSONArray();
        for(Object obj:objects){
            JSONObject state = (JSONObject) obj;
            String stateName = state.getString("stateName");
            JSONArray events = state.getJSONArray("events");
            for(Object eventObj:events){
                JSONObject event = (JSONObject) eventObj;
                String eventUrl = event.getString("url");
                if (eventUrl != null && !eventUrl.isEmpty()) {
                    JSONObject inputData = new JSONObject();
                    inputData.put("StateName", stateName);
                    inputData.put("Event", event.getString("name"));
                    inputData.put("Destroyed","false");
                    inputData.put("Optional","0");
                    // 发送准备数据的请求
                    String dataId_MC = getDataIDByUrl(ip, port, eventUrl);
                    if (dataId_MC == null) {
                        return null;
                    }
                    inputData.put("DataId", dataId_MC);
                    inputDataArr.add(inputData);
                }
            }
        }
        url = url + inputDataArr.toJSONString();
        String res = MyHttpUtils.myGet_str(url);
        return res;
    }

    // 获取模型运行记录
    public static ModelRecord getModelInstanceRecord(String ip, String port, String recordId,String inputs,String userId,String userName,String modelId,String modelName,String recordName,List<ModelState> modelStates) throws IOException, URISyntaxException {
        if(ip==null){
            ip = "172.21.212.75";
        }
        if(port==null){
            port = "8060";
        }
        String urlStr = "http://" + ip + ":" + port + "/modelserrun/json/" + recordId;
        JSONObject jsonObject = MyHttpUtils.myGet(urlStr);
        if (jsonObject == null) {
            return null;
        }
        //解析 记录信息
        ModelRecord modelRecord = parseRecord(jsonObject, inputs, ip, port,modelStates,recordId);
        modelRecord.setUserId(userId);
        modelRecord.setUserName(userName);
        modelRecord.setModelId(modelId);
        modelRecord.setModelName(modelName);
        if(recordName==null){
            modelRecord.setRecordName(modelName+"_"+modelRecord.getStartTime());
        }else{
            modelRecord.setRecordName(recordName);
        }
        modelRecord.setIp(ip);
        modelRecord.setPort(port);
        return modelRecord;
    }

    //解析模型容器返回的模型运行记录
    private static ModelRecord parseRecord(JSONObject jsonObject,String inputs,String ip,String port,List<ModelState> modelStates, String recordId) throws IOException {
        ModelRecord modelRecord = new ModelRecord();
        String msrid = jsonObject.getString("_id");
        Date startTime = jsonObject.getDate("msr_datetime");
        modelRecord.setMsrId(msrid);
        modelRecord.setStartTime(startTime);

        if(modelStates==null){
            JSONArray inputsObj = JSONArray.parseArray(inputs);
            JSONArray msr_inputs = jsonObject.getJSONArray("msr_input");
            modelStates = new ArrayList<>();
            for(Object obj:inputsObj){
//            ModelState state = (ModelState) obj;
                JSONObject jo = (JSONObject) obj;
                ModelState state = jo.toJavaObject(ModelState.class);
                modelStates.add(state);
            }

            for (Object msr_input : msr_inputs) {
                JSONObject msr_inputObj = (JSONObject) msr_input;
                String dataId = msr_inputObj.getString("DataId");
                if (!dataId.isEmpty()) {
                    for (ModelState state : modelStates) {
                        List<ModelEvent> events = state.getEvents();
                        for(ModelEvent event:events){
                            if(state.getStateName().equals(msr_inputObj.getString("StateName"))&&event.getName().equals(msr_inputObj.getString("Event"))){
                                event.setMcDataId(dataId);
                                break;
                            }
                        }
                    }
                }
            }
        }

//        modelRecord.setInputs(input_mdInfo);

        double msr_span = jsonObject.getDoubleValue("msr_span");
        if (msr_span > 0) {
            modelRecord.setTime_span(msr_span);

            List<ModelDataInfo> output_mdInfo = new ArrayList<>();
            JSONArray msr_output = jsonObject.getJSONArray("msr_output");
            int status = -1;
            for (Object output : msr_output) {
                JSONObject outputObj = (JSONObject) output;
                String dataId = outputObj.getString("DataId");
                if (!dataId.isEmpty()) {
                    status = 1;
                    for (ModelState state : modelStates) {
                        List<ModelEvent> events = state.getEvents();
                        for(ModelEvent event:events){
                            if(state.getStateName().equals(outputObj.getString("StateName"))&&event.getName().equals(outputObj.getString("Event"))){
                                event.setMcDataId(dataId);
                                String url = "http://" + ip + ":" + port +"/geodata/"+dataId;
                                event.setFileName(state.getStateName()+"_"+event.getName());
                                event.setUrl(url);
                                break;
                            }
                        }
                    }
                }
            }
            modelRecord.setStatus(status);
            if (status == 1) {
                //将输出数据上传至数据容器
                String res = CmpDataService.uploadOutputToDc(ip, port, recordId);
                JSONArray outputJSONArr = JSONArray.parseArray(res);
                for(Object obj:outputJSONArr){
                    JSONObject outputJson = (JSONObject) obj;
                    String stateName = outputJson.getString("StateName");
                    String eventName = outputJson.getString("Event");
                    String url = outputJson.getString("Url");
                    String sourceStoreId = "";
                    if(url.contains("&sourceStoreId=")){
                        sourceStoreId = url.substring(url.indexOf("&sourceStoreId=")+15,url.lastIndexOf("&"));
                    }
                    String suffix = "";
                    if(url.contains("&suffix=")){
                        suffix = url.substring(url.indexOf("&suffix=")+8);
                    }
                    for (ModelState state : modelStates) {
                        List<ModelEvent> events = state.getEvents();
                        for(ModelEvent event:events){
                            if(state.getStateName().equals(stateName)&&event.getName().equals(eventName)){
                                event.setUrl(url);
                                event.setDcSourceStoreId(sourceStoreId);
                                event.setFileName(event.getName()+"."+suffix);
                                break;
                            }
                        }
                    }
                }
            }
        }else{
            modelRecord.setStatus(0);
        }
        modelRecord.setStates(modelStates);
        return modelRecord;
    }



    private static String getDataIDByUrl(String ip,String port,String url) throws IOException {
        String urlStr = "http://"+ ip+":"+port+"/geodata?type=url";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("gd_tag", "fff");
        jsonObject.put("data", url);
        String res = MyHttpUtils.post_JSONObj(urlStr, jsonObject);
        return res;
    }


    // 获取可用的任务服务器
    private static String getServiceTask() throws IOException, URISyntaxException {
        String body="";
        CloseableHttpClient client = HttpClients.createDefault();
        String requestUrl = "http://"+ HttpContant.MANAGER_SERVER_IP+":"+HttpContant.MANAGER_SERVER_PORT+"/GeoModeling/taskNode/getTaskForMicroService";
        URL url=new URL(requestUrl);
        URI uri=new URI(url.getProtocol(), url.getHost()+":"+url.getPort(), url.getPath(), url.getQuery(),null);
        HttpGet httpGet = new HttpGet(uri);
        CloseableHttpResponse response = client.execute(httpGet);
        HttpEntity responseEntity = response.getEntity();
        int statusCode= response.getStatusLine().getStatusCode();
        if(statusCode == 200){
            BufferedReader reader = new BufferedReader(new InputStreamReader(responseEntity.getContent()));
            StringBuffer buffer = new StringBuffer();
            String str = "";

            while(!StringUtils.isEmpty(str = reader.readLine())) {
                buffer.append(str);
            }
            body = buffer.toString();
        }
        //释放链接
        response.close();
        client.close();
        return body;
    }

    // 从 task 服务器向模型服务容器部署模型
    private static String deployModel(String host,String port,Part filePart) throws IOException {
        String body = "";
        CloseableHttpClient client = HttpClients.createDefault();
        String requestUrl ="http://" + host+":"+port+"/server/modelser/deploy";
        HttpPost httpPost = new HttpPost(requestUrl);
//        httpPost.setHeader("Content-Type","application/json");

        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.addBinaryBody("file_model",filePart.getInputStream(),ContentType.MULTIPART_FORM_DATA,filePart.getSubmittedFileName());
//        multipartEntityBuilder.addBinaryBody("file_model",new File("F:\\workspace\\workspace_cpp\\model_zip_light\\climate_tools_10.zip"), ContentType.MULTIPART_FORM_DATA,"climate_tools_10");

        multipartEntityBuilder.addTextBody("ms_limited", "0");
        HttpEntity httpEntity = multipartEntityBuilder.build();
        httpPost.setEntity(httpEntity);
        CloseableHttpResponse httpResponse = client.execute(httpPost);

        HttpEntity responseEntity = httpResponse.getEntity();
        int statusCode= httpResponse.getStatusLine().getStatusCode();
        if(statusCode == 200){
            BufferedReader reader = new BufferedReader(new InputStreamReader(responseEntity.getContent()));
            StringBuffer buffer = new StringBuffer();
            String str = "";

            while(!StringUtils.isEmpty(str = reader.readLine())) {
                buffer.append(str);
            }
            body = buffer.toString();
        }
        //释放链接
        httpResponse.close();
        client.close();
        return body;
    }

    // 获取模型信息
    private static String getModelInfo(String host,String port,String msid) throws IOException, URISyntaxException {
        String body="";
        CloseableHttpClient client = HttpClients.createDefault();
        String requestUrl = "http://"+ host+":"+port+"/modelser/json/"+msid;
        URL url=new URL(requestUrl);
        URI uri=new URI(url.getProtocol(), url.getHost()+":"+url.getPort(), url.getPath(), url.getQuery(),null);
        HttpGet httpGet = new HttpGet(uri);
        CloseableHttpResponse response = client.execute(httpGet);
        HttpEntity responseEntity = response.getEntity();
        int statusCode= response.getStatusLine().getStatusCode();
        if(statusCode == 200){
            BufferedReader reader = new BufferedReader(new InputStreamReader(responseEntity.getContent()));
            StringBuffer buffer = new StringBuffer();
            String str = "";
            while(!StringUtils.isEmpty(str = reader.readLine())) {
                buffer.append(str);
            }
            body = buffer.toString();
        }
        //释放链接
        response.close();
        client.close();
        return body;
    }

    // 解析 mdl 信息
    private static List<ModelState> parseMdl(String mdlString){
        JSONObject mdlJson = JSONObject.parseObject(mdlString);
        JSONObject modelClass = mdlJson.getJSONObject("ModelClass");
        JSONObject behavior = modelClass.getJSONObject("Behavior");
        JSONObject stateGroup = behavior.getJSONObject("StateGroup");
        JSONObject states = stateGroup.getJSONObject("States");
        List<ModelState> modelStates = new ArrayList<>();
        JSONArray stateList;
        if(states.get("State") instanceof JSONArray){
            stateList = states.getJSONArray("State");
        }else{
            stateList = new JSONArray();
            JSONObject state = states.getJSONObject("State");
            stateList.add(state);
        }
        for (Object state : stateList) {
            JSONObject stateObj = (JSONObject) state;
            ModelState modelState = new ModelState();
            JSONObject state$ = stateObj.getJSONObject("$");
            modelState.setStateId(state$.getString("id"));
            modelState.setStateName(state$.getString("name"));
            modelState.setDescription(state$.getString("description"));
            modelState.setType(state$.getString("type"));
            List<ModelEvent> modelEvents = new ArrayList<>();

            JSONArray events = stateObj.getJSONArray("Event");
            for (Object event : events){
                JSONObject eventObj = (JSONObject) event;
                ModelEvent modelEvent = new ModelEvent();

                JSONObject event$ = eventObj.getJSONObject("$");
                modelEvent.setDescription(event$.getString("description"));
                modelEvent.setName(event$.getString("name"));
                modelEvent.setOptional(event$.getBoolean("optional"));
                modelEvent.setType(event$.getString("type"));

                modelEvents.add(modelEvent);
            }
            modelState.setEvents(modelEvents);
            modelStates.add(modelState);
        }
        return modelStates;
    }



}
