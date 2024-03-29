package com.zing.dingding.common;

import java.io.IOException;
import java.net.URISyntaxException;


import net.sf.json.JSONObject;

/**
 * 主要用于获取钉钉token，及一些公共路径
 * @author mark
 * @description 
 * @date 2017年8月29日 上午11:02:58
 * @version v1.0
 */
public class Constants {

//	String corpId = "ding7d8395159ab63c53";
//	String CorpSecret = "9EPQ7aLz4qYnazjcTnnmckU9RIYsdB_jenmXptoWHfAZQVOIVEvC2lr45_occvBx";
	
    public static String CORP_ID = "ding6cbc9a566e55edd035c2f4657eb6378f";
    public static String SECRET_ID = "PvypyCr3cLRaa9RN2BiqVDIrIDytp-GHiZpD6M96762mHwQBoX_Fbr71cE3otoQt";
    
	/**
	 * 获取token
	 * @return token
	 */
	public static String getAccessToken() {
		String corpId = CORP_ID;
		String CorpSecret = SECRET_ID;
		String url = "https://oapi.dingtalk.com/gettoken?corpid=" + corpId + "&corpsecret=" + CorpSecret;
		String token = null;
		String result = "";
		try {
			result = DingDingAuth.getAuth(url, null);
			JSONObject json = JSONObject.fromObject(result);
			token = json.getString("access_token");
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		return token;
	}
}
