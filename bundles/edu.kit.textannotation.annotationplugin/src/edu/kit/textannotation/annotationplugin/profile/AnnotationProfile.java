package edu.kit.textannotation.annotationplugin.profile;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class AnnotationProfile {
	private List<AnnotationClass> annotationClasses;
	private String name;
	
	public AnnotationProfile(String name) {
		this.setName(name);
		this.annotationClasses = new LinkedList<AnnotationClass>();
	}
	
	public AnnotationProfile(AnnotationClass[] annotationClasses) {
		this.setName("--internalProfile");
		this.annotationClasses = Arrays.asList(annotationClasses);
	}
	
	public void addAnnotationClass(AnnotationClass ac) {
		this.annotationClasses.add(ac);
	}
	
	public void removeAnnotationClass(AnnotationClass ac) {
		this.annotationClasses.removeIf(c -> c.getName().equals(ac.getName()));
	}
	
	public List<AnnotationClass> getAnnotationClasses() {
		return annotationClasses;
	}
	
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
	
	public String[] getAnnotationClassNames() {
		return annotationClasses.stream().map(AnnotationClass::getName).toArray(String[]::new);
	}
	
	public void alterAnnotationClass(String oldName, AnnotationClass alteredClass) {
		for (int i = 0; i < this.annotationClasses.size(); i++) {
			if (this.annotationClasses.get(i).getName() == oldName) {
				this.annotationClasses.set(i, alteredClass);
				return;
			}
		}
	}

	public String getName() {
		return name;
	}

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
