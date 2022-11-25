package classes;

public class TYPE extends TERMINAL {
	
    public TYPE (String spelling) {
        this.spelling = spelling;
    }
    
    public Object visit(Visitor v, Object arg) {
        return v.visitType(this, arg);
    }
}

