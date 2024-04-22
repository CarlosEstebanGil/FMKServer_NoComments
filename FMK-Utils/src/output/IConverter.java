package output;


import java.io.PrintWriter;
import java.sql.ResultSet;

import javax.servlet.jsp.JspWriter;

import configuration.SingletonServicesConfiguration;
/*TODO tal vez esta clase de utilidad de convertir un rs a xml no deberia conocer el out y escribir en 
// el directamente hacia el cliente sino devolver un xml (sb.ad .ad .. etc) devolver el sb o si no hay
// limite xa string ent el sb.toString. luego el write response del servlet deberia ser quien le envié la rta 
// al cli ( q escriba el sb.toString() en el out ) y si el xml falló y le arojó exception el servlet (en su execute())
// q llamó al xmlconcerter o el algorithm o como sea es el q catchea y verifica si es runtime ent arma err 
// xml del time fatal al user (x q 1ero catchea especializaciones, luego la mas generica de runtime y x ultimo x ahi
// la exception gral gral. ent sabe que es runtime en el catch (runtimeexception re) y arma ese xml fatal 
// comuniquese con el op, en cambio para las catched ( de app o recuperables ) un err con un msge claro tmb 
// pero en vez de "fatal:llame al proveedor" seria algo bien claro para q pueda recuperarse del error!!!!!!!! */
public interface IConverter { //

	public abstract void setConfKey( String k);
	public abstract void setRecordSet( ResultSet r);
	
	//Version original:
	public abstract void convert(String itemTag, PrintWriter o) throws Exception; //JspWriter o) throws Exception;
	
	//Version no usada: esta version sería la mas simple o tal vez una que reciba una salida genérica
	// y no necesariamente un printWriter sino una interfaz comun de algo de salida q cumpla .print(strxxx)
	//public abstract void convert(PrintWriter o) throws Exception; //JspWriter o) throws Exception;
	
	//version del metodo a implementar e invocar/usar si se necesita ir a buscar el tag e itemtag a una conf
	//requiere como paramtero la conf y los datos xa obtener tag e item tag de la misma osea el strsvcname y 
	//el strsvcName de conf global x default , su implementación los obtendrá de ahí. y luego invocará (reusará)
	//la version mas simple y original q existía que es: convert(str itemTag, printwriter out)
	public abstract void convert(PrintWriter o, String svcName, String strDefaultConfSvcName, SingletonServicesConfiguration svcConfs,Boolean result, Boolean error, String info  ) throws Exception;
	
	// A dif de la anterior, esta version no conoce de conf en properties, requiere entonces el tag y el itemtag ya
	// averiguados/conocidos previamente:
	public abstract void convert(PrintWriter o, String tag, String itemTag, Boolean result, Boolean error, String info  ) throws Exception;
}