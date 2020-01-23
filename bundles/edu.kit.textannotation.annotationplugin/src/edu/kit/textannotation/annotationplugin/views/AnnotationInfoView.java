package edu.kit.textannotation.annotationplugin.views;

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
        addLine(parent, "References:", "TODO");

        hoveringAnnotation.streamMetaData().forEach(metaDataEntry -> {
            addLine(
                    parent,
                    metaDataEntry.key,
                    metaDataEntry.value,
                    v -> hoveringAnnotation.putMetaDataEntry(metaDataEntry.key, v),
                    "delete",
                    () -> {
                        hoveringAnnotation.removeMetaDataEntry(metaDataEntry.key);
                        rebuildContent(parent, hoveringAnnotation);
                    }
            );
        });

        Button addEntry = new Button(parent, SWT.NONE);
        addEntry.setText("Add meta data entry");
        addEntry.setLayoutData(lu.gridData().withHorizontalSpan(3).withExcessHorizontalSpace(true)
                .withHorizontalAlignment(SWT.FILL).get());
        addEntry.addListener(SWT.Selection, e -> {
        hoveringAnnotation.putMetaDataEntry("New Key", "");
        rebuildContent(parent, hoveringAnnotation);
        });
        // TODO allow editing the key

        parent.layout();
    }

    private void addLine(Composite parent, String label, String value) {
        addLine(parent, label, value, null, null, null);
    }

    private void addLine(Composite parent, String label, String value, @Nullable Consumer<String> onChange) {
        addLine(parent, label, value, onChange, null, null);
    }

    private void addLine(Composite parent, String label, String value, @Nullable Consumer<String> onChange, 
                         @Nullable String buttonText, @Nullable Runnable onButtonClick) {
        Label l = new Label(parent, SWT.NULL);
        l.setText(label);

        Text t = new Text(parent, SWT.BORDER | SWT.SINGLE);
        t.setLayoutData(lu.gridData().withHorizontalAlignment(SWT.FILL).withExcessHorizontalSpace(true).get());
        t.setText(value);
        t.setEditable(onChange != null);
        if (onChange != null) {
            t.addModifyListener(e -> onChange.accept(t.getText()));
        }

        if (buttonText != null) {
            Button b = new Button(parent, SWT.NONE);
            b.setText(buttonText);
            b.setLayoutData(lu.horizontalFillingGridData());
            if (onButtonClick != null) {
                b.addListener(SWT.Selection, e -> onButtonClick.run());
            }
        } else {
            new Text(parent, SWT.NONE);
        }
    }
}
