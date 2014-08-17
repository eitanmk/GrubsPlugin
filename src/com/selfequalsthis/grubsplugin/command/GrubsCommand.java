package com.selfequalsthis.grubsplugin.command;

import java.lang.reflect.Constructor;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

public class GrubsCommand {

	public static PluginCommand makeCommand(String name, Plugin pluginRef) {
		PluginCommand pcmd = null;
		try {
			Constructor<PluginCommand> construct = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			construct.setAccessible(true);
			pcmd = construct.newInstance(name, pluginRef);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return pcmd;
	}

}
