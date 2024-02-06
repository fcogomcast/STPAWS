/**
 * 
 */
package stpa.exceptions;

/**
 * @author crubencvs
 *
 */
public class PeticionParseException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1852623682771147938L;
	private String error;
	private Throwable original;
	
	public PeticionParseException(String error, Throwable throwable){
		this.error = error;
		original = throwable;
	}
	public PeticionParseException (String error)
	{
		this.error = error;
		original=null;
	}
	
	public Throwable getCause(){
		return original;
	}
	
	public String getMessage(){
		StringBuffer strBuffer = new StringBuffer();
		if(super.getMessage()!=null){
			strBuffer.append(super.getMessage());
		}
		if(original !=null){
			strBuffer.append("[");
			strBuffer.append(original.getMessage());
			strBuffer.append("]");
		}
		return strBuffer.toString();
	}
	
	public String getError(){
		return error;
	}
}
