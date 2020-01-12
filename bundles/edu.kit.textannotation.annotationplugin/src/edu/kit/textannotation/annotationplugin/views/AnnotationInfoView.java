package edu.kit.textannotation.annotationplugin.views;

import java.util.function.Consumer;
import java.util.function.Function;

import edu.kit.textannotation.annotationplugin.AnnotationEditorFinder;
import edu.kit.textannotation.annotationplugin.EventManager;
import edu.kit.textannotation.annotationplugin.editor.AnnotationTextEditor;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfileRegistry;
import edu.kit.textannotation.annotationplugin.textmodel.SingleAnnotation;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.*;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.*;
import org.eclipse.swt.SWT;
import javax.inject.Inject;


public class AnnotationInfoView extends ViewPart {
    public static final String ID = "edu.kit.textannotation.annotationplugin.AnnotationInfoView";
    private AnnotationProfileRegistry registry;
    private AnnotationTextEditor editor;

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
        parent.setLayout(layout);
        layout.numColumns = 2;
        layout.verticalSpacing = 7;
        layout.horizontalSpacing = 20;
        layout.marginWidth = 25;
        layout.marginHeight = 15;

        addLine(parent, "Marked Text:", editor.getAnnotationContent(hoveringAnnotation));
        addLine(parent, "Annotated Class:", hoveringAnnotation.getAnnotationIdentifier());
        addLine(parent, "Location:", String.format("%s:%s", hoveringAnnotation.getOffset(), hoveringAnnotation.getLength()));
        addLine(parent, "Refereces:", "TODO");


        // TODO how to properly redraw parent s.t. widths are properly aligned?
        // parent.pack();
        parent.layout();
        // parent.redraw();
        // parent.update();
    }

    private void addLine(Composite parent, String label, String value) {
        Label l = new Label(parent, SWT.NULL);
        l.setText(label);
        Text t = new Text(parent, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        t.setLayoutData(gd);
        t.setText(value);
        t.setEditable(false);
    }
}
