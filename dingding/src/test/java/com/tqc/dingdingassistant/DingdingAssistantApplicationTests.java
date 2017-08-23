package com.tqc.dingdingassistant;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mml.dingding.common.Constants;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
public class DingdingAssistantApplicationTests {

	@Test
	public void contextLoads() throws Exception{
		HttpResponse<String> response = Unirest.get(Constants.DINGTALK_REQUEST_BASE_URL).asString();
		System.out.println(response.getBody());
		System.out.println(response.getStatus());
	}

}
