import org.jspecify.annotations.NullMarked;

/**
 * Small helper library that exposes the W3C DOM as Java {@link java.util.stream.Stream}s
 * and provides composable {@link java.util.function.Function}s for navigating and
 * extracting typed values from XML documents.
 *
 * <p>The module is {@link org.jspecify.annotations.NullMarked} by default; method
 * parameters and return types are treated as non-null unless explicitly annotated
 * with {@link org.jspecify.annotations.Nullable}.</p>
 */
@NullMarked
module io.github.ralfspoeth.xmls {
    requires java.xml;
    requires static org.jspecify;
    exports io.github.ralfspoeth.xmls;
}