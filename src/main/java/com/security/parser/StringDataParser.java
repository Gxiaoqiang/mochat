package com.security.parser;

public class StringDataParser implements DataParser {

	@Override
	public Object parseDataEntity(String data) throws SecurityException {
		return data;
	}

	@Override
	public Long parseTime(String data) throws SecurityException {
		return Long.valueOf(0);
	}

}
