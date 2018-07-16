/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package com.util;

import java.text.SimpleDateFormat;

public class DateFormatUtil {
	private static DateFormatUtil singleton = new DateFormatUtil();
	private SimpleDateFormat full;
	private SimpleDateFormat yyyyMMddHHmmssSSS;
	private SimpleDateFormat yyyyMMddHHmmss;
	private SimpleDateFormat yyyyMMddHHmm;
	private SimpleDateFormat yyyyMMddHH;
	private SimpleDateFormat yyyyMMdd;
	private SimpleDateFormat HHmmss;

	public static DateFormatUtil newInstance() {
		return new DateFormatUtil();
	}

	public static DateFormatUtil singleton() {
		return singleton;
	}

	public SimpleDateFormat full() {
		if (this.full == null) {
			synchronized (this) {
				if (this.full == null) {
					this.full = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				}
			}
		}
		return this.full;
	}

	public SimpleDateFormat yyyyMMddHHmmssSSS() {
		if (this.yyyyMMddHHmmssSSS == null) {
			synchronized (this) {
				if (this.yyyyMMddHHmmssSSS == null) {
					this.yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				}
			}
		}
		return this.yyyyMMddHHmmssSSS;
	}

	public SimpleDateFormat yyyyMMddHHmmss() {
		if (this.yyyyMMddHHmmss == null) {
			synchronized (this) {
				if (this.yyyyMMddHHmmss == null) {
					this.yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
				}
			}
		}
		return this.yyyyMMddHHmmss;
	}

	public SimpleDateFormat yyyyMMddHHmm() {
		if (this.yyyyMMddHHmm == null) {
			synchronized (this) {
				if (this.yyyyMMddHHmm == null) {
					this.yyyyMMddHHmm = new SimpleDateFormat("yyyyMMddHHmm");
				}
			}
		}
		return this.yyyyMMddHHmm;
	}

	public SimpleDateFormat yyyyMMddHH() {
		if (this.yyyyMMddHH == null) {
			synchronized (this) {
				if (this.yyyyMMddHH == null) {
					this.yyyyMMddHH = new SimpleDateFormat("yyyyMMddHH");
				}
			}
		}
		return this.yyyyMMddHH;
	}

	public SimpleDateFormat yyyyMMdd() {
		if (this.yyyyMMdd == null) {
			synchronized (this) {
				if (this.yyyyMMdd == null) {
					this.yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
				}
			}
		}
		return this.yyyyMMdd;
	}

	public SimpleDateFormat HHmmss() {
		if (this.HHmmss == null) {
			synchronized (this) {
				if (this.HHmmss == null) {
					this.HHmmss = new SimpleDateFormat("HHmmss");
				}
			}
		}
		return this.HHmmss;
	}
}