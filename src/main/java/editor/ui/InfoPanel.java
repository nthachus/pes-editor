package editor.ui;

import editor.data.*;
import editor.util.Bits;
import editor.util.Colors;
import editor.util.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

/**
 * Player information panel.
 */
public class InfoPanel extends JScrollPane {
	private static final Logger log = LoggerFactory.getLogger(InfoPanel.class);

	private final OptionFile of;
	private final SelectByTeam selector;

	public InfoPanel(OptionFile of, SelectByTeam teamSelect) {
		super(VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);

		if (null == of) throw new NullPointerException("of");
		this.of = of;
		selector = teamSelect;

		initComponents();
	}

	public InfoPanel(OptionFile of) {
		this(of, null);
	}

	private JEditorPane ta;
	private MutableAttributeSet attr;
	private Document doc;

	private void initComponents() {
		//setBorder(BorderFactory.createTitledBorder("Player info"));

		StyledEditorKit sek = new StyledEditorKit();
		ta = new JEditorPane();
		ta.setEditable(false);
		ta.setEditorKit(sek);
		ta.setBackground(Color.BLACK);

		setViewportView(ta);

		attr = sek.getInputAttributes();
		doc = ta.getDocument();

		setPreferredSize(new Dimension(hasListSquads() ? 290 : 145, 493));

		StyleConstants.setFontFamily(attr, Font.SANS_SERIF);
	}

	private boolean hasListSquads() {
		return (null != selector);
	}

	/**
	 * @param index1 Player 1 ID
	 * @param index2 Player 2 ID
	 */
	public void refresh(int index1, int index2) {
		//log.debug("index1: {}, index2: {}", index1, index2);
		ta.setText("");
		if (index1 <= 0 && index2 <= 0) return;

		try {
			if (index2 > 0) {
				StyleConstants.setFontSize(attr, 10);

				insertName(index1, index2);

				doc.insertString(doc.getLength(), "\n", attr);
				insertRole(index1, index2);

				doc.insertString(doc.getLength(), "\n", attr);
				insertPhysical(index1, index2);
				insertStats(index1, index2);

				doc.insertString(doc.getLength(), "\n", attr);
				insertAbilities(index1, index2);
			} else {
				StyleConstants.setFontSize(attr, 12);

				insertName(index1, index2);

				doc.insertString(doc.getLength(), "\n\n", attr);
				insertAgeNation(index1, index2);

				doc.insertString(doc.getLength(), "\n", attr);
				insertPhysical(index1, index2);

				doc.insertString(doc.getLength(), "\n", attr);
				insertRole(index1, index2);
				//doc.insertString(doc.getLength(), stats.FAVORITE_SIDE.getString(index1), attr);

				doc.insertString(doc.getLength(), "\n\n", attr);
				insertSquads(index1);
			}
		} catch (BadLocationException e) {
			log.warn(String.format("Failed to refresh player %d/%d info:", index1, index2), e);
		}

		ta.setCaretPosition(0);
	}

	private void insertStats(int index1, int index2) throws BadLocationException {
		StyleConstants.setForeground(attr, Color.WHITE);

		insertStat(Stats.WEAK_FOOT_ACC, index1, index2);
		insertStat(Stats.WEAK_FOOT_FREQ, index1, index2);
		//insertStat(stats.statX, index1, index2);

		for (Stat s : Stats.ABILITY99) {
			insertStat(s, index1, index2);
		}

		insertStat(Stats.CONSISTENCY, index1, index2);
		insertStat(Stats.CONDITION, index1, index2);
	}

	private void insertRole(int index1, int index2) throws BadLocationException {
		StringBuilder text = new StringBuilder();

		if (index1 > 0 && Stats.getValue(of, index1, Stats.GK) != 0)
			text.append(Stats.GK);
		//
		if (index2 > 0 && Stats.getValue(of, index2, Stats.GK) != 0)
			text.append("\t\t").append(Stats.GK);

		text.append('\n');

		if (index1 > 0 && Stats.getValue(of, index1, Stats.CWP) != 0)
			text.append(Stats.CWP).append("  ");
		if (index1 > 0 && Stats.getValue(of, index1, Stats.CBT) != 0)
			text.append(Stats.CBT).append("  ");
		//
		if (index2 > 0) text.append("\t\t");
		if (index2 > 0 && Stats.getValue(of, index2, Stats.CWP) != 0)
			text.append(Stats.CWP).append("  ");
		if (index2 > 0 && Stats.getValue(of, index2, Stats.CBT) != 0)
			text.append(Stats.CBT).append("  ");

		text.append('\n');

		if (index1 > 0 && Stats.getValue(of, index1, Stats.SB) != 0)
			text.append(Stats.SB).append("  ");
		if (index1 > 0 && Stats.getValue(of, index1, Stats.WB) != 0)
			text.append(Stats.WB).append("  ");
		if (index1 > 0 && Stats.getValue(of, index1, Stats.DM) != 0)
			text.append(Stats.DM).append("  ");
		//
		if (index2 > 0) text.append("\t\t");
		if (index2 > 0 && Stats.getValue(of, index2, Stats.SB) != 0)
			text.append(Stats.SB).append("  ");
		if (index2 > 0 && Stats.getValue(of, index2, Stats.WB) != 0)
			text.append(Stats.WB).append("  ");
		if (index2 > 0 && Stats.getValue(of, index2, Stats.DM) != 0)
			text.append(Stats.DM).append("  ");

		text.append('\n');

		if (index1 > 0 && Stats.getValue(of, index1, Stats.CM) != 0)
			text.append(Stats.CM).append("  ");
		if (index1 > 0 && Stats.getValue(of, index1, Stats.SM) != 0)
			text.append(Stats.SM).append("  ");
		if (index1 > 0 && Stats.getValue(of, index1, Stats.AM) != 0)
			text.append(Stats.AM).append("  ");
		//
		if (index2 > 0) text.append("\t\t");
		if (index2 > 0 && Stats.getValue(of, index2, Stats.CM) != 0)
			text.append(Stats.CM).append("  ");
		if (index2 > 0 && Stats.getValue(of, index2, Stats.SM) != 0)
			text.append(Stats.SM).append("  ");
		if (index2 > 0 && Stats.getValue(of, index2, Stats.AM) != 0)
			text.append(Stats.AM).append("  ");

		text.append('\n');

		if (index1 > 0 && Stats.getValue(of, index1, Stats.SS) != 0)
			text.append(Stats.SS).append("  ");
		if (index1 > 0 && Stats.getValue(of, index1, Stats.CF) != 0)
			text.append(Stats.CF).append("  ");
		if (index1 > 0 && Stats.getValue(of, index1, Stats.WG) != 0)
			text.append(Stats.WG).append("  ");
		//
		if (index2 > 0) text.append("\t\t");
		if (index2 > 0 && Stats.getValue(of, index2, Stats.SS) != 0)
			text.append(Stats.SS).append("  ");
		if (index2 > 0 && Stats.getValue(of, index2, Stats.CF) != 0)
			text.append(Stats.CF).append("  ");
		if (index2 > 0 && Stats.getValue(of, index2, Stats.WG) != 0)
			text.append(Stats.WG).append("  ");

		int fsz = 0;
		if (index2 > 0) {
			fsz = StyleConstants.getFontSize(attr);
			StyleConstants.setFontSize(attr, fsz - 1);
		}
		//
		doc.insertString(doc.getLength(), text.toString(), attr);
		if (index2 > 0) {
			StyleConstants.setFontSize(attr, fsz);
		}
	}

	private void insertAbilities(int index1, int index2) throws BadLocationException {
		for (int i = 0; i < Stats.ABILITY_SPECIAL.length; i++) {
			if ((i % 2) != 0) {
				insertAbility(Stats.ABILITY_SPECIAL[i], index1, index2, Color.RED);
			} else {
				insertAbility(Stats.ABILITY_SPECIAL[i], index1, index2, Color.GREEN);
			}
		}

		StyleConstants.setForeground(attr, Color.WHITE);//Color.YELLOW
	}

	private void insertSquads(int index1) throws BadLocationException {
		if (!hasListSquads())
			return;

		StyleConstants.setForeground(attr, Color.WHITE);
		doc.insertString(doc.getLength(), Resources.getMessage("info.squads"), attr);

		ComboBoxModel<String> teams = selector.getTeamBox().getModel();
		int adr = Squads.NATION_ADR;
		while (adr < Squads.END_ADR) {
			int id = Bits.toInt16(of.getData(), adr);
			if (id > 0 && id == index1) {
				//log.debug("adr: {}", adr);

				int team;
				if (adr < Squads.CLUB_ADR) {
					if (adr >= Squads.CLUB_ADR - 2 * Formations.CLUB_TEAM_SIZE) {
						team = Squads.FIRST_CLUB - 1;
					} else {
						team = (adr - Squads.NATION_ADR) / (2 * Formations.NATION_TEAM_SIZE);
					}
				} else {
					team = (adr - Squads.CLUB_ADR) / (2 * Formations.CLUB_TEAM_SIZE) + Squads.FIRST_CLUB;
				}
				//log.debug("team: {}", team);

				if (team < teams.getSize())
					doc.insertString(doc.getLength(), "\n" + teams.getElementAt(team), attr);
			}

			adr += 2;
		}
	}

	private void insertName(int index1, int index2) throws BadLocationException {
		StyleConstants.setForeground(attr, Color.WHITE);

		if (index1 > 0) {
			doc.insertString(doc.getLength(), new Player(of, index1).getName(), attr);
		}
		if (index2 > 0) {
			doc.insertString(doc.getLength(), "\t" + new Player(of, index2).getName(), attr);
		}
	}

	private void insertAgeNation(int index1, int index2) throws BadLocationException {
		StyleConstants.setForeground(attr, Color.WHITE);

		if (index1 > 0) {
			doc.insertString(doc.getLength(), Stats.getString(of, index1, Stats.NATIONALITY), attr);
		}
		if (index2 > 0) {
			doc.insertString(doc.getLength(), "\t" + Stats.getString(of, index2, Stats.NATIONALITY), attr);
		}

		String s = Resources.getMessage("info.age");
		if (index1 > 0) {
			doc.insertString(doc.getLength(), "\n" + s + " " + Stats.getString(of, index1, Stats.AGE), attr);
		}
		if (index2 > 0) {
			doc.insertString(doc.getLength(), "\t" + s + " " + Stats.getString(of, index2, Stats.AGE), attr);
		}
	}

	private void insertPhysical(int index1, int index2) throws BadLocationException {
		StyleConstants.setForeground(attr, Color.WHITE);

		if (index1 > 0) {
			String info = buildPhysicalInfo(
					Stats.getValue(of, index1, Stats.FOOT), Stats.getValue(of, index1, Stats.FAVORITE_SIDE),
					Stats.getString(of, index1, Stats.HEIGHT), Stats.getString(of, index1, Stats.WEIGHT));
			doc.insertString(doc.getLength(), info, attr);
		}

		if (index2 > 0) {
			doc.insertString(doc.getLength(), (index1 > 0) ? "\t" : "\t\t", attr);

			String info = buildPhysicalInfo(
					Stats.getValue(of, index2, Stats.FOOT), Stats.getValue(of, index2, Stats.FAVORITE_SIDE),
					Stats.getString(of, index2, Stats.HEIGHT), Stats.getString(of, index2, Stats.WEIGHT));
			doc.insertString(doc.getLength(), info, attr);
		}
	}

	private static String buildPhysicalInfo(int foot, int side, String height, String weight) {
		String fs = Stats.MOD_FOOT_SIDE[foot * 3 + side];
		fs = fs.replaceAll("\\b(?i:foot)\\b", "F").replaceAll("\\b(?i:side)\\b", "S").replaceAll("\\s+", "");

		return String.format("%s, %scm, %sKg", fs, height, weight);
	}

	private void insertStat(Stat st, int index1, int index2) throws BadLocationException {
		int v1 = Stats.getValue(of, index1, st);
		int v2 = Stats.getValue(of, index2, st);
		String s1 = Stats.getString(of, index1, st);
		String s2 = Stats.getString(of, index2, st);

		doc.insertString(doc.getLength(), "\n" + st.getName() + "\t", attr);
		if (index1 > 0) {
			StyleConstants.setForeground(attr, getStatColor(st, v1));
			doc.insertString(doc.getLength(), s1, attr);
		}

		if (index2 > 0) {
			if (index1 > 0 && v1 != v2) {
				int div, add;
				if (st == Stats.WEAK_FOOT_ACC || st == Stats.WEAK_FOOT_FREQ
						|| st == Stats.CONSISTENCY || st == Stats.CONDITION) {
					div = 1;
					add = 0;
				} else {
					div = 3;
					add = 1;
				}

				int diff = v1 - v2;
				StyleConstants.setForeground(attr, (diff > 0) ? Color.GREEN : Color.RED);
				diff = Math.round((float) Math.abs(diff) / div + add);
				doc.insertString(doc.getLength(), getDifferenceStar(diff), attr);
			}

			StyleConstants.setForeground(attr, getStatColor(st, v2));
			doc.insertString(doc.getLength(), "\t" + s2, attr);
		}

		StyleConstants.setForeground(attr, Color.WHITE);
	}

	private static String getDifferenceStar(int diff) {
		StringBuilder sb = new StringBuilder(" ");
		for (int i = 0, n = Math.min(diff, 10); i < n; i++)
			sb.append('*');
		return sb.toString();
	}

	private void insertAbility(Stat stat, int index1, int index2, Color colour) throws BadLocationException {
		StyleConstants.setForeground(attr, colour);
		doc.insertString(doc.getLength(), "\n" + stat.getName() + "\t", attr);

		if (index1 > 0 && Stats.getValue(of, index1, stat) != 0) {
			doc.insertString(doc.getLength(), "O", attr);// \u2713
		}
		if (index2 > 0 && Stats.getValue(of, index2, stat) != 0) {
			doc.insertString(doc.getLength(), "\tO", attr);
		}
	}

	private static Color getStatColor(Stat st, int val) {
		if (st == Stats.WEAK_FOOT_ACC || st == Stats.WEAK_FOOT_FREQ
				|| st == Stats.CONSISTENCY || st == Stats.CONDITION) {
			if (val >= 7) {
				return Color.RED;
			} else if (val == 6) {
				return Color.ORANGE;
			}
		} else {
			if (val >= 95) {
				return Color.RED;
			} else if (val >= 90) {
				return Color.ORANGE;
			} else if (val >= 80) {
				return Color.YELLOW;
			} else if (val >= 75) {
				return Colors.CHARTREUSE0;
			}
		}
		return Color.WHITE;
	}

}
