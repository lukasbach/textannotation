package edu.kit.textannotation.annotationplugin.profile;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.internal.ide.registry.AskUserViaPopupUnassociatedEditorStrategy;

public class AnnotationClass {

	private String name;
	private Color color;
	private List<AnnotationClass> possibleMatchings; 
	
	public AnnotationClass(String name, Color color) {
		this.name = name;
		this.color = color;
		this.possibleMatchings = new LinkedList<AnnotationClass>();
	}
	
	public AnnotationClass(String name, Color color, List<AnnotationClass> possibleMatchings) {
		this.name = name;
		this.color = color;
		this.possibleMatchings = possibleMatchings;
	}
	
	public AnnotationClass(String name, Color color, AnnotationClass[] possibleMatchings) {
		this(name, color, Arrays.asList(possibleMatchings));
	}
	
	public String getName() {
		return name;
	}
	
	public Color getColor() {
		return color;
	}
	
	public String getColorAsTextModelString() {
		return String.format("%s-%s-%s", color.getRed(), color.getGreen(), color.getBlue());
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
		this.possibleMatchings.removeIf(m -> m.getName().equals(matchingName));
	}
}
