package edu.kit.textannotation.annotationplugin.textmodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.DocumentEvent;

public class AnnotationSet {
	private List<SingleAnnotation> annotations;
	
	public AnnotationSet(SingleAnnotation[] annotations) {
		this.annotations = Arrays.asList(annotations);
	}
	
	public AnnotationSet(List<SingleAnnotation> annotations) {
		this.annotations = annotations;
	}

	public List<SingleAnnotation> getAnnotations() {
		return annotations;
	}
	
	@Override
	public String toString() {
		return "AnnotationSet(" + annotations.size() + ")";
	}
}
