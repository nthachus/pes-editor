package editor.ui;

import editor.data.OptionFile;
import editor.data.Stats;
import editor.lang.JTextChangeListener;
import editor.lang.JTextFieldLimit;
import editor.lang.NullArgumentException;
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

public class Ability99Panel extends JPanel implements ActionListener, ChangeListener, KeyListener {
	private static final long serialVersionUID = 8544889045993110691L;
	private static final Logger log = LoggerFactory.getLogger(Ability99Panel.class);

	private final OptionFile of;

	private final JTextField[] fields = new JTextField[Stats.ABILITY99.length];
	private final String[] initValues = new String[fields.length];

	public Ability99Panel(OptionFile of) {
		super(new GridBagLayout());
		if (null == of) {
			throw new NullArgumentException("of");
		}
		this.of = of;

		log.debug("Ability99 panel is initializing..");
		initComponents();
	}

	private void initComponents() {
		setBorder(BorderFactory.createTitledBorder(Resources.getMessage("ability.title")));

		GridBagConstraints grid = new GridBagConstraints();
		grid.ipadx = 2;

		Verifier99 verifier99 = new Verifier99();
		JLabel lab;
		String labText;
		for (int i = 0; i < fields.length; i++) {

			labText = Stats.ABILITY99[i].getName();
			lab = new JLabel(Resources.getMessage(labText));
			lab.setToolTipText(Resources.getNullableMessage(labText + ".tip"));
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

			lab.setLabelFor(fields[i]);

			grid.anchor = GridBagConstraints.CENTER;
			grid.gridx = 1;
			grid.gridy = i;
			add(fields[i], grid);
		}
	}

	public JTextField getField(int index) {
		if (index < 0 || index >= fields.length) {
			throw new ArrayIndexOutOfBoundsException(Integer.toString(index));
		}
		return fields[index];
	}

	public void load(int player) {
		log.info("Try to load all abilities for player: {}", player);

		for (int i = 0; i < fields.length; i++) {
			initValues[i] = Stats.getString(of, player, Stats.ABILITY99[i]);
			fields[i].setText(initValues[i]);
		}

		log.debug("Loading of all abilities for player {} succeeded", player);
	}

	public void actionPerformed(ActionEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		if (!(evt.getSource() instanceof JTextComponent)) {
			throw new IllegalArgumentException("evt");
		}
		log.info("Try to update ability field: {}", evt.getActionCommand());

		JTextComponent tf = (JTextComponent) evt.getSource();
		int f = Integer.parseInt(evt.getActionCommand());

		if (Verifier99.verify(tf)) {
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
	}

	public void stateChanged(ChangeEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		if (!(evt.getSource() instanceof JTextComponent)) {
			throw new IllegalArgumentException("evt");
		}

		JTextComponent tf = (JTextComponent) evt.getSource();
		String text = tf.getText();

		Color bg = Color.WHITE;
		if (!Strings.isBlank(text)) {
			log.debug("Try to change color for ability: {}", text);
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

		tf.setBackground(bg);// TODO: should call UIUtil.javaUI()
	}

	public void keyTyped(KeyEvent evt) {
		// Handle key-press event only
	}

	public void keyPressed(KeyEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		int key = evt.getKeyCode();
		if (key != KeyEvent.VK_UP && key != KeyEvent.VK_DOWN) {
			return;
		}

		if (!(evt.getSource() instanceof JTextComponent)) {
			throw new IllegalArgumentException("evt");
		}
		JTextComponent tf = (JTextComponent) evt.getSource();
		String text = tf.getText();
		log.debug("Try to perform key {} on field: {}", key, text);

		try {
			int v = Integer.parseInt(text);
			if (key == KeyEvent.VK_UP) {
				if (v < Stats.MAX_STAT99) {
					tf.setText(Integer.toString(v + 1));
				}
			} else if (v > 1) {
				tf.setText(Integer.toString(v - 1));
			}
		} catch (NumberFormatException nfe) {
			log.info(nfe.toString());
		}
	}

	public void keyReleased(KeyEvent evt) {
		// Handle key-press event only
	}

	public static class Verifier99 extends InputVerifier {
		@Override
		public boolean verify(JComponent input) {
			if (!(input instanceof JTextComponent)) {
				throw new IllegalArgumentException("input");
			}

			return verify((JTextComponent) input);
		}

		public static boolean verify(JTextComponent tf) {
			if (null == tf) {
				throw new NullArgumentException("tf");
			}
			try {
				int v = Integer.parseInt(tf.getText());
				if (v > 0 && v <= Stats.MAX_STAT99) {
					return true;
				}
			} catch (NumberFormatException nfe) {
				return false;
			}
			return false;
		}
	}

}
