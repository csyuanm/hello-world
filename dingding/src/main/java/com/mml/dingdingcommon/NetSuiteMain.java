package com.mml.dingdingcommon;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;


public class NetSuiteMain {
	public static void main(String[] args) {
		Map accountMap = new HashMap();
		accountMap.put("account", UserUtil.NS_COUNNET); //compayid=4556831
		accountMap.put("email", UserUtil.NS_EMAIL); //登录email
		accountMap.put("password", UserUtil.NS_PASS);//密码
		accountMap.put("role", UserUtil.NS_ROLE); //roleid 默认填写18
		String result = "";
		//get方式获取
		try { 
			result = NetSuiteAuth.getAuth(accountMap, UserUtil.SALESORDERURL, null);
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//post方式提交
		Map searchMap = new HashMap ();
		searchMap.put("internalid", "69");

		JSONObject itemObj = JSONObject.fromObject(searchMap);
		try {
			 result = (NetSuiteAuth.postAuth(accountMap, UserUtil.FULFILLURL, itemObj.toString()))
					.replaceAll("(\r\n|\r|\n|\n\r)", "");
			 //上传跟踪号至NS
			System.out.println("result:" +result);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("3、上传跟踪号到NS发生异常");
		}
	} 

}
