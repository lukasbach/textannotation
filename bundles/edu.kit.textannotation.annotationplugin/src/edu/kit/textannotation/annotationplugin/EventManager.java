package edu.kit.textannotation.annotationplugin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventManager<T> {
	private List<Consumer<T>> listeners;
	
	public class EmptyEvent {};
	
	public EventManager() {
		listeners = new ArrayList<Consumer<T>>();
	}
	
	public EventManager<T> addListener(Consumer<T> listener) {
		listeners.add(listener);
		return this;
	}
	
	public void fire(T payload) {
		listeners.stream().forEach(l -> l.accept(payload));
	}
}
