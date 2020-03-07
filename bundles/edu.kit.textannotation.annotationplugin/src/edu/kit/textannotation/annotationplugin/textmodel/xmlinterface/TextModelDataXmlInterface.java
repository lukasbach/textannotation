package edu.kit.textannotation.annotationplugin.textmodel.xmlinterface;

import edu.kit.textannotation.annotationplugin.textmodel.*;
import org.eclipse.jface.text.Document;
import org.w3c.dom.Element;

import java.util.List;

import static edu.kit.textannotation.annotationplugin.textmodel.XmlSchemaVariables.*;

/**
 * This Xml interface allows for generating an XML string based on a {@link TextModelData} data structure,
 * as well as parsing a {@link TextModelData} data structure from a given XML string.
 *
 * @see TextModelData
 */
public class TextModelDataXmlInterface
        implements XmlBuilderInterface<TextModelData>, XmlStringParserInterface<TextModelData> {
    private SchemaValidator validator = new SchemaValidator();
    private XmlStringParserInterface<String> profileIdParser = new AnnotatedFileProfileIdXmlInterface();
    private XmlStringParserInterface<String> contentParser = new AnnotatedFileContentXmlInterface();
    private XmlStringParserInterface<List<SingleAnnotation>> annotationParser = new SingleAnnotationListXmlInterface();
    private XmlInterfaceUtils utils = new XmlInterfaceUtils();

    @Override
    public String buildXml(TextModelData textModelData) {
        org.w3c.dom.Document doc = utils.getNewDocument();

        Element root = doc.createElement(KEY_ANNOTATEDFILE_ELEMENT);
        doc.appendChild(root);

        Element profileEl = doc.createElement(KEY_ANNOTATEDFILE_PROFILE_ELEMENT);
        profileEl.setAttribute(KEY_ANNOTATEDFILE_PROFILE_ATTR_ID, textModelData.getProfileId());
        root.appendChild(profileEl);

        textModelData.getAnnotations().stream().forEach(annotation -> {
            Element annotationEl = doc.createElement(KEY_ANNOTATIONDATA_ANNOTATION_ELEMENT);
            annotationEl.setAttribute(KEY_ANNOTATIONDATA_ANNOTATION_ATTR_ID, annotation.getId());
            annotationEl.setAttribute(KEY_ANNOTATIONDATA_ANNOTATION_ATTR_OFFSET, "" + annotation.getOffset());
            annotationEl.setAttribute(KEY_ANNOTATIONDATA_ANNOTATION_ATTR_LENGTH, "" + annotation.getLength());
            annotationEl.setAttribute(KEY_ANNOTATIONDATA_ANNOTATION_ATTR_ANNOTATION_IDENTIFIER, annotation.getAnnotationClassId());
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

        return utils.convertDocumentToString(doc);
    }

    @Override
    public TextModelData parseXml(String source) throws InvalidFileFormatException {
        validator.validateAnnotatedFile(source);

        return new TextModelData(
            new AnnotationSet(annotationParser.parseXml(source)),
            profileIdParser.parseXml(source),
            new Document(contentParser.parseXml(source))
        );
    }
}
