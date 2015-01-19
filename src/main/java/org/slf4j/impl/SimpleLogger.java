/*
 * Copyright (c) 2004-2012 QOS.ch
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.slf4j.impl;

import com.sendgrid.SendGrid;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.helpers.NamedLoggerBase;
import org.slf4j.helpers.Util;
import org.slf4j.spi.LocationAwareLogger;

import java.io.*;
import java.net.MalformedURLException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple implementation of {@link org.slf4j.Logger} that sends all enabled log messages,
 * for all defined loggers, to the console ({@code System.err}).
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author <a href="mailto:sanders@apache.org">Scott Sanders</a>
 * @author Rod Waldhoff
 * @author Robert Burrell Donkin
 * @author C&eacute;drik LIME
 */
public class SimpleLogger extends NamedLoggerBase {
	private static final long serialVersionUID = -632788891211436180L;

	private static volatile String CONFIG_FILE = "simple-logger-test.properties";

	private static final long START_TIME = System.currentTimeMillis();
	private static final Properties SIMPLE_LOGGER_PROPS = new Properties();

	private static final int LOG_LEVEL_TRACE = LocationAwareLogger.TRACE_INT;
	private static final int LOG_LEVEL_DEBUG = LocationAwareLogger.DEBUG_INT;
	private static final int LOG_LEVEL_INFO = LocationAwareLogger.INFO_INT;
	private static final int LOG_LEVEL_WARN = LocationAwareLogger.WARN_INT;
	private static final int LOG_LEVEL_ERROR = LocationAwareLogger.ERROR_INT;

	private static volatile boolean INITIALIZED = false;

	private static volatile int DEFAULT_LOG_LEVEL = LOG_LEVEL_INFO;
	private static volatile boolean SHOW_DATE_TIME = false;
	private static volatile String DATE_TIME_FORMAT_STR = null;
	private static volatile DateFormat DATE_FORMATTER = null;
	private static volatile boolean SHOW_THREAD_NAME = true;
	private static volatile boolean SHOW_LOG_NAME = true;
	private static volatile boolean SHOW_SHORT_LOG_NAME = false;
	private static volatile String LOG_FILE = "System.err";
	private static volatile PrintStream TARGET_STREAM = null;
	private static volatile boolean LEVEL_IN_BRACKETS = false;

	private static volatile String NEW_LINE = "\n";
	private static volatile ExecutorService THREAD_POOL = null;
	private static volatile SendGrid SMTP_APPENDER = null;
	private static final List<String> CACHED_MESSAGES = new LinkedList<String>();

	/**
	 * All system properties used by <code>SimpleLogger</code> start with this prefix
	 */
	protected static final String SYSTEM_PREFIX = "org.slf4j.simpleLogger.";

	private static final String DEFAULT_LOG_LEVEL_KEY = SYSTEM_PREFIX + "defaultLogLevel";
	private static final String SHOW_DATE_TIME_KEY = SYSTEM_PREFIX + "showDateTime";
	private static final String DATE_TIME_FORMAT_KEY = SYSTEM_PREFIX + "dateTimeFormat";
	private static final String SHOW_THREAD_NAME_KEY = SYSTEM_PREFIX + "showThreadName";
	private static final String SHOW_LOG_NAME_KEY = SYSTEM_PREFIX + "showLogName";
	private static final String SHOW_SHORT_LOG_NAME_KEY = SYSTEM_PREFIX + "showShortLogName";
	private static final String LOG_FILE_KEY = SYSTEM_PREFIX + "logFile";
	private static final String LEVEL_IN_BRACKETS_KEY = SYSTEM_PREFIX + "levelInBrackets";

	private static final String LOG_KEY_PREFIX = SYSTEM_PREFIX + "log.";

	private static final String CHARSET_KEY = SYSTEM_PREFIX + "charset";
	private static final String SMTP_APPENDER_KEY = SYSTEM_PREFIX + "sendGrid";
	private static final String SMTP_TIMEOUT_KEY = SMTP_APPENDER_KEY + ".timeout";


	protected static String getStringProperty(String name) {
		String prop;
		try {
			prop = System.getProperty(name);
		} catch (SecurityException e) {
			prop = null;
		}
		return (prop == null) ? SIMPLE_LOGGER_PROPS.getProperty(name) : prop;
	}

	protected static String getStringProperty(String name, String defaultValue) {
		String prop = getStringProperty(name);
		return (prop == null) ? defaultValue : prop;
	}

	protected static boolean getBooleanProperty(String name, boolean defaultValue) {
		String prop = getStringProperty(name);
		return (prop == null) ? defaultValue : "true".equalsIgnoreCase(prop);
	}


	// Initialize class attributes.
	// Load properties file, if found.
	// Override with system properties.
	private static void init() {
		INITIALIZED = true;
		loadProperties();

		String defaultLogLevelString = getStringProperty(DEFAULT_LOG_LEVEL_KEY, null);
		if (defaultLogLevelString != null) {
			DEFAULT_LOG_LEVEL = stringToLevel(defaultLogLevelString);
		}

		SHOW_LOG_NAME = getBooleanProperty(SHOW_LOG_NAME_KEY, SHOW_LOG_NAME);
		SHOW_SHORT_LOG_NAME = getBooleanProperty(SHOW_SHORT_LOG_NAME_KEY, SHOW_SHORT_LOG_NAME);
		SHOW_DATE_TIME = getBooleanProperty(SHOW_DATE_TIME_KEY, SHOW_DATE_TIME);
		SHOW_THREAD_NAME = getBooleanProperty(SHOW_THREAD_NAME_KEY, SHOW_THREAD_NAME);
		DATE_TIME_FORMAT_STR = getStringProperty(DATE_TIME_FORMAT_KEY, DATE_TIME_FORMAT_STR);
		LEVEL_IN_BRACKETS = getBooleanProperty(LEVEL_IN_BRACKETS_KEY, LEVEL_IN_BRACKETS);

		LOG_FILE = getStringProperty(LOG_FILE_KEY, LOG_FILE);
		TARGET_STREAM = computeTargetStream(LOG_FILE);

		if (DATE_TIME_FORMAT_STR != null) {
			try {
				DATE_FORMATTER = new SimpleDateFormat(DATE_TIME_FORMAT_STR);
			} catch (IllegalArgumentException e) {
				Util.report("Bad date format in " + CONFIG_FILE + "; will output relative time", e);
			}
		}

		initSMTPAppender();
	}

	private static PrintStream computeTargetStream(String logFile) {
		if ("System.err".equalsIgnoreCase(logFile)) {
			return System.err;
		} else if ("System.out".equalsIgnoreCase(logFile)) {
			return System.out;
		} else {
			try {
				String encoding = getStringProperty(CHARSET_KEY, "ISO-8859-1");

				logFile = replaceSystemProperties(logFile);
				logFile = replaceDatePatterns(logFile);

				FileOutputStream fos = new FileOutputStream(logFile, true);
				return new PrintStream(fos, true, encoding);

			} catch (Exception e) {
				Util.report("Could not open [" + logFile + "]. Defaulting to System.err", e);
			}
		}
		return System.err;
	}

	private static void initSMTPAppender() {
		NEW_LINE = getStringProperty("line.separator", NEW_LINE);
		THREAD_POOL = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

		String sendGridForm = getStringProperty(SMTP_APPENDER_KEY);
		if (null != sendGridForm) {
			try {
				SMTP_APPENDER = new SendGrid(sendGridForm);
			} catch (MalformedURLException e) {
				Util.report("Failed to initialize SendGrid: " + e.toString());
				SMTP_APPENDER = null;
				return;
			}

			String timeout = getStringProperty(SMTP_TIMEOUT_KEY);
			if (null != timeout) {
				try {
					SMTP_APPENDER.setTimeout(Integer.parseInt(timeout));
				} catch (NumberFormatException e) {
					Util.report("Bad SMTP timeout setting: " + e.toString());
				}
			}
		}
	}

	protected static String replaceSystemProperties(String filename) {
		StringBuffer sb = new StringBuffer();

		Matcher m = Pattern.compile("\\$\\{(.+?)\\}").matcher(filename);
		String key, val;
		while (m.find()) {
			key = m.group(1);
			val = getStringProperty(key);
			m.appendReplacement(sb, null != val ? Matcher.quoteReplacement(val) : "");
		}
		m.appendTail(sb);

		return sb.toString();
	}

	protected static String replaceDatePatterns(String filename) {
		StringBuffer sb = new StringBuffer();

		Matcher m = Pattern.compile("%[dD]\\{(.+?)\\}").matcher(filename);
		Date now = new Date();
		String key, val;
		while (m.find()) {
			key = m.group(1);
			try {
				val = new SimpleDateFormat(key).format(now);
			} catch (Exception e) {
				val = null;
			}
			m.appendReplacement(sb, null != val ? Matcher.quoteReplacement(val) : "");
		}
		m.appendTail(sb);

		return sb.toString();
	}

	private static void loadProperties() {
		// Add props from the resource simple-logger.properties
		PrivilegedAction<InputStream> loader = new ConfigFileLoader();
		InputStream in = AccessController.doPrivileged(loader);

		if (null == in) {
			CONFIG_FILE = CONFIG_FILE.replace("-test.", ".");
			in = AccessController.doPrivileged(loader);
		}

		if (null != in) {
			try {
				try {
					SIMPLE_LOGGER_PROPS.load(in);
				} finally {
					in.close();
				}
			} catch (IOException e) {
				Util.report(e.toString());
			}
		}
	}


	/**
	 * The current log level
	 */
	private final int currentLogLevel;
	/**
	 * The short name of this simple log instance
	 */
	private volatile transient String shortLogName = null;

	/**
	 * Package access allows only {@link SimpleLoggerFactory} to instantiate
	 * SimpleLogger instances.
	 */
	SimpleLogger(String name) {
		super(name);
		if (!INITIALIZED) {
			init();
		}

		String levelString = recursivelyComputeLevelString(name);
		if (levelString != null) {
			this.currentLogLevel = stringToLevel(levelString);
		} else {
			this.currentLogLevel = DEFAULT_LOG_LEVEL;
		}
	}

	protected static String recursivelyComputeLevelString(String name) {
		String levelString = null;
		int indexOfLastDot;
		if (null != name && (indexOfLastDot = name.length()) > 0) {
			while ((levelString == null) && (indexOfLastDot >= 0)) {
				name = name.substring(0, indexOfLastDot);
				levelString = getStringProperty(LOG_KEY_PREFIX + name, null);
				indexOfLastDot = name.lastIndexOf('.');
			}
		}
		return levelString;
	}

	protected static int stringToLevel(String levelStr) {
		if ("TRACE".equalsIgnoreCase(levelStr)) {
			return LOG_LEVEL_TRACE;
		} else if ("DEBUG".equalsIgnoreCase(levelStr)) {
			return LOG_LEVEL_DEBUG;
		} else if ("INFO".equalsIgnoreCase(levelStr)) {
			return LOG_LEVEL_INFO;
		} else if ("WARN".equalsIgnoreCase(levelStr)) {
			return LOG_LEVEL_WARN;
		} else if ("ERROR".equalsIgnoreCase(levelStr)) {
			return LOG_LEVEL_ERROR;
		}
		// assume INFO by default
		return LOG_LEVEL_INFO;
	}

	/**
	 * This is our internal implementation for logging regular (non-parameterized)
	 * log messages.
	 *
	 * @param level   One of the LOG_LEVEL_XXX constants defining the log level
	 * @param message The message itself
	 * @param t       The exception whose stack trace should be logged
	 */
	protected void log(int level, String message, Throwable t) {
		if (!isLevelEnabled(level)) {
			return;
		}

		StringBuffer buf = new StringBuffer(32);

		// Append date-time if so configured
		if (SHOW_DATE_TIME) {
			if (DATE_FORMATTER != null) {
				buf.append(getFormattedDate());
				buf.append(' ');
			} else {
				buf.append(System.currentTimeMillis() - START_TIME);
				buf.append(' ');
			}
		}

		// Append current thread name if so configured
		if (SHOW_THREAD_NAME) {
			buf.append('[');
			buf.append(Thread.currentThread().getName());
			buf.append("] ");
		}

		if (LEVEL_IN_BRACKETS) {
			buf.append('[');
		}

		// Append a readable representation of the log level
		switch (level) {
			case LOG_LEVEL_TRACE:
				buf.append("TRACE");
				break;
			case LOG_LEVEL_DEBUG:
				buf.append("DEBUG");
				break;
			case LOG_LEVEL_INFO:
				buf.append("INFO");
				break;
			case LOG_LEVEL_WARN:
				buf.append("WARN");
				break;
			default:// LOG_LEVEL_ERROR:
				buf.append("ERROR");
				break;
		}
		if (LEVEL_IN_BRACKETS) {
			buf.append(']');
		}
		buf.append(' ');

		// Append the name of the log instance if so configured
		if (SHOW_SHORT_LOG_NAME) {
			if (shortLogName == null) {
				shortLogName = computeShortName();
			}
			buf.append(String.valueOf(shortLogName)).append(" - ");
		} else if (SHOW_LOG_NAME) {
			buf.append(String.valueOf(getName())).append(" - ");
		}

		// Append the message
		buf.append(message);

		write(buf, t);

		if (null != SMTP_APPENDER && level >= LOG_LEVEL_ERROR) {
			sendErrorMail(SHOW_SHORT_LOG_NAME ? shortLogName : getName(),
					message, t instanceof ExceptionInInitializerError);
		}
	}

	protected void write(StringBuffer buf, Throwable t) {
		MessageWriter writer = new MessageWriter(buf.toString(), t);
		if (t instanceof ExceptionInInitializerError) {
			writer.run();
		} else {
			THREAD_POOL.execute(writer);
		}

		if (null != SMTP_APPENDER) {
			cacheMailMessages(buf, t);
		}
	}

	private static void cacheMailMessages(StringBuffer buf, Throwable t) {
		if (null != t) {
			StringWriter sw = new StringWriter();
			t.printStackTrace(new PrintWriter(sw));
			buf.append(NEW_LINE).append(sw.toString());
		}

		synchronized (CACHED_MESSAGES) {
			if (CACHED_MESSAGES.size() > 3) {
				CACHED_MESSAGES.remove(0);
			}
			CACHED_MESSAGES.add(buf.toString());
		}
	}

	private static void sendErrorMail(String name, String msg, boolean immediately) {
		String[] messages;
		synchronized (CACHED_MESSAGES) {
			messages = CACHED_MESSAGES.toArray(new String[CACHED_MESSAGES.size()]);
			CACHED_MESSAGES.clear();
		}

		StringBuilder body = new StringBuilder();
		for (String s : messages) {
			body.append(s).append(NEW_LINE);
		}
		String subject = String.valueOf(name) + " - " + msg;

		Runnable sender = new MailSender(subject, body.toString());
		if (immediately) {
			sender.run();
		} else {
			THREAD_POOL.execute(sender);
		}
	}

	protected static String getFormattedDate() {
		Date now = new Date();
		synchronized (CACHED_MESSAGES) {
			return DATE_FORMATTER.format(now);
		}
	}

	private String computeShortName() {
		int p;
		if (null == getName() || (p = getName().lastIndexOf('.')) < 0) {
			return getName();
		}
		return getName().substring(p + 1);
	}

	/**
	 * For formatted messages, first substitute arguments and then log.
	 */
	private void formatAndLog(int level, String format, Object arg1, Object arg2) {
		if (!isLevelEnabled(level)) {
			return;
		}
		FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
		log(level, tp.getMessage(), tp.getThrowable());
	}

	/**
	 * For formatted messages, first substitute arguments and then log.
	 *
	 * @param arguments a list of 3 ore more arguments
	 */
	private void formatAndLog(int level, String format, Object... arguments) {
		if (!isLevelEnabled(level)) {
			return;
		}
		FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
		log(level, tp.getMessage(), tp.getThrowable());
	}

	/**
	 * Is the given log level currently enabled?
	 *
	 * @param logLevel is this level enabled?
	 */
	protected boolean isLevelEnabled(int logLevel) {
		// log level are numerically ordered so can use simple numeric comparison
		return (logLevel >= currentLogLevel);
	}

	/**
	 * Are {@code trace} messages currently enabled?
	 */
	public boolean isTraceEnabled() {
		return isLevelEnabled(LOG_LEVEL_TRACE);
	}

	/**
	 * A simple implementation which logs messages of level TRACE according
	 * to the format outlined above.
	 */
	public void trace(String msg) {
		log(LOG_LEVEL_TRACE, msg, null);
	}

	/**
	 * Perform single parameter substitution before logging the message of level
	 * TRACE according to the format outlined above.
	 */
	public void trace(String format, Object param1) {
		formatAndLog(LOG_LEVEL_TRACE, format, param1, null);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * TRACE according to the format outlined above.
	 */
	public void trace(String format, Object param1, Object param2) {
		formatAndLog(LOG_LEVEL_TRACE, format, param1, param2);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * TRACE according to the format outlined above.
	 */
	public void trace(String format, Object... argArray) {
		formatAndLog(LOG_LEVEL_TRACE, format, argArray);
	}

	/**
	 * Log a message of level TRACE, including an exception.
	 */
	public void trace(String msg, Throwable t) {
		log(LOG_LEVEL_TRACE, msg, t);
	}

	/**
	 * Are {@code debug} messages currently enabled?
	 */
	public boolean isDebugEnabled() {
		return isLevelEnabled(LOG_LEVEL_DEBUG);
	}

	/**
	 * A simple implementation which logs messages of level DEBUG according
	 * to the format outlined above.
	 */
	public void debug(String msg) {
		log(LOG_LEVEL_DEBUG, msg, null);
	}

	/**
	 * Perform single parameter substitution before logging the message of level
	 * DEBUG according to the format outlined above.
	 */
	public void debug(String format, Object param1) {
		formatAndLog(LOG_LEVEL_DEBUG, format, param1, null);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * DEBUG according to the format outlined above.
	 */
	public void debug(String format, Object param1, Object param2) {
		formatAndLog(LOG_LEVEL_DEBUG, format, param1, param2);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * DEBUG according to the format outlined above.
	 */
	public void debug(String format, Object... argArray) {
		formatAndLog(LOG_LEVEL_DEBUG, format, argArray);
	}

	/**
	 * Log a message of level DEBUG, including an exception.
	 */
	public void debug(String msg, Throwable t) {
		log(LOG_LEVEL_DEBUG, msg, t);
	}

	/**
	 * Are {@code info} messages currently enabled?
	 */
	public boolean isInfoEnabled() {
		return isLevelEnabled(LOG_LEVEL_INFO);
	}

	/**
	 * A simple implementation which logs messages of level INFO according
	 * to the format outlined above.
	 */
	public void info(String msg) {
		log(LOG_LEVEL_INFO, msg, null);
	}

	/**
	 * Perform single parameter substitution before logging the message of level
	 * INFO according to the format outlined above.
	 */
	public void info(String format, Object arg) {
		formatAndLog(LOG_LEVEL_INFO, format, arg, null);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * INFO according to the format outlined above.
	 */
	public void info(String format, Object arg1, Object arg2) {
		formatAndLog(LOG_LEVEL_INFO, format, arg1, arg2);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * INFO according to the format outlined above.
	 */
	public void info(String format, Object... argArray) {
		formatAndLog(LOG_LEVEL_INFO, format, argArray);
	}

	/**
	 * Log a message of level INFO, including an exception.
	 */
	public void info(String msg, Throwable t) {
		log(LOG_LEVEL_INFO, msg, t);
	}

	/**
	 * Are {@code warn} messages currently enabled?
	 */
	public boolean isWarnEnabled() {
		return isLevelEnabled(LOG_LEVEL_WARN);
	}

	/**
	 * A simple implementation which always logs messages of level WARN according
	 * to the format outlined above.
	 */
	public void warn(String msg) {
		log(LOG_LEVEL_WARN, msg, null);
	}

	/**
	 * Perform single parameter substitution before logging the message of level
	 * WARN according to the format outlined above.
	 */
	public void warn(String format, Object arg) {
		formatAndLog(LOG_LEVEL_WARN, format, arg, null);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * WARN according to the format outlined above.
	 */
	public void warn(String format, Object arg1, Object arg2) {
		formatAndLog(LOG_LEVEL_WARN, format, arg1, arg2);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * WARN according to the format outlined above.
	 */
	public void warn(String format, Object... argArray) {
		formatAndLog(LOG_LEVEL_WARN, format, argArray);
	}

	/**
	 * Log a message of level WARN, including an exception.
	 */
	public void warn(String msg, Throwable t) {
		log(LOG_LEVEL_WARN, msg, t);
	}

	/**
	 * Are {@code error} messages currently enabled?
	 */
	public boolean isErrorEnabled() {
		return isLevelEnabled(LOG_LEVEL_ERROR);
	}

	/**
	 * A simple implementation which always logs messages of level ERROR according
	 * to the format outlined above.
	 */
	public void error(String msg) {
		log(LOG_LEVEL_ERROR, msg, null);
	}

	/**
	 * Perform single parameter substitution before logging the message of level
	 * ERROR according to the format outlined above.
	 */
	public void error(String format, Object arg) {
		formatAndLog(LOG_LEVEL_ERROR, format, arg, null);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * ERROR according to the format outlined above.
	 */
	public void error(String format, Object arg1, Object arg2) {
		formatAndLog(LOG_LEVEL_ERROR, format, arg1, arg2);
	}

	/**
	 * Perform double parameter substitution before logging the message of level
	 * ERROR according to the format outlined above.
	 */
	public void error(String format, Object... argArray) {
		formatAndLog(LOG_LEVEL_ERROR, format, argArray);
	}

	/**
	 * Log a message of level ERROR, including an exception.
	 */
	public void error(String msg, Throwable t) {
		log(LOG_LEVEL_ERROR, msg, t);
	}

	//region Nested Classes

	private static class ConfigFileLoader implements PrivilegedAction<InputStream> {
		public ConfigFileLoader() {
		}

		public InputStream run() {
			ClassLoader threadCL = Thread.currentThread().getContextClassLoader();
			if (threadCL != null) {
				return threadCL.getResourceAsStream(CONFIG_FILE);
			}
			return ClassLoader.getSystemResourceAsStream(CONFIG_FILE);
		}
	}

	private static class MailSender implements Runnable {
		private final String subject;
		private final String body;

		public MailSender(String subject, String body) {
			this.subject = subject;
			this.body = body;
		}

		public void run() {
			try {
				SMTP_APPENDER.send(subject, body);
			} catch (Exception e) {
				Util.report("Failed to send error mail.", e);
			}
		}
	}

	private static class MessageWriter implements Runnable {
		private final String message;
		private final Throwable error;

		public MessageWriter(String message, Throwable error) {
			this.message = message;
			this.error = error;
		}

		public void run() {
			TARGET_STREAM.println(message);
			if (null != error) {
				error.printStackTrace(TARGET_STREAM);
			}
		}
	}

	//endregion
}
