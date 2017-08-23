package com.mml.dingding.controller;

import java.io.Closeable;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mml.dingding.common.Constants;

import net.sf.json.JSONObject;

@RestController
@RequestMapping("/process")
public class ProcessInstanceController {
	
	/*
	 * AgentID    :   115257478
	 * 请假 ： PROC-59E70233-4A1D-4B22-919E-ACCDC2688EDD
	 * 报销 ： PROC-54C2B0E7-9A3E-4442-873C-F20AACB9069A
	 * 采购 ： PROC-0CC33047-C4C8-4A99-9E18-938DC920333B
	 * 测试请假 : PROC-EF6YRO35P2-R0DNLIH6NT3GE2UF0W4K2-5652776J-4
	 *           PROC-EF6YRO35P2-R0DNLIH6NT3GE2UF0W4K2-5652776J-4
	 * 审批测试 ： PROC-0CC33047-C4C8-4A99-9E18-938DC920333B
	 * 无理由报销 ： PROC-GTHKCO8W-4MCN48TKOW9ZT0BK7NAF2-6XF1B76J-R
	 * 单行一条明细 ： PROC-CK1K5CQV-ABDN3GAWR46MF6PGRMWN1-6MW4K76J-4
	 * 
	 * 多个单行 ：PROC-CK1K5CQV-MDDN6KOMRWSH6OVMPL4U1-KIVQM76J-1
	 */
	
	
	/**
	 * 发起审批实例
	 * @return
	 */
	@RequestMapping("create")
	public String createProcess(){
		String str = null;
	    String access_token = Constants.getAccessToken();
		HttpClient httpClient = new DefaultHttpClient();  
		String process_code="PROC-GTHKCO8W-4MCN48TKOW9ZT0BK7NAF2-6XF1B76J-R";
		HttpResponse response = null;
		HttpEntity httpEntity = null;
		HttpPost httppost = null;
	    try {  
	    	//当需要严格的json时，就应该严格的使用json对象设置参数，接收参数时需要用StringEntityhttp来接收参数
	    	//Post.setEntity(new StringEntity(postData.toString(), HTTP.UTF_8));
	    	JSONObject jobj = new JSONObject();  
			String approvers = "manager4417,02402648081143016";
			JSONObject obj = new JSONObject();
			obj.put("name", "报销金额");
			obj.put("value", "2");
			jobj.put("process_code", process_code); //审批类id7
			jobj.put("originator_user_id", "manager4417"); //发起人id
			jobj.put("dept_id", 45988760);//发起人所在部门
			jobj.put("approvers", approvers);  //审批人userid列表，只需要传递字符串就可以，用逗号隔开
			jobj.put("form_component_values", obj); // 审批内容
			
			String url = "https://eco.taobao.com/router/rest?method=dingtalk.smartwork.bpms.processinstance.create&v=2.0&format=json&session="+access_token;
			//设置Post参数     ，当不需要用json来发送post请求时，应当使用NameValuePair来设置参数,如下所示
			//httppost.setEntity(new UrlEncodedFormEntity(params,"utf-8"));
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("process_code", process_code));
			params.add(new BasicNameValuePair("originator_user_id", "manager4417"));
			params.add(new BasicNameValuePair("dept_id", "45988760"));
			params.add(new BasicNameValuePair("approvers", approvers));
			params.add(new BasicNameValuePair("form_component_values", obj.toString()));
			httppost = new HttpPost(url);
			httppost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
			System.out.println(httppost.getURI());
			response = httpClient.execute(httppost);
			httpEntity = response.getEntity();
			str = EntityUtils.toString(httpEntity, "utf-8");
			System.out.println(str);
	    } catch (ClientProtocolException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
//	    } catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
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
	 * 获取审批列表
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("get")
	public String getProcess()throws ParseException{
		String str = null;
		String processCode = "PROC-GTHKCO8W-4MCN48TKOW9ZT0BK7NAF2-6XF1B76J-R";
		String endTime = "";
		 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = sdf.parse("2017-08-17 00:00:00");
			long startTime = date.getTime();
	    String access_token = Constants.getAccessToken();
		HttpClient httpClient = new DefaultHttpClient();  
		HttpResponse response = null;
		HttpEntity httpEntity = null;
		HttpPost httppost = null;
	    String url="https://eco.taobao.com/router/rest?method=dingtalk.smartwork.bpms.processinstance.list&v=2.0&format=json&session="+access_token+"&process_code="+processCode+"&start_time="+startTime;  
	    try {  //+"&process_code="+processCode+"&start_time="+startTime
	    	 httppost = new HttpPost(url);
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
/*List<FormComponentValueVo>  list = new ArrayList<FormComponentValueVo>();
FormComponentValueVo reason = new FormComponentValueVo();
reason.setName("申请事由");
reason.setValue("8月采购");
FormComponentValueVo type = new FormComponentValueVo();
type.setName("采购类型");
type.setValue("生活");
FormComponentValueVo name = new FormComponentValueVo();
name.setName("名称");
name.setValue("咖啡");
FormComponentValueVo per = new FormComponentValueVo();
per.setName("单位");
per.setValue("盒");
FormComponentValueVo price = new FormComponentValueVo();
price.setName("价格");
price.setValue("500");
FormComponentValueVo mingxi = new FormComponentValueVo();
mingxi.setName("采购明细");
mingxi.setValue(JSONArray.fromObject(Arrays.asList(name,per,price)).toString());
list.add(reason);
list.add(type);
list.add(mingxi);*/