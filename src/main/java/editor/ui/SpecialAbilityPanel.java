package editor.ui;

import editor.data.OptionFile;
import editor.data.Stats;
import editor.lang.NullArgumentException;
import editor.util.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class SpecialAbilityPanel extends JPanel {
	private static final long serialVersionUID = 5396699026145364222L;
	private static final Logger log = LoggerFactory.getLogger(SpecialAbilityPanel.class);

	private final OptionFile of;
	private final JCheckBox[] abilityCheck = new JCheckBox[Stats.ABILITY_SPECIAL.length];

	public SpecialAbilityPanel(OptionFile of) {
		super(new GridLayout(0, 1));
		if (null == of) {
			throw new NullArgumentException("of");
		}
		this.of = of;

		log.debug("Special Abilities panel is initializing..");
		initComponents();
	}

	private void initComponents() {
		setBorder(BorderFactory.createTitledBorder(Resources.getMessage("specAbility.title")));

		String labText;
		for (int i = 0; i < abilityCheck.length; i++) {
			labText = Stats.ABILITY_SPECIAL[i].getName();
			abilityCheck[i] = new JCheckBox(Resources.getMessage(labText));
			abilityCheck[i].setToolTipText(Resources.getNullableMessage(labText + ".tip"));

			add(abilityCheck[i]);
		}
	}

	public JCheckBox getAbilityCheck(int index) {
		if (index < 0 || index >= abilityCheck.length) {
			throw new ArrayIndexOutOfBoundsException(Integer.toString(index));
		}
		return abilityCheck[index];
	}

	public void load(int player) {
		log.info("Try to load special abilities for player {}", player);

		for (int i = 0; i < abilityCheck.length; i++) {
			int v = Stats.getValue(of, player, Stats.ABILITY_SPECIAL[i]);
			abilityCheck[i].setSelected(v != 0);
		}
	}

}
