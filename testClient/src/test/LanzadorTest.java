package test;


import javax.jws.*;


@WebService
public class LanzadorTest {
	
	private stpa.services.LanzaPL_PortType lanzaderaPort;
	private stpa.services.LanzaPL_Service lanzaderaWS;
	
	@WebMethod
	public String callLanzador() throws Exception{


		lanzaderaWS = new stpa.services.LanzaPL_Service_Impl();
		lanzaderaPort = lanzaderaWS.getLanzaPLSoapPort();

		//Cambiamos endpoint
		//javax.xml.ws.BindingProvider bpr=null;
		//bpr = (javax.xml.ws.BindingProvider) lanzaderaPort; // enlazador de protocolo para el servicio.
		//bpr.getRequestContext().put (javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://bus.desa.epst.pa:7001/LanzadorWS/lanzaPLService"); // Cambiamos el endpoint

		return lanzaderaPort.executePL(
				"PSEUDOREAL",
				"<peti><proc nombre=\"INTERNET_DOCUMENTOS.altadocumento\"><param id=\"1\"><valor>M</valor><tipo>1</tipo><formato/> 	</param><param id=\"2\"><valor>0466900185837</valor><tipo>1</tipo><formato/></param> 	<param id=\"3\"><valor>1144A750FB572B4F</valor><tipo>1</tipo><formato/></param> 	<param id=\"4\"><valor>55555555K</valor><tipo>1</tipo><formato/></param> 	<param id=\"5\"><valor>55555555K</valor><tipo>1</tipo><formato/></param> 	<param id=\"6\"><valor>1121</valor><tipo>1</tipo><formato/></param> 	<param id=\"7\"><valor>P</valor><tipo>1</tipo><formato/></param> 	<param id=\"8\"><valor>1121</valor><tipo>1</tipo><formato/></param> 	<param id=\"9\"><valor>S</valor><tipo>1</tipo><formato/></param> 	<param id=\"10\"><valor>Documento</valor><tipo>1</tipo><formato></formato></param> 	<param id=\"11\"><valor>P</valor><tipo>1</tipo><formato/></param> 	</proc></peti> ",
				"", "", "", "");
		
	}
	
	@WebMethod
	public void callLanzadorRecursive(int iVeces) {

		int x=0;
		try {
			for (int i=0; i< iVeces; i++) {
				this.callLanzador();
				x++;
			}
		}
		catch (Exception e) {
			System.out.println("**** error en ejecución #" + x);
			e.printStackTrace();
		}
	}
		
}