/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package com.util;

public class SizeParser {
	private static final int UNIT_K = 1024;
	private static final int UNIT_M = 1048576;
	private static final int UNIT_G = 1073741824;

	public static int parse(String sizeStr) {
		if (null == sizeStr) {
			return 0;
		}

		sizeStr = sizeStr.trim().toUpperCase();

		char unitChar = sizeStr.charAt(sizeStr.length() - 1);

		if ((unitChar == 'K') || (unitChar == 'M') || (unitChar == 'G')) {
			int unit = 1;

			if (unitChar == 'K')
				unit = 1024;
			else if (unitChar == 'M')
				unit = 1048576;
			else if (unitChar == 'G') {
				unit = 1073741824;
			}

			return (Integer.parseInt(sizeStr.substring(0, sizeStr.length() - 1)) * unit);
		}
		return Integer.parseInt(sizeStr);
	}
}