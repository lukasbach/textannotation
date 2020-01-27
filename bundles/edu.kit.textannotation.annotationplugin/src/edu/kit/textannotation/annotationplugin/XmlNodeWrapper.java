package edu.kit.textannotation.annotationplugin;

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

public class XmlNodeWrapper {
    private final Node node;

    public XmlNodeWrapper(Node node) {
        this.node = node;
    }

    public Optional<Node> findChild(String elementName) {
        return findChild(n -> n.getNodeName().equals(elementName));
    }

    public List<Node> getChilds() {
        NodeList list = node.getChildNodes();
        List<Node> result = new ArrayList<>(list.getLength());
        forEach(result::add);
        return result;
    }

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
