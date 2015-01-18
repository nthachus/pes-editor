package editor.ui;

import editor.data.Formations;
import editor.data.OptionFile;
import editor.lang.NullArgumentException;
import editor.util.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TeamSettingPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 5663937717103660484L;
	private static final Logger log = LoggerFactory.getLogger(TeamSettingPanel.class);

	private static final String[] ITEMS3 = {"A", "B", "C"};

	private final OptionFile of;
	private volatile int alt = 0;
	private volatile int squad = 0;
	private volatile boolean isOk = false;

	private final JComboBox[] boxes = new JComboBox[Formations.SETTING_COUNT];

	public TeamSettingPanel(OptionFile of) {
		super(new GridBagLayout());
		if (null == of) {
			throw new NullArgumentException("of");
		}
		this.of = of;

		log.debug("Team Setting panel is initializing..");
		initComponents();
	}

	private void initComponents() {
		setBorder(BorderFactory.createTitledBorder(Resources.getMessage("setting.title")));

		for (int i = 0; i < boxes.length; i++) {
			boxes[i] = new JComboBox<String>(ITEMS3);
			boxes[i].setActionCommand(Integer.toString(i));
			boxes[i].addActionListener(this);
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

	public void actionPerformed(ActionEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		log.debug("Perform action {} on Team Setting panel", evt.getActionCommand());

		if (isOk) {
			int i = Integer.parseInt(evt.getActionCommand());
			Formations.setTeamSetting(of, squad, alt, i, boxes[i].getSelectedIndex());
		}
	}

	public void setAlt(int alt) {
		this.alt = alt;
	}

	public void refresh(int squad) {
		log.debug("Refresh Team Setting panel for alt: {}, squad: {}", alt, squad);

		isOk = false;
		this.squad = squad;

		for (int i = 0; i < boxes.length; i++) {
			int setting = Formations.getTeamSetting(of, squad, alt, i);
			boxes[i].setSelectedIndex(setting);
		}

		isOk = true;
	}

}
