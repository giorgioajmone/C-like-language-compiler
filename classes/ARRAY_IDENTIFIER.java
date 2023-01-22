package classes;

public class ARRAY_IDENTIFIER extends IDENTIFIER_ITEM{
	
	public EXPRESSION index;
	
	public ARRAY_IDENTIFIER (IDENTIFIER id,EXPRESSION index) {
			this.spelling = id.spelling;;
	        this.index = index;
	}
	
	public EXPRESSION getIndex () {
	    return index;
    }

	@Override
	public Object visit(Visitor v, Object arg) {
		return v.visitArrayIdentifier(this, arg);
	}
}
