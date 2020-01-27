package edu.kit.textannotation.annotationplugin.views;

import java.util.Comparator;
import java.util.function.Consumer;

import edu.kit.textannotation.annotationplugin.AnnotationEditorFinder;
import edu.kit.textannotation.annotationplugin.EventManager;
import edu.kit.textannotation.annotationplugin.LayoutUtilities;
import edu.kit.textannotation.annotationplugin.editor.AnnotationTextEditor;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfileRegistry;
import edu.kit.textannotation.annotationplugin.textmodel.SingleAnnotation;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.*;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.*;
import org.eclipse.swt.SWT;
import javax.inject.Inject;


public class AnnotationInfoView extends ViewPart {
    public static final String ID = "edu.kit.textannotation.annotationplugin.AnnotationInfoView";

    public EventManager<EventManager.EmptyEvent> onChangedMetaData = new EventManager<>("infoview:changedmeta");

    private AnnotationProfileRegistry registry;
    private AnnotationTextEditor editor;
    private LayoutUtilities lu = new LayoutUtilities();

    @Inject IWorkbench workbench;

    @Override
    public void createPartControl(Composite parent) {
        AnnotationEditorFinder finder = new AnnotationEditorFinder(workbench);

        Consumer<SingleAnnotation> onHover = s -> Display.getDefault().syncExec(() -> rebuildContent(parent, s));
        Consumer<EventManager.EmptyEvent> onUnHover = v -> Display.getDefault().syncExec(() -> rebuildContent(parent, null));

        finder.annotationEditorActivated.addListener(e -> {
            e.onClickAnnotation.addListener(onHover);
            e.onClickOutsideOfAnnotation.addListener(onUnHover);
            editor = e;
        });

        finder.annotationEditorDeactivated.addListener(editor -> {
            editor.onClickAnnotation.removeListener(onHover);
            editor.onClickOutsideOfAnnotation.removeListener(onUnHover);
        });

        onChangedMetaData.addListener(e -> editor.markDocumentAsDirty());
    }

    @Override
    public void setFocus() {
    }

    @Override
    public String getTitle() {
        return "Annotation Controls";
    }

    private void rebuildContent(Composite parent, @Nullable SingleAnnotation hoveringAnnotation) {
        registry = editor.getAnnotationProfileRegistry();

        for (Control child: parent.getChildren()) {
            child.dispose();
        }

        if (hoveringAnnotation == null) {
            parent.layout();
            return;
        }

        GridLayout layout = new GridLayout();
        // TODO LayoutUtilities
        parent.setLayout(layout);
        layout.numColumns = 3;
        layout.verticalSpacing = 7;
        layout.horizontalSpacing = 20;
        layout.marginWidth = 25;
        layout.marginHeight = 15;

        addLine(parent, "Marked Text:", editor.getAnnotationContent(hoveringAnnotation));
        addLine(parent, "Annotated Class:", hoveringAnnotation.getAnnotationIdentifier());
        addLine(parent, "Location:", String.format("%s:%s", hoveringAnnotation.getOffset(), hoveringAnnotation.getLength()));
        // addLine(parent, "References:", "TODO");

        hoveringAnnotation.metaData.stream()
                .sorted(Comparator.comparing(a -> a.key))
                .forEach(metaDataEntry -> addLine2(
                        parent,
                        metaDataEntry.key,
                        metaDataEntry.value,
                        true,
                        true,
                        true,
                        e -> {
                            hoveringAnnotation.metaData.remove(metaDataEntry.key);
                            hoveringAnnotation.metaData.put(e[0], e[1]);
                            rebuildContent(parent, hoveringAnnotation);
                            onChangedMetaData.fire(new EventManager.EmptyEvent());
                        },
                        e -> {
                            hoveringAnnotation.metaData.remove(metaDataEntry.key);
                            rebuildContent(parent, hoveringAnnotation);
                            onChangedMetaData.fire(new EventManager.EmptyEvent());
                        }
                ));

        Button addEntry = new Button(parent, SWT.NONE);
        addEntry.setText("Add meta data entry");
        addEntry.setLayoutData(lu.gridData().withHorizontalSpan(3).withExcessHorizontalSpace(true)
                .withHorizontalAlignment(SWT.FILL).get());
        addEntry.addListener(SWT.Selection, e -> {
            hoveringAnnotation.metaData.put(getNewMetaDataKey(hoveringAnnotation), "Meta data value");
            rebuildContent(parent, hoveringAnnotation);
            onChangedMetaData.fire(new EventManager.EmptyEvent());
        });

        parent.layout();
    }

    private void addLine(Composite parent, String label, String value) {
        addLine2(parent, label, value, false, false, false, null, null);
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
    private void addLine2(Composite parent, String label, String value, boolean canEditLabel, boolean canEditValue,
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

    private String getNewMetaDataKey(SingleAnnotation annotation) {
        String key = "";
        int i = 1;

        do {
            key = String.format("New Metadata entry %s", i++);
        } while (annotation.metaData.contains(key));

        return key;
    }
}
