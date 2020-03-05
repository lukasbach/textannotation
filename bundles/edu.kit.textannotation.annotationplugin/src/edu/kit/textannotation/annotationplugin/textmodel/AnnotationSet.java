package edu.kit.textannotation.annotationplugin.textmodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * This class is a wrapper for a set of {@link SingleAnnotation} instances. It additionally makes
 * sure that the contained annotations do not overlap with each another.
 *
 * @see AnnotationSet::addAnnotation
 */
public class AnnotationSet {
	private List<SingleAnnotation> annotations;

	/**
	 * Create a new annotation set with a predefined list of annotations.
	 * @param annotations initial annotations with which the annotation set will be filled.
	 */
	public AnnotationSet(SingleAnnotation[] annotations) {
		this.annotations = new ArrayList<SingleAnnotation>(Arrays.asList(annotations));
	}

	/**
	 * Create a new empty annotation set.
	 */
	public AnnotationSet() {
		this.annotations = new ArrayList<>();
	}

	/**
	 * Create a new annotation set with a predefined list of annotations.
	 * @param annotations initial annotations with which the annotation set will be filled.
	 */
	public AnnotationSet(List<SingleAnnotation> annotations) {
		this.annotations = annotations;
	}

	/** Return the contained annotations as list. */
	public List<SingleAnnotation> getAnnotations() {
		return annotations;
	}

	/** Return the contained annotations as stream. */
	public Stream<SingleAnnotation> stream() {
		return this.getAnnotations().stream();
	}

	/**
	 * Add an annotation instance to this set instance. Note that the annotation might be "trimmed",
	 * i.e. if the annotation overlaps with existing annotations in this set, it will be cut short
	 * to not overlap, and then only be added if it keeps a positive length.
	 * @param annotation the annotation to add to the set.
	 */
	public void addAnnotation(SingleAnnotation annotation) {
		trimAnnotation(annotation);

		if (annotation.getLength() > 0) {
			this.annotations.add(annotation);
		}
	}

	/**
	 * Remove the specified annotation.
	 * @param annotation the annotation instance to remove.
	 */
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
