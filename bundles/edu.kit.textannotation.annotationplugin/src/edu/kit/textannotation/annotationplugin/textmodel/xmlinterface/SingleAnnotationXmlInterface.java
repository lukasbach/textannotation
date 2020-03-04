package edu.kit.textannotation.annotationplugin.textmodel.xmlinterface;

import edu.kit.textannotation.annotationplugin.textmodel.SingleAnnotation;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static edu.kit.textannotation.annotationplugin.textmodel.XmlSchemaVariables.*;
import static edu.kit.textannotation.annotationplugin.textmodel.XmlSchemaVariables.KEY_ANNOTATIONDATA_METADATA_ATTR_NAME;

/**
 * This Xml interface allows for parsing a {@link SingleAnnotation} instance from an XML node. Note that this
 * node must represent the root of the single annotation substructure.
 *
 * @see SingleAnnotation
 * @see edu.kit.textannotation.annotationplugin.textmodel.TextModelData
 * @see TextModelDataXmlInterface
 */
class SingleAnnotationXmlInterface implements XmlNodeParserInterface<SingleAnnotation> {
    @Override
    public SingleAnnotation parseXml(Node node) {
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
}
