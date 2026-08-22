package RegEx;

public class Literal extends RegEx {
    private final char c;
    
    public Literal(char c) {
        this.c = c;
    }

    public char get() {
        return c;
    }
}
