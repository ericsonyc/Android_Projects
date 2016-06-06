package com.face.android.facetest;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import com.face.android.pojo.ParamsPojo;

public class HttpUtil {

	private static HttpPost httpRequest = null;
	private static List<NameValuePair> params = null;
	private static HttpResponse httpResponse;

	public static String getLogin(String httpURL, List<ParamsPojo> lp) {
		httpRequest = new HttpPost(httpURL);
		params = new ArrayList<NameValuePair>();
		ParamsPojo pp = new ParamsPojo();

		for (int i = 0; i < lp.size(); i++) {
			pp = lp.get(i);
			params.add(new BasicNameValuePair(pp.getParamsName(), pp
					.getParamsValue()));
		}

		try {
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			httpResponse = new DefaultHttpClient().execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toString(httpResponse.getEntity());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
