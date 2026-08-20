package RegEx;

public class Concatenate extends RegEx {
    private final RegEx left;
    private final RegEx right;

    public Concatenate(RegEx left, RegEx right) {
        this.left = left;
        this.right = right;
    }

    public RegEx getLeft() {
        return left;
    }

    public RegEx getRight() {
        return right;
    }
}