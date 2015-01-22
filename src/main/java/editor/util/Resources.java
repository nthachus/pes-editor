package editor.util;

import editor.lang.NullArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class Resources {
	private static final Logger log = LoggerFactory.getLogger(Resources.class);

	private Resources() {
	}

	private static volatile ResourceBundle messages = null;

	public static ResourceBundle getMessages(boolean reload) {
		if (null == messages || reload) {
			messages = ResourceBundle.getBundle("META-INF/i18n/messages", Locale.getDefault());
			if (null == messages) {
				throw new IllegalStateException("messages must not be null.");
			}
		}
		return messages;
	}

	public static ResourceBundle getMessages() {
		return getMessages(false);
	}

	private static String getMessage(String key, boolean nullable, Object... args) {
		if (null == key) {
			throw new NullArgumentException("key");
		}

		String msg;
		try {
			msg = getMessages().getString(key);
		} catch (MissingResourceException e) {
			msg = null;
		}

		if (nullable && Strings.isEmpty(msg)) {
			return null;
		}
		if (null == msg) {
			msg = key;
			log.error("Message key '{}' not found for '{}'", key, Locale.getDefault());
		}

		return (null == args || args.length == 0) ? msg : String.format(msg, args);
	}

	public static String getMessage(String key, Object... args) {
		return getMessage(key, false, args);
	}

	public static String getNullableMessage(String key, Object... args) {
		return getMessage(key, true, args);
	}

}
