package com.mml.dingding.service.impl;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mml.dingding.common.Constants;
import com.mml.dingding.common.DingDingAuth;
import com.mml.dingding.common.NetSuiteAuth;
import com.mml.dingding.common.UserUtil;
import com.mml.dingding.dao.DingUserDao;
import com.mml.dingding.dao.NetsuiteEmployeeDao;
import com.mml.dingding.model.NetsuiteEmployee;
import com.mml.dingding.service.NetSuiteService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class NetSuiteServiceImpl implements NetSuiteService{
	
	@Autowired NetsuiteEmployeeDao netSuiteDao;
	
	@Autowired DingUserDao dingUserDao;

	/**
	 * 从netsuite中获取所有员工，保存在本地，并推送到钉钉
	 */
	@Override
	public String getEmployeeList() {
		Map accountMap = new HashMap();
		accountMap.put("account", UserUtil.NS_COUNNET); //compayid=4556831
		accountMap.put("email", UserUtil.NS_EMAIL); //登录email
		accountMap.put("password", UserUtil.NS_PASS);//密码
		accountMap.put("role", UserUtil.NS_ROLE); //roleid 默认填写18
		String result = "";
		String str = "";
		String[] sArray = {"1"};
		String access_token =Constants.getAccessToken();
		Boolean flag = false;
		NetsuiteEmployee netsuiteEmployee = new NetsuiteEmployee();
		//get方式获取
		try { 
			result = NetSuiteAuth.getAuth(accountMap, UserUtil.EMPLOYEEINFOS, null);
			System.out.println("NS....result..."+result);
			JSONArray resultArray = JSONArray.fromObject(result);
			if(resultArray.size()>0){
				for(int i = 0; i<resultArray.size(); i++){
					JSONObject employeeArray = JSONObject.fromObject(resultArray.get(i));
					JSONObject employee = JSONObject.fromObject(employeeArray.get("columns"));
					Map map = employee;
					if(employee.has("supervisor")){
						netsuiteEmployee.setSupervisor(employee.getString("supervisor"));
					}
					if(employee.has("department")){
						netsuiteEmployee.setDepartment(employee.getString("department"));
					}
				    if(employee.has("subsidiary")){
					netsuiteEmployee.setSubsidiary(employee.getString("subsidiary"));
			    	}
				    if(employee.has("entityid")){
						netsuiteEmployee.setEntityid(employee.getString("entityid"));
				    }
				    if(employee.has("title")){
						netsuiteEmployee.setTitle(employee.getString("title"));
				    }
				    if(employee.has("email")){
						netsuiteEmployee.setEmail(employee.getString("email"));
				    }
				    if(employee.has("mobilephone")){
						netsuiteEmployee.setMobilephone(employee.getString("mobilephone").replace(" ", ""));
				    }
					//netSuiteDao.insert(map);
				    int count = netSuiteDao.checkRepeat(employee.getString("mobilephone"));
				    if(count ==0){
				    	netSuiteDao.insertEmployee(netsuiteEmployee);
				    }
					
					
					//if(flag){
					if(employee.has("mobilephone")&&employee.get("mobilephone")!=""){
						JSONObject dingObj = new JSONObject();
						dingObj.put("department", sArray);
						dingObj.put("name", employee.get("entityid"));
						dingObj.put("mobile", employee.get("mobilephone"));
						//推送员工数据到钉钉
						str = DingDingAuth.dingPost(dingObj,access_token);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取netsuite员工生日,并向下个月过生日的员工发祝福语
	 */
	@Override
	public String getEmployeeBirth() {
		Map accountMap = new HashMap();
		accountMap.put("account", UserUtil.NS_COUNNET); //compayid=4556831
		accountMap.put("email", UserUtil.NS_EMAIL); //登录email
		accountMap.put("password", UserUtil.NS_PASS);//密码
		accountMap.put("role", UserUtil.NS_ROLE); //roleid 默认填写18
		String result = "";
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH)+2;
		String token =Constants.getAccessToken();
		String url="https://oapi.dingtalk.com/message/send?access_token="+token;
		try { 
			//从netsuite获取员工生日日期
			result = NetSuiteAuth.getAuth(accountMap, UserUtil.EMPLOYEEBIRTH, null);
			JSONArray jArray = JSONArray.fromObject(result);;
			if(jArray.size()>0){
				JSONObject jsonEmployee = new JSONObject(); 
				for(int i = 0; i < jArray.size(); i++){
					 jsonEmployee = JSONObject.fromObject(jArray.get(i));
					 JSONObject obj = JSONObject.fromObject(jsonEmployee.get("columns"));
					// System.out.println("第"+(i+1)+"个columns"+obj);
					 if(obj.size()>0&&obj.has("birthdate")){
							 String birth = obj.getString("birthdate");
							 String arr[] = birth.split("/");//分割字符串
							 String mon = arr[1];
							 //获取下个月过生日的员工
							 if(month == Integer.valueOf(mon)&&obj.has("mobilephone")){
								 String mobile = obj.getString("mobilephone").replace(" ", "");
								 String userid = dingUserDao.selectIDByPhone(mobile);
								 String[] id = new String[1];
								 id[0]=userid;
								 String name = obj.getString("entityid");
								 JSONObject cobj = new JSONObject();
								 JSONObject content = new JSONObject();
								 content.put("content", "生日祝福语3");
								 //一天之内不能向同一个用户发送同一内容
								 cobj.put("agentid", 115257477);
								 cobj.put("touser", id[0]);
								 cobj.put("msgtype", "text");
								 cobj.put("text", content);
								 result = DingDingAuth.dingPostAll(url, cobj);
								 System.out.println(name+"恭喜你，下个月"+arr[1]+"月是你的生日，到时会有神秘大礼哦");
								 System.out.println("请求结构体："+cobj);
								 System.out.println("返回结果："+result);
							 }
					 }
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
