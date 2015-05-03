package com.selfequalsthis.grubsplugin.command;

import java.util.HashMap;

import org.spongepowered.api.util.command.spec.CommandSpec;

public abstract class AbstractGrubsCommandHandlers {

	protected HashMap<String,CommandSpec> commands = new HashMap<String,CommandSpec>();

	public HashMap<String,CommandSpec> getCommands() {
		return this.commands;
	}

}
