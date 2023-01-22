package compiler;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import TAM.Instruction;
import TAM.Machine;
import classes.*;



public class Encoder implements Visitor {

	private int nextAddress = Machine.CB;
	private int mainAddress;
	private int variableDisplacement;
	
	public void saveTargetProgram( String fileName )
	{
		try {
			DataOutputStream out = new DataOutputStream( new FileOutputStream( fileName ) );
			
			for( int i = Machine.CB; i < nextAddress; ++i )
				Machine.code[i].write( out );
			                      
			out.close();
		} catch( Exception ex ) {
			ex.printStackTrace();
			System.out.println( "Trouble writing " + fileName );
		}
	}
	
	private void emit( int op, int n, int r, int d )
	{
		if( n > 255 ) {
			System.out.println( "Operand too long" );
			n = 255;
		}
		
		Instruction instr = new Instruction();
		instr.op = op;
		instr.n = n;
		instr.r = r;
		instr.d = d;
		
		if( nextAddress >= Machine.PB )
			System.out.println( "Program too large" );
		else
			Machine.code[nextAddress++] = instr;
	}
	
	private void patch( int address, int d )
	{
		Machine.code[address].d = d;
	}

	public void encode( PROGRAM program )
	{
		program.visit( this, null );
	}

	// The same strategy used inside the checker is repeated in the encoder. Hence, the AST is visited again.
	// For each function declared in the source code, a visit operation is performed.
	public Object visitProgram(PROGRAM program, Object arg) {
		emit( Machine.CALLop, 0, Machine.CB, 0 );
		
		int n = program.blocks.size();
        for (int i = 0; i < n; i++) {
            program.blocks.get(i).visit(this, arg );
        }

		patch(0, mainAddress);
		return null;
	}
	
	// Every time a function is called, a memory area must be allocated.
	// Each of the fields of the block is visited.
	public Object visitBlock(BLOCK block, Object arg) {
		block.InstructionAddress = new CODE_ADDRESS(0, nextAddress);
		if(block.FuncName.spelling.equals("main")){
			mainAddress = nextAddress;
		}	

		int size = ((Integer) block.ParamDec.visit( this,  new DATA_ADDRESS(Machine.LBr, 0)) ).intValue();
		block.ParamDec.visit( this, new DATA_ADDRESS( Machine.LBr, -size ) );

		emit( Machine.POPop, 0, 0, 2 * size );
		variableDisplacement = 0;
		emit(Machine.PUSHop, 0, 0, 1);
		block.Commands.visit( this, new DATA_ADDRESS( Machine.LBr, 4 ) );

		int length = ((Integer)block.TypeDec.visit(this, Boolean.valueOf(true) )).intValue(); 
		block.ReturnedValues.visit(this,Boolean.valueOf(true));
		
		if(!block.FuncName.spelling.equals("main")){
			emit( Machine.RETURNop, length, 0, size );
		}
		else{
			emit( Machine.HALTop, 0, 0, 0 );
		}
		
		return null;
	}

	public Object visitDeclarationList(DECLARATION_LIST declarationList, Object arg) {
		DATA_ADDRESS dataAddress = (DATA_ADDRESS) arg;
		int startDisplacement = dataAddress.Displacement;
		int size = 0;
		int n = declarationList.declarations.size();
        for (int i = 0; i < n; i++) {
            size += (int)declarationList.declarations.get(i).visit(this, new DATA_ADDRESS(dataAddress.Level, startDisplacement + size));
        }
		return Integer.valueOf(size);
	}
	
	// For each declaration, an amount of memory corresponding to the size of variable is allocated.
	public Object visitDeclaration(DECLARATION declaration, Object arg) {
		int startDisplacement = ((DATA_ADDRESS) arg).Displacement;
		int n = declaration.singleDeclarations.size();
		for (int i = 0; i < n; i++) {
			arg = declaration.singleDeclarations.get(i).visit(this, arg);
		}
		DATA_ADDRESS address = (DATA_ADDRESS) arg;
		int size = address.Displacement - startDisplacement;	
		emit( Machine.PUSHop, 0, 0, size );	
		variableDisplacement += size;
		return Integer.valueOf(size);
	}
	
	public Object visitSingleDeclaration(SINGLE_DECLARATION singleDeclaration, Object arg) {
		singleDeclaration.DataAddress = (DATA_ADDRESS) arg;

		int weight = (int) singleDeclaration.type.visit(this, arg);
		
		return new DATA_ADDRESS(((DATA_ADDRESS)arg).Level, ((DATA_ADDRESS)arg).Displacement + singleDeclaration.size*weight);
	}

	// An if statement must be managed by declaring an address for if and else cases.
	public Object visitSelectionStatement(SELECTION_STATEMENT selectionStatement, Object arg){
		selectionStatement.Condition.visit( this, Boolean.valueOf(true) );
		
		int jump1Adr = nextAddress;
		emit( Machine.JUMPIFop, 0, Machine.CBr, 0 );
		
		selectionStatement.Commands.visit( this, arg );
		
		int jump2Adr = nextAddress;
		emit( Machine.JUMPop, 0, Machine.CBr, 0 );
		
		patch( jump1Adr, nextAddress );
		selectionStatement.Else_Commands.visit( this, arg );
		patch( jump2Adr, nextAddress );

		return null;
	}

	// A while loop is composed of verifying a condition and running a set of commands.
	public Object visitRepetitionStatement( REPETITION_STATEMENT repetitionStatement, Object arg ) {
		int startAdr = nextAddress;
		
		repetitionStatement.Condition.visit( this, Boolean.valueOf(true));

		int jumpAdr = nextAddress;
		emit( Machine.JUMPIFop, 0, Machine.CBr, 0 );

		repetitionStatement.Commands.visit( this, arg );

		emit( Machine.JUMPop, 0, Machine.CBr, startAdr );
		patch( jumpAdr, nextAddress );

		return null;
	}
	
	public Object visitCommandList(COMMAND_LIST commandList, Object arg) {
		
		int n = commandList.commands.size();
		for (int i = 0; i < n; i++) {
			commandList.commands.get(i).visit(this, new DATA_ADDRESS( ((DATA_ADDRESS)arg).Level, ((DATA_ADDRESS)arg).Displacement + variableDisplacement));
		}
		return null;
	}

	public Object visitDeclarationStatement(DECLARATION_STATEMENT declarationStatement, Object arg) {
		declarationStatement.declaration.visit(this, arg);
		return null;
	}

	public Object visitFunctionStatement(FUNCTION_STATEMENT functionStatement, Object arg) {
		functionStatement.function.visit(this, Boolean.valueOf(false));			
		return null;
	}

	// Standard functions must be linked to the proper sys calls.
	public Object visitFunction(FUNCTION function, Object arg) {
		boolean valueNeeded = ((Boolean) arg).booleanValue();

		CODE_ADDRESS address = (CODE_ADDRESS) function.declaration.InstructionAddress;

		function.args.visit(this, Boolean.valueOf(true));

		switch(function.name.spelling){
			case "writeInt": case "writeBool":
				emit( Machine.CALLop, 0, Machine.PBr, Machine.putintDisplacement );
				break;
			case "readInt": case "readBool":
				emit(Machine.LOADAop, 0, Machine.STr, 0);
				emit( Machine.CALLop, 0, Machine.PBr, Machine.getintDisplacement );
				break;
			case "writeChar": 
				emit( Machine.CALLop, 0, Machine.PBr, Machine.putDisplacement );
				break;
			case "readChar": 
				emit(Machine.LOADAop, 0, Machine.STr, 0);
				emit( Machine.CALLop, 0, Machine.PBr, Machine.getDisplacement );
				break;
			default: emit( Machine.CALLop, 0, Machine.CB, address.Index );
				int length = ((Integer)function.declaration.TypeDec.visit(this, Boolean.valueOf(true) )).intValue();
				if( !valueNeeded )
					emit( Machine.POPop, 0, 0, length );
		}

		return null;
	}

	public Object visitExpressionList(EXPRESSION_LIST expressionList, Object arg) {
		int n = expressionList.values.size();
        for (int i = 0; i < n; i++) {
            expressionList.values.get(i).visit(this, arg );
        }
		return null;
	}
	
	public Object visitCallExpression(CALL_EXPRESSION callExpression, Object arg) {
		callExpression.function.visit(this, Boolean.valueOf(true));
		return null;
	}

	public Object visitIntLitExpression(INTLIT_EXPRESSION intLitExpression, Object arg) {
		boolean valueNeeded = ((Boolean) arg).booleanValue();
		int value = ((Integer)intLitExpression.value.visit(this, arg)).intValue();
		
		if( valueNeeded )
			emit( Machine.LOADLop, 0, 0, value );
			
		return null;
	}

	public Object visitVarExpression(VAR_EXPRESSION varExpression, Object arg) {
		boolean valueNeeded = ((Boolean) arg).booleanValue();
		DATA_ADDRESS address = (DATA_ADDRESS)varExpression.variable.visit(this, arg);
		
		emit(Machine.LOADAop, 0, Machine.LBr, address.Displacement);
		emit( Machine.CALLop, 0, Machine.PBr, Machine.addDisplacement );

		if( valueNeeded )
			emit( Machine.LOADIop,  varExpression.variable.declaration.size * (int)varExpression.variable.declaration.type.visit(this, arg), 0, 0 ); 
			
		return address;
	}

	// For each pair variable/value, proper sys calls are invoked on the correct memory addresses.
	public Object visitAssignment(ASSIGNMENT assignment, Object arg) {
		emit(Machine.LOADAop, 0, Machine.STr, 0);
		emit(Machine.STOREop, 1, Machine.LBr, 3);

		assignment.Variables.visit(this, arg);
		assignment.Values.visit(this, Boolean.valueOf(true));
	
		int n = assignment.Variables.items.size();
		for(int i = n-1; i >= 0; i--){
			emit(Machine.LOADop, 1, Machine.LBr, 3);
			emit(Machine.LOADLop, 0, 0, i);
			emit( Machine.CALLop, 0, Machine.PBr, Machine.addDisplacement );
			emit( Machine.LOADIop, (int)assignment.Variables.items.get(i).declaration.type.visit(this, arg), 0, 0);
			emit( Machine.STOREIop, (int)assignment.Variables.items.get(i).declaration.type.visit(this, arg), 0, 0 );
		}
		emit(Machine.POPop, 0, 0, n);
		return null;
	}

	// Complex binary expressions are handled through chains of "primitive" sys calls.
	public Object visitBinary(BINARY binary, Object arg) {
		boolean valueNeeded = ((Boolean) arg).booleanValue();
		String Operator = (String) binary.Operator.visit( this, arg );
		binary.OperandOne.visit( this, arg );
		binary.OperandTwo.visit( this, arg );
		if(valueNeeded){
			switch(Operator){
				case "++":
					emit( Machine.CALLop, 0, Machine.PBr, Machine.addDisplacement );
					break;
				case  "--" :
					emit( Machine.CALLop, 0, Machine.PBr, Machine.subDisplacement );
					break;
				case "**":
					emit( Machine.CALLop, 0, Machine.PBr, Machine.multDisplacement );
					break;
				case "//" :
					emit( Machine.CALLop, 0, Machine.PBr, Machine.divDisplacement );
					break;
				case "%%": 
					emit( Machine.CALLop, 0, Machine.PBr, Machine.modDisplacement );
					break;
				case "&&":
					emit( Machine.CALLop, 0, Machine.PBr, Machine.andDisplacement );
					break;
				case  "||" :
					emit( Machine.CALLop, 0, Machine.PBr, Machine.orDisplacement );
					break;
				case "##":
					emit( Machine.CALLop, 0, Machine.PBr, Machine.modDisplacement );
					emit( Machine.CALLop, 0, Machine.PBr, Machine.notDisplacement );
					binary.OperandOne.visit( this, arg );
					emit( Machine.CALLop, 0, Machine.PBr, Machine.modDisplacement );
					emit( Machine.CALLop, 0, Machine.PBr, Machine.notDisplacement );
					binary.OperandOne.visit( this, arg );
					binary.OperandTwo.visit( this, arg );
					emit( Machine.CALLop, 0, Machine.PBr, Machine.modDisplacement );
					emit( Machine.CALLop, 0, Machine.PBr, Machine.notDisplacement );
					binary.OperandTwo.visit( this, arg );
					emit( Machine.CALLop, 0, Machine.PBr, Machine.modDisplacement );
					emit( Machine.CALLop, 0, Machine.PBr, Machine.notDisplacement );
					emit( Machine.CALLop, 0, Machine.PBr, Machine.modDisplacement );
					emit( Machine.CALLop, 0, Machine.PBr, Machine.notDisplacement );
					break;
				case "!|" :
					emit( Machine.CALLop, 0, Machine.PBr, Machine.orDisplacement );
					emit( Machine.CALLop, 0, Machine.PBr, Machine.notDisplacement );
					break;
				case "!&": 
					emit( Machine.CALLop, 0, Machine.PBr, Machine.modDisplacement );
					emit( Machine.CALLop, 0, Machine.PBr, Machine.notDisplacement );
					break;
				case "!#":
					emit( Machine.CALLop, 0, Machine.PBr, Machine.modDisplacement );
					emit( Machine.CALLop, 0, Machine.PBr, Machine.notDisplacement );
					binary.OperandOne.visit( this, arg );
					emit( Machine.CALLop, 0, Machine.PBr, Machine.modDisplacement );
					emit( Machine.CALLop, 0, Machine.PBr, Machine.notDisplacement );
					binary.OperandOne.visit( this, arg );
					binary.OperandTwo.visit( this, arg );
					emit( Machine.CALLop, 0, Machine.PBr, Machine.modDisplacement );
					emit( Machine.CALLop, 0, Machine.PBr, Machine.notDisplacement );
					binary.OperandTwo.visit( this, arg );
					emit( Machine.CALLop, 0, Machine.PBr, Machine.modDisplacement );
					emit( Machine.CALLop, 0, Machine.PBr, Machine.notDisplacement );
					emit( Machine.CALLop, 0, Machine.PBr, Machine.modDisplacement );
					emit( Machine.CALLop, 0, Machine.PBr, Machine.notDisplacement );
					emit( Machine.CALLop, 0, Machine.PBr, Machine.notDisplacement );
					break;
				case  "<<" :
					emit( Machine.CALLop, 0, Machine.PBr, Machine.ltDisplacement );
					break;
				case ">>":
					emit( Machine.CALLop, 0, Machine.PBr, Machine.gtDisplacement );
					break;
				case "<=" :
					emit( Machine.CALLop, 0, Machine.PBr, Machine.leDisplacement );
					break;
				case ">=": 
					emit( Machine.CALLop, 0, Machine.PBr, Machine.geDisplacement );
					break;
				case "==": 
					emit( Machine.CALLop, 1, Machine.PBr, Machine.eqDisplacement );
					break;
				case "<>": 
					emit( Machine.CALLop, 1, Machine.PBr, Machine.neDisplacement );
					break;
			}
		}
		return null;
	}

	public Object visitIdentifierList(IDENTIFIER_LIST identifierList, Object arg) {
		ArrayList<DATA_ADDRESS> declarations = new ArrayList<DATA_ADDRESS>();
		int n = identifierList.items.size();
		for(int i = 0; i < n; i++){
			declarations.add((DATA_ADDRESS)identifierList.items.get(i).visit(this, arg));
			emit(Machine.LOADAop, 0, Machine.LBr, declarations.get(i).Displacement);
			emit( Machine.CALLop, 0, Machine.PBr, Machine.addDisplacement );
		}
		return null;
	}

	public Object visitIdentifier(IDENTIFIER identifier, Object arg) {
		emit( Machine.LOADLop, 0, 0, 0 );
		return identifier.declaration.DataAddress;
	}

	public Object visitArrayIdentifier(ARRAY_IDENTIFIER arrayIdentifier, Object arg) {
		arrayIdentifier.index.visit(this, Boolean.valueOf(true));
		emit( Machine.LOADLop, 0, 0, (int)arrayIdentifier.declaration.type.visit(this, arg) );
		emit( Machine.CALLop, 0, Machine.PBr, Machine.multDisplacement );
		return null;
	}

	// Handles boolean values separately from integer ones.
	public Object visitIntegerLiteral(INTEGERLITERAL integerLiteral, Object arg) {
		if(integerLiteral.spelling.equals("FALSE"))
			return 0;
		if(integerLiteral.spelling.equals("TRUE"))
			return 1;
		return  Integer.valueOf(integerLiteral.spelling);
	}

	public Object visitOperator(OPERATOR operator, Object arg) {
		return operator.spelling;
	}

	// A list of types requires an amount of memory to be allocated which is the sum of the sizes of primitive types.
	public Object visitTypeList(TYPE_LIST typeList, Object arg) {
		int size = 0;
		int n = typeList.types.size();
		for(int i = 0; i < n; i++){
			size += (Integer)(typeList.types.get(i).visit(this, arg));
		}
		return size;
	}

	public Object visitType(TYPE type, Object arg) {
		return 1;
	}

	public Object visitCommand(COMMAND command, Object arg) {
		return null;
	}

	public Object visitExpression(EXPRESSION expression, Object arg) {
		return null;
	}

	public Object visitIdentifierItem(IDENTIFIER_ITEM identifierItem, Object arg) {
		return null;
	}

}