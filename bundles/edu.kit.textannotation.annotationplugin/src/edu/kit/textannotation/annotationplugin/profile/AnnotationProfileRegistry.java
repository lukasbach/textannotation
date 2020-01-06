package edu.kit.textannotation.annotationplugin.profile;

import edu.kit.textannotation.annotationplugin.textmodel.TextModelIntegration;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnnotationProfileRegistry {
    private List<String> registryPaths;

    private List<AnnotationProfile> profiles;

    public AnnotationProfileRegistry(List<String> registryPaths) {
        this.registryPaths = registryPaths;
        this.profiles = new LinkedList<>();
        registryPaths.forEach(p -> System.out.println("REGPATH: " + p));
    }

    public AnnotationProfile findProfile(String profileName) {
        readProfiles();
        return profiles.stream().filter(p -> p.getName().equals(profileName)).findFirst().orElse(null); // TODO
    }

    public List<AnnotationProfile> getProfiles() {
        readProfiles();
        return profiles;
    }

    private void readProfiles() {
        profiles.clear();

        for (String registryPath: registryPaths) {
            try {
                System.out.println("Walking " + registryPath);
                Stream<Path> paths = Files.walk(Paths.get(registryPath));
                profiles.addAll(
                    paths
                        .filter(Files::isRegularFile)
                        .filter(f -> f.getFileName().toString().toLowerCase().endsWith(".xml"))
                        .map(f -> {
                            try {
                                String s = new String(Files.readAllBytes(f));
                                AnnotationProfile profile = TextModelIntegration.parseAnnotationProfile(s);
                                System.out.println(String.format("Parsed '%s' from '%s'", profile.getName(), f.toString()));
                                return profile;
                            } catch (Exception e) {
                                System.out.println(String.format("Skipping %s because %s", f, e.getMessage()));
                                e.printStackTrace();
                                // Genuinely don't care what the problem is, if there is a problem with reading/parsing
                                // the file, it probably is not a annotation profile.
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()
                    )
                );
            } catch (IOException e) {
                System.out.println(String.format("Skipping annotation profiles in %s.", registryPath));
            }
        }
    }
}
