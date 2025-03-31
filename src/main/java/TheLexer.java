import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;


public class TheLexer {
    private File file;
    private Automata dfa;
    private Vector<TheToken> tokens;
    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
        "int", "end", "if", "else", "while", "do", "for", "break", "continue",
        "class", "float", "true", "false", "string", "char", "void", "boolean", "return", "switch", "case", "default"
    ));

    public TheLexer(File file) {
        this.file = file;
        tokens = new Vector<>();
        dfa = new Automata();
        
        // Binarios
        dfa.addTransition("s0", "0", "s1");
        dfa.addTransition("s1", "b", "s2");
        dfa.addTransition("s1", "B", "s2");
        dfa.addTransition("s2", "0", "s3");
        dfa.addTransition("s2", "1", "s3");
        dfa.addTransition("s3", "0", "s3");
        dfa.addTransition("s3", "1", "s3");

        // Números que empiezan con 1-9
        for (char c = '1'; c <= '9'; c++) {
            dfa.addTransition("s0", String.valueOf(c), "s4");
            dfa.addTransition("s4", String.valueOf(c), "s4");
        }
        dfa.addTransition("s4", "0", "s4");

        // Float y notación científica
        dfa.addTransition("s4", ".", "s5");
        for (char c = '0'; c <= '9'; c++) {
            dfa.addTransition("s5", String.valueOf(c), "s5");
        }
        dfa.addTransition("s5", "e", "s11");
        dfa.addTransition("s5", "E", "s11");
        for (char c = '0'; c <= '9'; c++) {
            dfa.addTransition("s11", String.valueOf(c), "s11");
        }

        // Octales
        dfa.addTransition("s1", "0", "s8");
        dfa.addTransition("s1", "1", "s8");
        dfa.addTransition("s1", "2", "s8");
        dfa.addTransition("s1", "3", "s8");
        dfa.addTransition("s1", "4", "s8");
        dfa.addTransition("s1", "5", "s8");
        dfa.addTransition("s1", "6", "s8");
        dfa.addTransition("s1", "7", "s8");
        for (char c = '0'; c <= '7'; c++) {
            dfa.addTransition("s8", String.valueOf(c), "s8");
        }

        // Hexadecimales
        dfa.addTransition("s1", "x", "s9");
        dfa.addTransition("s1", "X", "s9");
        for (char c = '0'; c <= '9'; c++) {
            dfa.addTransition("s9", String.valueOf(c), "s10");
            dfa.addTransition("s10", String.valueOf(c), "s10");
        }
        for (char c = 'a'; c <= 'f'; c++) {
            dfa.addTransition("s9", String.valueOf(c), "s10");
            dfa.addTransition("s10", String.valueOf(c), "s10");
        }
        for (char c = 'A'; c <= 'F'; c++) {
            dfa.addTransition("s9", String.valueOf(c), "s10");
            dfa.addTransition("s10", String.valueOf(c), "s10");
        }

        // Identificadores
        for (char c = 'a'; c <= 'z'; c++) {
            dfa.addTransition("s0", String.valueOf(c), "s6");
            dfa.addTransition("s6", String.valueOf(c), "s6");
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            dfa.addTransition("s0", String.valueOf(c), "s6");
            dfa.addTransition("s6", String.valueOf(c), "s6");
        }
        for (char c = '0'; c <= '9'; c++) {
            dfa.addTransition("s6", String.valueOf(c), "s6");
        }
        dfa.addTransition("s0", "$", "s6");
        dfa.addTransition("s6", "$", "s6");
        dfa.addTransition("s6", "_", "s6");

        // Estados de aceptación
        dfa.addAcceptState("s3", "BINARY");
        dfa.addAcceptState("s1", "INTEGER");
        dfa.addAcceptState("s4", "INTEGER");
        dfa.addAcceptState("s5", "FLOAT");
        dfa.addAcceptState("s6", "IDENTIFIER");
        dfa.addAcceptState("s8", "OCTAL");
        dfa.addAcceptState("s10", "HEX");
        dfa.addAcceptState("s11", "FLOAT");
    }

    public void run() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                processLine(line, lineNumber);
                lineNumber++;
            }
        }
    }

    private void processLine(String line, int lineNumber) {
        int i = 0;
        StringBuilder currentToken = new StringBuilder();
        String currentState = "s0";
        boolean inComment = false;

        while (i < line.length()) {
            char c = line.charAt(i);
            
            // Check for the beginning of a comment
            if (c == '/' && i + 1 < line.length() && line.charAt(i + 1) == '/') {
                // Found a comment, process any existing token and exit the loop
                if (currentToken.length() > 0) {
                    addToken(currentState, currentToken.toString(), lineNumber);
                }
                // Skip the rest of the line
                return;
            }
            
            // Ignorar espacios en blanco
            if (isWhitespace(c)) {
                if (currentToken.length() > 0) {
                    addToken(currentState, currentToken.toString(), lineNumber);
                    currentToken = new StringBuilder();
                    currentState = "s0";
                }
                i++;
                continue;
            }

            // Manejar delimitadores y operadores
            if (isDelimiter(c)) {
                if (currentToken.length() > 0) {
                    addToken(currentState, currentToken.toString(), lineNumber);
                    currentToken = new StringBuilder();
                }
                tokens.add(new TheToken(String.valueOf(c), "DELIMITER", lineNumber));
                currentState = "s0";
                i++;
                continue;
            }

            if (isOperator(c)) {
                if (currentToken.length() > 0) {
                    addToken(currentState, currentToken.toString(), lineNumber);
                    currentToken = new StringBuilder();
                }
                
                // Verificar operadores dobles
                String operator = String.valueOf(c);
                if (i + 1 < line.length()) {
                    char nextChar = line.charAt(i + 1);
                    
                    // Check for comment marker
                    if (c == '/' && nextChar == '/') {
                        // It's a comment, exit the loop
                        return;
                    }
                    
                    if (isPartOfDoubleOperator(c, nextChar)) {
                        operator = c + String.valueOf(nextChar);
                        i++;
                    }
                }
                tokens.add(new TheToken(operator, "OPERATOR", lineNumber));
                currentState = "s0";
                i++;
                continue;
            }

            // Procesar caracteres normales
            String nextState = dfa.getNextState(currentState, c);
            if (nextState != null) {
                currentToken.append(c);
                currentState = nextState;
            } else {
                if (currentToken.length() > 0) {
                    addToken(currentState, currentToken.toString(), lineNumber);
                    currentToken = new StringBuilder();
                }
                currentToken.append(c);
                currentState = "s0";
            }
            i++;
        }

        // Procesar el último token
        if (currentToken.length() > 0) {
            addToken(currentState, currentToken.toString(), lineNumber);
        }
    }

    private void addToken(String state, String value, int lineNumber) {
        if (dfa.isAcceptState(state)) {
            String type = dfa.getAcceptStateName(state);
            if (type.equals("IDENTIFIER") && KEYWORDS.contains(value.toLowerCase())) {
                tokens.add(new TheToken(value, "KEYWORD", lineNumber));
            } else if (type.equals("BINARY") && !value.matches("0b[01]+")) {
                tokens.add(new TheToken(value, "ERROR", lineNumber));
            } else {
                tokens.add(new TheToken(value, type, lineNumber));
            }
        } else {
            tokens.add(new TheToken(value, "ERROR", lineNumber));
        }
    }

    private boolean isWhitespace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    private boolean isDelimiter(char c) {
        return c == ',' || c == ';' || c == '(' || c == ')' || c == '[' || c == ']' 
            || c == '{' || c == '}' || c == ':'; 
    }

    private boolean isOperator(char c) {
        return  c == '+' || c == '-' || c == '*' || c == '/' || c == '=' || 
                c == '<' || c == '>' || c == '%' || c == '!' || c == '&' || c == '|';
    }

    private boolean isPartOfDoubleOperator(char first, char second) {
        String op = first + "" + second;
        return  op.equals("==") || op.equals("!=") || op.equals("<=") || 
                op.equals(">=") || op.equals("+=") || op.equals("-=") || 
                op.equals("*=") || op.equals("/=") || op.equals("%=") ||
                op.equals("++") || op.equals("--") || op.equals("||") ||
                op.equals("&&");
    }

    public void printTokens() {
        System.out.println("\nToken List:");
        System.out.printf("%-15s -> %-12s -> %s\n", "Value", "Type", "Line");
        System.out.println("-".repeat(40));
        
        for (TheToken token : tokens) {
            System.out.printf("%-15s -> %-12s -> %d\n", 
                truncateValue(token.getValue()), 
                token.getType(),
                token.getLineNumber());
        }
        System.out.println("\nTotal tokens: " + tokens.size());
    }

    private String truncateValue(String value) {
        return value.length() > 15 ? value.substring(0, 12) + "..." : value;
    }

    public Vector<TheToken> getTokens() {
        return tokens;
    }
}
