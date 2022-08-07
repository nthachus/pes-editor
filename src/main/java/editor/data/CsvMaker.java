package editor.data;

import editor.lang.NullArgumentException;
import editor.util.Files;
import editor.util.Resources;
import editor.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class CsvMaker implements Serializable {
	private static final long serialVersionUID = -4764145193633313484L;
	private static final Logger log = LoggerFactory.getLogger(CsvMaker.class);

	private final char separator;

	public CsvMaker(boolean useTab) {
		separator = useTab ? '\t' : ',';
	}

	public CsvMaker() {
		this(false);
	}

	public boolean makeFile(OptionFile of, File dest, boolean headings/*, boolean extra*/, boolean create) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		if (null == dest) {
			throw new NullArgumentException("dest");
		}
		log.debug("Try to make CSV file: {}", dest);

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

			/*if (extra) {
				for (int player = Player.FIRST_UNUSED; player < Player.TOTAL; player++) {
					writePlayer(of, out, player, teams);
				}
			}*/

			if (create) {
				for (int player = 0; player < Player.TOTAL_EDIT; player++) {
					writePlayer(of, out, player + Player.FIRST_EDIT, teams);
				}
			}

			log.debug("Making of CSV file '{}' succeeded", dest);
			return true;
		} catch (IOException e) {
			log.error("Failed to make CSV file:", e);
		} finally {
			Files.closeStream(out);
		}

		return false;
	}

	private void writeHeadings(DataOutput out) throws IOException {
		String s = Resources.getMessage("csv.headers");
		String[] headings = Strings.COMMA_REGEX.split(s);

		for (int i = 0; i < headings.length; i++) {
			if (i > 0) {
				out.write(separator);
			}
			out.write(headings[i].getBytes(Strings.UTF8));
		}
	}

	private void writePlayer(OptionFile of, DataOutput out, int player, String[] clubNames) throws IOException {
		writeName(of, out, player);

		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.AGE));
		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.HEIGHT));
		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.WEIGHT));
		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.FOOT));
		out.write(separator);
		out.writeBytes(getSide(of, player));

		for (Stat s : Stats.ROLES) {
			out.write(separator);
			out.writeBytes(Stats.getString(of, player, s));
		}

		out.write(separator);
		int v = Stats.getValue(of, player, Stats.REG_POS);
		//assert (v >= 0 && v <= Stats.ROLES.length) : "Invalid registered position #" + v + " of player #" + player;
		if (v > Stats.ROLES.length) {
			out.writeBytes(v + "?");
		} else {
			out.writeBytes(Stats.ROLES[Stats.regPosToRole(v)] + (v == 1 ? "*" : Strings.EMPTY));
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
		out.writeBytes(Stats.getString(of, player, Stats.GROWTH));
		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.FACE));
		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.FACE_TYPE));
		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.SKIN));
		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.HEAD_LENGTH));
		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.HEAD_WIDTH));
		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.HAIR));

		for (Stat s : Stats.ABILITY99) {
			out.write(separator);
			out.writeBytes(Stats.getString(of, player, s));
		}

		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.CONDITION));
		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.WEAK_FOOT_ACC));
		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.WEAK_FOOT_FREQ));
		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.CONSISTENCY));

		for (Stat s : Stats.ABILITY_SPECIAL) {
			out.write(separator);
			out.writeBytes(Stats.getString(of, player, s));
		}

		out.write(separator);
		out.writeBytes(Stats.getString(of, player, Stats.NATIONALITY));

		out.write(separator);
		writeInternationalStatus(of, out, player);

		out.write(separator);
		writeClassicStatus(of, out, player);

		out.write(separator);
		writeTeam(of, out, player, clubNames);

		out.writeBytes(Strings.NEW_LINE);
	}

	private void writeName(OptionFile of, DataOutput out, int player) throws IOException {
		String name = Player.getName(of, player);
		name = name.replace(separator, ' ');
		out.write(name.getBytes(Strings.UTF8));
	}

	private static void writeInternationalStatus(OptionFile of, DataOutput out, int player) throws IOException {
		int playerNationalNo = 0;

		int nat = Stats.getValue(of, player, Stats.NATIONALITY);
		if (nat < Squads.NATION_COUNT) {
			for (int np = 0; np < Formations.NATION_TEAM_SIZE; np++) {

				int p = Squads.getTeamPlayer(of, nat, np);
				if (p == player) {
					// get squad number
					playerNationalNo = Squads.getTeamSquadNum(of, nat, np);
					break;
				}
			}
		}

		out.writeBytes(Integer.toString(playerNationalNo));
	}

	private static void writeClassicStatus(OptionFile of, DataOutput out, int player) throws IOException {
		int playerClassicNo = 0;

		String nat = Stats.getString(of, player, Stats.NATIONALITY);
		int cNat = Squads.getClassicNation(nat);
		if (cNat > 0) {
			for (int np = 0; np < Formations.NATION_TEAM_SIZE; np++) {

				int p = Squads.getTeamPlayer(of, cNat, np);
				if (p == player) {
					// get squad number
					playerClassicNo = Squads.getTeamSquadNum(of, cNat, np);
					break;
				}
			}
		}

		out.writeBytes(Integer.toString(playerClassicNo));
	}

	private void writeTeam(OptionFile of, DataOutput out, int player, String[] clubNames) throws IOException {
		int playerClubNo = 0;
		String club = Strings.EMPTY;

		boolean found = false;
		for (int c = 0; c < Clubs.TOTAL && !found; c++) {
			for (int np = 0; np < Formations.CLUB_TEAM_SIZE; np++) {

				int p = Squads.getTeamPlayer(of, c + Squads.FIRST_CLUB, np);
				if (p == player) {
					// get squad number
					playerClubNo = Squads.getTeamSquadNum(of, c + Squads.FIRST_CLUB, np);
					club = clubNames[c];

					found = true;
					break;
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
		if (side == 0) {
			return Stats.getString(of, player, Stats.FOOT);
		} else if (side == 1) {
			return Stats.MOD_FOOT[1 - Stats.getValue(of, player, Stats.FOOT)];
		}
		return Stats.MOD_FOOT[1] + "&" + Stats.MOD_FOOT[0];
	}

}
