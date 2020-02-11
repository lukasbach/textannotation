package edu.kit.textannotation.annotationplugin.textmodel.xmlinterface;

import edu.kit.textannotation.annotationplugin.textmodel.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static edu.kit.textannotation.annotationplugin.textmodel.XmlSchemaVariables.KEY_ANNOTATIONDATA_ANNOTATION_ELEMENT;

class SingleAnnotationListXmlInterface implements XmlStringParserInterface<List<SingleAnnotation>> {
    private SchemaValidator validator = new SchemaValidator();
    private XmlInterfaceUtils utils = new XmlInterfaceUtils();
    private XmlNodeParserInterface annotationParser = new SingleAnnotationXmlInterface();

    @Override
    public List<SingleAnnotation> parseXml(String source) throws InvalidFileFormatException {
        validator.validateAnnotatedFile(source);

        Element root = null;

        try {
            root = utils.parseXmlFile(source);
        } catch (InvalidFileFormatException e) {
            throw new InvalidAnnotatedFileFormatException(e.getMessage());
        }

        NodeList annotationElements = root.getElementsByTagName(KEY_ANNOTATIONDATA_ANNOTATION_ELEMENT);

        return IntStream
                .rangeClosed(0, annotationElements.getLength() - 1)
                .boxed()
                .collect(Collectors.toList())
                .stream()
                .map(annotationElements::item)
                .map(node -> ((SingleAnnotation)annotationParser.parseXml(node)))
                .collect(Collectors.toList());
    }
}
