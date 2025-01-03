package io.github.ralfspoeth.xmls;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.ralfspoeth.xmls.XmlStreams.stream;
import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

public class XmlFunctions {
    // prevent instantiation
    private XmlFunctions() {
    }


    /**
     * Create a function which returns the {@link Attr} attribute
     * named {@code name} when applied to an {@link Element}
     *
     * @param name the unqualified name of the attribute
     * @return a function when applied to an element returns the attribute identified by the given unqualified name,
     * may be {@code null}
     */
    public static Function<Element, Attr> attribute(String name) {
        return e -> e.getAttributeNode(name);
    }

    /**
     * Similar to {@link #attribute(String)} but using namespace-qualified
     * attribute names.
     *
     * @param ns        the namespace URI
     * @param localName the local name
     * @return a function when applied to an element returns the attribute identified by the given qualified name,
     * may be {@code null}
     */
    public static Function<Element, Attr> attribute(String ns, String localName) {
        return e -> e.getAttributeNodeNS(ns, localName);
    }

    public static int intValue(Attr attribute, int def) {
        return ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .mapToInt(Integer::parseInt)
                .findFirst()
                .orElse(def);
    }

    public static int intValue(Attr attribute) {
        return intValue(attribute, 0);
    }

    public static long longValue(Attr attribute, long def) {
        return ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .mapToLong(Long::parseLong)
                .findFirst()
                .orElse(def);
    }

    public static long longValue(Attr attribute) {
        return longValue(attribute, 0L);
    }

    public static double doubleValue(Attr attribute, double def) {
        return ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .mapToDouble(Double::parseDouble)
                .findFirst()
                .orElse(def);
    }

    public static double doubleValue(Attr attribute) {
        return doubleValue(attribute, 0d);
    }

    public static BigDecimal decimalValue(Attr attribute, BigDecimal def) {
        return ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .map(BigDecimal::new)
                .findFirst()
                .orElse(def);
    }

    public static BigDecimal decimalValue(Attr attribute) {
        return decimalValue(attribute, BigDecimal.ZERO);
    }

    public static String stringValue(Attr attribute, String def) {
        return ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .findFirst()
                .orElse(def);
    }

    /**
     * Join the trimmed, non-blank node value of each child node
     * with a single space character as delimiter.
     *
     * @param node the node the text content of which is to be extracted.
     * @param delimiter the delimiter for the join operation
     * @return the joined string
     */
    public static String concatTextNodes(Node node, String delimiter) {
        return stream(node.getChildNodes())
                .map(Node::getNodeValue)
                .map(String::trim)
                .filter(not(String::isBlank))
                .collect(Collectors.joining(delimiter));
    }

    public static String stringValue(Attr attribute) {
        return stringValue(attribute, "");
    }

    public static LocalDateTime dateTimeValue(Attr attribute, LocalDateTime def) {
        return ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .map(LocalDateTime::parse)
                .findFirst()
                .orElse(def);
    }

    public static boolean booleanValue(Attr attribute, boolean def) {
        return ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .map(Boolean::parseBoolean)
                .findFirst()
                .orElse(def);
    }

    public static boolean booleanValue(Attr attribute) {
        return booleanValue(attribute, false);
    }

    public static LocalDate dateValue(Attr attribute, LocalDate def) {
        return ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .map(LocalDate::parse)
                .findFirst()
                .orElse(def);
    }

    /**
     * Create a map of keys - probably strings - to elements.
     *
     * @param nl      a node list
     * @param indexBy a function the returns the key for each element node
     * @param <T>     the type of the key
     * @return the map
     */
    public static <T> Map<T, Element> index(NodeList nl, Function<Element, T> indexBy) {
        return stream(nl)
                .filter(Element.class::isInstance)
                .map(Element.class::cast)
                .collect(Collectors.toMap(indexBy, Function.identity()));
    }

    /**
     * Create a map of the values of some attribute of each element node in the node list
     * to the nodes of this list.
     * Consider this snippet:
     * {@snippet :
     * import java.util.Map;
     * import org.w3c.dom.Document;
     * var xml = """
     * <?xml version='1.0'?>
     * <root>
     * <e id='1'/>
     * <e id='2'/>
     * <e id='3'/>
     * </root>
     * """;
     * // parse document
     * Document doc = null; // @replace regex="null" replacement="parse(xml)"
     * // index by attribute named id
     * var indexedByAttributeID = index(doc.getDocumentElement().getElementsByTagName("e"), "id");
     * // creates a map like this
     * var result = Map.of("1", "<e id='1'/>", "2", "<e id='2'/>", "3", "<e id='3'/>");
     *}
     *
     * @param nl       a node list
     * @param attrName the attribute name
     * @return a name of the values of a given attribute to the nodes
     */
    public static Map<String, Element> index(NodeList nl, String attrName) {
        return index(nl, attribute(attrName)
                .andThen(a -> ofNullable(a)
                        .map(Attr::getValue)
                        .orElseThrow())
        );
    }
}
