package edu.kit.textannotation.annotationplugin.editor;

import edu.kit.textannotation.annotationplugin.utils.EclipseUtils;
import edu.kit.textannotation.annotationplugin.utils.EventManager;
import edu.kit.textannotation.annotationplugin.profile.AnnotationClass;
import edu.kit.textannotation.annotationplugin.textmodel.TextModelData;
import edu.kit.textannotation.annotationplugin.textmodel.SingleAnnotation;
import org.eclipse.jdt.internal.ui.text.java.hover.AbstractAnnotationHover;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jgit.annotations.Nullable;

/**
 * This class contributes to the plugin by defining hover integration. It implements the interfaces
 * IJavaEditorTextHover, ITextHoverExtension and ITextHoverExtension2.
 *
 * This contribution only works in a {@link AnnotationTextEditor}.
 *
 * @see org.eclipse.jdt.ui.text.java.hover.IJavaEditorTextHover
 * @see org.eclipse.jface.text.ITextHoverExtension
 * @see org.eclipse.jface.text.ITextHoverExtension2
 */
class HoverProvider extends AbstractAnnotationHover {
	private TextModelData textModelData;
	private AnnotationTextEditor editor;

	/** Fires when an annotation is hovered over, and supplies the hovered annotation as payload. */
	final EventManager<SingleAnnotation> onHover = new EventManager<>("hoverprovider:hover");

	HoverProvider(TextModelData textModelData, AnnotationTextEditor editor) {
		super(true);
		this.textModelData = textModelData;
		this.editor = editor;
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		@Nullable SingleAnnotation match = textModelData.getSingleAnnotationAt(offset);

		if (match == null) {
			return null;
		} else {
			return new IRegion() {
				@Override
				public int getOffset() {
					return match.getOffset();
				}

				@Override
				public int getLength() {
					return match.getLength();
				}
			};
		}
	}

	@Override
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
		SingleAnnotation ann = textModelData.getSingleAnnotationAt(hoverRegion.getOffset());

		this.onHover.fire(ann);

		String content = "";
		AnnotationClass acl;

		try {
			content = textViewer.getDocument().get(hoverRegion.getOffset(), hoverRegion.getLength());
		} catch (BadLocationException e) {
			EclipseUtils.logger().error(e);
		}

		try {
			acl = editor.getAnnotationProfile().getAnnotationClass(ann.getAnnotationClassId());
		} catch (Exception e) {
			EclipseUtils.logger().error(e);
			// Don't act on the error, as the annotation might be from a previous profile.
			return null;
		}

		return new AnnotationInfo(
				new SingleAnnotationEclipseAnnotation(content, ann, acl),
				new Position(hoverRegion.getOffset(), hoverRegion.getLength()),
				textViewer
		);
	}
}
