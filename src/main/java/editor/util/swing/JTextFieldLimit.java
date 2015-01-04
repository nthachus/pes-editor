package editor.util.swing;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Used to limit {@link javax.swing.JTextField} input to a maximum length.
 */
public class JTextFieldLimit extends PlainDocument {
	private final int limit;
	private final Boolean toUppercase;

	public JTextFieldLimit(int limit, Boolean toUppercase) {
		super();
		this.limit = limit;
		this.toUppercase = toUppercase;
	}

	public JTextFieldLimit(int limit) {
		this(limit, null);
	}

	@Override
	public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
		if (null == str) return;

		if (getLength() + str.length() <= limit) {
			if (null != toUppercase && str.length() > 0) {
				if (toUppercase) str = str.toUpperCase();
				else str = str.toLowerCase();
			}

			super.insertString(offset, str, attr);
		}
	}

}
