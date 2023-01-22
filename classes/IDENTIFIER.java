package classes;

public class IDENTIFIER extends IDENTIFIER_ITEM {
	
    public IDENTIFIER (String spelling) {
        this.spelling = spelling;
    }

    public Object visit(Visitor v, Object arg) {
        return v.visitIdentifier(this, arg);
    }

}
