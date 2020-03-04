package edu.kit.textannotation.annotationplugin.textmodel.xmlinterface;

import org.w3c.dom.Node;

/**
 * Clients implementing this interface define a way of parsing an XML node into a data structure.
 *
 * This should be used if the XML data is a subnode of another XML node. Otherwise, i.e. the parsed XML
 * structure is already the root node of the XML document, the interface {@link XmlStringParserInterface}
 * should be used to define a way of parsing an XML document as a string directly.
 * @param <T> specifies the type of the data structure that the XML node is being parsed into.
 * @see XmlBuilderInterface
 */
interface XmlNodeParserInterface<T> {
    /**
     * Parse an XML node into a Java data structure.
     * @param node the original XML node.
     * @return the parsed data structure.
     */
    public T parseXml(Node node);
}
