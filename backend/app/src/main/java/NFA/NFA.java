package NFA;
import java.util.ArrayList;

public class NFA {
    private ArrayList<State> states = new ArrayList<>();
    private final State start;
    private final State accept;

    public NFA(ArrayList<State> states, State start, State accept) {
        this.states = states;
        this.start = start;
        this.accept = accept;
    }

    public ArrayList<State> getStates() {
        return states;
    }

    public State getStartState() {
        return start;
    }

    public State getAcceptState() {
        return accept;
    }

}
