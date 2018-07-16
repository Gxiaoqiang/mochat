/**
 * 
 */
package com.mochat.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mochat.constant.CommonLogger;
import com.mochat.model.ResponseEntity;

/**
 * @author 80374514
 *
 */
@ControllerAdvice
public class ControllerException<T> extends CommonLogger{

	private static final String lineSeparator = System.getProperty("line.separator", "\n");
	@ExceptionHandler
	@ResponseBody
	public ResponseEntity<T> handleException(HttpServletRequest request, Exception ex) {
		request.setAttribute("exception", ex);
		String stack = getCallStatck(ex);
		logger.error(stack);
		ex.printStackTrace();
		String msg = ex.getMessage();
		if(msg == null) {
			msg = stack;
		}
		return new ResponseEntity<T>(false, null, msg);
	}
	
	public String getCallStatck(Exception ex) {
        StackTraceElement[] stackElements = ex.getStackTrace();
        StringBuffer exceptionStr = new StringBuffer();
        exceptionStr.append("")
        	.append("/")
        	.append("")
        	.append(" ")
        	.append(df.get().format(new Date()))
        	.append(lineSeparator)
        	.append(ex.toString()).append(lineSeparator);
        if (stackElements != null) {            
            for (int i = 0; i < stackElements.length; i++) {
            	exceptionStr.append(stackElements[i].getClassName())
            		.append(".")
            		.append(stackElements[i].getMethodName())
            		.append("(")
            		.append(stackElements[i].getFileName())
            		.append(":")
            		.append(stackElements[i].getLineNumber())
            		.append(")")
            		.append(lineSeparator);
            }
        }
        return exceptionStr.toString();
    }
}
