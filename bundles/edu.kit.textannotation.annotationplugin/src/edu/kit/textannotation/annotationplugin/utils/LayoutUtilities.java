package edu.kit.textannotation.annotationplugin.utils;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class LayoutUtilities {
    public LayoutUtilities() {}

    public static class GridLayoutWrapper {
        private GridLayout layout;

        GridLayoutWrapper() {
            layout = new GridLayout();
        }

        public GridLayoutWrapper withNumCols(int numColumns) {
            layout.numColumns = numColumns;
            return this;
        }

        public GridLayoutWrapper withEqualColumnWidth(boolean is) {
            layout.makeColumnsEqualWidth = is;
            return this;
        }

        public GridLayout get() {
            return layout;
        }
    }

    public static class FillLayoutWrapper {
        private FillLayout layout;

        FillLayoutWrapper() {
            layout = new FillLayout();
        }

        public FillLayoutWrapper withVertical() {
            layout.type = SWT.VERTICAL;
            return this;
        }

        public FillLayoutWrapper withHorizontal() {
            layout.type = SWT.HORIZONTAL;
            return this;
        }

        public FillLayout get() {
            return layout;
        }
    }

    public static class GridDataWrapper {
        private GridData gridData;

        public GridDataWrapper() {
            gridData = new GridData();
        }

        public GridDataWrapper withHorizontalAlignment(int alignment) {
            gridData.horizontalAlignment = alignment;
            return this;
        }

        public GridDataWrapper withVerticalAlignment(int alignment) {
            gridData.verticalAlignment = alignment;
            return this;
        }

        public GridDataWrapper withExcessHorizontalSpace(boolean value) {
            gridData.grabExcessHorizontalSpace = value;
            return this;
        }

        public GridDataWrapper withExcessVerticalSpace(boolean value) {
            gridData.grabExcessVerticalSpace = value;
            return this;
        }

        public GridDataWrapper withHorizontalSpan(int span) {
            gridData.horizontalSpan = span;
            return this;
        }

        public GridDataWrapper withVerticalSpan(int span) {
            gridData.verticalSpan = span;
            return this;
        }

        public GridDataWrapper withWidthHint(int hint) {
            gridData.widthHint = hint;
            return this;
        }

        public GridDataWrapper withHeightHint(int hint) {
            gridData.heightHint = hint;
            return this;
        }

        public GridData get() {
            return gridData;
        }
    }

    public GridLayoutWrapper gridLayout() {
        return new GridLayoutWrapper();
    }

    public FillLayoutWrapper fillLayout() {
        return new FillLayoutWrapper();
    }

    public GridDataWrapper gridData() {
        return new GridDataWrapper();
    }

    public GridData completelyFillingGridData() {
        return gridData()
                .withHorizontalAlignment(SWT.FILL).withExcessHorizontalSpace(true)
                .withVerticalAlignment(SWT.FILL).withExcessVerticalSpace(true)
                .get();
    }

    public GridData horizontalFillingGridData() {
        return gridData().withHorizontalAlignment(SWT.FILL).withExcessHorizontalSpace(true).get();
    }

    public GridData verticallyFillingGridData() {
        return gridData().withVerticalAlignment(SWT.FILL).withExcessVerticalSpace(true).get();
    }

    /**
     * Create a composite inside the given parent which contains a vertical scrollbar if the child is too large.
     * When the childs contents get resized, the supplied relayout eventhandler should be fired to notify the
     * scrolled component to update its sizing. The relayout eventhandler should also be initially fired after
     * its contents where created.
     *
     * @param parent which should contain the scrolled composite.
     * @param onReLayout can be fired to update the scrolled component's sizings.
     * @return a container where scrollable contents can be placed into.
     */
    public Composite createVerticalScrollComposite(Composite parent,
                                                   EventManager<EventManager.EmptyEvent> onReLayout) {
        ScrolledComposite scrollContainer = new ScrolledComposite(parent, SWT.V_SCROLL);
        scrollContainer.setLayout(gridLayout().withNumCols(1).get());
        scrollContainer.setLayoutData(completelyFillingGridData());
        scrollContainer.setExpandVertical(true);
        scrollContainer.setExpandHorizontal(true);

        Composite contentContainer = new Composite(scrollContainer, SWT.NULL);
        contentContainer.setLayout(gridLayout().withNumCols(1).get());
        contentContainer.setLayoutData(completelyFillingGridData());

        scrollContainer.setMinHeight(200);

        onReLayout.addListener(e -> scrollContainer.setMinSize(contentContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT)));

        scrollContainer.setContent(contentContainer);

        return contentContainer;
    }
}
