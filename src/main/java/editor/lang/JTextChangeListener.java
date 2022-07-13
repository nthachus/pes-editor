package editor.lang;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Value changed listener to {@link javax.swing.JTextField}.
 */
public class JTextChangeListener implements DocumentListener, Runnable {
	private final JTextComponent textField;
	private final ChangeListener changeListener;

	public JTextChangeListener(JTextComponent textField, ChangeListener changeListener) {
		if (null == textField) {
			throw new NullArgumentException("textField");
		}
		if (null == changeListener) {
			throw new NullArgumentException("changeListener");
		}
		this.textField = textField;
		this.changeListener = changeListener;
	}

	public void insertUpdate(DocumentEvent e) {
		changedUpdate(e);
	}

	public void removeUpdate(DocumentEvent e) {
		changedUpdate(e);
	}

	private final AtomicInteger lastNotifiedChange = new AtomicInteger(0);

	public void changedUpdate(DocumentEvent e) {
		lastNotifiedChange.incrementAndGet();
		EventQueue.invokeLater(this);
	}

	public void run() {
		if (lastNotifiedChange.getAndSet(0) > 0) {
			changeListener.stateChanged(new ChangeEvent(textField));
		}
	}
}
