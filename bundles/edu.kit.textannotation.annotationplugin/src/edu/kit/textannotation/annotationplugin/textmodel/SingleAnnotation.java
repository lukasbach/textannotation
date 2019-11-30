package edu.kit.textannotation.annotationplugin.textmodel;

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

	public String getAnnotationIdentifier() {
		return annotationIdentifier;
	}

	public String[] getReferences() {
		return references;
	}
}
