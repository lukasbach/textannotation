package edu.kit.textannotation.annotationplugin.profile;

import edu.kit.textannotation.annotationplugin.textmodel.InvalidAnnotationProfileFormatException;

import java.util.stream.Collectors;

public class ProfileNotFoundException extends Exception {
    private String message;

    public ProfileNotFoundException(String profile) {
        message = String.format("Profile %s not found", profile);
    }

    public ProfileNotFoundException(String profile, AnnotationProfileRegistry registry) {
        try {
            message = String.format(
                    "Profile %s not found, available are %s",
                    profile,
                    registry.getProfiles().stream().map(AnnotationProfile::getName)
                            .collect(Collectors.joining(", "))
            );
        } catch (InvalidAnnotationProfileFormatException e) {
            message = String.format(
                    "Profile %s not found, and existing profiles could not be parsed (%s).",
                    profile,
                    e.getMessage()
            );
        }
    }

    @Override
    public String getMessage() {
        return message;
    }
}
