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
		profile = new AnnotationProfile("%PROFILEID", "%PROFILENAME");
		profile.addAnnotationClass(new AnnotationClass("%CLASSID", "%CLASSNAME", new Color(Display.getCurrent(), 1, 2, 3)));
	}
	
	@Test
	public void testCreation() throws Exception {
		assertEquals("%PROFILEID", profile.getId());
		assertEquals("%PROFILENAME", profile.getName());
		assertEquals("1, 2, 3", profile.getAnnotationClass("%CLASSID").getColorAsTextModelString());
		assertEquals("%CLASSNAME", profile.getAnnotationClass("%CLASSID").getName());
		assertEquals(1, profile.getAnnotationClasses().size());
	}
	
	@Test
	public void testAnnotationClassDeletion() {
		profile.removeAnnotationClass(new AnnotationClass("%CLASSID2", "%CLASSNAME2", new Color(Display.getCurrent(), 4, 5, 6)));
		assertEquals(1, profile.getAnnotationClasses().size());
		profile.removeAnnotationClass(new AnnotationClass("%CLASSID", "%CLASSNAME", new Color(Display.getCurrent(), 4, 5, 6)));
		assertEquals(0, profile.getAnnotationClasses().size());
	}
	
	@Test
	public void testAnnotationClassAlteration() throws Exception {
		profile.alterAnnotationClass("%CLASSID", new AnnotationClass("%CLASSID", "%CLASSNAME2", new Color(Display.getCurrent(), 4, 5, 6)));
		assertEquals("%CLASSNAME2", profile.getAnnotationClass("%CLASSID").getName());
		assertEquals("4, 5, 6", profile.getAnnotationClass("%CLASSID").getColorAsTextModelString());
	}	
}
