package edu.kit.textannotation.annotationplugin.textmodel;

import org.eclipse.jface.text.*;
import org.eclipse.jface.text.presentation.*;

import edu.kit.textannotation.annotationplugin.profile.AnnotationClass;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AnnotatedTextPresentationReconciler implements IPresentationReconciler, IPresentationReconcilerExtension {
	
	private AnnotationProfile profile;
	private AnnotationSet annotations;

	private ITextListener textListener;
	private ITextViewer textViewer;

	private class TextListener implements ITextListener {
		@Override
		public void textChanged(TextEvent event) {
			List<SingleAnnotation> annotationList = annotations
					.getAnnotations()
					.stream()
					.sorted(Comparator.comparingInt(SingleAnnotation::getOffset))
					.collect(Collectors.toList());

			for (SingleAnnotation an: annotationList) {
				try {
					AnnotationClass ac = profile.getAnnotationClass(an.getAnnotationIdentifier());

					textViewer.setTextColor(ac.getColor(), an.getOffset(), an.getLength(), true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public AnnotatedTextPresentationReconciler() {}

	@Override
	public void install(ITextViewer viewer) {
		textListener = new TextListener();
		textViewer = viewer;
		textViewer.addTextListener(textListener);
	}

	@Override
	public void uninstall() {
		textViewer.removeTextListener(textListener);
	}

	@Override
	public IPresentationDamager getDamager(String contentType) {
		return null;
	}

	@Override
	public IPresentationRepairer getRepairer(String contentType) {
		return null;
	}

	@Override
	public String getDocumentPartitioning() {
		return null;
	}
    
    public void setAnnotationInformation(AnnotationProfile profile, AnnotationSet annotations) { // TODO use TextModelData
    	this.profile = profile;
    	this.annotations = annotations;
    }
}