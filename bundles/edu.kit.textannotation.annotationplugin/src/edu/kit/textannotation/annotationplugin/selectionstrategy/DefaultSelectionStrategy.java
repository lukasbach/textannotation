package edu.kit.textannotation.annotationplugin.selectionstrategy;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;

/**
 * This selection just returns the original selection unchanged. It is the default selection strategy.
 *
 * @see SelectionStrategy
 * @see edu.kit.textannotation.annotationplugin.views.AnnotationControlsView
 */
public class DefaultSelectionStrategy implements SelectionStrategy {
    @Override
    public String getName() {
        return "Default";
    }

    @Override
    public String getId() {
        return "selectionstrategy/default";
    }

    @Override
    public String getDescription() {
        return "The exact selection made in the editor view will be annotated.";
    }

    @Override
    public Region evaluateSelection(Region selection, IDocument document) {
        return selection;
    }
}
