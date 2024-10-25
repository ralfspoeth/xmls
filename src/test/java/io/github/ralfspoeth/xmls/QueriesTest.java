package io.github.ralfspoeth.xmls;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import java.math.BigDecimal;

import static io.github.ralfspoeth.xmls.Queries.*;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class QueriesTest extends BaseTest {


    @Test
    void testNumericValues() throws Exception {
        // given
        var src = """
                <?xml version='1.0'?>
                <root a='10'/>
                """;
        // when
        Element root = parseString(src).getDocumentElement();
        Attr a = root.getAttributeNode("a");
        // then
        assertAll(
                () -> assertEquals(10, intValue(a, 0)),
                () -> assertEquals(10L, longValue(a, 0L)),
                () -> assertEquals(10d, doubleValue(a, 0d)),
                () -> assertEquals(BigDecimal.TEN, decimalValue(a, BigDecimal.ZERO))
        );
    }


}
