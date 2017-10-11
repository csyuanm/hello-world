package com.zing.dingding.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.zing.dingding.common.UserUtil;
import com.zing.dingding.service.INsService;

@SuppressWarnings("all")
@Service
public class NsServiceImpl implements INsService {

	@Override
	public Map getAccount() {
		
		Map accountMap = new HashMap();
		accountMap.put("account", UserUtil.NS_COUNNET); // compayid=4556831
		accountMap.put("email", UserUtil.NS_EMAIL); // 登录email
		accountMap.put("password", UserUtil.NS_PASS);// 密码
		accountMap.put("role", UserUtil.NS_ROLE); // roleid 默认填写18
		
		return accountMap;
	}
	
}
