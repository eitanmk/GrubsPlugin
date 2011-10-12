package com.selfequalsthis.grubsplugin;

import java.util.ArrayList;

public abstract class AbstractGrubsModule {
	
	protected ArrayList<String> commands;
	
	public abstract void enable();
	public abstract void disable();

}
