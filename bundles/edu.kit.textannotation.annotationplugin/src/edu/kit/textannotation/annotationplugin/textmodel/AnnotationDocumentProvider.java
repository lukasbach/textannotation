package edu.kit.textannotation.annotationplugin.textmodel;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.texteditor.AbstractDocumentProvider;

import edu.kit.textannotation.annotationplugin.EventManager;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

public class AnnotationDocumentProvider extends FileDocumentProvider {
	private TextModelIntegration tmi;
	private AnnotationSet annotationSet;
	private AnnotationProfile annotationProfile;
	
	public static class InitializeEvent {
		public AnnotationData annotationData;
		public IDocument document;
		
		InitializeEvent(AnnotationData annotationData, IDocument document) {
			this.annotationData = annotationData;
			this.document = document;
		}
	}
	
	public final EventManager<InitializeEvent> initializeEvent = new EventManager<>();
	
	@Override
	protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite)
			throws CoreException {
		System.out.println("doSaveDocument: " + element.toString() + ", document: " + document.get());

		try {
			String saveData = TextModelIntegration.buildAnnotationXml(annotationProfile, annotationSet, document.get(), true);
			super.doSaveDocument(monitor, element, new Document(saveData), overwrite);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	protected boolean setDocumentContent(IDocument document, IEditorInput editorInput, String encoding) throws CoreException {
		boolean result = super.setDocumentContent(document, editorInput, encoding);
		
		if (result) {
			tmi = new TextModelIntegration(document);

			try {
				annotationSet = new AnnotationSet(TextModelIntegration.parseAnnotationData(document.get()));
				annotationProfile = TextModelIntegration.parseAnnotationProfile(document.get());
				document.set(TextModelIntegration.parseContent(document.get()));
			} catch (ParserConfigurationException | IOException | SAXException e) {
				e.printStackTrace();
			}

			initializeEvent.fire(new InitializeEvent(new AnnotationData(annotationSet, annotationProfile), document));
			
			return true;
		} else {
			return false;
		}
	}
}
