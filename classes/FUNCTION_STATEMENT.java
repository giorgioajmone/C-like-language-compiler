package classes;

public class FUNCTION_STATEMENT extends COMMAND{
	
	public FUNCTION function;

        public FUNCTION_STATEMENT (FUNCTION function) {
        this.function = function;
    }
    
    public FUNCTION getFunction(){
        return function;
    }
    
    public Object visit(Visitor v, Object arg) {
        return v.visitFunctionStatement(this, arg);
    }
}
