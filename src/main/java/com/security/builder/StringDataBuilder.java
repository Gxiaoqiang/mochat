package com.security.builder;

public class StringDataBuilder implements DataBuilder{

	@Override
	public String buildEntityData(Object entity) {
		if (entity instanceof String) {
			return (String)entity;
		}
		return entity.toString();
	}

}
