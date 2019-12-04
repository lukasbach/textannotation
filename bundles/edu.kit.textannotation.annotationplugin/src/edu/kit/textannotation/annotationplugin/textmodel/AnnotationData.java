package edu.kit.textannotation.annotationplugin.textmodel;

import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;

public class AnnotationData {
	public AnnotationSet annotations;
	public AnnotationProfile profile;
	
	public AnnotationData(AnnotationSet annotations, AnnotationProfile profile) {
		this.annotations = annotations;
		this.profile = profile;
	}
	
	@Override
	public String toString() {
		return String.format("AnnotationData(%s, %s)", annotations.toString(), profile.toString());
	}
}
