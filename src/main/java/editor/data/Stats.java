package editor.data;

import editor.lang.NullArgumentException;
import editor.util.Arrays;
import editor.util.Bits;

public final class Stats {
	private Stats() {
	}

	//region Stat Definitions

	/*
	public static final Stat HAIR = new Stat(StatType.integer, 45, 0, 0x7FF, "Hair");
	public static final Stat FACE_TYPE = new Stat(StatType.integer, 55, 0, 0x3, "Face Type");
	public static final Stat SKIN = new Stat(StatType.integer, 41, 6, 0x3, "Skin");
	public static final Stat FACE = new Stat(StatType.integer, 53, 5, 0x1FF, "Face");
	public static final Stat FACE2 = new Stat(StatType.integer, 54, 0, 0x1FF, "face2");
	*/

	public static final Stat NAME_EDITED = new Stat(StatType.integer, 3, 0, 0x1, "Name Edited");
	public static final Stat CALL_EDITED = new Stat(StatType.integer, 3, 2, 0x1, "Call Edited");
	public static final Stat SHIRT_EDITED = new Stat(StatType.integer, 3, 1, 0x1, "Shirt Edited");
	public static final Stat ABILITY_EDITED = new Stat(StatType.integer, 40, 4, 0x1, "Ability Edited");

	public static final Stat CALL_NAME = new Stat(StatType.integer, 1, 0, 0xFFFF, "Call Name");
	public static final Stat HEIGHT = new Stat(StatType.height148, 41, 0, 0x3F, "Height");
	public static final Stat FOOT = new Stat(StatType.footId, 5, 0, 0x01, "Foot");// Stronger Foot
	/**
	 * The favorite side/foot of a player (0: Same side with stronger-foot, 1: Difference stronger-foot, 2: L&R).
	 */
	public static final Stat FAVORITE_SIDE = new Stat(StatType.integer, 33, 14, 0x03, "Favorite Side");
	public static final Stat WEAK_FOOT_ACC = new Stat(StatType.positiveInt, 33, 11, 0x07, "W-Foot Accuracy");
	public static final Stat WEAK_FOOT_FREQ = new Stat(StatType.positiveInt, 33, 3, 0x07, "W-Foot Frequency");
	public static final Stat INJURY = new Stat(StatType.injuryId, 33, 6, 0x03, "Injury Tolerance");
	public static final Stat DRIBBLE_STYLE = new Stat(StatType.positiveInt, 6, 0, 0x03, "Dribbling Style");
	public static final Stat FREE_KICK = new Stat(StatType.positiveInt, 5, 1, 0x0F, "Free Kick");
	public static final Stat PK_STYLE = new Stat(StatType.positiveInt, 5, 5, 0x07, "Penalty Kick");
	public static final Stat DK_STYLE = new Stat(StatType.positiveInt, 6, 2, 0x03, "Dropkick Style");
	public static final Stat AGE = new Stat(StatType.age15, 65, 9, 0x1F, "Age");
	public static final Stat WEIGHT = new Stat(StatType.integer, 41, 8, 0x7F, "Weight");
	public static final Stat NATIONALITY = new Stat(StatType.nationId, 65, 0, 0xFF, "Nationality");
	public static final Stat CONSISTENCY = new Stat(StatType.positiveInt, 33, 0, 0x07, "Consistency");
	public static final Stat CONDITION = new Stat(StatType.positiveInt, 33, 8, 0x07, "Condition");// Form
	public static final Stat REG_POS = new Stat(StatType.integer, 6, 4, 0xF, "Registered Position");

	public static final Stat GK = new Stat(StatType.integer, 7, 7, 1, "GK");
	public static final Stat CWP = new Stat(StatType.integer, 7, 15, 1, "SW");
	public static final Stat CBT = new Stat(StatType.integer, 9, 7, 1, "CB");
	public static final Stat SB = new Stat(StatType.integer, 9, 15, 1, "SB");
	public static final Stat DM = new Stat(StatType.integer, 11, 7, 1, "DMF");
	public static final Stat WB = new Stat(StatType.integer, 11, 15, 1, "WB");
	public static final Stat CM = new Stat(StatType.integer, 13, 7, 1, "CMF");
	public static final Stat SM = new Stat(StatType.integer, 13, 15, 1, "SMF");
	public static final Stat AM = new Stat(StatType.integer, 15, 7, 1, "AMF");
	public static final Stat WG = new Stat(StatType.integer, 15, 15, 1, "WF");
	public static final Stat SS = new Stat(StatType.integer, 17, 7, 1, "SS");
	public static final Stat CF = new Stat(StatType.integer, 17, 15, 1, "CF");

	public static final Stat[] ROLES = {
			GK,// goalkeeper
			CWP,// sweeper
			CBT,// centreBack
			SB,// sideBack
			DM,// defensiveMidfielder
			WB,// wingBack
			CM,// centralMidfielder
			SM,// sideMidfielder
			AM,// attackingMidfielder
			WG,// wingStriker
			SS,// secondStriker
			CF// centreForward
	};

	public static final Stat ATTACK = new Stat(StatType.integer, 7, 0, 0x7F, "Attack");
	public static final Stat DEFENCE = new Stat(StatType.integer, 8, 0, 0x7F, "Defense");
	public static final Stat BALANCE = new Stat(StatType.integer, 9, 0, 0x7F, "Balance");
	public static final Stat STAMINA = new Stat(StatType.integer, 10, 0, 0x7F, "Stamina");
	public static final Stat SPEED = new Stat(StatType.integer, 11, 0, 0x7F, "Speed");
	public static final Stat ACCELERATION = new Stat(StatType.integer, 12, 0, 0x7F, "Acceleration");
	public static final Stat RESPONSE = new Stat(StatType.integer, 13, 0, 0x7F, "Response");
	public static final Stat AGILITY = new Stat(StatType.integer, 14, 0, 0x7F, "Agility");// Explosive power
	public static final Stat DRIBBLE_ACC = new Stat(StatType.integer, 15, 0, 0x7F, "Dribble Accuracy");
	public static final Stat DRIBBLE_SPEED = new Stat(StatType.integer, 16, 0, 0x7F, "Dribble Speed");
	public static final Stat S_PASS_ACC = new Stat(StatType.integer, 17, 0, 0x7F, "S-Pass Accuracy");
	public static final Stat S_PASS_SPEED = new Stat(StatType.integer, 18, 0, 0x7F, "S-Pass Speed");
	public static final Stat L_PASS_ACC = new Stat(StatType.integer, 19, 0, 0x7F, "L-Pass Accuracy");
	public static final Stat L_PASS_SPEED = new Stat(StatType.integer, 20, 0, 0x7F, "L-Pass Speed");
	public static final Stat SHOT_ACC = new Stat(StatType.integer, 21, 0, 0x7F, "Shot Accuracy");
	public static final Stat SHOT_POWER = new Stat(StatType.integer, 22, 0, 0x7F, "Shot Power");
	public static final Stat SHOT_TEC = new Stat(StatType.integer, 23, 0, 0x7F, "Shot Technique");
	public static final Stat FK_ACC = new Stat(StatType.integer, 24, 0, 0x7F, "FK Accuracy");// Place kicking
	public static final Stat CURLING = new Stat(StatType.integer, 25, 0, 0x7F, "Swerve");
	public static final Stat HEADING = new Stat(StatType.integer, 26, 0, 0x7F, "Heading");// Header accuracy
	public static final Stat JUMP = new Stat(StatType.integer, 27, 0, 0x7F, "Jump");
	public static final Stat TECH = new Stat(StatType.integer, 29, 0, 0x7F, "Technique");// Ball control
	public static final Stat AGGRESSION = new Stat(StatType.integer, 30, 0, 0x7F, "Aggression");
	public static final Stat MENTALITY = new Stat(StatType.integer, 31, 0, 0x7F, "Mentality");// Tenacity
	public static final Stat GK_ABILITY = new Stat(StatType.integer, 32, 0, 0x7F, "GK Skills");
	public static final Stat TEAM_WORK = new Stat(StatType.integer, 28, 0, 0x7F, "Team Work");

	public static final Stat[] ABILITY99 = {
			ATTACK,
			DEFENCE,
			BALANCE,
			STAMINA,
			SPEED,
			ACCELERATION,
			RESPONSE,
			AGILITY,
			DRIBBLE_ACC,
			DRIBBLE_SPEED,
			S_PASS_ACC,
			S_PASS_SPEED,
			L_PASS_ACC,
			L_PASS_SPEED,
			SHOT_ACC,
			SHOT_POWER,
			SHOT_TEC,
			FK_ACC,
			CURLING,
			HEADING,
			JUMP,
			TECH,
			AGGRESSION,
			MENTALITY,
			GK_ABILITY,
			TEAM_WORK
	};

	public static final Stat[] ABILITY_SPECIAL = {
			new Stat(StatType.integer, 21, 7, 1, "Dribbling"),
			new Stat(StatType.integer, 21, 15, 1, "Tactical Dribble"),
			new Stat(StatType.integer, 23, 7, 1, "Positioning"),
			new Stat(StatType.integer, 23, 15, 1, "Reaction"),
			new Stat(StatType.integer, 25, 7, 1, "Playmaking"),
			new Stat(StatType.integer, 25, 15, 1, "Passing"),
			new Stat(StatType.integer, 27, 7, 1, "Scoring"),
			new Stat(StatType.integer, 27, 15, 1, "1-1 Scoring"),
			new Stat(StatType.integer, 29, 7, 1, "Post player"),
			new Stat(StatType.integer, 29, 15, 1, "Line Position"),
			new Stat(StatType.integer, 31, 7, 1, "Middle shooting"),
			new Stat(StatType.integer, 31, 15, 1, "Side"),
			new Stat(StatType.integer, 19, 15, 1, "Centre"),
			new Stat(StatType.integer, 19, 7, 1, "Penalties"),
			new Stat(StatType.integer, 35, 0, 1, "1-Touch Pass"),
			new Stat(StatType.integer, 35, 1, 1, "Outside"),
			new Stat(StatType.integer, 35, 2, 1, "Marking"),
			new Stat(StatType.integer, 35, 3, 1, "Sliding"),
			new Stat(StatType.integer, 35, 4, 1, "Covering"),
			new Stat(StatType.integer, 35, 5, 1, "D-Line Control"),
			new Stat(StatType.integer, 35, 6, 1, "Penalty GK"),
			new Stat(StatType.integer, 35, 7, 1, "1-On-1 GK"),
			new Stat(StatType.integer, 37, 7, 1, "Long Throw")
	};

	/*
	public static final Stat STAT_X = new Stat(StatType.integer, 27, 5, 0x7F, "StatX");
	public static final Stat GK_KICK = new Stat(StatType.integer, 33, 7, 1, "GK Kick");
	public static final Stat BFF = new Stat(StatType.integer, 29, 7, 1, "B F Feint");
	public static final Stat GROWTH = new Stat(StatType.positiveInt, 6, 2, 0x03, "Growth");
	*/

	//endregion

	//region String Constants

	public static final String[] NATION = {
			"Austria",
			"Belgium",
			"Bulgaria",
			"Croatia",
			"Czech Republic",
			"Denmark",
			"England",
			"Finland",
			"France",
			"Germany",
			"Greece",
			"Hungary",
			"Ireland",
			"Israel",
			"Italy",
			"Netherlands",
			"Northern Ireland",
			"Norway",
			"Poland",
			"Portugal",
			"Romania",
			"Russia",
			"Scotland",
			"Serbia",
			"Slovakia",
			"Slovenia",
			"Spain",
			"Sweden",
			"Switzerland",
			"Turkey",
			"Ukraine",
			"Wales",
			"Algeria",
			"Cameroon",
			"Cote d'Ivoire",
			"Egypt",
			"Ghana",
			"Nigeria",
			"South Africa",
			"Zambia",
			"Costa Rica",
			"Honduras",
			"Mexico",
			"USA",
			"Argentina",
			"Brazil",
			"Chile",
			"Colombia",
			"Ecuador",
			"Paraguay",
			"Peru",
			"Uruguay",
			"Australia",
			"China",
			"Japan",
			"North Korea",
			"Saudi Arabia",
			"South Korea",
			"United Arab Emirates",
			"New Zealand",
			"Kosovo",
			"Angola",
			"Benin",
			"Burundi",
			"Cape Verde",
			"Central African Republic",
			"Comoros",
			"Congo",
			"DR Congo",
			"Equatorial Guinea",
			"Gabon",
			"Guinea-Bissau",
			"Kenya",
			"Liberia",
			"Libya",
			"Madagascar",
			"Mauritania",
			"Mozambique",
			"Niger",
			"Gambia",
			"Rwanda",
			"Sierra Leone",
			"Togo",
			"Zimbabwe",
			"Antigua & Barbuda",
			"Aruba",
			"Barbados",
			"Canada",
			"Curaçao",
			"Dominican Republic",
			"Grenada",
			"Guadeloupe",
			"Guatemala",
			"Haiti",
			"Martinique",
			"Trinidad e Tobago",
			"Bahrain",
			"Philippines",
			"Syria",
			"New Caledonia",

			"[  -  ]",// Black * NOTE: Nation IDs [100-101] must not be used
			"[     ]",// White
			"[ M ]",// Master-League country

			"Albania",
			"Andorra",
			"Armenia",
			"Azerbaijan",
			"Belarus",
			"Bosnia-Herzegovina",
			"Cyprus",
			"Estonia",
			"Faroe Islands",
			"Georgia",
			"Iceland",
			"Kazakhstan",
			"Latvia",
			"Liechtenstein",
			"Lithuania",
			"Luxembourg",
			"Macedonia",
			"Malta",
			"Moldova",
			"Montenegro",
			"San Marino",
			"Burkina Faso",
			"Guinea",
			"Mali",
			"Morocco",
			"Senegal",
			"Tunisia",
			"Jamaica",
			"Panama",
			"Bolivia",
			"Venezuela",
			"Iran",
			"Iraq",
			"Jordan",
			"Kuwait",
			"Lebanon",
			"Oman",
			"Qatar",
			"Thailand",
			"Uzbekistan"
	};

	static final String[] MOD_FOOT = {"R", "L"};
	public static final String[] MOD_INJURY = {"C", "B", "A"};
	public static final String[] MOD_1_8 = {"1", "2", "3", "4", "5", "6", "7", "8"};
	public static final String[] MOD_FOOT_SIDE = {
			"R foot / R side", "R foot / L side", "R foot / B side",
			"L foot / L side", "L foot / R side", "L foot / B side"
	};
	public static final String[] MOD_FK = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
	public static final String[] MOD_PK = {"1", "2", "3", "4", "5"};
	public static final String[] MOD_1_4 = {"1", "2", "3", "4"};

	public static final int MAX_STAT99 = 99;

	//endregion

	public static int getValue(OptionFile of, int player, Stat stat) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		if (null == stat) {
			throw new NullArgumentException("stat");
		}

		int ofs = stat.getOffset(player);
		int val = Bits.toInt16(of.getData(), ofs - 1);
		val = (val >>> stat.getShift()) & stat.getMask();

		/*if (stat.getType() == StatType.nationId) {
			if (val == 100) {
				val = NATION.length - 1;
			} else if (val >= 103) {
				val = val - 3;
			}
		}*/

		return val;
	}

	public static void setValue(OptionFile of, int player, Stat stat, int value) {
		if (null == of) {
			throw new NullArgumentException("of");
		}
		if (null == stat) {
			throw new NullArgumentException("stat");
		}

		int ofs = stat.getOffset(player);

		/*if (stat.getType() == StatType.nationId) {
			if (value == NATION.length - 1) {
				value = 100;
			} else if (value >= 100) {
				value = value + 3;
			}
		}*/

		int old = Bits.toInt16(of.getData(), ofs - 1);
		old &= stat.getUnmask();

		value = (value & stat.getMask()) << stat.getShift();
		value = old | value;

		Bits.toBytes((short) value, of.getData(), ofs - 1);
	}

	/**
	 * @throws NumberFormatException
	 */
	public static void setValue(OptionFile of, int player, Stat stat, String value) {
		if (null == stat) {
			throw new NullArgumentException("stat");
		}

		int val;
		if (stat.getType() == StatType.nationId) {
			val = Arrays.indexOfIgnoreCase(NATION, value);
		} else if (stat.getType() == StatType.footId) {
			val = Arrays.indexOfIgnoreCase(MOD_FOOT, value);
		} else if (stat.getType() == StatType.injuryId) {
			val = Arrays.indexOfIgnoreCase(MOD_INJURY, value);
		} else {
			val = Integer.parseInt(value) - stat.minValue();
		}

		setValue(of, player, stat, val);
	}

	/**
	 * @throws IndexOutOfBoundsException
	 */
	public static String getString(OptionFile of, int player, Stat stat) {
		int val = getValue(of, player, stat);

		String result;
		if (stat.getType() == StatType.nationId) {
			result = NATION[val];
		} else if (stat.getType() == StatType.footId) {
			result = MOD_FOOT[val];
		} else if (stat.getType() == StatType.injuryId) {
			result = MOD_INJURY[val];
		} else {
			result = Integer.toString(val + stat.minValue());
		}

		return result;
	}

	public static int regPosToRole(int regPos) {
		return (regPos > 1) ? regPos - 1 : regPos;
	}

	public static int roleToRegPos(int regRole) {
		return (regRole > 0) ? regRole + 1 : regRole;
	}

}
