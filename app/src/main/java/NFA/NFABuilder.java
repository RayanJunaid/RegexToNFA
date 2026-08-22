package NFA;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import RegEx.Concatenate;
import RegEx.Literal;
import RegEx.RegEx;
import RegEx.Union;
import RegEx.Star;

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

    public void printNFA(NFA nfa) {
        for (State state : nfa.getStates()) {
            HashMap<Character, HashSet<State>> transitions = state.getTransitions();
            for (char entry : transitions.keySet()) {

                for (State target : transitions.get(entry)) {
                    System.out.println(
                        state.getId() + " --" + entry + "--> " + target.getId());
                }
            }

            for (State target : state.getEpsilonTransitions()) {
                System.out.println(state.getId() + " --\u03B5--> " + target.getId());
            }
        }
    }
}