import java.util.Vector;
import path.to.Lexer;

public class TheParser {
    private Vector<TheToken> tokens;
    private int currentToken;

    public TheParser(Vector<TheToken> tokens) {
        this.tokens = tokens;
        currentToken = 0;
    }

    public void run() {
        RULE_PROGRAM();
    }

    // PROGRAM: { global variable declarations methods }
    private void RULE_PROGRAM() {
        System.out.println("- RULE_PROGRAM");
        if (tokens.get(currentToken).getValue().equals("{")) {
            currentToken++;
            System.out.println("- {");
        } else {
            error(1);
        }
        
        // Global variable declarations
        RULE_DECLARATIONS();
        
        // Method declarations
        RULE_METHODS();
        
        if (tokens.get(currentToken).getValue().equals("}")) {
            currentToken++;
            System.out.println("- }");
        } else {
            error(2);
        }
    }

    // DECLARATIONS: (type identifier (= EXPRESSION)? ;)*
    private void RULE_DECLARATIONS() {
        System.out.println("-- RULE_DECLARATIONS");
        while (isType(tokens.get(currentToken))) {
            RULE_TYPES();
            if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
                System.out.println("-- IDENTIFIER");
                currentToken++;
                
                // Optional initialization
                if (tokens.get(currentToken).getValue().equals("=")) {
                    currentToken++;
                    RULE_EXPRESSION();
                }
                
                if (tokens.get(currentToken).getValue().equals(";")) {
                    currentToken++;
                    System.out.println("-- ;");
                } else {
                    error(7);
                }
            } else {
                error(6);
            }
        }
    }

    // METHODS: (type identifier ( PARAMS ) { BODY })*
    private void RULE_METHODS() {
        System.out.println("-- RULE_METHODS");
        while (isType(tokens.get(currentToken))) {
            RULE_TYPES();
            
            // Method name
            if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
                currentToken++;
                System.out.println("-- IDENTIFIER");
            } else {
                error(8);
            }
            
            // Parameters
            if (tokens.get(currentToken).getValue().equals("(")) {
                currentToken++;
                System.out.println("-- (");
                RULE_PARAMS();
                if (tokens.get(currentToken).getValue().equals(")")) {
                    currentToken++;
                    System.out.println("-- )");
                } else {
                    error(9);
                }
            } else {
                error(10);
            }
            
            // Method body
            if (tokens.get(currentToken).getValue().equals("{")) {
                currentToken++;
                System.out.println("-- {");
                RULE_BODY();
                if (tokens.get(currentToken).getValue().equals("}")) {
                    currentToken++;
                    System.out.println("-- }");
                } else {
                    error(11);
                }
            } else {
                error(12);
            }
        }
    }

    // TYPES: int | float | char | boolean | void
    private void RULE_TYPES() {
        System.out.println("--- RULE_TYPES");
        if (isType(tokens.get(currentToken))) {
            System.out.println("--- " + tokens.get(currentToken).getValue());
            currentToken++;
        } else {
            error(13);
        }
    }

    // Check if token is a valid type
    private boolean isType(TheToken token) {
        return token.getValue().equals("int") || token.getValue().equals("float") ||
               token.getValue().equals("char") || token.getValue().equals("boolean") ||
               token.getValue().equals("void");
    }

    // PARAMS: (type identifier (, type identifier)*)?
    private void RULE_PARAMS() {
        System.out.println("--- RULE_PARAMS");
        if (isType(tokens.get(currentToken))) {
            RULE_TYPES();
            if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
                currentToken++;
                System.out.println("--- IDENTIFIER");
            } else {
                error(14);
            }
            
            // Optional additional parameters
            while (tokens.get(currentToken).getValue().equals(",")) {
                currentToken++;
                System.out.println("--- ,");
                RULE_TYPES();
                if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
                    currentToken++;
                    System.out.println("--- IDENTIFIER");
                } else {
                    error(15);
                }
            }
        }
    }

    // BODY: Include various statement types
    private void RULE_BODY() {
        System.out.println("--- RULE_BODY");
        while (!tokens.get(currentToken).getValue().equals("}")) {
            // Local variable declarations
            if (isType(tokens.get(currentToken))) {
                RULE_VARIABLE();
            }
            // Method calls
            else if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
                RULE_CALL_METHOD();
            }
            // Control structures
            else if (tokens.get(currentToken).getValue().equals("if")) {
                RULE_IF();
            }
            else if (tokens.get(currentToken).getValue().equals("while")) {
                RULE_WHILE();
            }
            else if (tokens.get(currentToken).getValue().equals("do")) {
                RULE_DO_WHILE();
            }
            else if (tokens.get(currentToken).getValue().equals("for")) {
                RULE_FOR();
            }
            else if (tokens.get(currentToken).getValue().equals("switch")) {
                RULE_SWITCH();
            }
            else if (tokens.get(currentToken).getValue().equals("return")) {
                RULE_RETURN();
            }
            else {
                // If no specific rule matches, try processing an expression
                RULE_EXPRESSION();
                if (tokens.get(currentToken).getValue().equals(";")) {
                    currentToken++;
                } else {
                    error(16);
                }
            }
        }
    }

    // Local variable declaration with optional initialization
    private void RULE_VARIABLE() {
        System.out.println("--- RULE_VARIABLE");
        RULE_TYPES();
        if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
            currentToken++;
            
            // Optional initialization
            if (tokens.get(currentToken).getValue().equals("=")) {
                currentToken++;
                RULE_EXPRESSION();
            }
            
            if (tokens.get(currentToken).getValue().equals(";")) {
                currentToken++;
            } else {
                error(17);
            }
        } else {
            error(18);
        }
    }

    // IF statement
    private void RULE_IF() {
        System.out.println("--- RULE_IF");
        currentToken++; // Consume 'if'
        
        // Condition in parentheses
        if (tokens.get(currentToken).getValue().equals("(")) {
            currentToken++;
            RULE_EXPRESSION();
            if (tokens.get(currentToken).getValue().equals(")")) {
                currentToken++;
            } else {
                error(19);
            }
        } else {
            error(20);
        }
        
        // Body (single statement or block)
        if (tokens.get(currentToken).getValue().equals("{")) {
            currentToken++;
            RULE_BODY();
            if (tokens.get(currentToken).getValue().equals("}")) {
                currentToken++;
            } else {
                error(21);
            }
        } else {
            // Single line body without braces
            RULE_EXPRESSION();
            if (tokens.get(currentToken).getValue().equals(";")) {
                currentToken++;
            } else {
                error(22);
            }
        }
        
        // Optional ELSE
        if (tokens.get(currentToken).getValue().equals("else")) {
            currentToken++;
            // Similar body processing for else
            if (tokens.get(currentToken).getValue().equals("{")) {
                currentToken++;
                RULE_BODY();
                if (tokens.get(currentToken).getValue().equals("}")) {
                    currentToken++;
                } else {
                    error(23);
                }
            } else {
                RULE_EXPRESSION();
                if (tokens.get(currentToken).getValue().equals(";")) {
                    currentToken++;
                } else {
                    error(24);
                }
            }
        }
    }

    // WHILE statement
    private void RULE_WHILE() {
        System.out.println("--- RULE_WHILE");
        currentToken++; // Consume 'while'
        
        // Condition in parentheses
        if (tokens.get(currentToken).getValue().equals("(")) {
            currentToken++;
            RULE_EXPRESSION();
            if (tokens.get(currentToken).getValue().equals(")")) {
                currentToken++;
            } else {
                error(25);
            }
        } else {
            error(26);
        }
        
        // Body (single statement or block)
        if (tokens.get(currentToken).getValue().equals("{")) {
            currentToken++;
            RULE_BODY();
            if (tokens.get(currentToken).getValue().equals("}")) {
                currentToken++;
            } else {
                error(27);
            }
        } else {
            RULE_EXPRESSION();
            if (tokens.get(currentToken).getValue().equals(";")) {
                currentToken++;
            } else {
                error(28);
            }
        }
    }

    // Additional method stubs for further implementation
    private void RULE_DO_WHILE() {
        System.out.println("--- RULE_DO_WHILE");
        // TODO: Implement DO-WHILE logic
        error(29);
    }

    private void RULE_FOR() {
        System.out.println("--- RULE_FOR");
        // TODO: Implement FOR loop logic
        error(30);
    }

    private void RULE_SWITCH() {
        System.out.println("--- RULE_SWITCH");
        // TODO: Implement SWITCH statement logic
        error(31);
    }

    // Method call
    private void RULE_CALL_METHOD() {
        System.out.println("--- RULE_CALL_METHOD");
        if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
            currentToken++;
            
            // Method parameters
            if (tokens.get(currentToken).getValue().equals("(")) {
                currentToken++;
                RULE_PARAM_VALUES();
                if (tokens.get(currentToken).getValue().equals(")")) {
                    currentToken++;
                } else {
                    error(32);
                }
            } else {
                error(33);
            }
            
            // End with semicolon
            if (tokens.get(currentToken).getValue().equals(";")) {
                currentToken++;
            } else {
                error(34);
            }
        } else {
            error(35);
        }
    }

    // Method call parameters
    private void RULE_PARAM_VALUES() {
        System.out.println("--- RULE_PARAM_VALUES");
        if (!tokens.get(currentToken).getValue().equals(")")) {
            RULE_EXPRESSION();
            
            // Multiple parameters
            while (tokens.get(currentToken).getValue().equals(",")) {
                currentToken++;
                RULE_EXPRESSION();
            }
        }
    }

    // RETURN statement
    private void RULE_RETURN() {
        System.out.println("--- RULE_RETURN");
        currentToken++; // Consume 'return'
        
        // Optional return value
        if (!tokens.get(currentToken).getValue().equals(";")) {
            RULE_EXPRESSION();
        }
        
        if (tokens.get(currentToken).getValue().equals(";")) {
            currentToken++;
        } else {
            error(36);
        }
    }

    // Expression handling with operator precedence
    private void RULE_EXPRESSION() {
        System.out.println("--- RULE_EXPRESSION");
        RULE_X(); // Lowest precedence
    }

    private void RULE_X() {
        RULE_Y();
        while (isAssignmentOperator(tokens.get(currentToken))) {
            currentToken++;
            RULE_Y();
        }
    }

    private void RULE_Y() {
        RULE_R();
        while (isLogicalOperator(tokens.get(currentToken))) {
            currentToken++;
            RULE_R();
        }
    }

    private void RULE_R() {
        RULE_E();
        while (isComparisonOperator(tokens.get(currentToken))) {
            currentToken++;
            RULE_E();
        }
    }

    private void RULE_E() {
        RULE_A();
        while (isAdditiveOperator(tokens.get(currentToken))) {
            currentToken++;
            RULE_A();
        }
    }

    private void RULE_A() {
        RULE_B();
        while (isMultiplicativeOperator(tokens.get(currentToken))) {
            currentToken++;
            RULE_B();
        }
    }

    private void RULE_B() {
        RULE_C();
    }

    private void RULE_C() {
        // Handling different types of expressions: method calls, variables, literals
        if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
            // Check if it's a method call
            if (tokens.get(currentToken + 1).getValue().equals("(")) {
                RULE_CALL_METHOD();
            } else {
                currentToken++; // Simple variable
            }
        } else if (isLiteral(tokens.get(currentToken))) {
            currentToken++;
        } else if (tokens.get(currentToken).getValue().equals("(")) {
            currentToken++;
            RULE_EXPRESSION();
            if (tokens.get(currentToken).getValue().equals(")")) {
                currentToken++;
            } else {
                error(37);
            }
        } else {
            error(38);
        }
    }

    // Helper methods for operator and literal checks
    private boolean isAssignmentOperator(TheToken token) {
        return token.getValue().equals("=") || token.getValue().equals("+=") ||
               token.getValue().equals("-=") || token.getValue().equals("*=") ||
               token.getValue().equals("/=");
    }

    private boolean isLogicalOperator(TheToken token) {
        return token.getValue().equals("&&") || token.getValue().equals("||");
    }

    private boolean isComparisonOperator(TheToken token) {
        return token.getValue().equals("==") || token.getValue().equals("!=") ||
               token.getValue().equals("<") || token.getValue().equals(">") ||
               token.getValue().equals("<=") || token.getValue().equals(">=");
    }

    private boolean isAdditiveOperator(TheToken token) {
        return token.getValue().equals("+") || token.getValue().equals("-");
    }

    private boolean isMultiplicativeOperator(TheToken token) {
        return token.getValue().equals("*") || token.getValue().equals("/") ||
               token.getValue().equals("%");
    }

    private boolean isLiteral(TheToken token) {
        return token.getType().equals("INTEGER") || token.getType().equals("FLOAT") ||
               token.getType().equals("BINARY") || token.getType().equals("OCTAL") ||
               token.getType().equals("HEX");
    }

    // Error handling method
    private void error(int error) {
        System.out.println("Error " + error + " at token: " + tokens.get(currentToken));
        System.exit(1);
    }
}
