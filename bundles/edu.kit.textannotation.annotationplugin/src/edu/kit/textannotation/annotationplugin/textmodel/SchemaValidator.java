package edu.kit.textannotation.annotationplugin.textmodel;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * <p>This class defines a way for validating XML strings against their respective schemas. Currently
 * the following types of structures can be validated with this class:</p>
 *
 * <ul>
 *     <li>Annotatable text files, using {@link SchemaValidator::validateAnnotatedFile}</li>
 *     <li>Annotation profile files, using {@link SchemaValidator::validateAnnotationProfile}</li>
 * </ul>
 *
 * <p>It uses the schema files locates in <tt>src/schema</tt>.</p>
 */
public class SchemaValidator {
    private enum SchemaName {
        AnnotatedFile,
        AnnotationProfile,
    }

    /**
     * Validate an XML string which represents an annotatable text file. This validates it
     * against the schema file located at <tt>src/schema/annotatedfile.xsd</tt>.
     * @param xml the XML string which will be validated.
     * @throws InvalidFileFormatException if the validation fails.
     */
    public void validateAnnotatedFile(String xml) throws InvalidFileFormatException {
        validate(SchemaName.AnnotatedFile, xml);
    }

    /**
     * Validate an XML string which represents an annotation profile file. This validates it
     * against the schema file located at <tt>src/schema/annotationprofile.xsd</tt>.
     * @param xml the XML string which will be validated.
     * @throws InvalidFileFormatException if the validation fails.
     */
    public void validateAnnotationProfile(String xml) throws InvalidFileFormatException {
        validate(SchemaName.AnnotationProfile, xml);
    }

    private void validate(SchemaName schema, String xml) throws InvalidFileFormatException {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            Schema schemaObj = factory.newSchema(new StreamSource(getSchemaPath(schema)));
            Validator validator = schemaObj.newValidator();
            validator.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            validator.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            validator.validate(new StreamSource(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))));
        } catch(Exception ex) {
            switch (schema) {
                case AnnotatedFile:
                    throw new InvalidAnnotatedFileFormatException(ex.getMessage());
                case AnnotationProfile:
                    throw new InvalidAnnotationProfileFormatException(ex.getMessage());
            }
        }
    }

    private InputStream getSchemaPath(SchemaName schema) {
        switch (schema) {
            case AnnotatedFile:
                return this.getClass().getClassLoader().getResourceAsStream("schema/annotatedfile.xsd");
            case AnnotationProfile:
                return this.getClass().getClassLoader().getResourceAsStream("schema/annotationprofile.xsd");
        }

        return null;
    }
}
