package edu.kit.textannotation.annotationplugin.profile;

import edu.kit.textannotation.annotationplugin.textmodel.TextModelIntegration;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnnotationProfileRegistry {
    private String registryPath;
    private List<AnnotationProfile> profiles;

    AnnotationProfileRegistry(String registryPath) {
        this.registryPath = registryPath;
        this.profiles = new ArrayList<>();
    }

    public Optional<AnnotationProfile> findProfile(String profileName) {
        readProfiles();
        return profiles.stream().filter(p -> p.getName().equals(profileName)).findFirst();
    }

    private void readProfiles() {
        try {
            Stream<Path> paths = Files.walk(Paths.get(registryPath));
            profiles = paths
                    .filter(Files::isRegularFile)
                    .map(f -> {
                        try {
                            String s = new String(Files.readAllBytes(f));
                            return TextModelIntegration.parseAnnotationProfile(s);
                        } catch (IOException | SAXException | ParserConfigurationException e) {
                            e.printStackTrace();
                        }

                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
