package io.github.ralfspoeth.xmls;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;

import static io.github.ralfspoeth.xmls.XmlStreams.streamAllElems;
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
    void testStreamAllElems() throws Exception {
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
                () -> assertEquals(6, streamAllElems(doc).count()),
                () -> assertEquals(1, streamAllElems(doc).filter(e -> e.getTagName().equals("root")).count()),
                () -> assertEquals(2, streamAllElems(doc).filter(e -> e.getTagName().equals("e1")).count()),
                () -> assertEquals(3, streamAllElems(doc).filter(e -> e.getTagName().equals("e2")).count())
        );
    }

    @Test
    void streamElemsOf() {
    }

    @Test
    void testStreamElemsOf() {
    }
}