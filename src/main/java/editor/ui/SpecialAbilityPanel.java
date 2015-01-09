package editor.ui;

import editor.data.OptionFile;
import editor.data.Stats;
import editor.util.Resources;

import javax.swing.*;
import java.awt.*;

public class SpecialAbilityPanel extends JPanel {
	private final OptionFile of;

	private final JCheckBox[] abilityCheck = new JCheckBox[Stats.ABILITY_SPECIAL.length];

	public SpecialAbilityPanel(OptionFile of) {
		super(new GridLayout(0, 1));
		if (null == of) throw new NullPointerException("of");
		this.of = of;

		setBorder(BorderFactory.createTitledBorder(Resources.getMessage("specAbility.title")));

		for (int i = 0; i < abilityCheck.length; i++) {
			abilityCheck[i] = new JCheckBox(Stats.ABILITY_SPECIAL[i].getName());
			abilityCheck[i].setToolTipText(Resources.getMessage(abilityCheck[i].getText()));

			add(abilityCheck[i]);
		}
	}

	public JCheckBox getAbilityCheck(int index) {
		if (index < 0 || index >= abilityCheck.length)
			throw new ArrayIndexOutOfBoundsException(Integer.toString(index));
		return abilityCheck[index];
	}

	public void load(int player) {
		for (int i = 0; i < abilityCheck.length; i++) {
			if (Stats.getValue(of, player, Stats.ABILITY_SPECIAL[i]) != 0) {
				abilityCheck[i].setSelected(true);
			} else {
				abilityCheck[i].setSelected(false);
			}
		}
	}

}
