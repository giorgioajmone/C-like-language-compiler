package classes;

public abstract class IDENTIFIER_ITEM extends TERMINAL {
    public SINGLE_DECLARATION declaration;

    public Object visitForRetrieve(Visitor v, Object arg) {
        return v.visitIdentifierItem(this, arg);
    }
}
