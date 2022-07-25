package editor.ui;

import editor.data.*;
import editor.lang.NullArgumentException;
import editor.util.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SelectByTeam extends JPanel implements ActionListener {
	private static final long serialVersionUID = 776055403727356248L;
	private static final Logger log = LoggerFactory.getLogger(SelectByTeam.class);

	private final OptionFile of;
	private final boolean isNormal;

	private/* final*/ SquadList squadList;
	private/* final*/ JComboBox/*<String>*/ teamBox;
	private/* final*/ SquadNumberList numList;
	private/* final*/ PositionList posList;

	public SelectByTeam(OptionFile of, boolean isNormal) {
		super(new BorderLayout());
		if (null == of) {
			throw new NullArgumentException("of");
		}
		this.of = of;
		this.isNormal = isNormal;

		log.debug("Initialize By Team dropdown #{}", hashCode());
		initComponents();
	}

	private void initComponents() {
		teamBox = new JComboBox/*<String>*/();
		teamBox.setMaximumRowCount(Formations.CLUB_TEAM_SIZE);
		teamBox.addActionListener(this);
		add(teamBox, BorderLayout.NORTH);

		squadList = new SquadList(of, isNormal);
		if (isNormal) {
			numList = new SquadNumberList(of);
			posList = new PositionList(of, true);

			add(posList, BorderLayout.WEST);
			add(squadList, BorderLayout.CENTER);
			add(numList, BorderLayout.EAST);
		} else {
			JScrollPane scroll = new JScrollPane(
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scroll.setViewportView(squadList);

			setPreferredSize(null);
			add(scroll, BorderLayout.CENTER);
		}

		if (isNormal) {
			setPreferredSize(new Dimension(164, 601));
		}
	}

	public SquadList getSquadList() {
		return squadList;
	}

	public JComboBox getTeamBox() {
		return teamBox;
	}

	/**
	 * Squad number dropdown.
	 */
	public SquadNumberList getNumList() {
		return numList;
	}

	public PositionList getPosList() {
		return posList;
	}

	public void actionPerformed(ActionEvent evt) {
		if (null == evt) {
			throw new NullArgumentException("evt");
		}
		log.info("Perform team-select action: {}", evt.getActionCommand());

		if ("y".equalsIgnoreCase(evt.getActionCommand())) {
			refreshForTeam();
		}
	}

	public void refreshForTeam() {
		int teamId = teamBox.getSelectedIndex();
		log.info("Try to refresh for selected team: {}, normal-mode: {}", teamId, isNormal);

		if (teamId >= 0) {
			squadList.refresh(teamId, true);

			if (isNormal) {
				posList.refresh(teamId);
				numList.refresh(teamId);
			}
		}
	}

	private String[] getAllTeams() {
		int len = Squads.NATION_COUNT + Squads.EXTRAS.length + Clubs.TOTAL;
		if (!isNormal) {
			len++;
		}
		String[] squads = new String[len];

		int ofs = 0;
		System.arraycopy(Stats.NATION, 0, squads, ofs, Squads.NATION_COUNT);
		ofs += Squads.NATION_COUNT;
		System.arraycopy(Squads.EXTRAS, 0, squads, ofs, Squads.EXTRA_COUNT);
		ofs += Squads.EXTRA_COUNT;
		System.arraycopy(Clubs.getNames(of), 0, squads, ofs, Clubs.TOTAL);
		ofs += Clubs.TOTAL;
		System.arraycopy(Squads.EXTRAS, Squads.EXTRA_COUNT, squads, ofs, Squads.EXTRAS.length - Squads.EXTRA_COUNT);

		if (!isNormal) {
			squads[squads.length - 1] = Resources.getMessage("All Players");
		}

		log.debug("All {} teams was retrieved for normal-mode: {}", squads.length, isNormal);
		return squads;
	}

	public void refresh() {
		log.info("Try to reload By Team dropdown #{}", hashCode());
		teamBox.setActionCommand("n");

		String[] squads = getAllTeams();
		int newIdx = (isNormal) ? Squads.NATION_COUNT + Squads.EXTRA_COUNT : squads.length - 1;

		teamBox.setModel(new DefaultComboBoxModel/*<String>*/(squads));
		teamBox.setSelectedIndex(newIdx);

		refreshForTeam();

		teamBox.setActionCommand("y");
		// DEBUG
		log.debug("Reload completed on By Team dropdown #{}, select {}", hashCode(), newIdx);
	}

}
