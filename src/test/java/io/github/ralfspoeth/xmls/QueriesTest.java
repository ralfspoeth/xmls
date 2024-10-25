package io.github.ralfspoeth.xmls;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import java.math.BigDecimal;
import java.time.LocalDate;

import static io.github.ralfspoeth.xmls.Queries.*;
import static org.junit.jupiter.api.Assertions.*;

class QueriesTest extends BaseTest {


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
                () -> assertEquals(10, intValue(a, 0)),
                () -> assertEquals(10L, longValue(a, 0L)),
                () -> assertEquals(10d, doubleValue(a, 0d)),
                () -> assertEquals(BigDecimal.TEN, decimalValue(a, BigDecimal.ZERO)),
                () -> assertTrue(booleanValue(b, false)),
                () -> assertEquals("true", stringValue(b, ""))
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
                        dateValue(d, null)
                ),
                () -> assertEquals(
                        LocalDate.of(2024, 10, 24)
                                .atTime(12, 34, 56),
                        dateTimeValue(t, null)
                )
        );
    }
}
