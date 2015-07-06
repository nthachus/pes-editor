package editor.data;

public enum StatType {
	/**
	 * Indicates if the {@link Stat} value is an integer.
	 */
	integer,

	/**
	 * Indicates if the {@link Stat} value is a person height in centimeters
	 * and it will be decreased by 148cm before saving.
	 */
	height148,

	/**
	 * Indicates if the {@link Stat} value is a person age
	 * and it will be decreased by 15yo before saving.
	 */
	age15,

	/**
	 * Indicates if the {@link Stat} value is a nation ID (0-130).
	 */
	nationId,

	/**
	 * Indicates if the {@link Stat} value is a stronger-foot ID (R, L).
	 */
	footId,

	/**
	 * Indicates if the {@link Stat} value is a positive integer
	 * and it will be decreased by 1 before saving.
	 */
	positiveInt,

	/**
	 * Indicates if the {@link Stat} value is an injury-tolerance ID (C, B, A).
	 */
	injuryId,

	/**
	 * Indicates if the {@link Stat} value is a 4-bit signed integer.
	 */
	integer4,
}
