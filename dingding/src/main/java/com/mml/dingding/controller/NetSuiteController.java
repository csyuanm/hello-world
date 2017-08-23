package com.mml.dingding.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mml.dingding.common.NetSuiteAuth;
import com.mml.dingding.common.UserUtil;
import com.mml.dingding.service.NetSuiteService;

@RestController
@RequestMapping("NS")
public class NetSuiteController {
		
	@Autowired NetSuiteService nsService;
	

	/**
	 * 从netsuite中获取所有员工，保存在本地，并推送到钉钉
	 */
	@RequestMapping("getEmployeeList")
	public String getEmployeeList(){
		
		return nsService.getEmployeeList();
	}
	
	/**
	 * 获取netsuite员工生日,并向下个月过生日的员工发祝福语
	 * @return
	 */
	//@Scheduled(cron = "5 * * * * * ")
	@RequestMapping("/getBirthday")
	public String getBrithday(){
		
		return nsService.getEmployeeBirth();
	}
	
	/**
	 * 从netsuite获取待审批订单列表
	 * 要获取字段：数量、货品、选项 、费率、金额
	 * @return
	 */
	@RequestMapping("getPurchaseList")
	public String getPurchaseList(){
		
		return nsService.getPurchaseList();
	}
	
	/**
	 * 从netsuite获取待审批订单列表
	 * @return
	 */
	@RequestMapping("getList")
	public String getList(){
		String result = "";
		Map accountMap = new HashMap();
		accountMap.put("account", UserUtil.NS_COUNNET); //compayid=4556831
		accountMap.put("email", UserUtil.NS_EMAIL); //登录email
		accountMap.put("password", UserUtil.NS_PASS);//密码
		accountMap.put("role", UserUtil.NS_ROLE); //roleid 默认填写18
		try {
			result = NetSuiteAuth.getAuth(accountMap, UserUtil.PURCHASEORDERAPPROVE, null);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return result;
	}
	
}

