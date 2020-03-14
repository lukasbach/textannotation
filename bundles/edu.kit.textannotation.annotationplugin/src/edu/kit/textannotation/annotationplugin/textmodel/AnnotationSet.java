package edu.kit.textannotation.annotationplugin.textmodel;

import edu.kit.textannotation.annotationplugin.utils.EclipseUtils;

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
	 * Add an annotation instance to this set instance. The annotation will only be added if it does not
	 * overlap with existing annotations in the annotationset.
	 * @param annotation the annotation to add to the set.
	 * @return true if the annotation was added, false if it was not due to overlaps.
	 */
	public boolean addAnnotation(SingleAnnotation annotation) {
		// Alternative to erroring on overlapping annotations:
		// trimAnnotation(annotation);

		if (checkOverlaps(annotation)) {
			EclipseUtils.reportError("The annotation overlaps with other annotations. No annotation was placed.");
			return false;
		}

		if (annotation.getLength() > 0) {
			this.annotations.add(annotation);
			return true;
		}
		
		return false;
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
				if (existingAnnotation.isContainedWithin(annotation.getStart(), annotation.getEnd())) {
					annotation.setEnd(existingAnnotation.getStart() - 1);
				}
				if (existingAnnotation.containsPosition(annotation.getStart())) {
					annotation.setStart(existingAnnotation.getEnd() + 1);
				}
				if (existingAnnotation.containsPosition(annotation.getEnd())) {
					annotation.setEnd(existingAnnotation.getStart() - 1);
				}
			}
		}
	}

	/**
	 * Check whether the annotation overlaps with existing annotations in this annotation set.
	 */
	private boolean checkOverlaps(SingleAnnotation annotation) {
		for (SingleAnnotation existingAnnotation: annotations) {
			if (annotation.getLength() > 0) {
				if (existingAnnotation.isContainedWithin(annotation.getStart(), annotation.getEnd())) {
					return true;
				}
				if (existingAnnotation.containsPosition(annotation.getStart())) {
					return true;
				}
				if (existingAnnotation.containsPosition(annotation.getEnd())) {
					return true;
				}
			}
		}

		return false;
	}
}
