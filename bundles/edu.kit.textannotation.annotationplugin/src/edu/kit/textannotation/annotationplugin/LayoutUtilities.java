package edu.kit.textannotation.annotationplugin;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

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
}
