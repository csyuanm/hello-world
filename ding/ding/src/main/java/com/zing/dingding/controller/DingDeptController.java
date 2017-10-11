package com.zing.dingding.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zing.dingding.common.Constants;
import com.zing.dingding.service.IDepartmentService;

@RestController
@RequestMapping("department")
public class DingDeptController {
	
	@Autowired 
	IDepartmentService departmentService;
	
	/**
	 * 保存公司组织结构
	 * @auth mark
	 * @return    
	 * @date 2017年9月7日 下午4:55:10
	 */
//	@Scheduled(cron = "0/10 * * * * *")
	@RequestMapping("saveDepartment")
	public String getDpartmentList(){
		System.out.println("开始保存公司部门");
        String access_token = Constants.getAccessToken();
        
		return departmentService.saveDepartment(access_token);
	}
	
	//创建部门
	@RequestMapping("createDept")
	public String createDepartment(String deptString){
		String token = Constants.getAccessToken();
		departmentService.createDepartment(deptString, token);
		return "";
	}
}