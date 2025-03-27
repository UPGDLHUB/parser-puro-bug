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

	// PROGRAM: { declarations methods }
	private void RULE_PROGRAM() {
		System.out.println("- RULE_PROGRAM");
		if (tokens.get(currentToken).getValue().equals("{")) {
			currentToken++;
			System.out.println("- {");
		} else {
			error(1);
		}
		RULE_DECLARATIONS();
		RULE_METHODS();
		if (tokens.get(currentToken).getValue().equals("}")) {
			currentToken++;
			System.out.println("- }");
		} else {
			error(2);
		}
	}

	// DECLARATIONS: (type identifier ;)*
	private void RULE_DECLARATIONS() {
		System.out.println("-- RULE_DECLARATIONS");
		while (isType(tokens.get(currentToken))) {
			RULE_TYPES();
			if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
				System.out.println("-- IDENTIFIER");
				currentToken++;
			} else {
				error(6);
			}
			if (tokens.get(currentToken).getValue().equals(";")) {
				currentToken++;
				System.out.println("-- ;");
			} else {
				error(7);
			}
		}
	}

	// METHODS: (type identifier ( PARAMS ) { BODY })*
	private void RULE_METHODS() {
		System.out.println("-- RULE_METHODS");
		while (isType(tokens.get(currentToken))) {
			RULE_TYPES();
			if (tokens.get(currentToken).getType().equals("IDENTIFIER")) {
				currentToken++;
				System.out.println("-- IDENTIFIER");
			} else {
				error(8);
			}
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

	// TYPES: int | float | char | boolean
	private void RULE_TYPES() {
		System.out.println("--- RULE_TYPES");
		if (isType(tokens.get(currentToken))) {
			System.out.println("--- " + tokens.get(currentToken).getValue());
			currentToken++;
		} else {
			error(13);
		}
	}

	private boolean isType(TheToken token) {
		return token.getValue().equals("int") || token.getValue().equals("float") ||
				token.getValue().equals("char") || token.getValue().equals("boolean");
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

	private void error(int error) {
		System.out.println("Error " + error + " at line " + tokens.get(currentToken));
		System.exit(1);
	}
} 
