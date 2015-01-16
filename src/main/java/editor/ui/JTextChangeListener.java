package editor.ui;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Value changed listener to {@link javax.swing.JTextField}.
 */
public class JTextChangeListener implements DocumentListener, Runnable, Serializable {
	private static final long serialVersionUID = -3804385504201491051L;

	private final JTextComponent textField;
	private final ChangeListener changeListener;
	private final AtomicInteger lastNotifiedChange = new AtomicInteger(0);

	public JTextChangeListener(JTextComponent textField, ChangeListener changeListener) {
		if (null == textField) throw new NullPointerException("textField");
		if (null == changeListener) throw new NullPointerException("changeListener");
		this.textField = textField;
		this.changeListener = changeListener;
	}

	public void insertUpdate(DocumentEvent e) {
		changedUpdate(e);
	}

	public void removeUpdate(DocumentEvent e) {
		changedUpdate(e);
	}

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
