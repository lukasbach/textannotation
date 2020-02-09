package edu.kit.textannotation.annotationplugin.editor;

import edu.kit.textannotation.annotationplugin.profile.AnnotationClass;
import edu.kit.textannotation.annotationplugin.textmodel.SingleAnnotation;
import org.eclipse.jface.text.source.Annotation;

class SingleAnnotationEclipseAnnotation extends Annotation {
    /** The spelling annotation type. */
    public static final String TYPE= "edu.kit.textannotation.annotationplugin.editor.SingleAnnotationEclipseAnnotation"; //$NON-NLS-1$

    SingleAnnotationEclipseAnnotation(String content, SingleAnnotation ann, AnnotationClass acl) {
        super(TYPE, false, acl.getDescription().length() > 0 ? acl.getDescription() : acl.getName());
    }
}
