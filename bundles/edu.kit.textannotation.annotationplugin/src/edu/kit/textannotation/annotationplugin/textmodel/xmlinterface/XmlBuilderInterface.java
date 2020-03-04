package edu.kit.textannotation.annotationplugin.textmodel.xmlinterface;

/**
 * Clients implementing this interface define a way of serializing a Java data structure into an XML string.
 * @param <T> is the type of the original data structure.
 */
public interface XmlBuilderInterface<T> {
    /**
     * Serialize a supplied data structure into an XML string.
     * @param structure is the original data structure.
     * @return the serialized XML string.
     */
    public String buildXml(T structure);
}
