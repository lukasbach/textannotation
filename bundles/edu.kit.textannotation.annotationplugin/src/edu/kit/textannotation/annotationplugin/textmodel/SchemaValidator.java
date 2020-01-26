package edu.kit.textannotation.annotationplugin.textmodel;

import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

public class SchemaValidator {
    private enum SchemaName {
        AnnotatedFile,
        AnnotationProfile,
    }

    public class InvalidFileFormatException extends Exception {}

    public class InvalidAnnotatedFileFormatException extends InvalidFileFormatException {
        private String message;

        public InvalidAnnotatedFileFormatException(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    public class InvalidAnnotationProfileFormatException extends InvalidFileFormatException {
        private String message;

        public InvalidAnnotationProfileFormatException(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    public void validateAnnotatedFile(String xml) throws InvalidFileFormatException {
        validate(SchemaName.AnnotatedFile, xml);
    }

    public void validateAnnotationProfile(String xml) throws InvalidFileFormatException {
        validate(SchemaName.AnnotationProfile, xml);
    }

    public void throwInvalidAnnotatedFileFormatException() throws InvalidFileFormatException {
        throw new InvalidAnnotatedFileFormatException("anonymous annotated file format exception");
    }

    public void throwInvalidAnnotationProfileFileFormatException() throws InvalidFileFormatException {
        throw new InvalidAnnotationProfileFormatException("anonymous profile format exception");
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
                return SchemaValidator.class.getResourceAsStream("../../../../../schema/annotatedfile.xsd");
            case AnnotationProfile:
                return SchemaValidator.class.getResourceAsStream("../../../../../schema/annotationprofile.xsd");
        }

        return null;
    }
}
