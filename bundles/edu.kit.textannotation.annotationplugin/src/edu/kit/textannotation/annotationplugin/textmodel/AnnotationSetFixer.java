package edu.kit.textannotation.annotationplugin.textmodel;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;

public class AnnotationSetFixer {
	private AnnotationSet annotations;
	private int oldDocumentLength;
	private boolean isFeedbackEvent;

	public AnnotationSetFixer(AnnotationSet annotations, int initialDocumentLength) {
		this.annotations = annotations;
		this.oldDocumentLength = initialDocumentLength;
		this.isFeedbackEvent = false;
	}
	
	public void applyEditEvent(DocumentEvent e) {
		if (isFeedbackEvent) {
			isFeedbackEvent = false;
			return;
		}
		
		IDocument doc = e.getDocument();
		int docLength = doc.getLength();
		int eventStart = e.getOffset();
		int eventLength = docLength - oldDocumentLength;
		oldDocumentLength = docLength;		
		
		for (SingleAnnotation annotation: annotations.getAnnotations()) {
			System.out.println(String.format("annotationOffset=%s, eventStart=%s, eventLength=%s", annotation.getOffset(), eventStart, eventLength));
			if (annotation.getOffset() >= eventStart) {
				annotation.addOffset(eventLength);
			} else if (annotation.getOffset() < eventStart && annotation.getOffset() + annotation.getLength() >= eventStart) {
				annotation.addLength(eventLength);
			} else {
				// NOOP
			}
		}
		
		// Change document model to trigger syntax rehighlighting
		// use isFeedbackEvent to prevent infinite loop
		isFeedbackEvent = true;
		doc.set(doc.get());
	}
}
