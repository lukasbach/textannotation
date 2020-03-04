package edu.kit.textannotation.annotationplugin.textmodel.xmlinterface;

import edu.kit.textannotation.annotationplugin.textmodel.InvalidAnnotatedFileFormatException;
import edu.kit.textannotation.annotationplugin.textmodel.InvalidFileFormatException;
import edu.kit.textannotation.annotationplugin.textmodel.SchemaValidator;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static edu.kit.textannotation.annotationplugin.textmodel.XmlSchemaVariables.KEY_ANNOTATIONDATA_CONTENT;

/**
 * This Xml Interface allows parsing the raw text content from a the XML source of an annotated text file.
 */
public class AnnotatedFileContentXmlInterface implements XmlStringParserInterface<String> {
    private SchemaValidator validator = new SchemaValidator();
    private XmlInterfaceUtils utils = new XmlInterfaceUtils();

    @Override
    public String parseXml(String rawSource) throws InvalidFileFormatException {
        validator.validateAnnotatedFile(rawSource);
        Element root = null;

        try {
            root = utils.parseXmlFile(rawSource);
        } catch (InvalidFileFormatException e) {
            throw new InvalidAnnotatedFileFormatException(e.getMessage());
        }

        return root.getElementsByTagName(KEY_ANNOTATIONDATA_CONTENT).item(0).getTextContent();
    }
}
