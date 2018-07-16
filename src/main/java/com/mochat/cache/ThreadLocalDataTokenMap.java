package com.mochat.cache;

/**
 * 线程局部 (thread-local) 变量
 * 
 *
 */
public class ThreadLocalDataTokenMap {
	
	private static final ThreadLocal<Object> threadLocal = new ThreadLocal<Object>();
	
	/**
	 * 将此线程局部变量的当前线程副本中的值设置为指定值
	 * @param object
	 */
	public static void set(Object object) {
		threadLocal.set(object);
	}
	/**
	 * 返回此线程局部变量的当前线程副本中的值
	 * @return
	 */
	public static Object get() {
		return threadLocal.get();
	}
	
	/**
	 * 移除此线程局部变量当前线程的值
	 */
	public static void remove() {
		threadLocal.remove();
	}

}
