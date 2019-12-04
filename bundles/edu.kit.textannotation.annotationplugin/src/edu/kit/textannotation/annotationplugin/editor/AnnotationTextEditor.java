package edu.kit.textannotation.annotationplugin.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IElementStateListener;

import edu.kit.textannotation.annotationplugin.ProjectReconciler;
import edu.kit.textannotation.annotationplugin.ValidatorDocumentSetupParticipant;
import edu.kit.textannotation.annotationplugin.ValidatorDocumentSetupParticipant.DocumentValidator;
import edu.kit.textannotation.annotationplugin.textmodel.ProjectPresentationReconciler;

public class AnnotationTextEditor extends AbstractTextEditor {
	private PresentationReconciler presentationReconciler;
	
	public AnnotationTextEditor() {
        setDocumentProvider(new FileDocumentProvider()); // TODO custom extend DocumentProvider
        
        this.presentationReconciler = new ProjectPresentationReconciler();
	}
	
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
    	super.init(site, input);
        setInput(input);

        setSourceViewerConfiguration(new SourceViewerConfiguration() {
        	@Override
        	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
        		prepareSourceViewer(sourceViewer);
        		return presentationReconciler;
        	}
        });
    }
    
    private void prepareSourceViewer(ISourceViewer sv) {
    	// sv.getDocument().addDocumentListener(new ValidatorDocumentSetupParticipant.DocumentValidator());
    	DocumentValidator dv = new ValidatorDocumentSetupParticipant.DocumentValidator();
    	
		sv.addTextListener(new ITextListener() {	
			@Override
			public void textChanged(TextEvent event) {
				DocumentEvent de = event.getDocumentEvent();
				if (de != null) {
					dv.documentChanged(event.getDocumentEvent());
				}
				
				// sv.setVisibleRegion(2, 5);
				System.out.println(event.getText() + event.getOffset() + ":" + event.getLength());
				
			}
		});
    }
    
    
    
    
}
