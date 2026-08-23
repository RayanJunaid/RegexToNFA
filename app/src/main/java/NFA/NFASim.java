package NFA;
import java.util.HashMap;
import java.util.HashSet;

public class NFASim {

    public boolean simulate(NFA nfa, String input) {
        HashSet<State> currentStates = new HashSet<>();
        currentStates.add(nfa.getStartState());
        currentStates = epsilonClosure(currentStates);

        for (int i = 0; i < input.length(); i++) { // going through the string
            HashSet<State> newStates = new HashSet<>();
            for (State state : currentStates) {
                HashMap<Character,HashSet<State>> transitions = state.getTransitions();

                if (transitions.containsKey(input.charAt(i))) {
                    newStates.addAll(transitions.get(input.charAt(i)));
                }
            }

            currentStates = epsilonClosure(newStates);

            if (currentStates.size() == 0) {
                return false;
            }
        }

        return currentStates.contains(nfa.getAcceptState());
    }

    private HashSet<State> epsilonClosure(HashSet<State> states) {
        HashSet<State> closure = new HashSet<>(states);
        HashSet<State> currStates;

        do {
            currStates = new HashSet<>(closure);

            for (State state : currStates) {

                for (State transition : state.getEpsilonTransitions()) { // we go through the epsilon transitions and if a new state is reached we add it

                    if (!closure.contains(transition)) {
                        closure.add(transition);
                    }
                }
            }
        } while (closure.size() > currStates.size());

        return closure;
    }

}
