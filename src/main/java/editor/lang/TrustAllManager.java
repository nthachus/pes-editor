package editor.lang;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * A trust manager that does not validate certificate chains.
 */
public class TrustAllManager implements X509TrustManager {
	public void checkClientTrusted(X509Certificate[] chain, String authType) {
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType) {
	}

	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}
}
