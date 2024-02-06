package stpa.services;

import javax.jws.*;

import stpa.peticionesBD.IPeticionBD;
import stpa.peticionesBD.SelectorPeticion;


@WebService (serviceName="LanzaPLMasivo")
@HandlerChain (file="LogMessageHandler.xml",name="ChainLanzadorMasivo")
public class LanzaPLMasivo {

	@WebMethod
	public String executePL(@WebParam (name="esquema")java.lang.String esquema,
			@WebParam (name="peticion")java.lang.String peticion, 
			@WebParam (name="IP")java.lang.String IP, 
			@WebParam (name="NIF")java.lang.String NIF,
			@WebParam (name="nombre")java.lang.String nombre, 
			@WebParam (name="certificado")java.lang.String certificado) {
		
		SelectorPeticion sel = new SelectorPeticion();
		try
		{
			IPeticionBD pet = sel.selecciona(stpa.comun.Constantes.ModosFuncion.MASIVO);//Ahora es fijo, en el futuro podrán ser otros.
			return pet.executePL(esquema, peticion, IP, NIF, nombre, certificado);
		}
		catch (stpa.exceptions.SelectorPeticionException ex)
		{
			return "Error en la ejecución del servicio:" + ex.getMessage();
		}		
		
	}
}