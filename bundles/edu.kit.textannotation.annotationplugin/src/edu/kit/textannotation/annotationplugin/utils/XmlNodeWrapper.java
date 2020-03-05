package edu.kit.textannotation.annotationplugin.utils;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This utility class wraps XML nodes and implements convenience methods for better XML handling.
 *
 * @see Node
 * @see NodeList
 * @see org.w3c.dom.Element
 */
public class XmlNodeWrapper {
    private final Node node;

    /**
     * Create a new node wrapper on the supplied node.
     */
    public XmlNodeWrapper(Node node) {
        this.node = node;
    }

    /**
     * Find a child node of the relevant node, whose name matches the supplied argument.
     * @param elementName the name of the child to look for.
     * @return potentially the child node if it could be found.
     */
    public Optional<Node> findChild(String elementName) {
        return findChild(n -> n.getNodeName().equals(elementName));
    }

    /**
     * @return a list of child nodes on the relevant node.
     */
    public List<Node> getChilds() {
        NodeList list = node.getChildNodes();
        List<Node> result = new ArrayList<>(list.getLength());
        forEach(result::add);
        return result;
    }

    /**
     * Find a child using the supplied boolean handler to identify the node that is being looked for.
     * @param handler a method that returns true if its argument is the node that is being looked for, and false otherwise.
     * @return potentially the child node if it could be found.
     */
    public Optional<Node> findChild(Predicate<Node> handler) {
        AtomicReference<Node> r = new AtomicReference<>(null);

        forEach(n -> {
            if (handler.test(n)) {
                r.set(n);
            }
        });

        Node result = r.get();
        if (result == null) {
            return Optional.empty();
        } else {
            return Optional.of(result);
        }
    }

    /**
     * A functional for-each implementation on the list of child nodes.
     * @param handler a method that is invoked per child, with the child as argument.
     */
    public void forEach(Consumer<Node> handler) {
        NodeList nodes = node.getChildNodes();

        IntStream
            .rangeClosed(0, nodes.getLength() - 1)
            .boxed()
            .collect(Collectors.toList())
            .stream()
            .map(nodes::item)
            .forEach(handler);
    }
}
