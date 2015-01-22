package editor.ui;

import editor.data.OptionFile;
import editor.data.Stats;
import editor.lang.NullArgumentException;
import editor.util.Resources;
import editor.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PositionPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 6052934571754116663L;
	private static final Logger log = LoggerFactory.getLogger(PositionPanel.class);

	private final OptionFile of;
	private volatile int regRole;

	private final JCheckBox[] roleCheck = new JCheckBox[Stats.ROLES.length];
	private/* final*/ JComboBox/*<String>*/ regBox;

	public PositionPanel(OptionFile of) {
		super(new BorderLayout());
		if (null == of) {
			throw new NullArgumentException("of");
		}
		this.of = of;

		log.debug("Position panel is initializing..");
		initComponents();
	}

	private void initComponents() {
		setBorder(BorderFactory.createTitledBorder(Resources.getMessage("pos.title")));

		JPanel chkPanel = new JPanel(new GridLayout(4, 4));
		String labText;
		for (int i = 0; i < roleCheck.length; i++) {

			labText = Stats.ROLES[i].getName();
			roleCheck[i] = new JCheckBox(labText);
			roleCheck[i].setToolTipText(Resources.getNullableMessage(labText));
			roleCheck[i].setActionCommand(Integer.toString(i));
			roleCheck[i].addActionListener(this);

			chkPanel.add(roleCheck[i]);
			if (i == 0) {
				chkPanel.add(new JPanel());
				chkPanel.add(new JPanel());
				chkPanel.add(new JPanel());
			}
		}

		regBox = new JComboBox/*<String>*/();
		regBox.addActionListener(this);

		JLabel regLabel = new JLabel(Resources.getMessage("pos.registered"));
		JPanel regPanel = new JPanel();
		regPanel.add(regLabel);
		regPanel.add(regBox);

		add(chkPanel, BorderLayout.CENTER);
		add(regPanel, BorderLayout.SOUTH);
	}

	/**
	 * Registered position.
	 */
	public JComboBox getRegBox() {
		return regBox;
	}

	public JCheckBox getRoleCheck(int index) {
		if (index < 0 || index >= roleCheck.length) {
			throw new ArrayIndexOutOfBoundsException(Integer.toString(index));
		}
		return roleCheck[index];
	}

	public void load(int player) {
		log.info("Try to load role for player: {}", player);

		regRole = Stats.getValue(of, player, Stats.REG_POS);
		regRole = Stats.regPosToRole(regRole);

		for (int i = 0; i < roleCheck.length; i++) {
			int v = Stats.getValue(of, player, Stats.ROLES[i]);
			roleCheck[i].setSelected(v != 0 || regRole == i);
		}

		updateRegBox();
	}

	@SuppressWarnings("unchecked")
	private void updateRegBox() {
		regBox.setActionCommand("n");
		regBox.removeAllItems();

		for (int i = 0; i < roleCheck.length; i++) {
			if (roleCheck[i].isSelected()) {
				regBox.addItem(Stats.ROLES[i].getName());
			}
		}

		regBox.setSelectedItem(Stats.ROLES[regRole].getName());
		regBox.setActionCommand("y");

		log.debug("Updating of registered role {} succeeded", regRole);
	}

	/**
	 * On a role checkbox was changed.
	 */
	public void actionPerformed(ActionEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		log.info("Perform position action: {} from: {}", evt.getActionCommand(), Strings.valueOf(evt.getSource()));

		if (evt.getSource() == regBox) {
			registerRole(evt);
		} else {
			int box = Integer.parseInt(evt.getActionCommand());
			if (regRole == box) {
				roleCheck[box].setSelected(true);
			}
			updateRegBox();
		}
	}

	private void registerRole(ActionEvent evt) {
		if (!"y".equalsIgnoreCase(evt.getActionCommand())) {
			return;
		}

		String roleName = (String) regBox.getSelectedItem();
		for (int i = 0; i < Stats.ROLES.length; i++) {
			if (Stats.ROLES[i].getName().equalsIgnoreCase(roleName)) {
				regRole = i;
				break;
			}
		}
		// DEBUG
		log.debug("Register succeeded on role {} to {}", roleName, regRole);
	}

}
