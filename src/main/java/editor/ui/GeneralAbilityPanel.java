package editor.ui;

import editor.data.OptionFile;
import editor.data.Stat;
import editor.data.Stats;
import editor.lang.JTextFieldLimit;
import editor.lang.NullArgumentException;
import editor.util.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class GeneralAbilityPanel extends JPanel {
	private static final long serialVersionUID = -7411807493467061728L;
	private static final Logger log = LoggerFactory.getLogger(GeneralAbilityPanel.class);

	private final OptionFile of;

	public GeneralAbilityPanel(OptionFile of) {
		super(new GridLayout(0, 2));
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

	private void initComponents() {
		setBorder(BorderFactory.createTitledBorder(Resources.getMessage("genAbility.title")));

		nationBox = new JComboBox<String>(Stats.NATION);

		ageField = new JTextField(2);
		ageField.setDocument(new JTextFieldLimit(Integer.toString(Stats.AGE.maxValue()).length()));
		ageField.setInputVerifier(new StatVerifier(Stats.AGE));

		heightField = new JTextField(2);
		heightField.setDocument(new JTextFieldLimit(Integer.toString(Stats.HEIGHT.maxValue()).length()));
		heightField.setInputVerifier(new StatVerifier(Stats.HEIGHT));

		weightField = new JTextField(2);
		weightField.setDocument(new JTextFieldLimit(Integer.toString(Stats.WEIGHT.maxValue()).length()));
		weightField.setInputVerifier(new StatVerifier(Stats.WEIGHT));

		footBox = new JComboBox<String>(Stats.MOD_FOOT_SIDE);

		weakFootAccBox = new JComboBox<String>(Stats.MOD_1_8);
		weakFootFreqBox = new JComboBox<String>(Stats.MOD_1_8);

		consistencyBox = new JComboBox<String>(Stats.MOD_1_8);
		conditionBox = new JComboBox<String>(Stats.MOD_1_8);

		injuryBox = new JComboBox<String>(Stats.MOD_INJURY);

		dribbleBox = new JComboBox<String>(Stats.MOD_1_4);
		dkBox = new JComboBox<String>(Stats.MOD_1_4);

		fkBox = new JComboBox<String>(Stats.MOD_FK);
		pkBox = new JComboBox<String>(Stats.MOD_PK);

		// Register TextBox and Label
		JLabel lab = new JLabel(Resources.getMessage("genAbility.nation"));
		lab.setLabelFor(nationBox);
		add(lab);
		add(nationBox);

		lab = new JLabel(Resources.getMessage("genAbility.age"));
		lab.setLabelFor(ageField);
		add(lab);
		add(ageField);

		lab = new JLabel(Resources.getMessage("genAbility.height"));
		lab.setLabelFor(heightField);
		add(lab);
		add(heightField);

		lab = new JLabel(Resources.getMessage("genAbility.weight"));
		lab.setLabelFor(weightField);
		add(lab);
		add(weightField);

		lab = new JLabel(Resources.getMessage("genAbility.footSide"));
		lab.setLabelFor(footBox);
		add(lab);
		add(footBox);

		lab = new JLabel(Resources.getMessage("genAbility.wfAcc"));
		lab.setLabelFor(weakFootAccBox);
		add(lab);
		add(weakFootAccBox);

		lab = new JLabel(Resources.getMessage("genAbility.wfFreq"));
		lab.setLabelFor(weakFootFreqBox);
		add(lab);
		add(weakFootFreqBox);

		lab = new JLabel(Resources.getMessage("genAbility.cons"));
		lab.setLabelFor(consistencyBox);
		add(lab);
		add(consistencyBox);

		lab = new JLabel(Resources.getMessage("genAbility.cond"));
		lab.setLabelFor(conditionBox);
		add(lab);
		add(conditionBox);

		lab = new JLabel(Resources.getMessage("genAbility.injuryT"));
		lab.setLabelFor(injuryBox);
		add(lab);
		add(injuryBox);

		lab = new JLabel(Resources.getMessage("genAbility.dribble"));
		lab.setLabelFor(dribbleBox);
		add(lab);
		add(dribbleBox);

		lab = new JLabel(Resources.getMessage("genAbility.fk"));
		lab.setLabelFor(fkBox);
		add(lab);
		add(fkBox);

		lab = new JLabel(Resources.getMessage("genAbility.pk"));
		lab.setLabelFor(pkBox);
		add(lab);
		add(pkBox);

		lab = new JLabel(Resources.getMessage("genAbility.dk"));
		lab.setLabelFor(dkBox);
		add(lab);
		add(dkBox);
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

	public void load(int player) {
		log.debug("Try to load general abilities for player: {}", player);

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
