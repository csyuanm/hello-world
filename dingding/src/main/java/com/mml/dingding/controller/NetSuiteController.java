package com.mml.dingding.controller;

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
}

