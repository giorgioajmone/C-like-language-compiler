package classes;

import java.util.Vector;

public class DECLARATION_LIST extends AST{
	public Vector<DECLARATION> declarations = new Vector<DECLARATION>();

    public DECLARATION_LIST () {}
    
    public void addDeclaration (DECLARATION declaration) {
        declarations.addElement(declaration);
    }
    
    public Object visit(Visitor v, Object arg) {
        return v.visitDeclarationList(this, arg);
    }
}
