package com.zing.dingding.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.zing.dingding.model.NsApprovalListBean;

@Mapper
public interface NsApprovalListDao {
	
	int insert(NsApprovalListBean approvalList);
	
	NsApprovalListBean getApprovalInfo(Integer id);
	
	int updateApprovers(@Param("internalId") int internalId, @Param("approver") String approver);
	
	int updateProcessId(@Param("internalId") int internalId, @Param("processId") String processId);
	
	String selectProcessIdById(Integer id);
	
	Double selectTotalById(int id);
	
	Integer selectInternalIdByProcessId(String processId);
}