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

package org.slf4j.helpers;

/**
 * A direct NOP (no operation) implementation of {@link org.slf4j.Logger}.
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public final class NOPLogger extends NamedLoggerBase {
	private static final long serialVersionUID = -517220405410904473L;

	/**
	 * The unique instance of NOPLogger.
	 */
	public static final NOPLogger NOP_LOGGER = new NOPLogger();

	/**
	 * There is no point in creating multiple instances of NOPLogger,
	 * except by derived classes, hence the protected  access for the constructor.
	 */
	private NOPLogger() {
		super("NOP");
	}

	/**
	 * Always returns false.
	 *
	 * @return always false
	 */
	public boolean isTraceEnabled() {
		return false;
	}

	/**
	 * A NOP implementation.
	 */
	public void trace(String msg) {
	}

	/**
	 * A NOP implementation.
	 */
	public void trace(String format, Object arg) {
	}

	/**
	 * A NOP implementation.
	 */
	public void trace(String format, Object arg1, Object arg2) {
	}

	/**
	 * A NOP implementation.
	 */
	public void trace(String format, Object... argArray) {
	}

	/**
	 * A NOP implementation.
	 */
	public void trace(String msg, Throwable t) {
	}

	/**
	 * Always returns false.
	 *
	 * @return always false
	 */
	public boolean isDebugEnabled() {
		return false;
	}

	/**
	 * A NOP implementation.
	 */
	public void debug(String msg) {
	}

	/**
	 * A NOP implementation.
	 */
	public void debug(String format, Object arg) {
	}

	/**
	 * A NOP implementation.
	 */
	public void debug(String format, Object arg1, Object arg2) {
	}

	/**
	 * A NOP implementation.
	 */
	public void debug(String format, Object... argArray) {
	}

	/**
	 * A NOP implementation.
	 */
	public void debug(String msg, Throwable t) {
	}

	/**
	 * Always returns false.
	 *
	 * @return always false
	 */
	public boolean isInfoEnabled() {
		return false;
	}

	/**
	 * A NOP implementation.
	 */
	public void info(String msg) {
	}

	/**
	 * A NOP implementation.
	 */
	public void info(String format, Object arg1) {
	}

	/**
	 * A NOP implementation.
	 */
	public void info(String format, Object arg1, Object arg2) {
	}

	/**
	 * A NOP implementation.
	 */
	public void info(String format, Object... argArray) {
	}

	/**
	 * A NOP implementation.
	 */
	public void info(String msg, Throwable t) {
	}

	/**
	 * Always returns false.
	 *
	 * @return always false
	 */
	public boolean isWarnEnabled() {
		return false;
	}

	/**
	 * A NOP implementation.
	 */
	public void warn(String msg) {
	}

	/**
	 * A NOP implementation.
	 */
	public void warn(String format, Object arg1) {
	}

	/**
	 * A NOP implementation.
	 */
	public void warn(String format, Object arg1, Object arg2) {
	}

	/**
	 * A NOP implementation.
	 */
	public void warn(String format, Object... argArray) {
	}

	/**
	 * A NOP implementation.
	 */
	public void warn(String msg, Throwable t) {
	}

	/**
	 * A NOP implementation.
	 */
	public boolean isErrorEnabled() {
		return false;
	}

	/**
	 * A NOP implementation.
	 */
	public void error(String msg) {
	}

	/**
	 * A NOP implementation.
	 */
	public void error(String format, Object arg1) {
	}

	/**
	 * A NOP implementation.
	 */
	public void error(String format, Object arg1, Object arg2) {
	}

	/**
	 * A NOP implementation.
	 */
	public void error(String format, Object... argArray) {
	}

	/**
	 * A NOP implementation.
	 */
	public void error(String msg, Throwable t) {
	}
}
