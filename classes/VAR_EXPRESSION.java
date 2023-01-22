package classes;

public class VAR_EXPRESSION extends EXPRESSION{
	
	public IDENTIFIER_ITEM variable;
	
	public VAR_EXPRESSION(IDENTIFIER_ITEM variable) {
		this.variable=variable;
	}
	
	public IDENTIFIER_ITEM getVariable() {
	    return variable;
    }
    
    public Object visit(Visitor v, Object arg) {
        return v.visitVarExpression(this, arg);
    }
}
