import java.io.*;
import java.util.*;

public class Lexer {
    private File file;
    private Automata dfa;
    private Vector<Token> tokens;
    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
        "int", "float", "if", "else", "while", "do", "break", "continue", "end", "boolean"
    ));

    public Lexer(File file) {
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
            while ((line = reader.readLine()) != null) {
                processLine(line);
            }
        }
    }

    private void processLine(String line) {
        int i = 0;
        StringBuilder currentToken = new StringBuilder();
        String currentState = "s0";

        while (i < line.length()) {
            char c = line.charAt(i);
            
            // Ignorar espacios en blanco
            if (isWhitespace(c)) {
                if (currentToken.length() > 0) {
                    addToken(currentState, currentToken.toString());
                    currentToken = new StringBuilder();
                    currentState = "s0";
                }
                i++;
                continue;
            }

            // Manejar delimitadores y operadores
            if (isDelimiter(c)) {
                if (currentToken.length() > 0) {
                    addToken(currentState, currentToken.toString());
                    currentToken = new StringBuilder();
                }
                tokens.add(new Token(String.valueOf(c), "DELIMITER"));
                currentState = "s0";
                i++;
                continue;
            }

            if (isOperator(c)) {
                if (currentToken.length() > 0) {
                    addToken(currentState, currentToken.toString());
                    currentToken = new StringBuilder();
                }
                
                // Verificar operadores dobles
                String operator = String.valueOf(c);
                if (i + 1 < line.length() && isPartOfDoubleOperator(c, line.charAt(i + 1))) {
                    operator = c + String.valueOf(line.charAt(i + 1));
                    i++;
                }
                tokens.add(new Token(operator, "OPERATOR"));
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
                    addToken(currentState, currentToken.toString());
                    currentToken = new StringBuilder();
                }
                currentToken.append(c);
                currentState = "s0";
            }
            i++;
        }

        // Procesar el último token
        if (currentToken.length() > 0) {
            addToken(currentState, currentToken.toString());
        }
    }

    private void addToken(String state, String value) {
        if (dfa.isAcceptState(state)) {
            String type = dfa.getAcceptStateName(state);
            if (type.equals("IDENTIFIER") && KEYWORDS.contains(value.toLowerCase())) {
                tokens.add(new Token(value, "KEYWORD"));
            } else if (type.equals("BINARY") && !value.matches("0b[01]+")) {
                tokens.add(new Token(value, "ERROR"));
            } else {
                tokens.add(new Token(value, type));
            }
        } else {
            tokens.add(new Token(value, "ERROR"));
        }
    }

    private boolean isWhitespace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    private boolean isDelimiter(char c) {
        return c == ',' || c == ';' || c == '(' || c == ')' || c == '[' || c == ']' 
            || c == '{' || c == '}' || c == 'A';  // Si 'A' se considera delimitador
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '=' || 
               c == '<' || c == '>' || c == '%';
    }

    private boolean isPartOfDoubleOperator(char first, char second) {
        String op = first + "" + second;
        return op.equals("==") || op.equals("!=") || op.equals("<=") || 
               op.equals(">=") || op.equals("+=") || op.equals("-=") || 
               op.equals("*=") || op.equals("/=");
    }

    public void printTokens() {
        System.out.println("\nToken List:");
        System.out.printf("%-15s -> %-12s\n", "Value", "Type");
        System.out.println("-".repeat(30));
        
        for (Token token : tokens) {
            System.out.printf("%-15s -> %-12s\n", 
                truncateValue(token.getValue()), 
                token.getType());
        }
        System.out.println("\nTotal tokens: " + tokens.size());
    }

    private String truncateValue(String value) {
        return value.length() > 15 ? value.substring(0, 12) + "..." : value;
    }

    public Vector<Token> getTokens() {
        return tokens;
    }
}
