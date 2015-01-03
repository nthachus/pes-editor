package editor.ui;

import editor.data.*;

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
		setBackground(new Color(0xFF, 0xFF, 0xE0));
		setPreferredSize(new Dimension(30, 576));
		//setFont(new Font("Dialog", Font.BOLD, 12));
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

			posList[0] = "GK   ";
			posNum[0] = 0;
			for (int i = 1; i < 11; i++) {
				int pos = Formations.getPosition(of, team, alt, i);
				posNum[i] = pos;
				posList[i] = positionToString(pos);
			}

			for (int i = 11; i < posList.length; i++) {
				posList[i] = " ";
			}
		}

		setListData(posList);
	}

	public void selectPos(JList<Player> squadList, int listIndex) {
		if (null == squadList) throw new NullPointerException("squadList");
		clearSelection();

		if (listIndex < 0 || listIndex >= 11)
			return;

		int selPos = posNum[listIndex];
		Stat stat = positionToStat(selPos);

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

	private static Stat positionToStat(int pos) {
		if (pos <= 0) {
			return Stats.GK;
		} else if (pos < 4 || (pos > 5 && pos < 8)) {
			return Stats.CBT;
		} else if (pos == 4) {
			return Stats.CWP;
		} else if (pos == 5) {
			return Stats.CWP;
		} else if (pos == 8) {
			return Stats.SB;
		} else if (pos == 9) {
			return Stats.SB;
		} else if (pos > 9 && pos < 15) {
			return Stats.DM;
		} else if (pos == 15) {
			return Stats.WB;
		} else if (pos == 16) {
			return Stats.WB;
		} else if (pos > 16 && pos < 22) {
			return Stats.CM;
		} else if (pos == 22) {
			return Stats.SM;
		} else if (pos == 23) {
			return Stats.SM;
		} else if (pos > 23 && pos < 29) {
			return Stats.AM;
		} else if (pos == 29) {
			return Stats.WG;
		} else if (pos == 30) {
			return Stats.WG;
		} else if (pos > 30 && pos < 36) {
			return Stats.SS;
		} else if (pos >= 36 && pos < 41) {
			return Stats.CF;
		}
		return Stats.GK;
	}

	private static String positionToString(int pos) {
		if (pos <= 0) {
			return "GK";
		} else if (pos < 4 || (pos > 5 && pos < 8)) {
			return "CB";
		} else if (pos == 4) {
			return "SW";
		} else if (pos == 5) {
			return "SW";
		} else if (pos == 8) {
			return "LB";
		} else if (pos == 9) {
			return "RB";
		} else if (pos > 9 && pos < 15) {
			return "DMF";
		} else if (pos == 15) {
			return "LWB";
		} else if (pos == 16) {
			return "RWB";
		} else if (pos > 16 && pos < 22) {
			return "CMF";
		} else if (pos == 22) {
			return "LMF";
		} else if (pos == 23) {
			return "RMF";
		} else if (pos > 23 && pos < 29) {
			return "AMF";
		} else if (pos == 29) {
			return "LWF";
		} else if (pos == 30) {
			return "RWF";
		} else if (pos > 30 && pos < 36) {
			return "SS";
		} else if (pos >= 36 && pos < 41) {
			return "CF";
		}
		// else if (pos >= 41)
		return Integer.toString(pos);
	}

}
