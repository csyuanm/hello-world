package com.mml.dingding.service.impl;

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
import org.springframework.stereotype.Service;

import com.mml.dingding.dao.DingDepartmentDao;
import com.mml.dingding.model.DingDepartmentBean;
import com.mml.dingding.service.DepartmentService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class DepartmentServiceImpl implements DepartmentService {
	
	@Autowired 
	DingDepartmentDao departmentDao;
	
	
	/**
	 * 批量添加当前公司所有部门
	 */
	@Override
	public String addDepartment(String token) {
		DingDepartmentBean  department = new DingDepartmentBean();
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
            			.setPath("/department/list")
            			.setParameter("access_token", token)
            			.build();
            httpGet = new HttpGet(uri);
            System.out.println("department/getdetailsURI"+httpGet.getURI());
            //接收数据
            response = httpClient.execute(httpGet);
            httpEntity = response.getEntity();
            httpGet.setConfig(requestConfig);
            str = EntityUtils.toString(httpEntity,"utf-8");
            System.out.println("直接获取的返回结果是："+str);
          //将Json字符串转为java对象
            JSONObject jsonObject=JSONObject.fromObject(str);
            System.out.println("department/getList..."+str);
            System.out.println("返回内容转化为json..."+jsonObject);
            JSONArray jArray = jsonObject.getJSONArray("department");
            System.out.println("json获取departmentlist..."+jArray);
           // System.out.println("使用toString...结果是一样的说"+jArray.toString());
            if(jArray!=null&&jArray.size()>0){
            	for(int i = 0; i <jArray.size(); i++){
            		Object obj = jArray.get(i);//获取当前部门的信息
                	System.out.println("获取第"+i+"个对象内容..."+obj);
                	JSONObject json = JSONObject.fromObject(obj);//将获取的对象转化为json格式
                	System.out.println("转化为json格式为.."+json);
                     // 钉钉接口的返回的数据，有些是为空的时候不显示，该怎么获取？
                	if(json.has("parentid")){//判断是否存在父部门
                		department.setId(json.getString("id"));
                		department.setParentid(json.getString("parentid"));
                        department.setCreatedeptgroup(json.getBoolean("createDeptGroup"));
                        department.setName(json.getString("name"));
                        department.setAutoadduser(json.getBoolean("autoAddUser"));
                	}else{
                		department.setId(json.getString("id"));
                        department.setCreatedeptgroup(json.getBoolean("createDeptGroup"));
                        department.setName(json.getString("name"));
                        department.setAutoadduser(json.getBoolean("autoAddUser"));
                	}
                	System.out.println(json.has("parentid"));
                    System.out.println(department);
                    departmentDao.insert(department);
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
	
	
}
