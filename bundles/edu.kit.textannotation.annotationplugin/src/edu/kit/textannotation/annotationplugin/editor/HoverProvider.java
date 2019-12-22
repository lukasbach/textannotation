package edu.kit.textannotation.annotationplugin.editor;

import edu.kit.textannotation.annotationplugin.textmodel.AnnotationData;
import edu.kit.textannotation.annotationplugin.textmodel.SingleAnnotation;
import org.eclipse.jdt.internal.ui.text.java.hover.AbstractAnnotationHover;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;

import java.util.Optional;

public class HoverProvider extends AbstractAnnotationHover {
	private AnnotationData annotationData;

	HoverProvider(AnnotationData annotationData) {
		super(true);
		System.out.println("Create Hover Provider");
		this.annotationData = annotationData;
	}

	private SingleAnnotation getSingleAnnotationAt(int offset) {
		return annotationData.annotations
				.stream()
				.filter(a -> a.getOffset() <= offset && a.getOffset() + a.getLength() > offset)
				.findFirst()
				.orElse(null);
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		System.out.println("getHoverRegion: " + offset);
		Optional<SingleAnnotation> match = annotationData.annotations
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
		SingleAnnotation ann = getSingleAnnotationAt(hoverRegion.getOffset());
		String content = "";

		try {
			content = textViewer.getDocument().get(hoverRegion.getOffset(), hoverRegion.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		return new AnnotationInfo(
				new SingleAnnotationEclipseAnnotation(content, ann),
				new Position(hoverRegion.getOffset(), hoverRegion.getLength()),
				textViewer
		);
	}
}
