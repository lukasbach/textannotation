package edu.kit.textannotation.annotationplugin.textmodel;

/**
 * This exception is thrown if an annotatable text file was read from disk, but is malformed.
 *
 * @see InvalidFileFormatException
 * @see SchemaValidator
 */
public class InvalidAnnotatedFileFormatException extends InvalidFileFormatException {
    private String message;

    public InvalidAnnotatedFileFormatException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
