package edu.kit.textannotation.tests.textmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.kit.textannotation.annotationplugin.textmodel.SingleAnnotation;

public class SingleAnnotationTest {
	SingleAnnotation annotation;
	
	@Before
	public void setup() {
		annotation = new SingleAnnotation("%ID", 1, 2, "%ANNOTATION", null);
	}
	
	@Test
	public void testCreation() {
		assertEquals("%ID", annotation.getId());
		assertEquals(1, annotation.getOffset());
		assertEquals(2, annotation.getLength());
		assertEquals("%ANNOTATION", annotation.getAnnotationIdentifier());
	}
	
	@Test
	public void testPositionCalculations() {
		assertEquals("%ID", annotation.getId());
		assertEquals(1, annotation.getStart());
		assertEquals(2, annotation.getEnd());
		
		annotation.addOffset(1);
		assertEquals(2, annotation.getStart());
		assertEquals(3, annotation.getEnd());
		
		annotation.addLength(1);
		assertEquals(2, annotation.getStart());
		assertEquals(4, annotation.getEnd());

		assertFalse(annotation.containsPosition(1));
		assertTrue(annotation.containsPosition(2));
		assertTrue(annotation.containsPosition(3));
		assertTrue(annotation.containsPosition(4));
		assertFalse(annotation.containsPosition(5));
		
		assertTrue(annotation.isContainedWithin(2, 4));
		assertTrue(annotation.isContainedWithin(1, 4));
		assertTrue(annotation.isContainedWithin(2, 5));
		assertFalse(annotation.isContainedWithin(2, 3));
		assertFalse(annotation.isContainedWithin(3, 4));
	}
}
