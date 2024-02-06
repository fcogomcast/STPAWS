package stpa.services;

/**
 * Generated interface, please do not edit.
 * Date: [Mon Jun 15 11:15:49 CEST 2015]
 */

public interface lanzaPLPortType extends java.rmi.Remote {

  /**
   * Web Method: executePL ...
   */
  java.lang.String executePL(java.lang.String esquema,java.lang.String peticion,java.lang.String IP,java.lang.String NIF,java.lang.String nombre,java.lang.String certificado)
      throws java.rmi.RemoteException;
}
