package edu.kit.textannotation.annotationplugin.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import edu.kit.textannotation.annotationplugin.EclipseUtils;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfileRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import edu.kit.textannotation.annotationplugin.profile.AnnotationClass;
import edu.kit.textannotation.annotationplugin.textmodel.TextModelData;
import edu.kit.textannotation.annotationplugin.textmodel.AnnotationDocumentProvider;
import edu.kit.textannotation.annotationplugin.textmodel.AnnotationSetFixer;
import edu.kit.textannotation.annotationplugin.textmodel.ProjectPresentationReconciler;
import edu.kit.textannotation.annotationplugin.textmodel.SingleAnnotation;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

public class AnnotationTextEditor extends AbstractTextEditor {
	private ProjectPresentationReconciler presentationReconciler;
	private AnnotationDocumentProvider documentProvider;
	private TextModelData textModelData;
	private AnnotationSetFixer annotationFixer;
	private ISourceViewer sourceViewer;
	private String id;
	private AnnotationProfileRegistry registry;
	private Bundle bundle;
	private BundleContext bundleContext;
	
	public AnnotationTextEditor() {
		id = UUID.randomUUID().toString();
		documentProvider = new AnnotationDocumentProvider();
		documentProvider.initializeEvent.addListener(e -> {
			textModelData = e.textModelData;
			annotationFixer = new AnnotationSetFixer(e.textModelData.getAnnotations(), e.textModelData.getDocument().getLength());
			presentationReconciler.setAnnotationInformation(registry.findProfile(e.textModelData.getProfileName()), e.textModelData.getAnnotations());
		});
		
        setDocumentProvider(documentProvider);
        
        this.presentationReconciler = new ProjectPresentationReconciler();

		bundle = FrameworkUtil.getBundle(this.getClass());
		bundleContext = bundle.getBundleContext();

		initRegistry();
	}

	private void initRegistry() {
		List<String> paths = new ArrayList<>();
		// paths.add(System.getProperty("user.dir") + "/.textannotation"); // userdir/.textannotation
		paths.add(Platform.getStateLocation(bundle).toString() + "/profiles"); // workspace/.metadata/.textannotation
		paths.add(Objects.requireNonNull(EclipseUtils.getCurrentProjectDirectory()).toString()); // workspace/project/
		registry = new AnnotationProfileRegistry(paths);
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

        	@Override
			public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
				return new HoverProvider(textModelData);
			}
        });
    }

	public TextModelData getTextModelData() {
		return textModelData;
	}
	
	public void annotate(AnnotationClass annotationClass) {
		Point p = sourceViewer.getSelectedRange();
		int offset = p.x;
		int length = p.y;
		
		SingleAnnotation annotation = new SingleAnnotation(UUID.randomUUID().toString(), 
				offset, length, annotationClass.getName(), new String[0]);
		System.out.println("Annotating: " + annotation.toString());
		textModelData.getAnnotations().addAnnotation(annotation);
		
		// Trigger rehighlight
		IDocument doc = sourceViewer.getDocument();
		doc.set(doc.get());
	}
	
	public String getId() {
		return id;
	}
    
    private void prepareSourceViewer(ISourceViewer sv) {
    	sourceViewer = sv;
    	
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

	public AnnotationProfileRegistry getAnnotationProfileRegistry() {
		return registry;
	}
}
