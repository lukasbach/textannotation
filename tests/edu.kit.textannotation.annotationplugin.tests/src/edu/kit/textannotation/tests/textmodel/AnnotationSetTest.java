package edu.kit.textannotation.tests.textmodel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.kit.textannotation.annotationplugin.textmodel.AnnotationSet;
import edu.kit.textannotation.annotationplugin.textmodel.SingleAnnotation;

public class AnnotationSetTest {
	@Test
	public void testAnnotationTrimming() {
		AnnotationSet set = new AnnotationSet();
		set.addAnnotation(new SingleAnnotation("1", 0, 5, "a", null));
		set.addAnnotation(new SingleAnnotation("2", 10, 15, "b", null));
		
		SingleAnnotation testAnnotation = new SingleAnnotation("3", 2, 13, "c", null);
		set.addAnnotation(testAnnotation);
		assertEquals(5, testAnnotation.getStart());
		assertEquals(9, testAnnotation.getEnd());
	}
}
