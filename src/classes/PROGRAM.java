package classes;

import java.util.Vector;

public class PROGRAM extends AST{
	public Vector<BLOCK> blocks = new Vector<BLOCK>();
	
	public PROGRAM() {
		
	}
	
	public void addBlock(BLOCK b) {
        blocks.add(b);
    }
    
    public int getBlockCount() {
        return blocks.size();
    }
    
    public BLOCK getBlock(int i) {
        return blocks.get(i);
    }
    
    public Object visit(Visitor v, Object arg) {
        return v.visitProgram(this, arg);
    }
}
