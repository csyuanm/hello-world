package com.mml.dingding.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.mml.dingding.model.DingUserBean;

@Mapper
public interface DingUserDao {
	
	DingUserBean getUser(String id);
	
    
	DingUserBean get();
	
	List<DingUserBean> getDetail(String department_id);
	
	int insert(DingUserBean user);
	
	/**
	 * 通过手机号码查找用户ID
	 * @param mobile
	 * @return userid
	 */
	String selectIDByPhone(String mobile);
	
	/**
	 * 根据手机号码查询是否存在该员工数据
	 * @param mobile
	 * @return
	 */
	int checkRepeat(String mobile);
	
	//int insertActive(Map map);
	
	/**
	 * 将钉钉获取到到的员工数据保存到本地数据库
	 * @param map
	 * @return
	 */
	int forMap(Map map);
	int updateDepapartment(DingUserBean user);//map有些字段无法直接插入，先存空后再修改
}