package edu.kit.textannotation.annotationplugin.textmodel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.jface.text.IDocument;

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

// TODO refactor visibilities, static methods, add internal state etc
public class TextModelIntegration {
	private IDocument document;
	
	public TextModelIntegration(IDocument document) {
		this.document = document;
	}

	public static String parseContent(String rawSource) throws IOException, SAXException, ParserConfigurationException {
		Element root = parseXmlFile(rawSource);
		return root.getElementsByTagName("content").item(0).getTextContent();
	}

	public static List<SingleAnnotation> parseAnnotationData(String rawSource) throws ParserConfigurationException, IOException, SAXException {
		Element root = parseXmlFile(rawSource);
		NodeList annotationElements = root.getElementsByTagName("annotation");

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
				attributes.getNamedItem("id").getTextContent(),
				Integer.parseInt(attributes.getNamedItem("offset").getTextContent()),
				Integer.parseInt(attributes.getNamedItem("length").getTextContent()),
				attributes.getNamedItem("annotation").getTextContent(),
				new String[] {}
		);

		for (int i = 0; i < childs.getLength(); i++) {
			Node child = childs.item(i);
			if (child.getNodeName().equals("metadata")) {
				annotation.putMetaDataEntry(child.getAttributes().getNamedItem("name").getTextContent(), child.getTextContent());
			}
		}

		return annotation;
	}

	static String parseProfileName(String rawSource) throws IOException, SAXException, ParserConfigurationException {
		return parseXmlFile(rawSource)
				.getElementsByTagName("annotationprofile")
				.item(0)
				.getAttributes()
				.getNamedItem("name")
				.getNodeValue();
	}

	public static AnnotationProfile parseAnnotationProfile(String rawSource) throws IOException, SAXException, ParserConfigurationException {
		Element root = parseXmlFile(rawSource);
		if (!root.getTagName().equals("annotationprofile")) {
			throw new ParserConfigurationException(""); // TODO
		}

		Node annotationProfileElement = root; // TODO
		NodeList annotationClassElements = root.getElementsByTagName("annotationclass");
		NamedNodeMap annotationProfileAttributes = annotationProfileElement.getAttributes();

		AnnotationProfile profile = new AnnotationProfile(annotationProfileAttributes.getNamedItem("name").getTextContent());

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

		Element root = doc.createElement("annotated");
		doc.appendChild(root);

		Element profileEl = doc.createElement("annotationprofile");
		profileEl.setAttribute("name", textModelData.getProfileName());
		root.appendChild(profileEl);

		textModelData.getAnnotations().stream().forEach(annotation -> {
			Element annotationEl = doc.createElement("annotation");
			annotationEl.setAttribute("id", annotation.getId());
			annotationEl.setAttribute("offset", "" + annotation.getOffset());
			annotationEl.setAttribute("length", "" + annotation.getLength());
			annotationEl.setAttribute("annotation", annotation.getAnnotationIdentifier());
			annotation.streamMetaData().forEach(metaDataEntry -> {
				Element metaDataEl = doc.createElement("metadata");
				metaDataEl.setTextContent(metaDataEntry.value);
				metaDataEl.setAttribute("name", metaDataEntry.key);
				annotationEl.appendChild(metaDataEl);
			});
			root.appendChild(annotationEl);
		});

		Element contentEl = doc.createElement("content");
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
		Element profileElement = doc.createElement("annotationprofile");
		profileElement.setAttribute("name", profile.getName());

		profile.getAnnotationClasses().forEach(annotationClass -> {
			Element classEl = doc.createElement("annotationclass");
			classEl.setAttribute("name", annotationClass.getName());
			classEl.setAttribute("color", annotationClass.getColorAsTextModelString());
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
