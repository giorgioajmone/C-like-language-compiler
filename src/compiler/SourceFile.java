/*
 * 16.08.2016 Minor editing
 * 25.09.2009 New package structure
 * 22.09.2006 Original version (adapted from Watt&Brown)
 */
 
package compiler;


import java.io.*;


public class SourceFile
{
	public static final char EOL = '\n'; 
	public static final char EOT = 0;
	
	private FileInputStream source; // creates attribute
	
	
	public SourceFile( ) throws FileNotFoundException //builder that takes the file
	{
		source = new FileInputStream("D:\\Politecnico\\VIA University College\\CMC\\Compilatore\\src\\example.txt");

		}
	
	public char getSource()
	{
		try {
			int c = source.read();
			if( c < 0 ) // read method returs -1 if we are at the end of the file
				return EOT;
			else
				return (char) c; // Otherwise returns a char
		} catch( IOException ex ) {
			return EOT;
		}
	}
}