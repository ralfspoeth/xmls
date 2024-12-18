package io.github.ralfspoeth.xmls;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import java.util.function.Function;

public class XmlFunctions {
    // prevent instantiation
    private XmlFunctions() {}


    static Function<Element, Attr> attribute(String name) {
        return e -> e.getAttributeNode(name);
    }
}
