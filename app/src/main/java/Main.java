import RegEx.RegEx;
import RegEx.RegExParser;
import NFA.NFABuilder;
import NFA.NFA;

public class Main {
    public static void main(String[] args) {
        RegExParser parser = new RegExParser("(a|b)*abb");
        RegEx tree = parser.parse();
        parser.printTree(tree);

        NFABuilder builder = new NFABuilder();
        NFA nfa = builder.construct(tree);
        builder.printNFA(nfa);
    }
} 