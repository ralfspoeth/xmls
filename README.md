# XML Stream Support

This project provides support for iterating
and traversing XML documents with the Java Stream API.

## Rationale

The `java.xml` module shipped with the JDK contains
interfaces and classes dating back to the year 2004
when release 1.5 was published.
Especially the packages originating from the W3C
(`org.w3c.dom` in particular) translate the DOM verbatim
into interfaces and classes. The result is verbose,
iterator-unfriendly code: `NodeList` is not `Iterable`,
`NamedNodeMap` is not `Map`, and navigating a tree means
writing index-based `for` loops or recursive helpers by hand.

This module bridges those W3C types into `java.util.stream`
so that traversal, filtering, and value extraction can be
expressed as ordinary stream pipelines.

> **Note.** Use this module with unmodifiable XML trees only.
> The streams take a snapshot of `NodeList.getLength()` /
> `NamedNodeMap.getLength()` at construction time and do not
> detect concurrent modification of the underlying document.

## Requirements

- Java 17 or later
- The `java.xml` module (shipped with the JDK)

## Installation

Maven:

```xml
<dependency>
    <groupId>io.github.ralfspoeth</groupId>
    <artifactId>xmls</artifactId>
    <version>0.0.2</version>
</dependency>
```

And in your `module-info.java`:

```java
requires io.github.ralfspoeth.xmls;
```

## API at a glance

The library exposes two utility classes in the package
`io.github.ralfspoeth.xmls`.

### `XmlStreams` — turn DOM collections into streams

| Method | Returns |
| --- | --- |
| `attributes(Node)` | `Stream<Attr>` of the node's attributes (empty if the node has none) |
| `childNodes(Node)` | `Stream<Node>` of the node's direct children |
| `allElements(Document)` | `Stream<Element>` of every element in the document, in document order |

### `XmlFunctions` — composable navigators and typed value parsers

Higher-order helpers that return `Function`s so they compose
under `flatMap`:

- `elements(String name)` — child elements with the given local name
- `elements(String ns, String localName)` — same, namespace-aware
- `attribute(String name)` / `attribute(String ns, String localName)` — a single attribute as `Optional<Attr>`
- `childrenNamed(String name)` — all child nodes (any type) matching the name

Typed parsers for attribute values:

- `intValue`, `longValue`, `doubleValue`, `decimalValue`
- `stringValue`
- `dateValue` (ISO local date), `dateTimeValue` (ISO local date-time),
  `offsetDateTimeValue` (with offset, e.g. `…+02:00`),
  `zonedDateTimeValue` (with zone)
- `booleanValue` — follows the `xs:boolean` lexical space
  (`true`/`1` → true, `false`/`0` → false)

All parsers accept a `@Nullable Attr`. A `null` attribute returns
the empty optional; a non-null attribute with a malformed value
throws the corresponding `NumberFormatException`,
`DateTimeParseException`, or `IllegalArgumentException`.

## Usage

Given this document:

```xml
<root>
    <a id="1"/>
    <a id="2"/>
    <a id="3"/>
    <b>
        <c>
            <d>
                <e>1234</e>
            </d>
        </c>
    </b>
</root>
```

Count the `<a>` children of `<root>`:

```java
import static io.github.ralfspoeth.xmls.XmlFunctions.elements;

long n = Stream.of(root)
    .flatMap(elements("a"))
    .count();   // 3
```

Drill down a path and read a numeric value:

```java
int value = Stream.of(root)
    .flatMap(elements("b"))
    .flatMap(elements("c"))
    .flatMap(elements("d"))
    .flatMap(elements("e"))
    .findFirst()
    .map(Element::getTextContent)
    .map(String::trim)
    .map(Integer::parseInt)
    .orElseThrow();   // 1234
```

Read a typed attribute, with a default:

```java
import static io.github.ralfspoeth.xmls.XmlFunctions.*;

int id = intValue(element.getAttributeNode("id")).orElse(-1);
LocalDate when = dateValue(element.getAttributeNode("date"))
                    .orElse(LocalDate.now());
```

Iterate every element in a document:

```java
import static io.github.ralfspoeth.xmls.XmlStreams.allElements;

allElements(doc)
    .filter(e -> "item".equals(e.getTagName()))
    .forEach(System.out::println);
```

## License

Released under the MIT License — see [LICENSE](LICENSE).
