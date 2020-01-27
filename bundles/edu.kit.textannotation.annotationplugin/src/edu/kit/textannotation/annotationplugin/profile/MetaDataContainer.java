package edu.kit.textannotation.annotationplugin.profile;

import edu.kit.textannotation.annotationplugin.EventManager;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class MetaDataContainer {
    private Map<String, String> metaData;
    public EventManager<EventManager.EmptyEvent> onChange = new EventManager<>("metadata:change");

    public class MetaDataEntry {
        public String key;
        public String value;

        MetaDataEntry(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    public MetaDataContainer() {
        this.metaData = new HashMap<>(8);
    }

    public void put(String key, String value) {
        metaData.put(key, value);
        onChange.fire(new EventManager.EmptyEvent());
    }

    public void remove(String key) {
        metaData.remove(key);
        onChange.fire(new EventManager.EmptyEvent());
    }

    public Stream<MetaDataEntry> stream() {
        return metaData.keySet()
                .stream()
                .map(key -> new MetaDataEntry(key, metaData.get(key)));
    }

    public boolean contains(String key) {
        return metaData.containsKey(key);
    }

    public void clear() {
        metaData.clear();
        onChange.fire(new EventManager.EmptyEvent());
    }
}
