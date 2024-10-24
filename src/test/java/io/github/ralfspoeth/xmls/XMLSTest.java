package io.github.ralfspoeth.xmls;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class XMLSTest {

    private static final DocumentBuilderFactory DEFAULT_FACTORY = DocumentBuilderFactory.newDefaultInstance();
    private DocumentBuilder parser;

    Document parseString(String src) throws IOException, SAXException {
        return parser.parse(new InputSource(new StringReader(src)));
    }

    @BeforeEach
    void setUp() throws ParserConfigurationException {
        parser = DEFAULT_FACTORY.newDocumentBuilder();
    }

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
        var sumN = XMLS.stream(nl)
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