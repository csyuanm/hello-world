package com.zing.dingding.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zing.dingding.common.Constants;
import com.zing.dingding.service.IDingEmpService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@RestController
@SuppressWarnings("all")
@RequestMapping("/user")
public class DingEmpController {

	@Autowired
	IDingEmpService userService;

	/**
	 * 保存所有成员
	 * @auth mark
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException    
	 * @date 2017年8月31日 下午3:22:52
	 */
//	@Scheduled(cron = "0/20 * * * * *")
	@RequestMapping("savaEmployee")
	public String get() throws IOException, URISyntaxException {
		return userService.get();
	}
	
	
	
	
	
				
				//以下只是测试时需要
				//以下只是测试时需要
				//以下只是测试时需要
				//以下只是测试时需要
				//以下只是测试时需要
				//以下只是测试时需要
				//以下只是测试时需要
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 获取部门员工详细信息并保存
	 * @param department_id
	 * @return
	 */
	@RequestMapping("addUserList")
	public String getUserList(String department_id) {

		String access_token = Constants.getAccessToken();
		return userService.getDetail(access_token, department_id);
	}
	
	
	/**
	 * 向钉钉添加成员
	 * 应该传入部门id 、手机、名字 、	工号
	 * @return
	 * @throws Exception 
	 * @throws IOException 
	 */
	@RequestMapping("addUser")
	public String addUser() throws IOException, Exception {
		String str = null;
		String access_token = Constants.getAccessToken();
		String url = "https://oapi.dingtalk.com/user/create?access_token=" + access_token;
	//	str = NetSuiteAuth.getAuth(NetSuiteAuth.getAccount(), UserUtil.EMPLOYEE_INFOS, null);
		JSONArray resultArray = JSONArray.fromObject(str);
		if (resultArray.size() > 0) {
			for (int i = 0; i < resultArray.size(); i++) {
				JSONObject employeeArray = JSONObject.fromObject(resultArray.get(i));
				String employee = employeeArray.getString("columns");
				userService.addDingUser(employee, access_token);
			}
		}
		System.out.println(str);
		return str;
	}
}
