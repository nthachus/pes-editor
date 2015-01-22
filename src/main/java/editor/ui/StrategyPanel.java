package editor.ui;

import editor.data.ControlButton;
import editor.data.Formations;
import editor.data.OptionFile;
import editor.data.Player;
import editor.lang.NullArgumentException;
import editor.util.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

public class StrategyPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 3335107620381551627L;
	private static final Logger log = LoggerFactory.getLogger(StrategyPanel.class);

	private static final int CB_OVERLAP = 6;

	private final OptionFile of;
	private final SquadList list;

	private volatile boolean isOk = false;
	private volatile int squad = 0;
	private volatile boolean isAuto = false;

	private final JComboBox[] buttonBoxes = new JComboBox[ControlButton.size()];
	private final JLabel[] labels = new JLabel[buttonBoxes.length];
	private/* final*/ JButton autoButton;
	private/* final*/ JComboBox/*<SweepItem>*/ overlapBox;

	public StrategyPanel(OptionFile of, SquadList squadList) {
		super(new GridBagLayout());
		if (null == of) {
			throw new NullArgumentException("of");
		}
		if (null == squadList) {
			throw new NullArgumentException("squadList");
		}
		this.of = of;
		list = squadList;

		log.debug("Initialize Strategy panel with Squad list #{}", squadList.hashCode());
		initComponents();
	}

	private static String[] getItems() {
		String s = Resources.getMessage("strategy.items");
		return s.split("\\s*,\\s*");
	}

	@SuppressWarnings("unchecked")
	private void initComponents() {
		setBorder(BorderFactory.createTitledBorder(Resources.getMessage("strategy.title")));

		String[] items = getItems();
		for (int i = 0; i < buttonBoxes.length; i++) {
			labels[i] = new JLabel();
			labels[i].setPreferredSize(new Dimension(42, 17));
			labels[i].setText(null);
			labels[i].setIcon(new Ps2ButtonIcon(ControlButton.valueOf(i)));

			buttonBoxes[i] = new JComboBox/*<String>*/(items);
			buttonBoxes[i].setActionCommand(Integer.toString(i));
			buttonBoxes[i].addActionListener(this);
		}

		autoButton = new JButton(Resources.getMessage("strategy.manual"));
		autoButton.setPreferredSize(new Dimension(93, 26));
		autoButton.setActionCommand("Auto");
		autoButton.addActionListener(this);

		overlapBox = new JComboBox/*<SweepItem>*/();
		overlapBox.setActionCommand("OverlapCB");
		overlapBox.addActionListener(this);

		GridBagConstraints c = new GridBagConstraints();
		//c.anchor = GridBagConstraints.EAST;

		int x, y;
		for (int i = 0; i < buttonBoxes.length; i++) {
			if (i < 2) {
				x = i + 1;
				y = 0;
			} else {
				x = i - 1;
				y = 2;
			}

			c.gridx = x;
			c.gridy = y;
			add(labels[i], c);

			c.gridx = x;
			c.gridy = y + 1;
			add(buttonBoxes[i], c);
		}

		c.gridx = 0;
		c.gridy = 0;
		add(autoButton, c);

		c.gridx = 0;
		c.gridy = 4;
		add(new JLabel(Resources.getMessage("strategy.olCB")), c);

		c.gridwidth = 2;// 1
		c.gridx = 1;
		c.gridy = 4;
		add(overlapBox, c);
	}

	private void autoStrategy() {
		isAuto = !isAuto;
		if (isAuto) {
			autoButton.setText(Resources.getMessage("strategy.auto"));
		} else {
			autoButton.setText(Resources.getMessage("strategy.manual"));
		}

		Formations.setStrategyAuto(of, squad, isAuto);
		refresh(squad);

		log.debug("Update succeeded on auto-strategy: {} for squad: {}", isAuto, squad);
	}

	private void overlapCB() {
		SweepItem item = (SweepItem) overlapBox.getSelectedItem();
		if (null != item) {
			Formations.setCBOverlap(of, squad, item.index);
		}
		log.debug("Update succeeded on CB-Overlap: {} for squad: {}", item, squad);
	}

	public void actionPerformed(ActionEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		if (!isOk) {
			return;
		}
		log.info("Perform Strategy action: '{}' for squad: {}", evt.getActionCommand(), squad);

		if ("Auto".equalsIgnoreCase(evt.getActionCommand())) {
			autoStrategy();
		} else if ("OverlapCB".equalsIgnoreCase(evt.getActionCommand())) {
			overlapCB();
		} else {

			int btnId = Integer.parseInt(evt.getActionCommand());
			int strategy = buttonBoxes[btnId].getSelectedIndex();
			Formations.setStrategy(of, squad, btnId, strategy);

			if (strategy == CB_OVERLAP && Formations.getCBOverlap(of, squad) == 0) {
				for (int i = 1; i < Formations.PLAYER_COUNT; i++) {
					int pos = Formations.getPosition(of, squad, 0, i);
					if (pos > 0 && pos < 8) {
						Formations.setCBOverlap(of, squad, i);
						break;
					}
				}
			}

			refresh(squad);
			// DEBUG
			log.debug("Updated succeeded on strategy: {} for squad: {}", strategy, squad);
		}
	}

	public void refresh(int squad) {
		log.info("Try to refresh Strategy for squad: {}", squad);
		isOk = false;
		this.squad = squad;

		refreshAutoButton();

		boolean olCB = false;
		for (int i = 0; i < buttonBoxes.length; i++) {
			int strategy = Formations.getStrategy(of, squad, i);
			buttonBoxes[i].setSelectedIndex(strategy);

			if (strategy == CB_OVERLAP) {
				olCB = true;
			}
		}

		refreshOverlapBox(olCB);

		isOk = true;
		log.debug("Refresh completed on Strategy panel for squad: {}, CB-Overlap: {}", squad, olCB);
	}

	private void refreshAutoButton() {
		if (Formations.getStrategyAuto(of, squad)) {
			isAuto = true;
			autoButton.setText(Resources.getMessage("strategy.auto"));

			for (int i = 0; i < labels.length; i++) {
				if (i == 0) {
					labels[i].setText("L2");
				} else {
					labels[i].setText("AUTO " + i);
				}
				labels[i].setIcon(null);
			}
		} else {
			isAuto = false;
			autoButton.setText(Resources.getMessage("strategy.manual"));

			for (int i = 0; i < labels.length; i++) {
				labels[i].setText(null);
				labels[i].setIcon(new Ps2ButtonIcon(ControlButton.valueOf(i)));
			}
		}
		// DEBUG
		log.debug("Refresh completed on Auto button: {} for squad: {}", isAuto, squad);
	}

	@SuppressWarnings("unchecked")
	private void refreshOverlapBox(boolean olCB) {
		overlapBox.removeAllItems();

		int count = 0, sel = -1;
		Player p;
		for (int i = 1; i < Formations.PLAYER_COUNT; i++) {
			int pos = Formations.getPosition(of, squad, 0, i);
			if (pos > 0 && pos < 8) {
				if (i == Formations.getCBOverlap(of, squad)) {
					sel = count;
				}

				p = (Player) list.getModel().getElementAt(i);
				overlapBox.addItem(new SweepItem(i, p.getName()));

				count++;
			}
		}

		overlapBox.setSelectedIndex(sel);
		overlapBox.setEnabled(olCB && overlapBox.getItemCount() > 0);

		log.debug("Reload completed {} items on Overlap dropdown with CB-Overlap: {}", count, olCB);
	}

	private static class SweepItem implements Serializable {
		private static final long serialVersionUID = -3445292562668385712L;

		private final int index;
		private final String name;

		public SweepItem(int index, String playerName) {
			this.index = index;
			name = playerName;
		}

		@Override
		public String toString() {
			return name;
		}
	}

}
