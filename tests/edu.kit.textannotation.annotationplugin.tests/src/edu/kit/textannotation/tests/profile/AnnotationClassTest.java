package edu.kit.textannotation.tests.profile;

import static org.junit.Assert.assertEquals;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.junit.Test;

import edu.kit.textannotation.annotationplugin.profile.AnnotationClass;

public class AnnotationClassTest {
	
	@Test
	public void testAccessors() {
		AnnotationClass acl = new AnnotationClass("%NAME", new Color(Display.getCurrent(), 255, 255, 255), "%DESCRIPTION", null);
		assertEquals(acl.getName(), "%NAME");
		assertEquals(acl.getColor(), new Color(Display.getCurrent(), 255, 255, 255));
		assertEquals(acl.getColorAsTextModelString(), "255, 255, 255");
		
		acl.setColor(new Color(Display.getCurrent(), 1, 2, 3));
		assertEquals(acl.getColor(), new Color(Display.getCurrent(), 1, 2, 3));
		assertEquals(acl.getColorAsTextModelString(), "1, 2, 3");
		
		acl.setName("%NAME2");
		assertEquals(acl.getName(), "%NAME2");
	}
}
