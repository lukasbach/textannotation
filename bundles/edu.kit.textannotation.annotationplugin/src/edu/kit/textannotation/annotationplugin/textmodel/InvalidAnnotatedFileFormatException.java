package edu.kit.textannotation.annotationplugin.textmodel;

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
