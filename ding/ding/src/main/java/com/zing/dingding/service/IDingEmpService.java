package com.zing.dingding.service;

import net.sf.json.JSONObject;


public interface IDingEmpService {
	
	/**
	 * 根据部门id保存钉钉成员到本地
	 * @auth mark
	 * @param token
	 * @param department_id
	 * @return    
	 * @date 2017年8月31日 上午10:54:01
	 */
	String getDetail(String token, String department_id);
	
	/**
	 * 向钉钉添加员工
	 * @auth mark
	 * @param employee
	 * @param token
	 * @return    
	 * @date 2017年9月7日 下午4:45:14
	 */
	String addDingUser(String employee, String token);
	
	String updateDingUser(JSONObject employee, String token);

	/**
	 * 获取当前公司所有部门
	 * @auth mark
	 * @return
	 * @date 2017年8月31日 上午10:52:20
	 */
	String get();
	
}
