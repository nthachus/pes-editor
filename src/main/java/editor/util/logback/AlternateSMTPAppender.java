package editor.util.logback;

import ch.qos.logback.classic.net.SMTPAppender;
import editor.util.Strings;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Arrays;

public class AlternateSMTPAppender extends SMTPAppender {
	private static final String MAIL_SMTP_CONN_TIMEOUT = "mail.smtp.connectiontimeout";
	private static final String MAIL_SMTP_TIMEOUT = "mail.smtp.timeout";

	private volatile String timeout;
	private volatile String connectionTimeout;

	public AlternateSMTPAppender() {
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public void setConnectionTimeout(String connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	@Override
	public void start() {
		// fix gateway time-out error
		if (null == System.getProperty(MAIL_SMTP_TIMEOUT) && null != timeout)
			System.setProperty(MAIL_SMTP_TIMEOUT, timeout);
		if (null == System.getProperty(MAIL_SMTP_CONN_TIMEOUT) && null != connectionTimeout)
			System.setProperty(MAIL_SMTP_CONN_TIMEOUT, connectionTimeout);

		super.start();
	}

	@Override
	public void setPassword(String password) {
		password = decryptPassword(password, getUsername());
		super.setPassword(password);
	}

	private static final String ENC_ALGORITHM = "AES";
	private static final int ENC_KEY_SIZE = 16;

	private static String decryptPassword(String password, String key) {
		if (!Strings.isEmpty(password) && !Strings.isEmpty(key)) {
			try {
				byte[] pwd = DatatypeConverter.parseBase64Binary(password);
				byte[] salt = key.getBytes(Strings.ANSI);

				Key secret = new SecretKeySpec(Arrays.copyOf(salt, ENC_KEY_SIZE), ENC_ALGORITHM);
				Cipher cipher = Cipher.getInstance(ENC_ALGORITHM);

				cipher.init(Cipher.DECRYPT_MODE, secret);
				byte[] decrypted = cipher.doFinal(pwd);

				return new String(decrypted, Strings.ANSI);
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}

		return password;
	}

	private static String encryptPassword(String password, String key) {
		if (!Strings.isEmpty(password) && !Strings.isEmpty(key)) {
			try {
				byte[] pwd = password.getBytes(Strings.ANSI);
				byte[] salt = key.getBytes(Strings.ANSI);

				Key secret = new SecretKeySpec(Arrays.copyOf(salt, ENC_KEY_SIZE), ENC_ALGORITHM);
				Cipher cipher = Cipher.getInstance(ENC_ALGORITHM);

				cipher.init(Cipher.ENCRYPT_MODE, secret);
				byte[] encrypted = cipher.doFinal(pwd);

				return DatatypeConverter.printBase64Binary(encrypted);
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}

		return password;
	}

}
