package editor;

import editor.data.OptionFile;
import editor.data.Stats;

import javax.swing.*;
import java.awt.*;

public class GeneralAbilityPanel extends JPanel {
	private final OptionFile of;

	private final JComboBox<String> nationBox;
	private final JTextField ageField;
	private final JTextField heightField;
	private final JTextField weightField;
	private final JComboBox<String> footBox;
	private final JComboBox<String> weakFootAccBox;
	private final JComboBox<String> weakFootFreqBox;
	private final JComboBox<String> consistencyBox;
	private final JComboBox<String> conditionBox;
	private final JComboBox<String> injuryBox;
	private final JComboBox<String> fkBox;
	private final JComboBox<String> pkBox;
	private final JComboBox<String> dribbleBox;
	private final JComboBox<String> dkBox;

	public GeneralAbilityPanel(OptionFile opf) {
		super(new GridLayout(0, 2));
		of = opf;
		setBorder(BorderFactory.createTitledBorder("General"));
		// stats = s;
		nationBox = new JComboBox<String>(Stats.NATION);
		ageField = new JTextField(2);// TODO: maxlength
		ageField.setInputVerifier(new VerifierAge());
		heightField = new JTextField(2);
		heightField.setInputVerifier(new VerifierHeight());
		weightField = new JTextField(2);
		weightField.setInputVerifier(new VerifierWeight());
		String[] modF = {
				"R foot / R side", "R foot / L side",
				"R foot / B side", "L foot / L side", "L foot / R side",
				"L foot / B side"
		};
		footBox = new JComboBox<String>(modF);
		weakFootAccBox = new JComboBox<String>(Stats.MOD_1_8);
		weakFootFreqBox = new JComboBox<String>(Stats.MOD_1_8);
		consistencyBox = new JComboBox<String>(Stats.MOD_1_8);
		conditionBox = new JComboBox<String>(Stats.MOD_1_8);
		injuryBox = new JComboBox<String>(Stats.MOD_INJURY);
		String[] mod14 = {"1", "2", "3", "4"};
		dribbleBox = new JComboBox<String>(mod14);
		dkBox = new JComboBox<String>(mod14);
		String[] mod19 = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
		fkBox = new JComboBox<String>(mod19);
		String[] mod15 = {"1", "2", "3", "4", "5"};
		pkBox = new JComboBox<String>(mod15);

		add(new JLabel("Nationality"));
		add(nationBox);
		add(new JLabel("Age"));
		add(ageField);
		add(new JLabel("Height"));
		add(heightField);
		add(new JLabel("Weight"));
		add(weightField);
		add(new JLabel("Foot / Side"));
		add(footBox);
		add(new JLabel("Weak Foot Accuracy"));
		add(weakFootAccBox);
		add(new JLabel("Weak Foot Frequency"));
		add(weakFootFreqBox);
		add(new JLabel("Consistency"));
		add(consistencyBox);
		add(new JLabel("Condition"));
		add(conditionBox);
		add(new JLabel("Injury Tolerancy"));
		add(injuryBox);
		add(new JLabel("Dribble Style"));
		add(dribbleBox);
		add(new JLabel("Free Kick Style"));
		add(fkBox);
		add(new JLabel("Penalty Style"));
		add(pkBox);
		add(new JLabel("Drop Kick Style"));
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
		int item = (foot * 3) + side;
		footBox.setSelectedIndex(item);
	}

	public static class VerifierHeight extends InputVerifier {
		public boolean verify(JComponent input) {
			boolean ok = false;
			JTextField tf = (JTextField) input;
			try {
				int v = Integer.parseInt(tf.getText());
				if (v >= 148 && v <= 211) {
					ok = true;
				}
			} catch (NumberFormatException nfe) {
				ok = false;
			}
			return ok;
		}
	}

	public static class VerifierWeight extends InputVerifier {
		public boolean verify(JComponent input) {
			boolean ok = false;
			JTextField tf = (JTextField) input;
			try {
				int v = Integer.parseInt(tf.getText());
				if (v >= 1 && v < 128) {
					ok = true;
				}
			} catch (NumberFormatException nfe) {
				ok = false;
			}
			return ok;
		}
	}

	public static class VerifierAge extends InputVerifier {
		public boolean verify(JComponent input) {
			boolean ok = false;
			JTextField tf = (JTextField) input;
			try {
				int v = Integer.parseInt(tf.getText());
				if (v >= 15 && v <= 46) {
					ok = true;
				}
			} catch (NumberFormatException nfe) {
				ok = false;
			}
			return ok;
		}
	}

}
