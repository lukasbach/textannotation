package edu.kit.textannotation.annotationplugin.editor;

import java.util.UUID;

import edu.kit.textannotation.annotationplugin.EclipseUtils;
import edu.kit.textannotation.annotationplugin.EventManager;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfileRegistry;
import edu.kit.textannotation.annotationplugin.profile.ProfileNotFoundException;
import edu.kit.textannotation.annotationplugin.textmodel.*;
import edu.kit.textannotation.annotationplugin.views.AnnotationPerspective;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jgit.annotations.Nullable;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.*;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import edu.kit.textannotation.annotationplugin.profile.AnnotationClass;
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
	private Bundle bundle;
	private BundleContext bundleContext;

	public final EventManager<SingleAnnotation> onHoverAnnotation = new EventManager<>("editor:hover");
	public final EventManager<SingleAnnotation> onClickAnnotation = new EventManager<>("editor:click");
	public final EventManager<EventManager.EmptyEvent> onClickOutsideOfAnnotation = new EventManager<>("editor:clickoutside");
	public final EventManager<AnnotationProfile> onProfileChange = new EventManager<>("editor:profilechange");
	
	public AnnotationTextEditor() {
		id = UUID.randomUUID().toString();
		documentProvider = new AnnotationDocumentProvider();
		documentProvider.initializeEvent.addListener(e -> {
			textModelData = e.textModelData;
			annotationFixer = new AnnotationSetFixer(e.textModelData.getAnnotations(), e.textModelData.getDocument().getLength());
			try {
				presentationReconciler.setAnnotationInformation(getAnnotationProfileRegistry().findProfile(e.textModelData.getProfileName()), e.textModelData.getAnnotations());
			} catch (ProfileNotFoundException ex) {
				ex.printStackTrace();
				EclipseUtils.reportError("Profile not found.");
			} catch (InvalidAnnotationProfileFormatException ex) {
				ex.printStackTrace();
				EclipseUtils.reportError("Profile not properly formatted. " + ex.getMessage());
			}
		});

		onProfileChange.addListener(p -> {
			presentationReconciler.setAnnotationInformation(p, textModelData.getAnnotations());
		});
		
        setDocumentProvider(documentProvider);
        
        presentationReconciler = new ProjectPresentationReconciler();

		bundle = FrameworkUtil.getBundle(this.getClass());
		bundleContext = bundle.getBundleContext();
	}

	@Override
	public void handleCursorPositionChanged() {
		super.handleCursorPositionChanged();

		if (sourceViewer == null || sourceViewer.getSelectedRange() == null) {
			return;
		}

		Point selected = sourceViewer.getSelectedRange();
		@Nullable SingleAnnotation annotation = textModelData.getSingleAnnotationAt(selected.x);

		if (annotation != null) {
			onClickAnnotation.fire(annotation);
		} else {
			onClickOutsideOfAnnotation.fire(new EventManager.EmptyEvent());
		}
	}
	
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
    	super.init(site, input);
        setInput(input);

		openTextAnnotationPerspective(site);

        HoverProvider hover = new HoverProvider(textModelData);
        hover.onHover.addListener(onClickAnnotation::fire);

        handleCursorPositionChanged();

        // site.getSelectionProvider().addSelectionChangedListener(event -> onClickAnnotation.fire(textModelData.getSingleAnnotationAt(event.getSelection().)));

        setSourceViewerConfiguration(new SourceViewerConfiguration() {
        	@Override
        	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
        		prepareSourceViewer(sourceViewer);
        		return presentationReconciler;
        	}

        	@Override
			public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
				return hover;
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
		markDocumentAsDirty();
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
		return AnnotationProfileRegistry.createNew(FrameworkUtil.getBundle(this.getClass()));
	}

	public String getAnnotationContent(SingleAnnotation annotation) {
		return textModelData.getAnnotationContent(annotation);
	}

	public void markDocumentAsDirty() {
		IDocument doc = sourceViewer.getDocument();
		doc.set(doc.get());
	}

	public AnnotationProfile getAnnotationProfile()
			throws ProfileNotFoundException, InvalidAnnotationProfileFormatException {
		return getAnnotationProfileRegistry().findProfile(textModelData.getProfileName());
	}

	private void openTextAnnotationPerspective(IEditorSite site) {
		site.getWorkbenchWindow().getActivePage().setPerspective(new IPerspectiveDescriptor() {
			@Override public String getDescription() { return ""; }
			@Override public String getId() { return AnnotationPerspective.ID; }
			@Override public ImageDescriptor getImageDescriptor() { return null; }
			@Override public String getLabel() { return ""; }
		});
	}
}
