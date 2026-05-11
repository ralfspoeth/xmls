package io.github.ralfspoeth.xmls;

/**
 * Unchecked exception thrown by {@link Xml} when XML parsing or I/O fails.
 *
 * <p>The underlying cause &mdash; typically a
 * {@link org.xml.sax.SAXException} or a {@link java.io.IOException} &mdash;
 * is always preserved via {@link #getCause()}.</p>
 */
public class XmlException extends RuntimeException {

    /**
     * Create a new {@code XmlException} with the given message.
     *
     * @param message the detail message
     */
    public XmlException(String message) {
        super(message);
    }

    /**
     * Create a new {@code XmlException} wrapping the given cause.
     *
     * @param cause the underlying cause; must not be {@code null}
     */
    public XmlException(Throwable cause) {
        super(cause);
    }

    /**
     * Create a new {@code XmlException} with the given message and cause.
     *
     * @param message the detail message
     * @param cause   the underlying cause; must not be {@code null}
     */
    public XmlException(String message, Throwable cause) {
        super(message, cause);
    }
}
