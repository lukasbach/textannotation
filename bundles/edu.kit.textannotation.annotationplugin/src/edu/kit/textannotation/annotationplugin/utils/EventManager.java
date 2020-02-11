package edu.kit.textannotation.annotationplugin.utils;

import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventManager<T> {
	private List<Consumer<T>> listeners;
	@Nullable private String name;
	
	public static class EmptyEvent {};
	
	public EventManager(String name) {
		listeners = new ArrayList<Consumer<T>>();
		this.name = name;
	}

	public EventManager() {
		listeners = new ArrayList<Consumer<T>>();
	}
	
	public EventManager<T> addListener(Consumer<T> listener) {
		listeners.add(listener);
		log(String.format("listener added (%s->%s).", listeners.size() - 1, listeners.size()));
		return this;
	}

	public EventManager<T> removeListener(Consumer<T> listener) {
		listeners.remove(listener);
		log(String.format("listener removed (%s->%s).", listeners.size() + 1, listeners.size()));
		return this;
	}
	
	public void fire(T payload) {
		listeners.forEach(l -> l.accept(payload));
		log(String.format("listener fired with payload \"%s\"", payload.toString()));
	}

	public void attach(EventManager<T> otherEventManager) {
		otherEventManager.addListener(otherEventManager::fire);
	}

	private void log(String line) {
		if (name != null) {
			System.out.println(String.format("Eventmanager(%s): %s", name, line));
		}
	}
}
