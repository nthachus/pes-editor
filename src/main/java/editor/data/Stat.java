package editor.data;

import editor.util.Bits;

import java.io.Serializable;

public class Stat implements Serializable, Comparable<Stat> {
	private static final long serialVersionUID = 7447946182427124435L;

	private final StatType type;
	private final int offset;
	private final int shift;
	private final int mask;
	private final String name;

	public Stat(StatType type, int offset, int shift, int mask, String name) {
		if (null == type) throw new NullPointerException("type");
		if (null == name) throw new NullPointerException("name");
		if (offset < 0) throw new IllegalArgumentException("offset " + offset);
		if (shift < 0 || shift >= Short.SIZE) throw new IllegalArgumentException("shift " + shift);
		if (mask <= 0 || mask > 0xFFFF) throw new IllegalArgumentException("mask " + mask);

		this.type = type;
		this.offset = offset;
		this.shift = shift;
		this.mask = mask;
		this.name = name;
	}

	public StatType getType() {
		return type;
	}

	public int getOffset() {
		return offset;
	}

	public int getShift() {
		return shift;
	}

	public int getMask() {
		return mask;
	}

	public String getName() {
		return name;
	}

	public int getUnmask() {
		return 0xFFFF & (~(getMask() << getShift()));
	}

	public int getOffset(int player) {
		int ofs = Player.getOffset(player);
		return ofs + 48 + this.getOffset();
	}

	@Override
	public String toString() {
		return name;
	}

	public int minValue() {
		if (type == StatType.positiveInt)
			return 1;
		if (type == StatType.age15)
			return 15;
		if (type == StatType.height148)
			return 148;
		return 0;
	}

	public int maxValue() {
		return minValue() + getMask();
	}

	private int getBitOffset() {
		return getOffset() * Byte.SIZE + getShift();
	}

	private int getBitLength() {
		return Bits.bitLength(getMask());
	}

	@Override
	public boolean equals(Object o) {
		return (this == o
				|| (null != o && getClass() == o.getClass() && compareTo((Stat) o) == 0));
	}

	@Override
	public int hashCode() {
		return (offset << 24) | (shift << 16) | mask;
	}

	@SuppressWarnings("NullableProblems")
	@Override
	public int compareTo(Stat o) {
		if (null == o) return 1;

		int start = getBitOffset();
		int end = start + getBitLength() - 1;
		int startO = o.getBitOffset();
		int endO = startO + o.getBitLength() - 1;

		if (start > endO) return 1;
		if (startO > end) return -1;
		return 0;
	}

}
