package com.mochat.filter;

import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.cert.CertificateFactory;

import org.apache.commons.codec.binary.Base64;

public final class SignatureUtils {
	
	private static final String CERTTYPE_X509 = "X.509";
	private static final String ENCODING = "UTF-8";
	private static final String CERTFILE = "/SSOPKI.cer";
	private static final String ALG_SHA1withRSA = "SHA1withRSA";
	private static final String MD_SHA1 = "SHA1";

	public SignatureUtils() {
	}

	private static boolean checkBase64Data(String data) {
		return data.indexOf("USID") <= -1 || data.indexOf("TIME") <= -1;
	}

	public static boolean isValidSignature(String data, String token) {
		try {
			data = URLDecoder.decode(data, "UTF-8");
			token = URLDecoder.decode(token, "UTF-8");
			if (checkBase64Data(data))
				return validSignatureWithBase64(data, token);
			return validSignature(data, token);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static boolean validSignature(String data, String token)
			throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA1");
		md.update(data.getBytes());
		byte buff[] = md.digest();
		StringBuilder b = new StringBuilder();
		char ASCII[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
				'B', 'C', 'D', 'E', 'F' };
		for (int i = 0; i < buff.length; i++) {
			int item = buff[i] & 255;
			if (item < 16)
				b.append('0').append(ASCII[item]);
			else
				b.append(ASCII[item / 16]).append(ASCII[item % 16]);
		}

		byte plainTextByte[] = b.toString().getBytes();
		byte signatureByte[] = Base64.decodeBase64(token.getBytes());
		return validSignatureWithX509Cert(plainTextByte, signatureByte);
	}

	private static boolean validSignatureWithBase64(String data, String token)
			throws Exception {
		byte plainTextByte[] = Base64.decodeBase64(data.getBytes());
		byte signatureByte[] = Base64.decodeBase64(token.getBytes());
		return validSignatureWithX509Cert(plainTextByte, signatureByte);
	}

	private static boolean validSignatureWithX509Cert(byte plainTextByte[],
			byte signatureByte[]) throws Exception {
		CertificateFactory certificateFactory = CertificateFactory
				.getInstance("X.509");
		java.security.cert.Certificate certificate = certificateFactory
				.generateCertificate(SignatureUtils.class
						.getResourceAsStream("/SSOPKI.cer"));
		Signature signature = Signature.getInstance("SHA1withRSA");
		signature.initVerify(certificate);
		signature.update(plainTextByte);
		return signature.verify(signatureByte);
	}
}