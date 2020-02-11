package edu.kit.textannotation.annotationplugin.textmodel;

import edu.kit.textannotation.annotationplugin.utils.EventManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;

public class TextModelData {
	public final EventManager<String> onChangeProfileName = new EventManager<>("textmodeldata:changeprofile");

	private AnnotationSet annotations;
	private String profileName;
	private IDocument document;

	public TextModelData(AnnotationSet annotations, String profileName, IDocument document) {
		this.setAnnotations(annotations);
		this.profileName = profileName;
		this.setDocument(document);
	}

	public TextModelData(String profileName) {
		this.setAnnotations(new AnnotationSet());
		this.profileName = profileName;
		this.document = new Document("");
	}

	@Override
	public String toString() {
		return String.format("AnnotationData(%s, %s)", getAnnotations().toString(), getProfileName());
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
		onChangeProfileName.fire(profileName);
	}

	public AnnotationSet getAnnotations() {
		return annotations;
	}

	public void setAnnotations(AnnotationSet annotations) {
		this.annotations = annotations;
	}

	public IDocument getDocument() {
		return document;
	}

	public void setDocument(IDocument document) {
		this.document = document;
	}

	public String getAnnotationContent(SingleAnnotation annotation) {
		try {
			return document.get(annotation.getOffset(), annotation.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
			return "[BADANNOTATION]";
		}
	}

	public SingleAnnotation getSingleAnnotationAt(int offset) {
		return getAnnotations()
				.stream()
				.filter(a -> a.getOffset() <= offset && a.getOffset() + a.getLength() > offset)
				.findFirst()
				.orElse(null);
	}
}
