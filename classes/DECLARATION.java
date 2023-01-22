package classes;

import java.util.Vector;

public class DECLARATION extends AST {
	public Vector<SINGLE_DECLARATION> singleDeclarations = new Vector<SINGLE_DECLARATION>();

    public DECLARATION () {
    }
    
    public Object visit(Visitor v, Object arg) {
        return v.visitDeclaration(this, arg);
    }
}
