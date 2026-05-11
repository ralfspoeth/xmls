package io.github.ralfspoeth.xmls;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static io.github.ralfspoeth.xmls.XmlStreams.attributes;
import static org.junit.jupiter.api.Assertions.*;

class XmlFunctionsTest extends BaseTest {

    @Test
    void attributeTest() throws Exception {
        var src = """
                <?xml version='1.0'?>
                <root a='1' b='2' c="2"/>
                """;
        var doc = parseString(src);
        assertAll(
                () -> assertEquals("2", Optional.of(doc.getDocumentElement())
                        .flatMap(XmlFunctions.attribute("b"))
                        .map(Attr::getValue)
                        .orElseThrow()
                )
        );
    }

    @Test
    void testNumericValues() throws Exception {
        // given
        var src = """
                <?xml version='1.0'?>
                <root a='10' b='true'/>
                """;
        // when
        Element root = parseString(src).getDocumentElement();
        Attr a = root.getAttributeNode("a");
        Attr b = root.getAttributeNode("b");
        // then
        assertAll(
                () -> assertEquals(10, XmlFunctions.intValue(a).orElseThrow()),
                () -> assertEquals(10, XmlFunctions.intValue(a).orElse(1)),
                () -> assertEquals(10L, XmlFunctions.longValue(a).orElseThrow()),
                () -> assertEquals(10L, XmlFunctions.longValue(a).orElse(1L)),
                () -> assertEquals(10d, XmlFunctions.doubleValue(a).orElseThrow()),
                () -> assertEquals(10d, XmlFunctions.doubleValue(a).orElse(2d)),
                () -> assertEquals(BigDecimal.TEN, XmlFunctions.decimalValue(a).orElseThrow()),
                () -> assertEquals(BigDecimal.TEN, XmlFunctions.decimalValue(a).orElse(BigDecimal.ONE)),
                () -> assertTrue(XmlFunctions.booleanValue(b).orElseThrow()),
                () -> assertTrue(XmlFunctions.booleanValue(b).orElse(Boolean.FALSE)),
                () -> assertEquals("true", XmlFunctions.stringValue(b).orElseThrow()),
                () -> assertEquals("true", XmlFunctions.stringValue(b).orElse("TRUE"))
        );
    }

    @Test
    void testDateTimeValues() throws Exception {
        // given
        var src = """
                <?xml version='1.0'?>
                <root d='2024-10-24' t='2024-10-24T12:34:56'/>
                """;
        // when
        Element root = parseString(src).getDocumentElement();
        Attr d = root.getAttributeNode("d");
        Attr t = root.getAttributeNode("t");
        // then
        assertAll(
                () -> assertEquals(
                        LocalDate.of(2024, 10, 24),
                        XmlFunctions.dateValue(d).orElse(LocalDate.now())
                ),
                () -> assertEquals(
                        LocalDate.of(2024, 10, 24)
                                .atTime(12, 34, 56),
                        XmlFunctions.dateTimeValue(t).orElse(LocalDateTime.now())
                )
        );
    }

    @Test
    void testAttributes() throws Exception {
        var src = """
                <?xml version='1.0'?>
                <root a='1' b='2' c='3'/>
                """;
        var doc = parseString(src);
        assertAll(
                () -> assertTrue(Set.of("a", "b", "c").containsAll(attributes(doc.getDocumentElement()).map(Attr::getName).toList())),
                () -> assertTrue(Set.of("1", "2", "3").containsAll(attributes(doc.getDocumentElement()).map(Attr::getValue).toList())),
                () -> assertTrue(attributes(doc.getDocumentElement()).allMatch(a -> a.getValue().equals(
                        Map.of("a", "1", "b", "2", "c", "3").get(a.getName()))
                ))
        );
    }

    @Test
    void testNamespacedAttribute() throws Exception {
        // given
        var src = """
                <?xml version='1.0'?>
                <root xmlns:x='http://example.com/x' x:a='1' a='2'/>
                """;
        // when
        var root = parseStringNs(src).getDocumentElement();
        // then
        assertAll(
                () -> assertEquals(
                        "1",
                        Optional.of(root)
                                .flatMap(XmlFunctions.attribute("http://example.com/x", "a"))
                                .map(Attr::getValue)
                                .orElseThrow()
                ),
                // querying with the wrong namespace returns empty
                () -> assertTrue(Optional.of(root)
                        .flatMap(XmlFunctions.attribute("http://example.com/y", "a"))
                        .isEmpty()),
                // querying with an unknown local name returns empty
                () -> assertTrue(Optional.of(root)
                        .flatMap(XmlFunctions.attribute("http://example.com/x", "missing"))
                        .isEmpty())
        );
    }

    @Test
    void testNamespacedElements() throws Exception {
        // given: elements live in the default namespace, so their nodeName equals
        // the local name — which is what XmlFunctions.elements(ns, localName) filters on.
        var src = """
                <?xml version='1.0'?>
                <root xmlns='http://example.com/default'>
                    <a>plain-1</a>
                    <a>plain-2</a>
                    <b>other</b>
                </root>
                """;
        // when
        var root = parseStringNs(src).getDocumentElement();
        // then
        assertAll(
                () -> assertEquals(
                        2L,
                        Stream.of((Node) root)
                                .flatMap(XmlFunctions.elements("http://example.com/default", "a"))
                                .count()
                ),
                () -> assertEquals(
                        1L,
                        Stream.of((Node) root)
                                .flatMap(XmlFunctions.elements("http://example.com/default", "b"))
                                .count()
                ),
                // namespace that doesn't appear yields no matches
                () -> assertEquals(
                        0L,
                        Stream.of((Node) root)
                                .flatMap(XmlFunctions.elements("http://example.com/none", "a"))
                                .count()
                ),
                // unknown local name yields no matches
                () -> assertEquals(
                        0L,
                        Stream.of((Node) root)
                                .flatMap(XmlFunctions.elements("http://example.com/default", "missing"))
                                .count()
                )
        );
    }

    @Test
    void testChildrenNamed() throws Exception {
        // given
        var src = """
                <?xml version='1.0'?>
                <root>
                    <a/>
                    <b/>
                    <a/>
                    text
                    <a/>
                </root>
                """;
        // when
        var root = parseString(src).getDocumentElement();
        var names = Stream.of((Node) root)
                .flatMap(XmlFunctions.childrenNamed("a"))
                .map(Node::getNodeName)
                .toList();
        // then
        assertAll(
                () -> assertEquals(List.of("a", "a", "a"), names),
                () -> assertEquals(1L, Stream.of((Node) root)
                        .flatMap(XmlFunctions.childrenNamed("b"))
                        .count()),
                () -> assertEquals(0L, Stream.of((Node) root)
                        .flatMap(XmlFunctions.childrenNamed("missing"))
                        .count())
        );
    }

    @Test
    void testOffsetAndZonedDateTimeValues() throws Exception {
        // given
        var src = """
                <?xml version='1.0'?>
                <root o='2024-10-24T12:34:56+02:00' z='2024-10-24T12:34:56+02:00[Europe/Berlin]'/>
                """;
        // when
        Element root = parseString(src).getDocumentElement();
        Attr o = root.getAttributeNode("o");
        Attr z = root.getAttributeNode("z");
        // then
        assertAll(
                () -> assertEquals(
                        OffsetDateTime.of(2024, 10, 24, 12, 34, 56, 0, ZoneOffset.ofHours(2)),
                        XmlFunctions.offsetDateTimeValue(o).orElseThrow()
                ),
                () -> assertEquals(
                        ZonedDateTime.parse("2024-10-24T12:34:56+02:00[Europe/Berlin]"),
                        XmlFunctions.zonedDateTimeValue(z).orElseThrow()
                )
        );
    }

    @Test
    void testBooleanLiterals() throws Exception {
        // given
        var src = """
                <?xml version='1.0'?>
                <root t='true' f='false' one='1' zero='0' bad='yes'/>
                """;
        // when
        Element root = parseString(src).getDocumentElement();
        Attr t = root.getAttributeNode("t");
        Attr f = root.getAttributeNode("f");
        Attr one = root.getAttributeNode("one");
        Attr zero = root.getAttributeNode("zero");
        Attr bad = root.getAttributeNode("bad");
        // then
        assertAll(
                () -> assertEquals(Boolean.TRUE, XmlFunctions.booleanValue(t).orElseThrow()),
                () -> assertEquals(Boolean.FALSE, XmlFunctions.booleanValue(f).orElseThrow()),
                () -> assertEquals(Boolean.TRUE, XmlFunctions.booleanValue(one).orElseThrow()),
                () -> assertEquals(Boolean.FALSE, XmlFunctions.booleanValue(zero).orElseThrow()),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> XmlFunctions.booleanValue(bad))
        );
    }

    @Test
    void testNullAttributeYieldsEmpty() {
        // when/then: every parser called with null returns an empty optional
        assertAll(
                () -> assertTrue(XmlFunctions.intValue(null).isEmpty()),
                () -> assertEquals(42, XmlFunctions.intValue(null).orElse(42)),
                () -> assertTrue(XmlFunctions.longValue(null).isEmpty()),
                () -> assertEquals(42L, XmlFunctions.longValue(null).orElse(42L)),
                () -> assertTrue(XmlFunctions.doubleValue(null).isEmpty()),
                () -> assertEquals(3.14d, XmlFunctions.doubleValue(null).orElse(3.14d)),
                () -> assertTrue(XmlFunctions.decimalValue(null).isEmpty()),
                () -> assertTrue(XmlFunctions.stringValue(null).isEmpty()),
                () -> assertTrue(XmlFunctions.dateValue(null).isEmpty()),
                () -> assertTrue(XmlFunctions.dateTimeValue(null).isEmpty()),
                () -> assertTrue(XmlFunctions.offsetDateTimeValue(null).isEmpty()),
                () -> assertTrue(XmlFunctions.zonedDateTimeValue(null).isEmpty()),
                () -> assertTrue(XmlFunctions.booleanValue(null).isEmpty())
        );
    }
}