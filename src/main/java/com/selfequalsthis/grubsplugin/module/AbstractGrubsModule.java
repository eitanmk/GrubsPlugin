package com.selfequalsthis.grubsplugin.module;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.spongepowered.api.event.Listener;

import com.selfequalsthis.grubsplugin.AbstractGrubsComponent;
import com.selfequalsthis.grubsplugin.event.AbstractGrubsEventListeners;

public abstract class AbstractGrubsModule extends AbstractGrubsComponent {

	public AbstractGrubsEventListeners eventListeners;
	
	public abstract void enable();
	public abstract void disable();

	protected void registerEventHandlers(AbstractGrubsEventListeners listeners) {
		if (listeners == null) {
			this.log("Event listener class is null! Don't forget to instantiate it!");
			return;
		}

		ArrayList<String> listenerData = this.getEventListenerData(listeners);
		if (listenerData == null || listenerData.size() == 0) {
			return;
		}
		for (String data : listenerData) {
			this.log("Listening to " + data);
		}

		this.game.getEventManager().registerListeners(this.pluginRef, listeners);
	}
	
	protected void unregisterEventHandlers(AbstractGrubsEventListeners listeners) {
		ArrayList<String> listenerData = this.getEventListenerData(listeners);
		if (listenerData == null || listenerData.size() == 0) {
			return;
		}
		for (String data : listenerData) {
			this.log("Stopped listening to " + data);
		}

		this.game.getEventManager().unregisterListeners(listeners);
	}
	
	private ArrayList<String> getEventListenerData(AbstractGrubsEventListeners listener) {
		ArrayList<String> ret = new ArrayList<String>();
		Method[] methods;

		try {
			methods = listener.getClass().getDeclaredMethods();
		}
		catch (NoClassDefFoundError e) {
			this.log("Could not find listener class: " + listener.getClass());
			return null;
		}

		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			Listener eh = method.getAnnotation(Listener.class);
			if (eh == null) continue;
			Class<?> checkClass = method.getParameterTypes()[0];
			String eventClassName = checkClass.getName();
			String eventType = eventClassName.substring(eventClassName.lastIndexOf(".") + 1);
			ret.add(eventType);
		}

		return ret;
	}
}
