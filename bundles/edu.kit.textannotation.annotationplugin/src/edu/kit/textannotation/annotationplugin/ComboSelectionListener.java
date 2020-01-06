package edu.kit.textannotation.annotationplugin;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public class ComboSelectionListener implements SelectionListener {
    private Runnable onSelect;

    public ComboSelectionListener(Runnable onSelect) {
        this.onSelect = onSelect;
    }

    @Override public void widgetDefaultSelected(SelectionEvent e) {}
    @Override public void widgetSelected(SelectionEvent e) {
        onSelect.run();
    }
}
