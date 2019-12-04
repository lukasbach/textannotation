package edu.kit.textannotation.annotationplugin.editor;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import edu.kit.textannotation.annotationplugin.ValidatorDocumentSetupParticipant;
import edu.kit.textannotation.annotationplugin.ValidatorDocumentSetupParticipant.DocumentValidator;
import edu.kit.textannotation.annotationplugin.textmodel.AnnotationData;
import edu.kit.textannotation.annotationplugin.textmodel.AnnotationDocumentProvider;
import edu.kit.textannotation.annotationplugin.textmodel.AnnotationSetFixer;
import edu.kit.textannotation.annotationplugin.textmodel.ProjectPresentationReconciler;

public class AnnotationTextEditor extends AbstractTextEditor {
	private ProjectPresentationReconciler presentationReconciler;
	private AnnotationDocumentProvider documentProvider;
	private AnnotationData annotationData;
	private AnnotationSetFixer annotationFixer;
	
	public AnnotationTextEditor() {
		documentProvider = new AnnotationDocumentProvider();
		documentProvider.initializeEvent.addListener(e -> {
			annotationData = e.annotationData;
			annotationFixer = new AnnotationSetFixer(e.annotationData.annotations, e.document.getLength());
			presentationReconciler.setAnnotationInformation(e.annotationData.profile, e.annotationData.annotations);
		});
		
        setDocumentProvider(documentProvider);
        
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
    	System.out.println("DOCUMENT:" + sv.getDocument());
    	
		sv.addTextListener(new ITextListener() {	
			@Override
			public void textChanged(TextEvent event) {
				DocumentEvent de = event.getDocumentEvent();
				if (de != null) {
					annotationFixer.applyEditEvent(de);
					// dv.documentChanged(event.getDocumentEvent());
				}
				
				// sv.setVisibleRegion(2, 5);
				System.out.println(event.getText() + event.getOffset() + ":" + event.getLength());
				
			}
		});
    }

	public AnnotationData getAnnotationData() {
		return annotationData;
	}
    
    
    
    
}
