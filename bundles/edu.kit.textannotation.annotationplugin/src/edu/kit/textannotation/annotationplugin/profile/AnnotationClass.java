package edu.kit.textannotation.annotationplugin.profile;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.graphics.Color;

/**
 * This models a annotation specification, denoted as "annotation class", i.e. multiple regions in a text
 * document may be annotated with the same annotation class.
 *
 * A annotation class mostly consists of a name, a description, a color and a metadata set.
 */
public class AnnotationClass {
	private String name;
	private Color color;
	private List<AnnotationClass> possibleMatchings;
	private @Nullable String description;

	/** The metadata for this annotation class. */
	public final MetaDataContainer metaData;

	/** Create a new annotation class with all available options. */
	public AnnotationClass(String name, Color color, @Nullable String description,
						   List<AnnotationClass> possibleMatchings) {
		this.name = name;
		this.color = color;
		this.description = description;
		this.possibleMatchings = possibleMatchings;
		this.metaData = new MetaDataContainer();
	}

	/** Create a new annotation class with its name and color. */
	public AnnotationClass(String name, Color color) {
		this.name = name;
		this.color = color;
		this.description = null;
		this.possibleMatchings = new LinkedList<>();
		this.metaData = new MetaDataContainer();
	}

	/** Return the name of the annotation class, which can be represented to the end user. */
	public String getName() {
		return name;
	}

	/**
	 * Return the color used to visualize the annotation. This color is used in the editor view for annotated text regions.
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Return the color in the form "rrr, ggg, bbb", each section refers to either the red, green blue part of the color,
	 * and each value is between 0 and 255.
	 */
	public String getColorAsTextModelString() {
		return String.format("%s, %s, %s", color.getRed(), color.getGreen(), color.getBlue());
	}
	
	public List<AnnotationClass> getPossibleMatchings() {
		return possibleMatchings;
	}

	/** Set the name of the annotation class. */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Set the color of the annotation class.
	 * @see AnnotationClass::getColor
	 * @see AnnotationClass::getColorAsTextModelString
	 */
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

	/** Get a textual description of the annotation class. */
	public String getDescription() {
		return description == null ? "" : description;
	}

	/**
	 * Set the textual description of the annotation class.
	 * @see AnnotationClass::getDescription
	 */
	public void setDescription(@Nullable String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return String.format("AnnotationClass(%s, %s)", name, this.getColorAsTextModelString());
	}
}
