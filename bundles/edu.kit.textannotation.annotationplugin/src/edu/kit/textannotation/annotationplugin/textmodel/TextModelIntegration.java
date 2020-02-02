package edu.kit.textannotation.annotationplugin.textmodel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import edu.kit.textannotation.annotationplugin.profile.AnnotationClass;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static edu.kit.textannotation.annotationplugin.textmodel.XmlSchemaVariables.*;

// TODO refactor visibilities, static methods, add internal state etc
@SuppressWarnings("FieldCanBeLocal")
public class TextModelIntegration {
	private static SchemaValidator validator = new SchemaValidator();


	public static String parseContent(String rawSource) throws SchemaValidator.InvalidFileFormatException {
		validator.validateAnnotatedFile(rawSource);
		Element root = null;

		try {
			root = parseXmlFile(rawSource);
		} catch (ParserConfigurationException | IOException | SAXException e) {
			validator.throwInvalidAnnotatedFileFormatException();
		}

		return root.getElementsByTagName(KEY_ANNOTATIONDATA_CONTENT).item(0).getTextContent();
	}

	public static List<SingleAnnotation> parseAnnotationData(String rawSource) throws SchemaValidator.InvalidFileFormatException {
		validator.validateAnnotatedFile(rawSource);

		Element root = null;

		try {
			root = parseXmlFile(rawSource);
		} catch (ParserConfigurationException | IOException | SAXException e) {
			validator.throwInvalidAnnotatedFileFormatException();
		}

		NodeList annotationElements = root.getElementsByTagName(KEY_ANNOTATIONDATA_ANNOTATION_ELEMENT);

		return IntStream
				.rangeClosed(0, annotationElements.getLength() - 1)
				.boxed()
				.collect(Collectors.toList())
				.stream()
				.map(annotationElements::item)
				.map(TextModelIntegration::parseSingleAnnotation)
				.collect(Collectors.toList());
	}

	static SingleAnnotation parseSingleAnnotation(Node node) {
		NamedNodeMap attributes = node.getAttributes();
		NodeList childs = node.getChildNodes();

		SingleAnnotation annotation = new SingleAnnotation(
				attributes.getNamedItem(KEY_ANNOTATIONDATA_ANNOTATION_ATTR_ID).getTextContent(),
				Integer.parseInt(attributes.getNamedItem(KEY_ANNOTATIONDATA_ANNOTATION_ATTR_OFFSET).getTextContent()),
				Integer.parseInt(attributes.getNamedItem(KEY_ANNOTATIONDATA_ANNOTATION_ATTR_LENGTH).getTextContent()),
				attributes.getNamedItem(KEY_ANNOTATIONDATA_ANNOTATION_ATTR_ANNOTATION_IDENTIFIER).getTextContent(),
				new String[] {}
		);

		for (int i = 0; i < childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child.getNodeName().equals(KEY_ANNOTATIONDATA_METADATA_ELEMENT)) {
				annotation.metaData.put(child.getAttributes().getNamedItem(KEY_ANNOTATIONDATA_METADATA_ATTR_NAME)
						.getTextContent(), child.getTextContent());
			}
		}

		return annotation;
	}

	static String parseProfileName(String rawSource) throws SchemaValidator.InvalidFileFormatException {
		try {
			return parseXmlFile(rawSource)
					.getElementsByTagName(KEY_PROFILE_ELEMENT)
					.item(0)
					.getAttributes()
					.getNamedItem(KEY_PROFILE_ANNOTATIONCLASS_ATTR_NAME)
					.getNodeValue();
		} catch (ParserConfigurationException | IOException | SAXException e) {
			validator.throwInvalidAnnotatedFileFormatException();
			return ""; // TODO
		}
	}

	public static AnnotationProfile parseAnnotationProfile(String rawSource) throws SchemaValidator.InvalidFileFormatException {
		validator.validateAnnotationProfile(rawSource);

		Element root = null;
		try {
			root = parseXmlFile(rawSource);
		} catch (ParserConfigurationException | SAXException e) {
			validator.throwInvalidAnnotationProfileFileFormatException();
		} catch (IOException e) {
			// TODO should not occur
			// e.printStackTrace();
		}

		NodeList annotationClassElements = root.getElementsByTagName(KEY_PROFILE_ANNOTATIONCLASS_ELEMENT);
		NamedNodeMap annotationProfileAttributes = root.getAttributes();

		AnnotationProfile profile = new AnnotationProfile(
				annotationProfileAttributes.getNamedItem(KEY_PROFILE_ANNOTATIONCLASS_ATTR_NAME).getTextContent());

		IntStream
			.rangeClosed(0, annotationClassElements.getLength() - 1)
			.boxed()
			.collect(Collectors.toList())
			.stream()
			.map(annotationClassElements::item)
			.map(AnnotationClass::fromXml)
			.forEach(profile::addAnnotationClass);

		return profile;
	}

	public static String buildAnnotationXml(TextModelData textModelData) throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();

		Element root = doc.createElement(KEY_ANNOTATEDFILE_ELEMENT);
		doc.appendChild(root);

		Element profileEl = doc.createElement(KEY_ANNOTATEDFILE_PROFILE_ELEMENT);
		profileEl.setAttribute(KEY_ANNOTATEDFILE_PROFILE_ATTR_NAME, textModelData.getProfileName());
		root.appendChild(profileEl);

		textModelData.getAnnotations().stream().forEach(annotation -> {
			Element annotationEl = doc.createElement(KEY_ANNOTATIONDATA_ANNOTATION_ELEMENT);
			annotationEl.setAttribute(KEY_ANNOTATIONDATA_ANNOTATION_ATTR_ID, annotation.getId());
			annotationEl.setAttribute(KEY_ANNOTATIONDATA_ANNOTATION_ATTR_OFFSET, "" + annotation.getOffset());
			annotationEl.setAttribute(KEY_ANNOTATIONDATA_ANNOTATION_ATTR_LENGTH, "" + annotation.getLength());
			annotationEl.setAttribute(KEY_ANNOTATIONDATA_ANNOTATION_ATTR_ANNOTATION_IDENTIFIER, annotation.getAnnotationIdentifier());
			annotation.metaData.stream().forEach(metaDataEntry -> {
				Element metaDataEl = doc.createElement(KEY_ANNOTATIONDATA_METADATA_ELEMENT);
				metaDataEl.setTextContent(metaDataEntry.value);
				metaDataEl.setAttribute(KEY_ANNOTATIONDATA_METADATA_ATTR_NAME, metaDataEntry.key);
				annotationEl.appendChild(metaDataEl);
			});
			root.appendChild(annotationEl);
		});

		Element contentEl = doc.createElement(KEY_ANNOTATIONDATA_CONTENT);
		contentEl.setTextContent(textModelData.getDocument().get());
		root.appendChild(contentEl);

		return convertDocumentToString(doc);
	}

	public static String buildProfileXml(AnnotationProfile profile) throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		doc.appendChild(buildProfileElement(profile, doc));
		return convertDocumentToString(doc);
	}

	private static Element buildProfileElement(AnnotationProfile profile, Document doc) {
		Element profileElement = doc.createElement(KEY_PROFILE_ELEMENT);
		profileElement.setAttribute(KEY_PROFILE_ATTR_NAME, profile.getName());

		profile.getAnnotationClasses().forEach(annotationClass -> {
			Element classEl = doc.createElement(KEY_PROFILE_ANNOTATIONCLASS_ELEMENT);
			classEl.setAttribute(KEY_PROFILE_ANNOTATIONCLASS_ATTR_NAME, annotationClass.getName());
			classEl.setAttribute(KEY_PROFILE_ANNOTATIONCLASS_ATTR_COLOR, annotationClass.getColorAsTextModelString());

			annotationClass.metaData.stream().forEach(entry -> {
				Element metaDataEL = doc.createElement(KEY_PROFILE_ANNOTATIONCLASS_METADATA_ELEMENT);
				metaDataEL.setAttribute(KEY_PROFILE_ANNOTATIONCLASS_METADATA_ATTR_NAME, entry.key);
				metaDataEL.setTextContent(entry.value);
				classEl.appendChild(metaDataEL);
			});

			profileElement.appendChild(classEl);
		});

		return profileElement;
	}

	private static String convertDocumentToString(Document doc) {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			// transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			return writer.getBuffer().toString();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static Element parseXmlFile(String rawSource) throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document xml = db.parse(new ByteArrayInputStream(rawSource.getBytes(StandardCharsets.UTF_8)));
		return xml.getDocumentElement();
	}
}
