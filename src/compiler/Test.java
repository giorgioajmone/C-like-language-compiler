
package compiler;

import java.io.FileNotFoundException;

import classes.*;


public class Test
{
	public static void main( String args[] ) throws FileNotFoundException
	{		
		SourceFile f = new SourceFile();
		
		Scanner s = new Scanner(f);
		//Token t = s.scan();
		ParserAST p = new ParserAST( s );
		AST ast = p.parseProgram();
		Checker c = new Checker();
		c.check((PROGRAM) ast);
		new ASTViewer( ast );
	}
}