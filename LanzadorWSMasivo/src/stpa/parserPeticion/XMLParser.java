/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package stpa.parserPeticion;
import java.io.StringReader;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import stpa.bd.helpers.ParametroWrapper;
import stpa.bd.helpers.PeticionWrapper;
import stpa.bd.helpers.ParametroWrapper.tipoParametro;
/**
 *
 * @author CarlosRuben
 */
public class XMLParser {
    private PeticionWrapper peticion=new PeticionWrapper();
	
	
    private boolean hasInvalidChars (String procedimiento)
    {
    	if (procedimiento.indexOf(';')!=-1) // Para evitar inyecci�n de SQL
		{
			return true;
		}	
    	else
    	{
    		return false;
    	}
    }
	/**
	 * Crea un nuevo objeto XMLParser que contendr� una referencia a la petici�n ya preparada.
	 * @param pXml
	 * @throws stpa.exceptions.PeticionParseException Cuando no se puede interpretar el XML. En el atributo "cause" estar� la raz�n original.
	 */
	public XMLParser (String pXml) throws stpa.exceptions.PeticionParseException
	{
		try {
			XMLEventReader reader = getReader(pXml);
			peticion=procesaPeticion(reader);
			
		} catch (XMLStreamException e) {
			throw new stpa.exceptions.PeticionParseException ("Excepci�n al procesar la petici�n:"+ e.getMessage(),e);
		}
	}
	/**
	 * Recupera el objeto {@link stpa.bd.helpers.PeticionWrapper} que encapsula los datos de la petici�n que se recibe.
	 * @return
	 */
	public PeticionWrapper getPeticion()
	{
		return peticion;
	}
	/**
	 * Recupera un lector de XML en base a la petici�n.
	 * @param peticion Texto de xml de petici�n.
	 * @return Lector javax.xml.stream.XMLEventReader sobre la petici�n.
	 * @throws XMLStreamException
	 */

	private XMLEventReader getReader(String peticion) throws XMLStreamException
	{
		XMLInputFactory factory=XMLInputFactory.newInstance();
		StringReader sreader = new StringReader(peticion);
		XMLEventReader reader=null;
		reader=factory.createXMLEventReader(sreader);
		return reader;
	}
	/**
	 * Procesa cada par�metro de la petici�n.
	 * @param reader {@link javax.xml.stream.XMLEventReader} que lee el texto de la petici�n.
	 * @param first Indicador de si es o no el primer par�metro.
	 * @param param {@link stpa.bd.helpers.ParametroWrapper}. Encapsulador de par�metros, a la salida contendr� los datos del mismo.
	 * @return
	 * @throws stpa.exceptions.PeticionParseException
	 */
	private String procesarParametro(XMLEventReader reader, boolean first,ParametroWrapper param) throws stpa.exceptions.PeticionParseException
    {
        StringBuilder sbres = new StringBuilder();
        final String cCadena="1";
        final String cNumero="2";
        final String cFecha="3";
        final String cClob="4";
        //CRUBENCVS ** Nuevos tipos para paso de valores de array
        final String cArrNum ="5";
        final String cArrCad="6";
        final String cArrFec="7";
        int nArrNum=0; //Contador de arrays num�ricos
		int nArrCad=0; //Contador de arrays de cadenas
		int nArrFec=0; // Contador de arrays de fechas
        //Recuperamos el "valor".
        //Vamos hasta el final, y despu�s reseteamos.
        String tipo = null; //Tipo de par�metro. En base a "tipo" lo parsearemos de una forma u otra.
        String valor = null;
        String formato = null;
        XMLEvent evento = null;
        try
        {
        evento=reader.peek();
        while (!evento.isEndDocument() && !(evento.isEndElement() && evento.asEndElement().getName().getLocalPart().equalsIgnoreCase("param")))
        {
            evento=reader.nextEvent();
            if (evento.isStartElement() && evento.asStartElement().getName().getLocalPart().equalsIgnoreCase("valor")) {
                evento=reader.nextEvent(); //Cierre de valor
                //Esto tiene que ser el texto, no se permiten etiquetas inclu�da
                if (evento.isCharacters())
                {
                    valor=evento.asCharacters().getData();
                }
                else
                {
                    if (evento.isEndElement() && evento.asEndElement().getName().getLocalPart().equalsIgnoreCase("valor"))
                    {
                    	valor=null;
                    }
                    else
                    {
                    	throw new stpa.exceptions.PeticionParseException ("El formato de Xml no es correcto.");
                    }
                }
            }
            if (evento.isStartElement() && evento.asStartElement().getName().getLocalPart().equalsIgnoreCase("tipo")) {
                evento=reader.nextEvent();
                //Esto tiene que ser el texto, no se permiten etiquetas inclu�da
                if (evento.isCharacters())
                {
                    tipo=evento.asCharacters().getData();
                }
                else
                {
                    if (evento.isEndElement() && evento.asEndElement().getName().getLocalPart().equalsIgnoreCase("tipo"))
                    {
                    	tipo=null;
                    }
                    else
                    {
                    	throw new stpa.exceptions.PeticionParseException ("El formato de Xml no es correcto.");	
                    }
                }
            }
            if (evento.isStartElement() && evento.asStartElement().getName().getLocalPart().equalsIgnoreCase("formato")) {
                evento=reader.nextEvent();
                //
                //Formato puede tener o no un valor, e incluso puede no aparecer.
                if (evento.isCharacters())
                {
                    formato=evento.asCharacters().getData();
                }
            }
        }
        //Montamos la cadena
        if (!first)
        {
            sbres.append(",");//Para todos los par�metros que no sean el primero, se pone una comilla delante.
        }
        if (tipo.equalsIgnoreCase(cCadena) )
        {
        	sbres.append("?");
        	param.setValor(valor);
        	param.setTipo(tipoParametro.VARCHAR2);
        }
        else if (tipo.equalsIgnoreCase(cNumero))
        {
        	sbres.append("?");
        	if (valor!=null)
        	{
        		param.setValor(valor.replace(',', '.'));
        	}
        	else
        	{
        		param.setValor(valor);
        	}
            param.setTipo(tipoParametro.VARCHAR2);
        }
        else if (tipo.equalsIgnoreCase(cFecha))
        {
        	sbres.append("?");
        	if (valor!=null)
        	{
        		param.setTipo(tipoParametro.DATE);
        	}
        	else
        	{
        		param.setTipo(tipoParametro.VARCHAR2);//No permite introducir un valor nulo si se especifica una fecha.
        	}
            param.setValor(valor);
            param.setFormato(formato);
        }
        else if (tipo.equalsIgnoreCase(cClob))
        {
        	sbres.append("?");
        	param.setValor(valor);
        	param.setTipo(tipoParametro.CLOB);
        }
        else if (tipo.equalsIgnoreCase (cArrNum))
        {
        	nArrNum++;
        	sbres.append("n"+nArrNum);
        	param.setValor(valor);
        	param.setTipo(tipoParametro.ARRNUM);
        }
        else if (tipo.equalsIgnoreCase(cArrCad))
        {
        	nArrCad++;
        	sbres.append("c"+nArrCad);
        	param.setValor(valor);
        	param.setTipo(tipoParametro.ARRCAD);
        }
        else if (tipo.equalsIgnoreCase(cArrFec))
        {
        	nArrFec++;
        	sbres.append("f"+nArrFec);
        	param.setValor(valor);
        	param.setTipo(tipoParametro.ARRFEC);
        	param.setFormato(formato);
        }
        }
        catch (XMLStreamException ex)
        {
        	throw new stpa.exceptions.PeticionParseException("No se puede interpretar la petici�n :"+ ex.getMessage(),ex);
        }
        return sbres.toString();

    }
	/**
	 * Procesa la petici�n a base de datos, para dejarlo en un objeto PeticionWrapper.
	 * @param reader {@link javax.xml.stream.XMLEventReader} con que se leer� la petici�n.
	 * @return Objeto {@link stpa.bd.helpers.PeticionWrapper} que contendr� los datos de la petici�n preparados para montar la llamada a base de datos.
	 * @throws stpa.exceptions.PeticionParseException
	 */
	private PeticionWrapper procesaPeticion(XMLEventReader reader) throws stpa.exceptions.PeticionParseException
	{
        StringBuilder callB=new StringBuilder();
        XMLEvent event=null;
        boolean first=true; //Primer elemento tratado.
        boolean hasParameters=false; //Si tiene par�metros o la llamada es s�lo el nombre del procedimiento.
        String nombreProc="";
		PeticionWrapper peti=new  PeticionWrapper();
		java.util.ArrayList<ParametroWrapper> listaP=new java.util.ArrayList<ParametroWrapper>();
		ParametroWrapper param=null;
		//Procesamos.
		//Nombre del procedimiento.
		try
		{
		 while (reader.hasNext()) {
             event = reader.nextEvent();
             if (event.isStartElement()) {
                 if (event.asStartElement().getName().getLocalPart().equalsIgnoreCase("proc"))
                 {
                     Attribute myat;
                     Iterator<Attribute> myIt = event.asStartElement().getAttributes();
                     if (myIt.hasNext())
                     {
                         myat=myIt.next();
                         if (myat.getName().getLocalPart().equalsIgnoreCase("nombre"))
                         {
                        	 nombreProc=myat.getValue();
                             callB.append(nombreProc);
                             if (hasInvalidChars(nombreProc))
                     		{
                     			throw new stpa.exceptions.PeticionParseException ("Se han encontrado caracteres inv�lidos.",null);
                     		}
                         }
                     }
                 }
                 //Tratamos los parametros.
                 if (event.asStartElement().getName().getLocalPart().equalsIgnoreCase("param"))
                 {
                     if (!hasParameters)
                     {
                         hasParameters=true;
                         callB.append("(");
                     }
                     param=new ParametroWrapper();
                     callB.append(procesarParametro(reader,first,param));
                     //Incluimos el param en la lista de par�metros.
                     listaP.add(param);
                     if (first)
                     {
                         first=false;
                     }
                 }
             }
         }
		if (hasParameters)
		{
			callB.append(")");
		}
        peti.setProcName(nombreProc);
        peti.setParametros(listaP);
        peti.setJdbcCall(callB.toString());
		}
		catch (XMLStreamException ex)
		{
			throw new stpa.exceptions.PeticionParseException ("No se puede interpretar la petici�n:" + ex.getMessage(),ex);
		}
		return peti;
	}
}
