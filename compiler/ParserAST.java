package compiler;

import classes.*;
import exception.WrongTokenException;

public class ParserAST {

	private final Scanner scanner;
	private Token currentToken;
	
	public ParserAST(Scanner s) {
		this.scanner = s;
		this.currentToken = scanner.scan(); // the first token is read
        // even here, the constructor does not execute any of the parsing operations. It just initializes the scanner and the current token. Parsing is explicitly asked by Test afterwards
	}

    // Roughly for each of the grammar syntax rules, a Java class has been created, and it is a child of the AST class. Relationships among production rules are expressed in terms of inheritance.
    // Each of the following methods returns an AST object, which is the root of the subtree that represents the syntax rule that has been parsed.
    // To understand the class meaning, please have a look at both grammar rules and glossary.
    public PROGRAM parseProgram() {
        // a program is made up of more functions (blocks). It is actually the entire source code.
    	PROGRAM program = new PROGRAM();
        while (currentToken.check("FUNDEC")) {
        	program.addBlock(parseBlock());
        }
        return program;
    }

	public BLOCK parseBlock() {
        /*The structure of a single block is:
        @ TypeList @ Identifier ( ( ε | DeclarationList ) ) { CommandList return ExpressionList ; } 
        */
        DECLARATION_LIST declarationList;
        accept("FUNDEC");  //check @
        TYPE_LIST typeList = parseTypeList();
        accept("FUNDEC"); //check @
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
        accept("RETURN"); ///check 'return'
        EXPRESSION_LIST returnedValues = parseExpressionList();
        accept("ENDLINE");
        accept("RIGHTGRAPH");
        return new BLOCK(typeList, identifier, declarationList, commandList, returnedValues);
	}
	
    // parses va list of commands. End of the list is either 'return' keyword or '}'
    private COMMAND_LIST parseCommandList() {
        COMMAND_LIST commandList = new COMMAND_LIST();
        while (!currentToken.check("RETURN") && !currentToken.check("RIGHTGRAPH")) {
            commandList.addCommand(parseCommand());
        }
        return commandList;
    }

    /*
    according to grammar, a command can be:

     if $ Expression $ { CommandList } ( ε | else { CommandList } )       -- 1
   | loop $ ( ε | Expression ) $ { CommandList }                          -- 2
   | Declaration ;                                                        -- 3
   | Assignment ;                                                         -- 4
   | Function ;                                                           -- 5

    */
	private COMMAND parseCommand() {
		COMMAND command;
        // 1
        if (currentToken.check("IF")) {                                   
			command = parseSelectionStatement();
        }
        // 2
        else if (currentToken.check("LOOP")) {
            command = parseRepetitionStatement();
        }
        // 3
        else if (checkDeclaration()) {
            command = parseDeclarationStatement();
            accept("ENDLINE");
        }
        // 4
        else if (checkAssignment()) {
            command = parseAssignmentStatement();
            accept("ENDLINE");
        }
        // 5
        else {
            command = parseFunctionStatement();
            accept("ENDLINE");
        }
        return command;
    }

    /*
    according to grammar:
    DeclarationList	 := Declaration ( : Declaration )*
    */
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

    /* According to grammar
    Declaration	 := Type Identifier ( ε | [ IntegerLiteral ] ) ( , Identifier ( ε | [ IntegerLiteral ] ) )*
    */
    private DECLARATION parseDeclaration() {
        int size;
		TYPE type = parseType();
		DECLARATION declaration = new DECLARATION();
		IDENTIFIER identifier = parseIdentifier();
		String temp;
		accept("IDENTIFIER");
		if (currentToken.check("LEFTSQUARE")) { // It is an array
			accept("LEFTSQUARE");
			temp = parseIntegerLiteral().spelling;
            /*
            since in our compiler 'TRUE' and 'FALSE' are handled as integerliteral, it's necessary
              to check if the size of the array is declared with a number in order to avoid 
              errors like ' int array [TRUE]'
            */
            if (temp.equals("TRUE") || temp.equals("FALSE")) {
                throw new RuntimeException("Wrong token: " + temp);
            }
			size = Integer.parseInt(temp);
			accept("INTEGERLITERAL");
            //declaration of array
            declaration.singleDeclarations.add(new SINGLE_DECLARATION(type, identifier, size));
			accept("RIGHTSQUARE");
		}
        // declaration of variable, which is coded as an array of size 1
		else {
		    declaration.singleDeclarations.add(new SINGLE_DECLARATION(type, identifier, 1));
        }
        // handling multiple declaration on a single line ( EX: int a,b[4],c )
		while (currentToken.check("COMMA")) {
			accept("COMMA");
			identifier = parseIdentifier();
			accept("IDENTIFIER");
            //array
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
            //variable
		    else {
		        declaration.singleDeclarations.add(new SINGLE_DECLARATION(type, identifier, 1));
            }
		}
		return declaration;
	}

    /*
    according to grammar:
    ExpressionList := Expression ( , Expression )*
     */
	private EXPRESSION_LIST parseExpressionList() {
		EXPRESSION_LIST expressionList = new EXPRESSION_LIST();
        expressionList.addValue(parseExpression());
        while (currentToken.check("COMMA")) {
			accept("COMMA");
            expressionList.addValue(parseExpression());
        }
        return expressionList;
	}

    /*
    according to grammar
    Assignment := IdentifierList <- ExpressionList
    */
    private ASSIGNMENT parseAssignmentStatement() {
        IDENTIFIER_LIST identifiers = parseIdentifierList();
        accept("LEFTARROW");
        EXPRESSION_LIST expressions = parseExpressionList();
        return new ASSIGNMENT(identifiers, expressions);
    }

    /*
    according to grammar
    IdentifierList	 := IdentifierItem ( , IdentifierItem )*
    */
    private IDENTIFIER_LIST parseIdentifierList() {
		IDENTIFIER_LIST identifierList = new IDENTIFIER_LIST();
		identifierList.addItem(parseIdentifierItem());
        while (currentToken.check("COMMA")) {
            accept("COMMA");
            identifierList.addItem(parseIdentifierItem());
        }
        return identifierList;
    }

    /*
    according to grammar
    IdentifierItem	 := Identifier ( ε | [ Expression ] )
    */
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

    // the following methods take care about operator precedence

    /*
    according to grammar
    Expression	 := Quaternary ( ε | OperatorQ Expression )
    */
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

    /*
    according to grammar
    Quaternary	 := Tertiary ( ε | OperatorT Quaternary )
    */
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

    /*
    according to grammar
    Tertiary		 := Secondary ( ε | OperatorS Tertiary )
    */
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

    /*
    according to grammar
    Secondary	 := Primary ( ε | OperatorP Secondary )
    */
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

    /*
    according to grammar
    Primary		 := IdentifierItem
		   | IntegerLiteral
		   | (Expression)
		   | Function 
    */
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

    /*
    according to grammar
    if $ Expression $ { CommandList } ( ε | else { CommandList } )
    */
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

    /*
    according to grammar
     loop $ ( ε | Expression ) $ { CommandList }
    */
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

    /*
    according to grammar
    Function := @ ( ReadBool | WriteBool | ReadChar | WriteChar | ReadInt | WriteInt | Identifier ) ( ( ε | ExpressionList ) )
    */
    private FUNCTION parseFunction() {
        // For simplicity, predefined functions are hardcoded in a string array, which also contains the IDENTIFIER keyword to handle the case of a generic non-standard function declared by the user
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

    /*
    according to grammar
    TypeList := Type ( , Type )*
    */
	private TYPE_LIST parseTypeList() {
		TYPE_LIST typeList = new TYPE_LIST();
		typeList.addType(parseType());
        while (currentToken.check("COMMA")) {
            accept("COMMA");
            typeList.addType(parseType());
        }
        return typeList;
	}

	/*
    according to grammar
    Type := int 
	      | bool
    */
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

    //call scanner and verify if spelling of current token is the one expected
	private void accept(String s){
		if (currentToken.check(s)){
            // for debugging purposes, tokens are printed to the console
			System.out.println(currentToken.spelling + " " + currentToken.type);
			this.currentToken = this.scanner.scan();
		}
		else {
			throw new WrongTokenException("ERROR. Expected Token: " + s + ". Current token: "+ currentToken.spelling);
		}
	}

    // checks if the declaration type is one of the predefined types
	private boolean checkDeclaration() {
        return (currentToken.check("INT") || currentToken.check("BOOL"));
	}

    // checks if the assignment is made on an identifier
    private boolean checkAssignment() {
        return (currentToken.check("IDENTIFIER"));
    }
}