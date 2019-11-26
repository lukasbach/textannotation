package edu.kit.textannotation.annotationplugin.profile;

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
			if (ac.getName().equals(name)) {
				return ac;
			}
		}
		
		// TODO custom exception
		throw new Exception("Could not find annotation class " + name);
	}
	
	public String[] getAnnotationClassNames() {
		return annotationClasses.stream().map(ac -> ac.getName()).toArray(size -> new String[size]);
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
}
