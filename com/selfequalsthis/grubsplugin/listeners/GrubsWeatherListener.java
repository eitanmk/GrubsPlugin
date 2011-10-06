package com.selfequalsthis.grubsplugin.listeners;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.weather.WeatherListener;

public class GrubsWeatherListener extends WeatherListener {

	public void onWeatherChange(WeatherChangeEvent event) {
		World world = event.getWorld();
		
		if (event.toWeatherState()) {
			for (Player p : world.getPlayers()) {
				p.sendMessage(ChatColor.GREEN + "[Weather] Rain is starting.");
			}
		}
		else {
			for (Player p : world.getPlayers()) {
				p.sendMessage(ChatColor.GREEN + "[Weather] Rain is stopping.");
			}
		}
	}
	
}
