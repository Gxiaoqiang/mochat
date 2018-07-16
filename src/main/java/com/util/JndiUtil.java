/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package com.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JndiUtil {
	public static Object getObject(String jndiName) throws NamingException {
		Context ctx = new InitialContext();
		Object object = null;
		try {
			object = ctx.lookup(jndiName);
		} catch (NamingException e) {
			ctx = (Context) ctx.lookup("java:comp/env");
			object = ctx.lookup(jndiName);
		}
		return object;
	}
}