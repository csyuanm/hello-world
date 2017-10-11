package com.zing.dingding.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zing.dingding.common.Constants;
import com.zing.dingding.common.NetSuiteAuth;
import com.zing.dingding.common.UserUtil;
import com.zing.dingding.dao.NsApprovalListDao;
import com.zing.dingding.service.INsApprovalService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@SuppressWarnings("all")
@RestController
@RequestMapping("ns")
public class NsApprovalController {
	
	@Autowired
	INsApprovalService insService;
	
	@Autowired
	NsApprovalListDao approvalListDao;
	
	@RequestMapping("purchaseApproval")
	public String purchaseOrderApproval() {
		Map map = insService.getAccount();
		String token = Constants.getAccessToken();
		String url = UserUtil.PURCHASE_ORDER_GET;
		String result = "";
		String processId = "";
		try {
			result = NetSuiteAuth.getAuth(map, url, null);
			System.out.println(result);
			if (result != null || result != "") {
				JSONArray resultArray = JSONArray.fromObject(result);
				for (int i = 0; i < resultArray.size(); i++) {
					JSONObject purchaseObject = JSONObject.fromObject(resultArray.get(i));
					String internalId = purchaseObject.getString("id");
					int id = Integer.parseInt(internalId);
					processId = approvalListDao.selectProcessIdById(id);
					if (processId == null) {
						// 判断是否发起过审批
						// 根据id查看是否发起审批，发起过的话则不用重复进行相关操作
						String type = purchaseObject.getString("recordtype");
						// 1.保存明细 和 保存到审批列表,
						JSONObject detail = JSONObject.fromObject(purchaseObject.get("columns"));
						if(detail.has("mobilephone")){
							insService.saveGoodsDetail(detail, id);
							insService.saveApprovalList(detail, id, type);
						}
					}
				}
				// 2.去掉重复的订单号
				Set<Integer> idSet = insService.idSetGet(resultArray);
				for (Integer id : idSet) {
					// 在判断一次是否发起审批
					// 3.发起审批
					if (processId == null) {
						insService.purchaseOrderApproval(id, token);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	@RequestMapping("expenseApproval")
	public String expenseReportApproval(){
		String result = "";
		Map map = insService.getAccount();
		String token = Constants.getAccessToken();
		String url = UserUtil.EXPENSE_REPORT_LIST_GET;
		String processId = "";
		try {
			result = NetSuiteAuth.getAuth(map, url, null);
			System.out.println(result);
			if (result != null || result != "") {
				JSONArray resultArray = JSONArray.fromObject(result);
				// 1.保存明细 和 保存到审批列表
				for (int i = 0; i < resultArray.size(); i++) {
					JSONObject purchaseObject = JSONObject.fromObject(resultArray.get(i));
					String internalId = purchaseObject.getString("id");
					int id = Integer.parseInt(internalId);
					processId = insService.processIdGet(id);
					// 根据id判断是否发起过审批，发起过的话则不用重复进行相关操作
					if (processId == null) {
						String type = purchaseObject.getString("recordtype");
						JSONObject detail = JSONObject.fromObject(purchaseObject.get("columns"));
						insService.saveExpenseDetail(detail, id);
						insService.saveApprovalList(detail, id, type);
					}
				}
				// 2.去掉重复的订单号
				Set<Integer> idSet = insService.idSetGet(resultArray);
				for (Integer id : idSet) {
					// 在判断一次是否发起审批
					// 3.发起审批
					if (processId == null) {
						insService.expenseReportApproval(id, token);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 把自定义审批人类型保存在本地
	 * @auth mark
	 * @return    
	 * @date 2017年9月25日 下午4:19:04
	 */
	@RequestMapping("saveApprover")
	public String saveApprover(){
		String result = "";
		result = insService.saveApprover();
		return result;
	}
	
	
	
	@RequestMapping("test")
	public String test(int id){
		String result = "";
		//insService.
		return result;
	}
}
