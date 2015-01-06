package editor.ui;

import editor.data.*;
import editor.util.swing.JList;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class PositionList extends JList<String> {
	private final OptionFile of;
	private final boolean inTransfer;

	private final int[] posNum = new int[Formations.CLUB_TEAM_SIZE];
	private volatile int alt = 0;

	public PositionList(OptionFile of, boolean inTransfer) {
		super();
		if (null == of) throw new NullPointerException("of");
		this.of = of;
		this.inTransfer = inTransfer;

		//refresh(team);

		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setLayoutOrientation(JList.VERTICAL);
		setVisibleRowCount(Formations.CLUB_TEAM_SIZE);
		setBackground(new Color(255, 255, 224));
		setPreferredSize(new Dimension(30, 576));
		//setFont(new Font(Font.DIALOG, Font.BOLD, 12));
	}

	public int getPosNum(int index) {
		if (index < 0 || index >= posNum.length) throw new ArrayIndexOutOfBoundsException("index");
		return posNum[index];
	}

	public void setAlt(int alt) {
		this.alt = alt;
	}

	public void refresh(int team) {
		String[] posList = new String[Formations.CLUB_TEAM_SIZE];

		if (!inTransfer
				|| ((team < Squads.FIRST_EDIT_NATION || team >= Squads.FIRST_CLUB)
				&& team < Squads.FIRST_CLUB + Clubs.TOTAL)) {
			//Arrays.fill(posNum, (byte) 0);
			if (inTransfer && team >= Squads.FIRST_CLUB) {
				team -= Squads.EDIT_TEAM_COUNT;
			}

			posList[0] = Formations.positionToString(0) + "   ";// NOTE: padding right string ?
			posNum[0] = 0;
			for (int i = 1; i < Formations.PLAYER_COUNT; i++) {
				int pos = Formations.getPosition(of, team, alt, i);
				posNum[i] = pos;
				posList[i] = Formations.positionToString(pos);
			}

			for (int i = Formations.PLAYER_COUNT; i < posList.length; i++) {
				posList[i] = " ";
			}
		}

		setListData(posList);
	}

	public void selectPos(JList<Player> squadList, int listIndex) {
		if (null == squadList) throw new NullPointerException("squadList");
		clearSelection();

		if (listIndex < 0 || listIndex >= Formations.PLAYER_COUNT)
			return;

		int selPos = posNum[listIndex];
		Stat stat = Formations.positionToStat(selPos);

		int[] temp = new int[Formations.CLUB_TEAM_SIZE];
		int count = 0;
		for (int i = 0, size = squadList.getModel().getSize(); i < size; i++) {

			int playerId = squadList.getModel().getElementAt(i).getIndex();
			if (playerId > 0 && Stats.getValue(of, playerId, stat) != 0) {
				temp[count] = i;
				count++;
			}
		}

		int[] select = Arrays.copyOf(temp, count);
		setSelectedIndices(select);
	}

}
