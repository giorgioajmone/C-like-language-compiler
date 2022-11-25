package compiler;

import exception.WrongTokenException;

public class Parser {

	private Scanner scanner;
	private Token currentToken;
	
	public Parser(Scanner s) {
		this.scanner = s;
		this.currentToken = scanner.scan();
	}
	
    public void parseProgram() {
        while (currentToken.check("FUNDEC")) {
            parseBlock();
        }
    }

	public void parseBlock() {
        accept("FUNDEC");
        parseTypeList();
        accept("FUNDEC");
        accept("IDENTIFIER");
        accept("LEFTROUND");
        if (!this.currentToken.check("RIGHTROUND")) {
            parseDeclarationList();
        }
        accept("RIGHTROUND");
        accept("LEFTGRAPH");
        while (!this.currentToken.check("RIGHTGRAPH")) {
            parseCommandList();
        }
        accept("RIGHTGRAPH");
	}
	
    private void parseCommandList() {
        while (!currentToken.check("RIGHTGRAPH")) {
            parseCommand();
        }
    }

	private void parseCommand() {
        if (currentToken.check("IF")) {
            parseSelection();
        }
        else if (currentToken.check("FOR")) {
            parseRepetition();
        }
        else if (checkDeclaration()) {
            parseDeclaration();
            accept("ENDLINE");
        }
        else if (currentToken.check("RETURN")) {
        	accept("RETURN");
        	parseExpressionList();
        	accept("ENDLINE");
        }
        else if (checkAssignment()) {
            parseAssignment();
            accept("ENDLINE");
        }
        else {
            parseFunction();
            accept("ENDLINE");
        }
    }
    
	private void parseDeclarationList() {
        parseDeclaration();
        while (currentToken.check("COLON")) {
			accept("COLON");
			parseDeclaration();
        }
	}

    private void parseDeclaration() {
		parseType();
		accept("IDENTIFIER");
		if (currentToken.check("LEFTSQUARE")) {
			accept("LEFTSQUARE");
			accept("INTEGERLITERAL");
			accept("RIGHTSQUARE");
		}
		while (currentToken.check("COMMA")) {
			accept("COMMA");
			accept("IDENTIFIER");
            if (currentToken.check("LEFTSQUARE")) {
                accept("LEFTSQUARE");
                accept("INTEGERLITERAL");
                accept("RIGHTSQUARE");
            }
		}
	}

	private void parseExpressionList() {
        if (currentToken.check("LEFTGRAPH")) {
            accept("LEFTGRAPH");
            parseExpressionList();
            accept("RIGHTGRAPH");
        }
        else {
            parseExpression();
        }
        while (currentToken.check("COMMA")) {
			accept("COMMA");
            if (currentToken.check("LEFTGRAPH")) {
                accept("LEFTGRAPH");
                parseExpressionList();
                accept("RIGHTGRAPH");
            }
            else {
                parseExpression();
            }
        }
	}

    private void parseAssignment() {
        parseIdentifierList();
        accept("LEFTARROW");
        parseExpressionList();
    }

    private void parseIdentifierList() {
        parseIdentifierItem();
        while (currentToken.check("COMMA")) {
            accept("COMMA");
            parseIdentifierItem();
        }
    }

    private void parseIdentifierItem() {
        accept("IDENTIFIER");
        if (currentToken.check("LEFTSQUARE")) {
            accept("LEFTSQUARE");
            parseExpression();
            accept("RIGHTSQUARE");
        }
    }

    private void parseExpression() {
		parseQuaternary();
        if (currentToken.isQuaternaryOP()) {
			accept("OPERATOR");
            parseExpression();
        }
    }

    private void parseQuaternary() {
		parseTertiary();
        if (currentToken.isTertiaryOP()) {
			accept("OPERATOR");
            parseQuaternary();
        }
    }

    private void parseTertiary() {
		parseSecondary();
        if (currentToken.isSecondaryOP()) {
			accept("OPERATOR");
            parseTertiary();
        }
    }

    private void parseSecondary() {
		parsePrimary();
        if (currentToken.isPrimaryOP()) {
			accept("OPERATOR");
            parseSecondary();
        }
    }

    private void parsePrimary() {
        if (currentToken.check("IDENTIFIER")) {
            parseIdentifierItem();
        }
        else if (currentToken.check("INTEGERLITERAL")) {
            accept("INTEGERLITERAL");
        }
        else if (currentToken.check("LEFTROUND")) {
			accept("LEFTROUND");
			parseExpression();
			accept("RIGHTROUND");
        }
        else {
            parseFunction();
        }
    }

	private void parseSelection() {
        accept("IF");
        accept("DOLLAR");
        parseExpression();
        accept("DOLLAR");
        accept("LEFTGRAPH");
        parseCommandList();
        accept("RIGHTGRAPH");
        if (this.currentToken.check("ELSE")) {
			accept("ELSE");
			accept("LEFTGRAPH");
			parseCommandList();
            accept("RIGHTGRAPH");
        }
    }

    private void parseRepetition() {
		accept("FOR");
		accept("DOLLAR");
		if (!this.currentToken.check("COLON")) {
            parseAssignment();
        }
        accept("COLON");
        if(!this.currentToken.check("COLON")) {
            parseExpression();
        }
        accept("COLON");
        if(!this.currentToken.check("DOLLAR")) {
            parseAssignment();
        }
        accept("DOLLAR");
        accept("LEFTGRAPH");
        parseCommandList();
        accept("RIGHTGRAPH");
    }

    private void parseFunction() {
        String[] functions = { "READBOOL","WRITEBOOL", "READCHAR", "WRITECHAR", "READINT", "WRITEINT", "IDENTIFIER" };
        accept("FUNDEC");
        for (String item : functions) {
			if (currentToken.check(item)) {
			    accept(item);
                accept("LEFTROUND");
                if (!currentToken.check("RIGHTROUND")) {
					parseExpressionList();
                }
                accept("RIGHTROUND");
			}
        }
    }

	private void parseTypeList() {
        parseType();
        while (currentToken.check("COMMA")) {
            accept("COMMA");
            parseType();
        }
	}
	
    private void parseType() {
        if (currentToken.check("INT")) {
            accept("INT");
        }
        else {
			accept("BOOL");
        }
    }

	private void accept(String s){
		
		if (currentToken.check(s)){
			System.out.println(currentToken.spelling+" "+currentToken.type);
			this.currentToken = this.scanner.scan();
		}else {
			throw new WrongTokenException("ERROR. Expected Token: " + s + ". Current token: "+ currentToken.spelling);
		}		
	}

	private boolean checkDeclaration() {
        return (currentToken.check("INT") || currentToken.check("BOOL"));
	}

    private boolean checkAssignment() {
        return (currentToken.check("IDENTIFIER"));
    }
}