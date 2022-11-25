/*
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
gestione stringhe (unico identifier?tanti literal?)
verificare lettura secondo carattere dopo operatore esempio +a solo piu deve essere errore
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
*/
package compiler;

public class Scanner
{
	private SourceFile source;
	
	private char c;
	private StringBuffer buffer = new StringBuffer(); // creates and initializes string buffer
	public Scanner( SourceFile source ) // scanner builder
	{
		this.source = source; // Source file object
		
		c = source.getSource(); // c is initialized with the first char read.
	}
	
	
	private void getc() // method that takes each char into the buffer
	{
		buffer.append( c ); // insert char into buffer
		c = source.getSource(); // takes the next one
	}
	private void throwc() // method that goes on reading without uploading char into the buffer 
	{
		c=source.getSource();
	}
	
	private boolean isLetter( char c )
	{
		return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z'; // checks whether the char is a letter
	}
	
	
	private boolean isDigit( char c )
	{
		return c >= '0' && c <= '9';  // checks whether the char is a string
	}
	
	
	private void scanSeparator() // method for comments
	{
		while( c == '?' || c == '\n' ||
		        c == '\r' || c == '\t' ||
		       c == ' ' )
	{
		switch( c ) {
			case '?': // comment symbol
				getc();
				while( c != SourceFile.EOL && c != SourceFile.EOT ) // throws away the comments
					throwc(); 
				if( c == SourceFile.EOL ) // throws away end of line
					throwc();
				break;
			case ' ': case '\n': case '\r': case '\t':
				throwc();
				break;
		}
	}
	}
	
	
	private String scanToken() // defines type of token
	{
		if (c == SourceFile.EOT) {
			getc();
			return "EOT";
		}
		else if ( isLetter( c ) ) { // if the char is a letter goes on
			getc();
			while ( isLetter( c ) || isDigit( c ) )
				getc(); // goes on until you read a char or a number
				
			return "STRING";
			
		} else if ( isDigit( c ) ) {
			getc(); // otherwise if you read a number read all the numbers
			while ( isDigit( c ) )
				getc();
				
			return "INTEGERLITERAL";
			
		} else if ( c == '\'' ) {
			throwc();
			while ( c != '\'' ) {
				getc();
			}
			throwc();
			if (buffer.length() == 1) {
				buffer.replace(0, 1, Integer.toString((int)buffer.charAt(0)));
				return "INTEGERLITERAL";
			}
			return "ERROR";

		} else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '<' || c == '>' || c == '=' || c == '&' || c == '|' || c == '!' || c == '#') {
			String string= ""+c;
			getc();
			string+=c;
			for (int i = 0; i < Token.Operators.length; i++){
				if (string.equals(Token.Operators[i])) {
                    string = null;
                    getc();
                    return "OPERATOR";
				}
			}	
			string = null;
			return "SYMBOL";
		}
		else{
        	getc();
        	return "SYMBOL";
		}
	}
	
	

	public Token scan() 
	{
		scanSeparator(); // call the scan separator
		buffer = new StringBuffer( "" ); 
		String kind = scanToken(); // saves the type of the token
		return new Token( kind, new String( buffer ) ); // returns it
	}

}
