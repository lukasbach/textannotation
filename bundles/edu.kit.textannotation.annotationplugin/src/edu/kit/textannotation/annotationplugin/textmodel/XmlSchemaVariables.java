package edu.kit.textannotation.annotationplugin.textmodel;

/**
 * This class defines all identifiers used by the XML schemas, and is leveraged by
 * the <tt>xmlinterface</tt> package. When changes are made to the schema files, these changes
 * can often be applied in this class.
 */
public class XmlSchemaVariables {
    /** The element name of the root element of an annotated file. */
    public static final String KEY_ANNOTATEDFILE_ELEMENT = "annotated";

    /** The element name of the content element in a annotated file. Child of an annotated tag. */
    public static final String KEY_ANNOTATIONDATA_CONTENT = "content";

    /** The element name of the element that references the used profile in an annotated file. Child of an annotated tag. */
    public static final String KEY_ANNOTATEDFILE_PROFILE_ELEMENT = "annotationprofile";

    /** The attribute key of the name of a profile in the annotationprofile tag which references the used profile in an annotated file. */
    public static final String KEY_ANNOTATEDFILE_PROFILE_ATTR_ID = "id";

    /** The element name of the element that describes a specific annotation. Child of an annotated tag. */
    public static final String KEY_ANNOTATIONDATA_ANNOTATION_ELEMENT = "annotation";

    /** The attribute key of an annotations ID in a annotation element. */
    public static final String KEY_ANNOTATIONDATA_ANNOTATION_ATTR_ID = "id";

    /** The attribute key of an annotations offset in a annotation element. */
    public static final String KEY_ANNOTATIONDATA_ANNOTATION_ATTR_OFFSET = "offset";

    /** The attribute key of an annotations length in a annotation element. */
    public static final String KEY_ANNOTATIONDATA_ANNOTATION_ATTR_LENGTH = "length";

    /** The attribute key of an annotation class in a annotation element. */
    public static final String KEY_ANNOTATIONDATA_ANNOTATION_ATTR_ANNOTATION_IDENTIFIER = "annotation";

    /** The element name of the element that describes a key-value pair of metadata for one annotation.
     * Child of an annotation element. */
    public static final String KEY_ANNOTATIONDATA_METADATA_ELEMENT = "metadata";

    /** The attribute key of the metadata key in a metadata element of an annotated text file. */
    public static final String KEY_ANNOTATIONDATA_METADATA_ATTR_NAME = "name";

    /** The element name of the root element in a profile file. */
    public static final String KEY_PROFILE_ELEMENT = "annotationprofile";

    /** The attribute key of a profiles Id on the root element in a profile file. */
    public static final String KEY_PROFILE_ATTR_ID = "id";

    /** The attribute key of a profiles name on the root element in a profile file. */
    public static final String KEY_PROFILE_ATTR_NAME = "name";

    /** The element name of an annotation class in a profile file. Child of an annotationprofile element. */
    public static final String KEY_PROFILE_ANNOTATIONCLASS_ELEMENT = "annotationclass";

    /** The attribute key of an annotation classes name in an annotationclass element. */
    public static final String KEY_PROFILE_ANNOTATIONCLASS_ATTR_NAME = "name";

    /** The attribute key of an annotation classes id in an annotationclass element. */
    public static final String KEY_PROFILE_ANNOTATIONCLASS_ATTR_ID = "id";

    /** The attribute key of an annotation classes color in an annotationclass element. */
    public static final String KEY_PROFILE_ANNOTATIONCLASS_ATTR_COLOR = "color";

    /** The element name of the description element inside of an annotation class element. */
    public static final String KEY_PROFILE_ANNOTATIONCLASS_DESCRIPTION_ELEMENT = "description";

    /** The element name of the element that describes a key-value pair of metadata for one annotation class.
     * Child of an annotation class element. */
    public static final String KEY_PROFILE_ANNOTATIONCLASS_METADATA_ELEMENT = "metadata";

    /** The attribute key of the metadata key in a metadata element of a profile. */
    public static final String KEY_PROFILE_ANNOTATIONCLASS_METADATA_ATTR_NAME = "name";
}
