package edu.kit.textannotation.annotationplugin.profile;

import edu.kit.textannotation.annotationplugin.textmodel.InvalidAnnotationProfileFormatException;

import java.util.stream.Collectors;

/**
 * Exception for handling cases where a profile was referenced by its name, but it could not be resolved
 * by that name, i.e. no file could be found on disk which specifies a profile with this name.
 */
public class ProfileNotFoundException extends Exception {
    private String message;

    /** Create a new exception instance. */
    public ProfileNotFoundException(String profile) {
        message = String.format("Profile %s not found", profile);
    }

    /** Create a new exception instance. The registry is used to log all available profile names for convenience. */
    public ProfileNotFoundException(String profile, AnnotationProfileRegistry registry) {
        try {
            message = String.format(
                    "Profile %s not found, available are %s",
                    profile,
                    registry.getProfiles().stream().map(AnnotationProfile::getId)
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
