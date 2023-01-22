package classes;

import java.util.Vector;

public class TYPE_LIST extends AST{
	
	public Vector<TYPE> types = new Vector<TYPE>();
    
    public TYPE_LIST() {}
    
    public void addType(TYPE type) {
        types.addElement(type);
    }
    
    public Object visit(Visitor v, Object arg) {
        return v.visitTypeList(this, arg);
    }
}
