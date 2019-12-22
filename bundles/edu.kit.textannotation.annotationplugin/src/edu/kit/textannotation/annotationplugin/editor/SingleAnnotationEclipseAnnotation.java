package edu.kit.textannotation.annotationplugin.editor;

import edu.kit.textannotation.annotationplugin.textmodel.SingleAnnotation;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.jface.text.quickassist.IQuickFixableAnnotation;
import org.eclipse.jface.text.source.Annotation;

public class SingleAnnotationEclipseAnnotation extends Annotation implements IQuickFixableAnnotation { // TODO not quickfixable
    /** The spelling annotation type. */
    public static final String TYPE= "edu.kit.textannotation.annotationplugin.editor.SingleAnnotationEclipseAnnotation"; //$NON-NLS-1$

    public SingleAnnotationEclipseAnnotation(String content, SingleAnnotation ann) {
        super(TYPE, false, String.format("%s is marked as %s", content, ann.getAnnotationIdentifier()));
    }

    @Override
    public void setQuickFixable(boolean state) {
    }

    @Override
    public boolean isQuickFixableStateSet() {
        return false;
    }

    @Override
    public boolean isQuickFixable() throws AssertionFailedException {
        return false;
    }
}
