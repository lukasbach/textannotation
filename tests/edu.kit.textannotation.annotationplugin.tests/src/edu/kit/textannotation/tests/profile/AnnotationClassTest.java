package edu.kit.textannotation.tests.profile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.junit.Test;

import edu.kit.textannotation.annotationplugin.profile.AnnotationClass;

public class AnnotationClassTest {
	
	@Test
	public void testAccessors() {
		AnnotationClass acl = new AnnotationClass("%NAME", new Color(Display.getCurrent(), 255, 255, 255), "%DESCRIPTION");
		acl.setDescription("%DESCR");
		assertEquals(acl.getName(), "%NAME");
		assertEquals(acl.getColor(), new Color(Display.getCurrent(), 255, 255, 255));
		assertEquals(acl.getColorAsTextModelString(), "255, 255, 255");
		assertEquals(acl.getDescription(), "%DESCR");
		
		acl.setColor(new Color(Display.getCurrent(), 1, 2, 3));
		assertEquals(acl.getColor(), new Color(Display.getCurrent(), 1, 2, 3));
		assertEquals(acl.getColorAsTextModelString(), "1, 2, 3");
		
		acl.setName("%NAME2");
		assertEquals(acl.getName(), "%NAME2");

		acl.setDescription("%DESCR2");
		assertEquals(acl.getDescription(), "%DESCR2");
	}
	
	@Test
	public void testMetaData() {
		AnnotationClass acl = new AnnotationClass("%NAME", new Color(Display.getCurrent(), 255, 255, 255), "%DESCRIPTION");
		assertEquals(0, acl.metaData.size());
		
		acl.metaData.put("%KEY1", "%VALUE1");
		assertEquals(1, acl.metaData.size());
		assertEquals("%VALUE1", acl.metaData.stream().filter(v -> v.key.equals("%KEY1")).findAny().get().value);
		assertTrue(acl.metaData.contains("%KEY1"));
		acl.metaData.put("%KEY2", "%VALUE2");
		assertEquals(2,  acl.metaData.size());
		acl.metaData.remove("%KEY1");
		assertEquals(1, acl.metaData.size());
		assertFalse(acl.metaData.contains("%KEY1"));
		acl.metaData.clear();
		assertEquals(0,  acl.metaData.size());
	}
}
