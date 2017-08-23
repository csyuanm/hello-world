/**
 * HuiXian Inc.
 * Copyright (c) 2004-2017 All Rights Reserved.
 */
package com.tqc.dingdingassistant.controller;

import java.util.Iterator;
import java.util.Map;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.mml.dingding.DingdingApplication;

/**
 * 测试基类
 *
 * @author LiuYi
 * @version $Id: BaseTest.java, v 0.1 2017年6月6日 上午11:53:57 LiuYi Exp $
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = DingdingApplication.class)
@WebMvcTest
public class ControllerBaseTest {

    @Autowired
    protected MockMvc mvc;

    /**
     * 请求mvc
     *
     * @param url
     * @return
     * @throws Exception
     */
    protected MvcResult perfromMvcGetRequest(String url) throws Exception {
        MvcResult mvcResult = mvc
                .perform(MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult;
    }
    /**
     * 根据参数生成请求url
     *
     * @param url
     * @param params
     * @return
     */
    protected String generateRequestUrl(String url, Map<String, Object> params) {
        if (params != null) {
            boolean isFirstParam = true;
            Iterator<String> keyIterators = params.keySet().iterator();
            while (keyIterators.hasNext()) {
                String key = keyIterators.next();
                if (isFirstParam) {
                    url = url + "?" + key + "=" + params.get(key);
                    isFirstParam = false;
                } else {
                    url = url + "&" + key + "=" + params.get(key);
                }
            }
        }
        return url;
    }
}
