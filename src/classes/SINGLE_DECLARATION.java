package classes;

public class SINGLE_DECLARATION extends GLOBAL_DECLARATION{
	
	public TYPE type;
	public IDENTIFIER name;
	public int size;
	public DATA_ADDRESS DataAddress;
	
	public SINGLE_DECLARATION(TYPE type,IDENTIFIER name,int size) {
		this.type = type;
		this.name = name;
		this.size = size;
		this.DataAddress = null;
	}
	
	
	public Object visit(Visitor v, Object arg) {
		
		return v.visitSingleDeclaration(this,arg);
	}

}
