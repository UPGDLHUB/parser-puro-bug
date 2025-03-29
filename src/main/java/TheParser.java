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
        return token.getValue().equals("int") || token.getValue().equals("float") ||
               token.getValue().equals("char") || token.getValue().equals("boolean") ||
               token.getValue().equals("void") || token.getValue().equals("string");
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
            else if (tokens.get(currentToken).getType().equals("IDENTIFIER") && 
                     tokens.size() > currentToken + 1 && 
                     tokens.get(currentToken + 1).getValue().equals("=")) {
                RULE_ASSIGNMENT();
            }
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
                } else {
                    error(16);
                }
            }
        }
    }

    // Local variable declaration with initialization
    private void RULE_VARIABLE() {
        System.out.println("--- RULE_VARIABLE");
        RULE_TYPES();
        
        if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
            currentToken++;
            
            // Inicialización obligatoria o opcional
            if (tokens.get(currentToken).getValue().equals("=")) {
                currentToken++;
                RULE_EXPRESSION();
            }
            
            if (tokens.get(currentToken).getValue().equals(";")) {
                currentToken++;
            } else {
                error(62);
            }
        } else {
            error(63);
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
        
        // Bloque ELSE opcional
        if (currentToken < tokens.size() && tokens.get(currentToken).getValue().equals("else")) {
            currentToken++; // Consumir 'else'
            
            if (tokens.get(currentToken).getValue().equals("{")) {
                currentToken++;
                RULE_BODY();
                if (tokens.get(currentToken).getValue().equals("}")) {
                    currentToken++;
                } else {
                    error(23);
                }
            } else if (tokens.get(currentToken).getValue().equals("if")) {
                // Caso de 'else if'
                RULE_IF();
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

    // DO-WHILE statement
    private void RULE_DO_WHILE() {
        System.out.println("--- RULE_DO_WHILE");
        currentToken++; // Consumir 'do'
        
        // Cuerpo (una línea o bloque)
        if (tokens.get(currentToken).getValue().equals("{")) {
            currentToken++;
            RULE_BODY();
            if (tokens.get(currentToken).getValue().equals("}")) {
                currentToken++;
            } else {
                error(29);
            }
        } else {
            RULE_EXPRESSION();
            if (tokens.get(currentToken).getValue().equals(";")) {
                currentToken++;
            } else {
                error(30);
            }
        }
        
        // Parte while con condición
        if (tokens.get(currentToken).getValue().equals("while")) {
            currentToken++; // Consumir 'while'
            
            if (tokens.get(currentToken).getValue().equals("(")) {
                currentToken++;
                RULE_EXPRESSION();
                if (tokens.get(currentToken).getValue().equals(")")) {
                    currentToken++;
                } else {
                    error(31);
                }
            } else {
                error(32);
            }
            
            if (tokens.get(currentToken).getValue().equals(";")) {
                currentToken++;
            } else {
                error(33);
            }
        } else {
            error(34);
        }
    }

    // FOR statement
    private void RULE_FOR() {
        System.out.println("--- RULE_FOR");
        currentToken++; // Consumir 'for'
        
        // Formato: for (inicialización; condición; incremento)
        if (tokens.get(currentToken).getValue().equals("(")) {
            currentToken++;
            
            // Inicialización (opcional)
            if (!tokens.get(currentToken).getValue().equals(";")) {
                RULE_EXPRESSION();
            }
            
            if (tokens.get(currentToken).getValue().equals(";")) {
                currentToken++;
            } else {
                error(35);
            }
            
            // Condición (opcional)
            if (!tokens.get(currentToken).getValue().equals(";")) {
                RULE_EXPRESSION();
            }
            
            if (tokens.get(currentToken).getValue().equals(";")) {
                currentToken++;
            } else {
                error(36);
            }
            
            // Incremento (opcional)
            if (!tokens.get(currentToken).getValue().equals(")")) {
                RULE_EXPRESSION();
            }
            
            if (tokens.get(currentToken).getValue().equals(")")) {
                currentToken++;
            } else {
                error(37);
            }
        } else {
            error(38);
        }
        
        // Cuerpo (una línea o bloque)
        if (tokens.get(currentToken).getValue().equals("{")) {
            currentToken++;
            RULE_BODY();
            if (tokens.get(currentToken).getValue().equals("}")) {
                currentToken++;
            } else {
                error(39);
            }
        } else {
            RULE_EXPRESSION();
            if (tokens.get(currentToken).getValue().equals(";")) {
                currentToken++;
            } else {
                error(40);
            }
        }
    }

    // SWITCH statement
    private void RULE_SWITCH() {
        System.out.println("--- RULE_SWITCH");
        currentToken++; // Consumir 'switch'
        
        // Expresión entre paréntesis
        if (tokens.get(currentToken).getValue().equals("(")) {
            currentToken++;
            RULE_EXPRESSION();
            if (tokens.get(currentToken).getValue().equals(")")) {
                currentToken++;
            } else {
                error(41);
            }
        } else {
            error(42);
        }
        
        // Cuerpo del switch
        if (tokens.get(currentToken).getValue().equals("{")) {
            currentToken++;
            
            // Casos del switch
            while (tokens.get(currentToken).getValue().equals("case") ||
                   tokens.get(currentToken).getValue().equals("default")) {
                
                if (tokens.get(currentToken).getValue().equals("case")) {
                    currentToken++; // Consumir 'case'
                    RULE_EXPRESSION();
                } else {
                    currentToken++; // Consumir 'default'
                }
                
                if (tokens.get(currentToken).getValue().equals(":")) {
                    currentToken++;
                } else {
                    error(43);
                }
                
                // Cuerpo del caso (hasta el próximo case/default o fin del switch)
                while (!tokens.get(currentToken).getValue().equals("case") &&
                       !tokens.get(currentToken).getValue().equals("default") &&
                       !tokens.get(currentToken).getValue().equals("}")) {
                    
                    // Verificar si hay 'break'
                    if (tokens.get(currentToken).getValue().equals("break")) {
                        currentToken++;
                        if (tokens.get(currentToken).getValue().equals(";")) {
                            currentToken++;
                        } else {
                            error(44);
                        }
                    } else {
                        // Procesar otras instrucciones
                        if (isType(tokens.get(currentToken))) {
                            RULE_VARIABLE();
                        } else if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
                            RULE_CALL_METHOD();
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
                            } else {
                                error(45);
                            }
                        }
                    }
                }
            }
            
            if (tokens.get(currentToken).getValue().equals("}")) {
                currentToken++;
            } else {
                error(46);
            }
        } else {
            error(47);
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
    // Expression rule - highest level of precedence hierarchy
    private void RULE_EXPRESSION() {
        System.out.println("--- RULE_EXPRESSION");
        RULE_X();
    }
    
    // X: Assignment expressions
    private void RULE_X() {
        System.out.println("--- RULE_X");
        RULE_Y();
        
        if (tokens.get(currentToken).getValue().equals("=")) {
            currentToken++;
            RULE_X(); // Recursive to handle chained assignments
        }
    }
    
    // Y: Logical OR
    private void RULE_Y() {
        System.out.println("--- RULE_Y");
        RULE_R();
        
        while (tokens.get(currentToken).getValue().equals("|")) {
            currentToken++;
            if (tokens.get(currentToken).getValue().equals("|")) {
                currentToken++;
                RULE_R();
            } else {
                error(50); // Expected second | for OR operator
            }
        }
    }
    
    // R: Logical AND (adjusted for two-character operators)
    private void RULE_R() {
        System.out.println("--- RULE_R");
        RULE_E();
        
        while (tokens.get(currentToken).getValue().equals("&")) {
            currentToken++;
            if (tokens.get(currentToken).getValue().equals("&")) {
                currentToken++;
                RULE_E();
            } else {
                error(51); // Expected second & for AND operator
            }
        }
    }
    
    // E: Equality expressions
    private void RULE_E() {
        System.out.println("--- RULE_E");
        RULE_A();
        
        while (tokens.get(currentToken).getValue().equals("=") ||
               tokens.get(currentToken).getValue().equals("!")) {
            
            if (tokens.get(currentToken).getValue().equals("=")) {
                currentToken++;
                if (tokens.get(currentToken).getValue().equals("=")) {
                    currentToken++;
                    RULE_A();
                } else {
                    error(52); // Expected second = for equality operator
                }
            } else { // Must be "!"
                currentToken++;
                if (tokens.get(currentToken).getValue().equals("=")) {
                    currentToken++;
                    RULE_A();
                } else {
                    error(53); // Expected = after ! for inequality operator
                }
            }
        }
    }
    
    // A: Relational expressions
    private void RULE_A() {
        System.out.println("--- RULE_A");
        RULE_B();
        
        while (tokens.get(currentToken).getValue().equals("<") ||
               tokens.get(currentToken).getValue().equals(">")) {
            
            String operator = tokens.get(currentToken).getValue();
            currentToken++;
            
            // Handle <= and >= operators as separate tokens
            if (tokens.get(currentToken).getValue().equals("=")) {
                currentToken++;
            }
            
            RULE_B();
        }
    }
    
    // B: Additive expressions
    private void RULE_B() {
        System.out.println("--- RULE_B");
        RULE_C();
        
        while (tokens.get(currentToken).getValue().equals("+") ||
               tokens.get(currentToken).getValue().equals("-")) {
            
            currentToken++;
            RULE_C();
        }
    }
    
    // C: Multiplicative expressions and primary expressions
    private void RULE_C() {
        System.out.println("--- RULE_C");
        
        // Check for identifiers which could be variables or method calls
        if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
            String identifier = tokens.get(currentToken).getValue();
            currentToken++;
            
            // Check if it's a method call
            if (tokens.get(currentToken).getValue().equals("(")) {
                currentToken--;  // Go back to the identifier
                RULE_METHOD_CALL_EXPR();
            }
            // Otherwise it's just a variable reference, already consumed
        }
        // Check for literals
        else if (tokens.get(currentToken).getType().equals("INTEGER") ||
                 tokens.get(currentToken).getType().equals("FLOAT") ||
                 tokens.get(currentToken).getType().equals("CHAR") ||
                 tokens.get(currentToken).getType().equals("STRING") ||
                 tokens.get(currentToken).getValue().equals("true") ||
                 tokens.get(currentToken).getValue().equals("false")) {
            currentToken++;
        }
        // Check for parenthesized expressions
        else if (tokens.get(currentToken).getValue().equals("(")) {
            currentToken++;
            RULE_EXPRESSION();
            if (tokens.get(currentToken).getValue().equals(")")) {
                currentToken++;
            } else {
                error(55);
            }
        }
        else {
            error(56);
        }
        
        // Handle multiplication and division operators
        while (tokens.get(currentToken).getValue().equals("*") ||
               tokens.get(currentToken).getValue().equals("/") ||
               tokens.get(currentToken).getValue().equals("%")) {
            currentToken++;
            
            // Right operand
            if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
                String identifier = tokens.get(currentToken).getValue();
                currentToken++;
                
                // Check if it's a method call
                if (tokens.get(currentToken).getValue().equals("(")) {
                    currentToken--;  // Go back to the identifier
                    RULE_METHOD_CALL_EXPR();
                }
                // Otherwise it's just a variable reference, already consumed
            }
            else if (tokens.get(currentToken).getType().equals("INTEGER") ||
                     tokens.get(currentToken).getType().equals("FLOAT") ||
                     tokens.get(currentToken).getType().equals("CHAR") ||
                     tokens.get(currentToken).getType().equals("STRING") ||
                     tokens.get(currentToken).getValue().equals("true") ||
                     tokens.get(currentToken).getValue().equals("false")) {
                currentToken++;
            }
            else if (tokens.get(currentToken).getValue().equals("(")) {
                currentToken++;
                RULE_EXPRESSION();
                if (tokens.get(currentToken).getValue().equals(")")) {
                    currentToken++;
                } else {
                    error(57);
                }
            }
            else {
                error(58);
            }
        }
    }
    
    // Method call as an expression value
    private void RULE_METHOD_CALL_EXPR() {
        System.out.println("--- RULE_METHOD_CALL_EXPR");
        if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
            currentToken++;
            
            // Parámetros del método
            if (tokens.get(currentToken).getValue().equals("(")) {
                currentToken++;
                RULE_PARAM_VALUES();
                if (tokens.get(currentToken).getValue().equals(")")) {
                    currentToken++;
                } else {
                    error(59);
                }
            } else {
                error(60);
            }
        } else {
            error(61);
        }
    }

    // Assignment rule
    private void RULE_ASSIGNMENT() {
        System.out.println("--- RULE_ASSIGNMENT");
        
        if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
            currentToken++;
            
            if (tokens.get(currentToken).getValue().equals("=")) {
                currentToken++;
                RULE_EXPRESSION();
                
                if (tokens.get(currentToken).getValue().equals(";")) {
                    currentToken++;
                } else {
                    error(64);
                }
            } else {
                error(65); // Expected assignment operator
            }
        } else {
            error(66); // Expected identifier for assignment
        }
    }
    
    // Error handling
    private void error(int code) {
        System.out.println("Error in code: " + code);
    }
}
