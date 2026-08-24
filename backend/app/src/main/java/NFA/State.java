package NFA;
import java.util.HashMap;
import java.util.HashSet;

public class State {
    private final int id;
    private final HashMap<Character, HashSet<State>> transitions = new HashMap<>();
    private final HashSet<State> epsilonTransitions = new HashSet<>();

    public State(int id) {
        this.id = id;
    }

    public void addTransition(char c, State state) {
        if (transitions.get(c) == null) {
            transitions.put(c, new HashSet<>());
        }
        transitions.get(c).add(state);
    }

    public void addEpsilonTransition(State state) {
        epsilonTransitions.add(state);
    }

    public int getId() {
        return id;
    }

    public HashMap<Character, HashSet<State>> getTransitions() {
        return transitions;
    }

    public HashSet<State> getEpsilonTransitions() {
        return epsilonTransitions;
    }

}