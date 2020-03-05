package edu.kit.textannotation.annotationplugin.textmodel;

/**
 * This exception is thrown if a data text file was read from disk, but is malformed.
 *
 * @see InvalidAnnotatedFileFormatException
 * @see InvalidAnnotationProfileFormatException
 * @see SchemaValidator
 */
public class InvalidFileFormatException extends Exception {
    public InvalidFileFormatException(String message) {
        super(message);
    }
}
