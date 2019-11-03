package com.goldcontrol.cloudweb.service;

import com.goldcontrol.cloudweb.util.HttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ApiService {

    @Value("${api_host}")
    private String api_host;

    public String getProjectsList(String token){
        String url = String.format("%s/project/getProjectsList?token=%s&active=1",api_host,token);
        System.out.println(url);
        return HttpUtil.get(url);
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

}
