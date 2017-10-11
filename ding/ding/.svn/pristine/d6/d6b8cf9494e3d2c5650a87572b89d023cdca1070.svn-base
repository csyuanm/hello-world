package com.zing.dingding.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@SuppressWarnings("all")
public interface INsApprovalService {
	
	Map getAccount();
	
	/**
	 * 向钉钉发起审批
	 * @auth mark
	 * @param id
	 * @param token
	 * @return    
	 * @date 2017年9月25日 下午4:23:48
	 */
	String purchaseOrderApproval(Integer id, String token);
	
	String expenseReportApproval(Integer id, String token);
	
	/**
	 * 保存商品明细
	 * @auth mark
	 * @param detail
	 * @param id
	 * @return    
	 * @date 2017年9月25日 下午4:22:44
	 */
	String saveGoodsDetail(JSONObject detail, int id);
	
	
	String saveExpenseDetail(JSONObject detail, int id);
	
	
	/**
	 * 保存审批单信息
	 * @auth mark
	 * @param detail
	 * @param id
	 * @param type
	 * @return    
	 * @date 2017年9月25日 下午4:23:02
	 */
	String saveApprovalList(JSONObject detail, int id, String type);
	
	/**
	 * 去掉重复的单号id
	 * @auth mark
	 * @param resultArray
	 * @return    
	 * @date 2017年9月25日 下午4:22:14
	 */
	Set<Integer> idSetGet(JSONArray resultArray);
	
	/**
	 * 把自定义审批人类型保存在本地
	 * @auth mark
	 * @return    
	 * @date 2017年9月25日 下午4:20:14
	 */
	String saveApprover();
	
	/**
	 * 获取审批人id
	 * @auth mark
	 * @param id
	 * @return    
	 * @date 2017年9月25日 下午4:21:19
	 */
	String approversIdListGet(String name);
	
	/**
	 * 获取审批人姓名
	 * @auth mark
	 * @param id
	 * @return    
	 * @date 2017年9月25日 下午4:21:44
	 */
	String approverNameListGet(String name);
	
	String processIdGet(int id);
}
