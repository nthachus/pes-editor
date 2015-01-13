package editor.ui;

import editor.data.OptionFile;
import editor.data.Stats;
import editor.util.Resources;
import editor.util.Strings;
import editor.util.UIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Ability99Panel extends JPanel
		implements ActionListener, ChangeListener, KeyListener {
	private static final Logger log = LoggerFactory.getLogger(Ability99Panel.class);

	private final OptionFile of;

	private final JTextField[] fields = new JTextField[Stats.ABILITY99.length];
	private final String[] initValues = new String[fields.length];

	public Ability99Panel(OptionFile of) {
		super(new GridBagLayout());
		if (null == of) throw new NullPointerException("of");
		this.of = of;

		setBorder(BorderFactory.createTitledBorder(Resources.getMessage("ability.title")));

		GridBagConstraints grid = new GridBagConstraints();
		grid.ipadx = 2;

		Verifier99 verifier99 = new Verifier99();
		for (int i = 0; i < fields.length; i++) {
			JLabel lab = new JLabel(Stats.ABILITY99[i].getName());
			lab.setToolTipText(Resources.getNullableMessage(lab.getText()));
			//lab.setHorizontalAlignment(SwingConstants.TRAILING);

			grid.anchor = GridBagConstraints.EAST;
			grid.gridx = 0;
			grid.gridy = i;
			add(lab, grid);

			fields[i] = new JTextField(2);
			fields[i].setDocument(new JTextFieldLimit(Integer.toString(Stats.MAX_STAT99).length()));
			fields[i].setActionCommand(Integer.toString(i));
			fields[i].addActionListener(this);
			fields[i].setInputVerifier(verifier99);
			fields[i].getDocument().addDocumentListener(new JTextChangeListener(fields[i], this));
			fields[i].addKeyListener(this);

			grid.anchor = GridBagConstraints.CENTER;
			grid.gridx = 1;
			grid.gridy = i;
			add(fields[i], grid);
		}
	}

	public JTextField getField(int index) {
		if (index < 0 || index >= fields.length)
			throw new ArrayIndexOutOfBoundsException(Integer.toString(index));
		return fields[index];
	}

	public void load(int player) {
		for (int i = 0; i < fields.length; i++) {
			initValues[i] = Stats.getString(of, player, Stats.ABILITY99[i]);
			fields[i].setText(initValues[i]);
		}
	}

	public void actionPerformed(ActionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");
		if (!(evt.getSource() instanceof JTextComponent)) throw new IllegalArgumentException("evt");

		JTextComponent tf = (JTextComponent) evt.getSource();
		int f = Integer.parseInt(evt.getActionCommand());

		boolean invalid = false;
		try {
			int v = Integer.parseInt(tf.getText());
			if (v > 0 && v <= Stats.MAX_STAT99) {
				if (f < fields.length - 1) {
					fields[f + 1].requestFocus();
					fields[f + 1].selectAll();
				} else {
					fields[0].requestFocus();
					fields[0].selectAll();
				}
			} else {
				invalid = true;
			}
		} catch (NumberFormatException nfe) {
			invalid = true;
		}

		if (invalid) {
			tf.setText(initValues[f]);
			tf.selectAll();
		}
	}

	public static class Verifier99 extends InputVerifier {
		@Override
		public boolean verify(JComponent input) {
			if (null == input) throw new NullPointerException("input");
			if (!(input instanceof JTextComponent)) throw new IllegalArgumentException("input");

			JTextComponent tf = (JTextComponent) input;
			try {
				int v = Integer.parseInt(tf.getText());
				if (v >= 1 && v <= Stats.MAX_STAT99) {
					return true;
				}
			} catch (NumberFormatException nfe) {
				log.info(nfe.toString());
			}

			return false;
		}
	}

	public void stateChanged(ChangeEvent evt) {
		if (null == evt) throw new NullPointerException("evt");
		if (!(evt.getSource() instanceof JTextComponent)) throw new IllegalArgumentException("evt");

		JTextComponent tf = (JTextComponent) evt.getSource();
		String text = tf.getText();
		Color bg = Color.WHITE;

		if (!Strings.isEmpty(text)) {
			try {
				int v = Integer.parseInt(text);
				if (v >= 75 && v < 80) {
					bg = UIUtil.CHARTREUSE0;
				} else if (v >= 80 && v < 90) {
					bg = Color.YELLOW;
				} else if (v >= 90 && v < 95) {
					bg = Color.ORANGE;
				} else if (v >= 95) {
					bg = Color.RED;
				}
			} catch (NumberFormatException nfe) {
				log.info(nfe.toString());
			}
		}

		tf.setBackground(bg);
	}

	public void keyTyped(KeyEvent evt) {
	}

	public void keyPressed(KeyEvent evt) {
		if (null == evt) throw new NullPointerException("evt");
		if (!(evt.getSource() instanceof JTextComponent)) throw new IllegalArgumentException("evt");

		JTextComponent tf = (JTextComponent) evt.getSource();
		try {
			int v = Integer.parseInt(tf.getText());
			int key = evt.getKeyCode();

			if (key == 38 && v < Stats.MAX_STAT99) {
				tf.setText(Integer.toString(v + 1));
			} else if (key == 40 && v > 1) {
				tf.setText(Integer.toString(v - 1));
			}
		} catch (NumberFormatException nfe) {
			log.info(nfe.toString());
		}
	}

	public void keyReleased(KeyEvent evt) {
	}

}
