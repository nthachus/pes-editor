package editor.data;

import editor.lang.NullArgumentException;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public final class StatsTest extends BaseTest {
	private static final int FOR_PLAYER = 1;

	@Test(expected = NullArgumentException.class)
	public void testGetValueWithNullStat() {
		Stats.getValue(new OptionFile(), FOR_PLAYER, null);
	}

	@Test(expected = NullArgumentException.class)
	public void testGetValueWithNullOF() {
		Stats.getValue(null, FOR_PLAYER, Stats.AGE);
	}

	@Test(expected = NullArgumentException.class)
	public void testSetValueWithNullStat() {
		Stats.setValue(new OptionFile(), FOR_PLAYER, null, 1);
	}

	@Test(expected = NullArgumentException.class)
	public void testSetValueWithNullOF() {
		Stats.setValue(null, FOR_PLAYER, Stats.AGE, 1);
	}

	@Test
	public void testDuplicatedStats() throws IllegalAccessException {
		List<Stat> fields = readStaticFields(Stats.class, Stat.class, true, false);
		fields.addAll(Arrays.asList(Stats.ABILITY_SPECIAL));

		Collections.sort(fields);
		log.debug("Sorted Stats: {}", fields);

		for (int i = 1; i < fields.size(); i++) {
			Assert.assertNotEquals(fields.get(i - 1), fields.get(i));
		}
	}

	@Test
	public void testGetAndSetValue() throws IllegalAccessException {
		OptionFile of = loadLatestOF();

		List<Stat> fields = readStaticFields(Stats.class, Stat.class, true, false);
		fields.addAll(Arrays.asList(Stats.ABILITY_SPECIAL));
		for (Stat st : fields) {
			//log.debug("Process Stat: {}", st);

			int old = Stats.getValue(of, FOR_PLAYER, st);
			int val = (old > 0) ? old - 1 : old + 1;

			Stats.setValue(of, FOR_PLAYER, st, val);
			int v = Stats.getValue(of, FOR_PLAYER, st);
			Assert.assertNotEquals(old, v);
			Assert.assertEquals(val, v);
		}
	}

	@Test
	public void testPlayerStats() throws IllegalAccessException {
		OptionFile of = loadOriginalOF();

		testStatsForPlayer(of, 1, player1);
		testStatsForPlayer(of, 1506, player1506);
	}

	private void testStatsForPlayer(OptionFile of, int pid, String[][] playerStats) throws IllegalAccessException {
		Map<String, String> stats = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
		for (String[] ps : playerStats) {
			stats.put(ps[0], ps[1]);
		}

		Assert.assertEquals(stats.get("Name"), Player.getName(of, pid));
		stats.remove("Name");

		List<Stat> fields = readStaticFields(Stats.class, Stat.class, true, false);
		String val;
		for (Stat st : fields) {
			if (!stats.containsKey(st.getName())) {

				if (!st.getName().matches(".*(?i:Edited|Name|Style|Kick)")) {
					log.warn("Stat not found: {}", st);
				}
			} else {

				if (Stats.REG_POS.getName().equals(st.getName())) {
					val = Stats.ROLES[Stats.regPosToRole(Stats.getValue(of, pid, st))].getName();
				} else {
					val = Stats.getString(of, pid, st);
				}

				Assert.assertEquals("Incorrect value for stat: " + st, stats.get(st.getName()), val);
				stats.remove(st.getName());
			}
		}

		for (Stat st : Stats.ABILITY_SPECIAL) {
			val = Stats.getString(of, pid, st);
			if (!stats.containsKey(st.getName())) {
				Assert.assertEquals("Incorrect value for stat: " + st, "0", val);
			} else {
				Assert.assertEquals("Incorrect value for stat: " + st, stats.get(st.getName()), val);
				stats.remove(st.getName());
			}
		}

		if (stats.size() > 0) {
			log.debug("Un-verified stats: {}", stats);
		}
		Assert.assertEquals(0, stats.size());
	}

	private static final String[][] player1 = {
			{"Name", "ALMER"},
			{"Nationality", "Austria"},
			{"Height", "194"},
			{"Weight", "91"},
			{"Age", "28"},
			{"Foot", "R"},
			{"Registered Position", "GK"},
			{"GK", "1"},
			{"SW", "0"},
			{"CB", "0"},
			{"SB", "0"},
			{"DMF", "0"},
			{"CMF", "0"},
			{"SMF", "0"},
			{"AMF", "0"},
			{"WF", "0"},
			{"SS", "0"},
			{"CF", "0"},
			{"Attack", "30"},
			{"Defense", "76"},// Defence
			{"Heading", "45"},
			{"Dribble accuracy", "55"},
			{"S-pass accuracy", "58"},
			{"S-pass speed", "57"},
			{"L-pass accuracy", "59"},
			{"L-pass speed", "66"},
			{"Shot accuracy", "45"},
			{"FK Accuracy", "44"},// Place kicking
			{"Swerve", "43"},
			{"Technique", "50"},// Ball control
			{"W-foot accuracy", "4"},
			{"W-foot Frequency", "4"},
			{"GK skills", "77"},
			{"Response", "80"},
			{"Agility", "62"},// Explosive power
			{"Dribble speed", "53"},
			{"Speed", "64"},
			{"Balance", "84"},
			{"Stamina", "56"},
			{"Shot power", "80"},
			{"Jump", "73"},
			{"Injury Tolerance", "A"},// 1-3 [C-A]
			{"Condition", "4"},// Form
			{"Mentality", "72"},
			{"Team work", "69"},
	};

	private static final String[][] player1506 = {
			{"Name", "BAQUISTATA"},
			{"Nationality", "Argentina"},
			{"Height", "185"},
			{"Weight", "73"},
			{"Age", "30"},
			{"Foot", "R"},
			{"Registered Position", "CF"},
			{"GK", "0"},
			{"SW", "0"},
			{"CB", "0"},
			{"SB", "0"},
			{"DMF", "0"},
			{"CMF", "0"},
			{"SMF", "0"},
			{"AMF", "0"},
			{"WF", "0"},
			{"SS", "0"},
			{"CF", "1"},
			{"Attack", "97"},
			{"Defense", "24"},
			{"Heading", "91"},// Header accuracy
			{"Dribble accuracy", "75"},
			{"S-pass accuracy", "72"},
			{"S-pass speed", "76"},
			{"L-pass accuracy", "68"},
			{"L-pass speed", "78"},
			{"Shot accuracy", "93"},
			{"FK Accuracy", "85"},
			{"Swerve", "86"},
			{"Technique", "82"},
			{"W-foot accuracy", "5"},
			{"W-foot Frequency", "4"},
			{"GK skills", "50"},
			{"Response", "95"},
			{"Agility", "75"},// Explosive power
			{"Dribble speed", "78"},
			{"Speed", "82"},
			{"Balance", "88"},
			{"Stamina", "77"},
			{"Shot power", "97"},
			{"Jump", "85"},
			{"Injury Tolerance", "A"},// 1-3 [C-A]
			{"Condition", "7"},// Form
			{"Mentality", "87"},// Tenacity
			{"Team work", "71"},

			{"Scoring", "1"},// Goal Poacher
			{"1-Touch Pass", "1"},// One-touch play
			{"Positioning", "1"},
			{"Line Position", "1"},
			{"Middle shooting", "1"},
			{"Centre", "1"},
	};

}
