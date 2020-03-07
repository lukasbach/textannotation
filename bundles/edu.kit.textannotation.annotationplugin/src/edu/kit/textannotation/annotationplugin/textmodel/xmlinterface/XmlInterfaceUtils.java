package edu.kit.textannotation.annotationplugin.textmodel.xmlinterface;

import edu.kit.textannotation.annotationplugin.textmodel.InvalidFileFormatException;
import edu.kit.textannotation.annotationplugin.utils.EclipseUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/**
 * This class defines utility methods for other XML interfaces defined in this package.
 */
class XmlInterfaceUtils {
    /**
     * Create a new XML Document and return a reference to it.
     * @return a new XML document.
     * @see DocumentBuilderFactory
     */
    Document getNewDocument() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.newDocument();
        } catch (ParserConfigurationException e) {
            EclipseUtils.logger().error(e);
            EclipseUtils.reportError("Could not create XML Document builder: " + e.getMessage());
            return null;
        }
    }

    /**
     * Generate a XML string that represents the supplied XML document.
     * @param doc an XML document that should be used as source.
     * @return a XML string representing the supplied document.
     */
    String convertDocumentToString(Document doc) {
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
            EclipseUtils.logger().error(e);
        }

        return "";
    }

    /**
     * Parse the supplied XML source string into an XML element.
     * @param source a string containing XML code.
     * @return a parsed XML element.
     * @throws InvalidFileFormatException if the XML source string was malformed.
     * @see DocumentBuilderFactory
     */
    Element parseXmlFile(String source) throws InvalidFileFormatException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document xml = db.parse(new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)));
            return xml.getDocumentElement();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new InvalidFileFormatException(e.getMessage());
        }
    }
}
