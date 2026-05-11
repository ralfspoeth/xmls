package io.github.ralfspoeth.xmls;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XmlStreamsTest extends BaseTest {

    @Test
    void testStreamNodeList() {
        var src = """
                <?xml version='1.0'?>
                <root name='root'>
                    <node n='1'/>
                    <node n='2'/>
                    <node n='3'/>
                </root>
                """;
        var doc = parseString(src);
        var root = doc.getDocumentElement();
        var sumN = XmlStreams.childNodes(root)
                .peek(n -> System.out.println(n.getClass().getName() + ":" + n.getNodeName() + "/" +  n.getTextContent().trim()))
                .filter(Element.class::isInstance)
                .map(Element.class::cast)
                .map(e -> e.getAttribute("n"))
                .mapToInt(Integer::parseInt)
                .reduce(0, Integer::sum);
        assertAll(()->assertEquals(6, sumN));
    }

    @Test
    void testAttributesEmptyWhenNoAttributes() {
        // given
        var src = """
                <?xml version='1.0'?>
                <root>
                    <child/>
                </root>
                """;
        // when
        var doc = parseString(src);
        var root = doc.getDocumentElement();
        var child = XmlStreams.childNodes(root)
                .filter(Element.class::isInstance)
                .map(Element.class::cast)
                .findFirst()
                .orElseThrow();
        // then
        assertAll(
                // an element without attributes yields an empty stream
                () -> assertEquals(0L, XmlStreams.attributes(child).count()),
                // a non-element node (text) reports no attributes (getAttributes() returns null)
                () -> assertEquals(0L,
                        XmlStreams.childNodes(root)
                                .filter(n -> !(n instanceof Element))
                                .flatMap(XmlStreams::attributes)
                                .count())
        );
    }

    @Test
    void testDescendantElements() {
        // given
        var src = """
                <?xml version='1.0'?>
                <root>
                    <a>
                        <b/>
                        <b/>
                    </a>
                    <a>
                        <c/>
                    </a>
                </root>""";
        // when
        var doc = parseString(src);
        var root = doc.getDocumentElement();
        // then
        assertAll(
                // 5 descendants of <root>: two <a>, two <b>, one <c>
                () -> assertEquals(5L, XmlStreams.descendantElements(root).count()),
                // by tag name
                () -> assertEquals(2L, XmlStreams.descendantElements(root, "a").count()),
                () -> assertEquals(2L, XmlStreams.descendantElements(root, "b").count()),
                () -> assertEquals(1L, XmlStreams.descendantElements(root, "c").count()),
                () -> assertEquals(0L, XmlStreams.descendantElements(root, "missing").count()),
                // root itself is not included in its own descendants
                () -> assertEquals(0L,
                        XmlStreams.descendantElements(root, "root").count())
        );
    }

    @Test
    void testDescendantElementsNamespaced() {
        // given
        var src = """
                <?xml version='1.0'?>
                <root xmlns='http://example.com/default' xmlns:x='http://example.com/x'>
                    <a>plain</a>
                    <x:a>ns-1</x:a>
                    <wrapper>
                        <x:a>ns-2</x:a>
                    </wrapper>
                </root>""";
        // when
        var doc = parseStringNameSpaced(src);
        var root = doc.getDocumentElement();
        // then
        assertAll(
                () -> assertEquals(2L,
                        XmlStreams.descendantElements(root, "http://example.com/x", "a").count()),
                () -> assertEquals(1L,
                        XmlStreams.descendantElements(root, "http://example.com/default", "a").count()),
                // wildcard namespace matches both
                () -> assertEquals(3L,
                        XmlStreams.descendantElements(root, "*", "a").count())
        );
    }

    @Test
    void testAllElements() {
        // given
        var src = """
                <?xml version='1.0'?>
                <root>
                    <e1 id='1'/>
                    <e1 id='2'>
                        <e2 id='3'/>
                        <e2 id='4'/>
                        <e2 id='5'/>
                    </e1>
                </root>""";
        // when
        var doc = parseString(src);
        // then
        assertAll(
                () -> assertEquals(6, XmlStreams.allElements(doc).count()),
                () -> assertEquals(1, XmlStreams.allElements(doc).filter(e -> e.getTagName().equals("root")).count()),
                () -> assertEquals(2, XmlStreams.allElements(doc).filter(e -> e.getTagName().equals("e1")).count()),
                () -> assertEquals(3, XmlStreams.allElements(doc).filter(e -> e.getTagName().equals("e2")).count()),
                () -> assertEquals(
                        List.of("root", "e1", "e1", "e2", "e2", "e2"),
                        XmlStreams.allElements(doc).map(Element::getTagName).toList()
                )
        );
    }
}