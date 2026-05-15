package io.github.ralfspoeth.xmls;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Convenience entry points for parsing XML into a {@link Document}.
 *
 * <p>Each overload comes in two flavours:</p>
 * <ul>
 *   <li>{@code parse(...)} &mdash; uses the platform default
 *       {@link DocumentBuilderFactory}, which is <em>not</em> namespace-aware.</li>
 *   <li>{@code parseNameSpaced(...)} &mdash; uses a namespace-aware
 *       {@link DocumentBuilderFactory}; required if you intend to read
 *       namespaced names with {@link org.w3c.dom.Element#getNamespaceURI()} or
 *       use the namespace-aware helpers in {@link XmlFunctions} and
 *       {@link XmlStreams}.</li>
 * </ul>
 *
 * <p>All methods throw {@link XmlException} (an unchecked exception) on
 * parse or I/O failure; the original {@link SAXException} or
 * {@link IOException} is preserved as the cause.</p>
 *
 * <p>This class is not intended to be instantiated.</p>
 */
public final class Xml {

    private static final DocumentBuilderFactory DEFAULT_FACTORY = DocumentBuilderFactory.newDefaultInstance();
    private static final DocumentBuilderFactory NAMESPACE_AWARE_FACTORY = namespaceAwareFactory();

    private static DocumentBuilderFactory namespaceAwareFactory() {
        var f = DocumentBuilderFactory.newDefaultInstance();
        f.setNamespaceAware(true);
        return f;
    }

    // prevent instantiation
    private Xml() {}

    private static DocumentBuilder newBuilder(DocumentBuilderFactory factory) {
        try {
            return factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new XmlException("Failed to create DocumentBuilder", e);
        }
    }

    private static Document parse(DocumentBuilderFactory factory, InputSource source) {
        try {
            return newBuilder(factory).parse(source);
        } catch (SAXException | IOException e) {
            throw new XmlException(e);
        }
    }

    /**
     * Parse the given XML string into a {@link Document} (not namespace-aware).
     *
     * @param src the XML source text; must not be {@code null}
     * @return the parsed document
     * @throws XmlException if parsing fails
     */
    public static Document parse(CharSequence src) {
        return parse(DEFAULT_FACTORY, new InputSource(Reader.of(src)));
    }

    /**
     * Parse the given input stream into a {@link Document} (not namespace-aware).
     * The caller retains responsibility for closing the stream.
     *
     * @param in the input stream; must not be {@code null}
     * @return the parsed document
     * @throws XmlException if parsing fails
     */
    public static Document parse(InputStream in) {
        return parse(DEFAULT_FACTORY, new InputSource(in));
    }

    /**
     * Parse the given reader into a {@link Document} (not namespace-aware).
     * The caller retains responsibility for closing the reader.
     *
     * @param reader the reader; must not be {@code null}
     * @return the parsed document
     * @throws XmlException if parsing fails
     */
    public static Document parse(Reader reader) {
        return parse(DEFAULT_FACTORY, new InputSource(reader));
    }

    /**
     * Parse the file at the given path into a {@link Document} (not namespace-aware).
     *
     * @param path the file path; must not be {@code null}
     * @return the parsed document
     * @throws XmlException if parsing fails (including any underlying I/O error)
     */
    public static Document parse(Path path) {
        try (var in = Files.newInputStream(path)) {
            return parse(DEFAULT_FACTORY, new InputSource(in));
        } catch (IOException e) {
            throw new XmlException("Failed to read " + path, e);
        }
    }

    /**
     * Namespace-aware version of {@link #parse(CharSequence)}.
     *
     * @param src the XML source text; must not be {@code null}
     * @return the parsed document
     * @throws XmlException if parsing fails
     */
    public static Document parseNameSpaced(CharSequence src) {
        return parse(NAMESPACE_AWARE_FACTORY, new InputSource(Reader.of(src)));
    }

    /**
     * Namespace-aware version of {@link #parse(InputStream)}.
     *
     * @param in the input stream; must not be {@code null}
     * @return the parsed document
     * @throws XmlException if parsing fails
     */
    public static Document parseNameSpaced(InputStream in) {
        return parse(NAMESPACE_AWARE_FACTORY, new InputSource(in));
    }

    /**
     * Namespace-aware version of {@link #parse(Reader)}.
     *
     * @param reader the reader; must not be {@code null}
     * @return the parsed document
     * @throws XmlException if parsing fails
     */
    public static Document parseNameSpaced(Reader reader) {
        return parse(NAMESPACE_AWARE_FACTORY, new InputSource(reader));
    }

    /**
     * Namespace-aware version of {@link #parse(Path)}.
     *
     * @param path the file path; must not be {@code null}
     * @return the parsed document
     * @throws XmlException if parsing fails (including any underlying I/O error)
     */
    public static Document parseNameSpaced(Path path) {
        try (var in = Files.newInputStream(path)) {
            return parse(NAMESPACE_AWARE_FACTORY, new InputSource(in));
        } catch (IOException e) {
            throw new XmlException("Failed to read " + path, e);
        }
    }
}
