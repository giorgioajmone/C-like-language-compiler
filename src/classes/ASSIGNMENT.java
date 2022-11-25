package classes;

public class ASSIGNMENT extends COMMAND {
	public IDENTIFIER_LIST Variables;
	public EXPRESSION_LIST Values;
	
	public ASSIGNMENT(IDENTIFIER_LIST Variables,EXPRESSION_LIST Values) {
		this.Variables=Variables;
		this.Values=Values;
	}
	
	public IDENTIFIER_LIST getIdentifierList() {
	    return Variables;
    }

    public EXPRESSION_LIST getExpressionList() {
        return Values;
    }
    
    public Object visit(Visitor v, Object arg) {
        return v.visitAssignment(this, arg);
    }
	
}
