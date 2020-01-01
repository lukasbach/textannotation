package edu.kit.textannotation.annotationplugin.textmodel;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class SingleAnnotation {
	private String id;
	private int offset;
	private int length;
	private String annotationIdentifier;
	private String[] references;
	
	public SingleAnnotation(String id, int offset, int length, String annotationIdentifier, String[] references) {
		this.id = id;
		this.offset = offset;
		this.length = length;
		this.annotationIdentifier = annotationIdentifier;
		this.references = references;
	}

	static SingleAnnotation fromXmlNode(Node node) {
		NamedNodeMap attributes = node.getAttributes();

		return new SingleAnnotation(
				attributes.getNamedItem("id").getTextContent(),
				Integer.parseInt(attributes.getNamedItem("offset").getTextContent()),
				Integer.parseInt(attributes.getNamedItem("length").getTextContent()),
				attributes.getNamedItem("annotation").getTextContent(),
				new String[] {}
		);
	}

	public String getId() {
		return id;
	}

	public int getOffset() {
		return offset;
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public void addOffset(int offset) {
		this.offset += offset;
	}

	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public void addLength(int length) {
		this.length += length;
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
