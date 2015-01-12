package editor.ui;

import editor.data.Formations;
import editor.data.OptionFile;
import editor.util.Resources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TeamSettingPanel extends JPanel {
	private static final String[] ITEMS3 = {"A", "B", "C"};

	private final OptionFile of;

	private volatile int alt = 0;
	private volatile int squad = 0;
	private volatile boolean isOk = false;

	private final JComboBox[] boxes = new JComboBox[Formations.SETTING_COUNT];

	public TeamSettingPanel(OptionFile of) {
		super(new GridBagLayout());
		if (null == of) throw new NullPointerException("of");
		this.of = of;

		initComponents();
	}

	private void initComponents() {
		setBorder(BorderFactory.createTitledBorder(Resources.getMessage("setting.title")));

		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onBoxChanged(evt);
			}
		};
		for (int i = 0; i < boxes.length; i++) {
			boxes[i] = new JComboBox<String>(ITEMS3);
			boxes[i].setActionCommand(Integer.toString(i));
			boxes[i].addActionListener(listener);
		}

		GridBagConstraints grid = new GridBagConstraints();
		grid.anchor = GridBagConstraints.EAST;
		grid.insets = new Insets(0, 10, 0, 1);

		grid.gridx = 0;
		grid.gridy = 0;
		add(new JLabel(Resources.getMessage("Back line")), grid);

		grid.gridx = 0;
		grid.gridy = 1;
		add(new JLabel(Resources.getMessage("Pressure")), grid);

		grid.gridx = 2;
		grid.gridy = 0;
		add(new JLabel(Resources.getMessage("Offside Trap")), grid);

		grid.gridx = 2;
		grid.gridy = 1;
		add(new JLabel(Resources.getMessage("Counter Attack")), grid);

		grid.insets = new Insets(0, 1, 0, 10);

		grid.gridx = 1;
		grid.gridy = 0;
		add(boxes[0], grid);

		grid.gridx = 1;
		grid.gridy = 1;
		add(boxes[1], grid);

		grid.gridx = 3;
		grid.gridy = 0;
		add(boxes[2], grid);

		grid.gridx = 3;
		grid.gridy = 1;
		add(boxes[3], grid);
	}

	private void onBoxChanged(ActionEvent evt) {
		if (null == evt) throw new NullPointerException("evt");

		if (isOk) {
			int i = Integer.parseInt(evt.getActionCommand());
			Formations.setTeamSetting(of, squad, alt, i, boxes[i].getSelectedIndex());
		}
	}

	public void setAlt(int alt) {
		this.alt = alt;
	}

	public void refresh(int squad) {
		this.squad = squad;
		isOk = false;

		for (int i = 0; i < boxes.length; i++) {
			boxes[i].setSelectedIndex(Formations.getTeamSetting(of, squad, alt, i));
		}

		isOk = true;
	}

}
