	package edu.kit.textannotation.annotationplugin.textmodel;

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import edu.kit.textannotation.annotationplugin.profile.AnnotationClass;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationRepairer;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.TextViewer;

public class ProjectPresentationReconciler extends PresentationReconciler {
	
	private AnnotationProfile profile;
	private AnnotationSet annotations;
    
    private class CustomDamagerRepairer implements IPresentationDamager, IPresentationRepairer  {
    	private IDocument document;
    	private IPresentationDamager damager = this;
    	private IPresentationRepairer repairer = this;
    	private TextModelIntegration parser;
    	
		@Override
		public void setDocument(IDocument document) {
			System.out.println("Reset document");
			this.document = document;
			this.parser = new TextModelIntegration(document);
		}

		@Override
		public IRegion getDamageRegion(ITypedRegion partition, DocumentEvent event,
				boolean documentPartitioningChanged) {
			/*
			 * event.getOffset: total offset where change happened
			 * event.getLength: 
			 */
			
			// System.out.println("offset" + event.getOffset() + " length" + event.getLength() + " text" + event.getText() + " plen" + partition.getLength() + " poff" + partition.getOffset());
			// System.out.println("Marking as damaged: " + );
			// return partition;
			// TODO damaged region detected is currently very buggy...
			return new IRegion() {
				@Override public int getOffset() {
					return 0;
				}
				@Override public int getLength() {
					return document.getLength();
				}
			};
 		}

		@Override
		public void createPresentation(TextPresentation presentation, ITypedRegion damage) {
			System.out.println("Document: " + document.get());
			System.out.println("Creating presentation with the following annotations:");
			List<SingleAnnotation> annotationList = annotations
					.getAnnotations()
					.stream()
					.sorted((SingleAnnotation a, SingleAnnotation b) -> {
						return a.getOffset() - b.getOffset();
					})
					// TODO make sure that annotations dont overlap. Probably better to make sure during annotation creation
					.collect(Collectors.toList());
			
			for (SingleAnnotation an: annotationList) {
				try {
					AnnotationClass ac = profile.getAnnotationClass(an.getAnnotationIdentifier());
					System.out.println("Annotation: " + ac.toString() + ", offset=" + an.getOffset() + ", len=" + an.getLength());
					presentation.addStyleRange(new StyleRange(
							an.getOffset(), 
							an.getLength(), 
							ac.getColor(), 
							new Color(Display.getCurrent(), new RGB(255, 255, 255))
							));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.print("\n\n");
		}
    	
    }
    
    public ProjectPresentationReconciler() {
        CustomDamagerRepairer dr = new CustomDamagerRepairer();
        this.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        this.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
    }
    
    public void setAnnotationInformation(AnnotationProfile profile, AnnotationSet annotations) {
    	this.profile = profile;
    	this.annotations = annotations;
    }
}