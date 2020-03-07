package edu.kit.textannotation.annotationplugin.textmodel;

import edu.kit.textannotation.annotationplugin.utils.EventManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jgit.annotations.Nullable;

/**
 * This class models a wrapper around all information that is stored within an annotatable text file document.
 * This includes
 *
 * <ul>
 *     <li>The raw text document</li>
 *     <li>A set of annotations</li>
 *     <li>The name of the used profile</li>
 * </ul>
 *
 * @see AnnotationSet
 * @see edu.kit.textannotation.annotationplugin.profile.AnnotationProfile
 * @see IDocument
 */
public class TextModelData {
	/** This event fires when the name of the referenced profile changes. The payload describes the new profile name */
	public final EventManager<String> onChangeProfileName = new EventManager<>("textmodeldata:changeprofile");

	private AnnotationSet annotations;
	private String profileId;
	private IDocument document;

	/**
	 * Create a new data instance with the supplied initial values.
	 * @param annotations the set of annotations contained in the annotatable text file
	 * @param profileId the ID of the profile, which is defined in a seperate profile file
	 * @param document the raw text document from the annotatable text file
	 */
	public TextModelData(AnnotationSet annotations, String profileId, IDocument document) {
		this.setAnnotations(annotations);
		this.profileId = profileId;
		this.setDocument(document);
	}

	/**
	 * Create a new data instance with a supplied profile ID, and an empty annotation set and document.
	 * Convenience constructor for when those are not required.
	 */
	public TextModelData(String profileId) {
		this.setAnnotations(new AnnotationSet());
		this.profileId = profileId;
		this.document = new Document("");
	}

	@Override
	public String toString() {
		return String.format("AnnotationData(%s, %s)", getAnnotations().toString(), getProfileId());
	}

	/**
	 * Return the ID of the profile, which is referenced by the annotatable text file.
	 */
	public String getProfileId() {
		return profileId;
	}

	/**
	 * Set the profile ID, which dictates the profile used by the annotatable text file
	 * @param profileId the new ID of the profile. A profile with this must be defined on disk
	 *                    and visible to a
	 *                    {@link edu.kit.textannotation.annotationplugin.profile.AnnotationProfileRegistry}
	 *                    instance.
	 */
	public void setProfileId(String profileId) {
		this.profileId = profileId;
		onChangeProfileName.fire(profileId);
	}

	/**
	 * Return the set of annotations. Changes to the annotations can be done on the returned reference.
	 */
	public AnnotationSet getAnnotations() {
		return annotations;
	}

	/**
	 * Overwrite the reference to the used annotationset.
	 */
	public void setAnnotations(AnnotationSet annotations) {
		this.annotations = annotations;
	}

	/**
	 * Return the raw text document for the relevant annotatable text file.
	 */
	public IDocument getDocument() {
		return document;
	}

	/**
	 * Overwrite the reference for the relevant annotatable text file.
	 * @param document
	 */
	public void setDocument(IDocument document) {
		this.document = document;
	}

	/**
	 * Return the text content of an annotation, based on its defining region.
	 * @param annotation the region of this annotation defines the boundaries of the text that is returned
	 * @return the text within the region boundaries of the supplied annotation
	 */
	public String getAnnotationContent(SingleAnnotation annotation) {
		try {
			return document.get(annotation.getOffset(), annotation.getLength());
		} catch (BadLocationException e) {
			// TODO
			e.printStackTrace();
			return "[BADANNOTATION]";
		}
	}

	/**
	 * Return the annotation instance that includes the specified location offset, relative to the
	 * document beginning. Note that this can be at most one, as annotations may not overlap one another.
	 * The logic for not overlapping annotations is specified in {@link AnnotationSet}.
	 * @param offset where an annotation is being looked for.
	 * @return the found annotation at the specified location, or null if the location is not marked with an annotation.
	 */
	@Nullable public SingleAnnotation getSingleAnnotationAt(int offset) {
		return getAnnotations()
				.stream()
				.filter(a -> a.getOffset() <= offset && a.getOffset() + a.getLength() > offset)
				.findFirst()
				.orElse(null);
	}
}
