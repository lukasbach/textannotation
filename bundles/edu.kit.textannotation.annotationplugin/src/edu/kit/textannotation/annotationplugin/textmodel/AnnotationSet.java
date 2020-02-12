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

	public AnnotationSet() {
		this.annotations = new ArrayList<>();
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
		trimAnnotation(annotation);

		if (annotation.getLength() > 0) {
			this.annotations.add(annotation);
		}
	}

	public void removeAnnotation(SingleAnnotation annotation) {
		this.annotations.remove(annotation);
	}
	
	@Override
	public String toString() {
		return "AnnotationSet(" + annotations.size() + ")";
	}

	/**
	 * Make sure that the supplied annotation does not overlap with any annotations which exist
	 * in this annotation set by trimming its start and end.
	 */
	private void trimAnnotation(SingleAnnotation annotation) {
		for (SingleAnnotation existingAnnotation: annotations) {
			if (annotation.getLength() > 0) {
				if (existingAnnotation.containsPosition(annotation.getStart())) {
					annotation.setStart(existingAnnotation.getEnd() + 1);
				}
				if (existingAnnotation.containsPosition(annotation.getEnd())) {
					annotation.setEnd(existingAnnotation.getStart() - 1);
				}
				if (existingAnnotation.isContainedWithin(annotation.getStart(), annotation.getEnd())) {
					annotation.setEnd(existingAnnotation.getStart() - 1);
				}
			}
		}
	}
}
