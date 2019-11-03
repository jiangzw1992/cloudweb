package com.goldcontrol.cloudweb.controller;

import com.goldcontrol.cloudweb.service.ApiService;
import com.goldcontrol.cloudweb.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("api")
public class ApiController {

    public final String token = "db3720f0-8d56-4aa1-9d2c-e47173ba0fab";

    @Autowired
    ApiService apiService;

    @RequestMapping(value = "/getProjectsList",produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getProjectsList(@RequestParam String token){
        String result = apiService.getProjectsList(token);
        return result;
    }

    /**
     * 数据概览接口
     * @param token
     * @param projectId
     * @return
     */
    @RequestMapping(value = "/getProjectInfo",produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getProjectInfo(@RequestParam String token,@RequestParam String projectId){
        String result = apiService.getProjectInfo(token,projectId);
        return result;
    }

    @RequestMapping(value = "/getProjectCurrentItemData",produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getProjectCurrentItemData(@RequestParam String token,@RequestParam String projectId){
        String result = apiService.getProjectCurrentItemData(token,projectId);
        return result;
    }

    @RequestMapping(value = "/getVdevicesItems",produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getVdevicesItems(@RequestParam String token,@RequestParam String projectId){
        String result = apiService.getVdevicesItems(token,projectId);
        return result;
    }

}
