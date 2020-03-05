package edu.kit.textannotation.annotationplugin.utils;

import org.eclipse.jdt.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This utility class defines a way of creating and propagating universal events, without
 * having to explicitly specify custom event listeners everytime.
 * @param <T> the payload that an event carries. If not payload should be carried, the internal class
 *           {@link EventManager::EmptyEvent} should be used as payload type.
 */
public class EventManager<T> {
	private List<Consumer<T>> listeners;
	@Nullable private String name;

	/**
	 * Payload type to use for event managers that should not carry payload with each event.
	 */
	public static class EmptyEvent {};

	/**
	 * Create a new event manager with a specific name. The name will be used for logging.
	 * @param name the name of the event mananger.
	 */
	public EventManager(String name) {
		listeners = new ArrayList<Consumer<T>>();
		this.name = name;
	}

	/**
	 * Create a new event manager which does not log events to the console.
	 */
	public EventManager() {
		listeners = new ArrayList<Consumer<T>>();
	}

	/**
	 * Add a new listener, which will be invoked with event payload everytime a new event is dispatched.
	 * @param listener the handler for future events.
	 * @return a reference to <tt>this</tt> for chainability convenience
	 */
	public EventManager<T> addListener(Consumer<T> listener) {
		listeners.add(listener);
		log(String.format("listener added (%s->%s).", listeners.size() - 1, listeners.size()));
		return this;
	}

	/**
	 * Remove the supplied listener from this event manager, i.e. future events will not be dispatched
	 * via the supplied listener.
	 * @param listener the handler which will be removed.
	 * @return a reference to <tt>this</tt> for chainability convenience
	 */
	public EventManager<T> removeListener(Consumer<T> listener) {
		listeners.remove(listener);
		log(String.format("listener removed (%s->%s).", listeners.size() + 1, listeners.size()));
		return this;
	}

	/**
	 * Dispatch a new event with the given payload. All registered listeners will trigger with
	 * the supplied payload.
	 * @param payload the payload attached to the event.
	 */
	public void fire(T payload) {
		listeners.forEach(l -> l.accept(payload));
		log(String.format("listener fired with payload \"%s\"", payload.toString()));
	}

	/**
	 * Attach a different event manager to this one, i.e. everytime the other supplied event
	 * manager fires, the event will be propagated to all listeners of this event manager.
	 * @param otherEventManager the other event manager whose events will be propagated to listeners of this manager.
	 */
	public void attach(EventManager<T> otherEventManager) {
		otherEventManager.addListener(otherEventManager::fire);
	}

	private void log(String line) {
		if (name != null) {
			System.out.println(String.format("Eventmanager(%s): %s", name, line));
		}
	}
}
