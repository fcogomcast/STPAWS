package stpa.utils;

import java.io.FileOutputStream;
import java.io.Reader;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;

import stpa.utils.preferencias.Preferencias;

public class utilsDB {

	public static String lanzaPL (String sEsquema, String sPeticion, String sIP, String sNIF, String sNombre, String sCertificado) {
		Connection conn = null;
		CallableStatement cs = null;
		Clob cResult = null;
		FileOutputStream myFO = null;
		StringBuffer sOutDB = new StringBuffer();
		try {
			Preferencias _pr=Preferencias.getPreferencias();
			//Obtener conexion de base de datos
			Hashtable ht = new Hashtable();
			ht.put( Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory" );
			//ht.put( Context.PROVIDER_URL,"t3://localhost:7001"); 
			ht.put( Context.PROVIDER_URL,_pr.getProviderURL());
			Context ctx = new InitialContext( ht ); 
			//javax.sql.DataSource ds = (javax.sql.DataSource)ctx.lookup("gtpaDS");
			javax.sql.DataSource ds = (javax.sql.DataSource)ctx.lookup(_pr.getDataSource());
			conn = ds.getConnection();

			//Montar llamada a procedimiento almacenado en base de datos
			String sStoredProcedure = "{ call TRANSACS24X7.Lanzador24x7(?, ?, ?, ?, ?, ?, ?) }";
			cs = conn.prepareCall(sStoredProcedure);
			
			cs.setString(1, sEsquema);
			// Pasar parametro CLOB como string
			cs.setString(2, sPeticion);
			//cs.setString(3, "");
			//Según el driver, pasarlo como String fallará al recuperar valor.
			cs.registerOutParameter(3, java.sql.Types.CLOB);
			cs.setString(4, sIP);
			cs.setString(5, sNIF);
			cs.setString(6, sNombre);
			cs.setString(7, sCertificado);

			
			// Ejecutar procedimiento almacenado en base de datos
			cs.execute();

			// Obtener parametro de procedimiento almacenado de salida 
			cResult = cs.getClob(3);
						
			// Enviar resultado de parametro de salida como respuesta al WebService
			sOutDB = new StringBuffer();
		    char[] clobType = new char[ (int)cResult.length() ];
		    Reader reader = cResult.getCharacterStream(); 
		    int n = reader.read(clobType);
		    sOutDB.append(clobType);

		} catch (Exception e) {
			e.printStackTrace();
			stpa.utils.Log.TributasLogger log=new stpa.utils.Log.TributasLogger();
			log.error(e.getMessage());
			log.error(e.toString());
			log.printErrorStack(e.getStackTrace());
		} finally {
			if (cs != null) {
				try {cs.close();} catch (Exception e) {}
			}
			if (conn != null) {
				try {conn.close();} catch (Exception e) {}
			}
			if (myFO != null) {
				try {myFO.close();} catch (Exception e) {}
			}
			
		}
		if (cResult == null) {
			return "No ejecutado ok";
		} else {
			return sOutDB.toString();
		}
		
	}


}
