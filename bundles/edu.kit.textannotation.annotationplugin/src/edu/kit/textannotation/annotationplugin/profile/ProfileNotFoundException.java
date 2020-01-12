package edu.kit.textannotation.annotationplugin.profile;

import java.util.stream.Collectors;

public class ProfileNotFoundException extends Exception {
    private String message;

    public ProfileNotFoundException(String profile) {
        message = String.format("Profile %s not found", profile);
    }

    public ProfileNotFoundException(String profile, AnnotationProfileRegistry registry) {
        message = String.format(
                "Profile %s not found, available are %s",
                profile,
                registry.getProfiles().stream().map(AnnotationProfile::getName).collect(Collectors.joining(","))
        );
    }

    @Override
    public String getMessage() {
        return message;
    }
}
