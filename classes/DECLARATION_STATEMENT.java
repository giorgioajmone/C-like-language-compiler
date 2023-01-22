package classes;

public class DECLARATION_STATEMENT extends COMMAND{
	
	public DECLARATION declaration;
	
	public DECLARATION_STATEMENT(DECLARATION declaration) {
		this.declaration=declaration;
	}
	
    public DECLARATION getDeclaration() {
        return declaration;
    }
    
    public Object visit(Visitor v, Object arg) {
        return v.visitDeclarationStatement(this, arg);
    }
}
