package exception;

public class WrongTokenException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WrongTokenException(String message) {
	      super(message);
	      
	   }
	
}
