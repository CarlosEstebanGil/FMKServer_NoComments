/*package xml;

//NEW: 20140623 : estar�a al pedo, xq no quiero q a�ada nada nuevo xq asi
// ref en abstract jerarq con IConverter xa todo convcerter, la i es una sola y generica xa todo converter y 
// lo que cambian son las distintas implementaciones de la misma (xmlConverter,FileCOnverter) etc..!!!!!!!!!!!

import java.io.PrintWriter;
import java.sql.ResultSet;

import javax.servlet.jsp.JspWriter;

import output.IConverter;
import configuration.SingletonServicesConfiguration; //
//TODO tal vez esta clase de utilidad de convertir un rs a xml no deberia conocer el out y escribir en 
// el directamente hacia el cliente sino devolver un xml (sb.ad .ad .. etc) devolver el sb o si no hay
// limite xa string ent el sb.toString. luego el write response del servlet deberia ser quien le envi� la rta 
// al cli ( q escriba el sb.toString() en el out ) y si el xml fall� y le aroj� exception el servlet (en su execute())
// q llam� al xmlconcerter o el algorithm o como sea es el q catchea y verifica si es runtime ent arma err 
// xml del time fatal al user (x q 1ero catchea especializaciones, luego la mas generica de runtime y x ultimo x ahi
// la exception gral gral. ent sabe que es runtime en el catch (runtimeexception re) y arma ese xml fatal 
// comuniquese con el op, en cambio para las catched ( de app o recuperables ) un err con un msge claro tmb 
// pero en vez de "fatal:llame al proveedor" seria algo bien claro para q pueda recuperarse del error!!!!!!!! 
public interface IXMLConverter extends IConverter { 


 	public abstract void setConfKey( String k);
	public abstract void setRecordSet( ResultSet r);
	
	//Version original:
	public abstract void convert(String itemTag, PrintWriter o) throws Exception; //JspWriter o) throws Exception;
	
	//Version no usada: esta version ser�a la mas simple o tal vez una que reciba una salida gen�rica
	// y no necesariamente un printWriter sino una interfaz comun de algo de salida q cumpla .print(strxxx)
	//public abstract void convert(PrintWriter o) throws Exception; //JspWriter o) throws Exception;
	
	//version del metodo a implementar e invocar/usar si se necesita ir a buscar el tag e itemtag a una conf
	//requiere como paramtero la conf y los datos xa obtener tag e item tag de la misma osea el strsvcname y 
	//el strsvcName de conf global x default , su implementaci�n los obtendr� de ah�. y luego invocar� (reusar�)
	//la version mas simple y original q exist�a que es: convert(str itemTag, printwriter out)
	public abstract void convert(PrintWriter o, String svcName, String strDefaultConfSvcName, SingletonServicesConfiguration svcConfs,Boolean result, Boolean error, String info  ) throws Exception;
	
	// A dif de la anterior, esta version no conoce de conf en properties, requiere entonces el tag y el itemtag ya
	// averiguados/conocidos previamente:
	public abstract void convert(PrintWriter o, String tag, String itemTag, Boolean result, Boolean error, String info  ) throws Exception;

} */