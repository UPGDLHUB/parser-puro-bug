import java.util.Vector;

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
        
        // Declaraciones de atributos globales
        RULE_DECLARATIONS();
        
        // Declaraciones de métodos
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
            
            // Nombre del método
            if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
                currentToken++;
                System.out.println("-- IDENTIFIER");
            } else {
                error(8);
            }
            
            // Parámetros
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
            
            // Cuerpo del método
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
            
            // Parámetros adicionales opcionales
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
            // Declaraciones de variables locales
            if (isType(tokens.get(currentToken))) {
                RULE_VARIABLE();
            }
            // Llamadas a métodos
            else if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
                RULE_CALL_METHOD();
            }
            // Estructuras de control
            else if (tokens.get(currentToken).getValue().equals("if")) {
                RULE_IF();
            }
            else if (tokens.get(currentToken).getValue().equals("while")) {
                RULE_WHILE();
            }
            // Agregar más estructuras de control según sea necesario
            else {
                // Procesar expresiones
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
            
            // Inicialización opcional
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
        currentToken++; // Consumir 'if'
        
        // Condición entre paréntesis
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
        
        // Cuerpo (una línea o bloque)
        if (tokens.get(currentToken).getValue().equals("{")) {
            currentToken++;
            RULE_BODY();
            if (tokens.get(currentToken).getValue().equals("}")) {
                currentToken++;
            } else {
                error(21);
            }
        } else {
            RULE_EXPRESSION();
            if (tokens.get(currentToken).getValue().equals(";")) {
                currentToken++;
            } else {
                error(22);
            }
        }
    }

    // WHILE statement
    private void RULE_WHILE() {
        System.out.println("--- RULE_WHILE");
        currentToken++; // Consumir 'while'
        
        // Condición entre paréntesis
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
        
        // Cuerpo (una línea o bloque)
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

    // Method call
    private void RULE_CALL_METHOD() {
        System.out.println("--- RULE_CALL_METHOD");
        if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
            currentToken++;
            
            // Parámetros del método
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
            
            // Terminar con punto y coma
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
        currentToken++; // Consumir 'return'
        
        // Valor de retorno opcional
        if (!tokens.get(currentToken).getValue().equals(";")) {
            RULE_EXPRESSION();
        }
        
        if (tokens.get(currentToken).getValue().equals(";")) {
            currentToken++;
        } else {
            error(30);
        }
    }

    // Expression rule
    private void RULE_EXPRESSION() {
        RULE_X();
        while (tokens.get(currentToken).getValue().equals("|")) {
            System.out.println("--- |");
            currentToken++;
            RULE_X();
        }
    }

    private void RULE_X() {
        RULE_Y();
        while (tokens.get(currentToken).getValue().equals("&")) {
            System.out.println("--- &");
            currentToken++;
            RULE_Y();
        }
    }

    private void RULE_Y() {
        if (tokens.get(currentToken).getValue().equals("!")) {
            System.out.println("--- !");
            currentToken++;
        }
        RULE_R();
    }

    private void RULE_R() {
        while (tokens.get(currentToken).getValue().equals("<") |
               tokens.get(currentToken).getValue().equals(">") |
               tokens.get(currentToken).getValue().equals("==") |
               tokens.get(currentToken).getValue().equals("!=")) {
            System.out.println("--- " + tokens.get(currentToken).getValue());
            currentToken++;
            RULE_E();
        }
    }

    private void RULE_E() {
        RULE_A();
        while (tokens.get(currentToken).getValue().equals("-") |
              tokens.get(currentToken).getValue().equals("+")) {
            System.out.println("--- " + tokens.get(currentToken).getValue());
            currentToken++;
            RULE_A();
        }
    }

    private void RULE_A() {
        RULE_B();
        while (tokens.get(currentToken).getValue().equals("/") |
              tokens.get(currentToken).getValue().equals("*")) {
            System.out.println("--- " + tokens.get(currentToken).getValue());
            currentToken++;
            RULE_B();
        }
    }

    private void RULE_B() {
        if (tokens.get(currentToken).getValue().equals("-")) {
            System.out.println("--- -");
            currentToken++;
        }
        //RULE_C();
    }
    
    // Error handling
    private void error(int code) {
        System.out.println("Error in code: " + code);
    }
}
