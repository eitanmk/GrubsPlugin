package com.selfequalsthis.grubsplugin.command;

public class GrubsCommandInfo {

	//public CommandSender sender;
	//public Command command;
	public String label;
	public String[] args;

	public GrubsCommandInfo(/*CommandSender sender, Command command,*/ String label, String[] args) {
		//this.sender = sender;
		//this.command = command;
		this.label = label;
		this.args = args;
	}

}
