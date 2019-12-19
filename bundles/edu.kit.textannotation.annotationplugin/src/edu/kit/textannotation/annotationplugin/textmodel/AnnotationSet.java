package edu.kit.textannotation.annotationplugin.textmodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class AnnotationSet {
	private List<SingleAnnotation> annotations;
	
	public AnnotationSet(SingleAnnotation[] annotations) {
		this.annotations = new ArrayList<SingleAnnotation>(Arrays.asList(annotations));
	}
	
	public AnnotationSet(List<SingleAnnotation> annotations) {
		this.annotations = annotations;
	}

	public List<SingleAnnotation> getAnnotations() {
		return annotations;
	}

	public Stream<SingleAnnotation> stream() {
		return this.getAnnotations().stream();
	}
	
	public void addAnnotation(SingleAnnotation annotation) {
		this.annotations.add(annotation);
	}
	
	@Override
	public String toString() {
		return "AnnotationSet(" + annotations.size() + ")";
	}
}
