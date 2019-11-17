package com.goldcontrol.cloudweb.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.goldcontrol.cloudweb.util.HttpUtil;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ApiService {

    @Value("${api_host}")
    private String api_host;

    public String getProjectsList(String token) throws ParseException {
        String url = String.format("%s/project/getProjectsList?token=%s&active=1",api_host,token);
        String result = HttpUtil.get(url);
        System.out.println(result);
        JSONObject projectJSONObject = JSONObject.parseObject(result);
        JSONArray projectArray = projectJSONObject.getJSONArray("data");
        HashSet<String> serialNumberSetTotal = new HashSet<String>();//所有的项目网关集合
        int onCount = 0;//设备开机状态
        for(Object object : projectArray){
            if(object instanceof JSONObject){
                JSONObject project = (JSONObject)object;
                setDeafultVal(project);
                //获取项目供暖面积及其他信息
                dealProjectArea(token,project);
                //获取网关信息
                HashSet<String> serialNumberSet = dealVdevicesItems(token,project);
                serialNumberSetTotal.addAll(serialNumberSet);
                //获取项目的设备状态
                String currentItemData = getProjectCurrentItemData(token,project.getString("id"));
                String status = getProjectItemData(currentItemData,"设备状态");
                //当前时间耗电量
                String powerConsume = getProjectItemData(currentItemData,"耗电量");
                //获取耗电量的设备id和itemid
                JSONObject deviceObject = getProjectDeviceInfo(currentItemData,"耗电量");
                //今天凌晨的耗电量
                String todayConsume = getTodayGreenData(token,deviceObject);
                //本月第一天的耗电量
                String month1Consume = getMonthGreenData(token,deviceObject);
                //今年第一天的耗电量
                String year1Consume = getYearGreenData(token,deviceObject);

                JSONObject greenView = project.getJSONObject("greenView");
                if(StringUtils.isNotBlank(powerConsume) && StringUtils.isNotBlank(todayConsume)){
                    greenView.put("today",Double.parseDouble(powerConsume) - Double.parseDouble(todayConsume));
                }
                if(StringUtils.isNotBlank(powerConsume) && StringUtils.isNotBlank(month1Consume)){
                    greenView.put("month",Double.parseDouble(powerConsume) - Double.parseDouble(todayConsume));
                }
                if(StringUtils.isNotBlank(powerConsume) && StringUtils.isNotBlank(year1Consume)){
                    greenView.put("year",Double.parseDouble(powerConsume) - Double.parseDouble(year1Consume));
                }
                project.put("greenView",greenView);
                if("1".equals(status)){
                    onCount++;
                }
            }
        }

//        for(int k=0;k<projectArray.size();k++){
//            JSONObject jsonObject = projectArray.getJSONObject(k);
//            if( k == 0){
//                jsonObject.put("longitude","122.259722");
//                jsonObject.put("latitude","43.687401");
//            }else if(k == 1){
//                jsonObject.put("longitude","122.20453");
//                jsonObject.put("latitude","43.63982");
//            }else if(k == 2){
//                jsonObject.put("longitude","122.175784");
//                jsonObject.put("latitude","43.724938");
//            }else if(k == 3){
//                jsonObject.put("longitude","122.272729");
//                jsonObject.put("latitude","43.691469");
//            }else if(k == 4){
//                jsonObject.put("longitude","122.22228");
//                jsonObject.put("latitude","43.676709");
//            }
//        }

        projectJSONObject.put("vDeviceItemsNum",serialNumberSetTotal.size());
        //供暖站个数
        projectJSONObject.put("heatNum",projectArray.size());
        //获取告警的项目数量
        int alarmProjectCount = getProjectAlarmCount(token);
        int projectCount = projectArray.size();
        projectJSONObject.put("alarmProjectCount",alarmProjectCount);//报警数量
        projectJSONObject.put("nomalProjectCount",projectCount-alarmProjectCount);//正常数
        projectJSONObject.put("runCount",onCount);//运行总数
        projectJSONObject.put("standbyCount",projectCount-onCount);//待机总数
        int onlineCount = getAgentListInfo(token,serialNumberSetTotal);
        projectJSONObject.put("onlineCount",onlineCount);
        projectJSONObject.put("offlineCount",projectCount-onlineCount);
        sumProjectInfo(projectJSONObject);
        System.out.println(projectJSONObject.toJSONString());
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

    /**
     * 数据概览属性求和
     * @param projectJSONObject
     */
    private void sumProjectInfo(JSONObject projectJSONObject){
        JSONArray projectArray = projectJSONObject.getJSONArray("data");
        //数据概览数据求和
        double totalEnergyStorage = 0;
        double totalEnergyCapacity = 0;
        double totalHeatArea = 0;

        double totalCO2 = 0;
        double totalCoalSave = 0;
        double totalGreenSpace = 0;
        //绿色收纳求和
        double totaltoday = 0;
        double totalmonth = 0;
        double totalyear = 0;
        for(Object object : projectArray){
            if(object instanceof JSONObject){
                JSONObject jsonObject = (JSONObject)object;
                System.out.println(jsonObject.toJSONString());
                try{
                    if(jsonObject.get("EnergyStorage") != null
                            && StringUtils.isNotBlank(jsonObject.get("EnergyStorage").toString())){
                        totalEnergyStorage = totalEnergyStorage + Double.parseDouble(jsonObject.get("EnergyStorage").toString());
                    }
                    if(jsonObject.get("EnergyCapacity") != null
                            && StringUtils.isNotBlank(jsonObject.get("EnergyCapacity").toString())){
                        totalEnergyCapacity = totalEnergyCapacity + Double.parseDouble(jsonObject.get("EnergyCapacity").toString());
                    }
                    if(jsonObject.get("HeatArea") != null
                            && StringUtils.isNotBlank(jsonObject.get("HeatArea").toString())){
                        totalHeatArea = totalHeatArea + Double.parseDouble(jsonObject.get("HeatArea").toString());
                    }
                    if(jsonObject.get("CCERS") != null
                            && StringUtils.isNotBlank(jsonObject.get("CCERS").toString())){
                        totalCO2 = totalCO2 + Double.parseDouble(jsonObject.get("CCERS").toString());
                    }
                    if(jsonObject.get("CoalSave") != null
                            && StringUtils.isNotBlank(jsonObject.get("CoalSave").toString())){
                        totalCoalSave = totalCoalSave + Double.parseDouble(jsonObject.get("CoalSave").toString());
                    }
                    if(jsonObject.get("GreenSpace") != null
                            && StringUtils.isNotBlank(jsonObject.get("GreenSpace").toString())){
                        totalGreenSpace = totalGreenSpace + Double.parseDouble(jsonObject.get("GreenSpace").toString());
                    }
                    if(jsonObject.getJSONObject("greenView") != null){
                        JSONObject greenObject = jsonObject.getJSONObject("greenView");
                        if(greenObject.get("today") != null
                                && StringUtils.isNotBlank(greenObject.get("today").toString())){
                            totaltoday = totaltoday + Double.parseDouble(greenObject.get("today").toString());
                        }
                        if(greenObject.get("month") != null
                                && StringUtils.isNotBlank(greenObject.get("month").toString())){
                            totalmonth = totalmonth + Double.parseDouble(greenObject.get("month").toString());
                        }
                        if(greenObject.get("year") != null
                                && StringUtils.isNotBlank(greenObject.get("year").toString())){
                            totalyear = totalyear + Double.parseDouble(greenObject.get("year").toString());
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
        projectJSONObject.put("totalEnergyStorage",totalEnergyStorage);
        projectJSONObject.put("totalEnergyCapacity",totalEnergyCapacity);
        projectJSONObject.put("totalHeatArea",totalHeatArea);
        projectJSONObject.put("totaltoday",totaltoday);
        projectJSONObject.put("totalmonth",totalmonth);
        projectJSONObject.put("totalyear",totalyear);

        projectJSONObject.put("totalCO2",totalCO2);
        projectJSONObject.put("totalCoalSave",totalCoalSave);
        projectJSONObject.put("totalGreenSpace",totalGreenSpace);
    }

    /**
     * 获取指定的数据项的实时数据
     * @param result
     * @param alias
     * @return
     */
    public String getProjectItemData(String result,String alias){
        if(StringUtils.isNotBlank(result)){
            JSONObject resultJSONObject = JSONObject.parseObject(result);
            JSONArray dataArray = resultJSONObject.getJSONArray("data");
            for(Object object : dataArray){
                if(object instanceof JSONObject){
                    JSONObject jsonObject = (JSONObject)object;
                    if(alias.equals(jsonObject.getString("alias"))){
                        if(org.apache.commons.lang3.StringUtils.isNotBlank(jsonObject.getString("val"))){
                            return jsonObject.getString("val");
                        }
                        return "0";
                    }
                }
            }
        }
        return "0";
    }

    public JSONObject getProjectDeviceInfo(String result,String alias){
        JSONObject resultDataObject = new JSONObject();
        if(StringUtils.isNotBlank(result)){
            JSONObject resultJSONObject = JSONObject.parseObject(result);
            JSONArray dataArray = resultJSONObject.getJSONArray("data");
            for(Object object : dataArray){
                if(object instanceof JSONObject){
                    JSONObject jsonObject = (JSONObject)object;
                    if(alias.equals(jsonObject.getString("alias"))){
                        resultDataObject.put("deviceId",jsonObject.getString("devid"));
                        resultDataObject.put("itemId",jsonObject.getString("itemid"));
                    }
                }
            }
        }
        return resultDataObject;
    }

    public String getVdevicesItems(String token,String projectId){
        String url = String.format("%s/project/getVdevicesItems?token=%s&projectId=%s",api_host,token,projectId);
        return HttpUtil.get(url);
    }

    /**
     * 获取数据项历史数据
     * @param token
     * @param deviceid
     * @param dataitemid
     * @param stime
     * @param etime
     * @param limit
     * @return
     */
    public String getProjectHistoryData(String token,String deviceid,String dataitemid,Long stime,Long etime,int limit){
        String url = String.format("%s/historydata?token=%s&hash=test&deviceid=%s&dataitemid=%s&stime=%s&etime=%s&limit=%s",api_host,token,deviceid,dataitemid,stime,etime,limit);
        return HttpUtil.get(url);
    }

    /**
     * 网关状态信息
     */
    public int getAgentListInfo(String token, HashSet<String> serialNumberSetTotal){
        List<Long> agentIds = new ArrayList<Long>();
        for(String serialNumber : serialNumberSetTotal){
            agentIds.add(Long.parseLong(serialNumber));
        }
        String url = String.format("%s/agentList/condition",api_host);
        JSONObject request = new JSONObject();
        request.put("token",token);
        request.put("hash","test");
        request.put("agentIds",agentIds);
        String result =  HttpUtil.post(url,request.toJSONString());
        int onlineCount = 0;
        if(StringUtils.isNotBlank(result)){
            JSONObject resultJSONObject = JSONObject.parseObject(result);
            JSONArray dataArray = resultJSONObject.getJSONArray("data");
            for(Object object : dataArray){
                if(object instanceof JSONObject){
                    JSONObject agentInfoObject = (JSONObject)object;
                    if(1 == agentInfoObject.getInteger("condition")){
                        onlineCount++;
                    }
                }
            }
        }
        return onlineCount;
    }

    /**
     * 获取告警信息
     */
    public String getAlarmData(String token,long startTime,long endTime){
        String url = String.format("%s/alarm/projectCurrentAlarmCount?token=%s&startTime=%s&endTime=%s",api_host,token,startTime,endTime);
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
    public String initDataOne(String token,Integer projectIdParam) throws ParseException {
        String projectList = getProjectsList(token);
        JSONObject projectData = JSONObject.parseObject(projectList);
        JSONArray projectArray = projectData.getJSONArray("data");
        JSONObject project = new JSONObject();
        if(projectIdParam == null){
            project = projectArray.getJSONObject(0);
        }else{
            for(Object object : projectArray){
                if(object instanceof JSONObject){
                    JSONObject jsonObject = (JSONObject)object;
                    if(projectIdParam == jsonObject.getInteger("id")){
                        project = jsonObject;
                        break;
                    }
                }
            }
        }

        String projectId = project.getString("id");
        //getProjectCurrentItemData(token,projectId);

        //获取数据概览信息
        String infoString = getProjectInfo(token,projectId);
        JSONObject infoJSONObject = JSONObject.parseObject(infoString);
        JSONArray infoArray = infoJSONObject.getJSONArray("data");
        JSONObject dataView = project.getJSONObject("dataView");
        for(Object object : infoArray){
            if(object instanceof JSONObject){
                JSONObject jsonObject = (JSONObject)object;
                if("EnergyStorage".equals(jsonObject.getString("name"))){
                    if(StringUtils.isNotBlank(jsonObject.getString("value"))){
                        dataView.put("EnergyStorage",jsonObject.getString("value"));//储能容量
                    }
                }else if("EnergyCapacity".equals(jsonObject.getString("name"))){
                    if(StringUtils.isNotBlank(jsonObject.getString("value"))){
                        dataView.put("EnergyCapacity",jsonObject.getString("value"));//储能功率
                    }
                }else if("HeatArea".equals(jsonObject.getString("name"))){
                    if(StringUtils.isNotBlank(jsonObject.getString("value"))){
                        dataView.put("HeatArea",jsonObject.getString("value"));//供暖面积
                    }
                }else if("CCERS".equals(jsonObject.getString("name"))){
                    if(StringUtils.isNotBlank(jsonObject.getString("value"))){
                        dataView.put("CCERS",jsonObject.getString("value"));//CO2减排量
                    }
                }else if("CoalSave".equals(jsonObject.getString("name"))){
                    if(StringUtils.isNotBlank(jsonObject.getString("value"))){
                        dataView.put("CoalSave",jsonObject.getString("value"));//标煤节约量
                    }
                }else if("GreenSpace".equals(jsonObject.getString("name"))){
                    if(StringUtils.isNotBlank(jsonObject.getString("value"))){
                        dataView.put("GreenSpace",jsonObject.getString("value"));//绿化贡献面积
                    }
                }
            }
        }
        project.put("dataView",dataView);

        //获取监控中心数据
        JSONObject monitorView = project.getJSONObject("monitorView");
        String monitorInfo = getProjectCurrentItemData(token,projectId);
        JSONObject monitorJSONObject = JSONObject.parseObject(monitorInfo);
        JSONArray monitorArray = monitorJSONObject.getJSONArray("data");
        for(Object object : monitorArray){
            if(object instanceof JSONObject){
                JSONObject jsonObject = (JSONObject)object;
                if("室内温度".equals(jsonObject.getString("alias"))){
                    if(org.apache.commons.lang3.StringUtils.isNotBlank(jsonObject.getString("val"))){
                        monitorView.put("oottemp",jsonObject.getString("val"));
                    }
                }else if("室外温度".equals(jsonObject.getString("alias"))){
                    if(org.apache.commons.lang3.StringUtils.isNotBlank(jsonObject.getString("val"))){
                        monitorView.put("W_TEM",jsonObject.getString("val"));
                    }
                }else if("出水温度".equals(jsonObject.getString("alias"))){
                    if(org.apache.commons.lang3.StringUtils.isNotBlank(jsonObject.getString("val"))){
                        monitorView.put("S_TEM",jsonObject.getString("val"));
                    }
                }else if("回水温度".equals(jsonObject.getString("alias"))){
                    if(org.apache.commons.lang3.StringUtils.isNotBlank(jsonObject.getString("val"))){
                        monitorView.put("H_TEM",jsonObject.getString("val"));
                    }
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
                if("EnergyStorage".equals(jsonObject.getString("name"))){
                    if(jsonObject.getString("value") != null
                        && !"".equals(jsonObject.getString("value"))){
                        project.put("EnergyStorage",jsonObject.getString("value"));//储能容量
                    }else{
                        project.put("EnergyStorage","");//储能容量
                    }
                }else if("EnergyCapacity".equals(jsonObject.getString("name"))){
                    if(jsonObject.getString("value") != null
                            && !"".equals(jsonObject.getString("value"))){
                        project.put("EnergyCapacity",jsonObject.getString("value"));//储能功率
                    }else{
                        project.put("EnergyCapacity","");//储能功率
                    }
                }else if("HeatArea".equals(jsonObject.getString("name"))){
                    if(jsonObject.getString("value") != null
                            && !"".equals(jsonObject.getString("value"))){
                        project.put("HeatArea",jsonObject.getString("value"));//供暖面积
                    }else{
                        project.put("HeatArea","");//供暖面积
                    }
                }else if("CCERS".equals(jsonObject.getString("name"))){
                    if(jsonObject.getString("value") != null
                            && !"".equals(jsonObject.getString("value"))){
                        project.put("CCERS",jsonObject.getString("value"));//CO2减排量
                    }else{
                        project.put("CCERS","");//CO2减排量
                    }
                }else if("CoalSave".equals(jsonObject.getString("name"))){
                    if(jsonObject.getString("value") != null
                            && !"".equals(jsonObject.getString("value"))){
                        project.put("CoalSave",jsonObject.getString("value"));//标煤节约量
                    }else{
                        project.put("CoalSave","");//标煤节约量
                    }
                }else if("GreenSpace".equals(jsonObject.getString("name"))){
                    if(jsonObject.getString("value") != null
                            && !"".equals(jsonObject.getString("value"))){
                        project.put("GreenSpace",jsonObject.getString("value"));//绿化贡献面积
                    }else{
                        project.put("GreenSpace","");//绿化贡献面积
                    }
                }
            }
        }
    }

    /**
     * 处理项目网关信息
     */
    public HashSet<String> dealVdevicesItems(String token,JSONObject project){
        String projectId = project.getString("id");
        //获取数据概览信息
        String result = getVdevicesItems(token,projectId);
        JSONObject resultJSONObject = JSONObject.parseObject(result);
        JSONArray dataArray = resultJSONObject.getJSONArray("data");
        HashSet<String> serialNumberSet = new HashSet<String>();
        for(Object dataObject : dataArray){
            if(dataObject instanceof JSONObject){
                JSONObject jsonObject = (JSONObject)dataObject;
                String serialNumber = jsonObject.getString("serialNumber");
                if(serialNumber.substring(0,2).contains("14")){
                    project.put("serialNumber",serialNumber);
                    serialNumberSet.add(serialNumber);
                }
            }
        }
        if(project.get("serialNumber") == null){
            project.put("serialNumber","");
        }
        return serialNumberSet;
    }

    /**
     * 获取所有项目的告警数量
     */
    public int getProjectAlarmCount(String token){
        Date date = new Date();
        Long endTime = date.getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, -24);
        Date bDate = calendar.getTime();
        Long startTime = bDate.getTime();
        String result = getAlarmData(token,startTime,endTime);
        if(StringUtils.isNotBlank(result)){
            JSONObject jsonObject = JSON.parseObject(result);
            JSONArray dataArray = jsonObject.getJSONArray("data");
            return dataArray.size();
        }
        return 0;
    }

    public String getTodayGreenData(String token,JSONObject deviceObject) throws ParseException {
        if(org.apache.commons.lang3.StringUtils.isBlank(deviceObject.getString("deviceId")) ||
                org.apache.commons.lang3.StringUtils.isBlank(deviceObject.getString("itemId"))){
            return "0";
        }

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTime = sdf.format(date) +" 00:00:00";
        String endTime = sdf.format(date) +" 00:10:00";
        Date startDate = sdf2.parse(startTime);
        Date endDate = sdf2.parse(endTime);
        //单月第一天的电量
        String result = getProjectHistoryData(token,deviceObject.getString("deviceId"),deviceObject.getString("itemId"),startDate.getTime(),endDate.getTime(),1);
        if(StringUtils.isNotBlank(result)){
            JSONObject jsonObject = JSON.parseObject(result);
            JSONArray dataArray = jsonObject.getJSONArray("data");
            if(dataArray != null && !dataArray.isEmpty()){
                JSONObject jsonObject1 = dataArray.getJSONObject(0);
                return jsonObject1.getString("val");
            }
            return "0";
        }
        return "0";
    }

    public String getMonthGreenData(String token,JSONObject deviceObject) throws ParseException {
        if(org.apache.commons.lang3.StringUtils.isBlank(deviceObject.getString("deviceId")) ||
                org.apache.commons.lang3.StringUtils.isBlank(deviceObject.getString("itemId"))){
            return "0";
        }
        Calendar calendar = Calendar.getInstance();
        calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date date = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTime = sdf.format(date) +" 00:00:00";
        String endTime = sdf.format(date) +" 00:10:00";
        Date startDate = sdf2.parse(startTime);
        Date endDate = sdf2.parse(endTime);
        //当月第一天的电量
        String result = getProjectHistoryData(token,deviceObject.getString("deviceId"),deviceObject.getString("itemId"),startDate.getTime(),endDate.getTime(),1);
        if(StringUtils.isNotBlank(result)){
            JSONObject jsonObject = JSON.parseObject(result);
            JSONArray dataArray = jsonObject.getJSONArray("data");
            if(dataArray != null && !dataArray.isEmpty()){
                JSONObject jsonObject1 = dataArray.getJSONObject(0);
                return jsonObject1.getString("val");
            }
            return "0";
        }
        return "0";
    }

    public String getYearGreenData(String token,JSONObject deviceObject) throws ParseException {
        if(org.apache.commons.lang3.StringUtils.isBlank(deviceObject.getString("deviceId")) ||
                org.apache.commons.lang3.StringUtils.isBlank(deviceObject.getString("itemId"))){
            return "0";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTime = sdf.format(date) +" 00:00:00";
        String endTime = sdf.format(date) +" 00:10:00";
        Date startDate = sdf2.parse(startTime);
        Date endDate = sdf2.parse(endTime);
        //当年第一天的电量
        String result = getProjectHistoryData(token,deviceObject.getString("deviceId"),deviceObject.getString("itemId"),startDate.getTime(),endDate.getTime(),1);
        if(StringUtils.isNotBlank(result)){
            JSONObject jsonObject = JSON.parseObject(result);
            JSONArray dataArray = jsonObject.getJSONArray("data");
            if(dataArray != null && !dataArray.isEmpty()){
                JSONObject jsonObject1 = dataArray.getJSONObject(0);
                return jsonObject1.getString("val");
            }
            return "0";
        }
        return "0";
    }

    private void setDeafultVal(JSONObject projectJsonObject){
        //获取监控中心数据
        JSONObject monitorView = new JSONObject();
        monitorView.put("oottemp","0");
        monitorView.put("W_TEM","0");
        monitorView.put("S_TEM","0");
        monitorView.put("H_TEM","0");
        projectJsonObject.put("monitorView",monitorView);
        JSONObject greenView = new JSONObject();
        greenView.put("today","");
        greenView.put("month","");
        greenView.put("year","");
        projectJsonObject.put("greenView",greenView);
        JSONObject dataView = new JSONObject();
        dataView.put("EnergyStorage","");//储能容量
        dataView.put("EnergyCapacity","");//储能功率
        dataView.put("HeatArea","");//供暖面积
        dataView.put("CCERS","");//CO2减排量
        dataView.put("CoalSave","");//标煤节约量
        dataView.put("GreenSpace","");//绿化贡献面积
        projectJsonObject.put("dataView",dataView);
    }

}
