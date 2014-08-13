package com.selfequalsthis.grubsplugin.modules.weathercontrol;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

import com.selfequalsthis.grubsplugin.utils.GrubsMessager;

public class WeatherControlEventListeners implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onWeatherChange(WeatherChangeEvent event) {
		if (event.toWeatherState()) {
			GrubsMessager.broadcast(GrubsMessager.MessageLevel.INFO, "[Weather] Rain is starting.");
		}
		else {
			GrubsMessager.broadcast(GrubsMessager.MessageLevel.INFO, "[Weather] Rain is stopping.");
		}
	}

}
