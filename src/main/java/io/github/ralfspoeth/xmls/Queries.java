package io.github.ralfspoeth.xmls;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import java.util.Optional;
import java.util.function.Function;

public class Queries {
    private Queries() {
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
}
