package com.mml.dingdingcommon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

@SuppressWarnings("all")
public class NetSuiteAuth {

	private static String authorization(Map map) {
		String account = map.get("account") + "";
		String role = map.get("role") + "";
		String authorization = "NLAuth nlauth_email=" + map.get("email") + ", nlauth_signature=" + map.get("password");
		if (!"".equals(account) && account != null) {
			authorization = authorization + ", nlauth_account=" + account;
		}
		if (!"".equals(role) && role != null) {
			authorization = authorization + ", nlauth_role=" + role;
		}
		return authorization;

	}

	/**
	 * @param url
	 *            {String} [required] - The external URL of the RESTlet Script.
	 * @param entity
	 *            {String} [required] - Body used for a POST request. It should
	 *            be a JSON string.
	 * @return result
	 */
	public static String postAuth(Map map, String url, String entity) throws IOException, URISyntaxException {

		URI uri = new URI(url);

		HttpPost post = new HttpPost(uri);
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 20000);
		HttpConnectionParams.setSoTimeout(httpParameters, 42000);

		post.setHeader("Authorization", authorization(map));
		post.setHeader("Content-Type", "application/json");
		post.setHeader("Accept", "*/*");
		post.setEntity(new StringEntity(entity, "utf-8"));

		HttpClient client = new DefaultHttpClient(httpParameters);
		BufferedReader in = null;
		HttpResponse response = client.execute(post);
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
	 *            {String} [required] - The parameters for a GET request.
	 * @return result
	 */
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
