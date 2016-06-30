package com.supinfo.transcode.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Crypter {
	public static String Md5(String password){
		byte[] uniqueKey = password.getBytes();
		  byte[] hash = null;
		  try {
			hash = MessageDigest.getInstance("MD5").digest(uniqueKey);
		  } catch (NoSuchAlgorithmException e) {
			throw new Error("no MD5 support in this VM");
		  }
		  StringBuffer hashString = new StringBuffer();
		  for ( int i = 0; i < hash.length; ++i ) {
			String hex = Integer.toHexString(hash[i]);
			if ( hex.length() == 1 ) {
			  hashString.append('0');
			  hashString.append(hex.charAt(hex.length()-1));
			} else {
			  hashString.append(hex.substring(hex.length()-2));
			}
		  }
		  return hashString.toString();
	}
}
