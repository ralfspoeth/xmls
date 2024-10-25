package io.github.ralfspoeth.xmls;

import org.junit.jupiter.api.BeforeEach;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

abstract class BaseTest {

    private static final DocumentBuilderFactory DEFAULT_FACTORY = DocumentBuilderFactory.newDefaultInstance();
    private DocumentBuilder parser;

    Document parseString(String src) throws IOException, SAXException {
        return parser.parse(new InputSource(new StringReader(src)));
    }

    @BeforeEach
    void setUp() throws ParserConfigurationException {
        parser = DEFAULT_FACTORY.newDocumentBuilder();
    }

}
