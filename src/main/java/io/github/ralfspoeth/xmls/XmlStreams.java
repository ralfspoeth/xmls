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
    public static Stream<Attr> stream(NamedNodeMap mn) {
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
                        Spliterator.ORDERED | Spliterator.IMMUTABLE),
                false
        );
    }

    /**
     * Returns a stream of {@link Attr attributes}.
     *
     * @param elem the element node for which we need to have the attributes
     * @return a stream which reports all attribute child nodes of the elem.
     */
    public static Stream<Attr> attributes(Node elem) {
        return stream(elem.getAttributes());
    }

    /**
     * Turns a {@link NodeList list} of nodes
     * into a {@link Stream} of nodes.
     *
     * @param nl a nodelist
     * @return a sequential stream of nodes ordered as the given nodelist
     */
    public static Stream<Node> stream(NodeList nl) {
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
                Spliterator.ORDERED | Spliterator.IMMUTABLE), false
        );
    }

    public static Stream<Node> children(Node elem) {
        return stream(elem.getChildNodes());
    }

    public static Stream<Element> elements(Node elem) {
        return children(elem)
                .filter(n -> n.getNodeType() == Node.ELEMENT_NODE)
                .map(Element.class::cast);
    }

    /**
     * The direct child elements of the given element node identified by
     * tag (or node) name.
     *
     * @param elem    the starting point for the search of the children
     * @param tagName the node or tag name of the child elements
     * @return the children als {@link Stream} of {@link Element}s.
     */
    public static Stream<Element> elements(Node elem, String tagName) {
        return elements(elem)
                .filter(e -> e.getNodeName().equals(tagName));
    }

    /**
     * The leaf elements of the given starting point element node
     * reachable by the path {@code tag1/moreTags[0]/.../moreTags[moreTags.length-]}.
     * Given
     * {@code
     * <a>
     * <b>
     * <c>
     * 1
     * </c>
     * <c>
     * 2
     * </c>
     * </b>
     * </a>
     * }
     * then
     * {@snippet :
     * Element a = null; // @replace substring="null;" replacement="..."
     * var l = XmlStreams.elements(a, "b", "c").map(Node::getTextContent).map(String::trim).toList();
     * // l == ["1", "2"]
     *}
     *
     * @param elem     the starting point
     * @param tag1     the mandatory first tag
     * @param moreTags zero or more tags
     * @return a stream of elements
     */
    public static Stream<Element> elements(Node elem, String tag1, String... moreTags) {
        Stream<Element> str = elements(elem, tag1);
        for (String tag : moreTags) {
            str = str.flatMap(e -> elements(e, tag));
        }
        return str;
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
