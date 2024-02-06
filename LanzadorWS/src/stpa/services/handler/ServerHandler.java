package stpa.services.handler;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Date;

import javax.xml.namespace.QName;
import javax.xml.rpc.handler.MessageContext;
import javax.xml.rpc.handler.soap.SOAPMessageContext;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import stpa.utils.preferencias.Preferencias;
import stpa.utils.preferencias.PreferenciasException;

public class ServerHandler extends javax.xml.rpc.handler.GenericHandler {
		
	

	private final String LOG_FILE = "SOAP_SERVER.log";
	private final String LOG_DIR = "proyectos/Lanzador";
	
	@Override
	public boolean handleRequest(javax.xml.rpc.handler.MessageContext context) {
		try{
	    	Preferencias _pr=Preferencias.getPreferencias();
	    	if (_pr.getDebugSoap().equalsIgnoreCase("S"))
	    	{
	    		log(context,"Recepción");
	    		
	    	}
	    	}
	    	catch (PreferenciasException ex)
	    	{
	    		//Escribimos en log de servidor el stacktrace
	    		System.out.println (new Date().toString()+"::Error en log SOAP_SERVER de lanzador::"+ex.getMessage());
	    		ex.printStackTrace();
	    	}
	    	return true;
	}


	@Override
	public boolean handleResponse(MessageContext context) {
		try{
	    	Preferencias _pr=Preferencias.getPreferencias();
	    	if (_pr.getDebugSoap().equalsIgnoreCase("S"))
	    	{
	    		log(context,"Envío");
	    		
	    	}
	    	}
	    	catch (PreferenciasException ex)
	    	{
	    		//Escribimos en log de servidor el stacktrace
	    		System.out.println (new Date().toString()+"::Error en log SOAP_SERVER de lanzador::"+ex.getMessage());
	    		ex.printStackTrace();
	    	}
	    	return true;
	}


	@Override
	public QName[] getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}
	    private void log(javax.xml.rpc.handler.MessageContext messageContext, String direccion) 
	    { 	
			SOAPMessageContext smc = (SOAPMessageContext)messageContext;
			//Ip . Parece que esta es la forma de conseguirlo.
			//javax.servlet.http.HttpServletRequest request = null;

            //request = (javax.servlet.http.HttpServletRequest)
            //        smc.getProperty("com.sun.xml.rpc.server.http.HttpServletRequest");
                                   
            //String ip = request.getRemoteAddr();
            //Fin Ip
	        SOAPMessage msg = smc.getMessage();
	        FileWriter out = null;
	        PrintWriter pw=null;
	        synchronized (this)
	        {
		     try 
		     {
		    	 //obtenemos el array de bytes que se corresponde con el mensaje soap
		    	 ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		         msg.writeTo(byteArray);
		         
		         //componemos la linea del log
		         String soapMessage = new String(byteArray.toByteArray());
		         Date today = new Date();
		         String log = "Lanzador::SOAP_SERVER ::" +direccion+"::"+ " :: " + today + " :: " + soapMessage; 
		         
		         out = new FileWriter(LOG_DIR+"/"+LOG_FILE,true);
		         if (out!=null)
		         {
		        	 pw = new PrintWriter(out);
		        	 pw.println(log);
		         }
		     } 
		     catch (SOAPException ex) 
		     {
		         System.out.println("SOAP Exception escribiendo mensaje a fichero: "+ex.getMessage());
		         ex.printStackTrace();
		         //System.out.println(ex.getStackTrace());
		     } 
		     catch (IOException ex) 
		     {
		    	 System.out.println("IO Exception escribiendo mensaje a fichero: "+ex.getMessage());
		    	 ex.printStackTrace();
		    	 //System.out.println(ex.getStackTrace());
		     }
		     finally
		     {
	            if(out != null)
	            {
	            	try
	            	{
	            		pw.close();
	            	}
	            	catch (Exception e) // En principio no debería devolver, nunca, una excepción. Se controla 
	            						// por si hay cambios en la implementación.
	            	{
	            		System.out.println ("Error cerrando flujo de impresión de lanzador: " + e.getMessage());
	            		e.printStackTrace();
	            	}
	                try
	                {
	                    out.close();
	                }
	                catch(Exception e)
	                {
	                    System.out.println("Error cerrando fichero de log SOAP de lanzador-> "+e.getMessage());
	                    e.printStackTrace();
	                }
	            }
		     }
	        }
	    }
	

}
