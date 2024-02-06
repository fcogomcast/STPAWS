package stpa.services;

/**
 * Generated interface, please do not edit.
 * Date: [Mon Jun 15 11:15:49 CEST 2015]
 */

public interface lanzaPLPortType extends java.rmi.Remote {

  /**
   * Web Method: executePL ...
   */
  String executePL(String esquema, String peticion, String IP, String NIF, String nombre, String certificado)
      throws java.rmi.RemoteException;
}
