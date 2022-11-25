package compiler;

import classes.*;

public class Checker implements Visitor {

    private IdentificationTable idTable = new IdentificationTable();
    
    public void check( PROGRAM p )
	{
		p.visit( this, null );
	}
	
    
    public Object visitProgram(PROGRAM program, Object arg){
        initializeStandardEnvironment();
        int n = program.blocks.size();
        idTable.openScope();
        for (int i = 0; i < n; i++) {
            program.blocks.get(i).visit(this, arg);
        }
        idTable.closeScope();
        
        return null;
    }
    
    private void initializeStandardEnvironment() {
        idTable.enter("readBool", createFunction("readBool", null, "bool"));
        idTable.enter("writeBool", createFunction("writeBool", "bool", "bool"));
        idTable.enter("readChar", createFunction("readChar", null, "int"));
        idTable.enter("writeChar", createFunction("writeChar", "int", "bool"));
        idTable.enter("readInt", createFunction("readInt", null, "int"));
        idTable.enter("writeInt", createFunction("writeInt", "int", "bool"));
    }

    private BLOCK createFunction(String functionName, String paramTypeSpelling, String returnTypeSpelling) {
        TYPE_LIST typeDec = new TYPE_LIST();
        typeDec.addType(new TYPE(returnTypeSpelling));

        IDENTIFIER funcName = new IDENTIFIER(functionName);

        DECLARATION_LIST paramDec = new DECLARATION_LIST();
        DECLARATION declaration = new DECLARATION();
        if(paramTypeSpelling != null){
            declaration.singleDeclarations.add(new SINGLE_DECLARATION(new TYPE(paramTypeSpelling),new IDENTIFIER("cheers"), 1));
            paramDec.addDeclaration(declaration);
        }

        return new BLOCK(typeDec, funcName, paramDec, null, null);
    }

    public Object visitBlock(BLOCK block, Object arg){

        String id = (String)block.FuncName.visit(this, arg);
        idTable.enter(id, block);
        idTable.openScope();
        
        block.TypeDec.visit(this, arg);
        block.ParamDec.visit(this, arg);
        block.Commands.visit(this, arg);
        block.ReturnedValues.visit(this, arg);        

        if (block.TypeDec.types.size() == block.ReturnedValues.size) {
            for (int i = 0, h = 0; i < block.TypeDec.types.size(); i++, h++) {
                if ( block.ReturnedValues.values.get(h) instanceof CALL_EXPRESSION ){
                    CALL_EXPRESSION item = (CALL_EXPRESSION) block.ReturnedValues.values.get(h);
                    for (int j = 0; j < item.function.declaration.TypeDec.types.size(); j++) {
                        if (! (item.function.declaration.TypeDec.types.get(j).equals(block.TypeDec.types.get(i)))) {
                        	throw new RuntimeException("Type mismatch");
                        }
                        i++;
                    }
                    i--;
                }
                if(! (block.ReturnedValues.values.get(h).type.spelling.equals( block.TypeDec.types.get(i).spelling))) {
                    throw new RuntimeException("Type mismatch");
                }
            }
        }
        else {
            throw new RuntimeException("Type mismatch");
        }
        
        idTable.closeScope();
        return null;
    }
    
    public Object visitCommandList(COMMAND_LIST commandList, Object arg){
    	for(int i = 0; i < commandList.commands.size(); i++) {
    		commandList.commands.get(i).visit( this, arg );
    	}
		return null;
    }
   
    public Object visitCommand(COMMAND command, Object arg){
        return null;
    }
    
    public Object visitDeclarationList(DECLARATION_LIST declarationList, Object arg){
    	for(int i = 0; i < declarationList.declarations.size(); i++) {
    		declarationList.declarations.get(i).visit( this, arg );
    	}
			
		return null;
    }
    
    public Object visitDeclarationStatement(DECLARATION_STATEMENT declarationStatement, Object arg){
        declarationStatement.declaration.visit(this, arg);
        
        return null;
    }
    
    
    public Object visitFunctionStatement(FUNCTION_STATEMENT functionStatement, Object arg){
    	functionStatement.function.visit(this, arg);
    
    	return null;
    }
    
    public Object visitDeclaration(DECLARATION declaration, Object arg){
        for(int i = 0; i < declaration.singleDeclarations.size();i++){      
    	    declaration.singleDeclarations.get(i).visit(this, arg);
        }
		return null;
    }

    public Object visitExpressionList(EXPRESSION_LIST expressionList, Object arg){
        int size = 0;
    	for(int i = 0; i < expressionList.values.size(); i++) {
    		expressionList.values.get(i).visit( this, arg );
            size += expressionList.values.get(i).size;
    	}
		expressionList.size = size;
		return null;
    }
    
    public Object visitAssignment(ASSIGNMENT assignment, Object arg){
        assignment.Variables.visit(this, arg);
        assignment.Values.visit(this, arg);
        
        if (assignment.Variables.items.size() == assignment.Values.size) {
            for (int i = 0, h = 0; i < assignment.Variables.items.size(); i++, h++) {
                if (assignment.Values.values.get(h) instanceof CALL_EXPRESSION ){
                    CALL_EXPRESSION item = (CALL_EXPRESSION) assignment.Values.values.get(h);
                    for (int j = 0; j < item.function.declaration.TypeDec.types.size(); j++) {
                        if (! (item.function.declaration.TypeDec.types.get(j).spelling.equals( assignment.Variables.items.get(i).declaration.type.spelling))) {
                        	throw new RuntimeException("Type mismatch");
                        }
                        i++;
                    }
                    i--;
                }
                if(!(assignment.Values.values.get(h).type.spelling.equals(assignment.Variables.items.get(i).declaration.type.spelling))) {
                    throw new RuntimeException("Type mismatch");
                }
            }
        }
        else {
            throw new RuntimeException("Type mismatch");
        }
        //idTable.closeScope();
        return null;
    	
    }
    
    public Object visitIdentifierList(IDENTIFIER_LIST identifierList, Object arg){
        for (int i = 0; i < identifierList.items.size(); i++) {
            identifierList.items.get(i).visitForRetrieve(this, arg);
        }
        return null;
    }
    
    public Object visitIdentifier(IDENTIFIER identifier, Object arg){
        
        return identifier.spelling;
    }
 
    public Object visitIntegerLiteral(INTEGERLITERAL integerLiteral, Object arg){
    	return integerLiteral.spelling;
    }
    
    public Object visitOperator(OPERATOR operator, Object arg){
    	return operator.spelling;
    }
    
    public Object visitArrayIdentifier(ARRAY_IDENTIFIER arrayIdentifier, Object arg){
        return arrayIdentifier.spelling;
    }
    
    public Object visitExpression(EXPRESSION expression, Object arg){
    	return null;
    }

    public Object visitSelectionStatement(SELECTION_STATEMENT selectionStatement, Object arg){
    	
        selectionStatement.Condition.visit(this, arg);

        idTable.openScope();
    	selectionStatement.Commands.visit(this, arg);
        idTable.closeScope();

    	if(selectionStatement.Else_Commands != null) {
            idTable.openScope();
    		selectionStatement.Else_Commands.visit(this, arg);
            idTable.closeScope();
    	}
    	
    	return null;
    }

    public Object visitRepetitionStatement(REPETITION_STATEMENT repetitionStatement, Object arg){
    	repetitionStatement.Condition.visit(this, arg);

        idTable.openScope();
    	repetitionStatement.Commands.visit(this, arg);
        idTable.closeScope();
    	
    	return null;
    }

    public Object visitTypeList(TYPE_LIST typeList, Object arg){
    	for (int i = 0; i < typeList.types.size(); i++) {
            typeList.types.get(i).visit(this, arg);
        }
        return null;
    }

    public Object visitType(TYPE type, Object arg){
    	return type.spelling;
    }

    public Object visitCallExpression(CALL_EXPRESSION callExpression, Object arg){
    	callExpression.function.visit(this, arg);
    	callExpression.size = callExpression.function.declaration.TypeDec.types.size();
        callExpression.type = callExpression.function.declaration.TypeDec.types.get(0);
    	return null;
    }

    public Object visitBinary(BINARY binary, Object arg){
    	binary.OperandOne.visit(this, arg);
    	binary.Operator.visit(this, arg);
    	binary.OperandTwo.visit(this, arg);
    	
    	
        if (!(binary.OperandOne.type.spelling.equals( binary.OperandTwo.type.spelling))) {
            throw new RuntimeException("Type mismatch");
        }
        if (binary.OperandOne.size > 1 || binary.OperandTwo.size > 1) {
            throw new RuntimeException("Size not accepted");
        }
        binary.size = 1;
        if (binary.OperandOne.type.visit(this, arg).equals("int")){
            switch ((String)binary.Operator.visit(this, arg)) {
                case "&&": case "||": case "##": case "!&": case "!|": case "!#":
                    throw new RuntimeException("Operator not allowed");
                case ">=": case "<=": case "<<": case ">>": case "==": case "<>":
                    binary.type = new TYPE("bool");
                    break;
                default:
                    binary.type = new TYPE("int");
            }
        }
        else { 
            switch ((String)binary.Operator.visit(this, arg)) {
                case "++": case "--": case "**": case "//": case "%%": case "^^": 
                case ">=": case "<=": case "<<": case ">>": case "==": case "<>":
                    throw new RuntimeException("Operator not allowed");
                default:
                    binary.type = new TYPE("bool");
            }
        }
        return null;
    }

    public Object visitIntLitExpression(INTLIT_EXPRESSION intLitExpression, Object arg){
    	intLitExpression.value.visit(this, arg);
    	intLitExpression.size = 1;
        if(intLitExpression.value.visit(this, arg).equals("TRUE") || intLitExpression.value.visit(this, arg).equals("FALSE")) {
        	intLitExpression.type = new TYPE("bool");
        }
    	else {
            intLitExpression.type = new TYPE("int");
        }
        return null;
    }

    public Object visitFunction(FUNCTION function, Object arg){
    	String id = (String)function.name.visit(this, arg);
        GLOBAL_DECLARATION declaration = idTable.retrieve(id);
        

        if( declaration == null )
			System.out.println( id + " is not declared" );
		else if( !( declaration instanceof BLOCK ) ) 
			System.out.println( id + " is not a function" );
		else {
			function.declaration = (BLOCK) declaration;
			function.args.visit(this, arg);
							
            if( function.declaration.ParamDec.declarations.size() == function.args.size) {
				for (int i = 0, h = 0; i < function.declaration.ParamDec.declarations.size(); i++, h++) {
                    if (function.args.values.get(h) instanceof CALL_EXPRESSION ){
                        CALL_EXPRESSION item = (CALL_EXPRESSION) function.args.values.get(h);
                        for (int j = 0; j < item.function.declaration.ParamDec.declarations.size(); j++) {
                            if (! (item.function.declaration.ParamDec.declarations.get(j).singleDeclarations.get(0).type.spelling.equals( function.declaration.ParamDec.declarations.get(i).singleDeclarations.get(0).type.spelling))) {
                        	    throw new RuntimeException("Type mismatch");
                            }
                            i++;
                        }
                        i--;
                    }
                    if(! (function.args.values.get(h).type.spelling.equals(function.declaration.ParamDec.declarations.get(i).singleDeclarations.get(0).type.spelling))) {
                        throw new RuntimeException("Type mismatch");
                    }
                }
            }
            else {
                throw new RuntimeException("Type mismatch");
            }
		}
		return null;
    }

    public Object visitVarExpression(VAR_EXPRESSION varExpression, Object arg){
    	varExpression.variable.visitForRetrieve(this, arg);
    	varExpression.size = 1;
        varExpression.type = varExpression.variable.declaration.type;
        System.out.println(varExpression.type.spelling);
    	return null;
    }

    public Object visitSingleDeclaration(SINGLE_DECLARATION singleDeclaration, Object arg) {
        String id = (String)singleDeclaration.name.visit(this, arg);
        idTable.enter(id, singleDeclaration);
    	return null;
    }

    public Object visitIdentifierItem(IDENTIFIER_ITEM identifierItem, Object arg) {
        String id = (String)identifierItem.visit(this, arg);
        GLOBAL_DECLARATION declaration = idTable.retrieve(id);
		if( declaration == null )
			System.out.println( id + " is not declared" );
		else if( !( declaration instanceof SINGLE_DECLARATION ) ) 
			System.out.println( id + " is not a variable" );
		else
			identifierItem.declaration = (SINGLE_DECLARATION)declaration;
        return null;
    }


}
