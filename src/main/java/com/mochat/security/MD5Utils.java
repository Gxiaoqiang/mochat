package com.mochat.security;

import java.util.ArrayList;
import java.util.List;

import com.security.factory.SecuritySignFactory;
import com.security.factory.SecurityVerifyFactory;
import com.security.md.MD5SecuritySignStrategy;
import com.security.md.MD5SecurityVerifyStrategy;
import com.security.sign.SecuritySign;
import com.security.verify.SecurityVerify;
/**
 * 生成data和token以及相关的校验工作
 * @author 80374514
 *
 */
public class MD5Utils {

	
	private static final  SecurityVerifyFactory securityFactory = new SecurityVerifyFactory();
	
	private static final SecuritySignFactory SECURITY_SIGN_FACTORY = new SecuritySignFactory();
	
	public static final String secret = "mochat_secret";
	
	private static final int timeOut = 2*60*60*1000;
	
	private MD5Utils() {}
	
	static {
		MD5SecurityVerifyStrategy md5 = new MD5SecurityVerifyStrategy(secret, timeOut);
		md5.setDataParser(new MochatDataParser());
		List<SecurityVerify> securityVerifys = new ArrayList<>();
		securityVerifys.add(md5);
		securityFactory.setSecurityVerify(md5);
		
		MD5SecuritySignStrategy md5SecuritySignStrategy  = new MD5SecuritySignStrategy(secret);
		
		SECURITY_SIGN_FACTORY.setSecuritySign(md5SecuritySignStrategy);
	}
	
	
	public static SecuritySign getSignInstance() {
		return SECURITY_SIGN_FACTORY.getInstance();
	}
	
	public static SecurityVerifyFactory getVerifyInstance() {
		return securityFactory;
	}
}
