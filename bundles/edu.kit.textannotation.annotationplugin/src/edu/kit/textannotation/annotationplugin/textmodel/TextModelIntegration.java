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
						new Color(Display.getCurrent(), new RGB(128,128,128))
						))
				.toArray(size -> new AnnotationClass[size]);
		
		return new AnnotationProfile(annotationClasses);
	}
	
	public void updateTextModel(SingleAnnotation[] annotations, AnnotationProfile profile) {
		System.out.println(this.document.get());
		System.out.println("\n\n changed to: \n\n");
		String[] lines = this.document.get().split("\n");
		lines[0] = profile
				.getAnnotationClasses()
				.stream()
				.map(ac -> String.format("%s,%s", ac.getName(), ac.getColorAsTextModelString()))
				.collect(Collectors.joining(";"));
		lines[1] = Arrays.asList(annotations)
				.stream()
				// TODO should not use getAnnotationDataLength here, as annotationData is about to be changed and is likely wrong here.
				.map(a -> String.format("%s,%s,%s,%s", a.getId(), a.getOffset(), a.getLength(), a.getAnnotationIdentifier()))
				.collect(Collectors.joining(";"));
		System.out.println(Arrays.asList(lines).stream().collect(Collectors.joining("\n")) +"\n\n");
		this.document.set(Arrays.asList(lines).stream().collect(Collectors.joining("\n")));
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
}