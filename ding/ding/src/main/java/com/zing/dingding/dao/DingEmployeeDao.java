package com.zing.dingding.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.zing.dingding.model.DingEmployeeBean;

@SuppressWarnings("all")
@Mapper
public interface DingEmployeeDao {
	
	/**
	 * 根据id获取个人信息
	 * @auth mark
	 * @param id
	 * @return    
	 * @date 2017年9月5日 上午10:04:28
	 */
	DingEmployeeBean getDingUser(String id);
	
	/**
	 * 保存员工信息
	 * @auth mark
	 * @param user
	 * @return    
	 * @date 2017年9月5日 上午9:47:20
	 */
	int insert(DingEmployeeBean employee);
	
	/**
	 * 通过手机号码查找用户详细信息
	 * @param mobile
	 * @return userid
	 */
	DingEmployeeBean selectDetailByPhone(String mobile);
	
	/**
	 * 通过手机号码查找用户id
	 * @auth mark
	 * @param mobile
	 * @return    
	 * @date 2017年9月25日 下午3:46:35
	 */
	String selectIdByPhone(String mobile);
	
	
	int countByMobile(String mobile);
	
	int updateFlag();
	
	/**
	 * 获取部门员工详细信息
	 * @auth mark
	 * @param department_id
	 * @return    
	 * @date 2017年9月1日 下午2:00:50
	 */
	//List<DingEmployeeBean> getDetail(String department_id);
	
}