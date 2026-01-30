package io.github.ralfspoeth.xmls;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.stream.Stream;

import static io.github.ralfspoeth.xmls.XmlFunctions.*;
import static io.github.ralfspoeth.xmls.XmlStreams.*;
import static org.junit.jupiter.api.Assertions.*;

class SimpleTest extends BaseTest {

    private Document simple() throws IOException, SAXException {
        return super.parseResource("/simple.xml");
    }

    @Test
    void testStructure() throws SAXException, IOException {
        // given
        var doc = simple();
        // when
        var root = doc.getDocumentElement();
        // then
        assertAll(
                // three children with tag "a"
                () -> assertEquals(3, Stream.of(root)
                        .flatMap(elements("a"))
                        .count()),
                () -> assertEquals(1, Stream.of(root)
                        .flatMap(elements("b"))
                        .flatMap(elements("c"))
                        .flatMap(elements("d"))
                        .count()),
                () -> assertEquals(1234, Stream.of(root)
                        .flatMap(elements("b"))
                        .flatMap(elements("c"))
                        .flatMap(elements("d"))
                        .flatMap(elements("e"))
                        .findFirst()
                        .map(Element::getTextContent)
                        .map(String::trim)
                        .map(Integer::parseInt)
                        .orElseThrow())
        );
    }
}
