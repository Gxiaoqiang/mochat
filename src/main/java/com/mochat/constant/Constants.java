package com.mochat.constant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.mochat.cache.VolitleValue;

public class Constants {

	public static final ThreadLocal<DateFormat>  df = new ThreadLocal<DateFormat>(){
		@Override
		protected DateFormat initialValue(){
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};
	
	public static final String USER_SESSION = "user_session"; //用户session标志
	
	
	public static final String ROOM_CHAT = "room";
	
	public static final String RANDOM_CHAT = "random";
	
	public static final VolitleValue<String> PROJECT_PATH = new VolitleValue<String>();
}
