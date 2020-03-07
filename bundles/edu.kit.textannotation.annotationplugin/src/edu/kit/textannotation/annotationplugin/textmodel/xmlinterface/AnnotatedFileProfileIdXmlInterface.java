package edu.kit.textannotation.annotationplugin.textmodel.xmlinterface;

import edu.kit.textannotation.annotationplugin.textmodel.InvalidAnnotatedFileFormatException;
import edu.kit.textannotation.annotationplugin.textmodel.InvalidFileFormatException;

import static edu.kit.textannotation.annotationplugin.textmodel.XmlSchemaVariables.*;

/**
 * This Xml Interface allows parsing the profile identifier from a the XML source of an annotated text file.
 */
class AnnotatedFileProfileIdXmlInterface implements XmlStringParserInterface<String> {
    private XmlInterfaceUtils utils = new XmlInterfaceUtils();

    @Override
    public String parseXml(String source) throws InvalidFileFormatException {
        try {
            return utils.parseXmlFile(source)
                    .getElementsByTagName(KEY_ANNOTATEDFILE_PROFILE_ELEMENT)
                    .item(0)
                    .getAttributes()
                    .getNamedItem(KEY_ANNOTATEDFILE_PROFILE_ATTR_ID)
                    .getNodeValue();
        } catch (InvalidFileFormatException e) {
            throw new InvalidAnnotatedFileFormatException(e.getMessage());
        }
    }
}
