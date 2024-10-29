package io.github.ralfspoeth.xmls;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;

public class XmlQueries {
    private XmlQueries() {
    }

    static Function<Element, Attr> attribute(String name) {
        return e -> e.getAttributeNode(name);
    }

    static int intValue(Attr attribute, int def) {
        return Optional.ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .mapToInt(Integer::parseInt)
                .findFirst()
                .orElse(def);
    }

    static long longValue(Attr attribute, long def) {
        return Optional.ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .mapToLong(Long::parseLong)
                .findFirst()
                .orElse(def);
    }

    static double doubleValue(Attr attribute, double def) {
        return Optional.ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .mapToDouble(Double::parseDouble)
                .findFirst()
                .orElse(def);
    }

    static BigDecimal decimalValue(Attr attribute, BigDecimal def) {
        return Optional.ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .map(BigDecimal::new)
                .findFirst()
                .orElse(def);
    }

    static String stringValue(Attr attribute, String def) {
        return Optional.ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .findFirst()
                .orElse(def);
    }

    static LocalDateTime dateTimeValue(Attr attribute, LocalDateTime def) {
        return Optional.ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .map(LocalDateTime::parse)
                .findFirst()
                .orElse(def);
    }

    static boolean booleanValue(Attr attribute, boolean def) {
        return Optional.ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .map(Boolean::parseBoolean)
                .findFirst()
                .orElse(def);
    }

    static LocalDate dateValue(Attr attribute, LocalDate def) {
        return Optional.ofNullable(attribute)
                .stream()
                .map(Attr::getValue)
                .map(LocalDate::parse)
                .findFirst()
                .orElse(def);
    }
}
