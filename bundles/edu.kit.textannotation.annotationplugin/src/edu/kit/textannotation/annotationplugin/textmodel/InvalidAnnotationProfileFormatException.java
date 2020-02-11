package edu.kit.textannotation.annotationplugin.textmodel;

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
