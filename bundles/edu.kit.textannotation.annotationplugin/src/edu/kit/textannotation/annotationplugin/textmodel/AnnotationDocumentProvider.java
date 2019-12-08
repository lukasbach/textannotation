package edu.kit.textannotation.annotationplugin.textmodel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.texteditor.AbstractDocumentProvider;

import edu.kit.textannotation.annotationplugin.EventManager;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;

public class AnnotationDocumentProvider extends FileDocumentProvider {
	private TextModelIntegration tmi;
	private AnnotationSet annotationSet;
	private AnnotationProfile annotationProfile;
	
	public class InitializeEvent {
		public AnnotationData annotationData;
		public IDocument document;
		
		public InitializeEvent(AnnotationData annotationData, IDocument document) {
			this.annotationData = annotationData;
			this.document = document;
		}
	}
	
	public final EventManager<InitializeEvent> initializeEvent = new EventManager<InitializeEvent>();
	
	@Override
	protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite)
			throws CoreException {
		System.out.println("doSaveDocument: " + element.toString() + ", document: " + document.toString());
		String oldContent = document.get();

		// TODO flashing when saving could probably be prevented by cloning document and adapting the clone instead of changing the original document, saving and changing it back.
		addDataToDoc(document);
		
		super.doSaveDocument(monitor, element, document, overwrite);
		
		document.set(oldContent);
	}
	

	@Override
	protected boolean setDocumentContent(IDocument document, IEditorInput editorInput, String encoding) throws CoreException {
		boolean result = super.setDocumentContent(document, editorInput, encoding);
		
		if (result) {
			tmi = new TextModelIntegration(document);
			annotationSet = new AnnotationSet(tmi.parseAnnotationData());
			annotationProfile = tmi.parseAnnotationProfile();
			removeDataFromDocument(document);
			
			initializeEvent.fire(new InitializeEvent(new AnnotationData(annotationSet, annotationProfile), document));
			
			return true;
		} else {
			return false;
		}
	}
	
	private void removeDataFromDocument(IDocument doc) {
		String[] lines = doc.get().split("\n");
		String[] outLines = new String[lines.length - 2];
		for (int i = 0; i < lines.length - 2; i++) {
			outLines[i] = lines[i + 2];
		}
		doc.set(Arrays.asList(outLines).stream().collect(Collectors.joining("\n")));
	}
	
	private void addDataToDoc(IDocument doc) {
		doc.set(tmi.buildAnnotationPrefix(annotationSet, annotationProfile) + doc.get());
	}

	public List<SingleAnnotation> getAnnotationData() {
		return annotationSet.getAnnotations();
	}

	public AnnotationProfile getAnnotationProfile() {
		return annotationProfile;
	}
}
