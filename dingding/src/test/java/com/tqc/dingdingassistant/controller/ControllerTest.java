package com.tqc.dingdingassistant.controller;

import com.alibaba.fastjson.JSONObject;
import com.mml.dingding.model.ACRequest;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

public class ControllerTest extends ControllerBaseTest {

    @Test
    public void testHome() throws Exception {
        String url = "/index/ac";

        MvcResult mvcResult = super.perfromMvcGetRequest(url);
        int status = mvcResult.getResponse().getStatus();
        String content = mvcResult.getResponse().getContentAsString();

        System.out.println(content);
        System.out.println(status);
    }

}
