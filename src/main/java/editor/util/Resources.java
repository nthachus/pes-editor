package editor.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.ResourceBundle;

public final class Resources {
	private static final Logger log = LoggerFactory.getLogger(Resources.class);

	private Resources() {
	}

	private static volatile ResourceBundle messages = null;

	public static ResourceBundle getMessages(boolean reload) {
		if (null == messages || reload) {
			messages = ResourceBundle.getBundle("META-INF/i18n/messages");
			if (null == messages) throw new NullPointerException("messages");
		}
		return messages;
	}

	public static ResourceBundle getMessages() {
		return getMessages(false);
	}

	public static String getMessage(String key, Object... args) {
		if (null == key) throw new NullPointerException("key");

		String msg;
		if (getMessages().containsKey(key))
			msg = messages.getString(key);
		else {
			msg = key;
			log.error("Message key '{}' not found for '{}'", key, Locale.getDefault());
		}

		if (null == args || args.length == 0)
			return msg;
		return String.format(msg, args);
	}

	public static String getNullableMessage(String key, Object... args) {
		String msg = getMessage(key, args);
		return Strings.isEmpty(msg) ? null : msg;
	}

}