package edu.kit.textannotation.annotationplugin.textmodel;

import java.util.Arrays;

import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import edu.kit.textannotation.annotationplugin.profile.AnnotationClass;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;

public class TextModelParser {
	private IDocument document;
	
	public TextModelParser(IDocument document) {
		this.document = document;
	}
	
	public SingleAnnotation[] parseAnnotationData() {
		// uuid,offset,length,annotationname,reference,reference,reference;
		System.out.println("!!!!!" + document
					.get()
					.split("\n", 3)[1]);
		return Arrays.asList(
					document
					.get()
					.split("\n", 3)[1]
							.split(";"))
				.stream()
				.map(v -> v.split(",", 5))
				.map(v -> new SingleAnnotation(
						v[0], 
						Integer.parseInt(v[1]) + this.getAnnotationDataLength(), 
						Integer.parseInt(v[2]),
						v[3], 
						v.length > 4 ? v[4].split(",") : new String[0]
						))
				.toArray(size -> new SingleAnnotation[size]);
	}
	
	public AnnotationProfile parseAnnotationProfile() {
		// annotationname,color,possibleMatch,possibleMatch;
		// TODO add matchings
		AnnotationClass[] annotationClasses = Arrays.asList(
					document
					.get()
					.split("\n", 2)[0]
							.split(";"))
				.stream()
				.map(v -> v.split(",", 3))
				.map(v -> new AnnotationClass(
						v[0], 
						new Color(Display.getCurrent(), new RGB(128,128,128))
						))
				.toArray(size -> new AnnotationClass[size]);
		
		return new AnnotationProfile(annotationClasses);
	}
	
	public int getAnnotationDataLength() {
		String[] lines = document.get().split("\n");
		return lines[0].length() + lines[1].length();
	}
}
