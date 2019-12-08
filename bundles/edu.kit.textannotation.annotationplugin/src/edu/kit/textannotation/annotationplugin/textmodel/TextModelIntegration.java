package edu.kit.textannotation.annotationplugin.textmodel;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import edu.kit.textannotation.annotationplugin.profile.AnnotationClass;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;

public class TextModelIntegration {
	private IDocument document;
	
	public TextModelIntegration(IDocument document) {
		this.document = document;
	}
	
	public SingleAnnotation[] parseAnnotationData() {
		// uuid,offset,length,annotationname,reference,reference,reference;
		System.out.println("!!!!!" + document.get());
		return Arrays.asList(
					document
					.get()
					.split("\n", 3)[1]
							.split(";"))
				.stream()
				.map(v -> v.split(",", 5))
				.map(v -> new SingleAnnotation(
						v[0], 
						Integer.parseInt(v[1]), 
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
						parseColor(v[1])
						))
				.toArray(size -> new AnnotationClass[size]);
		
		return new AnnotationProfile(annotationClasses);
	}
	
	public String buildAnnotationPrefix(AnnotationSet annotations, AnnotationProfile profile) {
		return (
			profile
				.getAnnotationClasses()
				.stream()
				.map(ac -> String.format("%s,%s", ac.getName(), ac.getColorAsTextModelString()))
				.collect(Collectors.joining(";"))
			+ "\n" + 
			annotations.getAnnotations()
				.stream()
				// TODO should not use getAnnotationDataLength here, as annotationData is about to be changed and is likely wrong here.
				.map(a -> String.format("%s,%s,%s,%s", a.getId(), a.getOffset(), a.getLength(), a.getAnnotationIdentifier()))
				.collect(Collectors.joining(";"))
			+ "\n"
		);		
	}
	
	public int getAnnotationDataLength() {
		String[] lines = document.get().split("\n");
		return lines[0].length() + lines[1].length();
	}
	
	private Color parseColor(String colorString) {
		// colorString has the format 123-456-789
		Integer[] rgb = Arrays.asList(colorString.split("-")).stream().map(String::trim).map(Integer::parseInt).toArray(size -> new Integer[size]);
		return new Color(Display.getCurrent(), new RGB(rgb[0], rgb[1], rgb[2]));
		
	}
}
