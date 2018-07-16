package com.mochat.cache;


import com.mochat.model.UserInfo;

public class ThreadLocalCache {

	private static final  ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<UserInfo>();
   
	public static  void set(UserInfo t) {
		THREAD_LOCAL.set(t);
	}
	public static UserInfo get() {
		return THREAD_LOCAL.get();
	}
	public static  void remove() {
		THREAD_LOCAL.remove();
	}
}
