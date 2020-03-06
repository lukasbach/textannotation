package edu.kit.textannotation.annotationplugin.selectionstrategy;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;

import java.util.Arrays;
import java.util.List;

/**
 * This selection expands the user selection to include full sentences.
 *
 * @see SelectionStrategy
 * @see edu.kit.textannotation.annotationplugin.views.AnnotationControlsView
 */
public class SentenceSelectionStrategy implements SelectionStrategy {
    private List<Character> breakingCharacters = Arrays.asList(
            '.',
            ';'
    );

    @Override
    public String getName() {
        return "Sentence-based";
    }

    @Override
    public String getId() {
        return "selectionstrategy/sentence";
    }

    @Override
    public String getDescription() {
        return "The selection made in the editor view will be expanded to include full sentences before being annotated.";
    }

    @Override
    public Region evaluateSelection(Region selection, IDocument document) {
        int start = selection.getOffset();
        int end = selection.getOffset() + selection.getLength();
        String text = document.get();

        while(start > 0 && !breakingCharacters.contains(text.charAt(start - 1))) {
            start--;
        }

        while(end < text.length() - 1 && !breakingCharacters.contains(text.charAt(end + 1))) {
            end++;
        }

        return new Region(start, end - start + 1);
    }
}
