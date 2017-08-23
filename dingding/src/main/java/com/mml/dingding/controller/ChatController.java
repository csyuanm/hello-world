package com.mml.dingding.controller;

import java.io.Closeable;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mml.dingding.common.Constants;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@RestController
@RequestMapping("/chat")
public class ChatController {

    /**
     * 创建会话（创建的是一个群）
     * chat46e9a435b78794aace72608903d0d53a
     * 建群时才需要需要这个借口
     * @return
     */
    @RequestMapping("addChat")
	public String addChat(){
		String str = null;
	    String access_token = Constants.getAccessToken();
		HttpClient httpClient = new DefaultHttpClient();  
		HttpResponse response = null;
		HttpEntity httpEntity = null;
		HttpPost httppost = null;
	    String url="https://oapi.dingtalk.com/chat/create?access_token="+access_token;  
	    try {  
	        JSONObject jobj = new JSONObject();  
	     	Number[] n = {1};
			String[] s = {"manager4417"};
			//list.add("manager4417");
			JSONArray jsonArray = new JSONArray();  //讲数组格式保存在json中
			for(int i =0; i<s.length; i++){
				 jsonArray.add(s[i]);
			}
			jobj.put("name", "测试群会话2");
			jobj.put("owner", "manager4417");
			jobj.put("useridlist", s);
	       //  httppost.setHeader("Content-Type", "application/json");
	         StringEntity js = new StringEntity(jobj.toString(), "utf-8");
	         js.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
	    	 httppost = new HttpPost(url);
	    	 httppost.setEntity(js); 
	         System.out.println(httppost.getURI());
	         response = httpClient.execute(httppost);  
	         httpEntity = response.getEntity();
	         JSONObject obj = JSONObject.fromObject(EntityUtils.toString(httpEntity,"utf-8"));
	         str = obj.getString("chatid");
	         System.out.println(str);
	       //返回结果：chat46e9a435b78794aace72608903d0d53a
	    } catch (ClientProtocolException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } finally{
	        try {
	            if(httppost!=null){
	            	httppost.releaseConnection(); // 关闭method 的 httpclient连接
	            }
	            if(httpClient!=null){
	                ((Closeable) httpClient).close();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
		return str;
	}
    
    
    /**
     * 获取会话
     * @return
     */
    @RequestMapping("getChat")
	public String getChat(){
		String str = null;
	    String access_token = Constants.getAccessToken();
	    String chatId = "chat46e9a435b78794aace72608903d0d53a";
		HttpClient httpClient = new DefaultHttpClient(); 
		HttpResponse response = null;
		HttpEntity httpEntity = null;
		HttpGet httpGet = null;
	    String url="https://oapi.dingtalk.com/chat/get?access_token="+access_token+"&chatid="+chatId;  
	    try {  
	       // httpGet.addHeader("Content-Type", "application/json");
	         httpGet = new HttpGet(url);
	         System.out.println(httpGet.getURI());
	         response = httpClient.execute(httpGet);  
	         httpEntity = response.getEntity();
	         str = EntityUtils.toString(httpEntity,"utf-8");
	         System.out.println(str);
	    } catch (ClientProtocolException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } finally{
	        try {
	            if(httpGet!=null){
	            	httpGet.releaseConnection(); // 关闭method 的 httpclient连接
	            }
	            if(httpClient!=null){
	                ((Closeable) httpClient).close();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
		return str;
	}
    
    
    /**
     * 发送会话
     * @return
     */
    @RequestMapping("sendChat")
	public String sendChat(String content){
		String str = null;
	    String access_token = Constants.getAccessToken();
	    String chatId = "chat46e9a435b78794aace72608903d0d53a";
	    
		HttpClient httpClient = new DefaultHttpClient();  
		HttpResponse response = null;
		HttpEntity httpEntity = null;
		HttpPost httppost = null;
	    String url="https://oapi.dingtalk.com/chat/send?access_token="+access_token;  
	    try {  
	        JSONObject out = new JSONObject();  
	        JSONObject in = new JSONObject();  
	        in.put("content", content);
	        
	        out.put("chatid", chatId);
	        out.put("msgtype", "text");
	        out.put("text", in);
	         StringEntity js = new StringEntity(out.toString(), "utf-8");
	         js.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
	    	 httppost = new HttpPost(url);
	    	 httppost.setEntity(js); 
	         System.out.println(httppost.getURI());
	         response = httpClient.execute(httppost);  
	         httpEntity = response.getEntity();
	         str = EntityUtils.toString(httpEntity,"utf-8");
	         System.out.println(str);
	       //返回结果：{"errmsg":"ok","chatid":"chatd2e1682e4ac7cffeef105492b61ecd78","errcode":0}
	    } catch (ClientProtocolException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } finally{
	        try {
	            if(httppost!=null){
	            	httppost.releaseConnection(); // 关闭method 的 httpclient连接
	            }
	            if(httpClient!=null){
	                ((Closeable) httpClient).close();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
		return str;
	}
    
    /**
     * 修改会话
     * @return
     */
    @RequestMapping("updateChat")
	public String updateChat(){
		String str = null;
	    String access_token = Constants.getAccessToken();
	    String chatId = "chat46e9a435b78794aace72608903d0d53a";
	    
		HttpClient httpClient = new DefaultHttpClient();  
		HttpResponse response = null;
		HttpEntity httpEntity = null;
		HttpPost httppost = null;
	    String url="https://oapi.dingtalk.com/chat/update?access_token="+access_token;  
	    try {  
	        JSONObject jobj = new JSONObject();  
	     	String[] add= {"12192641651216800","05245112311216800"};
	     	String[] del= {"12192641651216800","05245112311216800"};
			jobj.put("name", "测试群会话");
			jobj.put("chatid", chatId);
			jobj.put("owner", "manager4417");
			jobj.put("add_useridlist", "manager4417");
			jobj.put("del_useridlist", "manager4417");
			
	       //  httppost.setHeader("Content-Type", "application/json");
	         StringEntity js = new StringEntity(jobj.toString(), "utf-8");
	         js.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
	    	 httppost = new HttpPost(url);
	    	 httppost.setEntity(js); 
	         System.out.println(httppost.getURI());
	         response = httpClient.execute(httppost);  
	         httpEntity = response.getEntity();
	         str = EntityUtils.toString(httpEntity,"utf-8");
	         System.out.println(str);
	    } catch (ClientProtocolException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } finally{
	        try {
	            if(httppost!=null){
	            	httppost.releaseConnection(); // 关闭method 的 httpclient连接
	            }
	            if(httpClient!=null){
	                ((Closeable) httpClient).close();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
		return str;
	}

}
