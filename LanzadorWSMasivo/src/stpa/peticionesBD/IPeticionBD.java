/**
 * 
 */
package stpa.peticionesBD;

/**
 * @author crubencvs
 * Interfaz principal para las peticiones contra la base de datos.
 */
public interface IPeticionBD {
	public String executePL(String sEsquema, String sPeticion, String sIP, String sNIF,
			String sNombre, String sCertificado);
}
