import RegEx.RegEx;
import RegEx.RegExParser;
import NFA.NFABuilder;
import NFA.NFA;
import NFA.NFASim;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        RegExParser parser = new RegExParser("(a|b)*abb");
        RegEx tree = parser.parse();
        parser.printTree(tree);

        NFABuilder builder = new NFABuilder();
        NFA nfa = builder.construct(tree);
        builder.printNFA(nfa);

        NFASim sim = new NFASim();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            
            System.out.println("Enter a string to test, or exit to leave: ");

            String input = scanner.nextLine();
            if (input.equals("exit")) {
                break;
            } else {
                if (sim.simulate(nfa, input)) {
                    System.out.println("True");
                } else {
                    System.out.println("False");
                }
            }

        }
        scanner.close();
    }
} 