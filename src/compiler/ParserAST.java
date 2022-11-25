package compiler;

import classes.*;
import exception.WrongTokenException;

public class ParserAST {

	private Scanner scanner;
	private Token currentToken;
	
	public ParserAST(Scanner s) {
		this.scanner = s;
		this.currentToken = scanner.scan();
	}
	
    public PROGRAM parseProgram() {
    	PROGRAM program = new PROGRAM();
        while (currentToken.check("FUNDEC")) {
        	program.addBlock(parseBlock());
        }
        return program;
    }

	public BLOCK parseBlock() {
        DECLARATION_LIST declarationList;
        accept("FUNDEC");
        TYPE_LIST typeList = parseTypeList();
        accept("FUNDEC");
        IDENTIFIER identifier = parseIdentifier();
        accept("IDENTIFIER");
        accept("LEFTROUND");
        if (!this.currentToken.check("RIGHTROUND")) {
            declarationList = parseDeclarationList();
        } else {
        	declarationList = new DECLARATION_LIST();
        }
        accept("RIGHTROUND");
        accept("LEFTGRAPH");
        COMMAND_LIST commandList = parseCommandList();
        accept("RETURN");
        EXPRESSION_LIST returnedValues = parseExpressionList();
        accept("ENDLINE");
        accept("RIGHTGRAPH");
        return new BLOCK(typeList, identifier, declarationList, commandList, returnedValues);
	}
	
    private COMMAND_LIST parseCommandList() {
        COMMAND_LIST commandList = new COMMAND_LIST();
        while (!currentToken.check("RETURN") && !currentToken.check("RIGHTGRAPH")) {
            commandList.addCommand(parseCommand());
        }
        return commandList;
    }

	private COMMAND parseCommand() {
		COMMAND command;
        if (currentToken.check("IF")) {
			command = parseSelectionStatement();
        }
        else if (currentToken.check("LOOP")) {
            command = parseRepetitionStatement();
        }
        else if (checkDeclaration()) {
            command = parseDeclarationStatement();
            accept("ENDLINE");
        }
        else if (checkAssignment()) {
            command = parseAssignmentStatement();
            accept("ENDLINE");
        }
        else {
            command = parseFunctionStatement();
            accept("ENDLINE");
        }
        return command;
    }
    
	private DECLARATION_LIST parseDeclarationList() {
		DECLARATION_LIST declarationList = new DECLARATION_LIST();
        declarationList.addDeclaration(parseDeclaration());
        while (currentToken.check("COLON")) {
			accept("COLON");
			declarationList.addDeclaration(parseDeclaration());
        }
        return declarationList;
	}

    private DECLARATION_STATEMENT parseDeclarationStatement() {
        return new DECLARATION_STATEMENT(parseDeclaration());
    }

    private FUNCTION_STATEMENT parseFunctionStatement() {
        return new FUNCTION_STATEMENT(parseFunction());
    }

    private DECLARATION parseDeclaration() {
        int size;
		TYPE type = parseType();
		DECLARATION declaration = new DECLARATION();
		IDENTIFIER identifier = parseIdentifier();
		String temp;
		accept("IDENTIFIER");
		if (currentToken.check("LEFTSQUARE")) {
			accept("LEFTSQUARE");
			temp = parseIntegerLiteral().spelling;
            if (temp.equals("TRUE") || temp.equals("FALSE")) {
                throw new RuntimeException("Wrong token: " + temp);
            }
			size = Integer.parseInt(temp);
			accept("INTEGERLITERAL");
            declaration.singleDeclarations.add(new SINGLE_DECLARATION(type, identifier, size));
			accept("RIGHTSQUARE");
		}
		else {
		    declaration.singleDeclarations.add(new SINGLE_DECLARATION(type, identifier, 1));
        }
		while (currentToken.check("COMMA")) {
			accept("COMMA");
			identifier = parseIdentifier();
			accept("IDENTIFIER");
            if (currentToken.check("LEFTSQUARE")) {
			    accept("LEFTSQUARE");
			    temp = parseIntegerLiteral().spelling;
                if (temp.equals("TRUE") || temp.equals("FALSE")) {
                    throw new RuntimeException("Wrong token: " + temp);
                }
			    size = Integer.parseInt(temp);
			    accept("INTEGERLITERAL");
                declaration.singleDeclarations.add(new SINGLE_DECLARATION(type, identifier, size ));
			    accept("RIGHTSQUARE");
		    }
		    else {
		        declaration.singleDeclarations.add(new SINGLE_DECLARATION(type, identifier, 1));
            }
		}
		return declaration;
	}

	private EXPRESSION_LIST parseExpressionList() {
		EXPRESSION_LIST expressionList = new EXPRESSION_LIST();
        expressionList.addValue(parseExpression());
        while (currentToken.check("COMMA")) {
			accept("COMMA");
            expressionList.addValue(parseExpression());
        }
        return expressionList;
	}

    private ASSIGNMENT parseAssignmentStatement() {
        IDENTIFIER_LIST identifiers = parseIdentifierList();
        accept("LEFTARROW");
        EXPRESSION_LIST expressions = parseExpressionList();
        return new ASSIGNMENT(identifiers, expressions);
    }

    private IDENTIFIER_LIST parseIdentifierList() {
		IDENTIFIER_LIST identifierList = new IDENTIFIER_LIST();
		identifierList.addItem(parseIdentifierItem());
        while (currentToken.check("COMMA")) {
            accept("COMMA");
            identifierList.addItem(parseIdentifierItem());
        }
        return identifierList;
    }

    private IDENTIFIER_ITEM parseIdentifierItem() {
        IDENTIFIER identifier = parseIdentifier();
        accept("IDENTIFIER");
        if (currentToken.check("LEFTSQUARE")) {
            accept("LEFTSQUARE");
            ARRAY_IDENTIFIER arrayIdentifier = parseArrayIdentifier(identifier, parseExpression());
            accept("RIGHTSQUARE");
            return arrayIdentifier;
        }
        return identifier;
    }

    private IDENTIFIER parseIdentifier() {
    	return new IDENTIFIER(currentToken.getSpelling());
    }
    
    private INTEGERLITERAL parseIntegerLiteral() {
    	return new INTEGERLITERAL(currentToken.getSpelling());
    }
    
    private OPERATOR parseOperator() {
    	return new OPERATOR(currentToken.getSpelling());
    }
    
    private ARRAY_IDENTIFIER parseArrayIdentifier(IDENTIFIER identifier, EXPRESSION expression) {
    	return new ARRAY_IDENTIFIER(identifier, expression);
    }
    
    private EXPRESSION parseExpression() {
		EXPRESSION quaternary = parseQuaternary();
        if (currentToken.isQuaternaryOP()) {
			OPERATOR operator = parseOperator();
			accept("OPERATOR");
            EXPRESSION expression = parseExpression();
            return new BINARY(quaternary, operator, expression);
        }
        return quaternary;
    }

    private EXPRESSION parseQuaternary() {
		EXPRESSION tertiary = parseTertiary();
        if (currentToken.isTertiaryOP()) {
			OPERATOR operator = parseOperator();
            accept("OPERATOR");
            EXPRESSION quaternary = parseQuaternary();
            return new BINARY(tertiary, operator, quaternary);
        }
        return tertiary;
    }

    private EXPRESSION parseTertiary() {
		EXPRESSION secondary = parseSecondary();
        if (currentToken.isSecondaryOP()) {
			OPERATOR operator = parseOperator();
            accept("OPERATOR");
            EXPRESSION tertiary = parseTertiary();
            return new BINARY(secondary, operator, tertiary);
        }
        return secondary;
    }

    private EXPRESSION parseSecondary() {
		EXPRESSION primary = parsePrimary();
        if (currentToken.isPrimaryOP()) {
			OPERATOR operator = parseOperator();
            accept("OPERATOR");
            EXPRESSION secondary = parseSecondary();
            return new BINARY(primary, operator, secondary);
        }
        return primary;
    }

    private EXPRESSION parsePrimary() {
        if (currentToken.check("IDENTIFIER")) {
			return new VAR_EXPRESSION(parseIdentifierItem());
        }
        else if (currentToken.check("INTEGERLITERAL")) {
			INTLIT_EXPRESSION integerLiteral = new INTLIT_EXPRESSION(parseIntegerLiteral());
            accept("INTEGERLITERAL");
            return integerLiteral;
        }
        else if (currentToken.check("LEFTROUND")) {
			accept("LEFTROUND");
			EXPRESSION expression = parseExpression();
			accept("RIGHTROUND");
			return expression;
        }
        else {
            return new CALL_EXPRESSION(parseFunction());
        }
    }

	private SELECTION_STATEMENT parseSelectionStatement() {
        accept("IF");
        accept("DOLLAR");
        EXPRESSION condition = parseExpression();
        accept("DOLLAR");
        accept("LEFTGRAPH");
        COMMAND_LIST ifCommands = parseCommandList();
        accept("RIGHTGRAPH");
        COMMAND_LIST elseCommands = null;
        if (this.currentToken.check("ELSE")) {
			accept("ELSE");
			accept("LEFTGRAPH");
			elseCommands = parseCommandList();
            accept("RIGHTGRAPH");
        }
        return new SELECTION_STATEMENT (condition, ifCommands, elseCommands);
    }

    private REPETITION_STATEMENT parseRepetitionStatement() {
		accept("LOOP");
		accept("DOLLAR");
		EXPRESSION condition = new INTLIT_EXPRESSION(new INTEGERLITERAL("TRUE"));
        if(!this.currentToken.check("DOLLAR")) {
            condition = parseExpression();
        }
        accept("DOLLAR");
        accept("LEFTGRAPH");
        COMMAND_LIST commands = parseCommandList();
        accept("RIGHTGRAPH");
        return new REPETITION_STATEMENT(condition, commands);
    }

    private FUNCTION parseFunction() {
        String[] functions = { "READBOOL","WRITEBOOL", "READCHAR", "WRITECHAR", "READINT", "WRITEINT", "IDENTIFIER" };
        accept("FUNDEC");
        IDENTIFIER identifier = parseIdentifier();
        EXPRESSION_LIST expressionList = null;
        for (String item : functions) {
			if (currentToken.check(item)) {
			    accept(item);
                accept("LEFTROUND");
                if (!currentToken.check("RIGHTROUND")) {
					expressionList = parseExpressionList();
                }
                accept("RIGHTROUND");
			}
        }
        return new FUNCTION(identifier, expressionList);
    }

	private TYPE_LIST parseTypeList() {
		TYPE_LIST typeList = new TYPE_LIST();
		typeList.addType(parseType());
        while (currentToken.check("COMMA")) {
            accept("COMMA");
            typeList.addType(parseType());
        }
        return typeList;
	}
	
    private TYPE parseType() {
		String spelling = currentToken.getSpelling();
        if (currentToken.check("INT")) {
            accept("INT");
        }
        else {
			accept("BOOL");
        }
        return new TYPE(spelling);
    }

	private void accept(String s){
		if (currentToken.check(s)){
			System.out.println(currentToken.spelling+" "+currentToken.type);
			this.currentToken = this.scanner.scan();
		}
		else {
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