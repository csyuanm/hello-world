package com.zing.dingding.service.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.zing.dingding.common.DingDingAuth;
import com.zing.dingding.common.NetSuiteAuth;
import com.zing.dingding.common.UserUtil;
import com.zing.dingding.controller.DingEmpController;
import com.zing.dingding.dao.DingDepartmentDao;
import com.zing.dingding.dao.DingEmployeeDao;
import com.zing.dingding.dao.NsEmployeeDao;
import com.zing.dingding.dao.NsApprovalListDao;
import com.zing.dingding.dao.NsApproverListDao;
import com.zing.dingding.dao.NsExpenseDetailDao;
import com.zing.dingding.dao.NsGoodsDetailDao;
import com.zing.dingding.model.DingDepartmentBean;
import com.zing.dingding.model.NsApprovalListBean;
import com.zing.dingding.model.NsApproverListBean;
import com.zing.dingding.model.NsExpenseDetailBean;
import com.zing.dingding.model.NsGoodsDetailBean;
import com.zing.dingding.service.INsApprovalService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@SuppressWarnings("all")
@Service
public class NsApprovalServiceImpl implements INsApprovalService {
	
	//private static Logger LOGGER = LogManager.getLogger(DingEmpController.class);

	@Autowired
	NsGoodsDetailDao goodsDetailDao;
	
	@Autowired
	NsExpenseDetailDao expenseDetailDao;
	
	@Autowired
	NsApprovalListDao approvalListDao;
	
	@Autowired
	DingEmployeeDao dingUserDao;
	
	@Autowired
	DingDepartmentDao departmentDao;
	
	@Autowired
	NsApproverListDao approverDao;
	
	@Autowired
	NsEmployeeDao nsEmpDao;
	
	@Override
	public Map getAccount() {
		
		Map accountMap = new HashMap();
		accountMap.put("account", UserUtil.NS_COUNNET); // compayid=4556831
		accountMap.put("email", UserUtil.NS_EMAIL); // 登录email
		accountMap.put("password", UserUtil.NS_PASS);// 密码
		accountMap.put("role", UserUtil.NS_ROLE); // roleid 默认填写18
		
		return accountMap;
	}
	
	public String deptIdGet(String deptName){
		String deptId = "";
		String[] arrayName = deptName.split(":");
		String name = arrayName[arrayName.length - 1].replace(" ", "");
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
	public String processPost(int nsId, String url, List<? extends NameValuePair> params){
		// 发起审批实例
		try {
			String str = DingDingAuth.postNoJson(url, params);
			JSONObject resultOne = JSONObject.fromObject(str);
			JSONObject resultTwo = JSONObject
					.fromObject(resultOne.get("dingtalk_smartwork_bpms_processinstance_create_response"));
			JSONObject resultThree = JSONObject.fromObject(resultTwo.get("result"));
			// 4.将返回的id写入数据库
			String processId = resultThree.getString("process_instance_id");
		//	purchaseDao.updateInstanceId(processId, nsId);
			approvalListDao.updateProcessId(nsId, processId);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return "发起审批实例成功...";
	}

	@Override
	public String purchaseOrderApproval(Integer id, String token) {
		String url = "https://eco.taobao.com/router/rest?method=dingtalk.smartwork.bpms.processinstance.create&v=2.0&format=json&session="
				+ token;
		String processCode = "PROC-424LSGUV-JM8OOE9UQ2KS2QH44NCP1-ETFVIF7J-1";
		NsApprovalListBean approvalInfo = new NsApprovalListBean();
		List<NsGoodsDetailBean> goodsDetailList = new ArrayList<NsGoodsDetailBean>();
		//获取申请人id,部门id
		approvalInfo = approvalListDao.getApprovalInfo(id);
		String mobile = approvalInfo.getMobile();
		Double total = approvalInfo.getTotal();
		String userId = dingUserDao.selectIdByPhone(mobile);
		String deptId = deptIdGet(approvalInfo.getDepartment());
		//获取审批人
		String approverIdList = approversIdListGet("采购审批人");
		String approverNameList = approverNameListGet("采购审批人");
		approvalListDao.updateApprovers(id, approverNameList);
		
		//设置表单参数
		JSONArray totalArray = new JSONArray();
    	//设置单行信息
    	JSONObject single1 = new JSONObject();
    	JSONObject single2 = new JSONObject();
    	JSONObject single3 = new JSONObject();
    	JSONObject single4 = new JSONObject();
    	JSONObject single5 = new JSONObject();
    	single1.put("name", "供应商");
    	single1.put("value", approvalInfo.getSupplier());
		single2.put("name", "供应商代码");
		single2.put("value", "暂无");
		single3.put("name", "采购总额");
		single3.put("value", approvalInfo.getTotal());
		System.out.println(approvalInfo.getRemark());
		if(approvalInfo.getRemark() != null){
			//当为空时会造成数据异常
			single4.put("name", "备注");
			single4.put("value", approvalInfo.getRemark());
			totalArray.add(single4);
		}
		single5.put("name", "发起日期");
		single5.put("value", approvalInfo.getStartTime());
		//1.创建表单信息对象
    	JSONObject detailed = new JSONObject();
    	JSONArray outerArray = new JSONArray();
    	goodsDetailList = goodsDetailDao.getGoodsDetailList(id);
    	for(int i = 0; i < goodsDetailList.size(); i++){
    		JSONArray innerArray = new JSONArray();
    		JSONObject innerObject1 = new JSONObject();
        	JSONObject innerObject2 = new JSONObject();
        	JSONObject innerObject3 = new JSONObject();
        	JSONObject innerObject4 = new JSONObject();
        	JSONObject innerObject5 = new JSONObject();
        	NsGoodsDetailBean goodsDetail = goodsDetailList.get(i);
        	//2.明细单行设值
        	innerObject1.put("name", "商品代码");
        	innerObject1.put("value", goodsDetail.getGoodsCode());
        	innerObject2.put("name", "商品名称");
        	innerObject2.put("value", goodsDetail.getGoodsName());
        	innerObject3.put("name", "数量");
        	innerObject3.put("value", goodsDetail.getQuantity());
        	innerObject4.put("name", "单价");
        	innerObject4.put("value", goodsDetail.getRate());
        	innerObject5.put("name", "总价");
        	innerObject5.put("value", goodsDetail.getTotal());
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
    	totalArray.add(single5);
    	totalArray.add(detailed);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("process_code", processCode));
		params.add(new BasicNameValuePair("originator_user_id", userId));
		params.add(new BasicNameValuePair("dept_id", deptId));
		params.add(new BasicNameValuePair("approvers", approverIdList));
		params.add(new BasicNameValuePair("form_component_values", totalArray.toString()));
		// 四、发起审批实例
		System.out.println(params);
		String info = processPost(id, url, params);
		return "";
	}
	
	@Override
	public String expenseReportApproval(Integer id, String token) {
		String url = "https://eco.taobao.com/router/rest?method=dingtalk.smartwork.bpms.processinstance.create&v=2.0&format=json&session="
				+ token;
		String processCode = "PROC-GTHKCO8W-4MCN48TKOW9ZT0BK7NAF2-6XF1B76J-R";
		final String imgUrl = "https://system2.na2.netsuite.com";
		NsApprovalListBean approvalInfo = new NsApprovalListBean();
		List<NsExpenseDetailBean> expenseDetailList = new ArrayList<NsExpenseDetailBean>();
		//获取申请人id,部门id
		approvalInfo = approvalListDao.getApprovalInfo(id);
		String mobile = approvalInfo.getMobile();
		String userId = dingUserDao.selectIdByPhone(mobile);
		String deptName = approvalInfo.getDepartment();
		String deptId = deptIdGet(deptName);
		//获取审批人id列表
		String approverIdList = approverSort(approvalInfo, id);
		approverIdList = approverIdList.replace(userId, "");
		
		//设置表单参数
		JSONArray totalArray = new JSONArray();
    	//设置单行信息
    	JSONObject single1 = new JSONObject();
    	JSONObject single2 = new JSONObject();
    	JSONObject single3 = new JSONObject();
    	single1.put("name", "发起日期");
    	single1.put("value", approvalInfo.getStartTime());
		single2.put("name", "到期日期");
		single2.put("value", approvalInfo.getEndTime());
		single3.put("name", "总金额");
		single3.put("value", approvalInfo.getTotal());
		//1.创建表单信息对象
    	JSONObject detailed = new JSONObject();
    	JSONArray outerArray = new JSONArray();
    	expenseDetailList = expenseDetailDao.getExpenseDetailList(id);
    	for(int i = 0; i < expenseDetailList.size(); i++){
    		JSONArray innerArray = new JSONArray();
    		JSONObject innerObject1 = new JSONObject();
        	JSONObject innerObject2 = new JSONObject();
        	JSONObject innerObject3 = new JSONObject();
        	JSONObject innerObject4 = new JSONObject();
        	JSONObject innerObject5 = new JSONObject();
        	JSONObject innerObject6 = new JSONObject();
        	NsExpenseDetailBean expenseDetail = expenseDetailList.get(i);
        	//2.明细单行设值
        	innerObject1.put("name", "开始时间");
        	innerObject1.put("value", expenseDetail.getStartTime());
        	innerObject2.put("name", "结束时间");
        	innerObject2.put("value", expenseDetail.getEndTime());
        	innerObject3.put("name", "类别");
        	innerObject3.put("value", expenseDetail.getCategory());
        	innerObject4.put("name", "金额");
        	innerObject4.put("value", expenseDetail.getAmount());
        	if(expenseDetail.getRemark() != null){
        		innerObject5.put("name", "备注");
            	innerObject5.put("value", expenseDetail.getRemark());
        	}
        	if(expenseDetail.getImgUrl() != null){
            	innerObject6.put("name", "图片");
            	JSONArray imgArray = new JSONArray();
            	String  img = imgUrl+expenseDetail.getImgUrl();
            	imgArray.add(img);
            	innerObject6.put("value", imgArray);
        	}
        	//3.把明细单行添加到数组
        	innerArray.add(innerObject1);
        	innerArray.add(innerObject2);
        	innerArray.add(innerObject3);
        	innerArray.add(innerObject4);
        	innerArray.add(innerObject5);
        	innerArray.add(innerObject6);
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
    	totalArray.add(detailed);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("process_code", processCode));
		params.add(new BasicNameValuePair("originator_user_id", userId));
		params.add(new BasicNameValuePair("dept_id", deptId));
		params.add(new BasicNameValuePair("approvers", approverIdList));
		params.add(new BasicNameValuePair("form_component_values", totalArray.toString()));
		// 四、发起审批实例
		System.out.println(params);
		String info = processPost(id, url, params);
	//	LOGGER.info(info);
		return "";
	}

	@Override
	public String saveGoodsDetail(JSONObject detail, int id) {
		String goodsName = detail.getString("displayname");
		int count = goodsDetailDao.checkGoodsRepeat(id, goodsName);
		if(count == 0){
			NsGoodsDetailBean goodsDetail = new NsGoodsDetailBean();
			goodsDetail.setInternalId(id);
			goodsDetail.setPoNumber(detail.getString("tranid"));
			goodsDetail.setGoodsName(detail.getString("displayname"));
			goodsDetail.setGoodsCode(JSONObject.fromObject(detail.getString("item")).getString("name"));
			goodsDetail.setQuantity(detail.getInt("quantity"));
			goodsDetail.setRate(detail.getDouble("rate"));
			goodsDetail.setTotal(detail.getDouble("amount"));
			goodsDetailDao.insert(goodsDetail);
		}
		return "";
	}
	
	@Override
	public String saveExpenseDetail(JSONObject detail, int id){
		String category = detail.getString("category");
		int count = expenseDetailDao.checkExpenseRepeat(id, category);
		if(count == 0){
			NsExpenseDetailBean expenseDetail = new NsExpenseDetailBean();
			expenseDetail.setInternalId(id);
			expenseDetail.setCategory(JSONObject.fromObject(detail.getString("expensecategory")).getString("name"));
			expenseDetail.setAmount(detail.getDouble("amount"));
			expenseDetail.setStartTime(detail.getString("expensedate"));
			if(detail.has("custcolend_date")){
				expenseDetail.setEndTime(detail.getString("custcolend_date"));
			}
			String memo = detail.getString("memo");
			if(memo.contains(":")){
				String[] remark = memo.split(":");
				expenseDetail.setRemark(remark[1]);
			}
			if(detail.has("url")){
				expenseDetail.setImgUrl(detail.getString("url"));
			}
			expenseDetailDao.insert(expenseDetail);
		}
		
		return "";
	}

	@Override
	public String saveApprovalList(JSONObject detail, int id, String type) {
		if(detail.has("mobilephone")){
			NsApprovalListBean approvalList = new NsApprovalListBean();
			approvalList.setInternalId(id);
			approvalList.setType(type);
			approvalList.setNumber(detail.getString("tranid"));
			String jobNumber = detail.getString("entityid");
			String applicantName = nsEmpDao.selectNameByJobNumber(jobNumber);
			approvalList.setApplicant(applicantName);
			approvalList.setApplicantId(JSONObject.fromObject(detail.get("internalid")).getInt("name"));
			approvalList.setDepartment(JSONObject.fromObject(detail.get("department")).getString("name"));
			approvalList.setMobile(detail.getString("mobilephone").replace(" ", ""));
			approvalList.setTotal(detail.getDouble("formulatext"));
			
			approvalList.setStartTime(detail.getString("trandate").replace("/", "-"));
			if(detail.has("duedate")){
				approvalList.setEndTime(detail.getString("duedate").replace("/", "-"));
			}
			if(detail.has("altname")){
				approvalList.setSupplier(detail.getString("altname"));
			}
			if(detail.has("memomain")){
				approvalList.setRemark(detail.getString("memomain"));
			}
			approvalListDao.insert(approvalList);
		}
		return null;
	}
	
	public String approverSort(NsApprovalListBean approvalInfo, int id) {
		String approverIdList = "";
		String approverNameList = "";
		String deptName = approvalInfo.getDepartment();
		String position = nsEmpDao.selectPositionById(approvalInfo.getApplicantId());
		String aPostion = "费用会计";
		String expenseAccountName = nsEmpDao.selectNameByPosition(position);
		String vPosition = "副总经理";
		String vicePresidentName = nsEmpDao.selectNameByPosition(vPosition);
		Double total = approvalInfo.getTotal();
		String[] deptArray = deptName.split(":");
		int length = deptArray.length;
		if (length > 0) {
			String firstDeptName = deptArray[0];
			// String[] b= Arrays.copyOf(a,a.length-1);
			// org.apache.commons.lang.StringUtils.join(deptArray)+str;
			//财务部或者总监都需要到总经理
			if ("财务部".equals(firstDeptName) || position.indexOf("总监") != -1 || position.indexOf("总经理") != -1) {
				firstDeptName = firstDeptName + "%";
				List<NsApproverListBean> approverList = approverDao.selectListByName(firstDeptName.replace(" ", ""));
				for (NsApproverListBean approver : approverList) {
					String firstApproverName = approver.getFirstApprover();
					if (position.indexOf("总监") != -1 || position.indexOf("总经理") != -1) {
						//总监级别的第一个审批人为会计
						if (expenseAccountName.equals(firstApproverName)) {
							String name = approver.getName();
							approverIdList = approversIdListGet(name);
							approverNameList = approverNameListGet(name);
							approvalListDao.updateApprovers(id, approverNameList);
						}
						// 跳出当前最近的循环
						break; 
					} else {
						String supervisor = nsEmpDao.selectSuperById(approvalInfo.getApplicantId());
						if (supervisor.equals(firstApproverName)) {
							String name = approver.getName();
							approverIdList = approversIdListGet(name);
							approverNameList = approverNameListGet(name);
							approvalListDao.updateApprovers(id, approverNameList);
						}
						break;
					}
				}
			} else if ("采购部".equals(firstDeptName) || "物流部".equals(firstDeptName) || "研发部".equals(firstDeptName)
					|| "IT及品类管理部".equals(firstDeptName) || "市场部".equals(firstDeptName)) {
				firstDeptName = firstDeptName + "%";
				List<NsApproverListBean> approverList = approverDao.selectListByName(firstDeptName.replace(" ", ""));
				for (NsApproverListBean approver : approverList) {
					String firstApproverName = approver.getFirstApprover();
					if(position.indexOf("经理") != -1) {
						String supervisor = nsEmpDao.selectSuperById(approvalInfo.getApplicantId());
						if(supervisor != null){
							if (supervisor.equals(firstApproverName)) {
								String name = approver.getName();
								String approverId = approversIdListGet(name);
								String approverName = approverNameListGet(name);
								approvalListDao.updateApprovers(id, approverNameList);
								approverIdList = approverIdGet(approverId, approverName, total, id);
							} else if(supervisor.equals(vicePresidentName)){
								if (vicePresidentName.equals(firstApproverName)) {
									String name = approver.getName();
									approverIdList = approversIdListGet(name);
									approverNameList = approverNameListGet(name);
									approvalListDao.updateApprovers(id, approverNameList);
								}
							}
							break;
						}
					} else {
						String supervisor = nsEmpDao.selectSuperById(approvalInfo.getApplicantId());
						if (supervisor.equals(firstApproverName)) {
							String name = approver.getName();
							String approverId = approversIdListGet(name);
							String approverName = approverNameListGet(name);
							approvalListDao.updateApprovers(id, approverNameList);
							approverIdList = approverIdGet(approverId, approverName, total, id);
						}
						break;
					}
				}
			} else {
				firstDeptName = firstDeptName + "%";
				List<NsApproverListBean> approverList = approverDao.selectListByName(firstDeptName.replace(" ", ""));
				for (NsApproverListBean approver : approverList) {
					String firstApproverName = approver.getFirstApprover();
					if(position.indexOf("经理") != -1) {
						String supervisor = nsEmpDao.selectSuperById(approvalInfo.getApplicantId());
						if(supervisor != null){
							if (supervisor.equals(firstApproverName)) {
								String name = approver.getName();
								String approverId = approversIdListGet(name);
								String approverName = approverNameListGet(name);
								approvalListDao.updateApprovers(id, approverNameList);
								approverIdList = approverIdGet(approverId, approverName, total, id);
							} else if(supervisor.equals(vicePresidentName)){
								if (vicePresidentName.equals(firstApproverName)) {
									String name = approver.getName();
									approverIdList = approversIdListGet(name);
									approverNameList = approverNameListGet(name);
									approvalListDao.updateApprovers(id, approverNameList);
								}
							}
							break;
						}
					} else {
						String supervisor = nsEmpDao.selectSuperById(approvalInfo.getApplicantId());
						if (supervisor.equals(firstApproverName)) {
							String name = approver.getName();
							String approverId = approversIdListGet(name);
							String approverName = approverNameListGet(name);
							approverIdList = approverIdGet(approverId, approverName, total, id);
						}
						break;
					}
				}
			}
		}
		return approverIdList;
	}
	
	
	public String approverIdGet(String approverId, String approverName, Double total, int id){
		String approverIdList = null;
		String approverNameList = null;
		String[] idList = approverIdList.split(",");
		String[] nameList = approverNameList.split(",");
		if (500 >= total) {
			String[] idList2 = Arrays.copyOfRange(idList, 0, idList.length - 2);
			String[] nameList2 = Arrays.copyOfRange(nameList, 0, idList.length - 2);
			approverIdList = StringUtils.arrayToDelimitedString(idList2, ",");
			approverNameList = StringUtils.arrayToDelimitedString(nameList2, ",");
		} else if (2000 > total) {
			String[] idList2 = Arrays.copyOfRange(idList, 0, idList.length - 1);
			String[] nameList2 = Arrays.copyOfRange(nameList, 0, idList.length - 1);
			approverIdList = StringUtils.arrayToDelimitedString(idList2, ",");
			approverNameList = StringUtils.arrayToDelimitedString(nameList2, ",");
		}
		approvalListDao.updateApprovers(id, approverNameList);
		return approverIdList;
	}
	
	@Override
	public String approversIdListGet(String name){
		String approversIdList = "";
		List approverIdList = new ArrayList();
		NsApproverListBean approver = new NsApproverListBean();
		approver = approverDao.selectByName(name);
		Integer firstId = approver.getFirstId();
		if(firstId != null){
			String firstMobile = nsEmpDao.selectPhoneById(firstId);
			String firstUserId = dingUserDao.selectIdByPhone(firstMobile);
			approverIdList.add(firstUserId);
		}
		Integer secondId = approver.getSecondId();
		if(secondId != null){
			String secondMobile = nsEmpDao.selectPhoneById(secondId);
			String secondUserId = dingUserDao.selectIdByPhone(secondMobile);
			approverIdList.add(secondUserId);
		}
		Integer thirdId = approver.getThirdId();
		if(thirdId != null){
			String thirdMobile = nsEmpDao.selectPhoneById(thirdId);
			String thirdUserId = dingUserDao.selectIdByPhone(thirdMobile);
			approverIdList.add(thirdUserId);
		}
		Integer fourthId = approver.getFourthId();
		if(fourthId != null){
			String fourthMobile = nsEmpDao.selectPhoneById(fourthId);
			String fourthUserId = dingUserDao.selectIdByPhone(fourthMobile);
			approverIdList.add(fourthUserId);
		}
		Integer fifthId = approver.getFifthId();
		if(fifthId != null){
			String fifthMobile = nsEmpDao.selectPhoneById(fifthId);
			String fifthUserId = dingUserDao.selectIdByPhone(fifthMobile);
			approverIdList.add(fifthUserId);
		}
		Integer sixId = approver.getSixId();
		if(sixId != null){
			String sixMobile = nsEmpDao.selectPhoneById(sixId);
			String sixUserId = dingUserDao.selectIdByPhone(sixMobile);
			approverIdList.add(sixUserId);
		}
		Set idSet = new LinkedHashSet();
		idSet.addAll(approverIdList);
		List list = new ArrayList();
		list.addAll(idSet);
		approversIdList = StringUtils.collectionToDelimitedString(list, ",");
		return approversIdList;
	}
	
	@Override
	public String approverNameListGet(String name){
		String approversNameList = "";
		List approverNameList = new ArrayList();
		NsApproverListBean approver = new NsApproverListBean();
		approver = approverDao.selectByName(name);
		if(approver.getFirstApprover() != null){
			approverNameList.add(approver.getFirstApprover());
		}
		if(approver.getSecondApprover() != null){
			approverNameList.add(approver.getSecondApprover());
		}
		if(approver.getThirdApprover() != null){
			approverNameList.add(approver.getThirdApprover());
		}
		if(approver.getFourthApprover() != null){
			approverNameList.add(approver.getFourthApprover());
		}
		if(approver.getFifthApprover() != null){
			approverNameList.add(approver.getFifthApprover());
		}
		if(approver.getSixApprover() != null){
			approverNameList.add(approver.getSixApprover());
		}
		Set idSet = new LinkedHashSet();
		idSet.addAll(approverNameList);
		List list = new ArrayList();
		list.addAll(idSet);
		approversNameList = StringUtils.collectionToDelimitedString(list, ",");
		return approversNameList;
	}

	@Override
	public Set idSetGet(JSONArray resultArray) {
		List idList = new ArrayList();
		for(int i = 0;i < resultArray.size();i++){
			JSONObject purchaseObject = JSONObject.fromObject(resultArray.get(i));
			int id = purchaseObject.getInt("id");
			idList.add(id);
		}
		Set idSet = new LinkedHashSet();
		idSet.addAll(idList);
		return idSet;
	}

	/**
	 * 保存审批人类型在本地
	 */
	@Override
	public String saveApprover() {
		String result = "";
		String url = UserUtil.APPROVER_LIST_GET;
		try {
			result = NetSuiteAuth.getAuth(getAccount(), url, null);
			JSONArray resultArray = JSONArray.fromObject(result);
			System.out.println(resultArray);
			for(int i = 0;i < resultArray.size();i++){
				NsApproverListBean approverBean = new NsApproverListBean();
				JSONObject approverObj = JSONObject.fromObject(resultArray.get(i));
				JSONObject approverDetail = JSONObject.fromObject(approverObj.get("columns"));
				approverBean.setNsInternalid(approverObj.getInt("id"));
				approverBean.setName(approverDetail.getString("name"));
				if(approverDetail.has("custrecordapprover1")){
					approverBean.setFirstId(JSONObject.fromObject(approverDetail.getString("custrecordapprover1")).getInt("internalid"));
					String jobNumber = JSONObject.fromObject(approverDetail.get("custrecordapprover1")).getString("name");
					String firstName = nsEmpDao.selectNameByJobNumber(jobNumber);
					approverBean.setFirstApprover(firstName);
				}
				if(approverDetail.has("custrecordapprover2")){
					approverBean.setSecondId(JSONObject.fromObject(approverDetail.getString("custrecordapprover2")).getInt("internalid"));
					String jobNumber = JSONObject.fromObject(approverDetail.get("custrecordapprover2")).getString("name");
					String secondName = nsEmpDao.selectNameByJobNumber(jobNumber);
					approverBean.setSecondApprover(secondName);
				}
				if(approverDetail.has("custrecordapprover3")){
					approverBean.setThirdId(JSONObject.fromObject(approverDetail.getString("custrecordapprover3")).getInt("internalid"));
					String jobNumber = JSONObject.fromObject(approverDetail.get("custrecordapprover3")).getString("name");
					String thirdName = nsEmpDao.selectNameByJobNumber(jobNumber);
					approverBean.setThirdApprover(thirdName);
				}
				if(approverDetail.has("custrecordapprover4")){
					approverBean.setFourthId(JSONObject.fromObject(approverDetail.getString("custrecordapprover4")).getInt("internalid"));
					String jobNumber = JSONObject.fromObject(approverDetail.get("custrecordapprover4")).getString("name");
					String fourthName = nsEmpDao.selectNameByJobNumber(jobNumber);
					approverBean.setFourthApprover(fourthName);
				}
				if(approverDetail.has("custrecordapprover5")){
					approverBean.setFifthId(JSONObject.fromObject(approverDetail.getString("custrecordapprover5")).getInt("internalid"));
					String jobNumber = JSONObject.fromObject(approverDetail.get("custrecordapprover5")).getString("name");
					String fifthName = nsEmpDao.selectNameByJobNumber(jobNumber);
					approverBean.setFifthApprover(fifthName);
				}
				if(approverDetail.has("custrecordapprover6")){
					approverBean.setSixId(JSONObject.fromObject(approverDetail.getString("custrecordapprover6")).getInt("internalid"));
					String jobNumber = JSONObject.fromObject(approverDetail.get("custrecordapprover1")).getString("name");
					String sixName = nsEmpDao.selectNameByJobNumber(jobNumber);
					approverBean.setSixApprover(sixName);
				}
				approverDao.insert(approverBean);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String processIdGet(int id) {
		
		return approvalListDao.selectProcessIdById(id);
	}
	
	
	
}
