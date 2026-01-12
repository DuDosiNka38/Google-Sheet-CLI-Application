package issuetracksystem.terminal_interface;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class Terminal implements CommandLineRunner {

    private final IssuesController controller;

    public Terminal(IssuesController controller) {
        this.controller = controller;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Issue Track System CLI. Type 'help'.");


        if (args != null && args.length > 0) {
            String commanfLine = String.join(" ", args);
            String out = controller.handleCommand(commanfLine);
            if (out != null && !out.isBlank() && !"__EXIT__".equals(out)) {
                System.out.println(out);
            }
            return;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("> ");
            String line = br.readLine();
            if (line == null) {
                System.out.println("\nSTDIN closed. Exiting.");
                break;
            }
            String out = controller.handleCommand(line);
            if (out == null || out.isBlank()) continue;
            if ("__EXIT__".equals(out)) {
                System.out.println("Bye.");
                break;
            }
            System.out.println(out);
        }
    }

}
