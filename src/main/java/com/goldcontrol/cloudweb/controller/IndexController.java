package com.goldcontrol.cloudweb.controller;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class IndexController {

    @RequestMapping("index")
    public String index(Model model,String param){
        JSONArray jsonArray = new JSONArray();
        JSONObject data = new JSONObject();
        data.put("ln",122.209416);
        data.put("la",43.625412);

        JSONObject data1 = new JSONObject();
        data1.put("ln",122.257422);
        data1.put("la",43.688027);
        jsonArray.add(data);
        jsonArray.add(data1);
        model.addAttribute("dataArray",jsonArray.toJSONString());
        return "html/index";
    }

    @RequestMapping("hello")
    public String hello(Model model,String param){
        return "html/hello";
    }

}
