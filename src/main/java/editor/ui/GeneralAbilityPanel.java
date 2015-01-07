package editor.ui;

import editor.data.OptionFile;
import editor.data.Stat;
import editor.data.Stats;
import editor.util.Resources;
import editor.util.swing.JComboBox;
import editor.util.swing.JTextFieldLimit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class GeneralAbilityPanel extends JPanel {
	private static final Logger log = LoggerFactory.getLogger(GeneralAbilityPanel.class);
	private final OptionFile of;

	public GeneralAbilityPanel(OptionFile of) {
		super(new GridLayout(0, 2));
		if (null == of) throw new NullPointerException("of");
		this.of = of;

		initComponents();
	}

	private/* final*/ JComboBox<String> nationBox;
	private/* final*/ JTextField ageField;
	private/* final*/ JTextField heightField;
	private/* final*/ JTextField weightField;
	private/* final*/ JComboBox<String> footBox;
	private/* final*/ JComboBox<String> weakFootAccBox;
	private/* final*/ JComboBox<String> weakFootFreqBox;
	private/* final*/ JComboBox<String> consistencyBox;
	private/* final*/ JComboBox<String> conditionBox;
	private/* final*/ JComboBox<String> injuryBox;
	private/* final*/ JComboBox<String> fkBox;
	private/* final*/ JComboBox<String> pkBox;
	private/* final*/ JComboBox<String> dribbleBox;
	private/* final*/ JComboBox<String> dkBox;

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

		add(new JLabel(Resources.getMessage("genAbility.nation")));
		add(nationBox);
		add(new JLabel(Resources.getMessage("genAbility.age")));
		add(ageField);
		add(new JLabel(Resources.getMessage("genAbility.height")));
		add(heightField);
		add(new JLabel(Resources.getMessage("genAbility.weight")));
		add(weightField);
		add(new JLabel(Resources.getMessage("genAbility.footSide")));
		add(footBox);
		add(new JLabel(Resources.getMessage("genAbility.wfAcc")));
		add(weakFootAccBox);
		add(new JLabel(Resources.getMessage("genAbility.wfFreq")));
		add(weakFootFreqBox);
		add(new JLabel(Resources.getMessage("genAbility.cons")));
		add(consistencyBox);
		add(new JLabel(Resources.getMessage("genAbility.cond")));
		add(conditionBox);
		add(new JLabel(Resources.getMessage("genAbility.injuryT")));
		add(injuryBox);
		add(new JLabel(Resources.getMessage("genAbility.dribble")));
		add(dribbleBox);
		add(new JLabel(Resources.getMessage("genAbility.fk")));
		add(fkBox);
		add(new JLabel(Resources.getMessage("genAbility.pk")));
		add(pkBox);
		add(new JLabel(Resources.getMessage("genAbility.dk")));
		add(dkBox);
	}

	public JComboBox<String> getNationBox() {
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

	public JComboBox<String> getFootBox() {
		return footBox;
	}

	public JComboBox<String> getWeakFootAccBox() {
		return weakFootAccBox;
	}

	public JComboBox<String> getWeakFootFreqBox() {
		return weakFootFreqBox;
	}

	public JComboBox<String> getConsistencyBox() {
		return consistencyBox;
	}

	public JComboBox<String> getConditionBox() {
		return conditionBox;
	}

	public JComboBox<String> getInjuryBox() {
		return injuryBox;
	}

	public JComboBox<String> getFreeKickBox() {
		return fkBox;
	}

	public JComboBox<String> getPenaltyBox() {
		return pkBox;
	}

	public JComboBox<String> getDribbleBox() {
		return dribbleBox;
	}

	public JComboBox<String> getDropKickBox() {
		return dkBox;
	}

	public void load(int player) {
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
			if (null == stat) throw new NullPointerException("stat");
			this.stat = stat;
		}

		@Override
		public boolean verify(JComponent input) {
			if (null == input) throw new NullPointerException("input");
			if (!(input instanceof JTextComponent)) throw new IllegalArgumentException("input");

			JTextComponent tf = (JTextComponent) input;
			try {
				int v = Integer.parseInt(tf.getText());
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
