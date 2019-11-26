package edu.kit.textannotation.annotationplugin.profile;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Color;

public class AnnotationClass {

	private String name;
	private Color color;
	private List<AnnotationClass> possibleMatchings; 
	
	public AnnotationClass(String name, Color color) {
		this.name = name;
		this.color = color;
		this.possibleMatchings = new LinkedList<AnnotationClass>();
		
	}
	
	public String getName() {
		return name;
	}
	
	public Color getColor() {
		return color;
	}
	
	public List<AnnotationClass> getPossibleMatchings() {
		return possibleMatchings;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	public void addPossibleMatching(AnnotationClass matching) {
		this.possibleMatchings.add(matching);
	}
	
	public void removePossibleMatching(AnnotationClass matching) {
		this.removePossibleMatching(matching.getName());
	}
	
	public void removePossibleMatching(String matchingName) {
		this.possibleMatchings.removeIf(m -> m.getName() == matchingName);
	}
}
