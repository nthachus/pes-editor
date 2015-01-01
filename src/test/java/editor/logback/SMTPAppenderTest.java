package editor.logback;

import editor.data.BaseTest;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;

public final class SMTPAppenderTest extends BaseTest {
	@Test
	public void testSetPassword() throws Exception {
		AlternateSMTPAppender appender = new AlternateSMTPAppender();
		for (int i = 0; i < 100; i++) {
			String pwd = randomString(6, 14);
			String username = randomString(8, 32);

			Method m = AlternateSMTPAppender.class.getDeclaredMethod("encryptPassword", String.class, String.class);
			m.setAccessible(true);
			String encPwd = (String) m.invoke(null, pwd, username);

			Assert.assertNotNull(encPwd);
			//log.debug("Encrypted password '{}' / '{}': {}", pwd, username, encPwd);

			appender.setUsername(username);
			appender.setPassword(encPwd);

			Assert.assertEquals(pwd, appender.getPassword());
		}
	}

}
