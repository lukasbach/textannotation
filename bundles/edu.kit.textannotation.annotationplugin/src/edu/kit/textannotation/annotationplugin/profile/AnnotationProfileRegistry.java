package edu.kit.textannotation.annotationplugin.profile;

import edu.kit.textannotation.annotationplugin.textmodel.InvalidFileFormatException;
import edu.kit.textannotation.annotationplugin.textmodel.xmlinterface.AnnotationProfileXmlInterface;
import edu.kit.textannotation.annotationplugin.utils.EclipseUtils;
import edu.kit.textannotation.annotationplugin.PluginConfig;
import edu.kit.textannotation.annotationplugin.textmodel.InvalidAnnotationProfileFormatException;
import org.osgi.framework.Bundle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/**
 * The Annotation Profile Registry is used to read profiles from disk and resolve a profile by its ID.
 * This can be used by clients that have an instance of
 * {@link edu.kit.textannotation.annotationplugin.textmodel.TextModelData}, which contains annotation data and the
 * ID of the profile, but not the profile itself.
 * <br/><br/>
 *
 * The registry resolves profiles which are located in the following paths:
 *
 * <ul>
 *     <li>%eclispeinstallation%/.textannotation</li>
 *     <li>The workspace directory</li>
 * </ul>
 */
public class AnnotationProfileRegistry {
    private AnnotationProfileXmlInterface annotationProfileXmlInterface = new AnnotationProfileXmlInterface();
    private List<String> registryPaths;
    private List<AnnotationProfile> profiles;

    /** Maps profile ids to their paths */
    private Map<String, Path> profilePathMap;

    /**
     * Create a new registry instance with the supplied paths where the registry will look for
     * profiles. This should usually not be used by clients as this class already implements a
     * usable selection of default-paths. A reference to an registry object can be obtained via
     * {@link AnnotationProfileRegistry::createNew}, which uses sensible defaults.
     * @param registryPaths are paths to where the registry will look for profile files.
     */
    public AnnotationProfileRegistry(List<String> registryPaths) {
        this.registryPaths = registryPaths;
        this.profiles = new LinkedList<>();
        this.profilePathMap = new HashMap<>();
        registryPaths.forEach(p -> System.out.println("REGPATH: " + p));
    }

    /**
     * Create a new registry instance, using sensible lookup defaults.
     * @param bundle is defined by the Eclipse environment.
     * @return a registry instance.
     */
    public static AnnotationProfileRegistry createNew(Bundle bundle) {
        List<String> paths = new ArrayList<>();

        paths.add(System.getProperty("user.dir") + "/.textannotation"); // eclipseinstalldir/.textannotation
        paths.add(EclipseUtils.getCurrentWorkspaceDirectory(bundle)); // workspace directory

        System.out.println("AnnotationProfileRegistry: Reading profiles from the following paths:");
        paths.forEach(System.out::println);

        return new AnnotationProfileRegistry(paths);
    }

    /**
     * Resolve a profile instance by its name.
     * @param profileId the name of the profile, which is used for resolvement.
     * @return a {@link AnnotationProfile} instance if the profile could be resolved.
     * @throws ProfileNotFoundException if the profile was not found on disk.
     * @throws InvalidAnnotationProfileFormatException if the profile file was malformed.
     */
    public AnnotationProfile findProfile(String profileId) throws ProfileNotFoundException, InvalidAnnotationProfileFormatException {
        readProfiles();
        return profiles
                .stream()
                .filter(p -> p.getId().equals(profileId))
                .findFirst()
                .orElseThrow(() -> new ProfileNotFoundException(profileId, this));
    }

    /**
     * Get a list of all profiles which could be found on disk.
     * @return a list of the found profiles.
     * @throws InvalidAnnotationProfileFormatException if one of the profile files was malformed.
     */
    public List<AnnotationProfile> getProfiles() throws InvalidAnnotationProfileFormatException {
        readProfiles();
        return profiles;
    }

    /**
     * Find the profile with the same name on disk, and overwrite its file with the supplied
     * new profile data.
     * @param profile contains the changed profile name alongside the name of the old profile.
     */
    public void overwriteProfile(AnnotationProfile profile) {
        FileOutputStream writeStream = null;
        try {
            Path path = profilePathMap.get(profile.getId());
            String fileContent = annotationProfileXmlInterface.buildXml(profile);
            File file = new File(path.toString());
            writeStream = new FileOutputStream(file, false);
            writeStream.write(fileContent.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            EclipseUtils.reportError("Could not save profile data: " + e.getMessage());
        } finally {
            if (writeStream != null) {
                try {
                    writeStream.close();
                } catch (IOException e) {
                    EclipseUtils.reportError("Could not close profile write stream: " + e.getMessage());
                }
            }
        }
    }

    private void readProfiles() throws InvalidAnnotationProfileFormatException {
        profiles.clear();
        profilePathMap.clear();

        AtomicReference<String> error = new AtomicReference<>(null);

        for (String registryPath: registryPaths) {
            Stream<Path> paths = null;
            try {
                System.out.println("Walking " + registryPath);
                paths = Files.walk(Paths.get(registryPath));

                paths
                    .filter(Files::isRegularFile)
                    .filter(f -> f.getFileName().toString().toLowerCase()
                            .endsWith("." + PluginConfig.ANNOTATION_PROFILE_EXTENSION))
                    .forEach(f -> {
                        try {
                            String s = new String(Files.readAllBytes(f));
                            AnnotationProfile profile = annotationProfileXmlInterface.parseXml(s);
                            System.out.println(String.format("Parsed '%s' from '%s'", profile.getId(), f.toString()));

                            profiles.add(profile);
                            profilePathMap.put(profile.getId(), f);
                        } catch (IOException e) {
                            e.printStackTrace();
                            EclipseUtils.reportError("Could not read profile: " + e.getMessage());
                        } catch (InvalidFileFormatException e) {
                            e.printStackTrace();
                            EclipseUtils.reportError("Profile is improperly formatted: " + e.getMessage());
                        }
                    });
            } catch (IOException e) {
                System.out.println(String.format("Skipping annotation profiles in %s.", registryPath));
            } finally {
                if (paths != null) {
                    paths.close();
                }
            }
        }

        if (error.get() != null) {
            throw new InvalidAnnotationProfileFormatException(error.get());
        }
    }
}
