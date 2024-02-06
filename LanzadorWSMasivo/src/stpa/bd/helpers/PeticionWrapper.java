/**
 * 
 */
package stpa.bd.helpers;


import stpa.bd.helpers.ParametroWrapper.tipoParametro;
import stpa.comun.Constantes;
import stpa.comun.Utils;

/**
 * @author crubencvs
 * Encapsula los datos de una petición. 
 */
public class PeticionWrapper {
	private String jdbcCall;
	private String procName;
	private java.util.ArrayList<ParametroWrapper> parametros;
	/**
	 * @return the jdbcCall
	 */
	public String getJdbcCall() {
		return jdbcCall;
	}
	/**
	 * @param jdbcCall the jdbcCall to set
	 */
	public void setJdbcCall(String jdbcCall) {
		this.jdbcCall = jdbcCall;
	}
	/**
	 * @return the parametros
	 */
	public java.util.ArrayList<ParametroWrapper> getParametros() {
		return parametros;
	}
	/**
	 * @param parametros the parametros to set
	 */
	public void setParametros(java.util.ArrayList<ParametroWrapper> parametros) {
		this.parametros = parametros;
	}
	
	/**
	 * Devuelve una representación en cadena de la petición.
	 */
	public String toString()
	{
		StringBuilder sb;
		ParametroWrapper param=null;
		tipoParametro tipo=null;
		StringBuilder seccDeclare= new StringBuilder("declare s VARCHAR2(32000);");
		StringBuilder seccInicializacion = new StringBuilder();
		int nArrNum=0;
		int nArrCad=0;
		int nArrFec=0;
		String[] elementos; //Elementos del array que se pasa por parámetro.
		java.util.ArrayList<stpa.bd.helpers.ParametroWrapper> params=null;
		//Recuperamos de la petición la cadena de llamada jdbc.
		String call = this.jdbcCall;
		sb=new StringBuilder();
		if (call!=null)
		{
			params = this.parametros;
			//Recorremos los parámetros, construyendo la cadena de llamada
			//en función del tipo de cada uno de ellos.
			sb.append(this.procName);
			if (params.size()>0)
			{
				sb.append ("(");
			}
			for (int i=0;i<params.size();i++)
			{
				//Recuperamos el parámetro en posición
				param=params.get(i);
				if (param!=null)
				{
					tipo = param.getTipo();
				}
				if (tipo.equals(tipoParametro.VARCHAR2))
				{
					sb.append("'"+param.getValor()+"'");
				}
				else if (tipo.equals(tipoParametro.NUMBER)) // No se usa. No probado.
				{
					sb.append(param.getValor());
				}
				else if (tipo.equals(tipoParametro.DATE)) 
				{
					sb.append("to_date('"+param.getValor()+"','"+param.getFormato()+"')");
				}
				else if (tipo.equals(tipoParametro.CLOB))
				{
					sb.append ("'"+param.getValor()+"'");
				}
				else if (tipo.equals (tipoParametro.ARRNUM))
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
						seccDeclare.append(" n" + nArrNum + " tipo_entrada.t_nume_numeros;");
						for (int elem=0;elem<elementos.length;elem++)
						{
							seccInicializacion.append("n"+nArrNum+"("+elem+"):= "+elementos[elem].replace(',','.')+";");
						}
						sb.append("n"+nArrNum);
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
						sb.append("c"+nArrCad);
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
						seccDeclare.append(" f"+nArrFec+" tipo_entrada.t_cade_cadenas;");
						for (int elem=0;elem<elementos.length;elem++)
						{
							seccInicializacion.append("f"+nArrFec+"("+elem+"):= to_date('"+elementos[elem]+"','"+param.getFormato()+"');");
						}
						sb.append("f"+nArrFec);
					}
				}
				if (i<params.size()-1) //Para todos los parámetros, menos el último, concatenamos una comilla.
				{
					sb.append (",");
				}
			}
			if (params.size()>0)
			{
				sb.append(")");
			}
		}
		return sb.toString();
	}
	/**
	 * @return the procName
	 */
	public String getProcName() {
		return procName;
	}
	/**
	 * @param procName the procName to set
	 */
	public void setProcName(String procName) {
		this.procName = procName;
	}
	
	
}
