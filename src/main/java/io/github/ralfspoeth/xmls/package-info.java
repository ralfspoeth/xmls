/**
 * Utilities for working with the W3C DOM through the Java
 * {@linkplain java.util.stream.Stream Stream} API.
 *
 * <p>The package contains two utility classes:</p>
 * <ul>
 *   <li>{@link io.github.ralfspoeth.xmls.XmlStreams} &mdash; static methods that turn
 *       {@link org.w3c.dom.NodeList NodeList}s and
 *       {@link org.w3c.dom.NamedNodeMap NamedNodeMap}s into streams of
 *       {@link org.w3c.dom.Node Node}s and {@link org.w3c.dom.Attr Attr}ibutes.</li>
 *   <li>{@link io.github.ralfspoeth.xmls.XmlFunctions} &mdash; static factory methods
 *       for {@link java.util.function.Function}s that navigate the DOM tree, plus
 *       parsers that convert {@link org.w3c.dom.Attr} values to typed
 *       {@link java.util.Optional} results.</li>
 * </ul>
 *
 * <p>All types in this package are
 * {@link org.jspecify.annotations.NullMarked NullMarked}; parameters and return
 * types are non-null unless explicitly annotated otherwise.</p>
 */
package io.github.ralfspoeth.xmls;
