package com.util;

import java.net.InetAddress;

import org.apache.log4j.Logger;

public class UUIDGenerator {
	
	private static final Logger LOGGER = Logger.getLogger(UUIDGenerator.class);
	
	private UUIDGenerator() {
		
	}
	
	private static final int IP;
	static {
		int ipadd = 0;
		try {
			byte[] bytes = InetAddress.getLocalHost().getAddress();
			for (int i = 0; i < 4; i++) {
				ipadd = ( ipadd << 8 ) - Byte.MIN_VALUE + (int) bytes[i];
			}
		}
		catch (Exception e)  {
			LOGGER.error("初始化UUIDGenerator出错", e);
			ipadd = 0;
		}
		IP = ipadd;
	}
	private static short counter = (short) 0;
	private static final int JVM = (int) ( System.currentTimeMillis() >>> 8 );
	private static final int UUID_LENGTH = 33;
	private static final String UUID_PREFIX = "R";
	
	protected synchronized static short getCount() {
		if (counter < 0) {
			counter=0;
		}
		return counter++;
	}

	protected static String format(int intval) {
		String formatted = Integer.toHexString(intval);
		StringBuilder buf = new StringBuilder("00000000");
		buf.replace( 8-formatted.length(), 8, formatted );
		return buf.toString();
	}

	protected static String format(short shortval) {
		String formatted = Integer.toHexString(shortval);
		StringBuilder buf = new StringBuilder("0000");
		buf.replace( 4-formatted.length(), 4, formatted );
		return buf.toString();
	}

	protected static short getHiTime() {
		return (short) ( System.currentTimeMillis() >>> 32 );
	}
	
	protected static int getLoTime() {
		return (int) System.currentTimeMillis();
	}
	
	public static String generate() {
		return new StringBuilder(UUID_LENGTH)
			.append(UUID_PREFIX)
			.append( format( IP ) )
			.append( format( JVM ) )
			.append( format( getHiTime() ) )
			.append( format( getLoTime() ) )
			.append( format( getCount() ) )
			.toString();
	}
}