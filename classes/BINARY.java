package classes;

public class BINARY extends EXPRESSION{
	public EXPRESSION OperandOne;
	public OPERATOR Operator;
	public EXPRESSION OperandTwo;
	
	
	public BINARY(EXPRESSION OperandOne,OPERATOR Operator,EXPRESSION OperandTwo) {
		this.OperandOne = OperandOne;
		this.OperandTwo = OperandTwo;
		this.Operator = Operator;
	}
	
	public EXPRESSION getOperandOne() {
        return OperandOne;
    }
    
    public OPERATOR getOperator() {
        return Operator;
    }
    
    public EXPRESSION getOperandTwo() {
        return OperandTwo;
    }
    
    public Object visit(Visitor v, Object arg) {
        return v.visitBinary(this, arg);
    }
}
