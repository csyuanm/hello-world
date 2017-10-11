package com.zing.dingding.service.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.zing.dingding.common.Constants;
import com.zing.dingding.common.DingDingAuth;
import com.zing.dingding.common.NetSuiteAuth;
import com.zing.dingding.common.UserUtil;
import com.zing.dingding.dao.DingDepartmentDao;
import com.zing.dingding.dao.DingEmployeeDao;
import com.zing.dingding.dao.NsEmployeeDao;
import com.zing.dingding.dao.NetsuitePurchaseApproveDao;
import com.zing.dingding.dao.NsApprovalListDao;
import com.zing.dingding.model.DingDepartmentBean;
import com.zing.dingding.model.DingEmployeeBean;
import com.zing.dingding.model.NsApprovePurchaseOrderBean;
import com.zing.dingding.model.NsEmployeeBean;
import com.zing.dingding.service.INetSuiteService;
import com.zing.dingding.service.INsApprovalService;
import com.zing.dingding.service.IDingEmpService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
@SuppressWarnings("all")
public class NetSuiteServiceImpl implements INetSuiteService {

	@Autowired
	NsEmployeeDao nsEmpDao;

	@Autowired
	DingEmployeeDao dingUserDao;

	@Autowired
	DingDepartmentDao departmentDao;
	
	@Autowired
	IDingEmpService userService;

	@Autowired
	NetsuitePurchaseApproveDao purchaseDao;
	
	@Autowired
	INsApprovalService nsService;
	
	@Autowired
	NsApprovalListDao approvalListDao;

	/**
	 * 从netsuite中获取所有员工，保存在本地，并推送到钉钉
	 */
	@Override
	public String getEmployeeList() {
		Map accountMap = nsService.getAccount();
		String result = "";
		String str = "";
		String access_token = Constants.getAccessToken();
		
		try {
			//1.从netsuite获取成员
			result = NetSuiteAuth.getAuth(accountMap, UserUtil.EMPLOYEE_INFOS, null);
			nsEmpDao.updateFlag();
			str = saveNsEmp(result,access_token);
		} catch (Exception e) {
			System.out.println("异常");
			e.printStackTrace();
		}
		return str;
	}
	
	public String saveNsEmp(String emp, String accessToken){
		JSONArray resultArray = JSONArray.fromObject(emp);
		if (resultArray.size() > 0) {
			for (int i = 0; i < resultArray.size(); i++) {
				NsEmployeeBean nsEmployee = new NsEmployeeBean();
				JSONObject employeeArray = JSONObject.fromObject(resultArray.get(i));
				System.out.println(employeeArray);
				nsEmployee.setEmpInternalid(employeeArray.getInt("id"));
				JSONObject employee = JSONObject.fromObject(employeeArray.get("columns"));
				System.out.println(employee);
				if (employee.has("mobilephone")) {
				if (employee.has("supervisor")) {
					JSONObject supervisorObj = JSONObject.fromObject(employee.get("supervisor"));
					String jobNumber = supervisorObj.getString("name");
					String name = nsEmpDao.selectNameByJobNumber(jobNumber);
					nsEmployee.setSupervisor(name);
					nsEmployee.setSuperInternalid(supervisorObj.getString("internalid"));
				}
				if (employee.has("department")) {
					JSONObject deptObj = JSONObject.fromObject(employee.get("department"));
					nsEmployee.setDeptInternalid(deptObj.getInt("internalid"));
					nsEmployee.setDeptName(deptObj.getString("name"));
				}
				if (employee.has("subsidiary")) {
					nsEmployee.setSubsidiary(JSONObject.fromObject(employee.getString("subsidiary")).getString("name"));
				}
				nsEmployee.setJobNumber(employee.getString("entityid"));
				if(employee.has("formulatext")){
					nsEmployee.setName(employee.getString("formulatext"));
				}
				if (employee.has("title")) {
					nsEmployee.setPosition(employee.getString("title"));
				}
				if (employee.has("email")) {
					nsEmployee.setEmail(employee.getString("email"));
				}
				nsEmployee.setMobile(employee.getString("mobilephone").replace(" ", ""));
				//2.把获取到的成员数据保存在本地
				nsEmpDao.insertEmployee(nsEmployee);
				System.out.println("已添加第"+(i+1)+"个人");
				//3.把员工添加到钉钉
				//添加之前判断有没有添加到钉钉
				//已添加的就修改
				int count = dingUserDao.countByMobile(employee.getString("mobilephone"));
				if(count == 0){
				//	userService.addDingUser(employee.toString(), accessToken);
				}
				if(count == 1){
					System.out.println("可以执行修改语句");
				//	userService.updateDingUser(employee, accessToken);
				}
				}
			}
		}
		return "";
	}

	/**
	 * 获取netsuite员工生日,并向下个月过生日的员工发祝福语
	 */
	@Override
	public String getEmployeeBirth() {
		
		Map accountMap = nsService.getAccount();
		
		String result = "";
		String sendMsg = "生日快乐！";
		String token = Constants.getAccessToken();
		String url = "https://oapi.dingtalk.com/message/send?access_token=" + token;
		try {
			// 从netsuite获取下个月过生日员工信息列表
			result = NetSuiteAuth.getAuth(accountMap, UserUtil.EMPLOYEEBIRTH, null);
			System.out.println("要过生日的人："+result);
			JSONArray jArray = JSONArray.fromObject(result);
			if (jArray.size() > 0) {
				JSONObject jsonEmployee = new JSONObject();
				for (int i = 0; i < jArray.size(); i++) {
					jsonEmployee = JSONObject.fromObject(jArray.get(i));
					JSONObject obj = JSONObject.fromObject(jsonEmployee.get("columns"));
					if (obj.size() > 0 && obj.has("mobilephone")) {
						String mobile = obj.getString("mobilephone").replace(" ", "");
						DingEmployeeBean userBean = dingUserDao.selectDetailByPhone(mobile);
						if(userBean != null){
							String userid = userBean.getUserId();
							if (userid != null) {
								JSONObject cobj = new JSONObject();
								JSONObject content = new JSONObject();
								content.put("content", sendMsg);
								// 一天之内不能向同一个用户发送同一内容
								cobj.put("agentid", 115257477);
								cobj.put("touser", userid);// 一个人时不需要传入数组，但是多人传数组也不行
								cobj.put("msgtype", "text");
								cobj.put("text", content);
								DingDingAuth.dingPost(cobj, url);
							}
						} else {
							System.out.println("钉钉中不存在此人");
						}
					} else{
						System.out.println("手机号码不存在，请填写手机号码");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 从netsuite获取待审批采购单，保存在本地
	 *  向钉钉发起审批
	 *  发起成功后将返回的审批id保存到待审批订单中
	 */
	@Override
	public String getPurchaseList() {

		Map accountMap = nsService.getAccount();
		
		String result = "";
		String ret = "";
		String access_token = Constants.getAccessToken();
		String process_code = "PROC-0CC33047-C4C8-4A99-9E18-938DC920333B";
		NsApprovePurchaseOrderBean purchaseBean = new NsApprovePurchaseOrderBean();
		try {
			// 1.从netsuite获取待审批订单
			result = NetSuiteAuth.getAuth(accountMap, UserUtil.PURCHASE_ORDER_GET, null);
			System.out.println("获取待审批订单返回结果"+result);
			if(result!=null&&result!=""){
				System.out.println("待审批采购订单列表为：" + result);
				JSONArray purchaseArray = JSONArray.fromObject(result);
				if (purchaseArray.size() > 0) {
					JSONObject jsonPurchase = new JSONObject();
					for (int i = 0; i < purchaseArray.size(); i++) {
						System.out.println("第" + (i + 1) + "次订单开始执行");
						jsonPurchase = JSONObject.fromObject(purchaseArray.get(i));
						String nsId = jsonPurchase.getString("id");
						JSONObject approverObject = JSONObject.fromObject(jsonPurchase.get("columns"));
						// 获取到订单后，直接通过id查询审批id，判断是否发起审批
						String instanceId = purchaseDao.selectInstanceId(nsId);
						if (approverObject.size() > 0 && approverObject.has("custbodyapprover")) {
							// 通过id判断是否发送
							if (instanceId == null) {
								// purchaseBean.setTranId(jsonPurchase.getString("id"));
								if (approverObject.has("taxcode")) {
									purchaseBean.setTaxCode(approverObject.getString("taxcode"));
								}
								purchaseBean.setStatusRef(approverObject.getString("statusref"));
								if (approverObject.has("location")) {
									purchaseBean.setLocation(approverObject.getString("location"));
								}
								if (approverObject.has("duedate")) {
									purchaseBean.setDueDate(approverObject.getString("duedate"));
								}
								purchaseBean.setNsId(nsId);
								purchaseBean.setCustBodyApprover(approverObject.getString("custbodyapprover"));
								purchaseBean.setTranId(approverObject.getString("tranid"));
								purchaseBean.setAmount(approverObject.getDouble("amount"));
								purchaseBean.setDisplayName(approverObject.getString("displayname"));
								purchaseBean.setQuantity(approverObject.getInt("quantity"));
								purchaseBean.setRate(approverObject.getDouble("rate"));
								purchaseBean.setEntityId(approverObject.getString("entityid"));
								purchaseBean.setTranDate(approverObject.getString("trandate"));
								// 2.将采购单保存在本地
								purchaseDao.insert(purchaseBean);

								ret = processFormat(nsId, access_token, approverObject, process_code);
								System.out.println(ret);
							} else {
								System.out.println("该订单已发起审批");
							}
						} else {
							System.out.println("获取订单出错或者审批人不存在");
						}
					}
				}
			} else {
				System.out.println("无审批订单");
				return "无审批订单";
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 获取到审批订单数据后，解析成审批格式数据，
	 * @auth mark
	 * @param nsId
	 * @param access_token
	 * @param approverObject
	 * @param process_code
	 * @return    
	 * @date 2017年9月1日 上午11:24:13
	 */
	public String processFormat(String nsId, String access_token, JSONObject approverObject, String process_code){
		
		String url = "https://eco.taobao.com/router/rest?method=dingtalk.smartwork.bpms.processinstance.create&v=2.0&format=json&session="
				+ access_token;
		// 获取审批人
		String approverlist = approverIdGet(approverObject);
		//获取申请人id
		String userId = getUserId(approverObject);
		//获取申请人部门id
		//1.ns 部门id通过名称去查询，当出现相同部门时，再根据其上一级部门判断
		//2.钉钉，可以采用发起人所在部门id查部门名称与返回名称是否相同判断
		String department = approverObject.getString("department");
		JSONObject emp = JSONObject.fromObject(approverObject.get("employee"));
		int empId = emp.getInt("internalid");
		String deptId = deptIdGet(department,empId);
		System.out.println("申请人所在部门："+deptId);
		String rate = JSONObject.fromObject(approverObject).getString("rate");
		String quantity = JSONObject.fromObject(approverObject).getString("quantity");
		String amount = JSONObject.fromObject(approverObject).getString("amount");
		String displayname = JSONObject.fromObject(approverObject).getString("displayname");
		System.out.println("displayname..." + displayname);
		JSONObject obj1 = new JSONObject();
		JSONObject obj2 = new JSONObject();
		JSONObject obj3 = new JSONObject();
		JSONObject obj4 = new JSONObject();
		obj1.put("name", "采购物品");
		obj1.put("value", displayname);
		obj2.put("name", "数量");
		obj2.put("value", quantity);
		obj3.put("name", "采购单价");
		obj3.put("value", rate);
		obj4.put("name", "采购金额");
		obj4.put("value", amount);
		JSONArray array = new JSONArray();
		array.add(obj1);
		array.add(obj2);
		array.add(obj3);
		array.add(obj4);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("process_code", process_code));
		params.add(new BasicNameValuePair("originator_user_id", userId));
		params.add(new BasicNameValuePair("dept_id", deptId));
		params.add(new BasicNameValuePair("approvers", approverlist));
		params.add(new BasicNameValuePair("form_component_values", array.toString()));
		// 3.发起审批实例
		System.out.println("-------");
		System.out.println(nsId);
		System.out.println(url);
		System.out.println(params);
		processPost(nsId, url, params);
		
		return "";
	}
	
	/**
	 * 向钉钉发起审批实例,并把返回审批id更新到订单表
	 * @auth mark
	 * @param nsId
	 * @param url
	 * @param params
	 * @return    
	 * @date 2017年9月1日 上午11:15:31
	 */
	public String processPost(String nsId, String url, List<? extends NameValuePair> params){
		// 发起审批实例
		try {
			String str = DingDingAuth.postNoJson(url, params);
			System.out.println("审批结果为："+str);
			JSONObject resultOne = JSONObject.fromObject(str);
			JSONObject resultTwo = JSONObject
					.fromObject(resultOne.get("dingtalk_smartwork_bpms_processinstance_create_response"));
			JSONObject resultThree = JSONObject.fromObject(resultTwo.get("result"));
			// 4.将返回的id写入数据库
			String processId = resultThree.getString("process_instance_id");
			System.out.println(processId + "...." + nsId);
		//	purchaseDao.updateInstanceId(processId, nsId);
	//		approveDao.updateReturnId(processId, nsId);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * 获取userId
	 * @auth mark
	 * @param approverObject
	 * @return    
	 * @date 2017年9月14日 上午11:05:22
	 */
	public String getUserId(JSONObject approverObject){
		DingEmployeeBean userBean = new DingEmployeeBean();
		if(approverObject.has("mobilephone")){
			String mobile = approverObject.getString("mobilephone").replace(" ", "");
			userBean = dingUserDao.selectDetailByPhone(mobile);
		} else {
			return "请输入申请人手机号";
		}
		String userId = userBean.getUserId();
		if(userId == null){
			return "userId不能为空";
		}
		System.out.println("entityid申请人..." + userId);
		return userId;
	}
	/**
	 * 根据采购单信息获取申请人所在部门id
	 * @auth mark
	 * @param approverObject
	 * @return    
	 * @date 2017年9月5日 下午4:15:13
	 */
	public String deptIdGet(String department, int empId){
		String deptId = "";
		JSONObject deptObject = JSONObject.fromObject(department);
		String deptName = deptObject.getString("name");
	//	JSONObject emp = JSONObject.fromObject(approverObject.get("employee"));
	//	String internalId = emp.getString("internalid"); 
		NsEmployeeBean empBean = nsEmpDao.selectEmpById(empId);
	//	String mobile = approverObject.getString("mobilephone").replace(" ", "");
	//	NsEmpBean empBean = netSuiteEmpDao.selectEmpByPhone(mobile);
		String departmentName = empBean.getDeptName();
		System.out.println("部门名称:"+departmentName);
		String[] arrayName = deptName.split(":");
		String name = arrayName[arrayName.length - 1].replace(" ", "");
		System.out.println("name.."+name);
		int count = departmentDao.deptCount(name);
		if(count == 1){
			deptId = departmentDao.selectIdByName(name);
		}else if(count >1){
			String parentName = arrayName[arrayName.length - 2].replace(" ", "");
			List<DingDepartmentBean> repeatBean= departmentDao.selectByName(name);
			//根据父id查询其名称，与parentName比较，如果相等，则其当前部门id为该部门所要id
			for (DingDepartmentBean reBean : repeatBean) {
				String pName = departmentDao.selectNameById(reBean.getParentId());
				if(pName.equals(parentName)){
					deptId = reBean.getDepartmentId();
				}
			}
		}else{
			return "查找部门出错或不存在该部门";
		}
		return deptId;
	}
	/**
	 * 获取审批人id
	 * @auth mark
	 * @param approverObject
	 * @return    
	 * @date 2017年9月5日 下午4:44:01
	 */
	public String approverIdGet(JSONObject approverObject){
		List<String> approvers = new ArrayList<String>();
		String approverlist = "";
		String approverString = approverObject.getString("custbodyapprover");
		if(approverString.startsWith("{")){//单个
			JSONObject appObject = JSONObject.fromObject(approverString);
			// 通过NS员工id获取手机号码后，查询钉钉员工id，并添加到审批人列表
			int empId = appObject.getInt("internalid");
			String mobile = nsEmpDao.selectPhoneById(empId);
			DingEmployeeBean userBean = dingUserDao.selectDetailByPhone(mobile);
			approverlist = userBean.getUserId();
			System.out.println("审批人id列表1："+approverlist);
		}else if (approverString.startsWith("[")) {
			//多个
			JSONArray approverArray = JSONArray.fromObject(approverObject.getString("custbodyapprover"));
			for (int j = 0; j < approverArray.size(); j++) {
				JSONObject approver = JSONObject.fromObject(approverArray.get(j));
				int empId =approver.getInt("internalid");
				String mobile = nsEmpDao.selectPhoneById(empId);
		//		DingEmpBean userBean = dingUserDao.selectDetailByPhone(mobile);
				String uId = dingUserDao.selectIdByPhone(mobile);
				if(uId == null){
					return "查找不到待审人id，无法发起审批";
				}
		//		String userId = userBean.getUserId();
				approvers.add(uId);
				approverlist = StringUtils.collectionToDelimitedString(approvers, ",");
			}
			System.out.println("审批人id列表2："+approverlist);
		} else {
			return "审批人不存在！";
		}
		return approverlist;
	}

	/**
	 * 获取待审批id列表
	 * @auth mark
	 * @return  idList
	 * @date 2017年9月14日 上午11:39:22
	 */
	public List<String> getInternalIdList(Map accountMap, String url){
		List<String> idList = new ArrayList<>();
		String result = "";
		try {
			// 1.从netsuite获取待审批订单id列表
			result = NetSuiteAuth.getAuth(accountMap, url, null);
			System.out.println("netsuite返回结果"+result);
			if(result == null||result == ""){
				return idList;
			}
			JSONArray resultJson = JSONArray.fromObject(result);
			JSONObject colJson = new JSONObject();
			JSONObject internalIdJson = new JSONObject();
			for (int i = 0;i<resultJson.size();i++) {
				JSONObject getOne = JSONObject.fromObject(resultJson.get(i));
				colJson = JSONObject.fromObject(getOne.get("columns"));
				internalIdJson = JSONObject.fromObject(colJson.get("internalid"));
				String id = internalIdJson.getString("internalid");
				idList.add(id);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return idList;
	}
	

	/**
	 * 将采购单保存在本地
	 * @auth mark
	 * @param id
	 * @param detailJson
	 * @return    
	 * @date 2017年9月14日 下午2:32:38
	 */
	public String savePurchaseOrder(String id, JSONObject detailJson){
//		NsApprovalBean purchaseBean = new NsApprovalBean();
//		purchaseBean.setInternalId(id);
//		if(detailJson.has("custbodyapprover")){
//			purchaseBean.setApprover(detailJson.getString("custbodyapprover"));
//		}
//		purchaseBean.setTranId(detailJson.getString("tranid"));
//		purchaseBean.setTotal(detailJson.getDouble("total"));
//		purchaseBean.setSupplier(detailJson.getString("entity"));
//		purchaseBean.setGoods(detailJson.getString("item"));
//		if(detailJson.has("employee")){
//			JSONObject emp = JSONObject.fromObject(detailJson.getString("employee"));
//			purchaseBean.setApplicant(emp.getString("name"));
//		}
//		purchaseBean.setTaxtotal(detailJson.getDouble("taxtotal"));
//		if(detailJson.has("location")){
//			purchaseBean.setLocation(detailJson.getString("location"));
//		}
////		approveDao.insert(purchaseBean);
		return "";
	}
	/**
	 * 获取id->查询详细信息->保存本地->组装参数、发送审批
	 */
	@Override
	public String getPurchaseOrderDetail() {
		Map accountMap = nsService.getAccount();
		String result = "";
		String detail = "";
		String ret = "";
		String url = UserUtil.TEST_GROUP;
		String access_token = Constants.getAccessToken();
		String process_code = "PROC-424LSGUV-JM8OOE9UQ2KS2QH44NCP1-ETFVIF7J-1";
		try {
			// 1.从netsuite获取待审批订单
			List<String> idList = getInternalIdList(accountMap,url);
			System.out.println(idList);
			for (int i = 0; i < idList.size(); i++) {
				String id = idList.get(i);
				int iid = Integer.parseInt(id);
				String processId = approvalListDao.selectProcessIdById(iid);
				if (processId == null) {
					// 2.通过订单号获取订单详情
					detail = NetSuiteAuth.getAuth(accountMap, UserUtil.GET_DETAIL_ORDER, id);
					System.out.println("获取订单详情为：" + detail);
					JSONObject detailJson = JSONObject.fromObject(detail);
					// 3.将采购单保存在本地
					savePurchaseOrder(id, detailJson);
					// 4.准备向钉钉发送审批
					ret = purchaseDetailFormat(id, access_token, detailJson, process_code);
				} else {
					System.out.println("该订单已发起审批");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 * 使用明细后的审批格式
	 * @auth mark
	 * @param nsId
	 * @param access_token
	 * @param approverObject
	 * @param process_code
	 * @return    
	 * @date 2017年9月12日 下午5:18:13
	 */
	public String purchaseDetailFormat(String nsId, String access_token, JSONObject approverObject, String process_code){
		
		String url = "https://eco.taobao.com/router/rest?method=dingtalk.smartwork.bpms.processinstance.create&v=2.0&format=json&session="
				+ access_token;
		// 一、获取审批人
		String approverIdList = approverIdGet(approverObject);
		// 二、获取申请人id
		DingEmployeeBean userBean = new DingEmployeeBean();
		JSONObject emp = JSONObject.fromObject(approverObject.get("employee"));
		int empId = emp.getInt("internalid"); 
		String mobile = nsEmpDao.selectPhoneById(empId);
		userBean = dingUserDao.selectDetailByPhone(mobile);
		String userId = userBean.getUserId();
		if(userId == null){
			return "userId不能为空";
		}
		System.out.println("entityid申请人id..." + userId);
		//所在部门id
		String department = approverObject.getString("department");
		String deptId = deptIdGet(department,empId);
		System.out.println("申请人所在部门id："+deptId);
		
		
		// 三、组装明细参数
		String location = JSONObject.fromObject(approverObject.getString("location")).getString("name");
		String total = approverObject.getString("total");
		String taxTotal = approverObject.getString("taxtotal");
		String subTotal = approverObject.getString("subtotal");
		String tranDate = "";
		if(approverObject.has("trandate")){
			tranDate = approverObject.getString("trandate");
		}
		String entity = JSONObject.fromObject(approverObject.getString("entity")).getString("name");
    	JSONArray totalArray = new JSONArray();
    	//设置单行信息
    	JSONObject single1 = new JSONObject();
    	JSONObject single2 = new JSONObject();
    	JSONObject single3 = new JSONObject();
    	JSONObject single4 = new JSONObject();
    	JSONObject single5 = new JSONObject();
    	JSONObject single6 = new JSONObject();
    	single1.put("name", "门店");
    	single1.put("value", location);
		single2.put("name", "总金额");
		single2.put("value", total);
		single3.put("name", "总税率");
		single3.put("value", taxTotal);
		single4.put("name", "税后总额");
		single4.put("value", subTotal);
		single5.put("name", "截止日期");
		single5.put("value", tranDate);
		single6.put("name", "供应商");
		single6.put("value", entity);
		//1.创建表单信息对象
    	JSONObject detailed = new JSONObject();
    	JSONArray outerArray = new JSONArray();
    	
    	JSONArray itemArray = JSONArray.fromObject(approverObject.get("item"));
    	for(int i = 0; i < itemArray.size(); i++){
    		JSONArray innerArray = new JSONArray();
    		JSONObject innerObject1 = new JSONObject();
        	JSONObject innerObject2 = new JSONObject();
        	JSONObject innerObject3 = new JSONObject();
        	JSONObject innerObject4 = new JSONObject();
        	JSONObject innerObject5 = new JSONObject();
        	JSONObject itemObj = JSONObject.fromObject(itemArray.get(i));
        	JSONObject item = JSONObject.fromObject(itemObj.getString("item"));
        	String name = item.getString("name");
        	String rate = itemObj.getString("rate");
        	String quantity = itemObj.getString("quantity");
        	String amount = itemObj.getString("amount");
        	String taxrate1 = itemObj.getString("taxrate1");
        	//2.明细单行设值
        	innerObject1.put("name", "名称");
        	innerObject1.put("value", name);
        	innerObject2.put("name", "单价");
        	innerObject2.put("value", rate);
        	innerObject3.put("name", "数量");
        	innerObject3.put("value", quantity);
        	innerObject4.put("name", "税率");
        	innerObject4.put("value", taxrate1);
        	innerObject5.put("name", "总价");
        	innerObject5.put("value", amount);
        	//3.把明细单行添加到数组
        	innerArray.add(innerObject1);
        	innerArray.add(innerObject2);
        	innerArray.add(innerObject3);
        	innerArray.add(innerObject4);
        	innerArray.add(innerObject5);
        	//4.组装第几个明细
        	outerArray.add(innerArray);
    	}
    	System.out.println(outerArray);
    	//5.把组装好的值添加到明细对象
    	detailed.put("name", "明细");
    	detailed.put("value", outerArray);
    	//6.把明细和单行添加到整个表单值
    	totalArray.add(single1);
    	totalArray.add(single2);
    	totalArray.add(single3);
    	totalArray.add(single4);
    	totalArray.add(single5);
    	totalArray.add(single6);
    	totalArray.add(detailed);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("process_code", process_code));
		params.add(new BasicNameValuePair("originator_user_id", userId));
		params.add(new BasicNameValuePair("dept_id", deptId));
		params.add(new BasicNameValuePair("approvers", approverIdList));
		params.add(new BasicNameValuePair("form_component_values", totalArray.toString()));
		// 四、发起审批实例
		System.out.println("-------");
		System.out.println(nsId);
		System.out.println(url);
		System.out.println(params);
		processPost(nsId, url, params);
		
		return "";
	}

	public String expenseDetailFormat(String nsId, String access_token, JSONObject approverObject, String process_code){
		
		String url = "https://eco.taobao.com/router/rest?method=dingtalk.smartwork.bpms.processinstance.create&v=2.0&format=json&session="
				+ access_token;
//		// 获取审批人
//		String approverlist = approverIdGet(approverObject);
//		//获取申请人id
//		DingEmpBean userBean = new DingEmpBean();
//		JSONObject emp = JSONObject.fromObject(approverObject.get("entity"));
//		String internalId = emp.getString("internalid"); 
//		String mobile = netSuiteEmpDao.selectPhoneById(internalId);
//		userBean = dingUserDao.selectDetailByPhone(mobile);
//		String userId = userBean.getUserId();
//		if(userId == null){
//			return "userId不能为空";
//		}
//		System.out.println("entityid申请人id..." + userId);
//		//所在部门id
//		String deptId = deptIdGet(approverObject);
//		System.out.println("申请人所在部门id："+deptId);
		
		String approverIdList = "manager4417,manager4417,manager4417";
		String userId = "manager4417";
		String deptId = "45628314";
		
		String startDate = approverObject.getString("trandate");
		String endDate = approverObject.getString("duedate");
		String total = approverObject.getString("total");
		String reimBursAble = approverObject.getString("reimbursable");
		String advance = approverObject.getString("advance2");
		String amountTotal = approverObject.getString("amount");
		
		String entity = JSONObject.fromObject(approverObject.getString("entity")).getString("name");
		//1.创建表单信息对象
		JSONArray totalArray = new JSONArray();
    	//2.设置单行信息
    	JSONObject single1 = new JSONObject();
    	JSONObject single2 = new JSONObject();
    	JSONObject single3 = new JSONObject();
    	JSONObject single4 = new JSONObject();
    	JSONObject single5 = new JSONObject();
    	JSONObject single6 = new JSONObject();
    	JSONObject single7 = new JSONObject();
    	single1.put("name", "发起日期");
    	single1.put("value", startDate);
		single2.put("name", "到期日期");
		single2.put("value", endDate);
		single3.put("name", "费用总计");
		single3.put("value", total);
//		single4.put("name", "无偿开支");
//		single4.put("value", "");
//		single5.put("name", "有补偿费用");
//		single5.put("value", reimBursAble);
//		single6.put("name", "任意预付款");
//		single6.put("value", advance);
//		single7.put("name", "有补偿总额");
//		single7.put("value", amountTotal);
		//3.创建明细对象
    	JSONObject detailed = new JSONObject();
    	JSONArray outerArray = new JSONArray();
    	JSONArray itemArray = JSONArray.fromObject(approverObject.get("expense"));
    	for(int i = 0; i < itemArray.size(); i++){
    		JSONArray innerArray = new JSONArray();
    		JSONObject innerObject1 = new JSONObject();
        	JSONObject innerObject2 = new JSONObject();
        	JSONObject innerObject3 = new JSONObject();
        	JSONObject innerObject4 = new JSONObject();
        	JSONObject innerObject5 = new JSONObject();
        	JSONObject innerObject6 = new JSONObject();
        	JSONObject innerObject7 = new JSONObject();
        	JSONObject innerObject8 = new JSONObject();
        //	JSONObject innerObject9 = new JSONObject();
        	JSONObject itemObj = JSONObject.fromObject(itemArray.get(i));
       // 	JSONObject item = JSONObject.fromObject(itemObj.getString("expense"));
        	String category = JSONObject.fromObject(itemObj.getString("category")).getString("name");
        	String amount = itemObj.getString("amount");
        	String taxcode = JSONObject.fromObject(itemObj.getString("taxcode")).getString("name");
        	String taxrate1 = itemObj.getString("taxrate1");
        	String tax1amt = itemObj.getString("tax1amt");
        	String grossamt = itemObj.getString("grossamt");
        	String location = JSONObject.fromObject(itemObj.getString("location")).getString("name");
        	if(itemObj.has("memo")){
        		String memo = itemObj.getString("memo");
        		innerObject7.put("name", "备注");
            	innerObject7.put("value", memo);
        	}
        	
        	//4.明细单行设值
        	innerObject1.put("name", "类别");
        	innerObject1.put("value", category);
        	innerObject2.put("name", "金额");
        	innerObject2.put("value", amount);
        	innerObject3.put("name", "税类代码");
        	innerObject3.put("value", taxcode);
        	innerObject4.put("name", "税率");
        	innerObject4.put("value", taxrate1);
        	innerObject5.put("name", "税额");
        	innerObject5.put("value", tax1amt);
        	innerObject6.put("name", "毛额");
        	innerObject6.put("value", grossamt);
        	//innerObject7.put("name", "备注");
        	innerObject8.put("name", "门店");
        	innerObject8.put("value", location);
        	//5.把明细单行添加到数组
        	innerArray.add(innerObject1);
        	innerArray.add(innerObject2);
        	innerArray.add(innerObject3);
        	innerArray.add(innerObject4);
        	innerArray.add(innerObject5);
        	innerArray.add(innerObject6);
        	innerArray.add(innerObject7);
        	innerArray.add(innerObject8);
        	//6.把第i个明细添加到内部明细数组
        	outerArray.add(innerArray);
    	}
    	//7.把内部明细数组的值添加到外部明细对象
    	detailed.put("name", "明细");
    	detailed.put("value", outerArray);
    	//8.把明细和单行添加到整个表单值
    	totalArray.add(single1);
    	totalArray.add(single2);
    	totalArray.add(single3);
    	totalArray.add(single4);
    	totalArray.add(single5);
    	totalArray.add(single6);
    	totalArray.add(single7);
    	totalArray.add(detailed);
    	
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("process_code", process_code));
		params.add(new BasicNameValuePair("originator_user_id", userId));
		params.add(new BasicNameValuePair("dept_id", deptId));
		params.add(new BasicNameValuePair("approvers", approverIdList));
		params.add(new BasicNameValuePair("form_component_values", totalArray.toString()));
		
		// 3.发起审批实例
		System.out.println("-------");
		System.out.println(nsId);
		System.out.println(url);
		System.out.println(params);
		String info = processPost(nsId, url, params);
		
		return info;
	}


	@Override
	public String getExpenseList() {
		Map accountMap = nsService.getAccount();
		String processCode = "PROC-GTHKCO8W-4MCN48TKOW9ZT0BK7NAF2-6XF1B76J-R";
		String url = UserUtil.GET_EXENSEID_LIST;
		String token = Constants.getAccessToken();
		String detail = "";
		
		// 1.从netsuite获取待报销订单
		List<String> idList = getInternalIdList(accountMap,url);
		if(idList == null||idList == null){
			return "查询为空";
		}
		for(int i = 0;i < idList.size();i++){
			String id = idList.get(i);
			// 2.通过订单号获取订单详情
			try {
				detail = NetSuiteAuth.getAuth(accountMap, UserUtil.EXENSEID_DETAIL_GET, id);
				System.out.println("获取详情为：" + detail);
				JSONObject detailJson = JSONObject.fromObject(detail);
				// 3.将采购单保存在本地
			//	savePurchaseOrder(id, detailJson);
				// 4.准备向钉钉发送审批
				String ret = expenseDetailFormat(id, token, detailJson, processCode);
				System.out.println("发起审批返回结果："+ret);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		
		}
		return detail;
	}


	@Override
	public String getExpenseDetail() {
		Map accountMap = nsService.getAccount();
		String processCode = "PROC-GTHKCO8W-4MCN48TKOW9ZT0BK7NAF2-6XF1B76J-R";
		String url = UserUtil.EXENSEID_REPORT_GET;
		String token = Constants.getAccessToken();
		
		// 1.从netsuite获取待报销订单
		String result = "";
		try {
			result = NetSuiteAuth.getAuth(accountMap, url, null);
			if(result == null||result ==""){
				return "查询为空";
			}
			JSONArray resultArray = JSONArray.fromObject(result);
			for(int i = 0;i < resultArray.size();i++){
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
//		for(int i = 0;i < idList.size();i++){
//			String id = idList.get(i);
//			// 2.通过订单号获取订单详情
//			try {
//				detail = NetSuiteAuth.getAuth(accountMap, UserUtil.EXENSEID_DETAIL_GET, id);
//				System.out.println("获取详情为：" + detail);
//				JSONObject detailJson = JSONObject.fromObject(detail);
//				// 3.将采购单保存在本地
//			//	savePurchaseOrder(id, detailJson);
//				// 4.准备向钉钉发送审批
//				String ret = expenseDetailFormat(id, token, detailJson, processCode);
//				System.out.println("发起审批返回结果："+ret);
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (URISyntaxException e) {
//				e.printStackTrace();
//			}
//		}
		return result;
	}

}
