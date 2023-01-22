package classes;

public class OPERATOR extends TERMINAL {
	
    public OPERATOR (String spelling) {
        this.spelling = spelling;
    }

    public Object visit(Visitor v, Object arg) {
        return v.visitOperator(this, arg);
    }
}

