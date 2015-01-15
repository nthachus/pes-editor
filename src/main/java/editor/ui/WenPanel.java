package editor.ui;

import editor.data.OptionFile;
import editor.util.Bits;
import editor.util.Resources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WenPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1283642115923368976L;

	public static final int MAX_WEN = 99999;
	private static final int START_ADR = 5208;
	private static final int ALT_ADR = 52;

	private final OptionFile of;

	private final JLabel current;
	private final JTextField field;

	public WenPanel(OptionFile optionFile) {
		super();
		if (null == optionFile) throw new NullPointerException("optionFile");
		of = optionFile;

		current = new JLabel();

		field = new JTextField(8);
		field.setToolTipText(Resources.getMessage("wen.tooltip", MAX_WEN));
		field.setDocument(new JTextFieldLimit(Integer.toString(MAX_WEN).length()));
		field.addActionListener(this);

		JPanel contentPane = new JPanel(new GridLayout(0, 1));
		contentPane.setBorder(BorderFactory.createTitledBorder(Resources.getMessage("wen.title")));
		contentPane.add(field);
		contentPane.add(current);

		add(contentPane);
		refresh();
	}

	public void refresh() {
		long wen = Bits.toInt(of.getData(), ALT_ADR, 3);
		long wen2 = Bits.toInt(of.getData(), START_ADR, 3);

		current.setText(Resources.getMessage("wen.label", wen2));
		current.setToolTipText((wen != wen2) ? Long.toString(wen) : null);
		field.setText("");
	}

	public void setWen(int newWen) {
		if (newWen >= 0 && newWen <= MAX_WEN) {
			byte[] temp = Bits.toBytes(newWen);
			System.arraycopy(temp, 0, of.getData(), ALT_ADR, 3);
			System.arraycopy(temp, 0, of.getData(), START_ADR, 3);

			refresh();
		} else {
			field.setText("");
			JOptionPane.showMessageDialog(null,
					Resources.getMessage("msg.invalidWen", MAX_WEN), Resources.getMessage("Error"),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void actionPerformed(ActionEvent evt) {
		try {
			setWen(Integer.parseInt(field.getText()));
		} catch (NumberFormatException nfe) {
			JOptionPane.showMessageDialog(null,
					nfe.getLocalizedMessage(), Resources.getMessage("Error"), JOptionPane.ERROR_MESSAGE);
		}
	}

}
