package com.selfequalsthis.grubsplugin.annotations;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;


@SupportedAnnotationTypes(value= { "com.selfequalsthis.grubsplugin.annotations.*" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)

public class GrubsAnnotationProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		Messager messager = this.processingEnv.getMessager();
		
		HashMap<String,GrubsCommandHandler> commands = new HashMap<String,GrubsCommandHandler>();
		
		messager.printMessage(Kind.NOTE, roundEnv.toString());
		messager.printMessage(Kind.NOTE, "" + roundEnv.getElementsAnnotatedWith(GrubsCommandHandler.class).size());
		
		for (Element element : roundEnv.getElementsAnnotatedWith(GrubsCommandHandler.class)) {			
			if (element.getKind() == ElementKind.METHOD) {
				GrubsCommandHandler handler = element.getAnnotation(GrubsCommandHandler.class);
				String command = handler.command();
				messager.printMessage(Kind.NOTE, command);
				commands.put(command, handler);
			}
		}

		Filer filer = this.processingEnv.getFiler();
		FileObject fileObj;
		Writer writer;
		String[] orderedKeys = commands.keySet().toArray(new String[0]);
		Arrays.sort(orderedKeys);
		
		try {
			// create commands.part file
			fileObj = filer.createResource(
					StandardLocation.SOURCE_OUTPUT,
					"",
					"commands.part",
					(Element)null);
				
			writer = fileObj.openWriter().append("commands:\n");
			for(int i = 0, len = commands.size(); i < len; ++i) {
				GrubsCommandHandler handler = commands.get(orderedKeys[i]);
				writer
					.append("    " + handler.command() + ":\n")
					.append("        description: " + handler.desc() + "\n")
					.append("        usage: " + handler.usage() + "\n")
					.append("        permission: grubs.command." + handler.command() + "\n")
					.append("        permission-message: Sorry.\n");
			}
			writer.close();
			
			// create permissions.part file
			fileObj = filer.createResource(
					StandardLocation.SOURCE_OUTPUT,
					"",
					"permissions.part",
					(Element)null);
			
			writer = fileObj.openWriter().append("permissions:\n");
			writer
				.append("    grubs.command.*:\n")
				.append("        default: op\n")
				.append("        children:");
			for(int i = 0, len = commands.size(); i < len; ++i) {
				GrubsCommandHandler handler = commands.get(orderedKeys[i]);
				writer.append("\n            grubs.command." + handler.command() + ": true");
			}
			writer.append("\n");
			for(int i = 0, len = commands.size(); i < len; ++i) {
				GrubsCommandHandler handler = commands.get(orderedKeys[i]);
				writer
					.append("    grubs.command." + handler.command() + ":\n")
					.append("        default: " + handler.defaultPermission() + "\n");
			}
			writer.close();
		} catch (IOException e) { }

		return true;
	}

}
