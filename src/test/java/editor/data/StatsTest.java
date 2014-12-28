package editor.data;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class StatsTest extends BaseTest {
	@Test(expected = NullPointerException.class)
	public void testGetValueWithNullStat() throws Exception {
		Stats.getValue(new OptionFile(), 0, null);
	}

	@Test(expected = NullPointerException.class)
	public void testGetValueWithNullOF() throws Exception {
		Stats.getValue(null, 0, Stats.AGE);
	}

	@Test(expected = NullPointerException.class)
	public void testSetValueWithNullStat() throws Exception {
		Stats.setValue(new OptionFile(), 0, null, 1);
	}

	@Test(expected = NullPointerException.class)
	public void testSetValueWithNullOF() throws Exception {
		Stats.setValue(null, 0, Stats.AGE, 1);
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

				int old = Stats.getValue(of, 0, st);
				int val = (old > 0) ? old - 1 : old + 1;

				Stats.setValue(of, 0, st, val);
				int v = Stats.getValue(of, 0, st);
				Assert.assertNotEquals(old, v);
				Assert.assertEquals(val, v);
			}
		}
	}

}
