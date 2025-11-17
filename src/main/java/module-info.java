import org.jspecify.annotations.NullMarked;

@NullMarked
module io.github.ralfspoeth.xmls {
    requires java.xml;
    requires static org.jspecify;
    exports io.github.ralfspoeth.xmls;
}