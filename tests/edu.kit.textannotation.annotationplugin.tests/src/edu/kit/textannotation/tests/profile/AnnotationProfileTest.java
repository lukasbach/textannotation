package edu.kit.textannotation.tests.profile;

import static org.junit.Assert.assertEquals;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.junit.Before;
import org.junit.Test;

import edu.kit.textannotation.annotationplugin.profile.AnnotationClass;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;

public class AnnotationProfileTest {
	private AnnotationProfile profile;
	
	@Before
	public void setup() {
		profile = new AnnotationProfile("%PROFILENAME");
		profile.addAnnotationClass(new AnnotationClass("%CLASSNAME", new Color(Display.getCurrent(), 1, 2, 3)));
	}
	
	@Test
	public void testCreation() throws Exception {
		assertEquals("%PROFILENAME", profile.getName());
		assertEquals("1, 2, 3", profile.getAnnotationClass("%CLASSNAME").getColorAsTextModelString());
		assertEquals("%CLASSNAME", profile.getAnnotationClass("%CLASSNAME").getName());
		assertEquals(1, profile.getAnnotationClasses().size());
	}
	
	@Test
	public void testAnnotationClassDeletion() {
		profile.removeAnnotationClass(new AnnotationClass("%CLASSNAME2", new Color(Display.getCurrent(), 4, 5, 6)));
		assertEquals(1, profile.getAnnotationClasses().size());
		profile.removeAnnotationClass(new AnnotationClass("%CLASSNAME", new Color(Display.getCurrent(), 4, 5, 6)));
		assertEquals(0, profile.getAnnotationClasses().size());
	}
	
	@Test
	public void testAnnotationClassAlteration() throws Exception {
		profile.alterAnnotationClass("%CLASSNAME", new AnnotationClass("%CLASSNAME2", new Color(Display.getCurrent(), 4, 5, 6)));
		assertEquals("%CLASSNAME2", profile.getAnnotationClass("%CLASSNAME2").getName());
		assertEquals("4, 5, 6", profile.getAnnotationClass("%CLASSNAME2").getColorAsTextModelString());
	}	
}
