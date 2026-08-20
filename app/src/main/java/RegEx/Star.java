package RegEx;

public class Star extends RegEx {
    private final RegEx self;

    public Star(RegEx self) {
        this.self = self;
    }

    public RegEx get() {
        return self;
    }
}
