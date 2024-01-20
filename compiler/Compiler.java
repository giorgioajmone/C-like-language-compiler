
package compiler;

import java.io.FileNotFoundException;

import javax.swing.JFileChooser;

import TAM.Interpreter;
import classes.*;


// Main file to be executed
public class Test
{
	private static final String EXAMPLES_DIR = "/path/to/file";
	
	public static void main( String args[] ) throws FileNotFoundException
	{		
		JFileChooser fc = new JFileChooser( EXAMPLES_DIR );
		
		if( fc.showOpenDialog( null ) == fc.APPROVE_OPTION ) {
			String sourceName = fc.getSelectedFile().getAbsolutePath();
			SourceFile f = new SourceFile();
			// Syntactic analysis
			Scanner s = new Scanner(f);
			ParserAST p = new ParserAST( s );
			AST ast = p.parseProgram();
			// Contextual analysis
			Checker c = new Checker();
			// Code generation
			Encoder e = new Encoder();
			c.check((PROGRAM) ast);
			e.encode((PROGRAM) ast );
			
			String targetName;
			if( sourceName.endsWith( ".txt" ) )
				targetName = sourceName.substring( 0, sourceName.length() - 4 ) + ".tam";
			else
				targetName = sourceName + ".tam";
			
			e.saveTargetProgram( targetName );
		}
		
	}
}