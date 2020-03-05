package edu.kit.textannotation.annotationplugin.textmodel;

/**
 * This exception is thrown if an annotation profile file was read from disk, but is malformed.
 *
 * @see InvalidFileFormatException
 * @see SchemaValidator
 */
public class InvalidAnnotationProfileFormatException extends InvalidFileFormatException {
    private String message;

    public InvalidAnnotationProfileFormatException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
