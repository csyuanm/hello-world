package com.mml.dingding.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mml.dingding.common.Constants;
import com.mml.dingding.service.DepartmentService;

import net.sf.json.JSONObject;

@RestController
@RequestMapping("department")
public class DepartmentController {
	
	@Autowired 
	DepartmentService departmentService;
	
	@RequestMapping("addDepartment")
	public String getDpartmentList(){
		
		//String token = getAcessToken();

		String corpId = "ding7d8395159ab63c53";
        String CorpSecret = "9EPQ7aLz4qYnazjcTnnmckU9RIYsdB_jenmXptoWHfAZQVOIVEvC2lr45_occvBx";
        String access_token = Constants.getToken(corpId, CorpSecret);
		
		
		return departmentService.addDepartment(access_token);
	}
	
    /**
     * 不用参数的获取token
     * @return
     */
    public static String getAcessToken(){
    	 String corpId = "ding6cbc9a566e55edd035c2f4657eb6378f";
         String CorpSecret = "PvypyCr3cLRaa9RN2BiqVDIrIDytp-GHiZpD6M96762mHwQBoX_Fbr71cE3otoQt";
         HttpEntity httpEntity = null;
         String token = null;
         CloseableHttpClient httpClient = null;
         CloseableHttpResponse response = null;
         HttpGet httpGet = null;
         try {
             httpClient = HttpClients.createDefault();
             RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();     
             URI uri = new URIBuilder()
             			.setScheme("https")
             			.setHost("oapi.dingtalk.com")
             			.setPath("/gettoken")
             			.setParameter("corpid", corpId)
             			.setParameter("corpsecret", CorpSecret)
             			.build();
             httpGet = new HttpGet(uri);
             System.out.println("getListURI...."+httpGet.getURI());
             //接收数据
             response = httpClient.execute(httpGet);
             httpEntity = response.getEntity();
             httpGet.setConfig(requestConfig);
             JSONObject jsonObject=JSONObject.fromObject(EntityUtils.toString(httpEntity,"utf-8"));
             token = (String) jsonObject.get("access_token");//获取json字符串中某个key的值
             System.out.println(token);
         } catch (ClientProtocolException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         } catch (URISyntaxException e) {
         	e.printStackTrace();
         } finally{// 关闭method的 httpclient连接
             try {
                 if(httpGet!=null){
                     httpGet.releaseConnection(); 
                 }
                 if(httpClient!=null){
                     httpClient.close();
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
         return token;
 }
}
