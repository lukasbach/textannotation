package edu.kit.textannotation.annotationplugin.profile;

import edu.kit.textannotation.annotationplugin.EclipseUtils;
import edu.kit.textannotation.annotationplugin.PluginConfig;
import edu.kit.textannotation.annotationplugin.textmodel.InvalidAnnotationProfileFormatException;
import edu.kit.textannotation.annotationplugin.textmodel.SchemaValidator;
import edu.kit.textannotation.annotationplugin.textmodel.TextModelIntegration;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class AnnotationProfileRegistry {
    private List<String> registryPaths;

    private List<AnnotationProfile> profiles;

    /** Maps profile names to their paths */
    private Map<String, Path> profilePathMap;

    public AnnotationProfileRegistry(List<String> registryPaths) {
        this.registryPaths = registryPaths;
        this.profiles = new LinkedList<>();
        this.profilePathMap = new HashMap<>();
        registryPaths.forEach(p -> System.out.println("REGPATH: " + p));
    }

    public static AnnotationProfileRegistry createNew(Bundle bundle) {
        List<String> paths = new ArrayList<>();
        paths.add(System.getProperty("user.dir") + "/.textannotation"); // userdir/.textannotation
        paths.add(Platform.getStateLocation(bundle).toString() + "/profiles"); // workspace/.metadata/.textannotation
        // paths.add(Objects.requireNonNull(EclipseUtils.getCurrentProjectDirectory()).toString()); // workspace/project/
        paths.add(EclipseUtils.getCurrentWorkspaceDirectory(bundle));
        return new AnnotationProfileRegistry(paths);
    }

    public AnnotationProfile findProfile(String profileName) throws ProfileNotFoundException, InvalidAnnotationProfileFormatException {
        readProfiles();
        return profiles
                .stream()
                .filter(p -> p.getName().equals(profileName))
                .findFirst()
                .orElseThrow(() -> new ProfileNotFoundException(profileName, this));
    }

    public List<AnnotationProfile> getProfiles() throws InvalidAnnotationProfileFormatException {
        readProfiles();
        return profiles;
    }

    public void overwriteProfile(AnnotationProfile profile) {
        try {
            Path path = profilePathMap.get(profile.getName());
            String fileContent = TextModelIntegration.buildProfileXml(profile);
            File file = new File(path.toString());
            FileOutputStream writeStream = new FileOutputStream(file, false);
            writeStream.write(fileContent.getBytes());
            writeStream.close();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO
            e.printStackTrace();
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }
    }

    private void readProfiles() throws InvalidAnnotationProfileFormatException {
        profiles.clear();
        profilePathMap.clear();

        AtomicReference<String> error = new AtomicReference<>(null);

        for (String registryPath: registryPaths) {
            try {
                System.out.println("Walking " + registryPath);
                Stream<Path> paths = Files.walk(Paths.get(registryPath));

                paths
                    .filter(Files::isRegularFile)
                    .filter(f -> f.getFileName().toString().toLowerCase()
                            .endsWith("." + PluginConfig.ANNOTATION_PROFILE_EXTENSION))
                    .forEach(f -> {
                        try {
                            String s = new String(Files.readAllBytes(f));
                            AnnotationProfile profile = TextModelIntegration.parseAnnotationProfile(s);
                            System.out.println(String.format("Parsed '%s' from '%s'", profile.getName(), f.toString()));

                            profiles.add(profile);
                            profilePathMap.put(profile.getName(), f);
                        } catch (Exception e) {
                            error.set(e.getMessage());
                        }
                    });
            } catch (IOException e) {
                System.out.println(String.format("Skipping annotation profiles in %s.", registryPath));
            }
        }

        if (error.get() != null) {
            throw new InvalidAnnotationProfileFormatException(error.get());
        }
    }
}
