package compiler;
import java.util.*;
import classes.*;


public class IdentificationTable
{
	private Vector<IdEntry> table = new Vector<IdEntry>(); //create a vector of IdEntry
	private int level = 0;
	
	
	public IdentificationTable()
	{
	}
	
	
	public void enter( String id, GLOBAL_DECLARATION attribute )
	{
		IdEntry entry = find( id ); //looks for the same id in the table
	
		if( entry != null){
			if( entry.level == level )
				System.out.println( id + " declared twice" ); // if it's found and has the same level
			else if (entry.level == 0)
				System.out.println( id + " is a standard function" );
		}
		else{						//    ---> declarated twice		    
			table.add( new IdEntry( level, id, attribute ) ); //else adds the declaration to the table
		}
		System.out.println("ID: "+id+" livello: "+level);
	}
	
	
	public GLOBAL_DECLARATION retrieve( String id )  //returns entry attributes(maybe the declaration)
	{
		IdEntry entry = find( id );
		
		if( entry != null )
			return entry.attribute;
		else
			return null;
	}
	
	
	public void openScope()
	{
		++level;
	}
	
	
	public void closeScope()
	{
		int pos = table.size() - 1; //positions from 0 to n-1
		while( pos >= 0 && table.get(pos).level == level ) {
			table.remove( pos ); //removes the element in that position
			pos--;
		}
		
		level--;
	}
//scope works in this way maybe.
//open scope opens all the declarations at a specific level
//once these declarations are closed, they need to be removed from the table
	
	
	private IdEntry find( String id )
	{
		for( int i = table.size() - 1; i >= 0; i-- )
			if( table.get(i).id.equals( id ) )
				return table.get(i);
				
		return null;
	}
}