package NFA;
import java.util.HashMap;
import java.util.HashSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class NFASim {

    @SuppressWarnings("unchecked")
    public JSONObject simulate(NFA nfa, String input) {

        JSONObject json = new JSONObject(); // steps + whether the input was accepted
        JSONArray stepsJSON = new JSONArray(); // for storing the steps of the simulation
        HashSet<State> currentStates = new HashSet<>();

        // add step for the initial epsilon transition
        currentStates.add(nfa.getStartState());
        currentStates = epsilonClosure(currentStates);
        JSONObject stepJSON = new JSONObject();
        stepJSON.put("symbol", "ε");
        JSONArray activeStates = new JSONArray();
        activeStates.addAll(stateIds(currentStates));
        stepJSON.put("activeStates", activeStates);
        stepsJSON.add(stepJSON);

        // going through the input string
        for (int i = 0; i < input.length(); i++) { 

            HashSet<State> newStates = new HashSet<>();
            stepJSON = new JSONObject();

            
            // going through each parallel state and storing the transition states
            for (State state : currentStates) { 

                HashMap<Character,HashSet<State>> transitions = state.getTransitions();

                // adding the transitions for the given input char
                if (transitions.containsKey(input.charAt(i))) {
                    newStates.addAll(transitions.get(input.charAt(i)));
                }

            }

            // add a step after the input char is processed by the states in currentStates
            stepJSON.put("symbol", String.valueOf(input.charAt(i)));
            activeStates = new JSONArray();
            activeStates.addAll(stateIds(newStates));
            stepJSON.put("activeStates", activeStates);
            stepsJSON.add(stepJSON);

            // add another step for after epsilon closure
            currentStates = epsilonClosure(newStates);
            stepJSON = new JSONObject();
            stepJSON.put("symbol", "ε");
            activeStates = new JSONArray();
            activeStates.addAll(stateIds(currentStates));
            stepJSON.put("activeStates", activeStates);
            stepsJSON.add(stepJSON);

        }

        json.put("accepted", currentStates.contains(nfa.getAcceptState()));
        json.put("steps", stepsJSON);

        return json;

    }

    private HashSet<State> epsilonClosure(HashSet<State> states) {

        HashSet<State> closure = new HashSet<>(states);
        HashSet<State> currStates;

        // we go through the epsilon transitions and if a new state is reached we add it, we keep going till no new states are reached
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

    private HashSet<Integer> stateIds(HashSet<State> states) {

        HashSet<Integer> stateSet = new HashSet<>();
        for (State state : states) {
            stateSet.add(state.getId());
        }

        return stateSet;
    }
}
