package io.github.ralfspoeth.xmls;

import org.jspecify.annotations.Nullable;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

public class XmlFunctions {
    // prevent instantiation
    private XmlFunctions() {}


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
     * Same as {@link #attribute(String)} but with a namespace URI
     *
     * @param ns        the namespace URI
     * @param localName the local name
     */
    public static Function<Element, Optional<Attr>> attribute(String ns, String localName) {
        return e -> ofNullable(e.getAttributeNodeNS(ns, localName));
    }

    /**
     * Obtain a function which returns the child elements of the given node as
     * a stream; to be used with {@link Stream#flatMap(Function)}.
     *
     * @param name the element (or tag) name
     */
    public static Function<Node, Stream<Element>> elements(String name) {
        return n -> XmlStreams.childNodes(n)
                .filter(Element.class::isInstance)
                .filter(e -> e.getNodeName().equals(name))
                .map(Element.class::cast);
    }

    /**
     * Same as {@link #elements(String)} but with a namespace URI.
     *
     * @param ns        the namespace URI
     * @param localName the local name
     */
    public static Function<Node, Stream<Element>> elements(String ns, String localName) {
        return n -> XmlStreams.childNodes(n)
                .filter(e -> e.getNodeName().equals(localName))
                .filter(e -> e.getNamespaceURI().equals(ns))
                .filter(Element.class::isInstance)
                .map(Element.class::cast);
    }

    /**
     * Obtain a function which returns the child nodes of the given node whose
     * node name equals the given {@code name}, regardless of node type.
     *
     * @param name the node name to match
     * @return a function that, when applied to a node, returns a stream of its
     * matching children
     */
    public static Function<Node, Stream<Node>> childrenNamed(String name) {
        return n -> XmlStreams.childNodes(n).filter(e -> e.getNodeName().equals(name));
    }

    /**
     * Parse into optional {@code int} value.
     *
     * @param attribute the attribute to read; may be {@code null}, in which case
     *                  an empty {@link OptionalInt} is returned
     * @return the parsed value, or an empty optional if {@code attribute} is {@code null}
     * @throws NumberFormatException if the attribute value is non-null but not a valid {@code int}
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
     *
     * @param attribute the attribute to read; may be {@code null}, in which case
     *                  an empty {@link OptionalLong} is returned
     * @return the parsed value, or an empty optional if {@code attribute} is {@code null}
     * @throws NumberFormatException if the attribute value is non-null but not a valid {@code long}
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
     *
     * @param attribute the attribute to read; may be {@code null}, in which case
     *                  an empty {@link OptionalDouble} is returned
     * @return the parsed value, or an empty optional if {@code attribute} is {@code null}
     * @throws NumberFormatException if the attribute value is non-null but not a valid {@code double}
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
     *
     * @param attribute the attribute to read; may be {@code null}
     * @return the parsed value, or an empty optional if {@code attribute} is {@code null}
     * @throws NumberFormatException if the attribute value is non-null but not a valid {@link BigDecimal}
     */
    public static Optional<BigDecimal> decimalValue(@Nullable Attr attribute) {
        return ofNullable(attribute).map(Attr::getValue).map(BigDecimal::new);
    }

    /**
     * Return as optional {@link String} value.
     *
     * @param attribute the attribute to read; may be {@code null}
     * @return the attribute value, or an empty optional if {@code attribute} is {@code null}
     */
    public static Optional<String> stringValue(@Nullable Attr attribute) {
        return ofNullable(attribute).map(Attr::getValue);
    }

    /**
     * Parse into optional {@link LocalDate} value using the
     * {@link java.time.format.DateTimeFormatter#ISO_LOCAL_DATE ISO_LOCAL_DATE} format
     * (e.g. {@code 2024-10-24}).
     *
     * @param attribute the attribute to read; may be {@code null}
     * @return the parsed date, or an empty optional if {@code attribute} is {@code null}
     * @throws java.time.format.DateTimeParseException if the attribute value is non-null but cannot be parsed
     */
    public static Optional<LocalDate> dateValue(@Nullable Attr attribute) {
        return ofNullable(attribute)
                .map(Attr::getValue)
                .map(LocalDate::parse);
    }

    /**
     * Parse into optional {@link LocalDateTime} value using the
     * {@link java.time.format.DateTimeFormatter#ISO_LOCAL_DATE_TIME ISO_LOCAL_DATE_TIME} format
     * (e.g. {@code 2024-10-24T12:34:56}). This method does not accept timezone offsets;
     * use {@link #offsetDateTimeValue(Attr)} or {@link #zonedDateTimeValue(Attr)} for those.
     *
     * @param attribute the attribute to read; may be {@code null}
     * @return the parsed date-time, or an empty optional if {@code attribute} is {@code null}
     * @throws java.time.format.DateTimeParseException if the attribute value is non-null but cannot be parsed
     */
    public static Optional<LocalDateTime> dateTimeValue(@Nullable Attr attribute) {
        return ofNullable(attribute)
                .map(Attr::getValue)
                .map(LocalDateTime::parse);
    }

    /**
     * Parse into optional {@link OffsetDateTime} value using the
     * {@link java.time.format.DateTimeFormatter#ISO_OFFSET_DATE_TIME ISO_OFFSET_DATE_TIME} format
     * (e.g. {@code 2024-10-24T12:34:56Z} or {@code 2024-10-24T12:34:56+02:00}).
     * Suitable for {@code xs:dateTime} values that carry a timezone offset.
     *
     * @param attribute the attribute to read; may be {@code null}
     * @return the parsed date-time, or an empty optional if {@code attribute} is {@code null}
     * @throws java.time.format.DateTimeParseException if the attribute value is non-null but cannot be parsed
     */
    public static Optional<OffsetDateTime> offsetDateTimeValue(@Nullable Attr attribute) {
        return ofNullable(attribute)
                .map(Attr::getValue)
                .map(OffsetDateTime::parse);
    }

    /**
     * Parse into optional {@link ZonedDateTime} value using the
     * {@link java.time.format.DateTimeFormatter#ISO_ZONED_DATE_TIME ISO_ZONED_DATE_TIME} format
     * (e.g. {@code 2024-10-24T12:34:56+02:00[Europe/Berlin]}).
     *
     * @param attribute the attribute to read; may be {@code null}
     * @return the parsed date-time, or an empty optional if {@code attribute} is {@code null}
     * @throws java.time.format.DateTimeParseException if the attribute value is non-null but cannot be parsed
     */
    public static Optional<ZonedDateTime> zonedDateTimeValue(@Nullable Attr attribute) {
        return ofNullable(attribute)
                .map(Attr::getValue)
                .map(ZonedDateTime::parse);
    }

    /**
     * Parse into optional {@link Boolean} value following the {@code xs:boolean}
     * lexical space: the literals {@code "true"} and {@code "1"} map to {@code true},
     * {@code "false"} and {@code "0"} map to {@code false}. Parsing is case-sensitive,
     * consistent with XML Schema.
     *
     * @param attribute the attribute to read; may be {@code null}
     * @return the parsed value, or an empty optional if {@code attribute} is {@code null}
     * @throws IllegalArgumentException if the attribute value is non-null but is not one of
     *                                  {@code "true"}, {@code "false"}, {@code "1"}, or {@code "0"}
     */
    public static Optional<Boolean> booleanValue(@Nullable Attr attribute) {
        return ofNullable(attribute)
                .map(Attr::getValue)
                .map(v -> switch (v) {
                    case "true", "1" -> Boolean.TRUE;
                    case "false", "0" -> Boolean.FALSE;
                    default -> throw new IllegalArgumentException("Not a valid xs:boolean literal: " + v);
                });
    }
}
