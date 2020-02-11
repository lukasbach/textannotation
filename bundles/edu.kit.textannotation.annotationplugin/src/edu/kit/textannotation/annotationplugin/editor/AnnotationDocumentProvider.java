package edu.kit.textannotation.annotationplugin.editor;

import edu.kit.textannotation.annotationplugin.textmodel.AnnotationSet;
import edu.kit.textannotation.annotationplugin.textmodel.InvalidFileFormatException;
import edu.kit.textannotation.annotationplugin.textmodel.TextModelData;
import edu.kit.textannotation.annotationplugin.textmodel.xmlinterface.TextModelDataXmlInterface;
import edu.kit.textannotation.annotationplugin.utils.EclipseUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;

import edu.kit.textannotation.annotationplugin.utils.EventManager;

import javax.xml.parsers.ParserConfigurationException;

public class AnnotationDocumentProvider extends FileDocumentProvider {
	private TextModelData textModelData;
	private TextModelDataXmlInterface textModelDataXmlInterface = new TextModelDataXmlInterface();

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

		String saveData = textModelDataXmlInterface.buildXml(textModelData);
		super.doSaveDocument(monitor, element, new Document(saveData), overwrite);
	}
	

	@Override
	protected boolean setDocumentContent(IDocument document, IEditorInput editorInput, String encoding) throws CoreException {
		boolean result = super.setDocumentContent(document, editorInput, encoding);
		
		if (result) {
			try {
				textModelData = textModelDataXmlInterface.parseXml(document.get());

				// Mark document as dirty after changing the profile
				textModelData.onChangeProfileName.addListener(profile -> document.set(document.get()));

				document.set(textModelData.getDocument().get());
				initializeEvent.fire(new InitializeEvent(textModelData));
			} catch (InvalidFileFormatException e) {
				e.printStackTrace();
				EclipseUtils.reportError("File not properly formatted. " + e.getMessage());
			}
			
			return true;
		} else {
			return false;
		}
	}
}
