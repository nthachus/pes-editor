package editor.ui;

import editor.data.Hairs;
import editor.data.OptionFile;
import editor.data.Stat;
import editor.data.Stats;
import editor.lang.JTextChangeListener;
import editor.lang.JTextFieldLimit;
import editor.lang.NullArgumentException;
import editor.util.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GeneralAbilityPanel extends JPanel implements ActionListener, ChangeListener {
	private static final long serialVersionUID = -7411807493467061728L;
	private static final Logger log = LoggerFactory.getLogger(GeneralAbilityPanel.class);

	private final OptionFile of;

	public GeneralAbilityPanel(OptionFile of) {
		super(new GridBagLayout());
		if (null == of) {
			throw new NullArgumentException("of");
		}
		this.of = of;

		log.debug("General Ability panel is initializing..");
		initComponents();
	}

	//region Initialize the GUI components

	private/* final*/ JComboBox nationBox;
	private/* final*/ JTextField ageField;
	private/* final*/ JTextField heightField;
	private/* final*/ JTextField weightField;
	private/* final*/ JComboBox footBox;
	private/* final*/ JComboBox weakFootAccBox;
	private/* final*/ JComboBox weakFootFreqBox;
	private/* final*/ JComboBox consistencyBox;
	private/* final*/ JComboBox conditionBox;
	private/* final*/ JComboBox injuryBox;
	private/* final*/ JComboBox fkBox;
	private/* final*/ JComboBox pkBox;
	private/* final*/ JComboBox dribbleBox;
	private/* final*/ JComboBox dkBox;
	//
	private/* final*/ JComboBox faceBox;
	private/* final*/ JTextField faceField;
	private/* final*/ JTextField hairField;
	private/* final*/ JCheckBox specHairCheck;

	private void initComponents() {
		setBorder(BorderFactory.createTitledBorder(Resources.getMessage("genAbility.title")));

		nationBox = new JComboBox/*<String>*/(Stats.NATION);

		ageField = new JTextField(2);
		ageField.setDocument(new JTextFieldLimit(Integer.toString(Stats.AGE.maxValue()).length()));
		ageField.setInputVerifier(new StatVerifier(Stats.AGE));

		heightField = new JTextField(3);
		heightField.setDocument(new JTextFieldLimit(Integer.toString(Stats.HEIGHT.maxValue()).length()));
		heightField.setInputVerifier(new StatVerifier(Stats.HEIGHT));

		weightField = new JTextField(2);
		weightField.setDocument(new JTextFieldLimit(Integer.toString(Stats.WEIGHT.maxValue()).length()));
		weightField.setInputVerifier(new StatVerifier(Stats.WEIGHT));

		footBox = new JComboBox/*<String>*/(Stats.MOD_FOOT_SIDE);

		weakFootAccBox = new JComboBox/*<String>*/(Stats.MOD_1_8);
		weakFootFreqBox = new JComboBox/*<String>*/(Stats.MOD_1_8);

		consistencyBox = new JComboBox/*<String>*/(Stats.MOD_1_8);
		conditionBox = new JComboBox/*<String>*/(Stats.MOD_1_8);

		injuryBox = new JComboBox/*<String>*/(Stats.MOD_INJURY);

		dribbleBox = new JComboBox/*<String>*/(Stats.MOD_1_4);
		dkBox = new JComboBox/*<String>*/(Stats.MOD_1_4);

		fkBox = new JComboBox/*<String>*/(Stats.MOD_FK);
		pkBox = new JComboBox/*<String>*/(Stats.MOD_PK);

		// Layout manager
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		// Register TextBox and Label
		JLabel lab = new JLabel(Resources.getMessage("genAbility.nation"));
		lab.setLabelFor(nationBox);
		c.gridx = 0;
		c.gridy++;
		add(lab, c);
		c.gridx++;
		add(nationBox, c);

		lab = new JLabel(Resources.getMessage("genAbility.age"));
		lab.setLabelFor(ageField);
		c.gridx = 0;
		c.gridy++;
		add(lab, c);
		c.gridx++;
		add(ageField, c);

		lab = new JLabel(Resources.getMessage("genAbility.height"));
		lab.setLabelFor(heightField);
		c.gridx = 0;
		c.gridy++;
		add(lab, c);
		c.gridx++;
		add(heightField, c);

		lab = new JLabel(Resources.getMessage("genAbility.weight"));
		lab.setLabelFor(weightField);
		c.gridx = 0;
		c.gridy++;
		add(lab, c);
		c.gridx++;
		add(weightField, c);

		lab = new JLabel(Resources.getMessage("genAbility.footSide"));
		lab.setLabelFor(footBox);
		c.gridx = 0;
		c.gridy++;
		add(lab, c);
		c.gridx++;
		add(footBox, c);

		// Separator
		GridBagConstraints sep = (GridBagConstraints) c.clone();
		int pad = lab.getPreferredSize().height / 3;
		sep.insets = new Insets(pad, 0, pad, 0);
		sep.gridwidth = 2;
		sep.gridx = 0;
		sep.gridy = ++c.gridy;
		add(new JSeparator(SwingConstants.HORIZONTAL), sep);

		lab = new JLabel(Resources.getMessage("genAbility.cond"));
		lab.setLabelFor(conditionBox);
		c.gridx = 0;
		c.gridy++;
		add(lab, c);
		c.gridx++;
		add(conditionBox, c);

		lab = new JLabel(Resources.getMessage("genAbility.wfAcc"));
		lab.setToolTipText(Resources.getNullableMessage("genAbility.wfAcc.tip"));
		lab.setLabelFor(weakFootAccBox);
		c.gridx = 0;
		c.gridy++;
		add(lab, c);
		c.gridx++;
		add(weakFootAccBox, c);

		lab = new JLabel(Resources.getMessage("genAbility.wfFreq"));
		lab.setToolTipText(Resources.getNullableMessage("genAbility.wfFreq.tip"));
		lab.setLabelFor(weakFootFreqBox);
		c.gridx = 0;
		c.gridy++;
		add(lab, c);
		c.gridx++;
		add(weakFootFreqBox, c);

		lab = new JLabel(Resources.getMessage("genAbility.cons"));
		lab.setLabelFor(consistencyBox);
		c.gridx = 0;
		c.gridy++;
		add(lab, c);
		c.gridx++;
		add(consistencyBox, c);

		// Separator
		sep.gridy = ++c.gridy;
		add(new JSeparator(SwingConstants.HORIZONTAL), sep);

		lab = new JLabel(Resources.getMessage("genAbility.injuryT"));
		lab.setLabelFor(injuryBox);
		c.gridx = 0;
		c.gridy++;
		add(lab, c);
		c.gridx++;
		add(injuryBox, c);

		lab = new JLabel(Resources.getMessage("genAbility.dribble"));
		lab.setLabelFor(dribbleBox);
		c.gridx = 0;
		c.gridy++;
		add(lab, c);
		c.gridx++;
		add(dribbleBox, c);

		lab = new JLabel(Resources.getMessage("genAbility.fk"));
		lab.setLabelFor(fkBox);
		c.gridx = 0;
		c.gridy++;
		add(lab, c);
		c.gridx++;
		add(fkBox, c);

		lab = new JLabel(Resources.getMessage("genAbility.pk"));
		lab.setLabelFor(pkBox);
		c.gridx = 0;
		c.gridy++;
		add(lab, c);
		c.gridx++;
		add(pkBox, c);

		lab = new JLabel(Resources.getMessage("genAbility.dk"));
		lab.setLabelFor(dkBox);
		c.gridx = 0;
		c.gridy++;
		add(lab, c);
		c.gridx++;
		add(dkBox, c);

		// Separator
		sep.gridy = ++c.gridy;
		add(new JSeparator(SwingConstants.HORIZONTAL), sep);

		// Face and Hair
		faceBox = new JComboBox/*<String>*/(Stats.MOD_FACE);
		faceBox.setActionCommand("Face");
		faceBox.addActionListener(this);

		faceField = new JTextField(4);
		faceField.setDocument(new JTextFieldLimit(Integer.toString(Stats.FACE_TYPE.maxValue()).length()));
		faceField.setInputVerifier(new StatVerifier(Stats.FACE_TYPE));

		hairField = new JTextField(4);
		hairField.setDocument(new JTextFieldLimit(Integer.toString(Stats.HAIR.maxValue()).length()));
		hairField.setInputVerifier(new StatVerifier(Stats.HAIR));
		hairField.getDocument().addDocumentListener(new JTextChangeListener(hairField, this));

		specHairCheck = new JCheckBox("Is Spec 2");
		specHairCheck.setToolTipText(Resources.getMessage("genAbility.specHair"));

		lab = new JLabel(Resources.getMessage("genAbility.face"));
		lab.setLabelFor(faceBox);
		c.gridx = 0;
		c.gridy++;
		add(lab, c);
		c.gridx++;
		JPanel pan = new JPanel(new GridLayout(0, 2));
		pan.add(faceBox);
		pan.add(faceField);
		add(pan, c);

		lab = new JLabel(Resources.getMessage("genAbility.hair"));
		lab.setLabelFor(hairField);
		c.gridx = 0;
		c.gridy++;
		add(lab, c);
		c.gridx++;
		pan = new JPanel(new GridLayout(0, 2));
		pan.add(hairField);
		pan.add(specHairCheck);
		add(pan, c);
	}

	//endregion

	public JComboBox getNationBox() {
		return nationBox;
	}

	public JTextField getAgeField() {
		return ageField;
	}

	public JTextField getHeightField() {
		return heightField;
	}

	public JTextField getWeightField() {
		return weightField;
	}

	public JComboBox getFootBox() {
		return footBox;
	}

	public JComboBox getWeakFootAccBox() {
		return weakFootAccBox;
	}

	public JComboBox getWeakFootFreqBox() {
		return weakFootFreqBox;
	}

	public JComboBox getConsistencyBox() {
		return consistencyBox;
	}

	public JComboBox getConditionBox() {
		return conditionBox;
	}

	public JComboBox getInjuryBox() {
		return injuryBox;
	}

	public JComboBox getFreeKickBox() {
		return fkBox;
	}

	public JComboBox getPenaltyBox() {
		return pkBox;
	}

	public JComboBox getDribbleBox() {
		return dribbleBox;
	}

	public JComboBox getDropKickBox() {
		return dkBox;
	}

	public JComboBox getFaceBox() {
		return faceBox;
	}

	public JTextField getFaceField() {
		return faceField;
	}

	public JTextField getHairField() {
		return hairField;
	}

	public JCheckBox getSpecHairCheck() {
		return specHairCheck;
	}

	public void load(int player) {
		log.info("Try to load general abilities for player: {}", player);

		nationBox.setSelectedItem(Stats.getString(of, player, Stats.NATIONALITY));
		ageField.setText(Stats.getString(of, player, Stats.AGE));
		heightField.setText(Stats.getString(of, player, Stats.HEIGHT));
		weightField.setText(Stats.getString(of, player, Stats.WEIGHT));

		weakFootAccBox.setSelectedItem(Stats.getString(of, player, Stats.WEAK_FOOT_ACC));
		weakFootFreqBox.setSelectedItem(Stats.getString(of, player, Stats.WEAK_FOOT_FREQ));
		consistencyBox.setSelectedItem(Stats.getString(of, player, Stats.CONSISTENCY));
		conditionBox.setSelectedItem(Stats.getString(of, player, Stats.CONDITION));
		injuryBox.setSelectedItem(Stats.getString(of, player, Stats.INJURY));
		fkBox.setSelectedItem(Stats.getString(of, player, Stats.FREE_KICK));
		pkBox.setSelectedItem(Stats.getString(of, player, Stats.PK_STYLE));
		dribbleBox.setSelectedItem(Stats.getString(of, player, Stats.DRIBBLE_STYLE));
		dkBox.setSelectedItem(Stats.getString(of, player, Stats.DK_STYLE));

		int foot = Stats.getValue(of, player, Stats.FOOT);
		int side = Stats.getValue(of, player, Stats.FAVORITE_SIDE);
		footBox.setSelectedIndex(foot * 3 + side);

		faceField.setText(Stats.getString(of, player, Stats.FACE_TYPE));
		faceBox.setSelectedItem(Stats.getString(of, player, Stats.FACE));
		hairField.setText(Stats.getString(of, player, Stats.HAIR));
		specHairCheck.setSelected(Stats.getValue(of, player, Stats.SPECIAL_HAIRSTYLES2) != 0);
	}

	public void actionPerformed(ActionEvent evt) {
		log.info("Perform general-ability changed action: {}", (null == evt) ? null : evt.getActionCommand());

		//if ("Face".equalsIgnoreCase(evt.getActionCommand())) {
		faceField.setEnabled(faceBox.getSelectedIndex() > 0);
		//}
	}

	public void stateChanged(ChangeEvent evt) {
		String s;
		if (specHairCheck.isSelected()) {
			s = null;
		} else {
			String text = hairField.getText();
			// DEBUG
			log.info("Try to analyze changed hair value: {}", text);

			try {
				int v = Integer.parseInt(text);
				s = Hairs.toString(v);
			} catch (RuntimeException ignore) {
				s = null;
			}
		}
		hairField.setToolTipText(s);
	}

	public static class StatVerifier extends InputVerifier {
		private final Stat stat;

		public StatVerifier(Stat stat) {
			this.stat = stat;
		}

		@Override
		public boolean verify(JComponent input) {
			if (!(input instanceof JTextComponent)) {
				throw new IllegalArgumentException("input");
			}

			return verify(stat, (JTextComponent) input);
		}

		public static boolean verify(Stat stat, JTextComponent tf) {
			if (null == stat) {
				throw new NullArgumentException("stat");
			}
			if (null == tf) {
				throw new NullArgumentException("tf");
			}
			String text = tf.getText();
			log.debug("Try to verify Stat '{}' value: {}", stat, text);

			try {
				int v = Integer.parseInt(text);
				if (v >= stat.minValue() && v <= stat.maxValue()) {
					return true;
				}
			} catch (NumberFormatException nfe) {
				log.info(nfe.toString());
			}

			return false;
		}
	}

}
