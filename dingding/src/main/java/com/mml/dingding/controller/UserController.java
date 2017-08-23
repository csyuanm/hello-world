package com.mml.dingding.controller;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mml.dingding.common.Constants;
import com.mml.dingding.common.DingDingAuth;
import com.mml.dingding.model.DingUserBean;
import com.mml.dingding.service.UserService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired UserService userService;
	
	/**
	 * test POST
	 * @return
	 */
	@RequestMapping("testPost")
	public String addDingUser(){
		String str = "";
		String token = Constants.getAccessToken();
		
		//https://oapi.dingtalk.com/message/send?access_token=
		//"{\"msgtype\":\"text\",\"text\":{\"content\":\"hello\"}}"
		HttpClient httpClient = new DefaultHttpClient();  
		HttpResponse response = null;
		HttpEntity httpEntity = null;
		HttpPost httppost = null;
	    String url="https://oapi.dingtalk.com/message/send?access_token="+token;  
	    try {  
	    	String[] userid = {"manager4417"};
			//String url = "https://eco.taobao.com/router/rest?method=dingtalk.corp.message.corpconversation.asyncsend&v=2.0&format=json&session="+token;
			JSONObject obj = new JSONObject();
			JSONObject content = new JSONObject();
			content.put("content", "生日祝福语");
			obj.put("agentid", 115257477);
			obj.put("touser", "manager4417");
			obj.put("msgtype", "text");
			obj.put("text", content);
			//obj.put("msgcontent",content);
	         StringEntity js = new StringEntity(obj.toString(), "utf-8");
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
	            	httppost.releaseConnection(); 
	            }
	            if(httpClient!=null){
	                ((Closeable) httpClient).close();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
		return str;
		
		//DingDingAuth.dingPostAll(url, obj);
		
	}
	
	/**
	 * 向钉钉添加成员
	 * @return
	 */
	@RequestMapping("addUser")
	public String addUser(String[] department_id){
		String str = null;
		String corpId = "ding6cbc9a566e55edd035c2f4657eb6378f";
	    String CorpSecret = "PvypyCr3cLRaa9RN2BiqVDIrIDytp-GHiZpD6M96762mHwQBoX_Fbr71cE3otoQt";
	    String access_token = Constants.getAccessToken();
		HttpClient httpClient = new DefaultHttpClient();  
		HttpResponse response = null;
		HttpEntity httpEntity = null;
		HttpPost httppost = null;
	    String url="https://oapi.dingtalk.com/user/create?access_token="+access_token;  
	    try {  
	         JSONObject jobj = new JSONObject();  
	         jobj.put("department", department_id); 
	         jobj.put("name", "12345");
	         jobj.put("mobile", "1309849876");
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

	/**
	 * 获取部门员工详细信息并保存
	 * @param department_id
	 * @return
	 */
	@RequestMapping("addUserList")
	public String getUserList(String department_id){
		
		String corpId = "ding7d8395159ab63c53";
        String CorpSecret = "9EPQ7aLz4qYnazjcTnnmckU9RIYsdB_jenmXptoWHfAZQVOIVEvC2lr45_occvBx";
        //String access_token = getToken(corpId, CorpSecret);
        String access_token = Constants.getAccessToken();
		return userService.getDetail(access_token, department_id);
	}

	/**
	 * 根据id获取个人信息并保存
	 */
	@RequestMapping("get")
	public String getUserById(String id){
		
		String corpId = "ding7d8395159ab63c53";
        String CorpSecret = "9EPQ7aLz4qYnazjcTnnmckU9RIYsdB_jenmXptoWHfAZQVOIVEvC2lr45_occvBx";
        String userid = "12192641651216800"; 
       // String access_token = getToken(corpId, CorpSecret);
        String access_token = Constants.getAccessToken();
		return userService.getUser(access_token, id);
	}
	
	/**
	 * 获取部门员工简单信息
	 * @return
	 */
    @RequestMapping("/simplelist")
    public String getDepartmentUser(String department_id){
        
        String corpId = "ding7d8395159ab63c53";
        String CorpSecret = "9EPQ7aLz4qYnazjcTnnmckU9RIYsdB_jenmXptoWHfAZQVOIVEvC2lr45_occvBx";
        String access_token = Constants.getToken(corpId, CorpSecret);
       // String url="https://oapi.dingtalk.com/gettoken";
        HttpEntity httpEntity = null;
        String str = null;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpGet httpGet = null;
        try {
            httpClient = HttpClients.createDefault();
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();     
            URI uri = new URIBuilder()
            			.setScheme("https")
            			.setHost("oapi.dingtalk.com")
            			.setPath("/user/simplelist")
            			.setParameter("access_token", access_token)
            			.setParameter("department_id", department_id)
            			.build();
            httpGet = new HttpGet(uri);
            System.out.println("user/list。。。"+httpGet.getURI());
            //接收数据
            response = httpClient.execute(httpGet);
            httpEntity = response.getEntity();
            httpGet.setConfig(requestConfig);
            str = EntityUtils.toString(httpEntity,"utf-8");
           // JSONObject jsonObject=JSONObject.fromObject(str);
           // String ret = (String) jsonObject.get("access_token");
            System.out.println("user/list..."+str);
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
        return "该部门下的员工为："+str;
    }
    
    /**
	 * 获取部门员工详细信息
	 * @return
	 */
    @RequestMapping("/detaillist")
    public String getDepartmentDetailUser(String department_id){
        
        String corpId = "ding7d8395159ab63c53";
        String CorpSecret = "9EPQ7aLz4qYnazjcTnnmckU9RIYsdB_jenmXptoWHfAZQVOIVEvC2lr45_occvBx";
        String access_token = Constants.getAccessToken();
        HttpEntity httpEntity = null;
        String str = null;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpGet httpGet = null;
        try {
            httpClient = HttpClients.createDefault();
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();     
            URI uri = new URIBuilder()
            			.setScheme("https")
            			.setHost("oapi.dingtalk.com")
            			.setPath("/user/list")
            			.setParameter("access_token", access_token)
            			.setParameter("department_id", department_id)
            			.build();
            httpGet = new HttpGet(uri);
            System.out.println("user/list。。。"+httpGet.getURI());
            //接收数据
            response = httpClient.execute(httpGet);
            httpEntity = response.getEntity();
            httpGet.setConfig(requestConfig);
            str = EntityUtils.toString(httpEntity,"utf-8");
           // JSONObject jsonObject=JSONObject.fromObject(str);
           // String ret = (String) jsonObject.get("access_token");
            System.out.println("user/list..."+str);
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
        return "该部门下的员工详细信息为："+str;
    }

	
	
	@RequestMapping("test2")
	public DingUserBean get(){
		return userService.get();
	}
	
    
   /**
    *修改某个员工信息
    *只测试了能不能改，参数没设定
    * @return
    */
	@RequestMapping("/update")
	public String update(){
		String corpId = "ding7d8395159ab63c53";
        String CorpSecret = "9EPQ7aLz4qYnazjcTnnmckU9RIYsdB_jenmXptoWHfAZQVOIVEvC2lr45_occvBx";
        String access_token = Constants.getAccessToken();
        System.out.println("这里获取不到？？"+access_token);
		HttpClient httpClient = new DefaultHttpClient();  
		HttpResponse response = null;
		HttpEntity httpEntity = null;
		HttpPost httppost = null;
        String smsUrl="https://oapi.dingtalk.com/user/update?access_token=3baf22f9c5603522953dff77bf1ea5ed";  
        String str = null;
        try {  
        	 httppost = new HttpPost(smsUrl);
        	 List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();  
             JSONObject jobj = new JSONObject();  
           //  jobj.put("access_token", access_token);  
             jobj.put("userid", "05245112311216800"); 
             jobj.put("name", "小lan");
                nameValuePairs.add(new BasicNameValuePair("msg", jobj.toString()));//这个好像不是json格式
                httppost.setHeader("Content-Type", "application/json");
                httppost.addHeader("Authorization", "Basic YWRtaW46");
                StringEntity s = new StringEntity(jobj.toString(), "utf-8");
                s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
                System.out.println("StringEntity..."+s);
        		System.out.println("nameValuePairs..."+nameValuePairs);
        		httppost = new HttpPost(smsUrl);
        		httppost.setEntity(s); 
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
	
	
	@RequestMapping("test")
	public void test(){
		// 取得HttpClient  
        HttpClient httpClient = new DefaultHttpClient();
		// HttpPost连接对象  
        HttpPost httpPost = new HttpPost("https://oapi.dingtalk.com/user/update?access_token=87313a0b40923e459209ddc930acf648");  
        // 使用NameValuePair来保存要传递的Post参数  
        List<NameValuePair> params = new ArrayList<NameValuePair>();  
        // 添加要传递的参数  
       // params.add(new BasicNameValuePair("access_token", "87313a0b40923e459209ddc930acf648"));
        params.add(new BasicNameValuePair("userid", "05245112311216800"));
        params.add(new BasicNameValuePair("name", "tmd"));
       // params.add(new BasicNameValuePair("access_token", "87313a0b40923e459209ddc930acf648"));
        try {  
            // 设置字符集  
            HttpEntity httpentity = new UrlEncodedFormEntity(params, HTTP.UTF_8);  
            // 请求httpPost  
            httpPost.setEntity(httpentity);  
            // 取得HttpResponse  
            System.out.println(httpPost.getURI());
            HttpResponse httpResponse = httpClient.execute(httpPost);  
            if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {  
                String strResult = EntityUtils.toString(httpResponse  
                        .getEntity());  
                System.out.println(strResult);  
            }else {  
                System.out.println("请求错误");  
            }  
        }  
        catch(Exception e) {  
            e.printStackTrace();  
        }finally{
        	try {
                if(httpPost!=null){
                	httpPost.releaseConnection(); // 关闭method 的 httpclient连接
                }
                if(httpClient!=null){
                    ((Closeable) httpClient).close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}

/**
 * test
 * @return
 */
@RequestMapping("post")
public String addDingUser2(){
	String str = null;
    String access_token = Constants.getAccessToken();
	HttpClient httpClient = new DefaultHttpClient();  
	HttpResponse response = null;
	HttpEntity httpEntity = null;
	HttpPost httppost = null;
    String url="https://oapi.dingtalk.com/attendance/listRecord?access_token="+access_token;  
    try {  
    	String[] s = {"manager4417","0432314435774889"};
		//list.add("manager4417");
		//加了时间报错。。。
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date nowTime = new Date();
		JSONArray jsonArray = new JSONArray();  //讲数组格式保存在json中
		for(int i =0; i<s.length; i++){
			 jsonArray.add(s[i]);
		}
		System.out.println("...........ssssssss"+jsonArray);
		//JSONObject jo = {};
		JSONObject obj3 = new JSONObject();
		obj3.put("userIds", jsonArray);
		obj3.put("checkDateFrom", "2017-08-08 12:00:00");
		obj3.put("checkDateTo", "2017-08-09 23:00:00");
     //    httppost.setHeader("Content-Type", "application/json");
      //   httppost.addHeader("Authorization", "Basic YWRtaW46");
         StringEntity js = new StringEntity(obj3.toString(), "utf-8");
         js.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
    	 httppost = new HttpPost(url);
    	 httppost.setEntity(js); 
         System.out.println(httppost.getURI());
         response = httpClient.execute(httppost);  
         httpEntity = response.getEntity();
         str = EntityUtils.toString(httpEntity,"utf-8");
       //  JSONObject jsonObject=JSONObject.fromObject(str);
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
/*try {  

List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();  
JSONObject jobj = new JSONObject();  
jobj.put("corpId", corpId);  
jobj.put("CorpSecret", CorpSecret);  */


/*            PostMethod method = new PostMethod("http://192.168.111.128/bak/login_post.php");
//表单域的值,既post传入的key=value
NameValuePair[] date = { new NameValuePair("username","admin"),new NameValuePair("password","123457")};
//method使用表单阈值
method.setRequestBody(date);*/
/*  for (String pKey : jobj.keySet()) {
if(!"".equals(ps)){
    ps = ps + "&";
}
ps = pKey+"="+jobj.get(pKey);
}
if(!"".equals(ps)){
url = url + "?" + ps;
}*/
//  httpGet.addHeader("Content-type", "application/x-www-form-urlencoded");  
// httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8")); 
// URI uri = URIUtils.createURI("http", "oapi.dingtalk.com", -1, "/user", URLEncodedUtils.format(nameValuePairs, "UTF-8",null), null);
//Map<String, String> params;
//List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();  
//JSONObject jobj = new JSONObject();  
//jobj.put("corpId", corpId);  
//jobj.put("CorpSecret", CorpSecret);
//System.out.println("jobj:"+jobj);
//for (String pKey : jobj.keySet()) {
//	nameValuePairs.add(new BasicNameValuePair(pKey, (String) jobj.get(pKey)));
//}
//httpGet.setParams((HttpParams) jobj);
//
//System.out.println(url);
//httpGet = new HttpGet(url);//发送get请求
//httpGet.setConfig(requestConfig);
//
/*                nameValuePairs.add(new BasicNameValuePair("msg", getStringFromJson(jobj)));  
   httppost.addHeader("Content-type", "application/x-www-form-urlencoded");  
   httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));  
   HttpResponse response = httpClient.execute(httppost);  
   HttpEntity httpEntity = response.getEntity();
   str = EntityUtils.toString(httpEntity,"utf-8");
   JSONObject jsonObject=JSONObject.fromObject(str);
   System.out.println("hellowrld");
   System.out.println("access_token..."+jsonObject.get("access_token"));
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
return str;*/
//List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();  
//JSONObject jobj = new JSONObject();  
//jobj.put("corpId", corpId);  
//jobj.put("CorpSecret", CorpSecret);  
//nameValuePairs.add(new BasicNameValuePair("msg", getStringFromJson(jobj)));
//httpGet.addHeader("Content-type", "application/x-www-form-urlencoded"); 
//((HttpResponse) httpGet).setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
//HttpResponse response = httpClient.execute(httpGet);
