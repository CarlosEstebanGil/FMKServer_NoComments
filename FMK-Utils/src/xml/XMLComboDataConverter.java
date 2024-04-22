/* No hacia falta!

<root>

	<summary />
	
	<qry>	
		<item label="1" data= "Movistar" order="2" />
		<item label="1" data= "Personal" order="1" />
		<item label="1" data= "Claro" 	 order="3" /> //
	</qry>

</root>



?svc=svcGetComboData
&idFieldName=Dni
&valueFieldName=nombre
&tabla=Personas
&orderField=order /ó null -> le paso el rs de esto al converter normal y deberia funcar con 
				los "as" (label y data) y el campo extra.
				( si label y data cambian x otra cosa, no hace falta recompilar)
				( si necesito 2 versiones ent defino 2 servicios similares y 
				  listo ). 

el svc deberá implementarse para que el select haga as "label" y as data 
directamente en la conf services.properties (en el querie sql) y listo 
además que el sql haga select * 

 Ent comento ALL :
 
import java.sql.ResultSetMetaData;

import org.apache.log4j.Logger;

//
// * Extiende XMLConverter, sobreEscribe unicamente los metodos getColumnsNames y getColumnsCount que utiliza
// * el método convert para armar el xml ( el de la XMLConverter class , toma rs.getMetadata y arma todo segun 
// * viene en el rs desde el query from DB , 
// * Este en cambio, utiliza solo 2 constantes ,por ahora seteadas a label y a data ( q son el id y el value , 
// * nombres de propiedades que espera flex en un xml para su com
// * @author Carlos esteban Gil 
// *
// 
public class XMLComboDataConverter extends XMLConverter {
	static final Logger logger = Logger.getLogger(XMLComboDataConverter.class);
	
	static protected int DEFAULT_COLUMNS_COUNT   = 2; // pueden venir mas ej order o nMas xero si o si las 1eras 2 tomo como id y value.
	
	static protected String DEFAULT_ID_CMB_PROP   	= "label"; //default flex values
	static protected String DEFAULT_VALUE_CMB_PROP  = "data";  // for comboBox comp.
	
	private String idPropName= DEFAULT_ID_CMB_PROP; //with Default initialization
	private String valuePropName= DEFAULT_VALUE_CMB_PROP; //getters y setters abajo..
		
	//20140521: utilizo object param x si alguna clase necesita recibir algo y q lo castee a lo q quiera
	//			( lo ideal seria utilizar una interfaz generica de lo q se recibe y usar sus metodos sin cast y DI )
			
		public String[] getColumnsNames(Object param) throws Exception {
				try {
					//ResultSetMetaData md = rs.getMetaData();
					
					int cols = getColumnsCount(null);
					
					String tag[] = new String[cols];
					
					//for (int i = 0; i < tag.length; i++) {
					//	tag[i] = md.getColumnName(i+1); //.toLowerCase();
					//}
					tag[0]=getIdPropName();
					tag[1]=getValuePropName();
					
					return tag;
					
				} catch (Exception e) {
					logger.fatal(e.getMessage());
					throw new Exception( e.getMessage());
				}
			}
			
		//20140521: utilizo object param x si alguna clase necesita recibir algo y q lo castee a lo q quiera
		//			( lo ideal seria utilizar una interfaz generica de lo q se recibe y usar sus metodos sin cast y DI )
		public int getColumnsCount(Object param) throws Exception {
				try {
					return DEFAULT_COLUMNS_COUNT;
				} catch (Exception e) {
					logger.fatal(e.getMessage());
					throw new Exception( e.getMessage());
				}
			}
			
		//override next functions if needed:
		public int getIdPropName(Object param) throws Exception {
			try {
				ResultSetMetaData md = rs.getMetaData();
				return md.getColumnCount();
			} catch (Exception e) {
				logger.fatal(e.getMessage());
				throw new Exception( e.getMessage());
			}
		}
		//func getIdPropName -> devuelve "label" y getValuePropName -> devuelve "data" ( label y data Ktes de la clase.
		//ási se pueden redefinir o cambiar.. tmb q se puedan recibir esos kstes string como param en las funcs
		//taria weno xa ni obligar a herencia y new clasess.. 
		
		
		//Getters & Setters .. :
		
		public String getIdPropName() {
			return idPropName;
		}

		public void setIdPropName(String idPropName) {
			this.idPropName = idPropName;
		}

		public String getValuePropName() {
			return valuePropName;
		}

		public void setValuePropName(String valuePropName) {
			this.valuePropName = valuePropName;
		}
}
*/