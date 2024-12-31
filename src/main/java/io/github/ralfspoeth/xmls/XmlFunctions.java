package io.github.ralfspoeth.xmls;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.ralfspoeth.xmls.XmlStreams.stream;
import static java.util.Optional.ofNullable;

public class XmlFunctions {
    // prevent instantiation
    private XmlFunctions() {}


    /**
     * Create a function which returns the {@link Attr} attribute
     * named {@code name} when applied to an {@link Element}
     * @param name the unqualified name of the attribute
     * @return a function when applied to an element returns the attribute identified by the given unqualified name,
     * may be {@code null}
     */
    static Function<Element, Attr> attribute(String name) {
        return e -> e.getAttributeNode(name);
    }

    /**
     * Similar to {@link #attribute(String)} but using namespace-qualified
     * attribute names.
     *
     * @param ns the namespace URI
     * @param localName the local name
     * @return a function when applied to an element returns the attribute identified by the given qualified name,
     * may be {@code null}
     */
    static Function<Element, Attr> attribute(String ns, String localName) {
        return e -> e.getAttributeNodeNS(ns, localName);
    }

    static int intValue(Attr attribute, int def) {
        return ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .mapToInt(Integer::parseInt)
                .findFirst()
                .orElse(def);
    }

    static int intValue(Attr attribute) {
        return intValue(attribute, 0);
    }

    static long longValue(Attr attribute, long def) {
        return ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .mapToLong(Long::parseLong)
                .findFirst()
                .orElse(def);
    }

    static long longValue(Attr attribute) {
        return longValue(attribute, 0L);
    }

    static double doubleValue(Attr attribute, double def) {
        return ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .mapToDouble(Double::parseDouble)
                .findFirst()
                .orElse(def);
    }

    static double doubleValue(Attr attribute) {
        return doubleValue(attribute, 0d);
    }

    static BigDecimal decimalValue(Attr attribute, BigDecimal def) {
        return ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .map(BigDecimal::new)
                .findFirst()
                .orElse(def);
    }

    static BigDecimal decimalValue(Attr attribute) {
        return decimalValue(attribute, BigDecimal.ZERO);
    }

    static String stringValue(Attr attribute, String def) {
        return ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .findFirst()
                .orElse(def);
    }

    static String stringValue(Attr attribute) {
        return stringValue(attribute, "");
    }

    static LocalDateTime dateTimeValue(Attr attribute, LocalDateTime def) {
        return ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .map(LocalDateTime::parse)
                .findFirst()
                .orElse(def);
    }

    static boolean booleanValue(Attr attribute, boolean def) {
        return ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .map(Boolean::parseBoolean)
                .findFirst()
                .orElse(def);
    }

    static boolean booleanValue(Attr attribute) {
        return booleanValue(attribute, false);
    }

    static LocalDate dateValue(Attr attribute, LocalDate def) {
        return ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .map(LocalDate::parse)
                .findFirst()
                .orElse(def);
    }

    static <T> Map<T, Element> index(NodeList nl, Function<Element, T> indexBy) {
        return stream(nl)
                .filter(Element.class::isInstance)
                .map(Element.class::cast)
                .collect(Collectors.toMap(indexBy, Function.identity()));
    }

    static Map<String, Element> index(NodeList nl, String attrName) {
        return index(nl, attribute(attrName)
                .andThen(a -> ofNullable(a)
                        .map(Attr::getValue)
                        .orElseThrow())
        );
    }
}
