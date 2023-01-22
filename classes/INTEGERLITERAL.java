package classes;

public class INTEGERLITERAL extends TERMINAL {
		
    public INTEGERLITERAL (String spelling) {
        this.spelling = spelling;
    }
    
    public Object visit(Visitor v, Object arg) {
        return v.visitIntegerLiteral(this, arg);
    }

}
