package com.zing.dingding.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.zing.dingding.model.NsApprovePurchaseOrderBean;


@Mapper
public interface NetsuitePurchaseApproveDao {
	/**
	 * 
	 * @auth mark
	 * @Description:  保存从netsuite获取的待审批订单数据
	 * @param map
	 * @return    
	 * @date 2017年8月24日 下午6:08:53
	 */
	int insert(NsApprovePurchaseOrderBean purchaseBean);
	
	/**
	 * 
	 * @auth mark
	 * @Description:存入审批人
	 * @param purchaseBean
	 * @return    
	 * @date 2017年8月24日 下午6:10:18
	 */
	int update(NsApprovePurchaseOrderBean purchaseBean);
	
	/**
	 * 发起审批后通过订单id写入审批实例id
	 * @auth mark
	 * @Description: 
	 * @param id
	 * @return    
	 * @date 2017年8月24日 下午7:27:05
	 */
	int updateInstanceId(@Param("returnId")String returnId, @Param("nsId")String nsId);
	
	/**
	 * 通过订单id查询审批实例id，用于判断是否已经发起该审批
	 * @auth mark
	 * @param tranid
	 * @return    
	 * @date 2017年8月24日 下午8:16:41
	 */
	String selectInstanceId(String nsId);
	
	/**
	 * 通过实例id查找订单id
	 * @auth mark
	 * @param processId
	 * @return    
	 * @date 2017年8月25日 下午9:12:53
	 */
	String  selectOrderId(String processId);
}