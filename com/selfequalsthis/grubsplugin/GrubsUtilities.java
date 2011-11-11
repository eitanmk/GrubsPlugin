package com.selfequalsthis.grubsplugin;

import org.bukkit.entity.Player;

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
	
	public static void multilinePrint(Player target, String prefix, String[] listItems) {
		boolean useSeparator = false;
		String list = "";
		for (String s : listItems) {
			if ( (prefix.length() + list.length() + 2 + s.length()) > 55) {
				GrubsMessager.sendMessage(
					target, 
					GrubsMessager.MessageLevel.INFO,
					prefix + list
				);
				list = "";
				useSeparator = false;
			}
			
			if (useSeparator) {
				list += ", ";
			}
			else {
				useSeparator = true;
			}
			
			
			list += s;	
		}

		GrubsMessager.sendMessage(
			target, 
			GrubsMessager.MessageLevel.INFO,
			prefix + list
		);
	}
}
