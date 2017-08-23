package com.mml.dingdingcommon;

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

import net.sf.json.JSONObject;

public class Constants {

    public static String CORP_ID = "ding6cbc9a566e55edd035c2f4657eb6378f";
    public static String SECRET_ID = "PvypyCr3cLRaa9RN2BiqVDIrIDytp-GHiZpD6M96762mHwQBoX_Fbr71cE3otoQt";
    public static String DINGTALK_REQUEST_BASE_URL = "https://eco.taobao.com/router/rest";
    
    /**
     * 传入corpId、CorpSecret获取token
     * @param corpId
     * @param CorpSecret
     * @return
     */
    public static String getToken(String corpId,String CorpSecret){
       // String access_token = getToken(corpId, CorpSecret);
        //String url="https://oapi.dingtalk.com/gettoken";
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
            System.out.println("gettokenURI...."+httpGet.getURI());
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
    /**
     * 不用参数的获取token
     * @return
     */
    public static String getAccessToken(){
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
             System.out.println("gettokenURI...."+httpGet.getURI());
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
