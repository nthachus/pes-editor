package editor.ui;

import editor.data.OptionFile;
import editor.data.Stats;
import editor.util.Strings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PositionPanel extends JPanel implements ActionListener {
	private final OptionFile of;
	private volatile int regPos;

	private final JCheckBox[] roleCheck = new JCheckBox[Stats.ROLES.length];
	private/* final*/ JComboBox<String> regBox;

	public PositionPanel(OptionFile of) {
		super(new BorderLayout());
		if (null == of) throw new NullPointerException("of");
		this.of = of;

		initComponents();
	}

	private void initComponents() {
		setBorder(BorderFactory.createTitledBorder(Strings.getMessage("pos.title")));

		JPanel chkPanel = new JPanel(new GridLayout(4, 4));
		for (int i = 0; i < roleCheck.length; i++) {
			roleCheck[i] = new JCheckBox(Stats.ROLES[i].getName());
			roleCheck[i].setToolTipText(Strings.getMessage(roleCheck[i].getText()));
			roleCheck[i].setActionCommand(Integer.toString(i));
			roleCheck[i].addActionListener(this);

			chkPanel.add(roleCheck[i]);
			if (i == 0) {
				chkPanel.add(new JPanel());
				chkPanel.add(new JPanel());
				chkPanel.add(new JPanel());
			}
		}

		regBox = new JComboBox<String>();
		regBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onRegPos(evt);
			}
		});

		JLabel regLabel = new JLabel(Strings.getMessage("pos.registered"));
		JPanel regPanel = new JPanel();
		regPanel.add(regLabel);
		regPanel.add(regBox);

		add(chkPanel, BorderLayout.CENTER);
		add(regPanel, BorderLayout.SOUTH);
	}

	/**
	 * Registered position.
	 */
	public JComboBox<String> getRegBox() {
		return regBox;
	}

	public JCheckBox getRoleCheck(int index) {
		if (index < 0 || index >= roleCheck.length) throw new ArrayIndexOutOfBoundsException("index");
		return roleCheck[index];
	}

	public void load(int player) {
		regPos = Stats.getValue(of, player, Stats.REG_POS);

		for (int i = 0; i < roleCheck.length; i++) {
			if (Stats.getValue(of, player, Stats.ROLES[i]) != 0 || regPos == i) {
				roleCheck[i].setSelected(true);
			} else {
				roleCheck[i].setSelected(false);
			}
		}

		updateRegBox();
	}

	private void updateRegBox() {
		regBox.setActionCommand("n");
		regBox.removeAllItems();

		for (int i = 0; i < roleCheck.length; i++) {
			if (roleCheck[i].isSelected()) {
				regBox.addItem(Stats.ROLES[i].getName());
			}
		}

		regBox.setSelectedItem(Stats.ROLES[regPos].getName());
		regBox.setActionCommand("y");
	}

	/**
	 * On a role checkbox was changed.
	 */
	public void actionPerformed(ActionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");

		int box = Integer.parseInt(evt.getActionCommand());
		if (regPos == box) {
			roleCheck[box].setSelected(true);
		}

		updateRegBox();
	}

	private void onRegPos(ActionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");
		if (!"y".equalsIgnoreCase(evt.getActionCommand()))
			return;

		String roleName = (String) regBox.getSelectedItem();
		int r = 0;
		if (!Strings.isEmpty(roleName)) {
			for (int i = 0; i < Stats.ROLES.length; i++) {
				if (roleName.equalsIgnoreCase(Stats.ROLES[i].getName())) {
					r = i;
					break;
				}
			}
		}
		//log.debug("", r);
		regPos = r;
	}

}
