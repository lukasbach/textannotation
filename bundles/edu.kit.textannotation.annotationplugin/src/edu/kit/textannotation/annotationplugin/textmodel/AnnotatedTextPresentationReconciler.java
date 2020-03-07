package edu.kit.textannotation.annotationplugin.textmodel;

import edu.kit.textannotation.annotationplugin.utils.EclipseUtils;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.presentation.*;

import edu.kit.textannotation.annotationplugin.profile.AnnotationClass;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class defines how regions in annotatable text files should be highlighted. This is based
 * on the explicit annotation data available in the annotatable data structure. This class is designed
 * to be used as a client by {@link edu.kit.textannotation.annotationplugin.editor.AnnotationTextEditor},
 * and as such is instantiated by the Eclipse framework.
 *
 * @see TextModelData
 * @see AnnotationProfile
 * @see SingleAnnotation
 */
public class AnnotatedTextPresentationReconciler implements IPresentationReconciler, IPresentationReconcilerExtension {
	
	private AnnotationProfile profile;
	private AnnotationSet annotations;

	private ITextListener textListener;
	private ITextViewer textViewer;

	private class TextListener implements ITextListener {
		@Override
		public void textChanged(TextEvent event) {
			List<SingleAnnotation> annotationList = annotations
					.getAnnotations()
					.stream()
					.sorted(Comparator.comparingInt(SingleAnnotation::getOffset))
					.collect(Collectors.toList());

			for (SingleAnnotation an: annotationList) {
				try {
					AnnotationClass ac = profile.getAnnotationClass(an.getAnnotationClassId());

					textViewer.setTextColor(ac.getColor(), an.getOffset(), an.getLength(), true);
				} catch (Exception e) {
					EclipseUtils.logger().error(e);
				}
			}
		}
	}

	/** Create a new reconciler instance. This constructor is not supposed to be called manually by clients,
	 * but is automatically invoked by the Eclipse framework. */
	public AnnotatedTextPresentationReconciler() {}

	@Override
	public void install(ITextViewer viewer) {
		textListener = new TextListener();
		textViewer = viewer;
		textViewer.addTextListener(textListener);
	}

	@Override
	public void uninstall() {
		textViewer.removeTextListener(textListener);
	}

	@Override
	public IPresentationDamager getDamager(String contentType) {
		return null;
	}

	@Override
	public IPresentationRepairer getRepairer(String contentType) {
		return null;
	}

	@Override
	public String getDocumentPartitioning() {
		return null;
	}

	/**
	 * Attach annotation data, i.e. the annotation profile and the set of annotations to this reconciler.
	 * This is required for highlighting the annotation data, and is invoked by the editor instance.
	 *
	 * @see edu.kit.textannotation.annotationplugin.editor.AnnotationTextEditor
	 * @param profile the profile of the currently opened annotatable text file.
	 * @param annotations the set of annotations in the annotatable text file.
	 */
    public void setAnnotationInformation(AnnotationProfile profile, AnnotationSet annotations) { // TODO use TextModelData
    	this.profile = profile;
    	this.annotations = annotations;
    }
}