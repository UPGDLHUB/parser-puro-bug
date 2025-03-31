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

    // Método de error mejorado
    private void error(int error) {
        System.out.println("Error " + error +
                " at line " + tokens.get(currentToken).getLineNumber() +
                ", token: " + tokens.get(currentToken).getValue());
        System.exit(1);
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
                    System.out.println("-- =");
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

    // TYPES: int | float | char | boolean | void | string
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
        return token.getType().equals("KEYWORD") &&
            (token.getValue().equals("int") ||
             token.getValue().equals("float") ||
             token.getValue().equals("void") ||
             token.getValue().equals("char") ||
             token.getValue().equals("string") ||
             token.getValue().equals("boolean"));
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
            // Llamadas a métodos o asignaciones
            else if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
                String identifier = tokens.get(currentToken).getValue();
                currentToken++;
                
                if (tokens.get(currentToken).getValue().equals("(")) {
                    // Es una llamada a método
                    currentToken--; // Retroceder para procesar correctamente
                    RULE_CALL_METHOD();
                } else if (tokens.get(currentToken).getValue().equals("=")) {
                    // Es una asignación
                    currentToken--;  // Retroceder para procesar correctamente
                    RULE_ASSIGNMENT();
                } else {
                    error(50);
                }
            }
            // Estructuras de control
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
                // Procesar expresiones
                RULE_EXPRESSION();
                if (tokens.get(currentToken).getValue().equals(";")) {
                    currentToken++;
                    System.out.println("--- ;");
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
            System.out.println("--- IDENTIFIER");
            
            // Inicialización opcional
            if (tokens.get(currentToken).getValue().equals("=")) {
                currentToken++;
                System.out.println("--- =");
                RULE_EXPRESSION();
            }
            
            if (tokens.get(currentToken).getValue().equals(";")) {
                currentToken++;
                System.out.println("--- ;");
            } else {
                error(17);
            }
        } else {
            error(18);
        }
    }

    // Assignment: identifier = EXPRESSION ;
    private void RULE_ASSIGNMENT() {
        System.out.println("--- RULE_ASSIGNMENT");
        if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
            currentToken++;
            System.out.println("--- IDENTIFIER");
            
            if (tokens.get(currentToken).getValue().equals("=")) {
                currentToken++;
                System.out.println("--- =");
                RULE_EXPRESSION();
                
                if (tokens.get(currentToken).getValue().equals(";")) {
                    currentToken++;
                    System.out.println("--- ;");
                } else {
                    error(51);
                }
            } else {
                error(52);
            }
        } else {
            error(53);
        }
    }

    // IF statement
    private void RULE_IF() {
        System.out.println("--- RULE_IF");
        currentToken++; // Consumir 'if'
        System.out.println("--- if");
        
        // Condición entre paréntesis
        if (tokens.get(currentToken).getValue().equals("(")) {
            currentToken++;
            System.out.println("--- (");
            RULE_EXPRESSION();
            if (tokens.get(currentToken).getValue().equals(")")) {
                currentToken++;
                System.out.println("--- )");
            } else {
                error(19);
            }
        } else {
            error(20);
        }
        
        // Cuerpo (una línea o bloque)
        if (tokens.get(currentToken).getValue().equals("{")) {
            currentToken++;
            System.out.println("--- {");
            RULE_BODY();
            if (tokens.get(currentToken).getValue().equals("}")) {
                currentToken++;
                System.out.println("--- }");
            } else {
                error(21);
            }
        } else {
            // Single statement without braces
            if (isType(tokens.get(currentToken))) {
                RULE_VARIABLE();
            } else if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
                String identifier = tokens.get(currentToken).getValue();
                currentToken++;
                
                if (tokens.get(currentToken).getValue().equals("(")) {
                    currentToken--; // Retroceder para procesar correctamente
                    RULE_CALL_METHOD();
                } else if (tokens.get(currentToken).getValue().equals("=")) {
                    currentToken--; // Retroceder para procesar correctamente
                    RULE_ASSIGNMENT();
                } else {
                    error(54);
                }
            } else if (tokens.get(currentToken).getValue().equals("if")) {
                RULE_IF();
            } else if (tokens.get(currentToken).getValue().equals("while")) {
                RULE_WHILE();
            } else if (tokens.get(currentToken).getValue().equals("do")) {
                RULE_DO_WHILE();
            } else if (tokens.get(currentToken).getValue().equals("for")) {
                RULE_FOR();
            } else if (tokens.get(currentToken).getValue().equals("switch")) {
                RULE_SWITCH();
            } else if (tokens.get(currentToken).getValue().equals("return")) {
                RULE_RETURN();
            } else {
                RULE_EXPRESSION();
                if (tokens.get(currentToken).getValue().equals(";")) {
                    currentToken++;
                    System.out.println("--- ;");
                } else {
                    error(22);
                }
            }
        }
        
        // Optional else part
        if (currentToken < tokens.size() && tokens.get(currentToken).getValue().equals("else")) {
            currentToken++;
            System.out.println("--- else");
            
            if (tokens.get(currentToken).getValue().equals("{")) {
                currentToken++;
                System.out.println("--- {");
                RULE_BODY();
                if (tokens.get(currentToken).getValue().equals("}")) {
                    currentToken++;
                    System.out.println("--- }");
                } else {
                    error(23);
                }
            } else if (tokens.get(currentToken).getValue().equals("if")) {
                // Handle else if
                RULE_IF();
            } else {
                // Single statement without braces
                if (isType(tokens.get(currentToken))) {
                    RULE_VARIABLE();
                } else if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
                    String identifier = tokens.get(currentToken).getValue();
                    currentToken++;
                    
                    if (tokens.get(currentToken).getValue().equals("(")) {
                        currentToken--; // Retroceder para procesar correctamente
                        RULE_CALL_METHOD();
                    } else if (tokens.get(currentToken).getValue().equals("=")) {
                        currentToken--; // Retroceder para procesar correctamente
                        RULE_ASSIGNMENT();
                    } else {
                        error(55);
                    }
                } else {
                    RULE_EXPRESSION();
                    if (tokens.get(currentToken).getValue().equals(";")) {
                        currentToken++;
                        System.out.println("--- ;");
                    } else {
                        error(24);
                    }
                }
            }
        }
    }

    // WHILE statement
    private void RULE_WHILE() {
        System.out.println("--- RULE_WHILE");
        currentToken++; // Consumir 'while'
        System.out.println("--- while");
        
        // Condición entre paréntesis
        if (tokens.get(currentToken).getValue().equals("(")) {
            currentToken++;
            System.out.println("--- (");
            RULE_EXPRESSION();
            if (tokens.get(currentToken).getValue().equals(")")) {
                currentToken++;
                System.out.println("--- )");
            } else {
                error(25);
            }
        } else {
            error(26);
        }
        
        // Cuerpo (una línea o bloque)
        if (tokens.get(currentToken).getValue().equals("{")) {
            currentToken++;
            System.out.println("--- {");
            RULE_BODY();
            if (tokens.get(currentToken).getValue().equals("}")) {
                currentToken++;
                System.out.println("--- }");
            } else {
                error(27);
            }
        } else {
            // Single statement without braces
            if (isType(tokens.get(currentToken))) {
                RULE_VARIABLE();
            } else if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
                String identifier = tokens.get(currentToken).getValue();
                currentToken++;
                
                if (tokens.get(currentToken).getValue().equals("(")) {
                    currentToken--; // Retroceder para procesar correctamente
                    RULE_CALL_METHOD();
                } else if (tokens.get(currentToken).getValue().equals("=")) {
                    currentToken--; // Retroceder para procesar correctamente
                    RULE_ASSIGNMENT();
                } else {
                    error(56);
                }
            } else if (tokens.get(currentToken).getValue().equals("if")) {
                RULE_IF();
            } else if (tokens.get(currentToken).getValue().equals("while")) {
                RULE_WHILE();
            } else if (tokens.get(currentToken).getValue().equals("do")) {
                RULE_DO_WHILE();
            } else if (tokens.get(currentToken).getValue().equals("for")) {
                RULE_FOR();
            } else if (tokens.get(currentToken).getValue().equals("switch")) {
                RULE_SWITCH();
            } else if (tokens.get(currentToken).getValue().equals("return")) {
                RULE_RETURN();
            } else {
                RULE_EXPRESSION();
                if (tokens.get(currentToken).getValue().equals(";")) {
                    currentToken++;
                    System.out.println("--- ;");
                } else {
                    error(28);
                }
            }
        }
    }

    // DO-WHILE statement
    private void RULE_DO_WHILE() {
        System.out.println("--- RULE_DO_WHILE");
        currentToken++; // Consumir 'do'
        System.out.println("--- do");
        
        // Cuerpo (una línea o bloque)
        if (tokens.get(currentToken).getValue().equals("{")) {
            currentToken++;
            System.out.println("--- {");
            RULE_BODY();
            if (tokens.get(currentToken).getValue().equals("}")) {
                currentToken++;
                System.out.println("--- }");
            } else {
                error(40);
            }
        } else {
            // Single statement without braces
            if (isType(tokens.get(currentToken))) {
                RULE_VARIABLE();
            } else if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
                String identifier = tokens.get(currentToken).getValue();
                currentToken++;
                
                if (tokens.get(currentToken).getValue().equals("(")) {
                    currentToken--; // Retroceder para procesar correctamente
                    RULE_CALL_METHOD();
                } else if (tokens.get(currentToken).getValue().equals("=")) {
                    currentToken--; // Retroceder para procesar correctamente
                    RULE_ASSIGNMENT();
                } else {
                    error(57);
                }
            } else {
                RULE_EXPRESSION();
                if (tokens.get(currentToken).getValue().equals(";")) {
                    currentToken++;
                    System.out.println("--- ;");
                } else {
                    error(41);
                }
            }
        }
        
        // While condition
        if (tokens.get(currentToken).getValue().equals("while")) {
            currentToken++;
            System.out.println("--- while");
            
            if (tokens.get(currentToken).getValue().equals("(")) {
                currentToken++;
                System.out.println("--- (");
                RULE_EXPRESSION();
                if (tokens.get(currentToken).getValue().equals(")")) {
                    currentToken++;
                    System.out.println("--- )");
                    
                    if (tokens.get(currentToken).getValue().equals(";")) {
                        currentToken++;
                        System.out.println("--- ;");
                    } else {
                        error(42);
                    }
                } else {
                    error(43);
                }
            } else {
                error(44);
            }
        } else {
            error(45);
        }
    }

    // FOR statement
    private void RULE_FOR() {
        System.out.println("--- RULE_FOR");
        currentToken++; // Consumir 'for'
        System.out.println("--- for");
        
        if (tokens.get(currentToken).getValue().equals("(")) {
            currentToken++;
            System.out.println("--- (");
            
            // Inicialización (opcional)
            if (!tokens.get(currentToken).getValue().equals(";")) {
                if (isType(tokens.get(currentToken))) {
                    RULE_VARIABLE();
                } else {
                    RULE_EXPRESSION();
                    if (tokens.get(currentToken).getValue().equals(";")) {
                        currentToken++;
                        System.out.println("--- ;");
                    } else {
                        error(60);
                    }
                }
            } else {
                currentToken++;
                System.out.println("--- ;");
            }
            
            // Condición (opcional)
            if (!tokens.get(currentToken).getValue().equals(";")) {
                RULE_EXPRESSION();
            }
            
            if (tokens.get(currentToken).getValue().equals(";")) {
                currentToken++;
                System.out.println("--- ;");
            } else {
                error(61);
            }
            
            // Incremento (opcional)
            if (!tokens.get(currentToken).getValue().equals(")")) {
                RULE_EXPRESSION();
            }
            
            if (tokens.get(currentToken).getValue().equals(")")) {
                currentToken++;
                System.out.println("--- )");
            } else {
                error(62);
            }
            
            // Cuerpo (una línea o bloque)
            if (tokens.get(currentToken).getValue().equals("{")) {
                currentToken++;
                System.out.println("--- {");
                RULE_BODY();
                if (tokens.get(currentToken).getValue().equals("}")) {
                    currentToken++;
                    System.out.println("--- }");
                } else {
                    error(63);
                }
            } else {
                // Single statement without braces
                if (isType(tokens.get(currentToken))) {
                    RULE_VARIABLE();
                } else if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
                    String identifier = tokens.get(currentToken).getValue();
                    currentToken++;
                    
                    if (tokens.get(currentToken).getValue().equals("(")) {
                        currentToken--; // Retroceder para procesar correctamente
                        RULE_CALL_METHOD();
                    } else if (tokens.get(currentToken).getValue().equals("=")) {
                        currentToken--; // Retroceder para procesar correctamente
                        RULE_ASSIGNMENT();
                    } else {
                        error(64);
                    }
                } else {
                    RULE_EXPRESSION();
                    if (tokens.get(currentToken).getValue().equals(";")) {
                        currentToken++;
                        System.out.println("--- ;");
                    } else {
                        error(65);
                    }
                }
            }
        } else {
            error(66);
        }
    }

    // SWITCH statement
    private void RULE_SWITCH() {
        System.out.println("--- RULE_SWITCH");
        currentToken++; // Consumir 'switch'
        System.out.println("--- switch");
        
        if (tokens.get(currentToken).getValue().equals("(")) {
            currentToken++;
            System.out.println("--- (");
            RULE_EXPRESSION();
            if (tokens.get(currentToken).getValue().equals(")")) {
                currentToken++;
                System.out.println("--- )");
            } else {
                error(70);
            }
        } else {
            error(71);
        }
        
        if (tokens.get(currentToken).getValue().equals("{")) {
            currentToken++;
            System.out.println("--- {");
            
            // Procesar case statements
            while (tokens.get(currentToken).getValue().equals("case") || 
                   tokens.get(currentToken).getValue().equals("default")) {
                
                if (tokens.get(currentToken).getValue().equals("case")) {
                    currentToken++;
                    System.out.println("--- case");
                    RULE_EXPRESSION();
                    
                    if (tokens.get(currentToken).getValue().equals(":")) {
                        currentToken++;
                        System.out.println("--- :");
                    } else {
                        error(72);
                    }
                } else { // default
                    currentToken++;
                    System.out.println("--- default");
                    
                    // SWITCH statement (continuación)
                    if (tokens.get(currentToken).getValue().equals(":")) {
                        currentToken++;
                        System.out.println("--- :");
                    } else {
                        error(73);
                    }
                }
                
                // Procesar statements dentro del case/default
                while (!tokens.get(currentToken).getValue().equals("case") && 
                       !tokens.get(currentToken).getValue().equals("default") && 
                       !tokens.get(currentToken).getValue().equals("}")) {
                    
                    if (tokens.get(currentToken).getValue().equals("break")) {
                        currentToken++;
                        System.out.println("--- break");
                        
                        if (tokens.get(currentToken).getValue().equals(";")) {
                            currentToken++;
                            System.out.println("--- ;");
                            break; // Salir del while interior
                        } else {
                            error(74);
                        }
                    }
                    
                    if (isType(tokens.get(currentToken))) {
                        RULE_VARIABLE();
                    } else if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
                        String identifier = tokens.get(currentToken).getValue();
                        currentToken++;
                        
                        if (tokens.get(currentToken).getValue().equals("(")) {
                            currentToken--; // Retroceder para procesar correctamente
                            RULE_CALL_METHOD();
                        } else if (tokens.get(currentToken).getValue().equals("=")) {
                            currentToken--; // Retroceder para procesar correctamente
                            RULE_ASSIGNMENT();
                        } else {
                            error(75);
                        }
                    } else if (tokens.get(currentToken).getValue().equals("if")) {
                        RULE_IF();
                    } else if (tokens.get(currentToken).getValue().equals("while")) {
                        RULE_WHILE();
                    } else if (tokens.get(currentToken).getValue().equals("do")) {
                        RULE_DO_WHILE();
                    } else if (tokens.get(currentToken).getValue().equals("for")) {
                        RULE_FOR();
                    } else if (tokens.get(currentToken).getValue().equals("return")) {
                        RULE_RETURN();
                    } else {
                        RULE_EXPRESSION();
                        if (tokens.get(currentToken).getValue().equals(";")) {
                            currentToken++;
                            System.out.println("--- ;");
                        } else {
                            error(76);
                        }
                    }
                }
            }
            
            if (tokens.get(currentToken).getValue().equals("}")) {
                currentToken++;
                System.out.println("--- }");
            } else {
                error(77);
            }
        } else {
            error(78);
        }
    }

    // Method call
    private void RULE_CALL_METHOD() {
        System.out.println("--- RULE_CALL_METHOD");
        if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
            currentToken++;
            System.out.println("--- IDENTIFIER");
            
            // Parámetros del método
            if (tokens.get(currentToken).getValue().equals("(")) {
                currentToken++;
                System.out.println("--- (");
                RULE_PARAM_VALUES();
                if (tokens.get(currentToken).getValue().equals(")")) {
                    currentToken++;
                    System.out.println("--- )");
                } else {
                    error(32);
                }
            } else {
                error(33);
            }
            
            // Terminar con punto y coma
            if (tokens.get(currentToken).getValue().equals(";")) {
                currentToken++;
                System.out.println("--- ;");
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
                System.out.println("--- ,");
                RULE_EXPRESSION();
            }
        }
    }

    // RETURN statement
    private void RULE_RETURN() {
        System.out.println("--- RULE_RETURN");
        currentToken++; // Consumir 'return'
        System.out.println("--- return");
        
        // Valor de retorno opcional
        if (!tokens.get(currentToken).getValue().equals(";")) {
            RULE_EXPRESSION();
        }
        
        if (tokens.get(currentToken).getValue().equals(";")) {
            currentToken++;
            System.out.println("--- ;");
        } else {
            error(30);
        }
    }

    // Expression rule
    private void RULE_EXPRESSION() {
        System.out.println("--- RULE_EXPRESSION");
        RULE_X();
        while (tokens.get(currentToken).getValue().equals("|")) {
            System.out.println("--- |");
            currentToken++;
            RULE_X();
        }
    }

    private void RULE_X() {
        System.out.println("--- RULE_X");
        RULE_Y();
        while (tokens.get(currentToken).getValue().equals("&")) {
            System.out.println("--- &");
            currentToken++;
            RULE_Y();
        }
    }

    private void RULE_Y() {
        System.out.println("--- RULE_Y");
        if (tokens.get(currentToken).getValue().equals("!")) {
            System.out.println("--- !");
            currentToken++;
        }
        RULE_R();
    }

    private void RULE_R() {
        System.out.println("--- RULE_R");
        RULE_E();
        // Ajuste para operadores de comparación de dos caracteres
        while (tokens.get(currentToken).getValue().equals("<") ||
               tokens.get(currentToken).getValue().equals(">") ||
               tokens.get(currentToken).getValue().equals("=")) {
            
            String operator = tokens.get(currentToken).getValue();
            currentToken++;
            System.out.println("--- " + operator);
            
            // Manejo de operadores de dos caracteres (==, !=, <=, >=)
            if (operator.equals("=") && tokens.get(currentToken).getValue().equals("=")) {
                currentToken++;
                System.out.println("--- =");
            } else if (operator.equals("!") && tokens.get(currentToken).getValue().equals("=")) {
                currentToken++;
                System.out.println("--- =");
            } else if (operator.equals("<") && tokens.get(currentToken).getValue().equals("=")) {
                currentToken++;
                System.out.println("--- =");
            } else if (operator.equals(">") && tokens.get(currentToken).getValue().equals("=")) {
                currentToken++;
                System.out.println("--- =");
            }
            
            RULE_E();
        }
    }

    private void RULE_E() {
        System.out.println("--- RULE_E");
        RULE_A();
        while (tokens.get(currentToken).getValue().equals("-") ||
               tokens.get(currentToken).getValue().equals("+")) {
            System.out.println("--- " + tokens.get(currentToken).getValue());
            currentToken++;
            RULE_A();
        }
    }

    private void RULE_A() {
        System.out.println("--- RULE_A");
        RULE_B();
        while (tokens.get(currentToken).getValue().equals("/") ||
               tokens.get(currentToken).getValue().equals("*")) {
            System.out.println("--- " + tokens.get(currentToken).getValue());
            currentToken++;
            RULE_B();
        }
    }

   private void RULE_B() {
    System.out.println("--- RULE_B");
    if (tokens.get(currentToken).getValue().equals("-")) {
        System.out.println("--- -");
        currentToken++;
    }
    RULE_C();
}

private void RULE_C() {
    System.out.println("--- RULE_C");
    if (tokens.get(currentToken).getType().equals("INTEGER")) {
        System.out.println("--- INTEGER");
        currentToken++;
    }
    else if (tokens.get(currentToken).getType().equals("OCTAL")) {
        System.out.println("--- OCTAL");
        currentToken++;
    }
    else if (tokens.get(currentToken).getType().equals("HEXADECIMAL")) {
        System.out.println("--- HEXADECIMAL");
        currentToken++;
    }
    else if (tokens.get(currentToken).getType().equals("BINARY")) {
        System.out.println("--- BINARY");
        currentToken++;
    }
    else if (tokens.get(currentToken).getValue().equals("true")) {
        System.out.println("--- TRUE");
        currentToken++;
    }
    else if (tokens.get(currentToken).getValue().equals("false")) {
        System.out.println("--- FALSE");
        currentToken++;
    }
    else if (tokens.get(currentToken).getType().equals("STRING")) {
        System.out.println("--- STRING");
        currentToken++;
    }
    else if (tokens.get(currentToken).getType().equals("CHAR")) {
        System.out.println("--- CHAR");
        currentToken++;
    }
    else if (tokens.get(currentToken).getType().equals("FLOAT")) {
        System.out.println("--- FLOAT");
        currentToken++;
    }
    else if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
        // Puede ser una variable o una llamada a método como valor
        currentToken++;
        System.out.println("--- IDENTIFIER");
        
        // Si viene un paréntesis, es una llamada a método como valor
        if (currentToken < tokens.size() && tokens.get(currentToken).getValue().equals("(")) {
            currentToken++;
            System.out.println("--- (");
            RULE_PARAM_VALUES();
            if (tokens.get(currentToken).getValue().equals(")")) {
                currentToken++;
                System.out.println("--- )");
            }
            else {
                error(80);
            }
        }
    }
    else if (tokens.get(currentToken).getValue().equals("(")) {
        System.out.println("--- (");
        currentToken++;
        RULE_EXPRESSION();
        if (tokens.get(currentToken).getValue().equals(")")) {
            System.out.println("--- )");
            currentToken++;
        }
        else {
            error(4);
        }
    }
    else {
        error(5);
    }
}
    
private void error(int error) {
    System.out.println("Error " + error +
            " at line " + tokens.get(currentToken).getLineNumber() +
            ", token: " + tokens.get(currentToken).getValue());
    System.exit(1);
}
