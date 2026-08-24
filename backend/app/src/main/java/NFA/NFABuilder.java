package NFA;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import RegEx.Concatenate;
import RegEx.Literal;
import RegEx.RegEx;
import RegEx.Star;
import RegEx.Union;

public class NFABuilder {

    private ArrayList<State> states;
    private int i;

    public NFA construct(RegEx AST) {
        states = new ArrayList<>();
        i = 0; // accept state = 0, increases when a state is created.
        State accept = createState();
        State start = createState();

        add(start, accept, AST);

        return new NFA(states, start, accept);
    }

    private State createState() {
        State state = new State(i++);
        states.add(state);
        return state;
    }

    private void add(State start, State end, RegEx regEx) {
        switch(regEx) {
            case Literal c -> {
                start.addTransition(c.get(), end); // adds transition between nodes for that character
            }
            case Concatenate concat -> { // insert an intermediate state so that the word requires both characters to proceed
                State middle = createState();
                add(start, middle, concat.getLeft());
                add(middle, end, concat.getRight());
            }
            case Union union -> { // make parallel transitions between the nodes so either character is accepted
                add(start, end, union.getLeft());
                add(start, end, union.getRight());
            }
            case Star star -> { // add epsilon transition to and from the middle state, and at the middle state have a transition to itself for repetition
                State middle = createState();
                start.addEpsilonTransition(middle);
                middle.addEpsilonTransition(end);
                add(middle, middle, star.get());
            }
            default -> throw new RuntimeException("Unexpected node type in AST");
        }
    }

    @SuppressWarnings("unchecked")
    public JSONObject getNFA(NFA nfa) {

        JSONObject json = new JSONObject();
        JSONArray statesJSON = new JSONArray();
        JSONArray transitionsJSON = new JSONArray();

        for (State state : nfa.getStates()) {

            // add state
            JSONObject stateJSON = new JSONObject();
            stateJSON.put("id", state.getId());
            stateJSON.put("start", state == nfa.getStartState());
            stateJSON.put("accept", state == nfa.getAcceptState());
            statesJSON.add(stateJSON);

            // add transitions
            for (HashMap.Entry<Character, HashSet<State>> entry : state.getTransitions().entrySet()) {

                for (State transitionState : entry.getValue()) {

                    JSONObject transitionJSON = new JSONObject();
                    transitionJSON.put("start", state.getId());
                    transitionJSON.put("end", transitionState.getId());
                    transitionJSON.put("symbol", String.valueOf(entry.getKey()));
                    transitionsJSON.add(transitionJSON);

                }

            }

            for (State transitionState : state.getEpsilonTransitions()) {

                JSONObject transitionJSON = new JSONObject();
                transitionJSON.put("start", state.getId());
                transitionJSON.put("end", transitionState.getId());
                transitionJSON.put("symbol", "ε");
                transitionsJSON.add(transitionJSON);
                
            }
        }

        json.put("states", statesJSON);
        json.put("transitions", transitionsJSON);
        return json;
    }
}