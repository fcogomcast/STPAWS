package stpa.services;

import javax.jws.*;

import stpa.utils.utilsDB;

@WebService (serviceName="lanzaPL")
@HandlerChain (file="LogMessageHandler.xml",name="ChainLanzador") //Más vale que se ponga el nombre, si no, no genera correctamente las clases
public class lanzaPL {

	@WebMethod
	public String executePL(@WebParam(name="esquema")java.lang.String esquema,
	@WebParam (name="peticion")java.lang.String peticion, 
	@WebParam (name="IP")java.lang.String IP, 
	@WebParam (name="NIF")java.lang.String NIF,
	@WebParam (name="nombre")java.lang.String nombre, 
	@WebParam (name="certificado")java.lang.String certificado) {
		return utilsDB.lanzaPL(esquema, peticion, IP, NIF, nombre, certificado);
	}
}