package com.toymb.server;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Utils {

	
	/*
	 * Assumes none of the parameters are null or empty, Should be done by the caller
	 */
	public static String getFirstClosestString(List<String> strings,String testString) {
		int minDist = Integer.MAX_VALUE;
		String result =null;
		for(String s: strings) {
			int dist = StringUtils.getLevenshteinDistance(s, testString);
			if(dist<minDist) {
				minDist=dist;
				result=s;
			}
		}
		return result;
		
	}
}
