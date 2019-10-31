package com.goldcontrol.cloudweb;

import com.goldcontrol.cloudweb.controller.ApiController;
import com.goldcontrol.cloudweb.service.ApiService;
import com.goldcontrol.cloudweb.util.HttpUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HttpTest {

    @Autowired
    ApiService apiService;

    @Autowired
    ApiController apiController;

    private static final String token = "67d1d546-b50e-439d-b751-f5d6b842202d";

    @Test
    public void getProjectsList(){
        String result = apiService.getProjectsList(token);
        System.out.println(result);
    }

    @Test
    public void projectInfo(){
        String result = apiService.getProjectInfo(token,"2");
        System.out.println(result);
    }

    @Test
    public void getProjectCurrentItemData(){
        String result = apiService.getProjectsList(token);
        System.out.println(result);
    }

    @Test
    public void getVdevicesItems(){
        String result = apiService.getProjectsList(token);
        System.out.println(result);
    }

}
