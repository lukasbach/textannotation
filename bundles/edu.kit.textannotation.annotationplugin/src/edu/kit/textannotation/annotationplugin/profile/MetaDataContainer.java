package edu.kit.textannotation.annotationplugin.profile;

import edu.kit.textannotation.annotationplugin.utils.EventManager;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * <p>This container object can store key-value pairs, and is used in several locations of the plugin:</p>
 *
 * <ul>
 *     <li>An annotation class can store metadata</li>
 *     <li>A specific {@link edu.kit.textannotation.annotationplugin.textmodel.SingleAnnotation} can store metadata</li>
 * </ul>
 *
 * <p>There also exists a view which specifically can display metadata, alongside the option to edit this data, which
 * is {@link edu.kit.textannotation.annotationplugin.views.MetaDataView}.</p>
 *
 * <p>{@link MetaDataContainer::fromEmpty} and {@link MetaDataContainer::withEntry} can be used for chaining the
 * instance construction, i.e.:</p>
 *
 * <pre>
 *     MetaDataContainer.fromEmpty()
 *       .withEntry("key1", "value1")
 *       .withEntry("key2", "value2")
 *       .withEntry("key3", "value3");
 * </pre>
 *
 * @see edu.kit.textannotation.annotationplugin.views.MetaDataView
 * @see edu.kit.textannotation.annotationplugin.textmodel.SingleAnnotation
 * @see AnnotationClass
 */
public class MetaDataContainer {
    private Map<String, String> metaData;

    /** This event fires when some datum pair was changed in this metadata set. */
    public final EventManager<EventManager.EmptyEvent> onChange = new EventManager<>("metadata:change");

    /**
     * This class models a key-value pair which is stored in the meta data container. Its variables key and
     * value are modelled as nonfinal public fields as public reading and writing is supposed to be possible
     * from all clients.
     */
    public class MetaDataEntry {
        public String key; //NOSONAR
        public String value; //NOSONAR

        MetaDataEntry(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    /** Create a new empty metadata container. */
    public MetaDataContainer() {
        this.metaData = new HashMap<>(8);
    }

    /** Create a new empty metadata container. Convenience constructor for chainability. */
    public static MetaDataContainer fromEmpty() {
        return new MetaDataContainer();
    }

    /** Extend the dataset by one entry, and return the container instance. Convenience constructor for chainability. */
    public MetaDataContainer withEntry(String key, String value) {
        put(key, value);
        return this;
    }

    /**
     * Set the value at the supplied key, potentially removing existing fields.
     */
    public void put(String key, String value) {
        metaData.put(key, value);
        onChange.fire(new EventManager.EmptyEvent());
    }

    /** Remove the value at the specified key, if one exists. */
    public void remove(String key) {
        metaData.remove(key);
        onChange.fire(new EventManager.EmptyEvent());
    }

    /**
     * Stream the key-value pairs as {@link MetaDataEntry}-instances.
     * @return a stream on the fields of this container.
     */
    public Stream<MetaDataEntry> stream() {
        return metaData.keySet()
                .stream()
                .map(key -> new MetaDataEntry(key, metaData.get(key)));
    }

    /**
     * Check whether a field is specified at the supplied key.
     * @param key where the existence of a field is checked.
     * @return true if the field exists at the supplied key, false otherwise.
     */
    public boolean contains(String key) {
        return metaData.containsKey(key);
    }

    /** Remove all fields in this container. */
    public void clear() {
        metaData.clear();
        onChange.fire(new EventManager.EmptyEvent());
    }

    /** Return the number of fields in this container. */
    public int size() {
        return metaData.size();
    }
}
