/** <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao. **/
package com.util;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ResourceUtil {
	public static InputStream getResourceAsStream(String res) throws FileNotFoundException {
		InputStream is = ClassLoader.class.getResourceAsStream(res);
		if (null == is) {
			throw new FileNotFoundException("class path resource [" + res + "] cannot be found");
		}
		return ClassLoader.class.getResourceAsStream(res);
	}
}