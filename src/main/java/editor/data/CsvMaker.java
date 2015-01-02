package editor.data;

import editor.Clubs;
import editor.Player;
import editor.util.Bits;
import editor.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class CsvMaker implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(CsvMaker.class);

	private final char separator;

	public CsvMaker(boolean useTab) {
		separator = useTab ? '\t' : ',';
	}

	public CsvMaker() {
		this(false);
	}

	public boolean makeFile(OptionFile of, File dest, boolean headings, boolean extra, boolean create) {
		if (null == of) throw new NullPointerException("of");
		if (null == dest) throw new NullPointerException("dest");

		RandomAccessFile out = null;
		try {
			out = new RandomAccessFile(dest, "rw");
			out.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});// UTF-8 BOM

			if (headings) {
				writeHeadings(out);
				out.writeBytes(Strings.NEW_LINE);
			}

			String[] teams = Clubs.getNames(of);
			for (int player = 1; player < Player.FIRST_UNUSED; player++) {
				writePlayer(of, out, player, teams);
			}

			if (extra) {
				for (int player = Player.FIRST_UNUSED; player < Player.TOTAL; player++) {
					writePlayer(of, out, player, teams);
				}
			}

			if (create) {
				for (int player = 0; player < Player.TOTAL_EDIT; player++) {
					writePlayer(of, out, player + Player.FIRST_EDIT, teams);
				}
			}

			return true;
		} catch (IOException e) {
			log.error("Failed to make CSV file:", e);
		} finally {
			if (null != out) {
				try {
					out.close();
				} catch (IOException e) {
					log.warn(e.toString());
				}
			}
		}

		return false;
	}

	//region CSV Header

	private static final String[] HEADINGS = {
			"NAME",
			"GK  0",
			"SW  1",
			"SW  2",
			"CB  3",
			"SB  4",
			"DMF  5",
			"WB  6",
			"CMF  7",
			"SMF  8",
			"AMF  9",
			"WF 10",
			"SS  11",
			"CF  12",
			"REGISTERED POSITION",
			"HEIGHT",
			"STRONG FOOT",
			"FAVOURED SIDE",
			"WEAK FOOT ACCURACY",
			"WEAK FOOT FREQUENCY",
			"ATTACK",
			"DEFENSE",
			"BALANCE",
			"STAMINA",
			"TOP SPEED",
			"ACCELERATION",
			"RESPONSE",
			"AGILITY",
			"DRIBBLE ACCURACY",
			"DRIBBLE SPEED",
			"SHORT PASS ACCURACY",
			"SHORT PASS SPEED",
			"LONG PASS ACCURACY",
			"LONG PASS SPEED",
			"SHOT ACCURACY",
			"SHOT POWER",
			"SHOT TECHNIQUE",
			"FREE KICK ACCURACY",
			"SWERVE",
			"HEADING",
			"JUMP",
			"TECHNIQUE",
			"AGGRESSION",
			"MENTALITY",
			"GOAL KEEPING",
			"TEAM WORK",
			"CONSISTENCY",
			"CONDITION / FITNESS",
			"DRIBBLING",
			"TACTICAL DRIBBLE",
			"POSITIONING",
			"REACTION",
			"PLAYMAKING",
			"PASSING",
			"SCORING",
			"1-1 SCORING",
			"POST PLAYER",
			"LINES",
			"MIDDLE SHOOTING",
			"SIDE",
			"CENTRE",
			"PENALTIES",
			"1-TOUCH PASS",
			"OUTSIDE",
			"MARKING",
			"SLIDING",
			"COVERING",
			"D-LINE CONTROL",
			"PENALTY STOPPER",
			"1-ON-1 STOPPER",
			"LONG THROW",
			"INJURY TOLERANCE",
			"DRIBBLE STYLE",
			"FREE KICK STYLE",
			"PK STYLE",
			"DROP KICK STYLE",
			"AGE",
			"WEIGHT",
			"NATIONALITY",
			"INTERNATIONAL NUMBER",
			"CLASSIC NUMBER",
			"CLUB TEAM",
			"CLUB NUMBER"
	};

	private void writeHeadings(DataOutput out) throws IOException {
		for (int i = 0; i < HEADINGS.length; i++) {
			if (i > 0) out.write(separator);
			out.writeBytes(HEADINGS[i]);
		}
	}

	//endregion

	private void writePlayer(OptionFile of, DataOutput out, int player, String[] clubNames) throws IOException {
		writeName(of, out, player);

		for (Stat s : Stats.ROLES) {
			out.write(separator);
			out.writeBytes(Stats.getString(of, player, s));
		}

		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.REG_POS));

		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.HEIGHT));
		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.FOOT));
		out.write(separator);
		out.writeBytes(getSide(of, player));
		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.WEAK_FOOT_ACC));
		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.WEAK_FOOT_FREQ));

		for (Stat s : Stats.ABILITY99) {
			out.write(separator);
			out.writeBytes(Stats.getString(of, player, s));
		}

		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.CONSISTENCY));
		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.CONDITION));

		for (Stat s : Stats.ABILITY_SPECIAL) {
			out.write(separator);
			out.writeBytes(Stats.getString(of, player, s));
		}

		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.INJURY));
		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.DRIBBLE_STYLE));
		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.FREE_KICK));
		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.PK_STYLE));
		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.DK_STYLE));
		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.AGE));
		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.WEIGHT));
		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.NATIONALITY));

		out.write(separator);
		writeInterStatus(of, out, player);

		out.write(separator);
		writeClassicStatus(of, out, player);

		out.write(separator);
		writeTeam(of, out, player, clubNames);

		out.writeBytes(Strings.NEW_LINE);
	}

	private void writeName(OptionFile of, DataOutput out, int player) throws IOException {
		Player p = new Player(of, player, 0);
		String name = p.name.replace(separator, ' ');
		out.write(name.getBytes(Strings.UTF8));
	}

	private static void writeInterStatus(OptionFile of, DataOutput out, int player) throws IOException {
		int playerNationalNo = 0;

		int nat = Stats.getValue(of, player, Stats.NATIONALITY);
		if (nat < Squads.NATION_COUNT) {
			for (int np = 0; np < Formations.NATION_TEAM_SIZE; np++) {

				int ofs = Squads.NATION_ADR + (Formations.NATION_TEAM_SIZE * nat + np) * 2;
				int p = Bits.toInt16(of.getData(), ofs);
				if (p == player) {
					// get squad number
					ofs = Squads.NATION_NUM_ADR + Formations.NATION_TEAM_SIZE * nat + np;
					playerNationalNo = Bits.toInt(of.getData()[ofs]) + 1;
					break;
				}
			}
		}

		out.writeBytes(Integer.toString(playerNationalNo));
	}

	private static int getClassicNationId(String nation) {
		for (int i = 0; i < Squads.CLASSIC_COUNT; i++) {
			if (Squads.EXTRAS[i].substring(8).equalsIgnoreCase(nation))
				return 60 + i;
		}
		return -1;
	}

	private static void writeClassicStatus(OptionFile of, DataOutput out, int player) throws IOException {
		int playerClassicNo = 0;

		String nat = Stats.getString(of, player, Stats.NATIONALITY);
		int cNat = getClassicNationId(nat);
		if (cNat > 0) {
			for (int np = 0; np < Formations.NATION_TEAM_SIZE; np++) {

				int ofs = Squads.NATION_ADR + (Formations.NATION_TEAM_SIZE * cNat + np) * 2;
				int p = Bits.toInt16(of.getData(), ofs);
				if (p == player) {
					// get squad number
					ofs = Squads.NATION_NUM_ADR + Formations.NATION_TEAM_SIZE * cNat + np;
					playerClassicNo = Bits.toInt(of.getData()[ofs]) + 1;
					break;
				}
			}
		}

		out.writeBytes(Integer.toString(playerClassicNo));
	}

	private void writeTeam(OptionFile of, DataOutput out, int player, String[] clubNames) throws IOException {
		int playerClubNo = 0;
		String club = "";

		outerLoop:
		for (int c = 0; c < Clubs.TOTAL; c++) {
			for (int np = 0; np < Formations.CLUB_TEAM_SIZE; np++) {

				int ofs = Squads.CLUB_ADR + (Formations.CLUB_TEAM_SIZE * c + np) * 2;
				int p = Bits.toInt16(of.getData(), ofs);
				if (p == player) {
					// get squad number
					ofs = Squads.CLUB_NUM_ADR + Formations.CLUB_TEAM_SIZE * c + np;
					playerClubNo = Bits.toInt(of.getData()[ofs]) + 1;
					club = clubNames[c];
					break outerLoop;
				}
			}
		}

		out.write(club.getBytes(Strings.UTF8));

		out.write(separator);
		out.writeBytes(Integer.toString(playerClubNo));
	}

	private static String getSide(OptionFile of, int player) {
		int side = Stats.getValue(of, player, Stats.FAVORITE_SIDE);

		// same side with stronger-foot
		if (side == 0)
			return Stats.getString(of, player, Stats.FOOT);

		if (side == 1)
			return Stats.MOD_FOOT[1 - Stats.getValue(of, player, Stats.FOOT)];

		return Stats.MOD_FOOT[1] + "&" + Stats.MOD_FOOT[0];
	}

}
