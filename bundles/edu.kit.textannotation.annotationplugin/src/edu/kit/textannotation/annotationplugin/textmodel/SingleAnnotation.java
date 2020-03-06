package edu.kit.textannotation.annotationplugin.textmodel;

import edu.kit.textannotation.annotationplugin.utils.EventManager;
import edu.kit.textannotation.annotationplugin.profile.MetaDataContainer;

/**
 * This class models a specific annotation within an annotatable text file,
 * i.e. it marks a region and attaches annotation data to it. This annotation
 * maps to an annotation class, which is modelled by
 * {@link edu.kit.textannotation.annotationplugin.profile.AnnotationClass}.
 *
 * @see edu.kit.textannotation.annotationplugin.profile.AnnotationClass
 * @see TextModelData
 * @see AnnotationSet
 * @see AnnotationSetFixer
 */
public class SingleAnnotation {
	private String id;
	private int offset;
	private int length;
	private String annotationIdentifier;

	/** This event fires when the location of the region of the annotation changes. */
	public final EventManager<EventManager.EmptyEvent> onLocationChange =
			new EventManager<>("singleAnnotation:locationChange");

	/** Metadata attached to the specific annotation. */
	public final MetaDataContainer metaData = new MetaDataContainer();

	/**
	 * Create a new annotation.
	 * @param id a unique string that represents the annotation. May be random.
	 * @param offset where the annotation starts, relative to the beginning of the text document.
	 * @param length of the annotation
	 * @param annotationIdentifier the ID of the associated annotation class.
	 */
	public SingleAnnotation(String id, int offset, int length, String annotationIdentifier) {
		this.id = id;
		this.offset = offset;
		this.length = length;
		this.annotationIdentifier = annotationIdentifier; // TODO rename to annotationclass
	}

	/** Return the unique ID which identifies this annotation. */
	public String getId() {
		return id;
	}

	/** Return the offset, i.e. the location where the annotation region starts relative to the document start. */
	public int getOffset() {
		return offset;
	}

	/**
	 * Set the offset of the annotation.
	 * @see SingleAnnotation::getOffset
	 */
	public void setOffset(int offset) {
		this.offset = offset;
		onLocationChange.fire(new EventManager.EmptyEvent());
	}

	/**
	 * Add the supplied value to the offset of the annotation region.
	 * @see SingleAnnotation::getOffset
	 */
	public void addOffset(int offset) {
		this.offset += offset;
		onLocationChange.fire(new EventManager.EmptyEvent());
	}

	/**
	 * Return the length of the annotation region.
	 */
	public int getLength() {
		return length;
	}

	/**
	 * Set the length of the annotation region. Should be larger than zero.
	 * @see SingleAnnotation::getLength
	 */
	public void setLength(int length) {
		this.length = length;
		onLocationChange.fire(new EventManager.EmptyEvent());
	}

	/**
	 * Add the supplied value to the length of the annotation region
	 * @see SingleAnnotation::getLength
	 */
	public void addLength(int length) {
		this.length += length;
		onLocationChange.fire(new EventManager.EmptyEvent());
	}

	/**
	 * Check whether the supplied value marks a location in the text document that is contained
	 * within the annotation region.
	 * @param pos the position which is checked.
	 * @return true if the posiion is contained within the annotation region, false otherwise.
	 */
	public boolean containsPosition(int pos) {
		return getStart() <= pos && getEnd() >= pos;
	}

	/**
	 * Check whether the supplied values define a region, where both its start and end locations
	 * are contained within the annotation region.
	 * @param start the start location of the checked region.
	 * @param end the end location of the checked region.
	 * @return true if the region defined by the parameters is wholly contained within the region
	 * of the annotation.
	 * @see SingleAnnotation::containsPosition
	 */
	public boolean isContainedWithin(int start, int end) {
		return start <= getStart() && end >= getEnd();
	}

	/**
	 * Set the start of the annotation region. This is a alias for {@link SingleAnnotation::setOffset}
	 */
	public void setStart(int start) {
		setOffset(start);
	}

	/**
	 * Set the end of the annotation region. This adapts the length of the annotation.
	 */
	public void setEnd(int end) {
		setLength(end - getStart() + 1);
	}

	/**
	 * Return the location in the text document where the region of the annotation starts (inclusive).
	 */
	public int getStart() {
		return getOffset();
	}

	/**
	 * Return the location in the text document where the region of the annotation ends (inclusive).
	 */
	public int getEnd() {
		return getOffset() + getLength() - 1;
	}


	/**
	 * Return the identifier for the annotation, i.e. the ID of the annotation class which is
	 * referenced by this annotation.
	 * @see edu.kit.textannotation.annotationplugin.profile.AnnotationClass
	 */
	public String getAnnotationIdentifier() {
		return annotationIdentifier;
	}

	@Override
	public String toString() {
		return String.format("SingleAnnotation(annotation=%s, offset=%s, length=%s)", getAnnotationIdentifier(), getOffset(), getLength());
	}
}
