package edu.kit.textannotation.annotationplugin.textmodel;

public class InvalidAnnotationMetaDataKey extends Exception {
    private String message;

    public InvalidAnnotationMetaDataKey(String attemptedKey) {
        message = String.format("The key \"%s\" cannot be used as metadata key, it can only contain lowercase" +
                "letters, uppercase letters, spaces, numbers or any of the following symbols: -_", attemptedKey);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
