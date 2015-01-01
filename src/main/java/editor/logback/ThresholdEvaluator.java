package editor.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;

/**
 * Evaluates to true when the logging event passed as parameter has specified level or higher.
 */
public class ThresholdEvaluator extends EventEvaluatorBase<ILoggingEvent> {
	private volatile Level level;

	public void setLevel(String level) {
		this.level = Level.toLevel(level);
	}

	/**
	 * Return true if event passed as parameter has specified level or higher, returns false otherwise.
	 */
	public boolean evaluate(ILoggingEvent event) throws NullPointerException, EvaluationException {
		return (null == level || event.getLevel().levelInt >= level.levelInt);
	}
}
