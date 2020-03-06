package edu.kit.textannotation.tests.textmodel;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.Document;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.junit.Before;
import org.junit.Test;

import edu.kit.textannotation.annotationplugin.profile.AnnotationClass;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;
import edu.kit.textannotation.annotationplugin.textmodel.AnnotationSet;
import edu.kit.textannotation.annotationplugin.textmodel.InvalidFileFormatException;
import edu.kit.textannotation.annotationplugin.textmodel.SingleAnnotation;
import edu.kit.textannotation.annotationplugin.textmodel.TextModelData;
import edu.kit.textannotation.annotationplugin.textmodel.xmlinterface.AnnotationProfileXmlInterface;
import edu.kit.textannotation.annotationplugin.textmodel.xmlinterface.TextModelDataXmlInterface;
import edu.kit.textannotation.annotationplugin.textmodel.xmlinterface.XmlBuilderInterface;
import edu.kit.textannotation.annotationplugin.textmodel.xmlinterface.XmlStringParserInterface;

public class ParserTests {
	String profileXml;
	String annotationFileXml;
	XmlStringParserInterface<AnnotationProfile> profileParser;
	XmlStringParserInterface<TextModelData> annotatedFileParser;
	XmlBuilderInterface<AnnotationProfile> profileBuilder;
	XmlBuilderInterface<TextModelData> annotatedFileBuilder;
	
	@Before
	public void setup() {
		profileParser = new AnnotationProfileXmlInterface();
		annotatedFileParser = new TextModelDataXmlInterface();
		profileBuilder = new AnnotationProfileXmlInterface();
		annotatedFileBuilder = new TextModelDataXmlInterface();
		
		profileXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n" + 
				"<annotationprofile name=\"Profilename\">\r\n" + 
				"  <annotationclass color=\"1, 2, 3\" name=\"Subject\"/>\r\n" + 
				"  <annotationclass color=\"4, 5, 6\" name=\"Object\"/>\r\n" + 
				"  <annotationclass color=\"7, 8, 9\" name=\"Verb\">\r\n" + 
				"    <metadata name=\"metadatakey\">metadatavalue</metadata>\\r\\n" +
				"  </annotationclass>\r\n" + 
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
	
	@Test
	public void testParseAnnotationProfile() throws InvalidFileFormatException {
		AnnotationProfile profile = profileParser.parseXml(profileXml);
		assertEquals(profile.getName(), "Profilename");
		assertEquals(profile.getAnnotationClasses().size(), 3);
		assertArrayEquals(profile.getAnnotationClassNames(), new String[] {"Subject", "Object", "Verb"});
		assertEquals(profile.getAnnotationClasses().get(0).getColorAsTextModelString(), "1, 2, 3");
		assertTrue(profile.getAnnotationClasses().get(2).metaData.contains("metadatakey"));
		assertEquals(profile.getAnnotationClasses().get(2).metaData.stream()
				.filter(e -> e.key.equals("metadatakey")).findAny().orElse(null).value, "metadatavalue");
		
	}
	
	@Test
	public void testParseAnnotationFileAnnotationData() throws InvalidFileFormatException {
		TextModelData data = annotatedFileParser.parseXml(annotationFileXml);
		
		assertEquals(data.getDocument().get(), "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor");
		assertEquals(data.getAnnotations().getAnnotations().get(0).getAnnotationIdentifier(), "Object");
		assertEquals(data.getAnnotations().getAnnotations().get(1).getAnnotationIdentifier(), "Subject");
		assertEquals(data.getAnnotations().getAnnotations().get(2).getAnnotationIdentifier(), "Verb");

		assertEquals(data.getAnnotations().getAnnotations().get(0).getId(), "a");
		assertEquals(data.getAnnotations().getAnnotations().get(0).getLength(), 2);
		assertEquals(data.getAnnotations().getAnnotations().get(0).getOffset(), 0);
		
		assertTrue(data.getAnnotations().getAnnotations().get(2).metaData.contains("metadatakey"));
		assertEquals(data.getAnnotations().getAnnotations().get(2).metaData.stream()
				.filter(e -> e.key.equals("metadatakey")).findAny().orElse(null).value, "metadatavalue");
	}
	
	@Test
	public void testBuildAnnotationProfile()  {
		AnnotationProfile profile = new AnnotationProfile("Profilename");
		profile.addAnnotationClass(new AnnotationClass("Subject", new Color(Display.getCurrent(), 1, 2, 3)));
		profile.addAnnotationClass(new AnnotationClass("Object", new Color(Display.getCurrent(), 4, 5, 6)));
		
		AnnotationClass aclWithMetadata = new AnnotationClass("Verb", new Color(Display.getCurrent(), 7, 8, 9));
		aclWithMetadata.metaData.put("metadatakey", "metadatavalue");
		
		profile.addAnnotationClass(aclWithMetadata);
		
		assertEquals(clean(profileBuilder.buildXml(profile)), clean(profileXml));
	}

	@Test
	public void testBuildAnnotationFile()  {
		SingleAnnotation annotationWithMetadata = new SingleAnnotation("c", 10, 4, "Verb");
		annotationWithMetadata.metaData.put("metadatakey", "metadatavalue");
		
		TextModelData tmd = new TextModelData(
			new AnnotationSet(
				Arrays.asList(
					new SingleAnnotation("a", 0, 2, "Object"),
					new SingleAnnotation("b", 5, 3, "Subject"),
					annotationWithMetadata
				)
			), 
			"Profilename", 
			new Document("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor")
		);

		assertEquals(clean(annotatedFileBuilder.buildXml(tmd)), clean(annotationFileXml));
		
	}
	
	private String clean(String raw) {
		return raw.replaceAll("\\s+", "").replaceAll("\\\\r+", "").replaceAll("\\\\n+", "");
	}
	
}
