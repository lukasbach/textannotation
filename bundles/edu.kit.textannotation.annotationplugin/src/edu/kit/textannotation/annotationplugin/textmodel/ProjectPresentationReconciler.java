package edu.kit.textannotation.annotationplugin.textmodel;

import edu.kit.textannotation.annotationplugin.EventManager;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.presentation.*;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import edu.kit.textannotation.annotationplugin.profile.AnnotationClass;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.Token;

public class ProjectPresentationReconciler implements IPresentationReconciler, IPresentationReconcilerExtension {
	
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

	public ProjectPresentationReconciler() {}

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