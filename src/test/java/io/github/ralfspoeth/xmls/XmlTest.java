package io.github.ralfspoeth.xmls;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
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
    void parsePathYieldsDocument(@org.junit.jupiter.api.io.TempDir Path tmp) throws IOException {
        var p = tmp.resolve("simple.xml");
        Files.writeString(p, SIMPLE);
        var doc = Xml.parse(p);
        assertEquals("root", doc.getDocumentElement().getTagName());
    }

    @Test
    void parseNameSpacedRecognisesNamespaces() {
        var src = """
                <?xml version='1.0'?>
                <root xmlns:x='http://example.com/x' x:a='1'/>
                """;
        var doc = Xml.parseNameSpaced(src);
        var root = doc.getDocumentElement();
        // namespace-aware parsing populates getNamespaceURI on attributes
        assertEquals(
                "http://example.com/x",
                root.getAttributeNodeNS("http://example.com/x", "a").getNamespaceURI()
        );
    }

    @Test
    void parseInvalidXmlThrowsXmlException() {
        // unterminated tag
        var bad = "<root";
        var ex = assertThrows(XmlException.class, () -> Xml.parse(bad));
        assertNotNull(ex.getCause());
    }

    @Test
    void parseMissingFileThrowsXmlException() {
        var missing = Path.of("/this/path/should/not/exist/" + System.nanoTime() + ".xml");
        var ex = assertThrows(XmlException.class, () -> Xml.parse(missing));
        assertNotNull(ex.getCause());
    }
}
