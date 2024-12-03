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

import static io.github.ralfspoeth.xmls.XmlFunctions.attribute;
import static java.util.Optional.ofNullable;

public class XmlQueries {
    private XmlQueries() {
    }

    static int intValue(Attr attribute, int def) {
        return ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .mapToInt(Integer::parseInt)
                .findFirst()
                .orElse(def);
    }

    static long longValue(Attr attribute, long def) {
        return ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .mapToLong(Long::parseLong)
                .findFirst()
                .orElse(def);
    }

    static double doubleValue(Attr attribute, double def) {
        return ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .mapToDouble(Double::parseDouble)
                .findFirst()
                .orElse(def);
    }

    static BigDecimal decimalValue(Attr attribute, BigDecimal def) {
        return ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .map(BigDecimal::new)
                .findFirst()
                .orElse(def);
    }

    static String stringValue(Attr attribute, String def) {
        return ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .findFirst()
                .orElse(def);
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

    static LocalDate dateValue(Attr attribute, LocalDate def) {
        return ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .map(LocalDate::parse)
                .findFirst()
                .orElse(def);
    }

    static Map<String, Element> index(NodeList nl, Function<Element, String> indexBy) {
        return XmlStreams.stream(nl)
                .filter(Element.class::isInstance)
                .map(Element.class::cast)
                .collect(Collectors.toMap(indexBy, Function.identity()));
    }

    static Map<String, Element> index(NodeList nl, String attrName) {
        return index(nl, attribute(attrName)
                        .andThen(an -> ofNullable(an)
                                .map(Attr::getValue)
                                .orElseThrow())
        );
    }
}
