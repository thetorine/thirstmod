package com.thetorine.thirstmod.common.utils;

public class Version {
	public static final String MAJOR_VERSION = "1";
	public static final String REVISION_VERSION = "8";
	public static final String MINOR_VERSION = "4";

	public static final String VERSION = MAJOR_VERSION + "." + REVISION_VERSION + "." + MINOR_VERSION;
	public static final String VERSION_BOUNDS = MAJOR_VERSION + "." + REVISION_VERSION + ",";

	public static String getVersion() {
		return String.format("%d.%d.%d", MAJOR_VERSION, REVISION_VERSION, MINOR_VERSION);
	}

	public static String getRelease() {
		return String.format("%d.%d", MAJOR_VERSION, REVISION_VERSION);
	}
}
