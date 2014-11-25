package com.digger.server.zeroone.utils;

public class StringUtils {

	public static boolean isEmpty(String target){
		return target==null||target.length()==0;
	}
	
	public static boolean isNotEmpty(String target){
		return !isEmpty(target);
	}
	
	
}
