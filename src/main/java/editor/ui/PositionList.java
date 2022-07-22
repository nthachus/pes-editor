package editor.ui;

import editor.data.*;
import editor.lang.NullArgumentException;
import editor.util.Arrays;
import editor.util.Strings;
import editor.util.UIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class PositionList extends JList/*<String>*/ {
	private static final long serialVersionUID = 1293447084620847213L;
	private static final Logger log = LoggerFactory.getLogger(PositionList.class);

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

		log.debug("Initialize Position list #{} with in-transfer: {}", hashCode(), inTransfer);
		initComponents();
	}

	private void initComponents() {
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setLayoutOrientation(JList.VERTICAL);
		setVisibleRowCount(posNum.length);
		setBackground(UIUtil.LIGHT_YELLOW);
		setPreferredSize(new Dimension(32, 576));//30
		//setFont(new Font(UIUtil.DIALOG, Font.BOLD, 12));
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

	public void refresh(int team) {
		log.info("Refresh Position list for team: {}, in-transfer: {}", team, inTransfer);

		String[] posList = new String[posNum.length];
		java.util.Arrays.fill(posList, Strings.SPACE);

		if (!inTransfer
				|| ((team < Squads.FIRST_EDIT_NATION || team >= Squads.FIRST_CLUB)
				&& team < Squads.FIRST_CLUB + Clubs.TOTAL)) {
			//Arrays.fill(posNum, (byte) 0);
			if (inTransfer && team >= Squads.FIRST_CLUB) {
				team -= Squads.EDIT_TEAM_COUNT;
			}

			posList[0] = Formations.positionToString(0);// NOTE: padding right string ?
			posNum[0] = 0;
			for (int i = 1; i < Formations.PLAYER_COUNT; i++) {
				int pos = Formations.getPosition(of, team, alt, i);
				posNum[i] = pos;
				posList[i] = Formations.positionToString(pos);
			}
		}

		setListData(posList);
		// DEBUG
		log.debug("{} Positions in team {} was refreshed", posList.length, team);
	}

	public void selectPos(JList/*<Player>*/ squadList, int listIndex) {
		if (null == squadList) {
			throw new NullArgumentException("squadList");
		}
		log.info("Try to select position at index: {}", listIndex);

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
		// DEBUG
		log.debug("{} Positions of Stat {} was selected", count, stat);
	}

}
