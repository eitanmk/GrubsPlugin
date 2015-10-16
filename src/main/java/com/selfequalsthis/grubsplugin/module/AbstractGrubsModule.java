package com.selfequalsthis.grubsplugin.module;

import com.selfequalsthis.grubsplugin.AbstractGrubsComponent;

public abstract class AbstractGrubsModule extends AbstractGrubsComponent {

	public abstract void enable();
	public abstract void disable();


	/*
	 * protected void registerEventHandlers(Listener listener) { if (listener ==
	 * null) {
	 * this.log("Event listener class is null! Don't forget to instantiate it!"
	 * ); }
	 *
	 * ArrayList<String> listenerData = this.getEventListenerData(listener);
	 *
	 * if (listenerData == null || listenerData.size() == 0) { return; }
	 *
	 * for (String data : listenerData) { this.log("Listening to " + data); }
	 *
	 * Bukkit.getPluginManager().registerEvents(listener, this.pluginRef); }
	 *
	 * protected void unregisterEventHandlers(Listener listener) {
	 * ArrayList<String> listenerData = this.getEventListenerData(listener);
	 *
	 * if (listenerData == null || listenerData.size() == 0) { return; }
	 *
	 * for (String data : listenerData) { this.log("Stopped listening to " +
	 * data); }
	 *
	 * HandlerList.unregisterAll(listener); }
	 */

	/*
	 * private ArrayList<String> getEventListenerData(Listener listener) {
	 * ArrayList<String> ret = new ArrayList<String>();
	 *
	 * Method[] methods; try { methods =
	 * listener.getClass().getDeclaredMethods(); } catch (NoClassDefFoundError
	 * e) { this.log("Could not find listener class: " + listener.getClass());
	 * return null; }
	 *
	 * for (int i = 0; i < methods.length; i++) { Method method = methods[i];
	 * EventHandler eh = method.getAnnotation(EventHandler.class); if (eh ==
	 * null) continue; Class<?> checkClass = method.getParameterTypes()[0];
	 * String eventClassName = checkClass.getName(); String eventType =
	 * eventClassName.substring(eventClassName.lastIndexOf(".") + 1); String
	 * eventPriority = eh.priority().toString(); ret.add("" + eventType + " (" +
	 * eventPriority + ")"); }
	 *
	 * return ret; }
	 */

}
