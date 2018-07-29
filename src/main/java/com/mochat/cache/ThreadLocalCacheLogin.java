package com.mochat.cache;

public class ThreadLocalCacheLogin {
	
	private static final  ThreadLocal<String> THREAD_LOCAL = new ThreadLocal<String>();
	   
	public static  void set(String t) {
		THREAD_LOCAL.set(t);
	}
	public static String get() {
		return THREAD_LOCAL.get();
	}
	public static  void remove() {
		THREAD_LOCAL.remove();
	}

}
