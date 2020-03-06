package edu.kit.textannotation.annotationplugin.textmodel.xmlinterface;

import edu.kit.textannotation.annotationplugin.profile.AnnotationClass;
import edu.kit.textannotation.annotationplugin.utils.XmlNodeWrapper;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static edu.kit.textannotation.annotationplugin.textmodel.XmlSchemaVariables.*;

/**
 * This Xml Interface allows parsing the annotation class instance from a respective XML node. Note that
 * this node must be the root node for the relevant annotation class, and as such is found within the
 * XML document of an annotatable text document.
 *
 * @see TextModelDataXmlInterface
 * @see AnnotationClass
 */
public class AnnotationClassXmlInterface implements XmlNodeParserInterface<AnnotationClass> {
    private XmlInterfaceUtils utils = new XmlInterfaceUtils();

    @Override
    public AnnotationClass parseXml(Node node) {
        XmlNodeWrapper wrappedNode = new XmlNodeWrapper(node);
        NamedNodeMap attributes = node.getAttributes();
        List<Integer> rgb = Arrays.stream(attributes.getNamedItem("color")
                .getTextContent().replace(" ", "").split(","))
                .map(Integer::parseInt).collect(Collectors.toList());
        Color color = new Color(Display.getCurrent(), rgb.get(0), rgb.get(1), rgb.get(2));

        String description = wrappedNode.findChild(KEY_PROFILE_ANNOTATIONCLASS_DESCRIPTION_ELEMENT)
                .map(Node::getTextContent).orElse(null);

        AnnotationClass annotationClass = new AnnotationClass(
                attributes.getNamedItem("name").getTextContent(),
                color,
                description
        );

        wrappedNode.forEach(n -> {
            if (n.getNodeName().equals(KEY_PROFILE_ANNOTATIONCLASS_METADATA_ELEMENT)) {
                annotationClass.metaData.put(n.getAttributes()
                        .getNamedItem(KEY_PROFILE_ANNOTATIONCLASS_METADATA_ATTR_NAME)
                        .getTextContent(), n.getTextContent());
            }
        });

        return annotationClass;
    }
}
