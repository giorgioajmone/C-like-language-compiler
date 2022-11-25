package classes;

public class SELECTION_STATEMENT extends COMMAND{

	public EXPRESSION Condition;
	public COMMAND_LIST Commands;
	public COMMAND_LIST Else_Commands;
	
	public SELECTION_STATEMENT(EXPRESSION Condition,COMMAND_LIST Commands,COMMAND_LIST Else_Commands) {
		this.Condition=Condition;
		this.Commands=Commands;
		this.Else_Commands=Else_Commands;
	}
	
	public EXPRESSION getCondition() {
        return Condition;
    }
    
    public COMMAND_LIST getCommands() {
        return Commands;
    }
    
    public COMMAND_LIST getElse_Commands() {
        return Else_Commands;
    }
    
    public Object visit(Visitor v, Object arg) {
        return v.visitSelectionStatement(this, arg);
    }
}