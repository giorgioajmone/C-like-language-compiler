package classes;

public class CALL_EXPRESSION extends EXPRESSION{

	public FUNCTION function;
	
	public CALL_EXPRESSION (FUNCTION function) {
		this.function=function;
	}

    public FUNCTION getFunction() {
        return function;
    }
    public Object visit(Visitor v, Object arg) {
        return v.visitCallExpression(this, arg);
    }
}
