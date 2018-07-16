package com.mochat.cache;
/**
 * 
 * @author 80374514
 *
 * @param <T>
 */
public class VolitleValue <T> {
	
	private volatile T value ;
	
	public void set(T value) {
		this.value = value;
	}

	public T get() {
		return value;
	}
}
