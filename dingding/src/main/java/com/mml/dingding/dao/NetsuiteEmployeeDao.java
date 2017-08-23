package com.mml.dingding.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.mml.dingding.model.NetsuiteEmployee;

@Mapper
public interface NetsuiteEmployeeDao {
	
	int insert(Map map);
	int update(NetsuiteEmployee employee);
	

	/**
	 * 从netsuite中获取所有员工，保存在本地
	 */
	int insertEmployee(NetsuiteEmployee employee);
	
	/**
	 * 根据手机号码查询是否存在该员工数据
	 * @param mobilephone
	 * @return
	 */
	int checkRepeat(String mobilephone);
}