/**
 * 
 */
package stpa.bd.helpers;

/**
 * @author crubencvs
 * Encapsula los datos de un parámetro de la petición.
 */
public class ParametroWrapper {
	public enum tipoParametro {VARCHAR2,NUMBER,DATE,CLOB,ARRNUM,ARRCAD,ARRFEC};
	private String valor;
	private tipoParametro tipo;
	private String formato;
	/**
	 * @return the valor
	 */
	public String getValor() {
		return valor;
	}
	/**
	 * @param valor the valor to set
	 */
	public void setValor(String valor) {
		this.valor = valor;
	}
	/**
	 * @return the tipo
	 */
	public tipoParametro getTipo() {
		return tipo;
	}
	/**
	 * @param tipo the tipo to set
	 */
	public void setTipo(tipoParametro tipo) {
		this.tipo = tipo;
	}
	/**
	 * @return the formato
	 */
	public String getFormato() {
		return formato;
	}
	/**
	 * @param formato the formato to set
	 */
	public void setFormato(String formato) {
		this.formato = formato;
	}
	
}
