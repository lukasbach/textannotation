package edu.kit.textannotation.tests.textmodel;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;
import edu.kit.textannotation.annotationplugin.textmodel.InvalidFileFormatException;
import edu.kit.textannotation.annotationplugin.textmodel.SingleAnnotation;
import edu.kit.textannotation.annotationplugin.textmodel.TextModelIntegration;

public class ParserTests {
	String profileXml;
	String annotationFileXml;
	
	@Before
	public void setup() {
		profileXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n" + 
				"<annotationprofile name=\"Profilename\">\r\n" + 
				"  <annotationclass color=\"1, 2, 3\" name=\"Subject\"/>\r\n" + 
				"  <annotationclass color=\"4, 5, 6\" name=\"Object\"/>\r\n" + 
				"  <annotationclass color=\"7, 8, 9\" name=\"Verb\">\r\n" + 
				"    <metadata name=\"metadatakey\">metadatavalue</metadata>\\r\\n" +
				"</annotationprofile>\r\n";
		
		annotationFileXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n" + 
				"<annotated>\r\n" + 
				"  <annotationprofile name=\"Profilename\"/>\r\n" + 
				"  <annotation annotation=\"Object\" id=\"a\" length=\"2\" offset=\"0\"/>\r\n" + 
				"  <annotation annotation=\"Subject\" id=\"b\" length=\"3\" offset=\"5\"/>\r\n" + 
				"  <annotation annotation=\"Verb\" id=\"c\" length=\"4\" offset=\"10\">\r\n" + 
				"    <metadata name=\"metadatakey\">metadatavalue</metadata>\r\n" + 
				"  </annotation>\r\n" + 
				"  <content>Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor</content>\r\n" + 
				"</annotated>\r\n";
	}
	
	@Test @Ignore
	public void testParseAnnotationProfile() throws InvalidFileFormatException {
		AnnotationProfile profile = TextModelIntegration.parseAnnotationProfile(profileXml);
		assertEquals(profile.getName(), "Profilename");
		assertEquals(profile.getAnnotationClasses().size(), 3);
		assertArrayEquals(profile.getAnnotationClassNames(), new String[] {"Subject", "Object", "Verb"});
		assertEquals(profile.getAnnotationClasses().get(0).getColorAsTextModelString(), "1, 2, 3");
		assertTrue(profile.getAnnotationClasses().get(3).metaData.contains("metadatakey"));
		assertEquals(profile.getAnnotationClasses().get(3).metaData.stream()
				.filter(e -> e.key.equals("metadatakey")).findAny().orElse(null), "metadatavalue");
		
	}
	
	@Test @Ignore
	public void testParseAnnotationFileContent() throws InvalidFileFormatException {
		String content = TextModelIntegration.parseContent(annotationFileXml);
		assertEquals(content, "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor");
	}
	
	@Test @Ignore
	public void testParseAnnotationFileAnnotationData() throws InvalidFileFormatException {
		List<SingleAnnotation> annotations = TextModelIntegration.parseAnnotationData(annotationFileXml);
		assertEquals(annotations.get(0).getAnnotationIdentifier(), "Object");
		assertEquals(annotations.get(1).getAnnotationIdentifier(), "Subject");
		assertEquals(annotations.get(2).getAnnotationIdentifier(), "Verb");

		assertEquals(annotations.get(0).getId(), "a");
		assertEquals(annotations.get(0).getLength(), 2);
		assertEquals(annotations.get(0).getOffset(), 0);
		
		assertTrue(annotations.get(3).metaData.contains("metadatakey"));
		assertEquals(annotations.get(3).metaData.stream()
				.filter(e -> e.key.equals("metadatakey")).findAny().orElse(null), "metadatavalue");
	}
	
	
}
