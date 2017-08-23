package com.mml.dingding.common;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import net.sf.json.JSONObject;

@SuppressWarnings("all")
public class DingDingAuth {
	private static String authorization(Map map) {
		String corpid = map.get("account") + "";
		String role = map.get("role") + "";
		String authorization = "NLAuth nlauth_email=" + map.get("email") + ", nlauth_signature=" + map.get("password");
		if (!"".equals(corpid) && corpid != null) {
			authorization = authorization + ", nlauth_account=" + corpid;
		}
		if (!"".equals(role) && role != null) {
			authorization = authorization + ", nlauth_role=" + role;
		}
		return authorization;
	}


	public static String postAuth(String url, String entity) throws IOException, URISyntaxException {

		URI uri = new URI(url);
		
		HttpPost post = new HttpPost(uri);
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 20000);
		HttpConnectionParams.setSoTimeout(httpParameters, 42000);

		post.setHeader("Content-Type", "application/json");
		post.setHeader("Accept", "*/*");
		post.setEntity(new StringEntity(entity, "utf-8"));

		HttpClient client = new DefaultHttpClient(httpParameters);
		BufferedReader in = null;
		HttpResponse response = client.execute(post);
		in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
		System.out.println("in");
		StringBuffer sb = new StringBuffer("");
		String line;
		String NL = System.getProperty("line.separator");
		while ((line = in.readLine()) != null) {
			sb.append(line + NL);
		}
		in.close();
		String result = sb.toString();

		return result;
	}
	
	public static String dingPost(JSONObject jobj, String access_token){
		String str = null;
		String corpId = "ding6cbc9a566e55edd035c2f4657eb6378f";
	    String CorpSecret = "PvypyCr3cLRaa9RN2BiqVDIrIDytp-GHiZpD6M96762mHwQBoX_Fbr71cE3otoQt";
	   // String access_token =Constants.getAccessToken();
		HttpClient httpClient = new DefaultHttpClient();  
		HttpResponse response = null;
		HttpEntity httpEntity = null;
		HttpPost httppost = null;
	    String url="https://oapi.dingtalk.com/user/create?access_token="+access_token;  
	    try {  
	         StringEntity js = new StringEntity(jobj.toString(), "utf-8");
	         js.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
	    	System.out.println(url);
	         httppost = new HttpPost(url);
	    	 httppost.setEntity(js); 
	    	 System.out.println(url);
	         System.out.println(httppost.getURI());
	         response = httpClient.execute(httppost);  
	         httpEntity = response.getEntity();
	         str = EntityUtils.toString(httpEntity,"utf-8");
	         System.out.println(str);
	    } catch (ClientProtocolException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } finally{
	        try {
	            if(httppost!=null){
	            	httppost.releaseConnection(); // 关闭method 的 httpclient连接
	            }
	            if(httpClient!=null){
	                ((Closeable) httpClient).close();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
		return str;
	}
	
	public static String dingPostAll(String url, JSONObject jobj){
		String str = null;
		
	   // 
		HttpClient httpClient = new DefaultHttpClient();  
		HttpResponse response = null;
		HttpEntity httpEntity = null;
		HttpPost httppost = null;
		String token =Constants.getAccessToken();
	    try {  
	         StringEntity js = new StringEntity(jobj.toString(), "utf-8");
	         js.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
	    	 httppost = new HttpPost(url);
	    	 httppost.setEntity(js); 
	         System.out.println(httppost.getURI());
	         response = httpClient.execute(httppost);  
	         httpEntity = response.getEntity();
	         str = EntityUtils.toString(httpEntity,"utf-8");
	        // System.out.println(str);
	    } catch (ClientProtocolException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } finally{
	        try {
	            if(httppost!=null){
	            	httppost.releaseConnection(); 
	            }
	            if(httpClient!=null){
	                ((Closeable) httpClient).close();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
		return str;
	}


	public static String getAuth(Map map, String url, String param) throws IOException, URISyntaxException {

		if (!"".equals(param) && param != null) {
			url = url + "&" + param;
		}

		URI uri = new URI(url);

		HttpGet get = new HttpGet(uri);
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 20000);
		HttpConnectionParams.setSoTimeout(httpParameters, 42000);

		get.setHeader("Authorization", authorization(map));
		get.setHeader("Content-Type", "application/json");
		get.setHeader("Accept", "*/*");

		HttpClient client = new DefaultHttpClient(httpParameters);
		BufferedReader in = null;
		HttpResponse response = client.execute(get);
		in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
		StringBuffer sb = new StringBuffer("");
		String line;
		String NL = System.getProperty("line.separator");
		while ((line = in.readLine()) != null) {
			sb.append(line + NL);
		}
		in.close();
		String result = sb.toString();

		return result;
	}

	/**
	 * @param url
	 *            {String} [required] - The external URL of the RESTlet Script.
	 * @param param
	 *            {String} [required] - The parameters for a DELETE request.
	 */
	public static void deleteAuth(Map map, String url, String param) throws URISyntaxException, IOException {

		URI uri = new URI(url + "&" + param);

		HttpDelete delete = new HttpDelete(uri);

		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 20000);
		HttpConnectionParams.setSoTimeout(httpParameters, 42000);

		delete.setHeader("Authorization", authorization(map));

		HttpClient client = new DefaultHttpClient(httpParameters);
		client.execute(delete);
	}
}
