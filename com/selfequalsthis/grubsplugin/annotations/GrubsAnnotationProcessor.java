package com.selfequalsthis.grubsplugin.annotations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import com.selfequalsthis.grubsplugin.GrubsCommandHandler;

@SupportedAnnotationTypes(value= { "com.selfequalsthis.grubsplugin.*" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)

public class GrubsAnnotationProcessor extends AbstractProcessor {
	
	private File outputFile = new File("commands.yml");
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		
		for (Element element : roundEnv.getElementsAnnotatedWith(GrubsCommandHandler.class)) {
			if (element.getKind() == ElementKind.METHOD) {
				GrubsCommandHandler handler = element.getAnnotation(GrubsCommandHandler.class);
				String command = handler.command();
				
				try {
					boolean newFile = !outputFile.exists();
					BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true));
					if (newFile) {
						writer.write("commands:\n");
					}
					writer.write("\t" + command + ":\n");
					writer.write("\t\tusage: /<command>\n");
					writer.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return true;
	}

}
