package edu.kit.textannotation.annotationplugin.editor;

import edu.kit.textannotation.annotationplugin.AnnotationEditorFinder;
import edu.kit.textannotation.annotationplugin.EclipseUtils;
import edu.kit.textannotation.annotationplugin.EventManager;
import edu.kit.textannotation.annotationplugin.profile.AnnotationClass;
import edu.kit.textannotation.annotationplugin.textmodel.TextModelData;
import edu.kit.textannotation.annotationplugin.textmodel.SingleAnnotation;
import org.eclipse.jdt.internal.ui.text.java.hover.AbstractAnnotationHover;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.IWorkbench;

import javax.inject.Inject;
import java.util.Optional;

class HoverProvider extends AbstractAnnotationHover {
	private TextModelData textModelData;
	private AnnotationTextEditor editor;

	final EventManager<SingleAnnotation> onHover = new EventManager<>("hoverprovider:hover");

	HoverProvider(TextModelData textModelData, AnnotationTextEditor editor) {
		super(true);
		this.textModelData = textModelData;
		this.editor = editor;
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		System.out.println("getHoverRegion: " + offset);
		Optional<SingleAnnotation> match = textModelData.getAnnotations()
				.stream()
				.filter(a -> a.getOffset() <= offset && a.getOffset() + a.getLength() > offset)
				.findFirst();

		// TODO use getSingleAnnotationAt

		IRegion result = match.map(singleAnnotation -> new IRegion() {
			@Override
			public int getOffset() {
				return singleAnnotation.getOffset();
			}

			@Override
			public int getLength() {
				return singleAnnotation.getLength();
			}
		}).orElse(null);

		if (result != null) {
			System.out.println(result.getOffset() + ":" + result.getLength());
		}
		return result;
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
			e.printStackTrace();
		}

		try {
			acl = editor.getAnnotationProfile().getAnnotationClass(ann.getAnnotationIdentifier());
		} catch (Exception e) {
			e.printStackTrace();
			EclipseUtils.reportError("Could not get annotationclass data");
			return null;
		}

		return new AnnotationInfo(
				new SingleAnnotationEclipseAnnotation(content, ann, acl),
				new Position(hoverRegion.getOffset(), hoverRegion.getLength()),
				textViewer
		);
	}
}
