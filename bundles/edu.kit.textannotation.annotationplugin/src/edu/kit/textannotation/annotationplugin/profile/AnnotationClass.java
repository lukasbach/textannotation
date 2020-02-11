package edu.kit.textannotation.annotationplugin.profile;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import edu.kit.textannotation.annotationplugin.utils.XmlNodeWrapper;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import static edu.kit.textannotation.annotationplugin.textmodel.XmlSchemaVariables.*;

public class AnnotationClass {

	private String name;
	private Color color;
	private List<AnnotationClass> possibleMatchings;
	private @Nullable String description;

	public final MetaDataContainer metaData;

	public AnnotationClass(String name, Color color, @Nullable String description,
						   List<AnnotationClass> possibleMatchings) {
		this.name = name;
		this.color = color;
		this.description = description;
		this.possibleMatchings = possibleMatchings;
		this.metaData = new MetaDataContainer();
	}
	
	public AnnotationClass(String name, Color color) {
		this.name = name;
		this.color = color;
		this.description = null;
		this.possibleMatchings = new LinkedList<>();
		this.metaData = new MetaDataContainer();
	}
	
	public String getName() {
		return name;
	}
	
	public Color getColor() {
		return color;
	}
	
	public String getColorAsTextModelString() {
		return String.format("%s, %s, %s", color.getRed(), color.getGreen(), color.getBlue());
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
	
	@Override
	public String toString() {
		return String.format("AnnotationClass(%s, %s)", name, this.getColorAsTextModelString());
	}

	public String getDescription() {
		return description == null ? "" : description;
	}

	public void setDescription(@Nullable String description) {
		this.description = description;
	}
}
