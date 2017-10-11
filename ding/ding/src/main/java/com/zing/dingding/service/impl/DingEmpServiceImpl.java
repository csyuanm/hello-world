package com.zing.dingding.service.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zing.dingding.common.Constants;
import com.zing.dingding.common.DingDingAuth;
import com.zing.dingding.dao.DingDepartmentDao;
import com.zing.dingding.dao.DingEmployeeDao;
import com.zing.dingding.model.DingDepartmentBean;
import com.zing.dingding.model.DingEmployeeBean;
import com.zing.dingding.service.IDepartmentService;
import com.zing.dingding.service.INetSuiteService;
import com.zing.dingding.service.IDingEmpService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class DingEmpServiceImpl implements IDingEmpService {

	@Autowired
	DingEmployeeDao userDao;

	@Autowired
	INetSuiteService nsService;

	@Autowired
	IDingEmpService userSerice;

	@Autowired
	DingDepartmentDao departmentDao;

	@Autowired
	IDepartmentService departmentService;

	/**
	 * 获取公司全部部门列表
	 */
	@Override
	public String get() {
		String str = "";
		String token = Constants.getAccessToken();
		String url = "https://oapi.dingtalk.com/department/list?access_token=" + token;
		try {
			str = DingDingAuth.getAuth(url, null);
			if(str == null||str ==""){
				return "获取成员为空";
			}
			JSONObject strObject = JSONObject.fromObject(str);
			JSONArray deptArray = JSONArray.fromObject(strObject.get("department"));
			if (deptArray != null && deptArray.size() > 0) {
				for (int i = 0; i < deptArray.size(); i++) {
					JSONObject dept = JSONObject.fromObject(deptArray.get(i));
					String deptId = dept.getString("id");
					str = getDetail(token, deptId);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return str;
	}

	/**
	 * 根据部门id保存某个部门的成员数据
	 */
	@Override
	public String getDetail(String token, String department_id) {
		DingEmployeeBean user = new DingEmployeeBean();
		String str = null;
		String url = "https://oapi.dingtalk.com/user/list?access_token=" + token + "&department_id=" + department_id;
		try {
			str = DingDingAuth.getAuth(url, null);
			JSONObject jsonObject = JSONObject.fromObject(str);
			JSONArray jArray = JSONArray.fromObject(jsonObject.get("userlist"));
			if (jArray != null && jArray.size() > 0) {
				for (int i = 0; i < jArray.size(); i++) {
					Object obj = jArray.get(i);
					// 将获取的对象转化为json格式
					JSONObject json = JSONObject.fromObject(obj);
					user.setDepartment(json.getString("department"));
					if (json.has("position")) {
						user.setPosition(json.getString("position"));
					}
					if (json.has("jobnumber")) {
						user.setJobNumber(json.getString("jobnumber"));
					}
					user.setUserId(json.getString("userid"));
					user.setMobile(json.getString("mobile"));
					user.setName(json.getString("name"));
					userDao.insert(user);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 向钉钉中添加成员
	 */
	@Override
	public String addDingUser(String emp, String token) {
		// nsService.getEmployeeList();
		JSONObject employee = JSONObject.fromObject(emp);
		String url = "https://oapi.dingtalk.com/user/create?access_token=" + token;
		if (employee.has("mobilephone") && employee.get("mobilephone") != "") {
			if (employee.has("department") && employee.get("department") != "") {
				JSONObject departmentJson = JSONObject.fromObject(employee.getString("department"));
				String departmentName = departmentJson.getString("name");
				String[] arrayName = departmentName.split(":");
				String name = arrayName[arrayName.length - 1].replace(" ", "");
				List<String> deptArray = new ArrayList<>();
				String deptId = "";
				int count = departmentDao.deptCount(name);
				if (count == 1) {
					deptId = departmentDao.selectIdByName(name);
				} else if (count > 1) {
					String parentName = arrayName[arrayName.length - 2].replace(" ", "");
					List<DingDepartmentBean> repeatBean = departmentDao.selectByName(name);
					// 根据父id查询其名称，与parentName比较，如果相等，则其当前部门id为该部门所要id
					for (DingDepartmentBean reBean : repeatBean) {
						String pName = departmentDao.selectNameById(reBean.getParentId());
						if (pName.equals(parentName)) {
							deptId = reBean.getDepartmentId();
						}
					}
				} else if(count == 0){
					// 该部门不存在，创建该部门
					if(arrayName.length >= 2){
						String parentName = arrayName[arrayName.length - 2].replace(" ", "");
						String deptPId = departmentDao.selectIdByName(parentName);
						JSONObject deptJson = new JSONObject();
						deptJson.put("parentid", deptPId);
						deptJson.put("name", name);
						String returnCreate = departmentService.createDepartment(deptJson.toString(), token);
						//创建部门后把数据同步到本地，避免出现新建部门后查找不到，重复创建
						departmentService.saveDepartment(token);
						JSONObject resultJson = JSONObject.fromObject(returnCreate);
						if(resultJson != null){
							deptId = resultJson.getString("id");
						} else {
							return "查找部门id出错";
						}
					} 
					if(arrayName.length == 1) {
						JSONObject deptJson = new JSONObject();
						deptJson.put("parentid", "1");
						deptJson.put("name", name);
						String returnCreate = departmentService.createDepartment(deptJson.toString(), token);
						//创建部门后把数据同步到本地，避免出现新建部门后查找不到，重复创建
						departmentService.saveDepartment(token);
						JSONObject resultJson = JSONObject.fromObject(returnCreate);
						if(resultJson != null){
							deptId = resultJson.getString("id");
						} else {
							return "查找部门id出错";
						}
					}
				}
				deptArray.add(deptId);
				// deptArray.add(departmentDao.selectIdByName(name));
				JSONObject dingObj = new JSONObject();
				dingObj.put("department", deptArray);
			//	dingObj.put("name", employee.getString("entityid"));
				dingObj.put("jobnumber", employee.getString("entityid"));
				dingObj.put("name", employee.getString("formulatext"));
				if(employee.has("email")){
					dingObj.put("email", employee.getString("email"));
				}
				if(employee.has("title"))
				dingObj.put("position", employee.getString("title"));
				dingObj.put("mobile", employee.getString("mobilephone").replace(" ", ""));
				System.out.println("往钉钉中添加的人为：" + dingObj);
				// 推送员工数据到钉钉
				try {
					DingDingAuth.dingPost(dingObj, url);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("该员工部门不存在，无法添加到钉钉");
			}
		}
		return "add to dingding over";
	}

	@Override
	public String updateDingUser(JSONObject employee, String token) {
		String userId = userDao.selectIdByPhone(employee.getString("mobilephone"));
		if(userId != null){
			String deptInfo = JSONObject.fromObject(employee.getString("department")).getString("name");
			String[] deptArray = deptInfo.split(":");
			String deptName = deptArray[deptArray.length - 1];
			String deptId = departmentDao.selectIdByName(deptName);
			String smsUrl = "https://oapi.dingtalk.com/user/update?access_token="+token;
			
			//需要改的内容待确定
			JSONObject jobj = new JSONObject();
			jobj.put("userid", userId);
			//jobj.put("name", employee.get("name"));
			jobj.put("department", deptId);
			try {
				DingDingAuth.dingPost(jobj, smsUrl);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

}
