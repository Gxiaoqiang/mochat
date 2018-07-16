/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package com.util;

public class SizeDescriptor {
	private int value;
	private String description;

	public SizeDescriptor(String description) {
		this.description = description;
		this.value = SizeParser.parse(description);
	}

	public int getValue() {
		return this.value;
	}

	public String getDescription() {
		return this.description;
	}
}