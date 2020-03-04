package edu.kit.textannotation.annotationplugin.profile;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An annotation profile is defined in its specific file and referenced by an annotated text file, i.e.
 * a profile can be used by several annotation text files.
 *
 * It contains information about available annotation classes and is usually loaded directly from disk.
 * The resolution of profile information is done by the {@link AnnotationProfileRegistry}.
 *
 * @see AnnotationProfileRegistry
 * @see AnnotationClass
 */
public class AnnotationProfile {
	private List<AnnotationClass> annotationClasses;
	private String name;

	/**
	 * Create a new annotation profile with a name.
	 * @param name is used to identify the annotation profile against the user. It is also used to reference
	 *             the profile from within an annotation text file.
	 */
	public AnnotationProfile(String name) {
		this.setName(name);
		this.annotationClasses = new LinkedList<>();
	}

	/** Add the supplied annotation class to this profile. */
	public void addAnnotationClass(AnnotationClass ac) {
		this.annotationClasses.add(ac);
	}

	/** Remove the supplied annotation class from this profile.
	 * All classes with the same name as the supplied one are removed. */
	public void removeAnnotationClass(AnnotationClass ac) {
		this.annotationClasses.removeIf(c -> c.getName().equals(ac.getName()));
	}

	public List<AnnotationClass> getAnnotationClasses() {
		return annotationClasses;
	}

	/**
	 * Resolve the annotation class instance with the supplied annotation class name.
	 * @param name the name of the annotation class that is being resolved.
	 * @return the annotation class if it could be resolved.
	 * @throws Exception if the profile does not store an annotation class with the supplied name.
	 */
	public AnnotationClass getAnnotationClass(String name) throws Exception {
		for (AnnotationClass ac: annotationClasses) {
			if (ac.getName().equals(name.replace("\n", "").replace("\r", ""))) {
				return ac;
			}
		}
		
		// TODO custom exception
		throw new Exception("Could not find annotation class " + name + ", available classes are " 
		  + annotationClasses.stream().map(AnnotationClass::getName).collect(Collectors.joining(", ")));
	}

	/** Return the names of all available annotation classes that are stored in this profile. */
	public String[] getAnnotationClassNames() {
		return annotationClasses.stream().map(AnnotationClass::getName).toArray(String[]::new);
	}

	/** Remove the annotation with the supplied name, and replace it with the altered annotation class. */
	public void alterAnnotationClass(String oldName, AnnotationClass alteredClass) {
		for (int i = 0; i < this.annotationClasses.size(); i++) {
			if (this.annotationClasses.get(i).getName() == oldName) {
				this.annotationClasses.set(i, alteredClass);
				return;
			}
		}
	}

	/**
	 * Get the name of the annotation profile. The name is used to identify the annotation profile against the user.
	 * It is also used to reference the profile from within an annotation text file.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the profile. See the documentation of the getter for more details.
	 * @see AnnotationProfile::getName
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof AnnotationProfile) && ((AnnotationProfile)obj).getName().equals(getName());
	}

	@Override
	public String toString() {
		return getName();
	}
}
