package io.github.ralfspoeth.xmls;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;

import static io.github.ralfspoeth.xmls.Queries.attribute;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class QueriesTest extends BaseTest {


    @Test
    void testIntValue() throws IOException, SAXException {
        var src = """
                <?xml version='1.0'?>
                <root a='10'/>
                """;
        Element root = parseString(src).getDocumentElement();
        assertAll(
                () -> assertEquals(10, Queries.intValue(root.getAttributeNode("a"), 0))
        );
    }


}
