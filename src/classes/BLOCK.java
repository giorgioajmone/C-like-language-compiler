package classes;

public class BLOCK extends GLOBAL_DECLARATION{
	public TYPE_LIST TypeDec;
	public IDENTIFIER FuncName;
	public DECLARATION_LIST ParamDec;
	public COMMAND_LIST Commands;
	public EXPRESSION_LIST ReturnedValues;
	public ADDRESS InstructionAddress;
	
	public BLOCK(TYPE_LIST TypeDec,IDENTIFIER FuncName,DECLARATION_LIST ParamDec,COMMAND_LIST Commands, EXPRESSION_LIST ReturnedValues){
		this.Commands=Commands;
		this.TypeDec=TypeDec;
		this.FuncName=FuncName;
		this.ParamDec=ParamDec;
		this.ReturnedValues=ReturnedValues;
		this.InstructionAddress = null;
	}
	
	public TYPE_LIST getType(){
	    return TypeDec;
    }
    
    public IDENTIFIER getFuncName(){
        return FuncName;
    }
    
    public DECLARATION_LIST getParamDec(){
        return ParamDec;
    }
    
    public COMMAND_LIST getCommands(){
        return Commands;
    }
    
    public EXPRESSION_LIST getReturnedValues(){
        return ReturnedValues;
    }
    
    public Object visit(Visitor v, Object arg) {
        return v.visitBlock(this, arg);
    }
}
