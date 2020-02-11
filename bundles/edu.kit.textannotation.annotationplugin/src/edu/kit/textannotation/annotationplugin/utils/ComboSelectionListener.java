package edu.kit.textannotation.annotationplugin.utils;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;

import java.util.function.Consumer;

public class ComboSelectionListener implements SelectionListener {
    private Consumer<String> onSelect;
    private Combo combo;
    private String lastText;

    public ComboSelectionListener(Combo combo, Consumer<String> onSelect) {
        this.onSelect = onSelect;
        this.combo = combo;
        this.lastText = combo.getText();
    }

    public static ComboSelectionListener create(Combo combo, Consumer<String> onSelect) {
        ComboSelectionListener listener = new ComboSelectionListener(combo, onSelect);
        listener.applyToCombo();
        return listener;
    }

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
