package io.github.ralfspoeth.xmls;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

import static io.github.ralfspoeth.xmls.XmlStreams.*;
import static org.junit.jupiter.api.Assertions.*;

class EmptyTest extends BaseTest{
    @Test
    void testEmpty() throws SAXException, IOException {
        // given
        var doc = parseResource("/empty.xml");
        // when
        var root = doc.getDocumentElement();
        // then
        assertAll(
                () -> assertEquals("root", root.getTagName()),
                () -> assertNull(root.getSchemaTypeInfo().getTypeNamespace()),
                () -> assertEquals(0, childNodes(root).count())
        );
    }
}
