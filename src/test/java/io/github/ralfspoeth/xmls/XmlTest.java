package io.github.ralfspoeth.xmls;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class XmlTest {

    private static final String SIMPLE = """
            <?xml version='1.0'?>
            <root>
                <a/>
            </root>
            """;

    private static final String NAMESPACED = """
            <?xml version='1.0'?>
            <root xmlns:x='http://example.com/x' x:a='1'/>
            """;

    private static final String INVALID = "<root";

    // ------------------------------------------------------------------
    // parse(...) happy paths
    // ------------------------------------------------------------------

    @Test
    void parseStringYieldsDocument() {
        var doc = Xml.parse(SIMPLE);
        assertEquals("root", doc.getDocumentElement().getTagName());
    }

    @Test
    void parseInputStreamYieldsDocument() {
        var in = new ByteArrayInputStream(SIMPLE.getBytes(StandardCharsets.UTF_8));
        var doc = Xml.parse(in);
        assertEquals("root", doc.getDocumentElement().getTagName());
    }

    @Test
    void parseReaderYieldsDocument() {
        var doc = Xml.parse(new StringReader(SIMPLE));
        assertEquals("root", doc.getDocumentElement().getTagName());
    }

    @Test
    void parsePathYieldsDocument(@TempDir Path tmp) throws IOException {
        var p = tmp.resolve("simple.xml");
        Files.writeString(p, SIMPLE);
        var doc = Xml.parse(p);
        assertEquals("root", doc.getDocumentElement().getTagName());
    }

    // ------------------------------------------------------------------
    // parse(...) error paths
    // ------------------------------------------------------------------

    @Test
    void parseInvalidStringThrowsXmlException() {
        var ex = assertThrows(XmlException.class, () -> Xml.parse(INVALID));
        assertNotNull(ex.getCause());
    }

    @Test
    void parseInvalidInputStreamThrowsXmlException() {
        var in = new ByteArrayInputStream(INVALID.getBytes(StandardCharsets.UTF_8));
        var ex = assertThrows(XmlException.class, () -> Xml.parse(in));
        assertNotNull(ex.getCause());
    }

    @Test
    void parseInvalidReaderThrowsXmlException() {
        var ex = assertThrows(XmlException.class, () -> Xml.parse(new StringReader(INVALID)));
        assertNotNull(ex.getCause());
    }

    @Test
    void parseMissingFileThrowsXmlException() {
        var missing = Path.of("/this/path/should/not/exist/" + System.nanoTime() + ".xml");
        var ex = assertThrows(XmlException.class, () -> Xml.parse(missing));
        assertNotNull(ex.getCause());
        assertTrue(ex.getMessage().contains(missing.toString()));
    }

    @Test
    void parseInvalidFileThrowsXmlException(@TempDir Path tmp) throws IOException {
        var p = tmp.resolve("bad.xml");
        Files.writeString(p, INVALID);
        var ex = assertThrows(XmlException.class, () -> Xml.parse(p));
        assertNotNull(ex.getCause());
    }

    // ------------------------------------------------------------------
    // parseNS(...) happy paths
    // ------------------------------------------------------------------

    @Test
    void parseNSStringRecognisesNamespaces() {
        var doc = Xml.parseNS(NAMESPACED);
        var root = doc.getDocumentElement();
        var attr = root.getAttributeNodeNS("http://example.com/x", "a");
        assertEquals("http://example.com/x", attr.getNamespaceURI());
        assertEquals("1", attr.getValue());
    }

    @Test
    void parseNSInputStreamRecognisesNamespaces() {
        var in = new ByteArrayInputStream(NAMESPACED.getBytes(StandardCharsets.UTF_8));
        var doc = Xml.parseNS(in);
        var root = doc.getDocumentElement();
        assertEquals(
                "http://example.com/x",
                root.getAttributeNodeNS("http://example.com/x", "a").getNamespaceURI()
        );
    }

    @Test
    void parseNSReaderRecognisesNamespaces() {
        var doc = Xml.parseNS(new StringReader(NAMESPACED));
        var root = doc.getDocumentElement();
        assertEquals(
                "http://example.com/x",
                root.getAttributeNodeNS("http://example.com/x", "a").getNamespaceURI()
        );
    }

    @Test
    void parseNSPathRecognisesNamespaces(@TempDir Path tmp) throws IOException {
        var p = tmp.resolve("ns.xml");
        Files.writeString(p, NAMESPACED);
        var doc = Xml.parseNS(p);
        var root = doc.getDocumentElement();
        assertEquals(
                "http://example.com/x",
                root.getAttributeNodeNS("http://example.com/x", "a").getNamespaceURI()
        );
    }

    // ------------------------------------------------------------------
    // parseNS(...) error paths
    // ------------------------------------------------------------------

    @Test
    void parseNSInvalidStringThrowsXmlException() {
        var ex = assertThrows(XmlException.class, () -> Xml.parseNS(INVALID));
        assertNotNull(ex.getCause());
    }

    @Test
    void parseNSInvalidInputStreamThrowsXmlException() {
        var in = new ByteArrayInputStream(INVALID.getBytes(StandardCharsets.UTF_8));
        var ex = assertThrows(XmlException.class, () -> Xml.parseNS(in));
        assertNotNull(ex.getCause());
    }

    @Test
    void parseNSInvalidReaderThrowsXmlException() {
        var ex = assertThrows(XmlException.class,
                () -> Xml.parseNS(new StringReader(INVALID)));
        assertNotNull(ex.getCause());
    }

    @Test
    void parseNSMissingFileThrowsXmlException() {
        var missing = Path.of("/this/path/should/not/exist/" + System.nanoTime() + ".xml");
        var ex = assertThrows(XmlException.class, () -> Xml.parseNS(missing));
        assertNotNull(ex.getCause());
        assertTrue(ex.getMessage().contains(missing.toString()));
    }

    @Test
    void parseNSInvalidFileThrowsXmlException(@TempDir Path tmp) throws IOException {
        var p = tmp.resolve("bad-ns.xml");
        Files.writeString(p, INVALID);
        var ex = assertThrows(XmlException.class, () -> Xml.parseNS(p));
        assertNotNull(ex.getCause());
    }

    // ------------------------------------------------------------------
    // Structural / utility coverage
    // ------------------------------------------------------------------

    @Test
    void xmlClassIsFinalAndCannotBeInstantiated() throws Exception {
        assertTrue(Modifier.isFinal(Xml.class.getModifiers()),
                "Xml is expected to be final");
        Constructor<Xml> ctor = Xml.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(ctor.getModifiers()),
                "Xml() must be private");
        ctor.setAccessible(true);
        // invoking the private constructor exercises the otherwise-unreachable
        // bytecode and asserts that it doesn't blow up.
        assertNotNull(ctor.newInstance());
    }

    @Test
    void defaultParserIsNotNamespaceAware() {
        // attribute names with prefixes are kept as-is when not namespace aware
        var doc = Xml.parse(NAMESPACED);
        var root = doc.getDocumentElement();
        // Without namespace awareness, the namespace URI is null on attributes.
        var attr = root.getAttributeNode("x:a");
        assertNotNull(attr, "prefixed attribute should be addressable by qualified name");
        assertNull(attr.getNamespaceURI(),
                "non-namespace-aware parser must not populate the namespace URI");
    }
}
