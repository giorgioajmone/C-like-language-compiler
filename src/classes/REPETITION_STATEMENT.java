package classes;

public class REPETITION_STATEMENT extends COMMAND{
	public EXPRESSION Condition;
	public COMMAND_LIST Commands;
	
	public REPETITION_STATEMENT(EXPRESSION Condition,COMMAND_LIST Commands) {
		this.Condition=Condition;
		this.Commands=Commands;
	}
	
	public EXPRESSION getCondition() {
        return Condition;
    }
   
    public COMMAND_LIST getCommands() {
        return Commands;
    }
    
    public Object visit(Visitor v, Object arg) {
        return v.visitRepetitionStatement(this, arg);
    }
}
