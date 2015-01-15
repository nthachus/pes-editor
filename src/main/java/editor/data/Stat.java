package editor.data;

import java.io.Serializable;

public class Stat implements Serializable {
	private static final long serialVersionUID = 7447946182427124435L;

	private final StatType type;
	private final int offset;
	private final int shift;
	private final int mask;
	private final String name;

	public Stat(StatType type, int offset, int shift, int mask, String name) {
		if (null == type) throw new NullPointerException("type");
		if (null == name) throw new NullPointerException("name");

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
		return 0xFFFF & (~(this.getMask() << this.getShift()));
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

}
