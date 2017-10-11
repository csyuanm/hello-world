package com.zing.dingding.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.zing.dingding.model.NsExpenseDetailBean;

@Mapper
public interface NsExpenseDetailDao {

	int insert(NsExpenseDetailBean expenseBean);
	
	int checkExpenseRepeat(@Param("internalId") int internalId, @Param("category") String category);
	
	List<NsExpenseDetailBean> getExpenseDetailList(Integer internalId);
	
}
