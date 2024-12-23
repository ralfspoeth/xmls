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

    static Function<Element, Attr> attribute(String name) {
        return e -> e.getAttributeNode(name);
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

    static Map<String, Element> index(NodeList nl, Function<Element, String> indexBy) {
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
