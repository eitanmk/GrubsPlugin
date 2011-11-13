package com.selfequalsthis.grubsplugin.modules.weathercontrol;

import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.weather.WeatherListener;

import com.selfequalsthis.grubsplugin.GrubsMessager;

public class WeatherControlWeatherListener extends WeatherListener {

	public void onWeatherChange(WeatherChangeEvent event) {
		if (event.toWeatherState()) {
			GrubsMessager.broadcast(GrubsMessager.MessageLevel.INFO, "[Weather] Rain is starting.");
		}
		else {
			GrubsMessager.broadcast(GrubsMessager.MessageLevel.INFO, "[Weather] Rain is stopping.");
		}
	}
	
}
