package editor.ui;

import editor.JTextFieldLimit;
import editor.data.OptionFile;
import editor.data.Stats;
import editor.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Ability99Panel extends JPanel
		implements ActionListener, CaretListener, KeyListener {
	private static final Logger log = LoggerFactory.getLogger(Ability99Panel.class);
	public static final int MAX_VAL = 99;

	private final OptionFile of;

	private final JTextField[] fields = new JTextField[Stats.ABILITY99.length];
	private final String[] initValues = new String[fields.length];

	public Ability99Panel(OptionFile of) {
		super(new GridBagLayout());
		if (null == of) throw new NullPointerException("of");
		this.of = of;

		setBorder(BorderFactory.createTitledBorder(Strings.getMessage("ability.title")));

		GridBagConstraints grid = new GridBagConstraints();
		grid.ipadx = 2;

		Verifier99 verifier99 = new Verifier99();
		for (int i = 0; i < fields.length; i++) {
			JLabel lab = new JLabel(Stats.ABILITY99[i].getName());
			lab.setToolTipText(Strings.getMessage(lab.getText()));
			//lab.setHorizontalAlignment(SwingConstants.TRAILING);

			grid.anchor = GridBagConstraints.EAST;
			grid.gridx = 0;
			grid.gridy = i;
			add(lab, grid);

			fields[i] = new JTextField(2);
			fields[i].setDocument(new JTextFieldLimit(Integer.toString(MAX_VAL).length()));
			fields[i].setActionCommand(Integer.toString(i));
			fields[i].addActionListener(this);
			fields[i].setInputVerifier(verifier99);
			fields[i].addCaretListener(this);
			fields[i].addKeyListener(this);

			grid.anchor = GridBagConstraints.CENTER;
			grid.gridx = 1;
			grid.gridy = i;
			add(fields[i], grid);
		}
	}

	public JTextField getField(int index) {
		if (index < 0 || index >= fields.length) throw new ArrayIndexOutOfBoundsException("index");
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
		JTextField tf = (JTextField) evt.getSource();
		if (null == tf) throw new IllegalArgumentException("evt");

		int f = 0;
		try {
			f = Integer.parseInt(evt.getActionCommand());
			int v = Integer.parseInt(tf.getText());

			if (v > 0 && v <= MAX_VAL) {
				if (f < fields.length - 1) {
					fields[f + 1].requestFocus();
					fields[f + 1].selectAll();
				} else {
					fields[0].requestFocus();
					fields[0].selectAll();
				}
			} else {
				tf.setText(initValues[f]);
				tf.selectAll();
			}
		} catch (NumberFormatException nfe) {
			tf.setText(initValues[f]);
			tf.selectAll();
		}
	}

	public static class Verifier99 extends InputVerifier {
		@Override
		public boolean verify(JComponent input) {
			JTextField tf = (JTextField) input;
			if (null == tf) throw new NullPointerException("input");

			try {
				int v = Integer.parseInt(tf.getText());
				if (v >= 1 && v <= MAX_VAL) {
					return true;
				}
			} catch (NumberFormatException nfe) {
				log.info(nfe.toString());
			}

			return false;
		}
	}

	public void caretUpdate(CaretEvent evt) {
		if (null == evt) throw new NullPointerException("evt");
		JTextField tf = (JTextField) evt.getSource();
		if (null == tf) throw new IllegalArgumentException("evt");

		String text = tf.getText();
		Color bg = Color.white;

		if (!Strings.isEmpty(text)) {
			try {
				int v = Integer.parseInt(text);

				if (v >= 75 && v < 80) {
					bg = new Color(183, 255, 0);
				} else if (v >= 80 && v < 90) {
					bg = Color.yellow;
				} else if (v >= 90 && v < 95) {
					bg = Color.orange;
				} else if (v >= 80 && v <= MAX_VAL) {
					bg = Color.red;
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
		JTextField tf = (JTextField) evt.getSource();
		if (null == tf) throw new IllegalArgumentException("evt");

		try {
			int v = Integer.parseInt(tf.getText());
			int key = evt.getKeyCode();

			if (key == 38 && v < 99) {
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
