package editor.ui;

import editor.data.Clubs;
import editor.data.OptionFile;
import editor.data.Squads;
import editor.data.Stats;
import editor.util.Resources;
import editor.util.swing.DefaultComboBoxModel;
import editor.util.swing.JComboBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SelectByTeam extends JPanel {
	private final OptionFile of;
	private final boolean isNormal;

	private/* final*/ SquadList squadList;
	private/* final*/ JComboBox<String> teamBox;
	private/* final*/ SquadNumberList numList;
	private/* final*/ PositionList posList;

	public SelectByTeam(OptionFile of, boolean isNormal) {
		super(new BorderLayout());
		if (null == of) throw new NullPointerException("of");
		this.of = of;
		this.isNormal = isNormal;

		initComponents();
	}

	private void initComponents() {
		teamBox = new JComboBox<String>();
		teamBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				onTeamChanged(evt);
			}
		});
		teamBox.setMaximumRowCount(32);
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

	public JComboBox<String> getTeamBox() {
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

	private void onTeamChanged(ActionEvent evt) {
		int teamId = teamBox.getSelectedIndex();
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
		if (!isNormal) len++;
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

		return squads;
	}

	public void refresh() {
		String[] squads = getAllTeams();
		teamBox.setModel(new DefaultComboBoxModel<String>(squads));

		if (isNormal) {
			teamBox.setSelectedIndex(Squads.NATION_COUNT + Squads.EXTRA_COUNT);

			numList.refresh(teamBox.getSelectedIndex());
			posList.refresh(teamBox.getSelectedIndex());
		} else {
			teamBox.setSelectedIndex(squads.length - 1);
		}

		squadList.refresh(teamBox.getSelectedIndex(), true);
	}

}
