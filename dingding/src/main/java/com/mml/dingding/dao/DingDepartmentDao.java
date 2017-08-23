package com.mml.dingding.dao;

import org.apache.ibatis.annotations.Mapper;

import com.mml.dingding.model.DingDepartmentBean;

@Mapper
public interface DingDepartmentDao {
   int insert(DingDepartmentBean departmentBean);
}