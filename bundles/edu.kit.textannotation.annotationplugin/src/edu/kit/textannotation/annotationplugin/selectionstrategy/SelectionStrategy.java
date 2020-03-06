package edu.kit.textannotation.annotationplugin.selectionstrategy;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;

/**
 * A selection strategy defines how a region selected in the editor is being handled when the user
 * attempts to annotate this region. The Selection Strategy defines how the region can be expanded
 * based on the text semantics. The strategies are bootstrapped in
 * {@link edu.kit.textannotation.annotationplugin.views.AnnotationControlsView}.
 */
public interface SelectionStrategy {
    /** Return a name for the strategy that is intended to be displayed to the end user. */
    public String getName();

    /** Return an unique ID for the strategy to identify it against other strategies. */
    public String getId();

    /** Return a description for the strategy that explains its use to the end user. */
    public String getDescription();

    /**
     * The actual strategy implementation. Define how the strategy expands the region based on the
     * text document.
     * @param selection the original selection made be the user.
     * @param document the text document, with the selection being relative to the start of the document.
     * @return the altered selection.
     */
    public Region evaluateSelection(Region selection, IDocument document);
}
