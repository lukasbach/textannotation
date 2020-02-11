package edu.kit.textannotation.annotationplugin.textmodel.xmlinterface;

import edu.kit.textannotation.annotationplugin.textmodel.InvalidAnnotatedFileFormatException;
import edu.kit.textannotation.annotationplugin.textmodel.InvalidFileFormatException;

import static edu.kit.textannotation.annotationplugin.textmodel.XmlSchemaVariables.KEY_PROFILE_ANNOTATIONCLASS_ATTR_NAME;
import static edu.kit.textannotation.annotationplugin.textmodel.XmlSchemaVariables.KEY_PROFILE_ELEMENT;

class AnnotatedFileProfileNameXmlInterface implements XmlStringParserInterface<String> {
    private XmlInterfaceUtils utils = new XmlInterfaceUtils();

    @Override
    public String parseXml(String source) throws InvalidFileFormatException {
        try {
            return utils.parseXmlFile(source)
                    .getElementsByTagName(KEY_PROFILE_ELEMENT)
                    .item(0)
                    .getAttributes()
                    .getNamedItem(KEY_PROFILE_ANNOTATIONCLASS_ATTR_NAME)
                    .getNodeValue();
        } catch (InvalidFileFormatException e) {
            throw new InvalidAnnotatedFileFormatException(e.getMessage());
        }
    }
}
