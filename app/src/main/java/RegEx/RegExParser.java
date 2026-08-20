package RegEx;

public class RegExParser {
    private final String input;
    private int i;
    
    public RegExParser(String input) {
        this.input = input;
        this.i = 0;
    }

    public RegEx parse() {
        return regex();
    }

    public void printTree(RegEx node) {
        printTree(node, "");
    }

    private void printTree(RegEx node, String indent) {
        switch (node) {
            case Literal x -> System.out.println(indent + x.get());
            case Concatenate x -> {
                System.out.println(indent + "Concatenate");
                printTree(x.getLeft(), indent + "    ");
                printTree(x.getRight(), indent + "    ");
            }
            case Union x -> {
                System.out.println(indent + "Union");
                printTree(x.getLeft(), indent + "    ");
                printTree(x.getRight(), indent + "    ");
            }
            case Star x -> {
                System.out.println(indent + "Star");
                printTree(x.get(), indent + "    ");
            }
            default -> System.out.println(indent + "Empty");
        }
    }


    // Recursive descent methods

    private char peek() {
        if (more()) {
            return input.charAt(i);
        } else {
            throw new RuntimeException("Unexpected end of input");
        }
    }

    private void consume(char c) {
        if (peek() == c) {
            i++;
        } else {
            throw new RuntimeException(String.format("Expected %c but got %c", c, peek()));
        }
    }

    private char next() {
        char c = peek();
        consume(c);
        return c;
    }

    private boolean more() {
        return (i < input.length());
    }


    // RegEx expression types

    private RegEx regex() { // If there is a '|' then we split the left and right expressions into two branches, otherwise return the expression as a term
        RegEx term = term();
        if (more() && peek() == '|') {
            consume('|');
            RegEx regex = regex();
            return new Union(term, regex);
        } else {
            return term;
        }
    }

    private RegEx term() { // Indicates concatenation and splits the expression into branches for each factor
        RegEx factor = factor();
        while (more() && peek() != ')' && peek() != '|') {
            RegEx nextFactor = factor();
            factor = new Concatenate(factor, nextFactor);
        }
        return factor;
    }

    private RegEx factor() { // Handles repetition by recursively storing itself, accepts multiple stars
        RegEx base = base();
        while (more() && peek() == '*') {
            consume('*');
            base = new Star(base);
        }
        return base;
    }

    private RegEx base() {
        switch (peek()) {
            case '(' -> {
                consume('(');
                RegEx x = regex(); // produces AST for the inner expression until reaching ')' where it reaches the default case of base(), then ) removed.
                consume(')');
                return x;
            }
            case '\\' -> {
                consume('\\');
                return new Literal(next());
            }
            default -> {
                return new Literal(next());
            }
        }
    }
}
