package io.github.ralfspoeth.xmls;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

class BaseTest {

    private static final DocumentBuilderFactory DEFAULT_FACTORY = DocumentBuilderFactory.newDefaultInstance();
    private static final DocumentBuilderFactory NS_FACTORY;
    static {
        NS_FACTORY = DocumentBuilderFactory.newDefaultInstance();
        NS_FACTORY.setNamespaceAware(true);
    }
    private final DocumentBuilder parser;
    private final DocumentBuilder nsParser;

    BaseTest() {
        try {
            parser = DEFAULT_FACTORY.newDocumentBuilder();
            nsParser = NS_FACTORY.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Parse a text into a document instance using a namespace-aware parser.
     *
     * @param src the source text
     * @return the document object
     * @throws SAXException whenever the parser throws
     */
    public Document parseStringNs(String src) throws SAXException {
        try {
            return nsParser.parse(new InputSource(new StringReader(src)));
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Parse a text into a document instance.
     *
     * @param src the source text
     * @return the document object
     * @throws SAXException whenever the parser throws
     */
    public Document parseString(String src) throws SAXException {
        try {
            return parser.parse(new InputSource(new StringReader(src)));
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Parse a resource
     * into a document instance.
     *
     * @param path the path to the resource
     * @return the document object
     * @throws SAXException rethrown from the parser
     * @throws IOException rethrown from the parser
     */
    public Document parseResource(String path) throws SAXException, IOException {
        return parser.parse(new InputSource(getClass().getResourceAsStream(path)));
    }
}
