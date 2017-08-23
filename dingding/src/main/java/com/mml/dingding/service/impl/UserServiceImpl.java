package com.mml.dingding.service.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mml.dingding.common.DingDingAuth;
import com.mml.dingding.common.NetSuiteAuth;
import com.mml.dingding.common.UserUtil;
import com.mml.dingding.dao.DingUserDao;
import com.mml.dingding.model.DingUserBean;
import com.mml.dingding.service.NetSuiteService;
import com.mml.dingding.service.UserService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired DingUserDao userDao;
	
	@Autowired NetSuiteService nsService;
	
	/**
	 * 获取个人详细信息并保存
	 */
	@Override
	public String getUser(String token, String userid) {
		DingUserBean  user = new DingUserBean();
		String str = null;
		HttpEntity httpEntity = null;
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpGet httpGet = null;
        try {
            httpClient = HttpClients.createDefault();
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();     
            URI uri = new URIBuilder()
            			.setScheme("https")
            			.setHost("oapi.dingtalk.com")
            			.setPath("/user/get")
            			.setParameter("access_token", token)
            			.setParameter("userid", userid)
            			.build();
            httpGet = new HttpGet(uri);
            System.out.println("user/get。。。"+httpGet.getURI());
            //接收数据
            response = httpClient.execute(httpGet);
            httpEntity = response.getEntity();
            httpGet.setConfig(requestConfig);
            str = EntityUtils.toString(httpEntity,"utf-8");
            JSONObject json=JSONObject.fromObject(str);
            //读取json某个key的值，并赋值给实体类
            if(json.has("department")){
            	user.setDepartment(json.getString("department"));
            }
            if(json.has("roles")){
            	user.setRoles(json.getString("roles"));
            }
            if(json.has("userid")){
                user.setUserid(json.getString("userid"));
            }
            //json转化为map 可以直接实现对实体类的对应，[]等特殊字符除外
            Map map = json;
            map.put("department", "");
            map.put("roles", "");
            //[]不知道怎么转化，先加入控制，在进行修改。。
            int count = userDao.checkRepeat(json.getString("mobile"));
        	if(count == 0){
        		userDao.forMap(map);
        	}
            userDao.updateDepapartment(user);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
        	e.printStackTrace();
        } finally{
        	// 关闭method的 httpclient连接
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
        return str;
	}

	@Override
	public DingUserBean get() {
		DingUserBean test = userDao.get();
		System.out.println(test);
		return test;
	}
	
	/**
	 * 批量保存部门成员数据
	 * 问题：1.获取到的钉钉成员数据能否有其他方法转化为本地的实体对象 
	 * 解决办法： json.tobean或者直接用map做映射
	 *     
	 */
	@Override
	public String getDetail(String token, String department_id) {
		DingUserBean  user = new DingUserBean();
		String str = null;
		HttpEntity httpEntity = null;
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
            			.setParameter("access_token", token)
            			.setParameter("department_id", department_id)
            			.build();
            httpGet = new HttpGet(uri);
            System.out.println("user/getdetailsURI"+httpGet.getURI());
            //接收数据
            response = httpClient.execute(httpGet);
            httpEntity = response.getEntity();
            httpGet.setConfig(requestConfig);
            str = EntityUtils.toString(httpEntity,"utf-8");
            System.out.println("直接获取的返回结果是："+str);
          //将Json字符串转为java对象
            JSONObject jsonObject=JSONObject.fromObject(str);
            JSONArray jArray = jsonObject.getJSONArray("userlist");
            System.out.println("json获取userlist..."+jArray);
           // System.out.println("使用toString...结果是一样的说"+jArray.toString());
            if(jArray!=null&&jArray.size()>0){
            	for(int i = 0; i <jArray.size(); i++){
            		Object obj = jArray.get(i);
                	System.out.println("获取第"+(i+1)+"个对象内容..."+obj);
                	JSONObject json = JSONObject.fromObject(obj);//将获取的对象转化为json格式
                	user.setDepartment(json.getString("department"));
                	if(json.has("extattr")){
                		user.setExtattr(json.getString("extattr"));
                	}
                	user.setUserid(json.getString("userid"));
                	Map map = json;
                	map.put("department", "");
                	map.put("extattr", "");
                	int count = userDao.checkRepeat(json.getString("mobile"));
                	if(count == 0){
                		userDao.forMap(map);
                	}
                    userDao.updateDepapartment(user);
                }
            }
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
		return str;
	}

	@Override
	public String addDingUser() {

		nsService.getEmployeeList();

		String result = "";
		String url="https://oapi.dingtalk.com/user/create";
		// post方式提交
		Map searchMap = new HashMap();
		searchMap.put("internalid", "69");
		JSONObject itemObj = JSONObject.fromObject(searchMap);
		
		JSONObject jobj = new JSONObject();  
        jobj.put("department", "1"); 
        jobj.put("name", "小lan");
        jobj.put("mobile", "13310349876");
		
		try {
			result = (DingDingAuth.postAuth(url, jobj.toString()))
					.replaceAll("(\r\n|\r|\n|\n\r)", "");
			// 上传跟踪号至NS
			System.out.println("钉钉...result:" + result);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("添加成员到钉钉发生异常");
		}

		return null;
	}

}
