package org.slf4j.net;

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
import java.security.cert.X509Certificate;

/**
 * This allows you to quickly and easily send emails through HTTP mail service using Java.
 */
public class HttpMailer implements Serializable {
	private static final long serialVersionUID = 3614556945357970755L;

	protected volatile URL endpoint;
	protected volatile String authentication;
	protected final String form;
	protected final String encoding;
	protected volatile Integer timeout = null;

	public HttpMailer(String form, String encoding) {
		if (null == form) {
			throw new IllegalArgumentException("form must not be null.");
		}
		this.form = form;
		this.encoding = (null != encoding && encoding.length() > 0) ? encoding : "UTF-8";
	}

	public HttpMailer(String form) {
		this(form, null);
	}

	public HttpMailer setEndpoint(String endpoint) throws MalformedURLException {
		if (null == endpoint) {
			throw new IllegalArgumentException("endpoint must not be null.");
		}
		this.endpoint = new URL(endpoint);
		return this;
	}

	public HttpMailer setAuthentication(String authentication) {
		this.authentication = authentication;
		return this;
	}

	public HttpMailer setTimeout(int timeout) {
		this.timeout = timeout;
		return this;
	}

	public String getUserAgent() {
		return "Java/" + System.getProperty("java.version");
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

	protected void initHttpRequest(HttpURLConnection http) throws IOException {
		http.setInstanceFollowRedirects(true);
		http.setRequestMethod("POST");
		http.setRequestProperty("User-Agent", getUserAgent());

		if (null != authentication && authentication.length() > 0) {
			http.setRequestProperty("Authorization", authentication);
		}

		if (null != timeout) {
			http.setConnectTimeout(timeout);
			http.setReadTimeout(timeout);
		}
	}

	protected void initHttpsRequest(HttpsURLConnection https) throws GeneralSecurityException {
		// Tell the url connection object to use our socket factory which bypasses security checks
		https.setSSLSocketFactory(getSSLSocketFactory());
		https.setHostnameVerifier(getSSLHostVerifier());
	}

	protected String buildPostData(String subject, String body) throws IOException {
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

	protected static SSLSocketFactory getSSLSocketFactory() throws GeneralSecurityException {
		if (null == sslSocketFactory) {
			// Install the all-trusting trust manager
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, new TrustManager[]{new TrustAllManager()}, new SecureRandom());
			// Create an ssl socket factory with our all-trusting manager
			synchronized (HttpMailer.class) {
				if (null == sslSocketFactory) {
					sslSocketFactory = sslContext.getSocketFactory();
				}
			}
		}
		return sslSocketFactory;
	}

	protected static HostnameVerifier getSSLHostVerifier() {
		if (null == sslHostVerifier) {
			sslHostVerifier = new NullHostnameVerifier();
		}
		return sslHostVerifier;
	}

	/**
	 * A class to trust all hosts, so always returns true.
	 */
	public static class NullHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	/**
	 * A trust manager that does not validate certificate chains.
	 */
	public static class TrustAllManager implements X509TrustManager {
		public void checkClientTrusted(X509Certificate[] chain, String authType) {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}
	}

}
