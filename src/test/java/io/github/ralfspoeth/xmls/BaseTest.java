package io.github.ralfspoeth.xmls;

import org.w3c.dom.Document;

import java.io.StringReader;

import static java.util.Objects.requireNonNull;

class BaseTest {

    /**
     * Parse a text into a document instance using a namespace-aware parser.
     *
     * @param src the source text
     * @return the document object
     */
    public Document parseStringNameSpaced(String src) {
        return Xml.parseNameSpaced(new StringReader(src));
    }

    /**
     * Parse a text into a document instance.
     *
     * @param src the source text
     * @return the document object
     */
    public Document parseString(String src) {
        return Xml.parse(new StringReader(src));
    }

    /**
     * Parse a resource
     * into a document instance.
     *
     * @param path the path to the resource
     * @return the document object
     */
    public Document parseResource(String path) {
        return Xml.parse(requireNonNull(getClass().getResourceAsStream(path)));
    }
}
