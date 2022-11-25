package compiler;
public class Token {

    public String type;
    public String spelling;
    
    public static final String[] Operators = {"++", "--", "**", "//", "%%", "^^", "<<", "<-", ">>", "==", "<=", ">=", "<>", "&&", "||", "##", "!|", "!&", "!#"};

    private static String[] Spellings = {
            ";", "@", "(", ")", "{", "}", "[", "]", ",", "$", ":", "�", "\'",
            "if", "else", "loop", "return", "int", "bool","writeChar","readChar","writeInt","readInt","writeBool","readBool", "TRUE", "FALSE"
    };

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

        if(spelling.equals("<-")) {
            this.type = "LEFTARROW";
        }
    }

    private String getToken (String s, String p) {
        int i;
        for (i = 0; i < Spellings.length; i++) {
            if (s.equals(Spellings[i])){
                return Tokens[i];
            }
        }
        if (p.equals("SYMBOL")) {
            return "ERROR";
        }
        return "IDENTIFIER";
    }

    public boolean check(String s) {
       	if(type.equals(s)) return true;
    	return false;
    }
    
    public String getSpelling() {
    	return this.spelling;
    }

    public boolean isPrimaryOP () {
        return (spelling.equals("==") || spelling.equals(">>") || spelling.equals("<<") || spelling.equals("<>") || spelling.equals("<=") || spelling.equals(">="));
    }

    public boolean isSecondaryOP () {
    	return (spelling.equals("^^") || spelling.equals("&&") || spelling.equals("!&"));
    }

    public boolean isTertiaryOP () {
        return (spelling.equals("**") || spelling.equals("//") || spelling.equals("%%") || spelling.equals("##") || spelling.equals("!#"));
    }

    public boolean isQuaternaryOP () {
        return (spelling.equals("++") || spelling.equals("--") || spelling.equals("||") || spelling.equals("!|"));
    }
}

