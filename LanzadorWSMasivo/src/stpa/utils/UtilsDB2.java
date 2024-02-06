package stpa.utils;

import java.io.FileOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;


import stpa.bd.helpers.ParametroWrapper;
import stpa.bd.helpers.PeticionWrapper;
import stpa.bd.helpers.ParametroWrapper.tipoParametro;
import stpa.comun.Constantes;
import stpa.comun.Utils;
import stpa.parserPeticion.XMLParser;
import stpa.utils.preferencias.Preferencias;
/**
 * Permite acceso a base de datos, y soporta parámetros de tipo array.
 * @author crubencvs
 *
 */
public class UtilsDB2 {

	/**
	 * Convierte la fecha de formato Oracle a fecha de Java. Como no es cuestión de hacerse un parser,
	 * se hace enviando la fecha a BD para que nos la devuelva.
	 * @param valor     Cadena que contiene una fecha
	 * @param formato   Formato de fecha según Oracle
	 * @param conn      Conexión a base de datos
	 * @return          Fecha como objeto {@link Date}
	 * @throws java.sql.SQLException
	 */
	private java.sql.Date convertDateFromOracle (String valor, String formato, Connection conn) throws java.sql.SQLException
	{
		Statement st=null;
		java.sql.Date result=null;
		ResultSet rs=null;
		try
		{
			String call=null;
		if (formato!=null)
		{
			call = "SELECT to_date('"+valor+"','"+formato+"') from dual";
		}
		else
		{
			call = "SELECT to_date('"+valor+"') from dual";
		}
		st = conn.createStatement();
		rs = st.executeQuery(call);
		if (rs.next())
		{
			result = rs.getDate(1);
		}
		return result;
		}
		finally 
		{
			if (rs!=null)
			{
				rs.close();
			}
		}
		
	}
	/**
	 * Inicializa la petición en el servidor de BD, creando las variables de sesión adecuadas,
	 * y realizando cualquier otro trabajo que se necesite.
	 * @param conn          Conexión a base de datos
	 * @param esquema       Esquema de base de datos en que se ha de ejecutar la petición
	 * @param peticion      Petición a ejecutar
	 * @param ip            Ip desde donde se ejecuta la petición
	 * @param nif           Nif de peticionario
	 * @param nombre        Nombre de peticionario
	 * @param certificado   Certificado de peticionario
	 * @return              Id de operación registrada en BD (REOW).
	 * @throws java.sql.SQLException
	 */
	private int inicializarPeticion(Connection conn, String esquema,
									 String peticion, String ip, String nif,
									 String nombre, String certificado) throws java.sql.SQLException
	{
		CallableStatement cs=null;
		//Recuperamos de la petición la cadena de llamada jdbc.
		String call = "{call transacs24x7.init_remote_response(?,?,?,?,?,?,?)}";
		cs = conn.prepareCall(call);
		cs.setString(1, esquema);
		cs.setString(2, peticion);
		cs.setString(3, ip);
		cs.setString(4,nif);
		cs.setString(5, nombre);
		cs.setString(6, certificado);
		cs.registerOutParameter(7, java.sql.Types.NUMERIC);
		cs.execute();
		//Recuperamos el id
		int idoperacion=cs.getInt(7);
		return idoperacion;
	}
	private StringBuffer recuperarRespuestaPeticion(Connection conn) throws java.sql.SQLException, java.io.IOException
	{
		CallableStatement cs=null;
		Clob cResult = null;
		StringBuffer sOutDB = new StringBuffer();
		//Recuperamos de la petición la cadena de llamada jdbc.
		String call = "{?= call transacs24x7.get_remote_response}";
		cs = conn.prepareCall(call);
		cs.registerOutParameter(1, java.sql.Types.CLOB);
		cs.execute();
		cResult = cs.getClob(1);
		if (!cs.wasNull())
		{
		    char[] clobType = new char[ (int)cResult.length() ];
		    Reader reader = cResult.getCharacterStream(); 
		    @SuppressWarnings("unused")
			int n = reader.read(clobType);
		    sOutDB.append(clobType);
		}
		return sOutDB;
	}
	private void finalizarPeticion(Connection conn, int idoperacion, String resultado) throws java.sql.SQLException
	{
		CallableStatement cs=null;
		//Finalizamos la llamada
		String call = "{call transacs24x7.end_remote_response (?,?)}";
		cs = conn.prepareCall(call);
		cs.setInt(1, idoperacion);
		cs.setString(2, resultado);
		cs.execute();
		return;
	}
	private CallableStatement montarLlamadaFuncion(PeticionWrapper peti,Connection conn, List<Clob> clobs) throws java.sql.SQLException
	{
		CallableStatement cs=null;
		ParametroWrapper param=null;
		tipoParametro tipo=null;
		StringBuilder seccDeclare= new StringBuilder("declare ");
		StringBuilder seccInicializacion = new StringBuilder();
		int nArrNum=0;
		int nArrCad=0;
		int nArrFec=0;
		String[] elementos; //Elementos pasados como array, en los parámetros de ese tipo.
		java.util.ArrayList<stpa.bd.helpers.ParametroWrapper> params=null;
		//Recuperamos de la petición la cadena de llamada jdbc.
		String call = peti.getJdbcCall();
		if (call!=null)
		{
			//Recuperamos los parámetros y registramos tipos
			params = peti.getParametros();
			//Lo primero, preparamos los parámetros de tipo Array. Esto hace que cada llamada
			//sea diferente porque el bloque anónimo lo será, así que la velocidad de proceso será menor. 
			//Debería hacerse únicamente cuando se sepa que hay parámetros de tipo array.
			for (int j=0;j<params.size();j++)
			{
				param = params.get(j);
				tipo = param.getTipo();
				if (tipo.equals(tipoParametro.ARRNUM))
				{
					if (param.getValor().indexOf(Constantes.separador)!=-1)
					{
						elementos = Utils.trimSeparadores(param.getValor()).split(Constantes.separador);
					}
					else
					{
						elementos = new String[1];
						elementos[0] = param.getValor();
					}
					if (elementos.length>0)
					{
						nArrNum++;
						seccDeclare.append(" n"+nArrNum+" tipo_entrada.t_nume_numeros;");
						for (int elem=0;elem<elementos.length;elem++)
						{
							seccInicializacion.append("n"+nArrNum+"("+elem+"):= "+elementos[elem].replace(',','.')+";");
						}
					}
				}
				else if (tipo.equals(tipoParametro.ARRCAD))
				{
					if (param.getValor().indexOf(Constantes.separador)!=-1)
					{
						elementos = Utils.trimSeparadores(param.getValor()).split(Constantes.separador);
					}
					else
					{
						elementos = new String[1];
						elementos[0] = param.getValor();
					}
					if (elementos.length>0)
					{
						nArrCad++;
						seccDeclare.append(" c"+nArrCad+" tipo_entrada.t_cade_cadenas;");
						for (int elem=0;elem<elementos.length;elem++)
						{
							seccInicializacion.append("c"+nArrCad+"("+elem+"):= '"+elementos[elem]+"';");
						}
					}
				}
				else if (tipo.equals(tipoParametro.ARRFEC))
				{
					if (param.getValor().indexOf(Constantes.separador)!=-1)
					{
						elementos = Utils.trimSeparadores(param.getValor()).split(Constantes.separador);
					}
					else
					{
						elementos = new String[1];
						elementos[0] = param.getValor();
					}
					if (elementos.length>0)
					{
						nArrFec++;
						seccDeclare.append(" f"+nArrFec+" tipo_entrada.t_fech_fechas;");
						for (int elem=0;elem<elementos.length;elem++)
						{
							seccInicializacion.append("f"+nArrFec+"("+elem+"):= to_date('"+elementos[elem]+"','"+param.getFormato()+"');");
						}
					}
				}
			}
			//cs = conn.prepareCall("{? = call " + call + "}");
			cs = conn.prepareCall(seccDeclare.toString()+ " begin "+ seccInicializacion.toString() + " ?:= " + call+"; end;");
			//Metadados de la base, para recuperar la información de driver
			DatabaseMetaData dbd = conn.getMetaData();
			//Versión de driver
			int versionDrv = dbd.getDriverMajorVersion();
			//TODO:Registro del tipo de retorno.
			cs.registerOutParameter(1, java.sql.Types.CLOB);
			//Ahora se tratan los parámetros simples, que se introducen por enlace y no por concatenación.
			//Esto puede mejorar la velocidad si no hubiera parámetros de tipo array, pero igualmente
			//deberían de separarse la lógica de tratar arrays de la que trata los parámetros cuando no los hay.
			for (int j=0;j<params.size();j++) //TODO: no tratar el último parámetro en llamada a procedimiento, que es de entrada/salida. Aún no se hace así.
			{
				//La j+2 se da porque el primer parámetro de salida será el resultado de la función.
				param=params.get(j);
				tipo=param.getTipo();
				if (tipo.equals(tipoParametro.VARCHAR2))
				{
					if (param.getValor()!=null)
					{
						if (param.getValor().length()<=4000)
						{
							cs.setString(j+2, param.getValor());
						}
						else //Aunque hayan dicho que es String, se pasará como CLOB
						{
							//CRUBENCVS** Creamos el CLOB a partir de la cadema, no de forma
							//implícita, sino directamente
							if (versionDrv>3)
							{
								StringReader rd = new StringReader(param.getValor());
								cs.setCharacterStream(j+2, rd,param.getValor().length());
							}
							else
							{
								cs.setObject(j+2,param.getValor(), java.sql.Types.CLOB);
							}
						}
					}
					else
					{
						cs.setNull(j+2,java.sql.Types.VARCHAR);
					}
				}
				else if (tipo.equals(tipoParametro.NUMBER)) // No se usa. No probado.
				{
					//cs.setDouble(j+2, Double.parseDouble(param.getValor()));
				}
				else if (tipo.equals(tipoParametro.DATE)) 
				{
					if (param.getValor()!=null)
					{
						cs.setDate(j+2, convertDateFromOracle (param.getValor(), param.getFormato(), conn));	
					}
					else
					{
						cs.setNull(j+1, java.sql.Types.DATE);
					}
					
				}
				else if (tipo.equals(tipoParametro.CLOB))
				{
					if (param.getValor()!=null)
					{
						//CRUBENCVS** Creamos el CLOB a partir de la cadema, no de forma
						//implícita, sino directamente
						if (versionDrv>3) //El driver BEA tiene esta versión, y no acepta de forma correcta el siguiente método
						{
							StringReader rd = new StringReader(param.getValor());
							cs.setCharacterStream(j+2, rd,param.getValor().length());
						}
						else
						{
							cs.setObject(j+2,param.getValor(), java.sql.Types.CLOB);
						}
					}
					else
					{
						cs.setNull(j+2,java.sql.Types.CLOB);
					}
				}
			}
			
		}
		return cs;
	}
	@SuppressWarnings("unchecked")
	public String lanzaPL (String sEsquema, String peticion, String sIP, String sNIF, String sNombre, String sCertificado) {
		Connection conn = null;
		CallableStatement cs = null;
		FileOutputStream myFO = null;
		boolean autoCommit;
		StringBuffer sOutDB = new StringBuffer();
		int idoperacion=0; //Id de registro realizado en la inicialización de la llamada remota.
					  //Se utilizará para actualizar el estado de la operación en la base de datos.
		List<Clob> arrClob=new ArrayList<Clob>(); //Para almacenar referencias a los CLOBs creados
		//y poder liberar su memoria posteriormente
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

			//Montar llamada a procedimiento o función almacenado en base de datos
			XMLParser xmlp = new XMLParser(peticion);
			cs = montarLlamadaFuncion (xmlp.getPeticion(),conn, arrClob);
			String peticionText = xmlp.getPeticion().toString();
			//Liberamos la referencia al objeto de petición, puede ser muy grande y no sabemos
			//cuanto tiempo tarda la llamada en base de datos, de esta forma el recolector
			//puede liberar su memoria inmediatamente.
			xmlp=null;
			if (cs==null)
			{
				stpa.utils.Log.TributasLogger log=new stpa.utils.Log.TributasLogger();
				log.error("No se ha podido montar la llamada");
				return "No ejecutado ok";
			}
			autoCommit=conn.getAutoCommit();
			conn.setAutoCommit(false);
			idoperacion = inicializarPeticion(conn,sEsquema, peticionText,sIP,sNIF,
											  sNombre, sCertificado);
			peticionText=null; //Eliminamos la referencia al texto de la petición, que sólo se 
			//debe haber utilizado para la llamada anterior. De esta manera la memoria utilizada por esa cadena 
			//se marca para liberar, ya que si la petición es grande, puede ocupar bastante memoria.
			cs.execute();

			sOutDB=recuperarRespuestaPeticion(conn);
			conn.commit();
			//Terminamos la petición. En esta conexión debería ir a la misma sesión.
			finalizarPeticion(conn,idoperacion,"OK"); //Se graba el resultado "OK"
			conn.setAutoCommit(autoCommit);
			// Enviar resultado de parametro de salida como respuesta al WebService
		} catch (SQLException e)
		{
			String err="";
			try
			{
				conn.rollback();
				finalizarPeticion(conn,idoperacion,e.getMessage());
			} catch (java.sql.SQLException ex)
			{
				err="No se ha podido realizar el rollback: "+ ex.getMessage();
			}
			
			stpa.utils.Log.TributasLogger log=new stpa.utils.Log.TributasLogger();
			err+=e.getMessage()+e.toString();
			log.error(err);
			log.printErrorStack(e.getStackTrace());
			return "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?><estructuras><error><![CDATA["+
            err
            +"]]></error></estructuras>";
			
		} catch (Exception e) {
			String err="";
			try
			{
				conn.rollback();
				finalizarPeticion(conn, idoperacion, e.getMessage());
			}
			catch (java.sql.SQLException ex)
			{
				err="No se ha podido realizar el rollback: "+ ex.getMessage();
			}
			stpa.utils.Log.TributasLogger log=new stpa.utils.Log.TributasLogger();
			err+=e.getMessage()+e.toString();
			log.error(err);
			log.printErrorStack(e.getStackTrace());
			return "No ejecutado ok";
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
			if (arrClob.size()>0)
			{
				for (Clob c: arrClob)
				{
					try
					{
						c.free();
					}
					catch (SQLException e)
					{
						
					}
				}
			}
			
		}
		//if (cResult == null) {
		if (sOutDB.length()==0) {
			return "No ejecutado ok";
		} else {
			return sOutDB.toString();
		}
		
	}


}
