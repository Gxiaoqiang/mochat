/**
 * 
 */
package com.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author 80374514
 *
 */
public class MoThread extends Thread{
	private final static String DEFAULT_NAME = "MyAppThreadPool";
	private static final  AtomicLong aLive = new AtomicLong();
	private static final  AtomicLong created = new AtomicLong();
	

	private boolean isDaemon = false;
	
	public MoThread(Runnable target) {
		this(target, DEFAULT_NAME);
		// TODO Auto-generated constructor stub
	}
	public MoThread(Runnable target,boolean isDaemon) {
		this(target, DEFAULT_NAME);
		this.isDaemon = isDaemon;
		// TODO Auto-generated constructor stub
	}

	public MoThread(Runnable target, String name) {
		super(target, name+"-"+created.incrementAndGet());
		setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				// TODO Auto-generated method stub
				e.printStackTrace();
				System.out.println("UNCAUGHT in thread:"+t.getName()+e.getMessage());
				
			}
		});
		this.setDaemon(isDaemon);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			aLive.incrementAndGet();
			super.run();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{

			aLive.decrementAndGet();
		}
		
	}

	public static AtomicLong getAlive() {
		return aLive;
	}

	public static AtomicLong getCreated() {
		return created;
	}
}
