/**
 * 
 */
package stpa.exceptions;

/**
 * @author crubencvs
 *
 */
public class SelectorPeticionException extends Exception {
	private static final long serialVersionUID = -5362408473839357401L;
	private String error;
	private Throwable original;
	
	public SelectorPeticionException(String error, Throwable throwable){
		this.error = error;
		original = throwable;
	}
	public SelectorPeticionException (String error)
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
