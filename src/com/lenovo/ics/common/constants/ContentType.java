package com.lenovo.ics.common.constants;

public class ContentType {

	public static final int OTHER = 10000;
	public static final int FILM = 10001;
	public static final int TELEPLAY = 10002;
	public static final int VARIETY = 10003;
	public static final int SOPRT = 10004;

	public static boolean isSoprt(int contentType) {
		return contentType == SOPRT;
	}

}
