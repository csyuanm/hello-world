package com.zing.dingding.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zing.dingding.common.Constants;
import com.zing.dingding.common.DingDingAuth;
import com.zing.dingding.service.IProcessInstanceService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@RestController
@RequestMapping("/process")
public class ProcessInstanceController {
	
	
	@Autowired IProcessInstanceService processService;
	/*
	 * AgentID    :   115257478
	 * 采购申请： PROC-424LSGUV-JM8OOE9UQ2KS2QH44NCP1-ETFVIF7J-1
	 * 费用报销： PROC-GTHKCO8W-4MCN48TKOW9ZT0BK7NAF2-6XF1B76J-R
	 */
	
	
	/**
	 * 获取钉钉采购审批实例结果，把结果保存在本地，并把结果返回netsuite，对采购订单进行修改
	 * @return
	 * @throws ParseException
	 */
//	@Scheduled(cron = "0/30 * * * * * ")
	@RequestMapping("purchaseApprovalGet")
	public String getProcess()throws ParseException{
		String processCode = "PROC-424LSGUV-JM8OOE9UQ2KS2QH44NCP1-ETFVIF7J-1";
		return processService.processInstanceGet(processCode);
	}

	/**
	 * 获取钉钉费用报销审批实例结果，把结果保存在本地，并把结果返回netsuite，对采购订单进行修改
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("expenseApprovalGet")
	public String getExpenseProcess() throws ParseException{
		String processCode = "PROC-GTHKCO8W-4MCN48TKOW9ZT0BK7NAF2-6XF1B76J-R";
		return processService.processInstanceGet(processCode);
	}
	
	
	
	
	

	/*
	 * 
	 *    下边是test
	 *    下边是test
	 *    下边是test
	 * 
	 */
	
	/**
	 * test
	 * 
	 * 发起审批实例
	 * @return
	 */
	@RequestMapping("create")
	public String createProcess(){
		String str = null;
	    
		str = processService.createProcess();
		
		return str;
	}
	
	/** test
	 * 发起审批实例
	 * @return
	 * @throws Exception 
	 * @throws IOException 
	 */
	@RequestMapping("test")
	public String createTest() throws IOException, Exception{
		String str = null;
	    String access_token = Constants.getAccessToken();
		String process_code="PROC-0CC33047-C4C8-4A99-9E18-938DC920333B";
	    	JSONObject jobj = new JSONObject();  
	    	JSONArray array = new JSONArray();
	    	
			String approvers = "manager4417";
			String[] s1 = {"manager4417,1214685534684851"};
			String[] s2 = {"manager4417,1214685534684851"};
		//	List<String> s = new ArrayList<String>();
			JSONArray sj = new JSONArray();
			sj.add(s1);
			sj.add(s2);
			JSONObject obj1 = new JSONObject();
			obj1.put("name", "采购物品");
			obj1.put("value", "2");
			JSONObject obj2 = new JSONObject();
			obj2.put("name", "采购金额");
			obj2.put("value", "66");
			array.add(obj1);
			array.add(obj2);
			jobj.put("process_code", process_code); //审批类id7
			jobj.put("originator_user_id", "manager4417"); //发起人id
			jobj.put("dept_id", 45988760);//发起人所在部门
			jobj.put("approvers", approvers);  //审批人userid列表，只需要传递字符串就可以，用逗号隔开
			jobj.put("form_component_values", obj2); // 审批内容
			
			String url = "https://eco.taobao.com/router/rest?method=dingtalk.smartwork.bpms.processinstance.create&v=2.0&format=json&session="+access_token;
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("process_code", process_code));
			params.add(new BasicNameValuePair("originator_user_id", "manager4417"));
			params.add(new BasicNameValuePair("dept_id", "45988760"));
			params.add(new BasicNameValuePair("approvers", approvers));
			params.add(new BasicNameValuePair("form_component_values", array.toString()));
			
			str = DingDingAuth.postNoJson(url, params);
			
		return str;
	}

	
}
