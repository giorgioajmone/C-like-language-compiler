package compiler;
import classes.*;

public class IdEntry
{
	public int level;
	public String id;
	public GLOBAL_DECLARATION attribute;


	public IdEntry( int level, String id, GLOBAL_DECLARATION attribute )
	{
		this.level = level;
		this.id = id;
		this.attribute = attribute;
	}
	
	public String toString()
	{
		return level + "," + id;
	}
}