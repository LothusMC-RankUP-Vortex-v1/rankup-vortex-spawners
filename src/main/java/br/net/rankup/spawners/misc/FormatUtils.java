package br.net.rankup.spawners.misc;

import java.text.DecimalFormat;

public final class FormatUtils {
	private static String[] charlist;

	static {
		FormatUtils.charlist = new String[] { "K", "M", "B", "T", "Q", "Qi", "S", "Se", "O", "N", "D", "UD", "DD", "TD",
				"QD", "QiD", "SD", "SeD", "OD", "ND", "UT", "DT", "TT", "QT" };
	}

	public static String format(final double d) {
		final DecimalFormat df = new DecimalFormat("###.##");
		if (d < 1000.0) {
			return df.format(d);
		}
		if (d < 1000000.0) {
			return String.valueOf(String.valueOf(String.valueOf(df.format(d / 1000.0)))) + FormatUtils.charlist[0];
		}
		if (d < 1.0E9) {
			return String.valueOf(String.valueOf(String.valueOf(df.format(d / 1000000.0)))) + FormatUtils.charlist[1];
		}
		if (d < 1.0E12) {
			return String.valueOf(String.valueOf(String.valueOf(df.format(d / 1.0E9)))) + FormatUtils.charlist[2];
		}
		if (d < 1.0E15) {
			return String.valueOf(String.valueOf(String.valueOf(df.format(d / 1.0E12)))) + FormatUtils.charlist[3];
		}
		if (d < 1.0E18) {
			return String.valueOf(String.valueOf(String.valueOf(df.format(d / 1.0E15)))) + FormatUtils.charlist[4];
		}
		if (d < 1.0E21) {
			return String.valueOf(String.valueOf(String.valueOf(df.format(d / 1.0E18)))) + FormatUtils.charlist[5];
		}
		if (d < 1.0E24) {
			return String.valueOf(String.valueOf(String.valueOf(df.format(d / 1.0E21)))) + FormatUtils.charlist[6];
		}
		if (d < 1.0E27) {
			return String.valueOf(String.valueOf(String.valueOf(df.format(d / 1.0E24)))) + FormatUtils.charlist[7];
		}
		if (d < 1.0E30) {
			return String.valueOf(String.valueOf(String.valueOf(df.format(d / 1.0E27)))) + FormatUtils.charlist[8];
		}
		if (d < 1.0E33) {
			return String.valueOf(String.valueOf(String.valueOf(df.format(d / 1.0E30)))) + FormatUtils.charlist[9];
		}
		if (d < 1.0E36) {
			return String.valueOf(String.valueOf(String.valueOf(df.format(d / 1.0E33)))) + FormatUtils.charlist[10];
		}
		if (d < 1.0E39) {
			return String.valueOf(String.valueOf(String.valueOf(df.format(d / 1.0E36)))) + FormatUtils.charlist[11];
		}
		if (d < 1.0E42) {
			return String.valueOf(String.valueOf(String.valueOf(df.format(d / 1.0E39)))) + FormatUtils.charlist[12];
		}
		if (d < 1.0E45) {
			return String.valueOf(String.valueOf(String.valueOf(df.format(d / 1.0E42)))) + FormatUtils.charlist[13];
		}
		if (d < 1.0E48) {
			return String.valueOf(String.valueOf(String.valueOf(df.format(d / 1.0E45)))) + FormatUtils.charlist[14];
		}
		if (d < 1.0E51) {
			return String.valueOf(String.valueOf(String.valueOf(df.format(d / 1.0E48)))) + FormatUtils.charlist[15];
		}
		if (d < 1.0E54) {
			return String.valueOf(String.valueOf(String.valueOf(df.format(d / 1.0E51)))) + FormatUtils.charlist[16];
		}
		if (d < 1.0E57) {
			return String.valueOf(String.valueOf(String.valueOf(df.format(d / 1.0E54)))) + FormatUtils.charlist[17];
		}
		if (d < 1.0E60) {
			return String.valueOf(String.valueOf(String.valueOf(df.format(d / 1.0E57)))) + FormatUtils.charlist[18];
		}
		if (d < 1.0E63) {
			return String.valueOf(String.valueOf(String.valueOf(df.format(d / 1.0E60)))) + FormatUtils.charlist[19];
		}
		if (d < 1.0E66) {
			return String.valueOf(String.valueOf(String.valueOf(df.format(d / 1.0E63)))) + FormatUtils.charlist[20];
		}
		if (d < 1.0E69) {
			return String.valueOf(String.valueOf(String.valueOf(df.format(d / 1.0E66)))) + FormatUtils.charlist[21];
		}
		return String.valueOf(String.valueOf(String.valueOf(df.format(d / 1.0E69)))) + FormatUtils.charlist[22];
	}

}
