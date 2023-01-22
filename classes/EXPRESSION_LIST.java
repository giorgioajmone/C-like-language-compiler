package classes;

import java.util.Vector;

public class EXPRESSION_LIST extends AST{
	public Vector<EXPRESSION> values = new Vector<EXPRESSION>();
	public int size;
    
	public EXPRESSION_LIST() {
		
	}

    public void addValue(EXPRESSION value) {
        values.add(value);
    }

    public Object visit(Visitor v, Object arg) {
        return v.visitExpressionList(this, arg);
    }
}
