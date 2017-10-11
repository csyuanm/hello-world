package com.zing.dingding.dao;

import org.apache.ibatis.annotations.Mapper;

import com.zing.dingding.model.NsEmployeeBean;

@Mapper
public interface NsEmployeeDao {
	

	/**
	 * 从netsuite中获取所有员工，保存在本地
	 * @auth mark
	 * @param employee
	 * @return    int
	 * @date 2017年9月1日 上午9:55:57
	 */
	int insertEmployee(NsEmployeeBean employee);
	
	/**
	 * 根据员工id查询其手机号码
	 * @auth mark
	 * @param id
	 * @return    手机号码
	 * @date 2017年9月1日 上午9:52:04
	 */
	String selectPhoneById(int id);
	
	NsEmployeeBean selectEmpById(int id);
	
	String selectPositionById(int id);
	
	String selectSuperById(int id);
	
	String selectNameByJobNumber(String jobNumber);
	
	int updateFlag();
	
	String selectNameByPosition(String position);
	
	/**
	 * 根据手机号码查询是否存在该员工数据
	 * @param mobile
	 * @return
	 */
//	NsEmployeeBean selectEmpByPhone(String mobile);
}