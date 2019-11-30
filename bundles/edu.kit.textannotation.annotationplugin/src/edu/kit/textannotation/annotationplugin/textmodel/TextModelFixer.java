package edu.kit.textannotation.annotationplugin.textmodel;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;

import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;

public class TextModelFixer {
	private IDocument doc;
	private TextModelIntegration tmi;
	private int oldDocumentLength;
	
	public TextModelFixer(int oldDocumentLength) {
		this.oldDocumentLength = oldDocumentLength;
	}
	
	public void onChange(DocumentEvent event) {
		doc = event.getDocument();
		tmi = new TextModelIntegration(doc);

		int docLength = doc.getLength();
		int eventLength = docLength - oldDocumentLength;
		oldDocumentLength = docLength;		
		
		int eventStart = event.getOffset();
		// int eventLength = event.getText().length();
		AnnotationProfile profile = tmi.parseAnnotationProfile();
		SingleAnnotation[] annotations = tmi.parseAnnotationData();
		
		for (SingleAnnotation annotation: annotations) {
			System.out.println(String.format("annotationOffset=%s, eventStart=%s, eventLength=%s", annotation.getOffset(), eventStart, eventLength));
			if (annotation.getOffset() >= eventStart) {
				annotation.addOffset(eventLength);
			} else if (annotation.getOffset() < eventStart && annotation.getOffset() + annotation.getLength() >= eventStart) {
				annotation.addLength(eventLength);
			} else {
				// NOOP
			}
		}
		
		tmi.updateTextModel(annotations, profile);
	}
}
