package editor.ui;

import editor.data.*;
import editor.lang.NullArgumentException;
import editor.util.Bits;
import editor.util.Resources;
import editor.util.Strings;
import editor.util.UIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.font.FontRenderContext;

/**
 * Player information panel.
 */
public class InfoPanel extends JScrollPane {
	private static final long serialVersionUID = -4953711000070301800L;
	private static final Logger log = LoggerFactory.getLogger(InfoPanel.class);

	private final OptionFile of;
	private final SelectByTeam selector;

	public InfoPanel(OptionFile of, SelectByTeam teamDropdown) {
		super(VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);

		if (null == of) {
			throw new NullArgumentException("of");
		}
		this.of = of;
		selector = teamDropdown;

		log.debug("Initialize Info panel #{} for Team dropdown: {}", hashCode(), Strings.valueOf(teamDropdown));
		initComponents();
	}

	public InfoPanel(OptionFile of) {
		this(of, null);
	}

	private JEditorPane ta;
	private MutableAttributeSet attr;
	private StyledDocument doc;

	private void initComponents() {
		//setBorder(BorderFactory.createTitledBorder("Player info"));

		StyledEditorKit sek = new StyledEditorKit();
		ta = new JEditorPane();
		ta.setEditable(false);
		ta.setEditorKit(sek);
		ta.setBackground(Color.BLACK);

		setViewportView(ta);

		attr = sek.getInputAttributes();
		doc = (StyledDocument) ta.getDocument();

		setPreferredSize(new Dimension(hasSquadList() ? 290 : 145, 493));

		StyleConstants.setFontFamily(attr, UIUtil.SANS_SERIF);
	}

	private boolean hasSquadList() {
		return (null != selector);
	}

	/**
	 * @param index1 ID of Player 1
	 * @param index2 ID of Player 2
	 */
	public void refresh(int index1, int index2) {
		log.info("Refresh Info panel with players: {} <-> {}", index1, index2);

		ta.setText(Strings.EMPTY);
		if (index1 <= 0 && index2 <= 0) {
			return;
		}

		try {
			if (index2 > 0) {
				StyleConstants.setFontSize(attr, 10);

				insertName(index1, index2);

				doc.insertString(doc.getLength(), Strings.LF, attr);
				insertRole(index1, index2);

				doc.insertString(doc.getLength(), Strings.LF, attr);
				insertPhysical(index1, index2);
				insertStats(index1, index2);

				doc.insertString(doc.getLength(), Strings.LF, attr);
				insertAbilities(index1, index2);
			} else {
				StyleConstants.setFontSize(attr, 12);

				insertName(index1, index2);

				doc.insertString(doc.getLength(), Strings.LF + Strings.LF, attr);
				insertAgeNation(index1, index2);

				doc.insertString(doc.getLength(), Strings.LF, attr);
				insertPhysical(index1, index2);

				doc.insertString(doc.getLength(), Strings.LF, attr);
				insertRole(index1, index2);
				//doc.insertString(doc.getLength(), stats.FAVORITE_SIDE.getString(index1), attr);

				doc.insertString(doc.getLength(), Strings.LF + Strings.LF, attr);
				insertSquads(index1);
			}

			log.debug("Refresh info succeeded for player: {} <-> {}", index1, index2);
		} catch (BadLocationException e) {
			log.warn(String.format("Failed to refresh info for player: %d - %d", index1, index2), e);
		}

		ta.setCaretPosition(0);
	}

	private void insertStats(int index1, int index2) throws BadLocationException {
		StyleConstants.setForeground(attr, Color.WHITE);

		Font f = doc.getFont(attr);
		insertStat(Stats.WEAK_FOOT_ACC, index1, index2, f);
		insertStat(Stats.WEAK_FOOT_FREQ, index1, index2, f);
		//insertStat(stats.statX, index1, index2, f);

		for (Stat s : Stats.ABILITY99) {
			insertStat(s, index1, index2, f);
		}

		insertStat(Stats.CONSISTENCY, index1, index2, f);
		insertStat(Stats.CONDITION, index1, index2, f);
	}

	private static String buildRoleLine(OptionFile of, int index, Stat... stats) {
		StringBuilder s = new StringBuilder();
		for (Stat st : stats) {
			if (Stats.getValue(of, index, st) != 0) {
				s.append(Strings.SPACE).append(Strings.SPACE).append(st);
			}
		}
		return s.toString();
	}

	private void insertRole(int index1, int index2) throws BadLocationException {
		int fsz = 0;
		if (index2 > 0) {
			fsz = StyleConstants.getFontSize(attr);
			StyleConstants.setFontSize(attr, fsz - 1);
		}

		StringBuilder text = new StringBuilder();
		String s1, s2;
		Font f = doc.getFont(attr);

		if (index1 > 0) {
			s1 = buildRoleLine(of, index1, Stats.GK);
			text.append(s1);
		} else {
			s1 = null;
		}
		if (index2 > 0) {
			s2 = buildRoleLine(of, index2, Stats.GK);
			if (s2.length() > 0) {
				appendTabIfNeed(text, s1, f);
				text.append(Strings.TAB).append(s2);
			}
		}

		text.append(Strings.LF);
		if (index1 > 0) {
			s1 = buildRoleLine(of, index1, Stats.CWP, Stats.CBT);
			text.append(s1);
		} else {
			s1 = null;
		}
		if (index2 > 0) {
			s2 = buildRoleLine(of, index2, Stats.CWP, Stats.CBT);
			if (s2.length() > 0) {
				appendTabIfNeed(text, s1, f);
				text.append(Strings.TAB).append(s2);
			}
		}

		text.append(Strings.LF);
		if (index1 > 0) {
			s1 = buildRoleLine(of, index1, Stats.SB, Stats.WB, Stats.DM);
			text.append(s1);
		} else {
			s1 = null;
		}
		if (index2 > 0) {
			s2 = buildRoleLine(of, index2, Stats.SB, Stats.WB, Stats.DM);
			if (s2.length() > 0) {
				appendTabIfNeed(text, s1, f);
				text.append(Strings.TAB).append(s2);
			}
		}

		text.append(Strings.LF);
		if (index1 > 0) {
			s1 = buildRoleLine(of, index1, Stats.CM, Stats.SM, Stats.AM);
			text.append(s1);
		} else {
			s1 = null;
		}
		if (index2 > 0) {
			s2 = buildRoleLine(of, index2, Stats.CM, Stats.SM, Stats.AM);
			if (s2.length() > 0) {
				appendTabIfNeed(text, s1, f);
				text.append(Strings.TAB).append(s2);
			}
		}

		text.append(Strings.LF);
		if (index1 > 0) {
			s1 = buildRoleLine(of, index1, Stats.SS, Stats.CF, Stats.WG);
			text.append(s1);
		} else {
			s1 = null;
		}
		if (index2 > 0) {
			s2 = buildRoleLine(of, index2, Stats.SS, Stats.CF, Stats.WG);
			if (s2.length() > 0) {
				appendTabIfNeed(text, s1, f);
				text.append(Strings.TAB).append(s2);
			}
		}

		doc.insertString(doc.getLength(), text.toString(), attr);
		if (index2 > 0) {
			StyleConstants.setFontSize(attr, fsz);
		}
	}

	private static void appendTabIfNeed(StringBuilder text, String s, Font f) {
		if (isShortenThanTab(s, f)) {
			text.append(Strings.TAB);
		}
	}

	private void insertAbilities(int index1, int index2) throws BadLocationException {
		Font f = doc.getFont(attr);
		for (int i = 0; i < Stats.ABILITY_SPECIAL.length; i++) {
			if ((i % 2) != 0) {
				insertAbility(Stats.ABILITY_SPECIAL[i], index1, index2, Color.RED, f);
			} else {
				insertAbility(Stats.ABILITY_SPECIAL[i], index1, index2, Color.GREEN, f);
			}
		}

		StyleConstants.setForeground(attr, Color.WHITE);//Color.YELLOW
	}

	private void insertSquads(int index1) throws BadLocationException {
		if (!hasSquadList()) {
			return;
		}

		StyleConstants.setForeground(attr, Color.WHITE);
		doc.insertString(doc.getLength(), Resources.getMessage("info.squads"), attr);

		ComboBoxModel teams = selector.getTeamBox().getModel();
		int adr = Squads.NATION_ADR;
		int endAdr = Squads.END_ADR - Squads.EXTRA_CLUBS_COUNT * Formations.CLUB_TEAM_SIZE * 2;
		while (adr < endAdr) {
			int id = Bits.toInt16(of.getData(), adr);
			if (id > 0 && id == index1) {

				//log.debug("adr: {}", adr);
				int team = Squads.getTeamFromAdr(adr);
				//log.debug("team: {}", team);
				if (team >= 0 && team < teams.getSize()) {
					doc.insertString(doc.getLength(), Strings.LF + teams.getElementAt(team), attr);
				}
			}

			adr += 2;
		}
	}

	private void insertName(int index1, int index2) throws BadLocationException {
		StyleConstants.setForeground(attr, Color.WHITE);

		StringBuilder s = new StringBuilder();
		if (index1 > 0) {
			s.append(Player.getName(of, index1))
					.append(Strings.SPACE).append(Strings.SPACE).append('[').append(index1).append(']');
		}
		if (index2 > 0) {
			appendTabIfNeed(s, s.toString(), doc.getFont(attr));
			s.append(Strings.TAB).append(Player.getName(of, index2))
					.append(Strings.SPACE).append(Strings.SPACE).append('[').append(index2).append(']');
		}
		doc.insertString(doc.getLength(), s.toString(), attr);
	}

	private static boolean isShortenThanTab(String s, Font f) {
		if (Strings.isEmpty(s)) {
			return true;
		}
		FontRenderContext c = new FontRenderContext(f.getTransform(), false, false);
		return f.getStringBounds(s, c).getWidth() < 72;// a tab every 72 pixels
	}

	private void insertAgeNation(int index1, int index2) throws BadLocationException {
		StyleConstants.setForeground(attr, Color.WHITE);

		if (index1 > 0) {
			doc.insertString(doc.getLength(), Stats.getString(of, index1, Stats.NATIONALITY), attr);
		}
		if (index2 > 0) {
			doc.insertString(doc.getLength(), Strings.TAB + Stats.getString(of, index2, Stats.NATIONALITY), attr);
		}

		String s = Resources.getMessage("info.age");
		if (index1 > 0) {
			doc.insertString(doc.getLength(), Strings.LF + s + Strings.SPACE + Stats.getString(of, index1, Stats.AGE), attr);
		}
		if (index2 > 0) {
			doc.insertString(doc.getLength(), Strings.TAB + s + Strings.SPACE + Stats.getString(of, index2, Stats.AGE), attr);
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
			String info = Strings.TAB + buildPhysicalInfo(
					Stats.getValue(of, index2, Stats.FOOT), Stats.getValue(of, index2, Stats.FAVORITE_SIDE),
					Stats.getString(of, index2, Stats.HEIGHT), Stats.getString(of, index2, Stats.WEIGHT));
			if (index1 <= 0) {
				info = Strings.TAB + info;
			}
			doc.insertString(doc.getLength(), info, attr);
		}
	}

	private static String buildPhysicalInfo(int foot, int side, String height, String weight) {
		String fs = Stats.MOD_FOOT_SIDE[foot * 3 + side];
		fs = fs.replaceAll("\\b(?i:foot)\\b", "F")
				.replaceAll("\\b(?i:side)\\b", "S")
				.replaceAll("\\s+", Strings.EMPTY);

		return String.format("%s, %scm, %sKg", fs, height, weight);
	}

	private void insertStat(Stat st, int index1, int index2, Font f) throws BadLocationException {
		int v1 = Stats.getValue(of, index1, st);
		int v2 = Stats.getValue(of, index2, st);
		String s1 = Stats.getString(of, index1, st);
		String s2 = Stats.getString(of, index2, st);

		doc.insertString(doc.getLength(), Strings.LF + translateStatName(st.getName(), f) + Strings.TAB, attr);
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
				doc.insertString(doc.getLength(), buildDifferenceStars(diff), attr);
			}

			StyleConstants.setForeground(attr, getStatColor(st, v2));
			doc.insertString(doc.getLength(), Strings.TAB + s2, attr);
		}

		StyleConstants.setForeground(attr, Color.WHITE);
	}

	private static String translateStatName(String statName, Font f) {
		statName = Resources.getMessage(statName);
		if (isShortenThanTab(statName, f)) {
			statName += Strings.TAB;
		}
		return statName;
	}

	private static String buildDifferenceStars(int diff) {
		StringBuilder sb = new StringBuilder(Strings.SPACE);
		for (int i = 0, n = Math.min(diff, 10); i < n; i++) {
			sb.append('*');
		}
		return sb.toString();
	}

	private void insertAbility(Stat stat, int index1, int index2, Color color, Font f) throws BadLocationException {
		StyleConstants.setForeground(attr, color);
		doc.insertString(doc.getLength(), Strings.LF + translateStatName(stat.getName(), f) + Strings.TAB, attr);

		if (index1 > 0 && Stats.getValue(of, index1, stat) != 0) {
			doc.insertString(doc.getLength(), "O", attr);// \u2713
		}
		if (index2 > 0 && Stats.getValue(of, index2, stat) != 0) {
			doc.insertString(doc.getLength(), Strings.TAB + "O", attr);
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
				return UIUtil.CHARTREUSE0;
			}
		}
		return Color.WHITE;
	}

}
