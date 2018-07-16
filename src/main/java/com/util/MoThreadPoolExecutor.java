package com.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
/**
 * 
 * @author 80374514
 *
 */
public class MoThreadPoolExecutor extends ThreadPoolExecutor{
	private final ThreadLocal<Long> startTime = new ThreadLocal<Long>();
	private final Logger log = Logger.getLogger(MoThreadPoolExecutor.class);
	private final AtomicLong runTask = new AtomicLong();
	private final AtomicLong okTask = new AtomicLong();//executor ok

	public MoThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		// TODO Auto-generated constructor stub
	}

	public MoThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
		// TODO Auto-generated constructor stub
	}

	public MoThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
			RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory, handler);
		// TODO Auto-generated constructor stub
	}

	public MoThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory);
		// TODO Auto-generated constructor stub
	}
	
	protected void  afterExecute(Runnable r, Throwable t){
		try {
			Thread thread = Thread.currentThread();
			long end = System.currentTimeMillis();
			log.info("线程:"+thread.getName()+",:"+okTask.incrementAndGet()+"执行结束"+",耗时"+ (end-startTime.get())+"毫秒");
		} finally {
			// TODO: handle exception
			super.afterExecute(r, t);
		}
		
	}
	
	protected void beforeExecute(Thread t,Runnable r){
		super.beforeExecute(t, r);
		startTime.set(System.currentTimeMillis());
	}
	

}
