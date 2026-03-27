# XML Stream Support

This project provides support for iterating
and traversing XML documents in the Java streaming API.

## Rationale

The `java.xml` module shipped with the JDK contains
interfaces and classes dating back to the year 2004
when release 1.5 was published.
Especially the packages endorsed from the W3C (`org.w3c.com`
in particular) translate the DOM verbatim
into interfaces and classes.