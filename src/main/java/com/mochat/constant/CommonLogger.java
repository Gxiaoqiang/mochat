/**
 * 
 */
package com.mochat.constant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;

/**
 * @author 80374514
 *
 */
public class CommonLogger {
	
	protected Logger logger = null;
	protected static final ThreadLocal<DateFormat>  df = new ThreadLocal<DateFormat>(){
		@Override
		protected DateFormat initialValue(){
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};
	
	public CommonLogger(){
		logger = Logger.getLogger(this.getClass().getName());
	}
}
