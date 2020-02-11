package edu.kit.textannotation.annotationplugin.textmodel.xmlinterface;

import edu.kit.textannotation.annotationplugin.profile.AnnotationClass;
import edu.kit.textannotation.annotationplugin.profile.AnnotationProfile;
import edu.kit.textannotation.annotationplugin.textmodel.InvalidAnnotationProfileFormatException;
import edu.kit.textannotation.annotationplugin.textmodel.InvalidFileFormatException;
import edu.kit.textannotation.annotationplugin.textmodel.SchemaValidator;
import org.w3c.dom.*;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static edu.kit.textannotation.annotationplugin.textmodel.XmlSchemaVariables.*;
import static edu.kit.textannotation.annotationplugin.textmodel.XmlSchemaVariables.KEY_PROFILE_ANNOTATIONCLASS_DESCRIPTION_ELEMENT;

public class AnnotationProfileXmlInterface implements XmlBuilderInterface<AnnotationProfile>, XmlStringParserInterface<AnnotationProfile> {
    private SchemaValidator validator = new SchemaValidator();
    private XmlInterfaceUtils utils = new XmlInterfaceUtils();
    private XmlNodeParserInterface annotationParser = new AnnotationClassXmlInterface();

    @Override
    public String buildXml(AnnotationProfile profile) {
        Document doc = utils.getNewDocument();

        Element profileElement = doc.createElement(KEY_PROFILE_ELEMENT);
        profileElement.setAttribute(KEY_PROFILE_ATTR_NAME, profile.getName());

        profile.getAnnotationClasses().forEach(annotationClass -> {
            Element classEl = doc.createElement(KEY_PROFILE_ANNOTATIONCLASS_ELEMENT);
            classEl.setAttribute(KEY_PROFILE_ANNOTATIONCLASS_ATTR_NAME, annotationClass.getName());
            classEl.setAttribute(KEY_PROFILE_ANNOTATIONCLASS_ATTR_COLOR, annotationClass.getColorAsTextModelString());

            annotationClass.metaData.stream().forEach(entry -> {
                Element metaDataEL = doc.createElement(KEY_PROFILE_ANNOTATIONCLASS_METADATA_ELEMENT);
                metaDataEL.setAttribute(KEY_PROFILE_ANNOTATIONCLASS_METADATA_ATTR_NAME, entry.key);
                metaDataEL.setTextContent(entry.value);
                classEl.appendChild(metaDataEL);
            });

            if (annotationClass.getDescription().length() > 0) {
                Element descriptionEl = doc.createElement(KEY_PROFILE_ANNOTATIONCLASS_DESCRIPTION_ELEMENT);
                descriptionEl.setTextContent(annotationClass.getDescription());
                classEl.appendChild(descriptionEl);
            }

            profileElement.appendChild(classEl);
        });

        doc.appendChild(profileElement);
        return utils.convertDocumentToString(doc);
    }

    @Override
    public AnnotationProfile parseXml(String rawSource) throws InvalidFileFormatException {
        validator.validateAnnotationProfile(rawSource);

        Element root = null;
        try {
            root = utils.parseXmlFile(rawSource);
        } catch (InvalidFileFormatException e) {
            throw new InvalidAnnotationProfileFormatException(e.getMessage());
        }

        NodeList annotationClassElements = root.getElementsByTagName(KEY_PROFILE_ANNOTATIONCLASS_ELEMENT);
        NamedNodeMap annotationProfileAttributes = root.getAttributes();

        AnnotationProfile profile = new AnnotationProfile(
                annotationProfileAttributes.getNamedItem(KEY_PROFILE_ANNOTATIONCLASS_ATTR_NAME).getTextContent());

        IntStream
                .rangeClosed(0, annotationClassElements.getLength() - 1)
                .boxed()
                .collect(Collectors.toList())
                .stream()
                .map(annotationClassElements::item)
                .map((Node node) -> (AnnotationClass) annotationParser.parseXml(node))
                .forEach(profile::addAnnotationClass);

        return profile;
    }
}
