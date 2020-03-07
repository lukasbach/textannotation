package edu.kit.textannotation.annotationplugin.utils;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;

import java.util.function.Consumer;

/**
 * This class is a utility for defining a listener lambda for SWT selection components (Combos).
 * This class can handle combos with multiple elements with the same name.
 *
 * @see Combo
 */
public class ComboSelectionListener extends SelectionAdapter {
    public static class ComboSelectionEvent {
        final public String value;
        final public int index;

        public ComboSelectionEvent(String value, int index) {
            this.value = value;
            this.index = index;
        }
    }

    private Consumer<ComboSelectionEvent> onSelectValueHandler;
    private Combo combo;

    /**
     * Create a new selection listener. The listener will not yet be attached to the Combo element.
     * @param combo the Combo element to listen on.
     * @param onSelect the selection handler.
     * @see Combo::addSelectionListener
     */
    public ComboSelectionListener(Combo combo, Consumer<ComboSelectionEvent> onSelect) {
        this.onSelectValueHandler = onSelect;
        this.combo = combo;
    }

    /**
     * Convenience constructor that automatically binds the created listener to the Combo.
     */
    public static ComboSelectionListener create(Combo combo, Consumer<ComboSelectionEvent> onSelect) {
        ComboSelectionListener listener = new ComboSelectionListener(combo, onSelect);
        listener.applyToCombo();
        return listener;
    }

    /**
     * Add the created selection listener to the combo.
     */
    public void applyToCombo() {
        combo.addSelectionListener(this);
        combo.addDisposeListener(e -> combo.removeSelectionListener(this));
    }

    @Override public void widgetSelected(SelectionEvent e) {
        onSelectValueHandler.accept(new ComboSelectionEvent(combo.getText(), combo.getSelectionIndex()));
    }
}
