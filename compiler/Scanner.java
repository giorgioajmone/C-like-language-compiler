
package compiler;

public class Scanner
{
	private final SourceFile source;
	
	private char c;
	private StringBuffer buffer = new StringBuffer(); // the buffer will be used to accumulate the characters, until a token is found

	// constructor
	public Scanner( SourceFile source )
	{
		this.source = source; // Source file object
		
		c = source.getSource(); // c is initialized with the first char read

		// as you can see, the constructor does not take care of scanning operation itself. Methods contained in this class are retrieved by the following step of the compiler: parsing.
	}

	// takes each char into the buffer
	private void getc()
	{
		buffer.append( c ); // insert char into buffer
		c = source.getSource(); // takes the next one
	}

	// goes on reading without uploading char into the buffer
	private void throwc()
	{
		c=source.getSource();
	}

	// checks whether the char is a letter
	private boolean isLetter( char c )
	{
		return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
	}

	// checks whether the char is a digit
	private boolean isDigit( char c )
	{
		return c >= '0' && c <= '9';
	}
	
	// checks whether the char is one of the spacing characters that separate tokens. It also checks for comments.
	private void scanSeparator()
	{
		while( c == '?' || c == '\n' ||
		        c == '\r' || c == '\t' ||
		       c == ' ' )
	{
		switch( c ) {
			// if the char is a comment, it goes on reading until it finds the end of the comment
			case '?':
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
	
	// given a token, it returns the token type
	private String scanToken()
	{
		if (c == SourceFile.EOT) {
			getc();
			return "EOT";
		}
		// a token which starts with a letter can contain letters and digits afterwards
		else if ( isLetter( c ) ) {
			getc();
			while ( isLetter( c ) || isDigit( c ) )
				getc();
			return "STRING";
			
		}
		// instead, a token which starts with a digit can contain only digits
		else if ( isDigit( c ) ) {
			getc();
			while ( isDigit( c ) )
				getc();
			return "INTEGERLITERAL";
			
		}
		// the language does not support inserting a string literal, but only an ASCII character
		else if ( c == '\'' ) {
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

		}
		// if the character corresponds to the initial part of one of the operators, it must be appended to a string, and a check must be performed on the following character.
		// Whenever the operator is not a predefined one, the error must be handled. In particular, a SYMBOL token is returned, but it is actually an error, because when it is received by Token.getToken, it will be translated into an ERROR token.
		else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '<' || c == '>' || c == '=' || c == '&' || c == '|' || c == '!' || c == '#') {
			String string = "" + c;
			getc();
			string += c;
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
	
	// reads and generates a token
	public Token scan() 
	{
		scanSeparator();
		buffer = new StringBuffer(); // every time a new token is read, the buffer is reset
		String type = scanToken();
		return new Token( type, new String( buffer ) );
	}

}
