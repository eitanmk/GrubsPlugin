package com.selfequalsthis.grubsplugin.command;

import org.spongepowered.api.util.command.spec.CommandSpec;

public abstract class AbstractGrubsCommand {

	protected CommandSpec cmdSpec;

	public CommandSpec getCommandSpec() {
		return this.cmdSpec;
	}

	public abstract void init();
	public abstract String getCommandName();

}
