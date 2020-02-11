package edu.kit.textannotation.annotationplugin.textmodel;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SchemaValidator {
    private enum SchemaName {
        AnnotatedFile,
        AnnotationProfile,
    }

    public void validateAnnotatedFile(String xml) throws InvalidFileFormatException {
        validate(SchemaName.AnnotatedFile, xml);
    }

    public void validateAnnotationProfile(String xml) throws InvalidFileFormatException {
        validate(SchemaName.AnnotationProfile, xml);
    }

    private void validate(SchemaName schema, String xml) throws InvalidFileFormatException {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schemaObj = factory.newSchema(new StreamSource(getSchemaPath(schema)));
            Validator validator = schemaObj.newValidator();
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
