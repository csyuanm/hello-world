package com.mml.dingding.service;

public interface NetSuiteService {
	

	/**
	 * 从netsuite中获取所有员工，保存在本地，并推送到钉钉
	 */
	public String getEmployeeList();
	
	/**
	 * 获取netsuite员工生日,并向下个月过生日的员工发祝福语
	 * @return
	 */
	public String getEmployeeBirth();
}
