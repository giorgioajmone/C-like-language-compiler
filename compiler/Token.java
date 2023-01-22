package compiler;

// Atomic element of language (token)
public class Token {

    // For simplicity, we use strings to represent all the information contained in a token
    public String type;
    public String spelling;

    // Operators array includes arithmetic, logical and relational operators, but not assignment
    public static final String[] Operators = {"++", "--", "**", "//", "%%", "^^", "<<", "<-", ">>", "==", "<=", ">=", "<>", "&&", "||", "##", "!|", "!&", "!#"};

    // Spellings includes all the keywords of the language
    private static String[] Spellings = {
            ";", "@", "(", ")", "{", "}", "[", "]", ",", "$", ":", "?", "\'",
            "if", "else", "loop", "return", "int", "bool","writeChar","readChar","writeInt","readInt","writeBool","readBool", "TRUE", "FALSE"
    };

    // Tokens is a string array, so that for every token string there is a corresponding string in the Spellings array
    private static String[] Tokens = {
            "ENDLINE", "FUNDEC", "LEFTROUND", "RIGHTROUND", "LEFTGRAPH", "RIGHTGRAPH",  "LEFTSQUARE", "RIGHTSQUARE", "COMMA", "DOLLAR", "COLON", "COMMENT", "QUOTE",
            "IF", "ELSE", "LOOP", "RETURN", "INT", "BOOL","WRITECHAR","READCHAR","WRITEINT","READINT","WRITEBOOL","READBOOL","INTEGERLITERAL","INTEGERLITERAL"
    };

    public Token(String t, String s) {
        this.type = t; 
        this.spelling = s;

        if (type.equals("STRING") || type.equals("SYMBOL")) {
            this.type = getToken(s, type);
        }

        // The assignment operator is considered separately
        if(spelling.equals("<-")) {
            this.type = "LEFTARROW";
        }
    }

    // given spelling and type, it returns the corresponding token
    private String getToken (String s, String p) {
        int i;
        for (i = 0; i < Spellings.length; i++) {
            if (s.equals(Spellings[i])){
                return Tokens[i];
            }
        }
        // handling error case
        if (p.equals("SYMBOL")) {
            return "ERROR";
        }
        // handling variable/function names
        return "IDENTIFIER";
    }

    public boolean check(String s) {
       	if(type.equals(s)) return true;
    	return false;
    }
    
    public String getSpelling() {
    	return this.spelling;
    }

    public boolean isQuaternaryOP () {
        return (spelling.equals("==") || spelling.equals(">>") || spelling.equals("<<") || spelling.equals("<>") || spelling.equals("<=") || spelling.equals(">="));
    }

    public boolean isPrimaryOP () {
    	return (spelling.equals("^^") || spelling.equals("&&") || spelling.equals("!&"));
    }

    public boolean isSecondaryOP () {
        return (spelling.equals("**") || spelling.equals("//") || spelling.equals("%%") || spelling.equals("##") || spelling.equals("!#"));
    }

    public boolean isTertiaryOP () {
        return (spelling.equals("++") || spelling.equals("--") || spelling.equals("||") || spelling.equals("!|"));
    }
}

