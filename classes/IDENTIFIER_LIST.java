package classes;

import java.util.Vector;

public class IDENTIFIER_LIST extends AST{
	public Vector<IDENTIFIER_ITEM> items = new Vector<IDENTIFIER_ITEM>();
	
	public IDENTIFIER_LIST() {}

    public void addItem (IDENTIFIER_ITEM item) {
		items.addElement(item);
	}
	
	public Object visit(Visitor v, Object arg) {
        return v.visitIdentifierList(this, arg);
    }
}
