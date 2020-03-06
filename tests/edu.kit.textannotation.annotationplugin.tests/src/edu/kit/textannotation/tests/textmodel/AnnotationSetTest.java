package edu.kit.textannotation.tests.textmodel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.kit.textannotation.annotationplugin.textmodel.AnnotationSet;
import edu.kit.textannotation.annotationplugin.textmodel.SingleAnnotation;

public class AnnotationSetTest {
	@Test
	public void testAnnotationTrimming() {
		AnnotationSet set = new AnnotationSet();
		set.addAnnotation(new SingleAnnotation("1", 0, 5, "a"));
		set.addAnnotation(new SingleAnnotation("2", 10, 15, "b"));
		
		SingleAnnotation testAnnotation = new SingleAnnotation("3", 2, 13, "c");
		set.addAnnotation(testAnnotation);
		assertEquals(5, testAnnotation.getStart());
		assertEquals(9, testAnnotation.getEnd());
		
		testAnnotation = new SingleAnnotation("3", 9, 16, "c");
		set.addAnnotation(testAnnotation);
		assertEquals(10, testAnnotation.getStart());
		assertEquals(10, testAnnotation.getEnd());
	}
	
	@Test
	public void testLifecycle() {
		AnnotationSet set = new AnnotationSet();
		SingleAnnotation an = new SingleAnnotation("1", 0, 5, "a");
		set.addAnnotation(an);
		assertEquals(1, set.getAnnotations().size());
		assertEquals("AnnotationSet(1)", set.toString());
		set.removeAnnotation(an);
		assertEquals(0, set.getAnnotations().size());
		assertEquals("AnnotationSet(0)", set.toString());
	}
}
