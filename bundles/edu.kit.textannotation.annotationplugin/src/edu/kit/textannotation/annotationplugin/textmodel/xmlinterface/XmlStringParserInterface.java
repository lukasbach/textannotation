package edu.kit.textannotation.annotationplugin.textmodel.xmlinterface;

import edu.kit.textannotation.annotationplugin.textmodel.InvalidFileFormatException;

/**
 * Clients implementing this interface define a way of parsing a XML structure within a string into
 * a Java data structure.
 * @param <T> the Java data structure that the XML string should be parsed into.
 * @see XmlBuilderInterface
 * @see XmlNodeParserInterface
 */
public interface XmlStringParserInterface<T> {
    /**
     * Parse the supplied XML string into the specified data structure.
     * @param source the original XML string.
     * @return the parsed data structure.
     * @throws InvalidFileFormatException if the XML document was malformed or could not be parsed.
     */
    public T parseXml(String source) throws InvalidFileFormatException;
}
