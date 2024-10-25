package io.github.ralfspoeth.xmls;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class XmlStreamsTest extends BaseTest {

    @Test
    void testStreamNodeList() throws IOException, SAXException {
        var src = """
                <?xml version='1.0'?>
                <root>
                    <node n='1'/>
                    <node n='2'/>
                    <node n='3'/>
                </root>
                """;
        var doc = parseString(src);
        var nl = doc.getDocumentElement().getChildNodes();
        var sumN = XmlStreams.stream(nl)
                .filter(Element.class::isInstance)
                .map(Element.class::cast)
                .map(e -> e.getAttribute("n"))
                .mapToInt(Integer::parseInt)
                .reduce(0, Integer::sum);
        assertAll(()->assertEquals(6, sumN));
    }

    @Test
    void testStream() {
    }

    @Test
    void streamElemsOf() {
    }

    @Test
    void testStreamElemsOf() {
    }
}