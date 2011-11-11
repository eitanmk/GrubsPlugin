package com.selfequalsthis.grubsplugin;

public class GrubsUtilities {

	public static String join(String[] input, String delimiter) {
		StringBuilder sb = new StringBuilder();
		String localDelimiter = "";
	    for (int i = 0; i < input.length; ++i) {
	        sb.append(localDelimiter);
	    	sb.append(input[i]);
	    	
	    	if (i == 0) {
	    		localDelimiter = delimiter;
	    	}
	    }
	    return sb.toString();
	}
}
