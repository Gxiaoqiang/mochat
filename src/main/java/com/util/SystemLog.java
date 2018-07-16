package com.util;

import java.io.PrintStream;
import java.util.Date;

import org.springframework.util.StringUtils;

public class SystemLog {
	private static PrintStream out = System.out;
	private static PrintStream err = System.err;

	private static String prefix = "";

	public static void setPrefix(String[] prefixStr) {
		prefix = "";
		for (String s : prefixStr) {
			if (StringUtils.isEmpty(s)) {
				continue;
			}
			prefix = prefix + "[" + s + "]";
		}
	}

	private static String getTimestamp() {
		long logTime = System.currentTimeMillis();
		return "[" + DateFormatUtil.newInstance().full().format(new Date(logTime)) + "]";
	}

	public static void out(Object msg) {
		if (null == out) {
			return;
		}
		out.println(getTimestamp() + prefix + msg);
	}

	public static void err(Object msg) {
		if (null == err) {
			return;
		}
		err.println(getTimestamp() + prefix + msg);
	}

	public static void err(Object msg, Throwable e) {
		if (null == err) {
			return;
		}
		err.println(getTimestamp() + prefix + msg);
		err(e);
	}

	public static void err(Throwable e) {
		if (null == err) {
			return;
		}
		if (null == e) {
			return;
		}

		e.printStackTrace(err);
	}

	public static void setOut(PrintStream outStream) {
		out = outStream;
	}

	public static void setErr(PrintStream errStream) {
		err = errStream;
	}
}