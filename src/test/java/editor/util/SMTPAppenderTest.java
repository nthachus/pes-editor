package editor.util;

import editor.data.BaseTest;
import editor.util.logback.AlternateSMTPAppender;
import org.junit.Assert;
import org.junit.Test;

public final class SMTPAppenderTest extends BaseTest {
	@Test
	public void testSetPassword() throws Exception {
		AlternateSMTPAppender appender = new AlternateSMTPAppender();
		for (int i = 0; i < 100; i++) {
			String pwd = randomString(6, 14);
			String username = randomString(8, 32);

			String encPwd = (String) Systems.invokeStaticMethod(AlternateSMTPAppender.class,
					"encryptPassword", new Class[]{String.class, String.class}, true, pwd, username);

			Assert.assertNotNull(encPwd);
			//log.debug("Encrypted password '{}' / '{}': {}", pwd, username, encPwd);

			appender.setUsername(username);
			appender.setPassword(encPwd);

			Assert.assertEquals(pwd, appender.getPassword());
		}
	}

}
