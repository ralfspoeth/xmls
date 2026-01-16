package io.github.ralfspoeth.xmls;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XmlStreamsTest extends BaseTest {

    @Test
    void testStreamNodeList() throws Exception {
        var src = """
                <?xml version='1.0'?>
                <root>
                    <node n='1'/>
                    <node n='2'/>
                    <node n='3'/>
                </root>
                """;
        var doc = parseString(src);
        var root = doc.getDocumentElement();
        var sumN = XmlStreams.children(root)
                .filter(Element.class::isInstance)
                .map(Element.class::cast)
                .map(e -> e.getAttribute("n"))
                .mapToInt(Integer::parseInt)
                .reduce(0, Integer::sum);
        assertAll(()->assertEquals(6, sumN));
    }

    @Test
    void testAllElements() throws Exception {
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