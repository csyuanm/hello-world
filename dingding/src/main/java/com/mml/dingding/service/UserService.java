package com.mml.dingding.service;

import java.util.List;

import com.mml.dingding.model.DingUserBean;

public interface UserService {

	/*
	 * 获取个人详细信息
	 */
	String getUser(String token, String id);
	
	String getDetail(String token, String department_id);
	
	String addDingUser();

	//test简单查询数据库
	DingUserBean get();
	
}
