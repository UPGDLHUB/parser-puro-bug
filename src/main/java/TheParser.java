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
                String nextValue = tokens.get(currentToken + 1).getValue();
                if (nextValue.equals("(")) {
                    RULE_CALL_METHOD();
                } else if (nextValue.equals("=") || nextValue.equals("+=") || nextValue.equals("-=") || 
                          nextValue.equals("*=") || nextValue.equals("/=")) {
                    RULE_ASSIGNMENT();
                } else {
                    RULE_EXPRESSION();
                    if (tokens.get(currentToken).getValue().equals(";")) {
                        currentToken++;
                    } else {
                        error(16);
                    }
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

    // ASSIGNMENT: identifier = EXPRESSION ;
    private void RULE_ASSIGNMENT() {
        System.out.println("--- RULE_ASSIGNMENT");
        if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
            currentToken++;
            System.out.println("--- IDENTIFIER");
            
            if (tokens.get(currentToken).getValue().equals("=") || 
                tokens.get(currentToken).getValue().equals("+=") || 
                tokens.get(currentToken).getValue().equals("-=") || 
                tokens.get(currentToken).getValue().equals("*=") || 
                tokens.get(currentToken).getValue().equals("/=")) {
                
                System.out.println("--- " + tokens.get(currentToken).getValue());
                currentToken++;
                RULE_EXPRESSION();
                
                if (tokens.get(currentToken).getValue().equals(";")) {
                    currentToken++;
                    System.out.println("--- ;");
                } else {
                    error(17);
                }
            } else {
                error(18);
            }
        } else {
            error(19);
        }
    }

    // EXPRESSION: X (|| X)*
    private void RULE_EXPRESSION() {
        System.out.println("--- RULE_EXPRESSION");
        RULE_X();
        
        while (tokens.get(currentToken).getValue().equals("|") && 
               tokens.get(currentToken + 1).getValue().equals("|")) {
            System.out.println("--- ||");
            currentToken += 2; // Skip both | characters
            RULE_X();
        }
    }

    // X: Y (&& Y)*
    private void RULE_X() {
        System.out.println("---- RULE_X");
        RULE_Y();
        
        while (tokens.get(currentToken).getValue().equals("&") && 
               tokens.get(currentToken + 1).getValue().equals("&")) {
            System.out.println("---- &&");
            currentToken += 2; // Skip both & characters
            RULE_Y();
        }
    }

    // Y: R ((== | !=) R)*
    private void RULE_Y() {
        System.out.println("----- RULE_Y");
        RULE_R();
        
        while (tokens.get(currentToken).getValue().equals("=") && 
               tokens.get(currentToken + 1).getValue().equals("=") ||
               tokens.get(currentToken).getValue().equals("!") && 
               tokens.get(currentToken + 1).getValue().equals("=")) {
            
            System.out.println("----- " + tokens.get(currentToken).getValue() + 
                              tokens.get(currentToken + 1).getValue());
            currentToken += 2; // Skip both characters
            RULE_R();
        }
    }

    // R: E ((< | > | <= | >=) E)*
    private void RULE_R() {
        System.out.println("------ RULE_R");
        RULE_E();
        
        while (tokens.get(currentToken).getValue().equals("<") ||
               tokens.get(currentToken).getValue().equals(">")) {
               
            System.out.print("------ " + tokens.get(currentToken).getValue());
            currentToken++;
            
            // Check for <= or >=
            if (tokens.get(currentToken).getValue().equals("=")) {
                System.out.println("=");
                currentToken++;
            } else {
                System.out.println();
            }
            
            RULE_E();
        }
    }

    // E: A ((+ | -) A)*
    private void RULE_E() {
        System.out.println("------- RULE_E");
        RULE_A();
        
        while (tokens.get(currentToken).getValue().equals("+") ||
               tokens.get(currentToken).getValue().equals("-")) {
            
            System.out.println("------- " + tokens.get(currentToken).getValue());
            currentToken++;
            RULE_A();
        }
    }

    // A: B ((* | / | %) B)*
    private void RULE_A() {
        System.out.println("-------- RULE_A");
        RULE_B();
        
        while (tokens.get(currentToken).getValue().equals("*") ||
               tokens.get(currentToken).getValue().equals("/") ||
               tokens.get(currentToken).getValue().equals("%")) {
            
            System.out.println("-------- " + tokens.get(currentToken).getValue());
            currentToken++;
            RULE_B();
        }
    }

    // B: (! | - | +)* C
    private void RULE_B() {
        System.out.println("--------- RULE_B");
        while (tokens.get(currentToken).getValue().equals("!") ||
               tokens.get(currentToken).getValue().equals("-") ||
               tokens.get(currentToken).getValue().equals("+")) {
            
            System.out.println("--------- " + tokens.get(currentToken).getValue());
            currentToken++;
        }
        
        RULE_C();
    }

    // C: LITERAL | IDENTIFIER | (EXPRESSION) | CALL_METHOD_VALUE
    private void RULE_C() {
        System.out.println("---------- RULE_C");
        
        if (tokens.get(currentToken).getType().equals("INTEGER") ||
            tokens.get(currentToken).getType().equals("FLOAT") ||
            tokens.get(currentToken).getType().equals("CHAR") ||
            tokens.get(currentToken).getType().equals("STRING") ||
            tokens.get(currentToken).getValue().equals("true") ||
            tokens.get(currentToken).getValue().equals("false")) {
            
            System.out.println("---------- LITERAL: " + tokens.get(currentToken).getValue());
            currentToken++;
        }
        else if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
            String nextValue = tokens.get(currentToken + 1).getValue();
            if (nextValue.equals("(")) {
                // Llamada a método como valor
                RULE_CALL_METHOD_VALUE();
            } else {
                System.out.println("---------- IDENTIFIER: " + tokens.get(currentToken).getValue());
                currentToken++;
            }
        }
        else if (tokens.get(currentToken).getValue().equals("(")) {
            System.out.println("---------- (");
            currentToken++;
            RULE_EXPRESSION();
            
            if (tokens.get(currentToken).getValue().equals(")")) {
                System.out.println("---------- )");
                currentToken++;
            } else {
                error(20);
            }
        }
        else {
            error(21);
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
                error(22);
            }
        } else {
            error(23);
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
                error(24);
            }
        } else {
            error(25);
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
                error(26);
            }
        } else {
            // Una sola línea de código sin llaves
            if (isType(tokens.get(currentToken))) {
                RULE_VARIABLE();
            } else if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
                String nextValue = tokens.get(currentToken + 1).getValue();
                if (nextValue.equals("(")) {
                    RULE_CALL_METHOD();
                } else if (nextValue.equals("=") || nextValue.equals("+=") || nextValue.equals("-=") || 
                           nextValue.equals("*=") || nextValue.equals("/=")) {
                    RULE_ASSIGNMENT();
                } else {
                    RULE_EXPRESSION();
                    if (tokens.get(currentToken).getValue().equals(";")) {
                        currentToken++;
                        System.out.println("--- ;");
                    } else {
                        error(27);
                    }
                }
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
        
        // Procesamiento opcional de 'else'
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
                    error(29);
                }
            } else if (tokens.get(currentToken).getValue().equals("if")) {
                // Else if
                RULE_IF();
            } else {
                // Una sola línea de código sin llaves para else
                if (isType(tokens.get(currentToken))) {
                    RULE_VARIABLE();
                } else if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
                    String nextValue = tokens.get(currentToken + 1).getValue();
                    if (nextValue.equals("(")) {
                        RULE_CALL_METHOD();
                    } else if (nextValue.equals("=") || nextValue.equals("+=") || nextValue.equals("-=") || 
                               nextValue.equals("*=") || nextValue.equals("/=")) {
                        RULE_ASSIGNMENT();
                    } else {
                        RULE_EXPRESSION();
                        if (tokens.get(currentToken).getValue().equals(";")) {
                            currentToken++;
                            System.out.println("--- ;");
                        } else {
                            error(30);
                        }
                    }
                } else {
                    RULE_EXPRESSION();
                    if (tokens.get(currentToken).getValue().equals(";")) {
                        currentToken++;
                        System.out.println("--- ;");
                    } else {
                        error(31);
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
                error(32);
            }
        } else {
            error(33);
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
                error(34);
            }
        } else {
            // Una sola línea de código sin llaves
            if (isType(tokens.get(currentToken))) {
                RULE_VARIABLE();
            } else if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
                String nextValue = tokens.get(currentToken + 1).getValue();
                if (nextValue.equals("(")) {
                    RULE_CALL_METHOD();
                } else if (nextValue.equals("=") || nextValue.equals("+=") || nextValue.equals("-=") || 
                           nextValue.equals("*=") || nextValue.equals("/=")) {
                    RULE_ASSIGNMENT();
                } else {
                    RULE_EXPRESSION();
                    if (tokens.get(currentToken).getValue().equals(";")) {
                        currentToken++;
                        System.out.println("--- ;");
                    } else {
                        error(35);
                    }
                }
            } else {
                RULE_EXPRESSION();
                if (tokens.get(currentToken).getValue().equals(";")) {
                    currentToken++;
                    System.out.println("--- ;");
                } else {
                    error(36);
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
                error(37);
            }
        } else {
            // Una sola línea de código sin llaves
            if (isType(tokens.get(currentToken))) {
                RULE_VARIABLE();
            } else if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
                String nextValue = tokens.get(currentToken + 1).getValue();
                if (nextValue.equals("(")) {
                    RULE_CALL_METHOD();
                } else if (nextValue.equals("=") || nextValue.equals("+=") || nextValue.equals("-=") || 
                           nextValue.equals("*=") || nextValue.equals("/=")) {
                    RULE_ASSIGNMENT();
                } else {
                    RULE_EXPRESSION();
                    if (tokens.get(currentToken).getValue().equals(";")) {
                        currentToken++;
                        System.out.println("--- ;");
                    } else {
                        error(38);
                    }
                }
            } else {
                RULE_EXPRESSION();
                if (tokens.get(currentToken).getValue().equals(";")) {
                    currentToken++;
                    System.out.println("--- ;");
                } else {
                    error(39);
                }
            }
        }
        
        // Parte while
        if (tokens.get(currentToken).getValue().equals("while")) {
            currentToken++;
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
                    error(40);
                }
            } else {
                error(41);
            }
            
            // Punto y coma final
            if (tokens.get(currentToken).getValue().equals(";")) {
                currentToken++;
                System.out.println("--- ;");
            } else {
                error(42);
            }
        } else {
            error(43);
        }
    }

    // FOR statement
    private void RULE_FOR() {
        System.out.println("--- RULE_FOR");
        currentToken++; // Consumir 'for'
        System.out.println("--- for");
        
        // Paréntesis de apertura
        if (tokens.get(currentToken).getValue().equals("(")) {
            currentToken++;
            System.out.println("--- (");
            
            // Inicialización (opcional)
            if (!tokens.get(currentToken).getValue().equals(";")) {
                if (isType(tokens.get(currentToken))) {
                    RULE_VARIABLE();
                } else if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
                    RULE_ASSIGNMENT();
                } else {
                    RULE_EXPRESSION();
                    if (tokens.get(currentToken).getValue().equals(";")) {
                        currentToken++;
                        System.out.println("--- ;");
                    } else {
                        error(44);
                    }
                }
            } else {
                currentToken++; // Skip ;
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
                error(45);
            }
            
            // Incremento (opcional)
            if (!tokens.get(currentToken).getValue().equals(")")) {
                if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
                    String nextValue = tokens.get(currentToken + 1).getValue();
                    if (nextValue.equals("=") || nextValue.equals("+=") || nextValue.equals("-=") || 
                        nextValue.equals("*=") || nextValue.equals("/=")) {
                        RULE_ASSIGNMENT();
                        // Quitar el punto y coma del final
                        currentToken--;
                    } else {
                        RULE_EXPRESSION();
                    }
                } else {
                    RULE_EXPRESSION();
                }
            }
            
            // Paréntesis de cierre
            if (tokens.get(currentToken).getValue().equals(")")) {
                currentToken++;
                System.out.println("--- )");
            } else {
                error(46);
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
                    error(47);
                }
            } else {
                // Una sola línea de código sin llaves
                if (isType(tokens.get(currentToken))) {
                    RULE_VARIABLE();
                } else if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
                    String nextValue = tokens.get(currentToken + 1).getValue();
                    if (nextValue.equals("(")) {
                        RULE_CALL_METHOD();
                    } else if (nextValue.equals("=") || nextValue.equals("+=") || nextValue.equals("-=") || 
                               nextValue.equals("*=") || nextValue.equals("/=")) {
                        RULE_ASSIGNMENT();
                    } else {
                        RULE_EXPRESSION();
                        if (tokens.get(currentToken).getValue().equals(";")) {
                            currentToken++;
                            System.out.println("--- ;");
                        } else {
                            error(48);
                        }
                    }
                } else {
                    RULE_EXPRESSION();
                    if (tokens.get(currentToken).getValue().equals(";")) {
                        currentToken++;
                        System.out.println("--- ;");
                    } else {
                        error(49);
                    }
                }
            }
        } else {
            error(50);
        }
    }

    // SWITCH statement
    private void RULE_SWITCH() {
        System.out.println("--- RULE_SWITCH");
        currentToken++; // Consumir 'switch'
        System.out.println("--- switch");
        
        // Condición entre paréntesis
        if (tokens.get(currentToken).getValue().equals("(")) {
            currentToken++;
            System.out.println("--- (");
            RULE_EXPRESSION();
            if (tokens.get(currentToken).getValue().equals(")")) {
                currentToken++;
                System.out.println("--- )");
            } else {
                error(51);
            }
        } else {
            error(52);
        }
        
        // Bloque switch
        if (tokens.get(currentToken).getValue().equals("{")) {
            currentToken++;
            System.out.println("--- {");
            
            // Procesamiento de casos
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
                        error(53);
                    }
                } else if (tokens.get(currentToken).getValue().equals("default")) {
                    currentToken++;
                    System.out.println("--- default");
                    
                    if (tokens.get(currentToken).getValue().equals(":")) {
                        currentToken++;
                        System.out.println("--- :");
                    } else {
                        error(54);
                    }
                }
                
                // Procesar el cuerpo del caso
                while (!tokens.get(currentToken).getValue().equals("case") && 
                       !tokens.get(currentToken).getValue().equals("default") && 
                       !tokens.get(currentToken).getValue().equals("}")) {
                    
                    if (tokens.get(currentToken).getValue().equals("break")) {
                        currentToken++;
                        System.out.println("--- break");
                        
                        if (tokens.get(currentToken).getValue().equals(";")) {
                            currentToken++;
                            System.out.println("--- ;");
                            break;
                        } else {
                            error(55);
                        }
                    } else if (isType(tokens.get(currentToken))) {
                        RULE_VARIABLE();
                    } else if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
                        String nextValue = tokens.get(currentToken + 1).getValue();
                        if (nextValue.equals("(")) {
                            RULE_CALL_METHOD();
                        } else if (nextValue.equals("=") || nextValue.equals("+=") || nextValue.equals("-=") || 
                                  nextValue.equals("*=") || nextValue.equals("/=")) {
                            RULE_ASSIGNMENT();
                        } else {
                            RULE_EXPRESSION();
                            if (tokens.get(currentToken).getValue().equals(";")) {
                                currentToken++;
                                System.out.println("--- ;");
                            } else {
                                error(56);
                            }
                        }
                    } else {
                        RULE_EXPRESSION();
                        if (tokens.get(currentToken).getValue().equals(";")) {
                            currentToken++;
                            System.out.println("--- ;");
                        } else {
                            error(57);
                        }
                    }
                }
            }
            
            if (tokens.get(currentToken).
