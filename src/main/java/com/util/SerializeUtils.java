/**
 * @Copyright (c) 2015, cmbchina. All rights reserved
 */
package com.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 序列化工?
 * 
 * @project 1_bip_base
 * @version 1
 */
public class SerializeUtils {

	public static byte[] object2Byte(Object obj) {
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (IOException e) {
			SystemLog.err("序列化对象失?", e);
			return null;
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					SystemLog.err("", e);
				}
			}
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					SystemLog.err("", e);
				}
			}
		}
	}

	public static <T extends Serializable> T getObjectFromeBytes(byte[] bytes, Class<T> clazz) {
		ObjectInputStream ois = null;
		ByteArrayInputStream bais = null;
		T obj = null;
		try {
			bais = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bais);
			obj = (T) ois.readObject();
		} catch (IOException e) {
			SystemLog.err("", e);
		} catch (ClassNotFoundException e) {
			SystemLog.err("", e);
		} finally {
			if (bais != null) {
				try {
					bais.close();
				} catch (IOException e) {
					SystemLog.err("", e);
				}
			}
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					SystemLog.err("", e);
				}
			}
		}
		return obj;
	}
}
