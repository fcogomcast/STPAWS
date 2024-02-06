/**
 * 
 */
package stpa.peticionesBD;

import stpa.comun.Constantes;
import stpa.exceptions.SelectorPeticionException;

/**
 * @author crubencvs
 * Clase que en funci�n del par�metros de tratamiento de XML selecciona un gestor de  petici�n u otro.
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
			throw new SelectorPeticionException ("Par�metro de tratamiento de petici�n Inv�lido");
		}
		return pet;
	}
}
