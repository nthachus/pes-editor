/*
 * Copyright 2008-9 Compulsion
 * <pes_compulsion@yahoo.co.uk>
 * <http://www.purplehaze.eclipse.co.uk/>
 * <http://uk.geocities.com/pes_compulsion/>
 *
 * This file is part of PES Editor.
 *
 * PES Editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PES Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PES Editor.  If not, see <http://www.gnu.org/licenses/>.
 */

package editor;

import editor.data.*;
import editor.ui.SelectByTeam;
import editor.util.Bits;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class InfoPanel extends JScrollPane {
	OptionFile of;

	JEditorPane ta;

	SelectByTeam selector;

	boolean listSquads;

	SimpleAttributeSet attr;

	Document doc;

	public InfoPanel(SelectByTeam sbt, OptionFile opf) {
		super(VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);
		selector = sbt;
		of = opf;
		listSquads = true;
		init();
	}

	public InfoPanel(OptionFile opf) {
		super(VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);
		of = opf;
		listSquads = false;
		init();
	}

	private void init() {
		// setBorder(BorderFactory.createTitledBorder("Player info"));
		ta = new JEditorPane();
		ta.setEditable(false);
		setViewportView(ta);
		// add(scroll);
		// ta.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
		StyledEditorKit sek = new StyledEditorKit();
		ta.setEditorKit(sek);
		ta.setBackground(Color.black);
		attr = new SimpleAttributeSet(sek.getInputAttributes());
		doc = ta.getDocument();
		if (listSquads) {
			setPreferredSize(new Dimension(290, 493));
		} else {
			setPreferredSize(new Dimension(145, 493));
		}
		// ta.setVisible(false);
		// StyleConstants.setFontSize(attr, 10);
		StyleConstants.setFontFamily(attr, "SansSerif");
	}

	public void refresh(int index1, int index2) {
		// System.out.println(index1 + ", " + index2);
		ta.setText("");
		if (index1 > 0 || index2 > 0) {
			try {
				if (index2 > 0) {
					StyleConstants.setFontSize(attr, 10);
					insertName(index1, index2);
					doc.insertString(doc.getLength(), "\n", attr);
					insertRole(index1, index2);
					doc.insertString(doc.getLength(), "\n", attr);
					insertPhys(index1, index2);
					insertStats(index1, index2);
					doc.insertString(doc.getLength(), "\n", attr);
					insertAbilities(index1, index2);
				} else {
					StyleConstants.setFontSize(attr, 12);
					insertName(index1, index2);
					doc.insertString(doc.getLength(), "\n\n", attr);
					insertAgeNat(index1, index2);
					doc.insertString(doc.getLength(), "\n", attr);
					insertPhys(index1, index2);
					doc.insertString(doc.getLength(), "\n", attr);
					insertRole(index1, index2);
					// doc.insertString(doc.getLength(),
					// stats.FAVORITE_SIDE.getString(index1), attr);
					doc.insertString(doc.getLength(), "\n\n", attr);
					insertSquads(index1);
				}

			} catch (BadLocationException e) {
			}
			ta.setCaretPosition(0);
		}
		// ta.setVisible(true);
	}

	private void insertStats(int index1, int index2)
			throws BadLocationException {
		StyleConstants.setForeground(attr, Color.white);
		insertStat(Stats.WEAK_FOOT_ACC, index1, index2);
		insertStat(Stats.WEAK_FOOT_FREQ, index1, index2);
		// insertStat(stats.statX, index1, index2);
		for (int i = 0; i < Stats.ABILITY99.length; i++) {
			insertStat(Stats.ABILITY99[i], index1, index2);
		}
		insertStat(Stats.CONSISTENCY, index1, index2);
		insertStat(Stats.CONDITION, index1, index2);

	}

	private void insertRole(int index1, int index2) throws BadLocationException {
		String text = "";
		if (index1 > 0 && Stats.getValue(of, index1, Stats.GK) == 1) {
			text = "GK";
		}
		if (index2 > 0 && Stats.getValue(of, index2, Stats.GK) == 1) {
			text = text + "\t\tGK";
		}
		text = text + "\n";

		if (index1 > 0 && Stats.getValue(of, index1, Stats.CWP) == 1) {
			text = text + "SW  ";
		}
		if (index1 > 0 && Stats.getValue(of, index1, Stats.CBT) == 1) {
			text = text + "CB  ";
		}
		if (index2 > 0) {
			text = text + "\t\t";
		}
		if (index2 > 0 && Stats.getValue(of, index2, Stats.CWP) == 1) {
			text = text + "SW  ";
		}
		if (index2 > 0 && Stats.getValue(of, index2, Stats.CBT) == 1) {
			text = text + "CB  ";
		}
		text = text + "\n";

		if (index1 > 0 && Stats.getValue(of, index1, Stats.SB) == 1) {
			text = text + "SB  ";
		}
		if (index1 > 0 && Stats.getValue(of, index1, Stats.WB) == 1) {
			text = text + "WB  ";
		}
		if (index1 > 0 && Stats.getValue(of, index1, Stats.DM) == 1) {
			text = text + "DMF  ";
		}
		if (index2 > 0) {
			text = text + "\t\t";
		}

		if (index2 > 0 && Stats.getValue(of, index2, Stats.SB) == 1) {
			text = text + "SB  ";
		}
		if (index2 > 0 && Stats.getValue(of, index2, Stats.WB) == 1) {
			text = text + "WB  ";
		}
		if (index2 > 0 && Stats.getValue(of, index2, Stats.DM) == 1) {
			text = text + "DMF  ";
		}
		text = text + "\n";

		if (index1 > 0 && Stats.getValue(of, index1, Stats.CM) == 1) {
			text = text + "CMF  ";
		}
		if (index1 > 0 && Stats.getValue(of, index1, Stats.SM) == 1) {
			text = text + "SMF  ";
		}
		if (index1 > 0 && Stats.getValue(of, index1, Stats.AM) == 1) {
			text = text + "AMF  ";
		}
		if (index2 > 0) {
			text = text + "\t\t";
		}

		if (index2 > 0 && Stats.getValue(of, index2, Stats.CM) == 1) {
			text = text + "CMF  ";
		}
		if (index2 > 0 && Stats.getValue(of, index2, Stats.SM) == 1) {
			text = text + "SMF  ";
		}
		if (index2 > 0 && Stats.getValue(of, index2, Stats.AM) == 1) {
			text = text + "AMF  ";
		}

		text = text + "\n";
		if (index1 > 0 && Stats.getValue(of, index1, Stats.SS) == 1) {
			text = text + "SS  ";
		}
		if (index1 > 0 && Stats.getValue(of, index1, Stats.CF) == 1) {
			text = text + "CF  ";
		}
		if (index1 > 0 && Stats.getValue(of, index1, Stats.WG) == 1) {
			text = text + "WG  ";
		}
		if (index2 > 0) {
			text = text + "\t\t";
		}
		if (index2 > 0 && Stats.getValue(of, index2, Stats.SS) == 1) {
			text = text + "SS  ";
		}
		if (index2 > 0 && Stats.getValue(of, index2, Stats.CF) == 1) {
			text = text + "CF  ";
		}
		if (index2 > 0 && Stats.getValue(of, index2, Stats.WG) == 1) {
			text = text + "WG  ";
		}
		if (index2 > 0) {
			StyleConstants.setFontSize(attr, 9);
		}
		doc.insertString(doc.getLength(), text, attr);
		if (index2 > 0) {
			StyleConstants.setFontSize(attr, 10);
		}
	}

	private void insertAbilities(int index1, int index2)
			throws BadLocationException {
		for (int i = 0; i < Stats.ABILITY_SPECIAL.length; i++) {
			if ((i & 1) == 1) {
				insertAbility(Stats.ABILITY_SPECIAL[i], index1, index2,
						Color.red);
			} else {
				insertAbility(Stats.ABILITY_SPECIAL[i], index1, index2,
						Color.green);
			}
		}
		// StyleConstants.setForeground(attr, Color.yellow);
		StyleConstants.setForeground(attr, Color.white);
	}

	private void insertSquads(int index1) throws BadLocationException {
		if (listSquads) {
			StyleConstants.setForeground(attr, Color.white);
			int i;
			int t;
			int a;
			doc.insertString(doc.getLength(), "Squads:", attr);
			a = Squads.NATION_ADR - 2;
			do {
				a = a + 2;
				i = (Bits.toInt(of.getData()[a + 1]) << 8) | Bits.toInt(of.getData()[a]);
				if (i == index1) {
					// System.out.println(a);

					if (a < Squads.CLUB_ADR) {
						if (a >= Squads.CLUB_ADR - 64) {
							t = 74;
						} else {
							t = (a - Squads.NATION_ADR) / 46;
						}
					} else {
						t = ((a - Squads.CLUB_ADR) / 64) + 75;
					}
					// System.out.println(t);
					doc
							.insertString(doc.getLength(), "\n"
									+ selector.getTeamBox().getModel().getElementAt(
									t), attr);
				}
			} while (a < Formations.START_ADR - 128 - 2); // && i != p);
		}
	}

	private void insertName(int index1, int index2) throws BadLocationException {
		StyleConstants.setForeground(attr, Color.white);
		if (index1 > 0) {
			doc.insertString(doc.getLength(), new Player(of, index1).getName(), attr);
		}
		if (index2 > 0) {
			doc.insertString(doc.getLength(), "\t" + new Player(of, index2).getName(), attr);
		}
	}

	private void insertAgeNat(int index1, int index2) throws BadLocationException {
		StyleConstants.setForeground(attr, Color.white);
		if (index1 > 0) {
			doc.insertString(doc.getLength(), Stats.getString(of, index1,
					Stats.NATIONALITY), attr);
		}
		if (index2 > 0) {
			doc.insertString(doc.getLength(), "\t"
					+ Stats.getString(of, index2, Stats.NATIONALITY), attr);
		}
		if (index1 > 0) {
			doc.insertString(doc.getLength(), "\nAge: "
					+ Stats.getString(of, index1, Stats.AGE), attr);
		}
		if (index2 > 0) {
			doc.insertString(doc.getLength(), "\tAge: "
					+ Stats.getString(of, index2, Stats.AGE), attr);
		}
	}

	private void insertPhys(int index1, int index2) throws BadLocationException {
		StyleConstants.setForeground(attr, Color.white);
		if (index1 > 0) {
			String footSide = "LF/";
			if (Stats.getValue(of, index1, Stats.FOOT) == 1) {
				switch (Stats.getValue(of, index1, Stats.FAVORITE_SIDE)) {
					case 0:
						footSide = footSide + "LS";
						break;
					case 1:
						footSide = footSide + "RS";
						break;
					case 2:
						footSide = footSide + "BS";
						break;
				}
			} else {
				footSide = "RF/";
				switch (Stats.getValue(of, index1, Stats.FAVORITE_SIDE)) {
					case 0:
						footSide = footSide + "RS";
						break;
					case 1:
						footSide = footSide + "LS";
						break;
					case 2:
						footSide = footSide + "BS";
						break;
				}
			}
			footSide = footSide + ", ";
			doc.insertString(doc.getLength(), footSide
					+ Stats.getString(of, index1, Stats.HEIGHT) + "cm, "
					+ Stats.getString(of, index1, Stats.WEIGHT) + "Kg", attr);
		}
		if (index2 > 0) {
			if (index1 > 0) {
				doc.insertString(doc.getLength(), "\t", attr);
			} else {
				doc.insertString(doc.getLength(), "\t\t", attr);
			}
			String footSide = "LF/";
			if (Stats.getValue(of, index2, Stats.FOOT) == 1) {
				switch (Stats.getValue(of, index2, Stats.FAVORITE_SIDE)) {
					case 0:
						footSide = footSide + "LS";
						break;
					case 1:
						footSide = footSide + "RS";
						break;
					case 2:
						footSide = footSide + "BS";
						break;
				}
			} else {
				footSide = "RF/";
				switch (Stats.getValue(of, index2, Stats.FAVORITE_SIDE)) {
					case 0:
						footSide = footSide + "RS";
						break;
					case 1:
						footSide = footSide + "LS";
						break;
					case 2:
						footSide = footSide + "BS";
						break;
				}
			}
			footSide = footSide + ", ";
			doc.insertString(doc.getLength(), footSide
					+ Stats.getString(of, index2, Stats.HEIGHT) + "cm, "
					+ Stats.getString(of, index2, Stats.WEIGHT) + "Kg", attr);
		}
	}

	private void insertStat(Stat stat, int index1, int index2)
			throws BadLocationException {
		int v1 = Stats.getValue(of, index1, stat);
		int v2 = Stats.getValue(of, index2, stat);
		String s1 = Stats.getString(of, index1, stat);
		String s2 = Stats.getString(of, index2, stat);
		doc.insertString(doc.getLength(), "\n" + stat.getName() + "\t", attr);
		if (index1 > 0) {
			setStatColour(stat, v1);
			doc.insertString(doc.getLength(), s1, attr);
		}
		if (index2 > 0) {
			if (index1 > 0) {
				int dif = v1 - v2;
				String comp = " ";
				int div = 3;
				int add = 1;
				if (stat == Stats.WEAK_FOOT_ACC || stat == Stats.WEAK_FOOT_FREQ
						|| stat == Stats.CONSISTENCY || stat == Stats.CONDITION) {
					div = 1;
					add = 0;
				}
				if (dif > 0) {
					dif = (dif / div) + add;
					for (int i = 0; i < dif && i < 10; i++) {
						comp = comp + "*";
					}
					StyleConstants.setForeground(attr, Color.green);
					doc.insertString(doc.getLength(), comp, attr);
				}
				if (dif < 0) {
					dif = (dif / -div) + add;
					for (int i = 0; i < dif && i < 10; i++) {
						comp = comp + "*";
					}
					StyleConstants.setForeground(attr, Color.red);
					doc.insertString(doc.getLength(), comp, attr);
				}
			}
			StyleConstants.setForeground(attr, Color.white);
			setStatColour(stat, v2);
			doc.insertString(doc.getLength(), "\t" + s2, attr);
		}
		StyleConstants.setForeground(attr, Color.white);
	}

	private void insertAbility(Stat stat, int index1, int index2, Color colour)
			throws BadLocationException {
		StyleConstants.setForeground(attr, colour);
		doc.insertString(doc.getLength(), "\n" + stat.getName() + "\t", attr);
		if (index1 > 0 && Stats.getValue(of, index1, stat) == 1) {
			doc.insertString(doc.getLength(), "O", attr);
		}
		if (index2 > 0 && Stats.getValue(of, index2, stat) == 1) {
			doc.insertString(doc.getLength(), "\tO", attr);
		}
	}

	private void setStatColour(Stat stat, int v) {
		if (stat == Stats.WEAK_FOOT_ACC || stat == Stats.WEAK_FOOT_FREQ || stat == Stats.CONSISTENCY
				|| stat == Stats.CONDITION) {
			if (v == 7) {
				StyleConstants.setForeground(attr, Color.red);
			} else if (v == 6) {
				StyleConstants.setForeground(attr, Color.orange);
			}
		} else {
			if (v > 94) {
				StyleConstants.setForeground(attr, Color.red);
			} else if (v > 89) {
				StyleConstants.setForeground(attr, Color.orange);
			} else if (v > 79) {
				StyleConstants.setForeground(attr, Color.yellow);
			} else if (v > 74) {
				StyleConstants.setForeground(attr, new Color(183, 255, 0));
			}
		}
	}

}
