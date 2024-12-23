package io.github.ralfspoeth.xmls;

import org.w3c.dom.*;

import java.util.*;
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

    static Stream<Attr> stream(NamedNodeMap mn) {
        final int len = mn.getLength();
        return StreamSupport.stream(
                Spliterators.spliterator(new Iterator<>() {
                    int index = 0;

                    @Override
                    public boolean hasNext() {
                        while(index < len && mn.item(index).getNodeType()!=Node.ATTRIBUTE_NODE) index++;
                        return index < len;
                    }

                    @Override
                    public Attr next() {
                        return (Attr) mn.item(index++);
                    }
                }, len, 0),
                false
        );
    }

    static Stream<Attr> attributes(Node elem) {
        return stream(elem.getAttributes());
    }

    static Stream<Node> stream(NodeList nl) {
        return StreamSupport.stream(Spliterators.spliterator(new Iterator<>() {
                    private int index = 0;

                    @Override
                    public boolean hasNext() {
                        return index < nl.getLength();
                    }

                    @Override
                    public Node next() {
                        return nl.item(index++);
                    }
                }, nl.getLength(), Spliterator.ORDERED | Spliterator.IMMUTABLE), false
        );
    }

    static Stream<Element> streamElemsOf(Element elem, String tagName) {
        return stream(elem.getChildNodes()).filter(n -> n.getNodeType() == Node.ELEMENT_NODE)
                .filter(e -> e.getNodeName().equals(tagName))
                .map(Element.class::cast);
    }

    static Stream<Element> streamElemsOf(Element elem, String tag1, String... moreTags) {
        Stream<Element> str = streamElemsOf(elem, tag1);
        for (String tag : moreTags) {
            str = str.flatMap(e -> streamElemsOf(e, tag));
        }
        return str;
    }

    static Stream<Element> streamAllElems(Document doc) {
        return stream(doc.getElementsByTagName("*")).map(Element.class::cast);
    }
}
