package stpa.comun;

public class Utils {
	/**
	 * Elimina separadores de los lados de la cadena de entrada. <b>Se supone un separador formado
	 * por un único caracter</b> 
	 * @param cadena Cadena con separadores de elementos de tipo array
	 * @return Cadena sin separadores a los lados.
	 */
	static public String trimSeparadores(String cadena)
	{
		String modificada=cadena;
		if (modificada.startsWith(Constantes.separador)) //Primer elemento separador, se elimina.
		{
			modificada = modificada.substring(1);
		}
		if (modificada.endsWith(Constantes.separador)) //Último elemento separador
		{
			modificada = modificada.substring(0, modificada.length()-1);
		}
		return modificada;
	}
}
