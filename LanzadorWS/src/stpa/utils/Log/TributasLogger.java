/**
 * 
 */
package stpa.utils.Log;

/**
 * @author crubencvs
 * Esta clase no se utiliza, se tiene como opción por si no se consigue hacer funcionar bien 
 * java.util.logging 
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Pequeña clase de log que se utilizará para mantener el log de aplicación, no el de mensajes SOAP. 
 * 
 *
 */
public class TributasLogger implements ILogger
{
	@Override
	public void error(String msg) {
		doLog(msg,LEVEL.SEVERE);
		
	}
	@Override
	public void fine(String msg) {
		
	}
	@Override
	public void info(String msg) {
		
	}
	@Override
	public void warning(String msg) {
	}

	private final static String LOG_FILE = "Application.log";
	private final static String LOG_DIR = "proyectos/Lanzador";
	
	public enum LEVEL {NONE,FINE, INFO, WARNING, SEVERE,ALL}
	// Método sincronizado. 
	/**
	 * Método que realiza log.
	 * 
	 */
	public final synchronized static void doLog(String message, LEVEL level)
	{
		File file;
        FileWriter fichero = null;
        PrintWriter pw=null;

        try
        {
            Date today = new Date();
            String completeMsg = "Lanzador :: " + today + " :: " + level + " :: " + message;
            
            file = new File(LOG_DIR);
            if(file.exists() == false)
                if (file.mkdirs()==false)
                	{
                		throw new IOException("No se puede crear el directorio de log."); 
                	}
            
            fichero = new FileWriter(LOG_DIR + "/" + LOG_FILE, true);//true para que agregemos al final del fichero
            if (fichero!=null)
            {
            	pw = new PrintWriter(fichero);
            
            	pw.println(completeMsg);
            }
            
        }
        catch (IOException e)
        {
            System.err.println("Error escribiendo log '"+message+"' -> "+e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            if(fichero != null)
            {
            	try
            	{
            		pw.close();
            	}
            	catch (Exception e) // En principio no debería devolver, nunca, una excepción. Se controla 
            						// por si hay cambios en la implementación.
            	{
            		System.out.println ("Error cerrando flujo de impresión: " + e.getMessage());
            		e.printStackTrace();
            	}
                try
                {
                    fichero.close();
                }
                catch(Exception e)
                {
                    System.err.println("Error cerrando fichero de log -> "+e.getMessage());
                    e.printStackTrace();
                }
            }
        }
	}
	
	public final void printErrorStack(StackTraceElement[] stackTraceElements)
	{
		 if (stackTraceElements == null)
	            return;
        for (int i = 0; i < stackTraceElements.length; i++)
        {
            doLog("StackTrace -> " + stackTraceElements[i].getFileName() + " :: " + stackTraceElements[i].getClassName() + " :: " + stackTraceElements[i].getFileName() + " :: " + stackTraceElements[i].getMethodName() + " :: " + stackTraceElements[i].getLineNumber(),LEVEL.SEVERE);
        }
	}
	
}
