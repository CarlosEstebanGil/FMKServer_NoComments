package business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import configuration.SingletonApplicationConfiguration;

//podria tener la query dinamica con parametros http? o de donde sale sino lo variable? o es todo kte?
//si fuese var y x http ent podria heredar de abstract db  service y sobreescribir como todas el execution 
//algorithm
public abstract class AbstractHDFileGenerator { //hacer q el os sea generico asi sirve xa hd como http resp..!!!

//	static final String FILE_PREFIX = 	 "rec_";  //todo tomarlo del properties como svc no jdni
//	static final String FILE_EXTENSION = ".txt";  

	//  LO PONGO EN INIT(LLAMADO X EL EXecute,xq si un bean q herada esto es creado auto al ppio x container jboss
	//  y es creado antes de q se levanten / existan estos recursos ent falla. )
	//  
	// static final String FILE_PREFIX  = SingletonApplicationConfiguration.getInstance().getPropValue("Application.AbstractHDFileGenerator.FILE_PREFIX");
	// static final String FILE_EXTENSION =  SingletonApplicationConfiguration.getInstance().getPropValue("Application.AbstractHDFileGenerator.FILE_EXTENSION");
	//  
	//las ktes estan solo x comodidad, solo deben ser usadas en el algorimo o execute (no sueltas).
	private String FILE_PREFIX=""; //x si <>s instancias de una clase de impl concreta (bean) xero<>s inst necesitan <>	
	private String FILE_EXTENSION = ""; //  (y las levanto en init llamdo x execute(metodode instancia no autoexecuted)

	private String FILE_PATH = "";
	
	//Necesito: un objecto archivo 
	//una ruta de destino xa el archivo
	//una connexion a db 
	//un string buffer xa la consulta (me la arma julio, dinamicamente (?) (ES TODA FIJA, SIN PARAMETROS!)
	//un strSql xa almacenar sb.toString								   (= se podria levantar del prop)
	//un ps xa ejecutar la consulta (el strSql)
	//un rs xa almacenar el resultado 
	//una rutina generica xa recorrer el rs y tratar cada registro ( n campos , n reglas, parte variable)
	
	//una func tratarRegistro(rs) q reciba el rs y q 1 a 1 tome sus columnas (no dinamico) getCamp1, GetCampn (hardcode)
	//dichos getters van a hacer (rs(campoEspecifico) + [aplican ctrles (validez,formato,etc)] + [completan] o SKIP )
	// perohago 2 funcs x getter (esqueleto gen) con validar y luego con formatear (skip en el/los q no necesiten)
	//si todo ok este getter wrapper robusto del campoxxx devuelve el valor [formateado] del rs(ese campoXxx) ya validado
	//y [formateado] (si no pasa alguno de los 2 pasos el getter devuelve null o arroja exception (ver)
	
	//en el esqueleto generico de tratarReg no hago mucho
	
	//en la 1er impl concreta, hago un and de todos los getters robustos de todos los campos.. y si todo ok 
	//ent el registro general es devuelto (sino null o exception (al 1ero (1er campo del reg) q no cumpla)
	//ent en el esqueleto generico q recorre el rs el tratarReg le falla y ejecuta invalidReg(action) PONELE..
	//sino si todo OK ent el tratarREg mientras me concatenó TODO en 1 solo reg y ent obtuve eso y no null ent
	//se graba en arch de salida ( OJO, CONTEMPLA SI EXISTE ent APPEND!)
	
	//TODO ESTO CORRE SCHEDULEADO así q annotarlo como BEAN (stateless) (no xa prop ni svc sino xa el container jboss)
	// meterle la anotation de la conn (datasource) y @Schedule xa q ejecute cada x tiempo!!! 
	//(obs: este tiene el getConn a implementar idem dbservice arquitetura, y las impl posta son 
	//BEANS, @..stateless,conn etc
	//Contemplar en la ruta de destino el delimitador (si es linux o win (las barras) ) !!! ..
	
	// --- Obtención del nombre:
	public String getFileName() throws Exception {
		try {
			String r=null;
			
			r =   getFilePrefix() + getCurrDateAsString() + getFileExtension(); // FILE_PREFIX + getCurrDateAsString() + FILE_EXTENSION;
			
			return r;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	 // codigo util.. ver.. 
	//  if (System.getProperty("os.name").startsWith("Windows")) {
	//    // includes: Windows 2000,  Windows 95, Windows 98, Windows NT, Windows Vista, Windows XP
	// } else {
	//     // everything else
	// }  
	  
	
	 //PENSAR SIEMPRE EL ONJETIVO 1ERO!: "Quiero el path de output, incluye "unidad" ,ruta y nombre carpeta final"
	//									  PERO QUIERO Q SEA Auto en Linux o en Win Y QUE SEA CONFIGURABLE! "
	
	// Ent: en el properties hago outPath.linux.value = .. y  outPath.win.value = y el otro! (x/c/ impl bean)
	// y ent q en esta clase abstracta tenga la inteligencia para identificar el os (ver codigo util q pegué), y 
	// ent setee cada valor tomando la clave q corresponde :) asi no hay q cambiar de desarrollo a produccion!.
	
	
	
	//-- Esqueleto básico: lee rs xa tomar la data. tratarReg trata el reg actual. es sobreEscrito para n cols
	
	Connection conn=null;
	ResultSet rs=null; 
	PreparedStatement ps=null;
	StringBuilder sb=null;
	String strSQL = null;
	OutputStream os=null;
	//InputStream is=null; //lee un rs.. si kisiera leer un file seria simil. 
	int bytesRead= -1 ;
	byte[] buffer = new byte[8 * 1024];
	
	
	public void execute() throws Exception {
		try {
			initServiceImpl(); //1) CARGA KTES (conf) from Properties + 2) ... ejecuta un init def x el user (user init)
			conn = getConnection();//2) Obtiene la conneccion (en el bean , la real annotada jndi)
			strSQL = loadSQLQuery(); //3) Obtiene el url (debe definirla la impl especifica usuaria (bean class svc)
									 //	  		( impl loadSQLQuery = desde un properties o strhardcoded )!
			ps = conn.prepareStatement(strSQL);//4) paso al pedoxq la querie no recibe parametros x ahora.. 
			rs = ps.executeQuery(); //5) ejecucion de la query sql y obtencion del rs
			
			executionAlgorithm(); //6) recorre el rs , trata cada reg , si reg ok graba en disco sino log Err? (actions)
			
			freeServiceResources();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		
	} 
	
	abstract void customInitialization() throws Exception; //<- Xa q cada Clase la redefina..
	abstract Connection getConnection() throws Exception; //<- Xa q cada Clase la redefina.. (x la annotation)
	abstract String loadSQLQuery() throws Exception; //<- Xa q cada Clase la redefina..
	//GENERIC INITIALIZATION (indispensable xa el mecanismo). esqueleto funcional generico + invoc a CUSTOM USER INIT.
	//parte generica + invocación a parte customizada adicional x c/impl del usuario.. ------------------------------.
	private void initServiceImpl() throws Exception {
		try {
			//1) CARGA KTES (conf) from Properties: FILE_PREFIX, FILE_EXTENSION, FILE_PATH,  ..etc
			 loadStandardConf();
			
			 //2)Ejecuta inicializacion personalizada x c/impl conreta de clase/svc especifico.(nesec. xa execute()).
			 customInitialization(); // <- CALL TO User Initialization
		
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	protected void freeServiceResources() throws Exception {
		try {
			//if ps != null ps.close(); ?? 
			if (os != null )  
				os.close();
			
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	protected void loadStandardConf() throws Exception {
		try {
			 
			 FILE_PREFIX  = getFilePrefix(); // SingletonApplicationConfiguration.getInstance().getPropValue("Application.AbstractHDFileGenerator.FILE_PREFIX");
			 FILE_EXTENSION = getFileExtension(); // SingletonApplicationConfiguration.getInstance().getPropValue("Application.AbstractHDFileGenerator.FILE_EXTENSION");
			 
			 FILE_PATH = getOutputFilePath(); //SingletonApplicationConfiguration.getInstance().getPropValue("Application.AbstractHDFileGenerator.FILE_PATH");
			
			 LOGICAL_YES=getLOGICAL_YES(); //Defs en prop. autocargadas en init(). dispo x getters y

			 LOGICAL_NO=getLOGICAL_NO();  // PUEDEN CAMBIARSE (setearse) X INSTANCIA.
			 DECIMAL_SEPARATOR=getDECIMAL_SEPARATOR(); // ( como un procesador trabaja con cadenas, todo es string )
			 DATE_FORMAT=getDATE_FORMAT(); // la doc dice “dd/mm/aa”, pero no es "yyyyMMdd" ?? -> TODO : VER !!!!!!!!!!!!
				
			 TIPO_MOVIMIENTO_INCLUSION=getTIPO_MOVIMIENTO_INCLUSION();	//append ? / ó se refiere a insert  ?
			 TIPO_MOVIMIENTO_ALTERACION=getTIPO_MOVIMIENTO_ALTERACION();	//update
			 TIPO_MOVIMIENTO_EXCLUSION=getTIPO_MOVIMIENTO_EXCLUSION();	//delete
			 TIPO_MOVIMIENTO_NOT_FOUND=getTIPO_MOVIMIENTO_NOT_FOUND();	//No existita el registro -> insert ?
				
				
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	protected String getFilePrefix() throws Exception {
		String r  = null;
		try {
			r=SingletonApplicationConfiguration.getInstance().getPropValue("Application.AbstractHDFileGenerator.FILE_PREFIX");
			return r;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	protected String getFileExtension() throws Exception {
		String r  = null;
		try {
			r=SingletonApplicationConfiguration.getInstance().getPropValue("Application.AbstractHDFileGenerator.FILE_EXTENSION");
			return r;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	// --- Obtención del path de salida:
		//no: private String outPutFileDrive = "c:"; //sacarlo del properties x/cada svc Impl. si linuxent "/" (FileSep) ?..????
		//(xq el path va todo junto de una , todo o nada. cambia ent cambia todo el path en el prop y  listo.
			//x conf (properties): una conf xa linux y otra xa windows? (xa q funque sin q haya q cambiar nada?) 
			public String getOutputFilePath() throws Exception {
				try {
					String r=null;
					
					String path_key = "Application.AbstractHDFileGenerator.FILE_PATH.lin";
					 if (windowsOS()) 
						 path_key="Application.AbstractHDFileGenerator.FILE_PATH.win";
					 
					 
					//r = //"c:"+File.separator+OutputFolder+File.separator; //TODO tomarlo del properties (def xa la clase en cuestion..)
					r= SingletonApplicationConfiguration.getInstance().getPropValue(path_key);
					 
					return r;
				} catch (Exception e) {
					throw new Exception(e.getMessage());
				}
			}
			
			
			public String getCompleteOutputFilePath() throws Exception {
				try {
					String r=null;
					
				
					r= getOutputFilePath() + getFileName(); 
					 
					return r;
				} catch (Exception e) {
					throw new Exception(e.getMessage());
				}
			}
	
	//implemento algoritmo generico(puede igual redefinirse xa cosas muy diferentes..):
	//básicamente recorre el resultset y trata cada registro (simil procesador .net )
	//(**)si quieren otro execution algorithm tienen q redefinirlo. este es el standard 		//IDEA: 
	public void executionAlgorithm() throws Exception { // (aunq xa leer de un file seria identico , habria q wrapear
		String strResReg=null;						    // 1 a 1 el api xa q se los pueda tratar de = forma con un 
		try {											// api mio generico (q wrappea y hace de proxy/adapter) a c/u! 
			if (rs!=null) { //trajo datos..
				os = getOutputStream();//ojo, x ahi no cumple ningun reg y queda vacio..
				while (rs.next()) { // ->(**) si kieren otra condicion de fin tienen q redefinir (esto es lo standard)
					strResReg=(String) tratarReg(); //en realidad la impl usa especificamente una instancia de string 
					if (strResReg != null) { //cumple.. // en este caso obtiene cada col (1 a 1 ahardcoded) (hace 1 and de todos) devuelve object.
						validRegAction(); // 
					}else {
						invalidRegAction();
					}
				}		// asi si la impl devuelve elstring renglon o null o si devuelve un bool en el futuro joya
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	//Obs: si usan otro input Stream pueden escribir en el y la salida saldrá direccionada x http etc 
	//(ojo el contentType o seteo de tipo de devolucion) pero funcaria..
	
	protected OutputStream getOutputStream() throws Exception {
		OutputStream r = null;
		try {
			//String strFileName=getCurrDateAsString();
			r = new FileOutputStream(getCompleteOutputFilePath());//(strFilePath+strFileName); //("C:\\temp\\test.txt");
			return r;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	
	public abstract Object tratarReg() throws Exception;  //este tiene que hacer un and de todos los 
														//getters hardcodeados (1a1). 
	//..los getters ( y su validator y formatter correspndiente se hardcodean (agrega la impl de c/u ) en la concreta.
	
	public abstract Object validRegAction() throws Exception; //graba el reg en el os (file en este caso)
	
	public abstract Object invalidRegAction() throws Exception; //graba un log o algo.
	
	//-- Helper functions:
	
	//Date:
	
	public String getCurrDateAsString() throws Exception { //Ya brinda funcionalidad generica,pero puede sobreEscribirse. 
		try {
			String r = null;

			//java.util.Date hoy = new java.util.Date(); //sdf.parse("2000-06-22");
			java.util.Date hoy  = Calendar.getInstance().getTime();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			
			r = sdf.format(hoy);
			
			return r;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	
	public static String padSpacesRight(String s, int n) throws Exception {
		try {
			 return String.format("%1$-" + n + "s", s);  
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	    
	}

	public static String padSpacesLeft(String s, int n) throws Exception {
	   try {
		return String.format("%1$" + n + "s", s);
	   } catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	
	//Si el string solo contiene numerosse puede convertir a integer y entonces usar string.format xa numeros:

	public static String padZerosLeft(String s, int n) throws Exception {
		try {
			//  
			// //return String.format("%1$" + n + "s", s);
			// //String.format("%010d", Integer.parseInt(mystring));
			// return String.format("%0" + n + "d", Integer.parseInt(s));  
			return org.apache.commons.lang3.StringUtils.leftPad(s,n,"0"); //(str, size, padChar)
			//"0" is the padding char
			
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public static String padZerosRight(String s, int n) throws Exception {
		try {
			// 
			// //return String.format("%1$" + n + "s", s);
			// //String.format("%010d", Integer.parseInt(mystring));
			// return String.format("%0" + n + "d", Integer.parseInt(s)); 
			return org.apache.commons.lang3.StringUtils.rightPad(s,n,"0"); //(str, size, padChar)
			//"0" is the padding char
			
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public static boolean isNumeric(String str) //TODO <- PROBAAAARRRR !!!!!!!!!!
	{
	  return str.matches("-?\\d+(\\\\.\\d+)?"); //("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	
	//final static String DATE_FORMAT = "dd-MM-yyyy";

	//Me valida cualquier fecha con cualquier formato pero si le paso otro formato no me devuelve un string 
	//con el formato standard como lo hace la anterior ( usar si ya se q viene con el formato standard como input
	//y q no hace falta obtener un strFormatted distinto explicitamente. osea, cumple y ya es ese strFormat asi que
	//usala xa concatenar. sino uso la otra y hace todo de una pero xa cuando input es yyyyMMdd usar esta !!!! 
	public static boolean isDateValid(String strDateParam, String dateFormatParam) throws Exception
	{
	        try {
	        	if (dateFormatParam == null) dateFormatParam="yyyyMMdd";
	        	
	            DateFormat df = new SimpleDateFormat(dateFormatParam); //yyyyMMdd
	            df.setLenient(false);
	            df.parse(strDateParam);
	            
	            return true;
	            
	        } catch (ParseException e) {
	            return false;
	        }
	}
	
	
	final static String STANDARD_DATE_FORMAT = "yyyyMMdd";
	
	//recibe un strDate en un formato x y genera un date, si no hay error ent es un fate valido con ese formato,
	//luego toma ese date y lo obtiene en string pero con el formato standard!.
	
	//si hay algun error retorna null, sino retorna la fecha validada y en formato strStandard lista xa concatenar.
	public static String getyyyyMMddValidFormatedDate(String strDateParam, String dateInputFormatParam) { // throws Exception {
	        try {
	        	String standardFormatedStrDate=null; 
	        	//1)
	        	SimpleDateFormat sdf = new SimpleDateFormat(dateInputFormatParam); //("yyyy-MM-dd");
				java.util.Date dateInput = sdf.parse(strDateParam);
				
				//2)
				DateFormat df = new SimpleDateFormat(STANDARD_DATE_FORMAT); //("MM/dd/yyyy HH:mm:ss");
				standardFormatedStrDate = df.format(dateInput);

				return standardFormatedStrDate;
	        } catch (Exception e) {
	            return null; //puede ser x fecha input invalida o no en formatoInput dado 
	        }										  //  o sino por error en la parte 2) obtener en formato standard.
	}

	public static boolean windowsOS() throws Exception { // <- TODO pasarla a FMK-Utils !!! (idem las de texto tmb..!!).
		boolean r=false;
		try {
			if (System.getProperty("os.name").startsWith("Windows")) {
		        r=true;
		    } else {
		        r=false;
		    } 
			return r;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	/*ESTO NO VA: Usar las de apache commons.. : pero wrappeadas x las dudas:
	
	public static String padSpacesRight(String s, int n) throws Exception {
		try {
			 return String.format("%1$-" + n + "s", s);  
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	    
	}

	public static String padSpacesLeft(String s, int n) throws Exception {
	   try {
		return String.format("%1$" + n + "s", s);
	   } catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	
	//Si el string solo contiene numerosse puede convertir a integer y entonces usar string.format xa numeros:

	public static String padZerosLeft(String s, int n) throws Exception {
		try {
			//return String.format("%1$" + n + "s", s);
			//String.format("%010d", Integer.parseInt(mystring));
			return String.format("%0" + n + "d", Integer.parseInt(s));
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}	
*/	

	/*Getter xa las demas constantes (asi puedo pedirlas individualmente ademas de ejecutar estos getters en "init()",
	 * x las dudas los hago variables y no constantes (x si quisiera usar una instancia en forma distinta a las demas:
	   Igual las llamo en Mayusculas xa mantener el concepto de q son como constantes digamos x lo gral..
	   Además, Si una impl de essta cls abst (implA) necesita un tipo de kte y otra Clase (ImplB) otra <>, ent, si
	   heredan la kte cagaron. ( SOLO DEBE SER KTE algo q SIEMPRE! va a ser igual xa toda instancia y subArbol heredero)
	    
		Los patrones de formato de información dependiendo de tipo de campo son:
		a)	Lógico: 3 posiciones - “yes” o “no “;
		b)	Decimal: o separador decimal debe ser “.” (punto);
		c)	Fecha: “dd/mm/aa”, donde “dd” es dia, “mm” es  mes y “aa” es año; 
		d)  Tipo de Movimento - al final de cada registro debe contener un tipo de movimento: “I” – inclusion, 
			“A” – alteración, “E” – exclusion o “X” – caso que el registro no exista, será adicionado, 
			caso contrario será actualizado.
	*/
	
	//public enum Day {SUNDAY, MONDAY} -> def: Day dia = new dia() -> asig: dia=Day.SUNDAY -> use: if (dia==day.SUNDAY)
	private String LOGICAL_YES=""; //Defs en prop. autocargadas en init(). dispo x getters y

	private String LOGICAL_NO="";  // PUEDEN CAMBIARSE (setearse) X INSTANCIA.
	private String DECIMAL_SEPARATOR=""; // ( como un procesador trabaja con cadenas, todo es string )
	private String DATE_FORMAT=""; // la doc dice “dd/mm/aa”, pero no es "yyyyMMdd" ?? -> TODO : VER !!!!!!!!!!!!
	
	private String TIPO_MOVIMIENTO_INCLUSION="";	//append ? / ó se refiere a insert  ?
	private String TIPO_MOVIMIENTO_ALTERACION="";	//update
	private String TIPO_MOVIMIENTO_EXCLUSION="";	//delete
	private String TIPO_MOVIMIENTO_NOT_FOUND="";	//No existita el registro -> insert ?
	
	
	
	public String getLOGICAL_YES() throws Exception {
		//return LOGICAL_YES;
		String r  = null;
		try {
			r=SingletonApplicationConfiguration.getInstance().getPropValue("Application.AbstractHDFileGenerator.LOGICAL_YES");
			return r;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setLOGICAL_YES(String lOGICAL_YES) {
		LOGICAL_YES = lOGICAL_YES;
	}

	public String getLOGICAL_NO() throws Exception {
	//return LOGICAL_NO;
		String r  = null;
		try {
			r=SingletonApplicationConfiguration.getInstance().getPropValue("Application.AbstractHDFileGenerator.LOGICAL_NO");
			return r;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setLOGICAL_NO(String lOGICAL_NO) {
		LOGICAL_NO = lOGICAL_NO;
	}

	public String getDECIMAL_SEPARATOR() throws Exception {
		String r  = null;
		try {
			r=SingletonApplicationConfiguration.getInstance().getPropValue("Application.AbstractHDFileGenerator.DECIMAL_SEPARATOR");
			return r;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setDECIMAL_SEPARATOR(String dECIMAL_SEPARATOR) {
		DECIMAL_SEPARATOR = dECIMAL_SEPARATOR;
	}

	public String getDATE_FORMAT() throws Exception {
		String r  = null;
		try {
			r=SingletonApplicationConfiguration.getInstance().getPropValue("Application.AbstractHDFileGenerator.DATE_FORMAT");
			return r;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setDATE_FORMAT(String dATE_FORMAT) {
		DATE_FORMAT = dATE_FORMAT;
	}

	public String getTIPO_MOVIMIENTO_INCLUSION() throws Exception {
		String r  = null;
		try {
			r=SingletonApplicationConfiguration.getInstance().getPropValue("Application.AbstractHDFileGenerator.TIPO_MOVIMIENTO_INCLUSION");
			return r;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setTIPO_MOVIMIENTO_INCLUSION(String tIPO_MOVIMIENTO_INCLUSION) {
		TIPO_MOVIMIENTO_INCLUSION = tIPO_MOVIMIENTO_INCLUSION;
	}

	public String getTIPO_MOVIMIENTO_ALTERACION() throws Exception {
		String r  = null;
		try {
			r=SingletonApplicationConfiguration.getInstance().getPropValue("Application.AbstractHDFileGenerator.TIPO_MOVIMIENTO_ALTERACION");
			return r;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setTIPO_MOVIMIENTO_ALTERACION(String tIPO_MOVIMIENTO_ALTERACION) {
		TIPO_MOVIMIENTO_ALTERACION = tIPO_MOVIMIENTO_ALTERACION;
	}

	public String getTIPO_MOVIMIENTO_EXCLUSION() throws Exception {
		String r  = null;
		try {
			r=SingletonApplicationConfiguration.getInstance().getPropValue("Application.AbstractHDFileGenerator.TIPO_MOVIMIENTO_EXCLUSION");
			return r;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setTIPO_MOVIMIENTO_EXCLUSION(String tIPO_MOVIMIENTO_EXCLUSION) {
		TIPO_MOVIMIENTO_EXCLUSION = tIPO_MOVIMIENTO_EXCLUSION;
	}

	public String getTIPO_MOVIMIENTO_NOT_FOUND() throws Exception{
		String r  = null;
		try {
			r=SingletonApplicationConfiguration.getInstance().getPropValue("Application.AbstractHDFileGenerator.TIPO_MOVIMIENTO_NOT_FOUND");
			return r;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public void setTIPO_MOVIMIENTO_NOT_FOUND(String tIPO_MOVIMIENTO_NOT_FOUND) {
		TIPO_MOVIMIENTO_NOT_FOUND = tIPO_MOVIMIENTO_NOT_FOUND;
	}
}
