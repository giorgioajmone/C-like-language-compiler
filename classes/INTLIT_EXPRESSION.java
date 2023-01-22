package classes;

public class INTLIT_EXPRESSION extends EXPRESSION{
	
	public INTEGERLITERAL value;

	public INTLIT_EXPRESSION (INTEGERLITERAL value) {
		this.value = value;
	}

    public INTEGERLITERAL getValue() {
        return value;
    }
    
    public Object visit(Visitor v, Object arg) {
        return v.visitIntLitExpression(this, arg);
    }
}
