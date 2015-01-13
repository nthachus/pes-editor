package editor.data;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public final class StatsTest extends BaseTest {
	private static final int FOR_PLAYER = 1;

	@Test(expected = NullPointerException.class)
	public void testGetValueWithNullStat() throws Exception {
		Stats.getValue(new OptionFile(), FOR_PLAYER, null);
	}

	@Test(expected = NullPointerException.class)
	public void testGetValueWithNullOF() throws Exception {
		Stats.getValue(null, FOR_PLAYER, Stats.AGE);
	}

	@Test(expected = NullPointerException.class)
	public void testSetValueWithNullStat() throws Exception {
		Stats.setValue(new OptionFile(), FOR_PLAYER, null, 1);
	}

	@Test(expected = NullPointerException.class)
	public void testSetValueWithNullOF() throws Exception {
		Stats.setValue(null, FOR_PLAYER, Stats.AGE, 1);
	}

	@Test
	public void testGetAndSetValue() throws Exception {
		OptionFile of = loadLatestOF();

		List<Stat> fields = readStaticFields(Stats.class, Stat.class, true, false);
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

}
