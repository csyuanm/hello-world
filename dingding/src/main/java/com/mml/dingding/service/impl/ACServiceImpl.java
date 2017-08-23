package com.mml.dingding.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mml.dingding.common.Constants;
import com.mml.dingding.model.ACRequest;
import com.mml.dingding.model.ACResponse;
import com.mml.dingding.service.ACService;

@Service
public class ACServiceImpl implements ACService {

    private static Logger LOGGER = Logger.getLogger(ACServiceImpl.class);

    //    call the third party api, final call
    @Override
    public ACResponse addContact(ACRequest acRequest) {
        ACResponse response = null;
        try {
            response = getRespons(acRequest);
        } catch (Exception e) {
            LOGGER.error("获取回复失败！");
            e.printStackTrace();
        }

        return response;
    }


    public static String getAccessToken() throws Exception {
        LOGGER.info("获取access_token中...");

        HttpResponse<String> response =
                Unirest.get("https://oapi.dingtalk.com/gettoken?corpid=" + Constants.CORP_ID + "&corpsecret=" + Constants.SECRET_ID).asString();

        LOGGER.info("返回结果:" + response.getBody());

        JSONObject jsonObject = JSONObject.parseObject(response.getBody());
        if (jsonObject.getInteger("errcode") != 0) {
            throw new Exception("Get access token error!");
        }
        return jsonObject.getString("access_token");
    }

    public static ACResponse getRespons(ACRequest acRequest) throws Exception {
        String accessToken = "";
        try {
            accessToken = getAccessToken();
        } catch (Exception e) {
            LOGGER.error("获取access token出错！");
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date today = new Date();
        String url =
                Constants.DINGTALK_REQUEST_BASE_URL +
                        "?method=dingtalk.corp.ext.add&v=2.0&format=json&session" + accessToken + "&timestamp=" + sdf.format(today);
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(acRequest));
        HttpResponse<String> response = Unirest.post(url).queryString(jsonObject).asString();
        LOGGER.info("result: ");
        LOGGER.info(response.getBody());
        LOGGER.info(response.getStatus());
        ACResponse acResponse = new ACResponse();
        return acResponse;
    }
}
