package classes;

import java.util.Vector;

public class COMMAND_LIST extends AST{
	
	public Vector<COMMAND> commands = new Vector<COMMAND>();
	
	public COMMAND_LIST() {
	}

    public void addCommand(COMMAND command) {
        commands.add(command);
    }
    
    public Object visit(Visitor v, Object arg) {
        return v.visitCommandList(this, arg);
    }
}
