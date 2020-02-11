package edu.kit.textannotation.annotationplugin.textmodel.xmlinterface;

import edu.kit.textannotation.annotationplugin.textmodel.InvalidFileFormatException;
import org.w3c.dom.Node;

interface XmlNodeParserInterface<T> {
    public T parseXml(Node node);
}
