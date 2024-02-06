/**
 * 
 */
package stpa.peticionesBD;

//import stpa.utils.UtilsDB;
import stpa.utils.UtilsDB2;

/**
 * @author crubencvs
 *
 */
public class PeticionMasiva implements IPeticionBD {

	/* (non-Javadoc)
	 * @see stpa.impl.IPeticionBD#executePL(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	/**
	 * Trata la petición que llega al servicio web, y después llama a la función de utilidad que la
	 * ejecutará sin tratarla de nuevo.
	 * 
	 */
	@Override
	public String executePL(String sEsquema, String sPeticion, String sIP, String sNIF,
			String sNombre, String sCertificado) {
			UtilsDB2 util= new UtilsDB2();
			return util.lanzaPL(sEsquema, sPeticion, sIP, sNIF, sNombre, sCertificado);
	}

}
