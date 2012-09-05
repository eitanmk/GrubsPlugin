package com.selfequalsthis.grubsplugin.annotations;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import com.selfequalsthis.grubsplugin.GrubsCommandHandler;

@SupportedAnnotationTypes(value= { "com.selfequalsthis.grubsplugin.*" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)

public class GrubsAnnotationProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		for (Element element : roundEnv.getElementsAnnotatedWith(GrubsCommandHandler.class)) {
			if (element.getKind() == ElementKind.METHOD) {
				GrubsCommandHandler handler = element.getAnnotation(GrubsCommandHandler.class);
				String command = handler.command();

				Filer filer = this.processingEnv.getFiler();
				try {
					FileObject fileObj = filer.createResource(StandardLocation.SOURCE_OUTPUT,
															  "commands",
															  command + ".part",
															  element);
					fileObj.openWriter()
						.append("    " + command + ":\n")
						.append("        usage: /<command>\n")
						.close();
				} catch (IOException e) { }
			}
		}

		return true;
	}

}
