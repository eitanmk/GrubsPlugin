package com.selfequalsthis.grubsplugin.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GrubsCommandHandler {
	String command();
	String desc() default "FIXME: no description set";
	String usage() default "/<command>";
}
