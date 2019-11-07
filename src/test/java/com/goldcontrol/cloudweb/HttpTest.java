package com.goldcontrol.cloudweb;

import com.goldcontrol.cloudweb.controller.ApiController;
import com.goldcontrol.cloudweb.service.ApiService;
import com.goldcontrol.cloudweb.util.HttpUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HttpTest {

    @Autowired
    ApiService apiService;

    @Autowired
    ApiController apiController;

    private static final String token = "db3720f0-8d56-4aa1-9d2c-e47173ba0fab";

    @Test
    public void getProjectsList() throws ParseException {
        String result = apiService.getProjectsList(token);
        System.out.println(result);
    }

    @Test
    public void projectInfo(){
        String result = apiService.getProjectInfo(token,"2");
        System.out.println(result);
    }

    @Test
    public void getProjectCurrentItemData() throws ParseException {
        String result = apiService.getProjectsList(token);
        System.out.println(result);
    }

    @Test
    public void getVdevicesItems() throws ParseException {
        String result = apiService.getProjectsList(token);
        System.out.println(result);
    }

}
