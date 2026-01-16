package io.github.ralfspoeth.xmls;

import org.jspecify.annotations.Nullable;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

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
    public static Function<Element, Optional<Attr>> attribute(String name) {
        return e -> ofNullable(e.getAttributeNode(name));
    }

    /**
     * Obtain a function which returns the child elements of the given node as
     * a stream; to be used with {@link Stream#flatMap(Function)}.
     *
     * @param name the element (or tag) name
     * @return a function
     */
    public static Function<Node, Stream<Element>> elements(String name) {
        return n -> XmlStreams.children(n)
                .filter(e -> e.getNodeName().equals(name))
                .filter(Element.class::isInstance)
                .map(Element.class::cast);
    }

    public static Function<Node, Stream<Node>> children(String name) {
        return n -> XmlStreams.children(n).filter(e -> e.getNodeName().equals(name));
    }

    /**
     * Parse into optional {@code int} value.
     */
    public static OptionalInt intValue(@Nullable Attr attribute) {
        return ofNullable(attribute)
                .map(Attr::getValue)
                .stream()
                .mapToInt(Integer::parseInt)
                .findAny();
    }

    /**
     * Parse into optional {@code long} value.
     */
    public static OptionalLong longValue(@Nullable Attr attribute) {
        return ofNullable(attribute)
                .map(Attr::getValue)
                .stream()
                .mapToLong(Long::parseLong)
                .findAny();
    }

    /**
     * Parse into optional {@code double} value.
     */
    public static OptionalDouble doubleValue(@Nullable Attr attribute) {
        return ofNullable(attribute)
                .map(Attr::getValue)
                .stream()
                .mapToDouble(Double::parseDouble)
                .findAny();
    }

    /**
     * Parse into optional {@link BigDecimal} value.
     */
    public static Optional<BigDecimal> decimalValue(@Nullable Attr attribute) {
        return ofNullable(attribute).map(Attr::getValue).map(BigDecimal::new);
    }

    /**
     * Return as optional {@link String} value.
     */
    public static Optional<String> stringValue(@Nullable Attr attribute) {
        return ofNullable(attribute).map(Attr::getValue);
    }

    /**
     * Parse into optional {@link LocalDate} value.
     */
    public static Optional<LocalDate> dateValue(@Nullable Attr attribute) {
        return ofNullable(attribute)
                .map(Attr::getValue)
                .map(LocalDate::parse);
    }

    /**
     * Parse into optional {@link LocalDateTime} value.
     */
    public static Optional<LocalDateTime> dateTimeValue(@Nullable Attr attribute) {
        return ofNullable(attribute)
                .map(Attr::getValue)
                .map(LocalDateTime::parse);
    }

    /**
     * Parse {@code "true} and {@code "false} into optional {@link Boolean} value.
     */
    public static Optional<Boolean> booleanValue(@Nullable Attr attribute) {
        return ofNullable(attribute)
                .map(Attr::getValue)
                .map(Boolean::parseBoolean);
    }
}
