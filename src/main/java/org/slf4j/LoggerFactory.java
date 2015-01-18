/*
 * Copyright (c) 2004-2011 QOS.ch
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
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

package org.slf4j;

import org.slf4j.helpers.NOPLoggerFactory;
import org.slf4j.helpers.SubstituteLoggerFactory;
import org.slf4j.helpers.Util;
import org.slf4j.impl.StaticLoggerBinder;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * The <code>LoggerFactory</code> is a utility class producing Loggers for
 * various logging APIs, most notably for log4j, logback and JDK 1.4 logging.
 * Other implementations such as <code>NOPLogger</code> and
 * <code>SimpleLogger</code> are also supported.
 * <p/>
 * <code>LoggerFactory</code> is essentially a wrapper around an
 * {@link ILoggerFactory} instance bound with <code>LoggerFactory</code> at
 * compile time.
 * <p/>
 * Please note that all methods in <code>LoggerFactory</code> are static.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Robert Elliot
 */
public final class LoggerFactory {
	// private constructor prevents instantiation
	private LoggerFactory() {
	}

	private static final String HELP_URL = "http://www.slf4j.org/codes.html";

	private static final String NO_STATIC_LOGGER_BINDER_URL = HELP_URL + "#StaticLoggerBinder";
	private static final String MULTIPLE_BINDINGS_URL = HELP_URL + "#multiple_bindings";
	private static final String VERSION_MISMATCH = HELP_URL + "#version_mismatch";
	private static final String SUBSTITUTE_LOGGER_URL = HELP_URL + "#substituteLogger";

	private static final String UNSUCCESSFUL_INIT_URL = HELP_URL + "#unsuccessfulInit";
	private static final String UNSUCCESSFUL_INIT_MSG =
			LoggerFactory.class.getName() + " could not be successfully initialized. See also " + UNSUCCESSFUL_INIT_URL;

	private static final int UNINITIALIZED = 0;
	private static final int ONGOING_INITIALIZATION = 1;
	private static final int FAILED_INITIALIZATION = 2;
	private static final int SUCCESSFUL_INITIALIZATION = 3;
	private static final int NOP_FALLBACK_INITIALIZATION = 4;

	private static volatile int INITIALIZATION_STATE = UNINITIALIZED;
	private static final SubstituteLoggerFactory TEMP_FACTORY = new SubstituteLoggerFactory();
	private static final NOPLoggerFactory NOP_FALLBACK_FACTORY = new NOPLoggerFactory();

	/**
	 * It is LoggerFactory's responsibility to track version changes and manage
	 * the compatibility list.
	 * <p/>
	 * It is assumed that all versions in the 1.6 are mutually compatible.
	 */
	private static final String[] API_COMPATIBILITY_LIST = new String[]{"1.6", "1.7"};

	private static final String STATIC_LOGGER_BINDER_CLASS = StaticLoggerBinder.class.getName();
	private static final String STATIC_LOGGER_BINDER_BASE_PATH = STATIC_LOGGER_BINDER_CLASS.replace('.', '/');
	// We need to use the name of the StaticLoggerBinder class, but we can't reference
	// the class itself.
	private static final String STATIC_LOGGER_BINDER_PATH = STATIC_LOGGER_BINDER_BASE_PATH + ".class";

	private static void performInitialization() {
		bind();
		if (INITIALIZATION_STATE == SUCCESSFUL_INITIALIZATION) {
			versionSanityCheck();
		}
	}

	private static boolean messageContainsOrgSlf4jImplStaticLoggerBinder(String msg) {
		return (null != msg
				&& (msg.contains(STATIC_LOGGER_BINDER_BASE_PATH) || msg.contains(STATIC_LOGGER_BINDER_CLASS)));
	}

	private static void bind() {
		try {
			List<URL> staticLoggerBinderPathSet = findPossibleStaticLoggerBinderPathSet();
			reportMultipleBindingAmbiguity(staticLoggerBinderPathSet);
			// the next line does the binding
			StaticLoggerBinder.getSingleton();
			INITIALIZATION_STATE = SUCCESSFUL_INITIALIZATION;
			reportActualBinding(staticLoggerBinderPathSet);
			emitSubstituteLoggerWarning();
		} catch (NoClassDefFoundError ce) {
			String msg = ce.getMessage();
			if (messageContainsOrgSlf4jImplStaticLoggerBinder(msg)) {
				INITIALIZATION_STATE = NOP_FALLBACK_INITIALIZATION;
				Util.report("Failed to load class \"" + STATIC_LOGGER_BINDER_CLASS + "\".");
				Util.report("Defaulting to no-operation (NOP) logger implementation");
				Util.report("See " + NO_STATIC_LOGGER_BINDER_URL + " for further details.");
			} else {
				failedBinding(ce);
				throw ce;
			}
		} catch (NoSuchMethodError me) {
			String msg = me.getMessage();
			if (msg != null && msg.contains(STATIC_LOGGER_BINDER_CLASS + ".getSingleton()")) {
				INITIALIZATION_STATE = FAILED_INITIALIZATION;
				Util.report("slf4j-api 1.6.x (or later) is incompatible with this binding.");
				Util.report("Your binding is version 1.5.5 or earlier.");
				Util.report("Upgrade your binding to version 1.6.x.");
			}
			throw me;
		} catch (Exception e) {
			failedBinding(e);
			throw new IllegalStateException("Unexpected initialization failure", e);
		}
	}

	private static void failedBinding(Throwable t) {
		INITIALIZATION_STATE = FAILED_INITIALIZATION;
		Util.report("Failed to instantiate SLF4J LoggerFactory", t);
	}

	private static void emitSubstituteLoggerWarning() {
		List<String> loggerNameList = TEMP_FACTORY.getLoggerNameList();
		if (loggerNameList.size() == 0) {
			return;
		}
		Util.report("The following loggers will not work because they were created");
		Util.report("during the default configuration phase of the underlying logging system.");
		Util.report("See also " + SUBSTITUTE_LOGGER_URL);
		for (String loggerName : loggerNameList) {
			Util.report(loggerName);
		}
	}

	private static void versionSanityCheck() {
		try {
			String requested = StaticLoggerBinder.requestedApiVersion();

			boolean match = false;
			for (String api : API_COMPATIBILITY_LIST) {
				if (requested.startsWith(api)) {
					match = true;
					break;
				}
			}
			if (!match) {
				Util.report("The requested version " + requested + " by your slf4j binding is not compatible with "
						+ Arrays.toString(API_COMPATIBILITY_LIST));
				Util.report("See " + VERSION_MISMATCH + " for further details.");
			}
		} catch (NoSuchFieldError fe) {
			// given our large user base and SLF4J's commitment to backward
			// compatibility, we cannot cry here. Only for implementations
			// which willingly declare a REQUESTED_API_VERSION field do we
			// emit compatibility warnings.
		} catch (Exception e) {
			// we should never reach here
			Util.report("Unexpected problem occurred during version sanity check", e);
		}
	}

	private static List<URL> findPossibleStaticLoggerBinderPathSet() {
		// use List instead of list in order to deal with  bug #138
		// LinkedHashSet appropriate here because it preserves insertion order during iteration
		List<URL> staticLoggerBinderPathSet = new ArrayList<URL>();
		try {
			ClassLoader loggerFactoryClassLoader = LoggerFactory.class.getClassLoader();
			Enumeration<URL> paths;
			if (loggerFactoryClassLoader == null) {
				paths = ClassLoader.getSystemResources(STATIC_LOGGER_BINDER_PATH);
			} else {
				paths = loggerFactoryClassLoader.getResources(STATIC_LOGGER_BINDER_PATH);
			}
			while (paths.hasMoreElements()) {
				URL path = paths.nextElement();
				staticLoggerBinderPathSet.add(path);
			}
		} catch (IOException ioe) {
			Util.report("Error getting resources from path", ioe);
		}
		return staticLoggerBinderPathSet;
	}

	private static boolean isAmbiguousStaticLoggerBinderPathSet(List staticLoggerBinderPathSet) {
		return staticLoggerBinderPathSet.size() > 1;
	}

	/**
	 * Prints a warning message on the console if multiple bindings were found on the class path.
	 * No reporting is done otherwise.
	 */
	private static void reportMultipleBindingAmbiguity(List<URL> staticLoggerBinderPathSet) {
		if (isAmbiguousStaticLoggerBinderPathSet(staticLoggerBinderPathSet)) {
			Util.report("Class path contains multiple SLF4J bindings.");
			for (URL path : staticLoggerBinderPathSet) {
				Util.report("Found binding in [" + path + "]");
			}
			Util.report("See " + MULTIPLE_BINDINGS_URL + " for an explanation.");
		}
	}

	private static void reportActualBinding(List staticLoggerBinderPathSet) {
		if (isAmbiguousStaticLoggerBinderPathSet(staticLoggerBinderPathSet)) {
			Util.report("Actual binding is of type ["
					+ StaticLoggerBinder.getSingleton().getLoggerFactoryClassStr() + "]");
		}
	}

	/**
	 * Return a logger named according to the name parameter using the statically
	 * bound {@link ILoggerFactory} instance.
	 *
	 * @param name The name of the logger.
	 * @return logger
	 */
	public static Logger getLogger(String name) {
		ILoggerFactory iLoggerFactory = getILoggerFactory();
		return iLoggerFactory.getLogger(name);
	}

	/**
	 * Return a logger named corresponding to the class passed as parameter, using
	 * the statically bound {@link ILoggerFactory} instance.
	 *
	 * @param clazz the returned logger will be named after clazz
	 * @return logger
	 */
	public static Logger getLogger(Class clazz) {
		return getLogger(clazz.getName());
	}

	/**
	 * Return the {@link ILoggerFactory} instance in use.
	 * <p/>
	 * ILoggerFactory instance is bound with this class at compile time.
	 *
	 * @return the ILoggerFactory instance in use
	 */
	private static ILoggerFactory getILoggerFactory() {
		if (INITIALIZATION_STATE == UNINITIALIZED) {
			INITIALIZATION_STATE = ONGOING_INITIALIZATION;
			performInitialization();
		}
		switch (INITIALIZATION_STATE) {
			case SUCCESSFUL_INITIALIZATION:
				return StaticLoggerBinder.getSingleton().getLoggerFactory();
			case NOP_FALLBACK_INITIALIZATION:
				return NOP_FALLBACK_FACTORY;
			case FAILED_INITIALIZATION:
				throw new IllegalStateException(UNSUCCESSFUL_INIT_MSG);
			case ONGOING_INITIALIZATION:
				// support re-entrant behavior.
				// See also http://bugzilla.slf4j.org/show_bug.cgi?id=106
				return TEMP_FACTORY;
			default:
				throw new IllegalStateException("Unreachable code");
		}
	}

}
