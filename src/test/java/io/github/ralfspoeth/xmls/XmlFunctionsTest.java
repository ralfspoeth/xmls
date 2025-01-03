package io.github.ralfspoeth.xmls;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static io.github.ralfspoeth.xmls.XmlFunctions.index;
import static io.github.ralfspoeth.xmls.XmlStreams.attributes;
import static org.junit.jupiter.api.Assertions.*;

class XmlFunctionsTest extends BaseTest {

    @Test
    void attributeTest() throws Exception{
        var src = """
                <?xml version='1.0'?>
                <root a='1' b='2' c="2"/>
                """;
        var doc = parseString(src);
        assertAll(
                () -> assertEquals("2", Stream.of(doc.getDocumentElement())
                        .map(XmlFunctions.attribute("b"))
                        .findFirst()
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
                () -> assertEquals(10, XmlFunctions.intValue(a)),
                () -> assertEquals(10, XmlFunctions.intValue(a, 1)),
                () -> assertEquals(10L, XmlFunctions.longValue(a)),
                () -> assertEquals(10L, XmlFunctions.longValue(a, 1L)),
                () -> assertEquals(10d, XmlFunctions.doubleValue(a)),
                () -> assertEquals(10d, XmlFunctions.doubleValue(a, 2d)),
                () -> assertEquals(BigDecimal.TEN, XmlFunctions.decimalValue(a)),
                () -> assertEquals(BigDecimal.TEN, XmlFunctions.decimalValue(a, BigDecimal.ONE)),
                () -> assertTrue(XmlFunctions.booleanValue(b)),
                () -> assertTrue(XmlFunctions.booleanValue(b, true)),
                () -> assertEquals("true", XmlFunctions.stringValue(b)),
                () -> assertEquals("true", XmlFunctions.stringValue(b, "TRUE"))
        );
    }

    @Test
    void  testDateTimeValues() throws Exception {
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
                        XmlFunctions.dateValue(d, LocalDate.now())
                ),
                () -> assertEquals(
                        LocalDate.of(2024, 10, 24)
                                .atTime(12, 34, 56),
                        XmlFunctions.dateTimeValue(t, LocalDateTime.now())
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
    void testIndexAttrName() throws Exception{
        var src = """
                <?xml version='1.0'?>
                <root>
                   <x a='1'/>
                   <y a='2'/>
                   <z a='3'/>
                </root>
                """;
        var doc = parseString(src);
        var map = index(doc.getDocumentElement().getChildNodes(), "a");
        assertAll(
                () -> assertEquals("x", map.get("1").getTagName()),
                () -> assertEquals("y", map.get("2").getTagName()),
                () -> assertEquals("z", map.get("3").getTagName())
        );
    }
}