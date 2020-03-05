package edu.kit.textannotation.annotationplugin.utils;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;

import java.util.function.Consumer;

/**
 * This class is a utility for defining a listener lambda for SWT selection components (Combos).
 *
 * @see Combo
 */
public class ComboSelectionListener implements SelectionListener {
    private Consumer<String> onSelect;
    private Combo combo;
    private String lastText;

    /**
     * Create a new selection listener. The listener will not yet be attached to the Combo element.
     * @param combo the Combo element to listen on.
     * @param onSelect the selection handler.
     * @see Combo::addSelectionListener
     */
    public ComboSelectionListener(Combo combo, Consumer<String> onSelect) {
        this.onSelect = onSelect;
        this.combo = combo;
        this.lastText = combo.getText();
    }

    /**
     * Convenience constructor that automatically binds the created listener to the Combo.
     */
    public static ComboSelectionListener create(Combo combo, Consumer<String> onSelect) {
        ComboSelectionListener listener = new ComboSelectionListener(combo, onSelect);
        listener.applyToCombo();
        return listener;
    }

    /**
     * Add the created selection listener to the combo.
     */
    public void applyToCombo() {
        combo.addSelectionListener(this);
    }

    @Override public void widgetSelected(SelectionEvent e) {
        String newText = combo.getText();

        if (!lastText.equals(newText)) {
            onSelect.accept(newText);
            lastText = newText;
        }
    }
    @Override public void widgetDefaultSelected(SelectionEvent e) {}

}
