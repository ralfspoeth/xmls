package io.github.ralfspoeth.xmls;

import org.w3c.dom.*;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * The class contains static methods
 * that produce streams of {@link Node}s.
 */
public class XmlStreams {
    // prevent instantiation
    private XmlStreams() {
    }

    /**
     * Turns a {@link NamedNodeMap} into a
     * {@link Stream} of {@link Attr}ibutes.
     *
     * @param mn the attributes map
     */
    private static Stream<Attr> stream(NamedNodeMap mn) {
        final int len = mn.getLength();
        return StreamSupport.stream(
                Spliterators.spliterator(
                        new Iterator<>() {
                            int index = 0;

                            @Override
                            public boolean hasNext() {
                                return index < len;
                            }

                            @Override
                            public Attr next() {
                                return (Attr) mn.item(index++);
                            }
                        },
                        len,
                        Spliterator.ORDERED),
                false
        );
    }

    /**
     * Returns a stream of {@link Attr attributes}.
     *
     * @param node the element node for which we need to have the attributes
     * @return a stream which reports all attribute child nodes of the node.
     * @throws java.util.ConcurrentModificationException when the underlying document is changed.
     */
    public static Stream<Attr> attributes(Node node) {
        return stream(node.getAttributes());
    }

    /**
     * Turns a {@link NodeList list} of nodes
     * into a {@link Stream} of nodes.
     *
     * @param nl a nodelist
     * @return a sequential stream of nodes ordered as the given nodelist
     * @throws java.util.ConcurrentModificationException when the underlying document is changed.
     */
    private static Stream<Node> stream(NodeList nl) {
        final int len = nl.getLength();
        return StreamSupport.stream(Spliterators.spliterator(
                new Iterator<>() {
                    private int index = 0;

                    @Override
                    public boolean hasNext() {
                        return index < len;
                    }

                    @Override
                    public Node next() {
                        return nl.item(index++);
                    }
                },
                len,
                Spliterator.ORDERED), false
        );
    }

    /**
     * Turns the {@link NodeList} of child nodes
     * into a {@link Stream} of these nodes.
     *
     * @param node a nodelist
     * @return a sequential stream of nodes ordered as the given nodelist
     * @throws java.util.ConcurrentModificationException when the underlying document is changed.
     */
    public static Stream<Node> children(Node node) {
        return stream(node.getChildNodes());
    }

    /**
     * provide each and every element node of a document.
     *
     * @param doc the document
     * @return a stream of all element nodes
     */
    public static Stream<Element> allElements(Document doc) {
        return stream(doc.getElementsByTagName("*")).map(Element.class::cast);
    }
}
