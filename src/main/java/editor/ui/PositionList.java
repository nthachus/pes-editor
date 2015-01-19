package editor.ui;

import editor.data.*;
import editor.lang.NullArgumentException;
import editor.util.Arrays;
import editor.util.UIUtil;

import javax.swing.*;
import java.awt.*;

public class PositionList extends JList/*<String>*/ {
	private static final long serialVersionUID = 1293447084620847213L;

	private final OptionFile of;
	private final boolean inTransfer;

	private final int[] posNum = new int[Formations.CLUB_TEAM_SIZE];
	private volatile int alt = 0;

	public PositionList(OptionFile of, boolean inTransfer) {
		super();
		if (null == of) {
			throw new NullArgumentException("of");
		}
		this.of = of;
		this.inTransfer = inTransfer;

		//refresh(team);

		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setLayoutOrientation(JList.VERTICAL);
		setVisibleRowCount(posNum.length);
		setBackground(UIUtil.LIGHT_YELLOW);
		setPreferredSize(new Dimension(32, 576));//30
		//setFont(new Font(Font.DIALOG, Font.BOLD, 12));
	}

	public int getPosNum(int index) {
		if (index < 0 || index >= posNum.length) {
			throw new ArrayIndexOutOfBoundsException(Integer.toString(index));
		}
		return posNum[index];
	}

	public void setAlt(int alt) {
		this.alt = alt;
	}

	@SuppressWarnings("unchecked")
	public void refresh(int team) {
		String[] posList = new String[posNum.length];
		java.util.Arrays.fill(posList, " ");

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
		}

		setListData(posList);
	}

	public void selectPos(JList/*<Player>*/ squadList, int listIndex) {
		if (null == squadList) {
			throw new NullArgumentException("squadList");
		}
		clearSelection();

		if (listIndex < 0 || listIndex >= Formations.PLAYER_COUNT) {
			return;
		}

		int selPos = posNum[listIndex];
		Stat stat = Formations.positionToStat(selPos);

		int[] temp = new int[posNum.length];
		int count = 0;
		for (int i = 0, size = squadList.getModel().getSize(); i < size; i++) {

			Player p = (Player) squadList.getModel().getElementAt(i);
			if (p.getIndex() > 0 && Stats.getValue(of, p.getIndex(), stat) != 0) {
				temp[count] = i;
				count++;
			}
		}

		int[] select = Arrays.copyOf(temp, count);
		setSelectedIndices(select);
	}

}
