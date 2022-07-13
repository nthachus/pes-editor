package editor.data;

/**
 * @author thachnn on 07/11/2015.
 */
public enum Hairs {
	Bald(1) {
		@Override
		public int total() {
			return 4;
		}

		@Override
		public Integer getShape(int hair) {
			return hair - start() + 1;
		}
	},
	/**
	 * Buzz Cut
	 */
	CrewCut(Bald.start() + Bald.total()) {
		@Override
		public int total() {
			return 4 * 5 * 4;
		}

		@Override
		public Integer getShape(int hair) {
			return (hair - start()) / (5 * 4) + 1;
		}

		@Override
		public Integer getFront(int hair) {
			return ((hair - start()) % (5 * 4)) / 4 + 1;
		}

		@Override
		public Integer getDarkness(int hair) {
			return ((hair - start()) % (5 * 4)) % 4 + 1;
		}
	},
	/**
	 * Very Short 1
	 */
	Short1(CrewCut.start() + CrewCut.total()) {
		@Override
		public int total() {
			return 4 * 6;
		}

		@Override
		public Integer getShape(int hair) {
			return (hair - start()) / 6 + 1;
		}

		@Override
		public Integer getFront(int hair) {
			return (hair - start()) % 6 + 1;
		}
	},
	/**
	 * Very Short 2
	 */
	Short2(Short1.start() + Short1.total()) {
		@Override
		public int total() {
			return 3 * 10 + 3 * 5;
		}

		@Override
		public Integer getShape(int hair) {
			int i = hair - start();
			if (i >= 3 * 10) {
				return (i - 3 * 10) / 5 + 3 + 1;
			}
			return i / 10 + 1;
		}

		@Override
		public Integer getFront(int hair) {
			int i = hair - start();
			if (i >= 3 * 10) {
				return (i - 3 * 10) % 5 + 1;
			}
			return i % 10 + 1;
		}
	},
	Straight1(Short2.start() + Short2.total()) {
		@Override
		public int total() {
			return 4 * (9 * 3 * 3 + 7 * 3);
		}

		@Override
		public Integer getShape(int hair) {
			return (hair - start()) / (9 * 3 * 3 + 7 * 3) + 1;
		}

		@Override
		public Integer getFront(int hair) {
			int i = (hair - start()) % (9 * 3 * 3 + 7 * 3);
			if (i >= 9 * 3 * 3) {
				return (i - 9 * 3 * 3) / 3 + 9 + 1;
			}
			return i / (3 * 3) + 1;
		}

		@Override
		public Integer getVolume(int hair) {
			int i = (hair - start()) % (9 * 3 * 3 + 7 * 3);
			if (i >= 9 * 3 * 3) {
				return (i - 9 * 3 * 3) % 3 + 1;
			}
			return (i % (3 * 3)) / 3 + 1;
		}

		@Override
		public Integer getBandana(int hair) {
			int i = (hair - start()) % (9 * 3 * 3 + 7 * 3);
			if (i >= 9 * 3 * 3) {
				return null;
			}
			return (i % (3 * 3)) % 3;
		}
	},
	Straight2(Straight1.start() + Straight1.total()) {
		@Override
		public int total() {
			return 3 * (2 * 3 * 3 + 5 * 3);
		}

		@Override
		public Integer getShape(int hair) {
			return (hair - start()) / (2 * 3 * 3 + 5 * 3) + 1;
		}

		@Override
		public Integer getFront(int hair) {
			int i = (hair - start()) % (2 * 3 * 3 + 5 * 3);
			if (i >= 2 * 3 * 3) {
				return (i - 2 * 3 * 3) / 3 + 2 + 1;
			}
			return i / (3 * 3) + 1;
		}

		@Override
		public Integer getVolume(int hair) {
			int i = (hair - start()) % (2 * 3 * 3 + 5 * 3);
			if (i >= 2 * 3 * 3) {
				return (i - 2 * 3 * 3) % 3 + 1;
			}
			return (i % (3 * 3)) / 3 + 1;
		}

		@Override
		public Integer getBandana(int hair) {
			int i = (hair - start()) % (2 * 3 * 3 + 5 * 3);
			if (i >= 2 * 3 * 3) {
				return null;
			}
			return (i % (3 * 3)) % 3;
		}
	},
	Curly1(Straight2.start() + Straight2.total()) {
		@Override
		public int total() {
			return 4 * (5 * 3 * 3 + 2 * 3);
		}

		@Override
		public Integer getShape(int hair) {
			return (hair - start()) / (5 * 3 * 3 + 2 * 3) + 1;
		}

		@Override
		public Integer getFront(int hair) {
			int i = (hair - start()) % (5 * 3 * 3 + 2 * 3);
			if (i >= 5 * 3 * 3) {
				return (i - 5 * 3 * 3) / 3 + 5 + 1;
			}
			return i / (3 * 3) + 1;
		}

		@Override
		public Integer getVolume(int hair) {
			int i = (hair - start()) % (5 * 3 * 3 + 2 * 3);
			if (i >= 5 * 3 * 3) {
				return (i - 5 * 3 * 3) % 3 + 1;
			}
			return (i % (3 * 3)) / 3 + 1;
		}

		@Override
		public Integer getBandana(int hair) {
			int i = (hair - start()) % (5 * 3 * 3 + 2 * 3);
			if (i >= 5 * 3 * 3) {
				return null;
			}
			return (i % (3 * 3)) % 3;
		}
	},
	Curly2(Curly1.start() + Curly1.total()) {
		@Override
		public int total() {
			return 4 * 6 * 2;
		}

		@Override
		public Integer getShape(int hair) {
			return (hair - start()) / (6 * 2) + 1;
		}

		@Override
		public Integer getFront(int hair) {
			return ((hair - start()) % (6 * 2)) / 2 + 1;
		}

		@Override
		public Integer getVolume(int hair) {
			return ((hair - start()) % (6 * 2)) % 2 + 1;
		}
	},
	Ponytail1(Curly2.start() + Curly2.total()) {
		@Override
		public int total() {
			return 3 * 4 * 3;
		}

		@Override
		public Integer getShape(int hair) {
			return (hair - start()) / (4 * 3) + 1;
		}

		@Override
		public Integer getFront(int hair) {
			return ((hair - start()) % (4 * 3)) / 3 + 1;
		}

		@Override
		public Integer getVolume(int hair) {
			return ((hair - start()) % (4 * 3)) % 3 + 1;
		}
	},
	Ponytail2(Ponytail1.start() + Ponytail1.total()) {
		@Override
		public int total() {
			return 3 * 4 * 3;
		}

		@Override
		public Integer getShape(int hair) {
			return (hair - start()) / (4 * 3) + 1;
		}

		@Override
		public Integer getFront(int hair) {
			return ((hair - start()) % (4 * 3)) / 3 + 1;
		}

		@Override
		public Integer getVolume(int hair) {
			return ((hair - start()) % (4 * 3)) % 3 + 1;
		}
	},
	Dreadlocks(Ponytail2.start() + Ponytail2.total()) {
		@Override
		public int total() {
			return 3 * 4 * 2;
		}

		@Override
		public Integer getShape(int hair) {
			return (hair - start()) / (4 * 2) + 1;
		}

		@Override
		public Integer getFront(int hair) {
			return ((hair - start()) % (4 * 2)) / 2 + 1;
		}

		@Override
		public Integer getVolume(int hair) {
			return ((hair - start()) % (4 * 2)) % 2 + 1;
		}
	},
	/**
	 * Pulled Back
	 */
	Hairband(Dreadlocks.start() + Dreadlocks.total()) {
		@Override
		public int total() {
			return 3 * 6;
		}

		@Override
		public Integer getShape(int hair) {
			return (hair - start()) / 6 + 1;
		}

		@Override
		public Integer getFront(int hair) {
			return (hair - start()) % 6 + 1;
		}
	},
	SpecialHairstyles1(Hairband.start() + Hairband.total()) {
		@Override
		public int total() {
			return 18;
		}

		@Override
		public Integer getShape(int hair) {
			return hair - start() + 1;
		}
	};

	private final int from;

	Hairs(int start) {
		this.from = start;
	}

	public int start() {
		return from;
	}

	public abstract int total();

	public Integer getShape(int hair) {
		return null;
	}

	public Integer getFront(int hair) {
		return null;
	}

	public Integer getVolume(int hair) {
		return null;
	}

	public Integer getDarkness(int hair) {
		return null;
	}

	public Integer getBandana(int hair) {
		return null;
	}

	public static final int MIN_VALUE = Bald.start();
	public static final int MAX_VALUE = SpecialHairstyles1.start() + SpecialHairstyles1.total();

	public static Hairs valueOf(int hair) {
		if (hair < MIN_VALUE || hair >= MAX_VALUE) {
			throw new IllegalArgumentException("hair#" + hair);
		}
		for (Hairs i : values()) {
			if (hair >= i.start() && hair < i.start() + i.total()) {
				return i;
			}
		}
		return null;
	}

	public static String toString(int hair) {
		Hairs v = valueOf(hair);
		if (null == v) {
			return null;
		}

		String s = v.name();
		Integer i = v.getShape(hair);
		if (null != i) {
			s += " / Shape" + i;
		}
		i = v.getFront(hair);
		if (null != i) {
			s += " / Front" + i;
		}
		i = v.getVolume(hair);
		if (null != i) {
			s += " / Volume" + i;
		}
		i = v.getDarkness(hair);
		if (null != i) {
			s += " / Darkness" + i;
		}
		i = v.getBandana(hair);
		if (null != i) {
			s += " / Bandana" + i;
		}
		return s;
	}

}
