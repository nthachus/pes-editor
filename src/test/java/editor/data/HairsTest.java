package editor.data;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author thachnn on 07/11/2015.
 */
public final class HairsTest {
	@Test
	public void testBald() {
		Hairs test = Hairs.Bald;
		Assert.assertEquals(test.start(), 0x01);

		Assert.assertEquals(Integer.valueOf(1), test.getShape(1));
		Assert.assertEquals(Integer.valueOf(4), test.getShape(4));

		Assert.assertEquals(test.getFront(1), null);
		Assert.assertEquals(test.getBandana(1), null);

		Assert.assertEquals("Bald / Shape1", Hairs.toString(1));
		Assert.assertEquals("Bald / Shape4", Hairs.toString(4));
	}

	@Test
	public void testCrewCut() {
		Hairs test = Hairs.CrewCut;
		Assert.assertEquals(test.start(), 0x05);

		Assert.assertEquals(Integer.valueOf(1), test.getShape(5));
		Assert.assertEquals(Integer.valueOf(1), test.getFront(5));
		Assert.assertEquals(test.getVolume(5), null);
		Assert.assertEquals(Integer.valueOf(1), test.getDarkness(5));

		Assert.assertEquals(Integer.valueOf(1), test.getShape(0x18));
		Assert.assertEquals(Integer.valueOf(5), test.getFront(0x18));
		Assert.assertEquals(Integer.valueOf(4), test.getDarkness(0x18));

		Assert.assertEquals(Integer.valueOf(4), test.getShape(0x54));
		Assert.assertEquals(Integer.valueOf(5), test.getFront(0x54));
		Assert.assertEquals(Integer.valueOf(4), test.getDarkness(0x54));

		Assert.assertEquals(Integer.valueOf(2), test.getShape(0x19));
		Assert.assertEquals(Integer.valueOf(1), test.getFront(0x19));
		Assert.assertEquals(Integer.valueOf(1), test.getDarkness(0x19));

		Assert.assertEquals(Integer.valueOf(2), test.getShape(30));// 1E
		Assert.assertEquals(Integer.valueOf(2), test.getFront(30));
		Assert.assertEquals(Integer.valueOf(2), test.getDarkness(30));

		Assert.assertEquals("CrewCut / Shape4 / Front5 / Darkness4", Hairs.toString(0x54));
		Assert.assertEquals("CrewCut / Shape2 / Front2 / Darkness2", Hairs.toString(30));
	}

	@Test
	public void testShort1() {
		Hairs test = Hairs.Short1;
		Assert.assertEquals(test.start(), 0x55);

		Assert.assertEquals(Integer.valueOf(1), test.getShape(0x55));
		Assert.assertEquals(Integer.valueOf(1), test.getFront(0x55));
		Assert.assertEquals(test.getVolume(0x55), null);

		Assert.assertEquals(Integer.valueOf(1), test.getShape(0x5A));
		Assert.assertEquals(Integer.valueOf(6), test.getFront(0x5A));

		Assert.assertEquals(Integer.valueOf(4), test.getShape(0x67));
		Assert.assertEquals(Integer.valueOf(1), test.getFront(0x67));

		Assert.assertEquals(Integer.valueOf(4), test.getShape(0x6C));
		Assert.assertEquals(Integer.valueOf(6), test.getFront(0x6C));

		Assert.assertEquals("Short1 / Shape1 / Front6", Hairs.toString(0x5A));
		Assert.assertEquals("Short1 / Shape4 / Front6", Hairs.toString(0x6C));
	}

	@Test
	public void testShort2() {
		Hairs test = Hairs.Short2;
		Assert.assertEquals(test.start(), 0x6D);

		Assert.assertEquals(Integer.valueOf(1), test.getShape(0x6D));
		Assert.assertEquals(Integer.valueOf(1), test.getFront(0x6D));
		Assert.assertEquals(test.getVolume(0x6D), null);

		Assert.assertEquals(Integer.valueOf(1), test.getShape(0x75));
		Assert.assertEquals(Integer.valueOf(9), test.getFront(0x75));

		Assert.assertEquals(Integer.valueOf(2), test.getShape(0x77));
		Assert.assertEquals(Integer.valueOf(1), test.getFront(0x77));

		Assert.assertEquals(Integer.valueOf(3), test.getShape(0x8A));
		Assert.assertEquals(Integer.valueOf(10), test.getFront(0x8A));

		Assert.assertEquals(Integer.valueOf(4), test.getShape(0x8F));
		Assert.assertEquals(Integer.valueOf(5), test.getFront(0x8F));

		Assert.assertEquals(Integer.valueOf(6), test.getShape(0x95));
		Assert.assertEquals(Integer.valueOf(1), test.getFront(0x95));

		Assert.assertEquals("Short2 / Shape3 / Front10", Hairs.toString(0x8A));
		Assert.assertEquals("Short2 / Shape4 / Front5", Hairs.toString(0x8F));
		Assert.assertEquals("Short2 / Shape6 / Front1", Hairs.toString(0x95));
	}

	@Test
	public void testStraight1() {
		Hairs test = Hairs.Straight1;
		Assert.assertEquals(test.start(), 0x9A);

		Assert.assertEquals(Integer.valueOf(1), test.getShape(0x9A));
		Assert.assertEquals(Integer.valueOf(1), test.getFront(0x9A));
		Assert.assertEquals(Integer.valueOf(1), test.getVolume(0x9A));
		Assert.assertEquals(test.getDarkness(0x9A), null);
		Assert.assertEquals(Integer.valueOf(0), test.getBandana(0x9A));

		Assert.assertEquals(Integer.valueOf(1), test.getShape(0x9C));
		Assert.assertEquals(Integer.valueOf(1), test.getFront(0x9C));
		Assert.assertEquals(Integer.valueOf(1), test.getVolume(0x9C));
		Assert.assertEquals(Integer.valueOf(2), test.getBandana(0x9C));

		Assert.assertEquals(Integer.valueOf(1), test.getShape(0xA1));
		Assert.assertEquals(Integer.valueOf(1), test.getFront(0xA1));
		Assert.assertEquals(Integer.valueOf(3), test.getVolume(0xA1));
		Assert.assertEquals(Integer.valueOf(1), test.getBandana(0xA1));

		Assert.assertEquals(Integer.valueOf(1), test.getShape(0xEA));
		Assert.assertEquals(Integer.valueOf(9), test.getFront(0xEA));
		Assert.assertEquals(Integer.valueOf(3), test.getVolume(0xEA));
		Assert.assertEquals(Integer.valueOf(2), test.getBandana(0xEA));

		Assert.assertEquals(Integer.valueOf(1), test.getShape(0xEC));
		Assert.assertEquals(Integer.valueOf(10), test.getFront(0xEC));
		Assert.assertEquals(Integer.valueOf(2), test.getVolume(0xEC));
		Assert.assertEquals(test.getBandana(0xEC), null);

		Assert.assertEquals(Integer.valueOf(1), test.getShape(0xFF));
		Assert.assertEquals(Integer.valueOf(16), test.getFront(0xFF));
		Assert.assertEquals(Integer.valueOf(3), test.getVolume(0xFF));
		Assert.assertEquals(test.getBandana(0xFF), null);

		Assert.assertEquals(Integer.valueOf(2), test.getShape(0x100));
		Assert.assertEquals(Integer.valueOf(1), test.getFront(0x100));
		Assert.assertEquals(Integer.valueOf(1), test.getVolume(0x100));
		Assert.assertEquals(Integer.valueOf(0), test.getBandana(0x100));

		Assert.assertEquals(Integer.valueOf(4), test.getShape(0x231));
		Assert.assertEquals(Integer.valueOf(16), test.getFront(0x231));
		Assert.assertEquals(Integer.valueOf(3), test.getVolume(0x231));
		Assert.assertEquals(test.getBandana(0x231), null);

		Assert.assertEquals("Straight1 / Shape1 / Front16 / Volume3", Hairs.toString(0xFF));
		Assert.assertEquals("Straight1 / Shape2 / Front1 / Volume1 / Bandana0", Hairs.toString(0x100));
	}

	@Test
	public void testStraight2() {
		Hairs test = Hairs.Straight2;
		Assert.assertEquals(test.start(), 0x232);

		Assert.assertEquals(Integer.valueOf(1), test.getShape(0x232));
		Assert.assertEquals(Integer.valueOf(1), test.getFront(0x232));
		Assert.assertEquals(Integer.valueOf(1), test.getVolume(0x232));
		Assert.assertEquals(test.getDarkness(0x232), null);
		Assert.assertEquals(Integer.valueOf(0), test.getBandana(0x232));

		Assert.assertEquals(Integer.valueOf(1), test.getShape(0x243));
		Assert.assertEquals(Integer.valueOf(2), test.getFront(0x243));
		Assert.assertEquals(Integer.valueOf(3), test.getVolume(0x243));
		Assert.assertEquals(Integer.valueOf(2), test.getBandana(0x243));

		Assert.assertEquals(Integer.valueOf(1), test.getShape(0x252));
		Assert.assertEquals(Integer.valueOf(7), test.getFront(0x252));
		Assert.assertEquals(Integer.valueOf(3), test.getVolume(0x252));
		Assert.assertEquals(test.getBandana(0x252), null);

		Assert.assertEquals(Integer.valueOf(3), test.getShape(0x294));
		Assert.assertEquals(Integer.valueOf(7), test.getFront(0x294));
		Assert.assertEquals(Integer.valueOf(3), test.getVolume(0x294));
		Assert.assertEquals(test.getBandana(0x294), null);

		Assert.assertEquals("Straight2 / Shape1 / Front2 / Volume3 / Bandana2", Hairs.toString(0x243));
		Assert.assertEquals("Straight2 / Shape3 / Front7 / Volume3", Hairs.toString(0x294));
	}

	@Test
	public void testCurly1() {
		Hairs test = Hairs.Curly1;
		Assert.assertEquals(test.start(), 0x295);

		Assert.assertEquals(Integer.valueOf(1), test.getShape(0x295));
		Assert.assertEquals(Integer.valueOf(1), test.getFront(0x295));
		Assert.assertEquals(Integer.valueOf(1), test.getVolume(0x295));
		Assert.assertEquals(test.getDarkness(0x295), null);
		Assert.assertEquals(Integer.valueOf(0), test.getBandana(0x295));

		Assert.assertEquals(Integer.valueOf(1), test.getShape(0x2C1));
		Assert.assertEquals(Integer.valueOf(5), test.getFront(0x2C1));
		Assert.assertEquals(Integer.valueOf(3), test.getVolume(0x2C1));
		Assert.assertEquals(Integer.valueOf(2), test.getBandana(0x2C1));

		Assert.assertEquals(Integer.valueOf(1), test.getShape(0x2C7));
		Assert.assertEquals(Integer.valueOf(7), test.getFront(0x2C7));
		Assert.assertEquals(Integer.valueOf(3), test.getVolume(0x2C7));
		Assert.assertEquals(test.getBandana(0x2C7), null);

		Assert.assertEquals(Integer.valueOf(4), test.getShape(0x360));
		Assert.assertEquals(Integer.valueOf(7), test.getFront(0x360));
		Assert.assertEquals(Integer.valueOf(3), test.getVolume(0x360));
		Assert.assertEquals(test.getBandana(0x360), null);

		Assert.assertEquals("Curly1 / Shape1 / Front5 / Volume3 / Bandana2", Hairs.toString(0x2C1));
		Assert.assertEquals("Curly1 / Shape4 / Front7 / Volume3", Hairs.toString(0x360));
	}

	@Test
	public void testCurly2() {
		Hairs test = Hairs.Curly2;
		Assert.assertEquals(test.start(), 0x361);

		Assert.assertEquals(Integer.valueOf(1), test.getShape(0x361));
		Assert.assertEquals(Integer.valueOf(1), test.getFront(0x361));
		Assert.assertEquals(Integer.valueOf(1), test.getVolume(0x361));
		Assert.assertEquals(test.getDarkness(0x361), null);
		Assert.assertEquals(test.getBandana(0x361), null);

		Assert.assertEquals(Integer.valueOf(2), test.getShape(0x376));
		Assert.assertEquals(Integer.valueOf(5), test.getFront(0x376));
		Assert.assertEquals(Integer.valueOf(2), test.getVolume(0x376));

		Assert.assertEquals(Integer.valueOf(4), test.getShape(0x390));
		Assert.assertEquals(Integer.valueOf(6), test.getFront(0x390));
		Assert.assertEquals(Integer.valueOf(2), test.getVolume(0x390));

		Assert.assertEquals("Curly2 / Shape2 / Front5 / Volume2", Hairs.toString(0x376));
		Assert.assertEquals("Curly2 / Shape4 / Front6 / Volume2", Hairs.toString(0x390));
	}

	@Test
	public void testPonytail1() {
		Hairs test = Hairs.Ponytail1;
		Assert.assertEquals(test.start(), 0x391);

		Assert.assertEquals(Integer.valueOf(1), test.getShape(0x391));
		Assert.assertEquals(Integer.valueOf(1), test.getFront(0x391));
		Assert.assertEquals(Integer.valueOf(1), test.getVolume(0x391));
		Assert.assertEquals(test.getDarkness(0x391), null);
		Assert.assertEquals(test.getBandana(0x391), null);

		Assert.assertEquals(Integer.valueOf(2), test.getShape(0x3A4));
		Assert.assertEquals(Integer.valueOf(3), test.getFront(0x3A4));
		Assert.assertEquals(Integer.valueOf(2), test.getVolume(0x3A4));

		Assert.assertEquals(Integer.valueOf(3), test.getShape(0x3B4));
		Assert.assertEquals(Integer.valueOf(4), test.getFront(0x3B4));
		Assert.assertEquals(Integer.valueOf(3), test.getVolume(0x3B4));

		Assert.assertEquals("Ponytail1 / Shape2 / Front3 / Volume2", Hairs.toString(0x3A4));
		Assert.assertEquals("Ponytail1 / Shape3 / Front4 / Volume3", Hairs.toString(0x3B4));
	}

	@Test
	public void testPonytail2() {
		Hairs test = Hairs.Ponytail2;
		Assert.assertEquals(test.start(), 0x3B5);

		Assert.assertEquals(Integer.valueOf(1), test.getShape(0x3B5));
		Assert.assertEquals(Integer.valueOf(1), test.getFront(0x3B5));
		Assert.assertEquals(Integer.valueOf(1), test.getVolume(0x3B5));
		Assert.assertEquals(test.getDarkness(0x3B5), null);
		Assert.assertEquals(test.getBandana(0x3B5), null);

		Assert.assertEquals(Integer.valueOf(2), test.getShape(0x3C5));
		Assert.assertEquals(Integer.valueOf(2), test.getFront(0x3C5));
		Assert.assertEquals(Integer.valueOf(2), test.getVolume(0x3C5));

		Assert.assertEquals(Integer.valueOf(3), test.getShape(0x3D5));
		Assert.assertEquals(Integer.valueOf(3), test.getFront(0x3D5));
		Assert.assertEquals(Integer.valueOf(3), test.getVolume(0x3D5));

		Assert.assertEquals("Ponytail2 / Shape2 / Front2 / Volume2", Hairs.toString(0x3C5));
		Assert.assertEquals("Ponytail2 / Shape3 / Front3 / Volume3", Hairs.toString(0x3D5));
	}

	@Test
	public void testDreadlocks() {
		Hairs test = Hairs.Dreadlocks;
		Assert.assertEquals(test.start(), 0x3D9);

		Assert.assertEquals(Integer.valueOf(1), test.getShape(0x3D9));
		Assert.assertEquals(Integer.valueOf(1), test.getFront(0x3D9));
		Assert.assertEquals(Integer.valueOf(1), test.getVolume(0x3D9));
		Assert.assertEquals(test.getDarkness(0x3D9), null);
		Assert.assertEquals(test.getBandana(0x3D9), null);

		Assert.assertEquals(Integer.valueOf(2), test.getShape(0x3E6));
		Assert.assertEquals(Integer.valueOf(3), test.getFront(0x3E6));
		Assert.assertEquals(Integer.valueOf(2), test.getVolume(0x3E6));

		Assert.assertEquals(Integer.valueOf(2), test.getShape(0x3E7));
		Assert.assertEquals(Integer.valueOf(4), test.getFront(0x3E7));
		Assert.assertEquals(Integer.valueOf(1), test.getVolume(0x3E7));

		Assert.assertEquals(Integer.valueOf(3), test.getShape(0x3F0));
		Assert.assertEquals(Integer.valueOf(4), test.getFront(0x3F0));
		Assert.assertEquals(Integer.valueOf(2), test.getVolume(0x3F0));

		Assert.assertEquals("Dreadlocks / Shape2 / Front3 / Volume2", Hairs.toString(0x3E6));
		Assert.assertEquals("Dreadlocks / Shape3 / Front4 / Volume2", Hairs.toString(0x3F0));
	}

	@Test
	public void testHairband() {
		Hairs test = Hairs.Hairband;
		Assert.assertEquals(test.start(), 0x3F1);

		Assert.assertEquals(Integer.valueOf(2), test.getShape(0x3F7));
		Assert.assertEquals(Integer.valueOf(1), test.getFront(0x3F7));
		Assert.assertEquals(test.getVolume(0x3F7), null);
		Assert.assertEquals(test.getBandana(0x3F7), null);

		Assert.assertEquals(Integer.valueOf(3), test.getShape(0x402));
		Assert.assertEquals(Integer.valueOf(6), test.getFront(0x402));

		Assert.assertEquals("Hairband / Shape2 / Front1", Hairs.toString(0x3F7));
		Assert.assertEquals("Hairband / Shape3 / Front6", Hairs.toString(0x402));
	}

	@Test
	public void testSpecialHairstyles1() {
		Hairs test = Hairs.SpecialHairstyles1;
		Assert.assertEquals(test.start(), 0x403);

		Assert.assertEquals(Integer.valueOf(3), test.getShape(0x405));
		Assert.assertEquals(test.getFront(0x405), null);
		Assert.assertEquals(test.getVolume(0x405), null);
		Assert.assertEquals(test.getBandana(0x405), null);

		Assert.assertEquals(Integer.valueOf(17), test.getShape(0x413));
		Assert.assertEquals(test.getFront(0x413), null);

		Assert.assertEquals("SpecialHairstyles1 / Shape3", Hairs.toString(0x405));
		Assert.assertEquals("SpecialHairstyles1 / Shape17", Hairs.toString(0x413));
	}
}
