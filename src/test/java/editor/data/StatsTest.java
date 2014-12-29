package editor.data;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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

		Field[] fields = Stats.class.getDeclaredFields();
		Stat st;
		for (Field f : fields) {
			if (Modifier.isStatic(f.getModifiers())
					&& Modifier.isFinal(f.getModifiers()) && f.getType() == Stat.class) {

				st = (Stat) f.get(null);
				//System.out.println("Process Stat: " + st);

				int old = Stats.getValue(of, FOR_PLAYER, st);
				int val = (old > 0) ? old - 1 : old + 1;

				Stats.setValue(of, FOR_PLAYER, st, val);
				int v = Stats.getValue(of, FOR_PLAYER, st);
				Assert.assertNotEquals(old, v);
				Assert.assertEquals(val, v);
			}
		}
	}

}