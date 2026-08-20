import RegEx.RegEx;
import RegEx.RegExParser;

public class Main {
    public static void main(String[] args) {
        RegExParser parser = new RegExParser("(a|b)*abb");
        RegEx tree = parser.parse();
        parser.printTree(tree);
    }
}