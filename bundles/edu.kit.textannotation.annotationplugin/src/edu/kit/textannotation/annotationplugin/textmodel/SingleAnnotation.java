package edu.kit.textannotation.annotationplugin.textmodel;

import edu.kit.textannotation.annotationplugin.EventManager;
import edu.kit.textannotation.annotationplugin.profile.MetaDataContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class SingleAnnotation {
	private String id;
	private int offset;
	private int length;
	private String annotationIdentifier;
	private String[] references;

	public final EventManager<EventManager.EmptyEvent> onLocationChange =
			new EventManager<>("singleAnnotation:locationChange");
	public final MetaDataContainer metaData = new MetaDataContainer();


	public SingleAnnotation(String id, int offset, int length, String annotationIdentifier, String[] references) {
		this.id = id;
		this.offset = offset;
		this.length = length;
		this.annotationIdentifier = annotationIdentifier; // TODO rename to annotationclass
		this.references = references;
	}

	public String getId() {
		return id;
	}

	public int getOffset() {
		return offset;
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
		onLocationChange.fire(new EventManager.EmptyEvent());
	}
	
	public void addOffset(int offset) {
		this.offset += offset;
		onLocationChange.fire(new EventManager.EmptyEvent());
	}

	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
		onLocationChange.fire(new EventManager.EmptyEvent());
	}
	
	public void addLength(int length) {
		this.length += length;
		onLocationChange.fire(new EventManager.EmptyEvent());
	}

	public boolean containsPosition(int pos) {
		return getStart() <= pos && getEnd() >= pos;
	}

	public boolean isContainedWithin(int start, int end) {
		return start <= getStart() && end >= getEnd();
	}

	public void setStart(int start) {
		setOffset(start);
	}

	public void setEnd(int end) {
		setLength(end - getStart() + 1);
	}

	public int getStart() {
		return getOffset();
	}

	public int getEnd() {
		return getOffset() + getLength() - 1;
	}


	public String getAnnotationIdentifier() {
		return annotationIdentifier;
	}

	public String[] getReferences() {
		return references;
	}
	
	public String toString() {
		return String.format("SingleAnnotation(annotation=%s, offset=%s, length=%s)", getAnnotationIdentifier(), getOffset(), getLength());
	}
}
