package edu.kit.textannotation.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfileRegistry;

public class AbcTest {

    @Test
    public void test() {
        // just an example
        assertTrue(true);
        AnnotationProfileRegistry.createNew(null);
    }

}