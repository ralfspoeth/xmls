# XML Stream Support

A small Java library that exposes the W3C DOM through the `java.util.stream`
API and provides composable helpers for navigating, filtering, and extracting
typed values from XML documents.

## Rationale

The `java.xml` module shipped with the JDK contains interfaces and classes
dating back to 2004. The packages originating from the W3C
(`org.w3c.dom` in particular) translate the DOM verbatim into interfaces:
`NodeList` is not `Iterable`, `NamedNodeMap` is not `Map`, and navigating a
tree means writing index-based `for` loops or recursive helpers by hand.

This library bridges those W3C types into `java.util.stream` so that
traversal, filtering, and value extraction can be expressed as ordinary
stream pipelines.

> **Note.** Use this library with unmodifiable XML trees only. The streams
> take a snapshot of `NodeList.getLength()` / `NamedNodeMap.getLength()` at
> construction time and do not detect concurrent modification of the
> underlying document.

## Requirements

- Java 17 or later
- The `java.xml` module (shipped with the JDK)

## Installation

Maven:

```xml
<dependency>
    <groupId>io.github.ralfspoeth</groupId>
    <artifactId>xmls</artifactId>
    <version>0.9</version>
</dependency>
```

And in your `module-info.java`:

```java
requires io.github.ralfspoeth.xmls;
```

## API at a glance

Everything lives in the package `io.github.ralfspoeth.xmls`:

- **`Xml`** — convenience parsers that turn strings, streams, readers,
  or `Path`s into a `Document` without the JAXP boilerplate.
- **`XmlStreams`** — turn DOM collections into streams; descendant
  traversal.
- **`XmlFunctions`** — composable navigators returning `Function`s,
  plus typed parsers for attribute values and element text content.
- **`XmlException`** — unchecked exception thrown by `Xml` on parse or
  I/O failure; the underlying `SAXException` / `IOException` is preserved
  as the cause.

### `Xml` — parsing entry points

```java
Document doc = Xml.parse(string);          // from a String
Document doc = Xml.parse(inputStream);     // from an InputStream
Document doc = Xml.parse(reader);          // from a Reader
Document doc = Xml.parse(path);            // from a file Path
```

For namespace-aware parsing (required to use the `(ns, localName)`
overloads below), use the `parseNs` variants:

```java
Document doc = Xml.parseNs(string);
// …and parseNs(InputStream), parseNs(Reader), parseNs(Path)
```

Any failure is wrapped in `XmlException` (unchecked).

### `XmlStreams` — DOM collections as streams

| Method | Returns |
| --- | --- |
| `attributes(Node)` | `Stream<Attr>` of the node's attributes (empty if it has none) |
| `childNodes(Node)` | `Stream<Node>` of the node's direct children |
| `allElements(Document)` | `Stream<Element>` of every element in the document, in document order |
| `descendantElements(Element)` | `Stream<Element>` of all descendants of an element (self excluded) |
| `descendantElements(Element, String name)` | descendants whose tag name matches; `"*"` matches all |
| `descendantElements(Element, String ns, String localName)` | descendants by namespace URI and local name; either may be `"*"` |

### `XmlFunctions` — navigators and typed parsers

Higher-order helpers returning `Function`s that compose under `flatMap`:

- `elements(name)` / `elements(ns, localName)` — child elements with a given
  (qualified or namespaced) name
- `attribute(name)` / `attribute(ns, localName)` — a single attribute as
  `Function<Element, Optional<Attr>>`
- `attributeValue(name)` / `attributeValue(ns, localName)` — shortcut
  returning the attribute's value directly as
  `Function<Element, Optional<String>>`
- `childrenNamed(name)` — all child nodes (any node type) matching the name

Typed parsers for **attribute values** (`@Nullable Attr` → typed `Optional`):

- `intValue`, `longValue`, `doubleValue`, `decimalValue`, `stringValue`
- `dateValue`, `dateTimeValue`, `offsetDateTimeValue`, `zonedDateTimeValue`
- `booleanValue` — follows the `xs:boolean` lexical space
  (`true`/`1` → true, `false`/`0` → false)

Symmetric parsers for **element text content** (`@Nullable Element` →
typed `Optional`; the text content is trimmed before parsing):

- `text` — the trimmed text content as `Optional<String>`
- `intContent`, `longContent`, `doubleContent`, `decimalContent`,
  `stringContent`
- `dateContent`, `dateTimeContent`, `offsetDateTimeContent`,
  `zonedDateTimeContent`
- `booleanContent`

A `null` input yields the empty optional. A non-null input whose value
cannot be parsed throws the corresponding `NumberFormatException`,
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

Parse and count the `<a>` children of `<root>`:

```java
import static io.github.ralfspoeth.xmls.XmlFunctions.elements;

Document doc = Xml.parse(xmlString);
Element root = doc.getDocumentElement();

long n = Stream.of(root)
    .flatMap(elements("a"))
    .count();   // 3
```

Drill down a path and read a typed value from text content:

```java
import static io.github.ralfspoeth.xmls.XmlFunctions.*;

Element e = Stream.of(root)
    .flatMap(elements("b"))
    .flatMap(elements("c"))
    .flatMap(elements("d"))
    .flatMap(elements("e"))
    .findFirst()
    .orElseThrow();

int value = intContent(e).orElseThrow();   // 1234
```

Read a typed attribute with a default:

```java
int id = intValue(element.getAttributeNode("id")).orElse(-1);
LocalDate when = dateValue(element.getAttributeNode("date"))
                    .orElse(LocalDate.now());
```

The `attributeValue` shortcut composes nicely in a stream pipeline:

```java
List<String> ids = Stream.of(root)
    .flatMap(elements("a"))
    .flatMap(e -> attributeValue("id").apply(e).stream())
    .toList();   // ["1", "2", "3"]
```

Walk every descendant of an element:

```java
import static io.github.ralfspoeth.xmls.XmlStreams.descendantElements;

descendantElements(root, "item")
    .forEach(System.out::println);
```

Namespace-aware lookup:

```java
Document doc = Xml.parseNs(xmlString);
Element root = doc.getDocumentElement();

Stream.of(root)
    .flatMap(elements("http://example.com/ns", "item"))
    .forEach(System.out::println);
```

## License

Released under the MIT License — see [LICENSE](LICENSE).
