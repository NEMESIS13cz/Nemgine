package com.nemezor.nemgine.misc;

public enum Data {

	BYTE(Registry.BYTE_SUFFIX, Registry.ONE_BYTE_IN_BYTES), KILOBYTE(Registry.KILOBYTE_SUFFIX, Registry.ONE_KILOBYTE_IN_BYTES),
	MEGABYTE(Registry.MEGABYTE_SUFFIX, Registry.ONE_MEGABYTE_IN_BYTES), GIGABYTE(Registry.GIGABYTE_SUFFIX, Registry.ONE_GIGABYTE_IN_BYTES),
	TERABYTE(Registry.TERABYTE_SUFFIX, Registry.ONE_TERABYTE_IN_BYTES), PETABYTE(Registry.PETABYTE_SUFFIX, Registry.ONE_PETABYTE_IN_BYTES),
	EXABYTE(Registry.EXABYTE_SUFFIX, Registry.ONE_EXABYTE_IN_BYTES), KIBIBYTE(Registry.KIBIBYTE_SUFFIX, Registry.ONE_KIBIBYTE_IN_BYTES),
	MEBIBYTE(Registry.MEBIBYTE_SUFFIX, Registry.ONE_MEBIBYTE_IN_BYTES), GIBIBYTE(Registry.GIBIBYTE_SUFFIX, Registry.ONE_GIBIBYTE_IN_BYTES),
	TEBIBYTE(Registry.TEBIBYTE_SUFFIX, Registry.ONE_TEBIBYTE_IN_BYTES), PEBIBYTE(Registry.PEBIBYTE_SUFFIX, Registry.ONE_PEBIBYTE_IN_BYTES),
	EXBIBYTE(Registry.EXBIBYTE_SUFFIX, Registry.ONE_EXBIBYTE_IN_BYTES);
	
	public final String suffix;
	public final long amount;
	
	Data(String suffix, long amount) {
		this.suffix = suffix;
		this.amount = amount;
	}
}
