package edu.kit.textannotation.annotationplugin.views;

import edu.kit.textannotation.annotationplugin.EventManager;
import edu.kit.textannotation.annotationplugin.LayoutUtilities;
import edu.kit.textannotation.annotationplugin.profile.MetaDataContainer;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.util.Comparator;
import java.util.function.Consumer;

public class MetaDataView {
    public EventManager<EventManager.EmptyEvent> onChangedMetaData = new EventManager<>("metadataview:changedmeta");

    private final LayoutUtilities lu;
    private final Composite parent;
    private final MetaDataContainer metaData;
    private final boolean canEditKeys;
    private final boolean canEditValues;
    private final boolean canRemoveKeys;
    private final boolean canAddKeys;


    public MetaDataView(Composite parent, MetaDataContainer metaData, boolean canEditKeys,
                        boolean canEditValues, boolean canRemoveKeys, boolean canAddKeys) {
        this.lu = new LayoutUtilities();
        this.parent = new Composite(parent, SWT.NULL);
        this.parent.setLayoutData(lu.horizontalFillingGridData());
        this.metaData = metaData;
        this.canEditKeys = canEditKeys;
        this.canEditValues = canEditValues;
        this.canRemoveKeys = canRemoveKeys;
        this.canAddKeys = canAddKeys;

        // metaData.onChange.addListener(e -> rebuildContent());
        rebuildContent();
    }

    private void rebuildContent() {
        for (Control child: parent.getChildren()) {
            child.dispose();
        }

        GridLayout layout = new GridLayout();
        // TODO LayoutUtilities
        parent.setLayout(layout);
        layout.numColumns = 3;
        layout.verticalSpacing = 7;
        layout.horizontalSpacing = 20;
        layout.marginWidth = 25;
        layout.marginHeight = 15;

        metaData.stream()
                .sorted(Comparator.comparing(a -> a.key))
                .forEach(metaDataEntry -> addLine(
                        parent,
                        metaDataEntry.key,
                        metaDataEntry.value,
                        canEditKeys,
                        canEditValues,
                        canRemoveKeys,
                        e -> {
                            metaData.remove(metaDataEntry.key);
                            metaData.put(e[0], e[1]);
                            rebuildContent();
                            onChangedMetaData.fire(new EventManager.EmptyEvent());
                        },
                        e -> {
                            metaData.remove(metaDataEntry.key);
                            rebuildContent();
                            onChangedMetaData.fire(new EventManager.EmptyEvent());
                        }
                ));

        if (canAddKeys) {
            Button addEntry = new Button(parent, SWT.NONE);
            addEntry.setText("Add meta data entry");
            addEntry.setLayoutData(lu.gridData().withHorizontalSpan(3).withExcessHorizontalSpace(true)
                    .withHorizontalAlignment(SWT.FILL).get());
            addEntry.addListener(SWT.Selection, e -> {
                metaData.put(getNewMetaDataKey(), "Meta data value");
                rebuildContent();
                onChangedMetaData.fire(new EventManager.EmptyEvent());
            });
        }

        parent.layout();
    }

    /**
     * Add a form line with the option to open an window for the line where the values
     * can be edited.
     * @param parent of the form line
     * @param label initial value of the label
     * @param value initial value for the field
     * @param canEditLabel specifies if the label can be changed in the edit window
     * @param canEditValue specifies if the value can be changed in the edit window
     * @param canRemove specifies if the label-value pair can be removed
     * @param onChange triggers after changes where applied. Returns with an array of two strings, the first
     *                 one specifies the current label, the second one specifies the current value.
     * @param onRemove triggers if the user attempts to remove the label-value pair.
     */
    private void addLine(Composite parent, String label, String value, boolean canEditLabel, boolean canEditValue,
                          boolean canRemove, @Nullable Consumer<String[]> onChange, @Nullable Listener onRemove) {
        Label l = new Label(parent, SWT.NULL);
        l.setText(label);

        Text t = new Text(parent, SWT.BORDER | SWT.SINGLE);
        t.setLayoutData(lu.horizontalFillingGridData());
        t.setText(value);
        t.setEditable(false);


        Button b = new Button(parent, SWT.PUSH);
        b.setText("Edit");
        b.setEnabled(canEditLabel || canEditValue || canRemove);
        b.addListener(SWT.Selection, e -> {
            Shell editWindow = new Shell(Display.getCurrent());
            editWindow.open();
            editWindow.setLayout(lu.gridLayout().withNumCols(2).get());

            Label keyLabel = new Label(editWindow, SWT.NULL);
            keyLabel.setText("Label:");

            Text keyText = new Text(editWindow, SWT.BORDER | SWT.SINGLE);
            keyText.setLayoutData(lu.horizontalFillingGridData());
            keyText.setEditable(canEditLabel);
            keyText.setText(label);

            Label valueLabel = new Label(editWindow, SWT.NULL);
            valueLabel.setText("Value:");
            valueLabel.setLayoutData(lu.gridData().withWidthHint(140).get());

            Text valueText = new Text(editWindow, SWT.BORDER | SWT.SINGLE);
            valueText.setLayoutData(lu.horizontalFillingGridData());
            valueText.setEditable(canEditValue);
            valueText.setText(value);

            Button buttonRemove = new Button(editWindow, SWT.PUSH);
            buttonRemove.setText("Remove field");
            buttonRemove.setEnabled(canRemove);
            buttonRemove.addListener(SWT.Selection, ignored -> {
                onRemove.handleEvent(ignored);
                editWindow.close();
            });
            buttonRemove.setLayoutData(lu.verticallyFillingGridData());

            Button buttonApply = new Button(editWindow, SWT.PUSH);
            buttonApply.setText("Apply");
            buttonApply.addListener(SWT.Selection, ignored -> {
                onChange.accept(new String[] {keyText.getText(), valueText.getText()});
                editWindow.close();
            });
            buttonApply.setLayoutData(lu.completelyFillingGridData());

            editWindow.pack();
            editWindow.layout();

            editWindow.setActive();
            editWindow.setMinimumSize(200, 120);
            editWindow.setSize(280, 140);
            editWindow.setText("Edit field");
        });
    }

    private String getNewMetaDataKey() {
        String key = "";
        int i = 1;

        do {
            key = String.format("New Metadata entry %s", i++);
        } while (metaData.contains(key));

        return key;
    }
}
