package edu.kit.textannotation.annotationplugin.textmodel;

import edu.kit.textannotation.annotationplugin.EventManager;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class SingleAnnotation {
	private String id;
	private int offset;
	private int length;
	private String annotationIdentifier;
	private String[] references;
	private Map<String, String> metaData;

	public EventManager<EventManager.EmptyEvent> onMetaDataChange = new EventManager<>("singleAnnotation:metaDataChange");
	public EventManager<EventManager.EmptyEvent> onLocationChange = new EventManager<>("singleAnnotation:locationChange");

	public class MetaDataEntry {
		public String xmlKey;
		public String readableKey;
		public String value;

		MetaDataEntry(String xmlKey, String readableKey, String value) {
			this.xmlKey = xmlKey;
			this.readableKey = readableKey;
			this.value = value;
		}
	}

	public SingleAnnotation(String id, int offset, int length, String annotationIdentifier, String[] references) {
		this.id = id;
		this.offset = offset;
		this.length = length;
		this.annotationIdentifier = annotationIdentifier;
		this.references = references;
		this.metaData = new HashMap<>();
	}

	static SingleAnnotation fromXmlNode(Node node) {
		NamedNodeMap attributes = node.getAttributes();

		SingleAnnotation annotation = new SingleAnnotation(
				attributes.getNamedItem("id").getTextContent(),
				Integer.parseInt(attributes.getNamedItem("offset").getTextContent()),
				Integer.parseInt(attributes.getNamedItem("length").getTextContent()),
				attributes.getNamedItem("annotation").getTextContent(),
				new String[] {}
		);

		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			if (attribute.getNodeName().startsWith("data-")) {
				try {
					annotation.putMetaDataEntry(attribute.getNodeName().substring(5), attribute.getNodeValue());
				} catch (InvalidAnnotationMetaDataKey invalidAnnotationMetaDataKey) {
					// TODO
					invalidAnnotationMetaDataKey.printStackTrace();
				}
			}
		}

		return annotation;
	}

	public String getId() {
		return id;
	}

	public int getOffset() {
		return offset;
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
		onLocationChange.fire(new EventManager.EmptyEvent());
	}
	
	public void addOffset(int offset) {
		this.offset += offset;
		onLocationChange.fire(new EventManager.EmptyEvent());
	}

	public int getLength() {
		return length;
	}
	
	public void setLength(int length) {
		this.length = length;
		onLocationChange.fire(new EventManager.EmptyEvent());
	}
	
	public void addLength(int length) {
		this.length += length;
		onLocationChange.fire(new EventManager.EmptyEvent());
	}

	public boolean containsPosition(int pos) {
		return getStart() <= pos && getEnd() >= pos;
	}

	public boolean isContainedWithin(int start, int end) {
		return start <= getStart() && end >= getEnd();
	}

	public void setStart(int start) {
		setOffset(start);
	}

	public void setEnd(int end) {
		setLength(end - getStart() + 1);
	}

	public int getStart() {
		return getOffset();
	}

	public int getEnd() {
		return getOffset() + getLength() - 1;
	}

	public void putMetaDataEntry(String key, String value) throws InvalidAnnotationMetaDataKey {
		metaData.put(cleanDataMapKey(key), value);
		onMetaDataChange.fire(new EventManager.EmptyEvent());
	}

	public void removeMetaDataEntry(String key) throws InvalidAnnotationMetaDataKey {
		metaData.remove(cleanDataMapKey(key));
		onMetaDataChange.fire(new EventManager.EmptyEvent());
	}

	public Stream<MetaDataEntry> streamMetaData() {
		return metaData.keySet()
				.stream()
				.map(key -> new MetaDataEntry(key, key.replace("_", " "), metaData.get(key)));
	}

	public void clearMetaData() {
		metaData.clear();
		onMetaDataChange.fire(new EventManager.EmptyEvent());
	}

	public String getAnnotationIdentifier() {
		return annotationIdentifier;
	}

	public String[] getReferences() {
		return references;
	}
	
	public String toString() {
		return String.format("SingleAnnotation(annotation=%s, offset=%s, length=%s)", getAnnotationIdentifier(), getOffset(), getLength());
	}

	/**
	 * Clean a supplied key so that it can be used as XML attribute key. Spaces or similar symbols will
	 * be replaced with a '_' symbol.
	 * @param key the original key
	 * @return the cleaned key
	 * @throws InvalidAnnotationMetaDataKey if the key contains invalid characters or is empty.
	 */
	private String cleanDataMapKey(String key) throws InvalidAnnotationMetaDataKey {
		if (key != null && key.matches("[a-zA-Z0-9\\-\\_\\s]+")) {
			return key.replaceAll("\\s", "_");
		} else {
			throw new InvalidAnnotationMetaDataKey(key);
		}
	}
}
