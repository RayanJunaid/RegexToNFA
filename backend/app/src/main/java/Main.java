import RegEx.RegEx;
import RegEx.RegExParser;
import NFA.NFABuilder;
import NFA.NFA;
import NFA.NFASim;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Main {
    public static void main(String[] args) {

        // open input stream, wrapped in buffer reader for efficiency
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            
            JSONParser parser = new JSONParser();
            String line;

            while ((line = in.readLine()) != null) {

                if (!line.isBlank()) {
                    
                    try {
                        
                        JSONObject request = (JSONObject) parser.parse(line);

                        String cmd = (String) request.get("command");

                        // if command matches nothing then throw an exception, otherwise we check if the command is to build or simulate the nfa
                        if (cmd == null || cmd.isBlank()) {
                            
                            err("Missing command");
                            continue;

                        }

                        switch (cmd) {

                            case "build" -> {
                                System.out.println(buildNFA(request));
                                System.out.flush();
                            }

                            case "simulate" -> {
                                System.out.println(simulateNFA(request));
                                System.out.flush();
                            }

                            default -> {
                                err("Unknown command: " + cmd);
                            }
                        }

                    } catch (Exception e) {
                        err(e.getMessage());
                    }

                }

            }


        } catch (Exception e) {
            System.err.println(e);
        }
        
    } 

    public static JSONObject buildNFA(JSONObject request) {

        NFABuilder builder = new NFABuilder();
        RegExParser parser;
        String regex = (String) request.get("regex");
        
        if (regex == null) {
            throw new IllegalArgumentException("Missing regex expression");
        }

        // build nfa and return json representation
        parser = new RegExParser(regex);
        RegEx ast = parser.parse();
        NFA nfa = builder.construct(ast);
        return builder.getNFA(nfa);
    }

    public static JSONObject simulateNFA(JSONObject request) {

        NFABuilder builder = new NFABuilder();
        NFASim simulator = new NFASim();
        RegExParser parser;
        String regex = (String) request.get("regex");
        String input = (String) request.get("input");
        
        if (regex == null) {
            throw new IllegalArgumentException("Missing regex expression");
        }

        if (input == null) {
            throw new IllegalArgumentException("Missing input");
        }

        // build nfa and simulate it, returning json representation of the simulation
        parser = new RegExParser(regex);
        RegEx ast = parser.parse();
        NFA nfa = builder.construct(ast);
        return simulator.simulate(nfa, input);
    }

    @SuppressWarnings("unchecked")
    public static void err(String msg) {

        JSONObject error = new JSONObject();
        error.put("error", msg);
        System.out.println(error.toJSONString());
        System.out.flush();

    }
} 