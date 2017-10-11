package com.zing.dingding.service.impl;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zing.dingding.common.DingDingAuth;
import com.zing.dingding.dao.DingDepartmentDao;
import com.zing.dingding.model.DingDepartmentBean;
import com.zing.dingding.service.IDepartmentService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class DepartmentServiceImpl implements IDepartmentService {
	
	@Autowired 
	DingDepartmentDao departmentDao;
	
	
	/**
	 * 批量添加当前公司所有部门
	 */
	@Override
	public String saveDepartment(String token) {
		DingDepartmentBean department = new DingDepartmentBean();
		String result = null;
		String url = "https://oapi.dingtalk.com/department/list?access_token=" + token;
		try {
			result = DingDingAuth.getAuth(url, null);
			JSONObject jsonObject = JSONObject.fromObject(result);
			JSONArray jArray = jsonObject.getJSONArray("department");
			//保存之前先执行假删除
			departmentDao.updateFlag();
			if (jArray != null && jArray.size() > 0) {
				for (int i = 0; i < jArray.size(); i++) {
					Object obj = jArray.get(i);
					JSONObject json = JSONObject.fromObject(obj);
					if (json.has("parentid")&&(json.getString("parentid")!="")) {
						department.setParentId(json.getString("parentid"));
					}
					department.setDepartmentId(json.getString("id"));
					department.setName(json.getString("name"));
					departmentDao.insert(department);
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
	 * 创建部门
	 */
	@Override
	public String createDepartment(String deptInfo, String token) {
		String url ="https://oapi.dingtalk.com/department/create?access_token="+token;
		JSONObject deptJson = JSONObject.fromObject(deptInfo);
		String result = "";
		try {
			result = DingDingAuth.dingPost(deptJson, url);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
