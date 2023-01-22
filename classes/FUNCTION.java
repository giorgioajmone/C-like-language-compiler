package classes;

public class FUNCTION extends AST{
	public IDENTIFIER name;
	public EXPRESSION_LIST args;
	public BLOCK declaration;
	
	public FUNCTION(IDENTIFIER name, EXPRESSION_LIST args) {
		this.name = name;
        this.args = args;
        declaration=null;
	}
	
	public IDENTIFIER getName() {
        return name;
    }
    
    public EXPRESSION_LIST getArgs() {
        return args;
    }
    
    public Object visit(Visitor v, Object arg) {
        return v.visitFunction(this, arg);
    }
}
