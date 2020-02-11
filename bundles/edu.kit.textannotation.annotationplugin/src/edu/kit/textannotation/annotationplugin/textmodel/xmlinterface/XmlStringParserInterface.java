package edu.kit.textannotation.annotationplugin.textmodel.xmlinterface;

import edu.kit.textannotation.annotationplugin.textmodel.InvalidFileFormatException;

public interface XmlStringParserInterface<T> {
    public T parseXml(String source) throws InvalidFileFormatException;
}
