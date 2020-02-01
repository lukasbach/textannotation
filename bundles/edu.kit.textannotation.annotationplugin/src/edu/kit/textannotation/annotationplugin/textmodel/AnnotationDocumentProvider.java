package edu.kit.textannotation.annotationplugin.textmodel;

import edu.kit.textannotation.annotationplugin.Activator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;

import edu.kit.textannotation.annotationplugin.EventManager;

import javax.xml.parsers.ParserConfigurationException;

public class AnnotationDocumentProvider extends FileDocumentProvider {
	private TextModelIntegration tmi;
	private TextModelData textModelData;

	public static class InitializeEvent {
		public TextModelData textModelData;

		InitializeEvent(TextModelData textModelData) {
			this.textModelData = textModelData;
		}
	}
	
	public final EventManager<InitializeEvent> initializeEvent = new EventManager<>();
	
	@Override
	protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite)
			throws CoreException {
		System.out.println("doSaveDocument: " + element.toString() + ", document: " + document.get());

		try {
			String saveData = TextModelIntegration.buildAnnotationXml(textModelData);
			super.doSaveDocument(monitor, element, new Document(saveData), overwrite);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	protected boolean setDocumentContent(IDocument document, IEditorInput editorInput, String encoding) throws CoreException {
		boolean result = super.setDocumentContent(document, editorInput, encoding);
		
		if (result) {
			tmi = new TextModelIntegration();

			try {
				textModelData = new TextModelData(
						new AnnotationSet(TextModelIntegration.parseAnnotationData(document.get())),
						TextModelIntegration.parseProfileName(document.get()),
						document
				);

				// Mark document as dirty after changing the profile
				textModelData.onChangeProfileName.addListener(profile -> document.set(document.get()));

				document.set(TextModelIntegration.parseContent(document.get()));
			} catch (SchemaValidator.InvalidFileFormatException e) {
				e.printStackTrace();
				throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
			}

			initializeEvent.fire(new InitializeEvent(textModelData));
			
			return true;
		} else {
			return false;
		}
	}
}
