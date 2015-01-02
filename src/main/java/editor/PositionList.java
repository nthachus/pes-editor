package editor;

import editor.data.Formations;
import editor.data.OptionFile;
import editor.data.Stat;
import editor.data.Stats;

import javax.swing.*;
import java.awt.*;

public class PositionList extends JList<String> {
	private final OptionFile of;

	boolean tran;

	int[] posNum;

	int alt = 0;

	public PositionList(OptionFile opf, boolean t) {
		super();
		tran = t;
		of = opf;
		// refresh(team);
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setLayoutOrientation(JList.VERTICAL);
		setVisibleRowCount(32);
		setBackground(new Color(255, 255, 224));
		setPreferredSize(new Dimension(30, 576));
		// setFont(new Font("Dialog", Font.BOLD, 12));
		// System.out.println(getFont());
	}

	public void refresh(int t) {
		String[] pos = new String[32];
		posNum = new int[32];
		if (tran && ((t > 66 && t < 75) || t > 204)) {
			setListData(pos);
		} else {
			if (tran && t > 74) {
				t = t - 8;
			}
			int p;
			pos[0] = "GK   ";
			posNum[0] = 0;
			for (int i = 0; i < 10; i++) {
				p = Formations.getPosition(of, t, alt, i + 1);
				posNum[i + 1] = p;
				if (p == 0) {
					pos[i + 1] = "GK";
				}
				if ((p > 0 && p < 4) || (p > 5 && p < 8)) {
					pos[i + 1] = "CB";
				}
				if (p == 4) {
					pos[i + 1] = "SW";
				}
				if (p == 5) {
					pos[i + 1] = "SW";
				}
				if (p == 8) {
					pos[i + 1] = "LB";
				}
				if (p == 9) {
					pos[i + 1] = "RB";
				}
				if (p > 9 && p < 15) {
					pos[i + 1] = "DMF";
				}

				if (p == 15) {
					pos[i + 1] = "LWB";
				}
				if (p == 16) {
					pos[i + 1] = "RWB";
				}

				if (p > 16 && p < 22) {
					pos[i + 1] = "CMF";
				}
				if (p == 22) {
					pos[i + 1] = "LMF";
				}
				if (p == 23) {
					pos[i + 1] = "RMF";
				}
				if (p > 23 && p < 29) {
					pos[i + 1] = "AMF";
				}
				if (p == 29) {
					pos[i + 1] = "LWF";
				}
				if (p == 30) {
					pos[i + 1] = "RWF";
				}
				if (p > 30 && p < 36) {
					pos[i + 1] = "SS";
				}
				if (p > 35 && p < 41) {
					pos[i + 1] = "CF";
				}

				if (p > 40) {
					pos[i + 1] = Integer.toString(p);
				}
			}
			for (int i = 11; i < 32; i++) {
				pos[i] = " ";
			}
			setListData(pos);
		}
	}

	public void selectPos(JList sl, int li) {
		clearSelection();
		if (li >= 0 && li < 11) {
			int size = sl.getModel().getSize();
			int selPos = posNum[li];
			int pli;
			int[] temp = new int[32];
			int c = 0;
			Stat stat = Stats.GK;
			if ((selPos > 0 && selPos < 4) || (selPos > 5 && selPos < 8)) {
				stat = Stats.CBT;
			}
			if (selPos == 4) {
				stat = Stats.CWP;
			}
			if (selPos == 5) {
				stat = Stats.CWP;
			}
			if (selPos == 8) {
				stat = Stats.SB;
			}
			if (selPos == 9) {
				stat = Stats.SB;
			}
			if (selPos > 9 && selPos < 15) {
				stat = Stats.DM;
			}

			if (selPos == 15) {
				stat = Stats.WB;
			}
			if (selPos == 16) {
				stat = Stats.WB;
			}

			if (selPos > 16 && selPos < 22) {
				stat = Stats.CM;
			}
			if (selPos == 22) {
				stat = Stats.SM;
			}
			if (selPos == 23) {
				stat = Stats.SM;
			}
			if (selPos > 23 && selPos < 29) {
				stat = Stats.AM;
			}
			if (selPos == 29) {
				stat = Stats.WG;
			}
			if (selPos == 30) {
				stat = Stats.WG;
			}
			if (selPos > 30 && selPos < 36) {
				stat = Stats.SS;
			}
			if (selPos > 35 && selPos < 41) {
				stat = Stats.CF;
			}

			c = 0;
			for (int i = 0; i < size; i++) {
				pli = ((Player) (sl.getModel().getElementAt(i))).index;
				if (pli != 0 && Stats.getValue(of, pli, stat) == 1) {
					// System.out.println(i);
					temp[c] = i;
					c++;
				}
			}
			int[] select = new int[c];
			System.arraycopy(temp, 0, select, 0, c);
			/*
			 * for (int i = 0; i < select.length; i++) {
			 * System.out.println(select[i]); }
			 */
			// setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			setSelectedIndices(select);
		} else {
			clearSelection();
		}
	}

}
