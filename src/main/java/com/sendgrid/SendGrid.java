package com.sendgrid;

import editor.lang.NullArgumentException;
import editor.lang.NullHostnameVerifier;
import editor.lang.TrustAllManager;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

/**
 * This allows you to quickly and easily send emails through SendGrid using Java.
 */
public class SendGrid implements Serializable {
	private static final long serialVersionUID = 3614556945357970755L;

	private static final String VERSION = "2.1.0";
	private static final String USER_AGENT = "sendgrid/" + VERSION + ";java";

	private volatile URL endpoint;
	private final String form;
	private final String encoding;
	private volatile Integer timeout = null;

	public SendGrid(String form, String encoding) throws MalformedURLException {
		if (null == form) {
			throw new NullArgumentException("form");
		}
		setEndpoint("https://api.sendgrid.com/api/mail.send.json");
		this.form = form;
		this.encoding = (null != encoding && encoding.length() > 0) ? encoding : "UTF-8";
	}

	public SendGrid(String form) throws MalformedURLException {
		this(form, null);
	}

	public SendGrid setEndpoint(String endpoint) throws MalformedURLException {
		if (null == endpoint) {
			throw new NullArgumentException("endpoint");
		}
		this.endpoint = new URL(endpoint);
		return this;
	}

	public SendGrid setTimeout(int timeout) {
		this.timeout = timeout;
		return this;
	}

	public String getVersion() {
		return VERSION;
	}

	public boolean send(String subject, String body) throws IOException, GeneralSecurityException {
		HttpURLConnection http = (HttpURLConnection) endpoint.openConnection();

		if (http instanceof HttpsURLConnection) {
			initHttpsRequest((HttpsURLConnection) http);
		}

		// Add request header
		initHttpRequest(http);

		// Send post request
		String postData = buildPostData(subject, body);
		http.setDoOutput(true);
		OutputStreamWriter sw = null;
		try {
			sw = new OutputStreamWriter(http.getOutputStream(), encoding);
			sw.write(postData);
		} finally {
			if (null != sw) {
				sw.close();
			}
		}

		// Get response
		/*BufferedReader sr = null;
		StringBuilder response = new StringBuilder();
		try {
			sr = new BufferedReader(new InputStreamReader(http.getInputStream(), encoding));
			String line;
			while ((line = sr.readLine()) != null) {
				response.append(line);
			}
		} finally {
			if (null != sr) sr.close();
		}*/

		return (http.getResponseCode() == HttpURLConnection.HTTP_OK);
	}

	private void initHttpRequest(HttpURLConnection http) throws IOException {
		http.setInstanceFollowRedirects(true);
		http.setRequestMethod("POST");
		http.setRequestProperty("User-Agent", USER_AGENT);

		if (null != timeout) {
			http.setConnectTimeout(timeout);
			http.setReadTimeout(timeout);
		}
	}

	private void initHttpsRequest(HttpsURLConnection https) throws GeneralSecurityException {
		// Tell the url connection object to use our socket factory which bypasses security checks
		https.setSSLSocketFactory(getSSLSocketFactory());
		https.setHostnameVerifier(getSSLHostVerifier());
	}

	private String buildPostData(String subject, String body) throws IOException {
		if (null != subject && subject.length() > 0) {
			subject = URLEncoder.encode(subject, encoding);
		}

		if (null != body && body.length() > 0) {
			body = URLEncoder.encode(body, encoding);
		}

		return String.format(form, subject, body);
	}

	private static volatile SSLSocketFactory sslSocketFactory = null;
	private static volatile HostnameVerifier sslHostVerifier = null;

	private static SSLSocketFactory getSSLSocketFactory() throws GeneralSecurityException {
		if (null == sslSocketFactory) {
			// Install the all-trusting trust manager
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, new TrustManager[]{new TrustAllManager()}, new SecureRandom());
			// Create an ssl socket factory with our all-trusting manager
			sslSocketFactory = sslContext.getSocketFactory();
		}
		return sslSocketFactory;
	}

	private static HostnameVerifier getSSLHostVerifier() {
		if (null == sslHostVerifier) {
			sslHostVerifier = new NullHostnameVerifier();
		}
		return sslHostVerifier;
	}

}
