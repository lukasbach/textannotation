package edu.kit.textannotation.annotationplugin.editor;

import java.util.UUID;

import edu.kit.textannotation.annotationplugin.utils.EclipseUtils;
import edu.kit.textannotation.annotationplugin.utils.EventManager;
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
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.*;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import edu.kit.textannotation.annotationplugin.profile.AnnotationClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * A text editor model for annotatable text files. This implements the interface {@link org.eclipse.ui.texteditor.ITextEditor}
 * as defined by the Eclipse Framework for implementing custom editors and as such is defined as a direct
 * contribution for this plugin.
 *
 * The referenced classes are used as additional contributions for the plugin to provide further functionality.
 * This editor also required the {@link AnnotationDocumentProvider} to be specified as document provider for it to
 * work, as the editor assumes that it receives the parsed annotation data from there.
 *
 * @see AnnotationDocumentProvider
 * @see HoverProvider
 */
public class AnnotationTextEditor extends AbstractTextEditor {
	private AnnotatedTextPresentationReconciler presentationReconciler;
	private AnnotationDocumentProvider documentProvider;
	private TextModelData textModelData;
	private AnnotationSetFixer annotationFixer;
	private ISourceViewer sourceViewer;
	private String id;
	private Bundle bundle;
	private BundleContext bundleContext;

	/** This event fires when an annotation is clicked on in the editor view. */
	public final EventManager<SingleAnnotation> onClickAnnotation = new EventManager<>("editor:click");

	/** This event fires when the user dispatches a click in the editor view to a location which does not map to an annotation. */
	public final EventManager<EventManager.EmptyEvent> onClickOutsideOfAnnotation = new EventManager<>("editor:clickoutside");

	/** This event fires when the profile associated with the currently opened file is changed. */
	public final EventManager<AnnotationProfile> onProfileChange = new EventManager<>("editor:profilechange");

	/**
	 * Create a new Annotation Text Editor. Clients do not need to call this constructor manually, the Eclipse framework
	 * will create an instance using this constructor automatically.
	 */
	public AnnotationTextEditor() {
		id = UUID.randomUUID().toString();
		documentProvider = new AnnotationDocumentProvider();
		documentProvider.onInitialize.addListener(e -> {
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
        
        presentationReconciler = new AnnotatedTextPresentationReconciler();

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

	@Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
    	super.init(site, input);
        setInput(input);

		openTextAnnotationPerspective(site);

        HoverProvider hover = new HoverProvider(textModelData, this);
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

    /** Return a reference to the TextModel data for this class. */
	public TextModelData getTextModelData() {
		return textModelData;
	}

	/** Add the given annotation class to the text model associated with the currently opened annotation file. */
	public void annotate(AnnotationClass annotationClass) {
		Point p = sourceViewer.getSelectedRange();
		int offset = p.x;
		int length = p.y;
		
		SingleAnnotation annotation = new SingleAnnotation(UUID.randomUUID().toString(), 
				offset, length, annotationClass.getName());
		System.out.println("Annotating: " + annotation.toString());
		textModelData.getAnnotations().addAnnotation(annotation);
		
		// Trigger rehighlight
		markDocumentAsDirty();
	}

	/** Remove any annotations at the given offset in the currently opened annotation file. */
	public void deannotate(int offset) {
		SingleAnnotation annotation = textModelData.getSingleAnnotationAt(offset);
		textModelData.getAnnotations().removeAnnotation(annotation);
		markDocumentAsDirty();
	}

	/** Return a unique ID for this editor. */
    public String getId() {
		return id;
	}

	/** Create and return an instance of a annotation profile registry that can be used to receive a reference
	 * to the profile object.
	 */
	public AnnotationProfileRegistry getAnnotationProfileRegistry() {
		return AnnotationProfileRegistry.createNew(FrameworkUtil.getBundle(this.getClass()));
	}

	/** Return the text content that is annotated by the supplied annotation reference. */
	public String getAnnotationContent(SingleAnnotation annotation) {
		return textModelData.getAnnotationContent(annotation);
	}

	/**
	 * Mark the document as dirty, i.e. the user gets notified if he attempts to close the file without saving,
	 * and the document gets savable.
	 */
	public void markDocumentAsDirty() {
		IDocument doc = sourceViewer.getDocument();
		doc.set(doc.get());
	}

	/**
	 * Return an instance to the relevant annotation profile. This will create a new instance of an
	 * {@link AnnotationProfileRegistry} and read the profile from disk.
	 * @return a profile object, if the profile could be found.
	 * @throws ProfileNotFoundException if the profile was not found on the disk.
	 * @throws InvalidAnnotationProfileFormatException if the profile was found but malformed.
	 */
	public AnnotationProfile getAnnotationProfile()
			throws ProfileNotFoundException, InvalidAnnotationProfileFormatException {
		return getAnnotationProfileRegistry().findProfile(textModelData.getProfileName());
	}

	private void openTextAnnotationPerspective(IEditorSite site) {
		try {
			site.getWorkbenchWindow().getActivePage().setPerspective(new IPerspectiveDescriptor() {
				@Override public String getDescription() { return ""; }
				@Override public String getId() { return AnnotationPerspective.ID; }
				@Override public ImageDescriptor getImageDescriptor() { return null; }
				@Override public String getLabel() { return ""; }
			});
		} catch(Exception e) {
			// If automatic perspective opening fails, don't attempt it.
		}
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
}
