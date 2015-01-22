package editor.ui;

import editor.data.OptionFile;
import editor.lang.JTextFieldLimit;
import editor.lang.NullArgumentException;
import editor.util.Bits;
import editor.util.Resources;
import editor.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WenPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1283642115923368976L;
	private static final Logger log = LoggerFactory.getLogger(WenPanel.class);

	private static final int MAX_WEN = 99999;
	private static final int START_ADR = 5208;
	private static final int ALT_ADR = 52;

	private final OptionFile of;

	private/* final*/ JLabel current;
	private/* final*/ JTextField field;

	public WenPanel(OptionFile of) {
		super();
		if (null == of) {
			throw new NullArgumentException("of");
		}
		this.of = of;
		// DEBUG
		log.debug("WEN panel is initializing..");

		initComponents();
		//refresh();
	}

	private void initComponents() {
		field = new JTextField(8);
		field.setToolTipText(Resources.getMessage("wen.tooltip", MAX_WEN));
		field.setDocument(new JTextFieldLimit(Integer.toString(MAX_WEN).length()));
		field.addActionListener(this);

		current = new JLabel();

		JPanel contentPane = new JPanel(new GridLayout(0, 1));
		contentPane.setBorder(BorderFactory.createTitledBorder(Resources.getMessage("wen.title")));
		contentPane.add(field);
		contentPane.add(current);

		add(contentPane);
	}

	public void refresh() {
		long wen = Bits.toInt(of.getData(), ALT_ADR, 3);
		long wen2 = Bits.toInt(of.getData(), START_ADR, 3);

		current.setText(Resources.getMessage("wen.label", wen2));
		current.setToolTipText((wen != wen2) ? Long.toString(wen) : null);
		field.setText(Strings.EMPTY);

		log.info("WEN panel is refreshed with: {} / {}", wen2, wen);
	}

	private void setWen(int newWen) {
		log.debug("Try to set PES: {}", newWen);

		if (newWen >= 0 && newWen <= MAX_WEN) {
			byte[] temp = Bits.toBytes(newWen);
			System.arraycopy(temp, 0, of.getData(), ALT_ADR, 3);
			System.arraycopy(temp, 0, of.getData(), START_ADR, 3);

			refresh();
		} else {
			field.setText(Strings.EMPTY);
			JOptionPane.showMessageDialog(null,
					Resources.getMessage("msg.invalidWen", MAX_WEN), Resources.getMessage("Error"),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void actionPerformed(ActionEvent evt) {
		String text = field.getText();
		log.info("Try to perform WEN updating: {}", text);

		int newWen;
		try {
			newWen = Integer.parseInt(text);
		} catch (NumberFormatException nfe) {
			newWen = -1;
		}
		setWen(newWen);
	}

}
