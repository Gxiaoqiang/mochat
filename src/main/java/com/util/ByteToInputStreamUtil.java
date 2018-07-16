package com.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @date 2015-03-30
 * @version 1
 */

public class ByteToInputStreamUtil {

	public static final InputStream byteToInput(byte[] buf) {   
        return new ByteArrayInputStream(buf);   
    }   
  
    public static final byte[] inputToByte(InputStream inStream)   
            throws IOException {   
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();   
        byte[] buff = new byte[100];   
        int rc = 0;   
        while ((rc = inStream.read(buff, 0, 100)) > 0) {   
            swapStream.write(buff, 0, rc);   
        }   
        byte[] in2b = swapStream.toByteArray(); 
        return in2b;   
    }   

	
}
