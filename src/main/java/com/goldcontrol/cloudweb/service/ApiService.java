package com.goldcontrol.cloudweb.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.goldcontrol.cloudweb.util.HttpUtil;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ApiService {

    @Value("${api_host}")
    private String api_host;

    public String getProjectsList(String token){
        String url = String.format("%s/project/getProjectsList?token=%s&active=1",api_host,token);
        String result = HttpUtil.get(url);
        JSONObject projectJSONObject = JSONObject.parseObject(result);
        JSONArray projectArray = projectJSONObject.getJSONArray("data");
        int vDeviceItemsNum = 0;
        for(Object object : projectArray){
            if(object instanceof JSONObject){
                JSONObject project = (JSONObject)object;
                //获取项目供暖面积
                dealProjectArea(token,project);
                //获取网关信息
                vDeviceItemsNum = vDeviceItemsNum + dealVdevicesItems(token,project);
            }
        }
        projectJSONObject.put("vDeviceItemsNum",vDeviceItemsNum);
        //供暖站个数
        projectJSONObject.put("heatNum",projectArray.size());
        return projectJSONObject.toJSONString();
    }

    public String getProjectInfo(String token,String projectId){
        String url = String.format("%s/projectInfo/list?token=%s&projectId=%s",api_host,token,projectId);
        return HttpUtil.get(url);
    }

    public String getProjectCurrentItemData(String token,String projectId){
        String url = String.format("%s/project/getProjectCurrentItemData?token=%s&projectID=%s",api_host,token,projectId);
        return HttpUtil.get(url);
    }

    public String getVdevicesItems(String token,String projectId){
        String url = String.format("%s/project/getVdevicesItems?token=%s&projectId=%s",api_host,token,projectId);
        return HttpUtil.get(url);
    }

//    public String initData(String token){
//        String projectList = getProjectsList(token) ;
//        JSONObject projectData = JSONObject.parseObject(projectList);
//        JSONArray projectArray = projectData.getJSONArray("data");
//        for(int i=0;i<projectArray.size();i++){
//            JSONObject project = projectArray.getJSONObject(i);
//            initDataOne(token,project);
//        }
////        initDataOne(token,projectArray.getJSONObject(0));
//        return projectArray.toJSONString();
//    }

    /**
     * 初始化某个数据
     * @param token
     * @param idx
     */
    public String initDataOne(String token,int idx){
        String projectList = getProjectsList(token) ;
        String ddd = getProjectCurrentItemData(token,"11");
        System.out.println(ddd);
        JSONObject projectData = JSONObject.parseObject(projectList);
        JSONArray projectArray = projectData.getJSONArray("data");
        JSONObject project = projectArray.getJSONObject(idx);
        String projectId = project.getString("id");
        //获取数据概览信息
        String infoString = getProjectInfo(token,projectId);
        JSONObject infoJSONObject = JSONObject.parseObject(infoString);
        JSONArray infoArray = infoJSONObject.getJSONArray("data");
        JSONObject dataView = new JSONObject();
        for(Object object : infoArray){
            if(object instanceof JSONObject){
                JSONObject jsonObject = (JSONObject)object;
                if("EnergyStorage".equals(jsonObject.getString("name"))){
                    dataView.put("EnergyStorage",jsonObject.getString("value"));//储能容量
                }else if("EnergyCapacity".equals(jsonObject.getString("name"))){
                    dataView.put("EnergyCapacity",jsonObject.getString("value"));//储能功率
                }else if("HeatArea".equals(jsonObject.getString("name"))){
                    dataView.put("HeatArea",jsonObject.getString("value"));//供暖面积
                }else if("CCERS".equals(jsonObject.getString("name"))){
                    dataView.put("CCERS",jsonObject.getString("value"));//CO2减排量
                }else if("CoalSave".equals(jsonObject.getString("name"))){
                    dataView.put("CoalSave",jsonObject.getString("value"));//标煤节约量
                }else if("GreenSpace".equals(jsonObject.getString("name"))){
                    dataView.put("GreenSpace",jsonObject.getString("value"));//绿化贡献面积
                }
            }
        }
        project.put("dataView",dataView);

        //获取监控中心数据
        JSONObject monitorView = new JSONObject();
        String monitorInfo = getProjectCurrentItemData(token,projectId);
        JSONObject monitorJSONObject = JSONObject.parseObject(monitorInfo);
        JSONArray monitorArray = monitorJSONObject.getJSONArray("data");
        for(Object object : monitorArray){
            if(object instanceof JSONObject){
                JSONObject jsonObject = (JSONObject)object;
                if("oottemp".equals(jsonObject.getString("itemname"))){
                    monitorView.put("oottemp",jsonObject.getString("val"));
                }else if("W_TEM".equals(jsonObject.getString("itemname"))){
                    monitorView.put("W_TEM",jsonObject.getString("val"));
                }else if("S_TEM".equals(jsonObject.getString("itemname"))){
                    monitorView.put("S_TEM",jsonObject.getString("val"));
                }else if("H_TEM".equals(jsonObject.getString("itemname"))){
                    monitorView.put("H_TEM",jsonObject.getString("val"));
                }
            }
        }
        project.put("monitorView",monitorView);
        return project.toJSONString();
    }

    public void dealProjectArea(String token,JSONObject project){
        String projectId = project.getString("id");
        //获取数据概览信息
        String infoString = getProjectInfo(token,projectId);
        JSONObject infoJSONObject = JSONObject.parseObject(infoString);
        JSONArray infoArray = infoJSONObject.getJSONArray("data");
        for(Object infoObject : infoArray){
            if(infoObject instanceof JSONObject){
                JSONObject jsonObject = (JSONObject)infoObject;
                if("HeatArea".equals(jsonObject.getString("name"))){
                    if(StringUtils.isNotBlank(jsonObject.getString("value"))){
                        project.put("heatArea",jsonObject.getString("value"));//供暖面积
                    }else{
                        project.put("heatArea","");
                    }
                }
            }
        }
    }

    /**
     * 处理项目网关信息
     */
    public int dealVdevicesItems(String token,JSONObject project){
        String projectId = project.getString("id");
        //获取数据概览信息
        String result = getVdevicesItems(token,projectId);
        JSONObject resultJSONObject = JSONObject.parseObject(result);
        JSONArray dataArray = resultJSONObject.getJSONArray("data");
        int num = 0;
        for(Object dataObject : dataArray){
            if(dataObject instanceof JSONObject){
                JSONObject jsonObject = (JSONObject)dataObject;
                String serialNumber = jsonObject.getString("serialNumber");
                if(serialNumber.substring(0,2).contains("14")){
                    project.put("serialNumber",serialNumber);
                    num++;
                }
            }
        }
        if(project.get("serialNumber") == null){
            project.put("serialNumber","");
        }
        return num;
    }

}
