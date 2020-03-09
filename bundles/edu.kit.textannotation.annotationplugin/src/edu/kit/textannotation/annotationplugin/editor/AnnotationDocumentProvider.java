package edu.kit.textannotation.annotationplugin.editor;

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

/**
 * Document Provider for Annotatable Text Documents, supposed to used by {@link AnnotationTextEditor}.
 *
 * This class implements a Document Provider, but parses Annotated Text data from the file source and
 * supplies the text editor instance with the parsed annotation data and raw content. Saving the file
 * causes this provider to regenerate the annotatable text data tree and save that in XML form.
 *
 * @see TextModelDataXmlInterface
 */
public class AnnotationDocumentProvider extends FileDocumentProvider {
	private TextModelData textModelData;
	private TextModelDataXmlInterface textModelDataXmlInterface = new TextModelDataXmlInterface();

	/**
	 * This models a event that is dispatched when a new document is read causing the editor to initialize.
	 */
	public static class InitializeEvent {
		public final TextModelData textModelData;

		InitializeEvent(TextModelData textModelData) {
			this.textModelData = textModelData;
		}
	}

	/**
	 * Fires when a new document is read and the editor is initialized with the document.
	 */
	public final EventManager<InitializeEvent> onInitialize = new EventManager<>();
	
	@Override
	protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite)
			throws CoreException {
		EclipseUtils.logger().info("doSaveDocument: " + element.toString() + ", document: " + document.get());

		textModelData.setDocument(document);
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
				onInitialize.fire(new InitializeEvent(textModelData));
			} catch (InvalidFileFormatException e) {
				EclipseUtils.logger().error(e);
				EclipseUtils.reportError("File not properly formatted. " + e.getMessage());
			}
			
			return true;
		} else {
			return false;
		}
	}
}
