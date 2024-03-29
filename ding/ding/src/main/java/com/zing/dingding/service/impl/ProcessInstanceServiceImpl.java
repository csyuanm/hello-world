package com.zing.dingding.service.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zing.dingding.common.Constants;
import com.zing.dingding.common.DingDingAuth;
import com.zing.dingding.common.NetSuiteAuth;
import com.zing.dingding.common.UserUtil;
import com.zing.dingding.dao.DingApprovalReceiveDao;
import com.zing.dingding.dao.NetsuitePurchaseApproveDao;
import com.zing.dingding.dao.NsApprovalListDao;
import com.zing.dingding.model.DingApprovalReceiveBean;
import com.zing.dingding.model.NsApprovalListBean;
import com.zing.dingding.service.INsApprovalService;
import com.zing.dingding.service.IProcessInstanceService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
@SuppressWarnings("all")
public class ProcessInstanceServiceImpl implements IProcessInstanceService {

	@Autowired
	DingApprovalReceiveDao processInstanceDao;

	@Autowired
	NetsuitePurchaseApproveDao purchaseDao;
	
	@Autowired
	NsApprovalListDao approvalListDao;
	
	@Autowired
	INsApprovalService nsService;
	
	/**
	 * 获取钉钉审批实例结果，把结果保存在本地，并把结果返回netsuite，对采购订单进行修改
	 */
	@Override
	public String processInstanceGet(String processCode) throws ParseException {
		Map accountMap = nsService.getAccount();
		String str = null;
		long startTime;
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(cal.DATE, -2);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = sdf.parse(sdf.format(cal.getTime()));
		startTime = date.getTime();
		String access_token = Constants.getAccessToken();
		String url = "https://eco.taobao.com/router/rest?method=dingtalk.smartwork.bpms.processinstance.list&v=2.0&format=json&session="
				+ access_token + "&process_code=" + processCode + "&start_time=" + startTime;
		try {
			// 获取设定时间内的审批的订单列表
			str = DingDingAuth.dingPost2(url);
			JSONObject resultOne = JSONObject.fromObject(str);
			JSONObject resultTwo = JSONObject
					.fromObject(resultOne.get("dingtalk_smartwork_bpms_processinstance_list_response"));
			JSONObject resultThree = JSONObject.fromObject(resultTwo.get("result"));
			JSONObject resultFour = JSONObject.fromObject(resultThree.get("result"));
			JSONObject resultFive = JSONObject.fromObject(resultFour.get("list"));
			if (resultFour.get("list") != null || resultFour.get("list") != "") {
				String instanceList = resultFive.getString("process_instance_top_vo");
				// 把获取结果保存在本地
				JSONArray resultSix = JSONArray.fromObject(instanceList);
				for (int i = 0; i < resultSix.size(); i++) {
					String instanceDetail = resultSix.getString(i);
					String ret = saveProcessInstance(instanceDetail);
					JSONObject processJson = JSONObject.fromObject(resultSix.get(i));
					String result = processJson.getString("process_instance_result");
					JSONObject info = new JSONObject();
					String id = processJson.getString("process_instance_id");
					Integer internalId = approvalListDao.selectInternalIdByProcessId(id);
					NsApprovalListBean approvalInfo = approvalListDao.getApprovalInfo(internalId);
					if(internalId != null){
						info.put("id", internalId);
						info.put("demo", result);
						info.put("recordtype", approvalInfo.getType());
						if (result.equals("agree")) {
							// 改变订单状态
							String ss = "";
							System.out.println("同意的订单号为：" + internalId);
							if (internalId != null) {
								ss = NetSuiteAuth.postAuth(accountMap, UserUtil.APPROVAL_STATUS_UPDATE, info.toString());
								System.out.println(ss);
							}
						}
						if (result.equals("refuse")) {
							// 备注已拒绝
							String ss = "";
							System.out.println("拒绝的单号为：" + internalId);
							if (internalId != null) {
								ss = NetSuiteAuth.postAuth(accountMap, UserUtil.APPROVAL_STATUS_UPDATE, info.toString());
								System.out.println(ss);
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return str;
	}
	
	/**
	 * 保存从钉钉接收的审批列表
	 * @auth mark
	 * @param instanceList
	 * @return    
	 * @date 2017年9月14日 上午11:24:13
	 */
	public String saveProcessInstance(String instanceDetail) {
		DingApprovalReceiveBean processBean = new DingApprovalReceiveBean();
		JSONObject processJson = JSONObject.fromObject(instanceDetail);
		processBean.setApproverUseridList(
				JSONObject.fromObject(processJson.getString("approver_userid_list")).getString("string"));
		processBean.setCreateTime(processJson.getString("create_time"));
		if (processJson.has("finish_time")) {
			processBean.setFinishTime(processJson.getString("finish_time"));
		} else {
			processBean.setFinishTime(null);
		}
		processBean.setFormComponentValues(JSONObject.fromObject(processJson.getString("form_component_values"))
				.getString("form_component_value_vo"));
		processBean.setOriginatorDeptId(processJson.getString("originator_dept_id"));
		processBean.setOriginatorUserid(processJson.getString("originator_userid"));
		processBean.setProcessInstanceId(processJson.getString("process_instance_id"));
		processBean.setProcessInstanceResult(processJson.getString("process_instance_result"));
		processBean.setStatus(processJson.getString("status"));
		processBean.setTitle(processJson.getString("title"));
		processInstanceDao.insert(processBean);
		return "";
	}
	
	
	
	
	
				//test   test  test
				//test   test  test
				//test   test  test
				//test   test  test
				//test   test  test
				//test   test  test
	
	

	@Override
	public String createProcess() {
		String str = "";
		String access_token = Constants.getAccessToken();
		String process_code="PROC-0CC33047-C4C8-4A99-9E18-938DC920333B";
	    	//当需要严格的json时，就应该严格的使用json对象设置参数，接收参数时需要用StringEntityhttp来接收参数
	    	//Post.setEntity(new StringEntity(postData.toString(), HTTP.UTF_8));
	    	JSONArray array = new JSONArray();
	    	String userId = "manager4417";
	    	String deptId = "45677420";
			String s1 = "manager4417";
			JSONObject obj1 = new JSONObject();
			obj1.put("name", "采购物品");
			obj1.put("value", "A");
			JSONObject obj = new JSONObject();
			obj.put("name", "采购金额");
			obj.put("value", "2");
			array.add(obj1);
			array.add(obj);
			String url = "https://eco.taobao.com/router/rest?method=dingtalk.smartwork.bpms.processinstance.create&v=2.0&format=json&session="+access_token;
			//设置Post参数     ，当不需要用json来发送post请求时，应当使用NameValuePair来设置参数,如下所示
			//httppost.setEntity(new UrlEncodedFormEntity(params,"utf-8"));
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("process_code", process_code));
			params.add(new BasicNameValuePair("originator_user_id", userId));
			params.add(new BasicNameValuePair("dept_id", deptId));
			params.add(new BasicNameValuePair("approvers", s1));
			params.add(new BasicNameValuePair("form_component_values", array.toString()));
			try {
				str = DingDingAuth.postNoJson(url, params);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			System.out.println(str);
		return str;
	}

	
	
	
}
