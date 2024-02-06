/**
 * 
 */
package stpa.peticionesBD;

import stpa.comun.Constantes;
import stpa.exceptions.SelectorPeticionException;

/**
 * @author crubencvs
 * Clase que en función del parámetros de tratamiento de XML selecciona un gestor de  petición u otro.
 */
public class SelectorPeticion {
	public IPeticionBD selecciona(Constantes.ModosFuncion modo) throws SelectorPeticionException
	{
		IPeticionBD pet=null;
		if (modo.equals(Constantes.ModosFuncion.NORMAL))
		{
			//
		}
		else if (modo.equals(Constantes.ModosFuncion.MASIVO))
		{
			pet = new PeticionMasiva();
		}
		else
		{
			throw new SelectorPeticionException ("Parámetro de tratamiento de petición Inválido");
		}
		return pet;
	}
}
