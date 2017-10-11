package com.zing.dingding.service;

public interface IDepartmentService {
	/**
	 * 保存所有部门到本地
	 * @auth mark
	 * @param token
	 * @return    
	 * @date 2017年9月15日 下午3:24:37
	 */
	String saveDepartment(String token);
	
	/**
	 * 在钉钉创建部门
	 * @auth mark
	 * @param deptId
	 * @param token
	 * @return    
	 * @date 2017年9月8日 上午10:00:57
	 */
	String createDepartment(String deptString, String token);
	
}
