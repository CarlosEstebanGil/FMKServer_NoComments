package fmk_core_server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;








//import xml.IXMLConverter;
import configuration.SingletonFrameworkConfiguration;
import configuration.SingletonServicesConfiguration;
import configuration.ISingletonConfiguration;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import output.IConverter;
import serviceDispatcher.IStandardExecutableService;

/**
 * Implmentents an standard generic db sql service ( select/insert/del,sp etc )
 * @author carlos Esteban Gil
 *
 */

//tiene que ser un ejb bean ( un session  bean x ej ) x q sino @resource nunca va a andar mepa..
//@Stateless(name="DS_SessionBean", mappedName="DS_SessionBean")
//@LocalBean

// 20130311 TODO hacer mejor Istandard service provider. La version actual quedó rara, 
// 				 deberia cambiar su nombre por IDBService y q tenga solo lo de DB q se agrega a un IService comun

//2014 .. X Defecto los servicios son con impl de output xml pero puede sobreescribirse el writeResponse.

//20130311 public abstract class AbstractDBService implements IStandardServiceProvider {
public abstract class AbstractDBService extends AbstractService implements  IDBService {//IStandardServiceProvider{
	
	static final Logger logger = Logger.getLogger(AbstractDBService.class);
	
	//20130311
	protected boolean result=false;
    protected boolean error = false;
    protected String  xmlMsge = "";
    protected StringBuffer sb; //no utiliza converter
    //20130311: new:
    protected String qryActionType ="bool";
    
	/* SOLO POR AHORA!.. luego las clases de servicios deberian ser ejbs sessionBeans pero como se como
	  instanciar ejb beans en tiempo de ejecucion osea como injectar ejbs locales at runtime simil classforname 

	@Resource(mappedName="java:jboss/datasources/MyDS") DataSource ds; */
	//@Resource(mappedName="java:jboss/datasources/MyDS") DataSource ds; 
	
	//DataSource ds;

	/*@Override public void setDs(DataSource ds) {	//	this.ds = ds;	}*/
	
	//implementa IStandardServiceProvider en forma genérica un servicio a base de datos: simil generica.
	//es abstracta x q me resuelve algunos metodos para todas las q la herenden ej solvePSparams etc..
	//acá podría ir el esqueleto de implementación de la interfase pero en vez de preparar un statement
	//de select como lo hace genérica lo podria hacer instanciando la clase svcDb real .execute 
	
	//20130125 pruebo meterle un constructor ya q abstract dispatcher a pesar de ser abstract lo tiene
	//a ver q pasa...
	public AbstractDBService() {
		super();
		// TODO Auto-generated constructor stub
	}

	//tecnica: wrappeados en la interfaz genérica, esta clase recive y define sus vars y params en const etc
	//e invoca a los metodos wrappeados especificos de este tipo de service provider (ej db) y se setea su
	//attribs miembros y asi trabaja. luego la de ws wrapeará los suyos pero ambas implementan asi = interfase!

	//20130315
	//20130319 static public String DEFAULT_SERVICE_CONF = "default"; //TODO meter esto en un properties de conf global de app

	
	// 20130319 static public String DEFAULT_SERVICE_CONF = SingletonServicesConfiguration.getInstance().getPropValue("defaultServiceName.name");;
	public final String DEFAULT_SERVICE_CONF = SingletonServicesConfiguration.getInstance().getPropValue("defaultServiceName.name");
	
	/*
	static protected String CONF_FILENAME   = "services.properties";
	protected static ISingletonConfiguration svcConfs = null;  //si no era protected ent la q la heredaba
																//no podia accederla / referenciarla!!!!saber!
	*/
	
	/*20130311
	protected HttpServletRequest req;
	protected HttpServletResponse res; */
	
	//20130313
	//protected PreparedStatement ps = null;
	protected PreparedStatement ps = null; //lo dejo asi por q cs extends (es un) ps.
	
	protected ResultSet rs = null;
	protected IConverter converter = null;//20140623 IXMLConverter converter = null; // xa los servicios de RS mas q nada.
	protected Connection conn = null;
	//20140627 protected String strSQL = null;
	public String strSQL = null;
	protected String tag     = null;
	protected String itemTag = null;
	
	//20130311 protected PrintWriter o =  null;
	
	//20130125 acá está la cosa! si es una clase normal no se instancia hasta q el thread del req http la ejecuta
	//pero cuando es  un enterprise el container la instancia antes q mi app y ahi se ejecuta desde ese thread 
	//el code y obvio no anda ni para atras! x eso no voy a invocar en esta implementacion abstracta ni en el
	//session bean, entonces voy a crear un setter (meter en la <<i>> xa q sea obligatorio q exista x contrato
	//y q el programador q usa esta clase sepa q tiene q setear el ds!!!
	
	/*static {  svcConfs = SingletonServicesConfiguration.getInstance();   }*/
	
	/*no hace falta, obtengo el conf dentro de los metodos de instancia y no como static
	public void setServiceConfiguration(ISingletonConfiguration svcConfs){ 
		this.svcConfs = svcConfs; 	}*/
	
	protected Map<String, String[]> paramsFromReq;
	protected Map<String, String[]> paramsFromSvcDef;
	
	
	//20130311 protected String strServiceName = null; //xa que lo vean las clases q la extienden !
	
	/* SOLO POR AHORA!.. luego las clases de servicios deberian ser ejbs sessionBeans pero como se como
	  instanciar ejb beans en tiempo de ejecucion osea como injectar ejbs locales at runtime simil classforname */
	
	/* 20130311
	@Override
	public void setServiceName(String strServiceName) {
		// TODO Auto-generated method stub
		this.strServiceName = strServiceName;
	}

	@Override
	public void setConfiguration(ISingletonConfiguration svcConf) {
		// TODO Auto-generated method stub
		this.svcConfs = svcConf;
	} */

	/*20130311
	//Modularizo el seteo de recursos
	public void setResources(String strSvcName, HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		if (	strSvcName == null || strSvcName.length()==0 ||
				req == null || res == null ) {
			String errMsge = "Se requieren nombre del servicio,  objeto httpRequest y  objecto HHTResponse válidos";
			logger.fatal(errMsge);
			throw new Exception(errMsge);
		}else {
			setConfiguration( SingletonServicesConfiguration.getInstance() );
			setServiceName( strSvcName);
			setReq( req);
			setRes( res);
		}
	} */

	//Implementación standard de la cargaDelConversor xa los servicios q quieran devolver un RS.
	//20140623 public IXMLConverter loadConversor() throws Exception{
	public IConverter loadConversor() throws Exception{
		String converterClass = null;
		converterClass = svcConfs.getPropValue(strServiceName+".conversorClass");
		
		if ( converterClass  == null )  {
			converterClass =  svcConfs.getPropValue(DEFAULT_SERVICE_CONF+".conversorClass");//"xml.XMLconverter"; //ver si funca x q la clase esta en un proj externo shared utils!

			//20130315
			//logger.warn("No se ha provisto una clase conversora de resultado para el servivio. Se cargará por defecto el conversor a xml del FMK" );
			//20141009 la sig FUNCA pero la saco xa nodar info x console de los servicios ocultos cuando se ejecutan!
			//logger.warn(SingletonFrameworkConfiguration.getInstance().getPropValue("Framework.AbstractDBService.loadConversor.msges.log.defaultConverterLoaded"));
		}
			
		if (converterClass == null) {
			logger.fatal(SingletonFrameworkConfiguration.getInstance().getPropValue("Framework.AbstractDBService.loadConversor.msges.log.cannotLoadConverter"));
			throw new Exception(SingletonFrameworkConfiguration.getInstance().getPropValue("Framework.AbstractDBService.loadConversor.msges.throw.cannotLoadConverter"));
		}
				
		//20140623 IXMLConverter converter = null;
		IConverter converter = null;
		try{ 
			//Esta linea utiliza el loader del actual osea del core_server !!!
			 converter = (IConverter)(Class.forName( converterClass)).newInstance(); //20140623 (IXMLConverter)(Class.forName( converterClass)).newInstance();
			 
			//Esta linea utiliza el loader del thead actual que llamó o está ejecutando esto de este módulo
			//osea el loader del core_client!!!! en este caso!
			//converter = (IXMLConverter)(Thread.currentThread().getContextClassLoader().loadClass(converterClass)).newInstance();
			//converter = (IXMLConverter)(Thread.currentThread().getContextClassLoader().getParent()
		}catch (ClassNotFoundException cnfe){
			String strError =  SingletonFrameworkConfiguration.getInstance().getPropValue("Framework.AbstractDBService.loadConversor.msges.throw.ConversorClassNotFoundException.part1") + " " +
							   strServiceName +  
							   SingletonFrameworkConfiguration.getInstance().getPropValue("Framework.AbstractDBService.loadConversor.msges.throw.ConversorClassNotFoundException.part2") + " " ;
			
			logger.fatal(strError + cnfe.getMessage());
			throw new Exception(strError); //arrojo la exception y sale.TODO wrappear la exception y lanzar la propia 
		}
		return converter;
	}

	//Modularizo la carga de la sentencia sql standard (básica) (1 sola):
	/* (non-Javadoc)
	 * @see fmk_core_server.IDBService#loadSQLQuery()
	 */
	@Override
	public String loadSQLQuery() throws Exception{
		return loadSQLQuery(".sql");
	}
	//SobreCargo para parametrizar el "tagName" o clave prop de la sentencia sql, q sea var xa + de 1!
	/* (non-Javadoc)
	 * @see fmk_core_server.IDBService#loadSQLQuery(java.lang.String)
	 */
	@Override
	public String loadSQLQuery(String strSqlKey) throws Exception{
		
		//new:20130204: TODO contemplo armado con referencias en medio de una def.. 
		
		String strSQL = null;
		
		String strFullQryPropertyKey=strServiceName +".sql";
		if (!strSqlKey.equalsIgnoreCase(".sql")) 
			strFullQryPropertyKey = strServiceName + "." + "sql"+ "." +strSqlKey;
		
		strSQL = svcConfs.getPropValue(strFullQryPropertyKey);
		
		if ( strSQL  == null ) {
			String strError = SingletonFrameworkConfiguration.getInstance().getPropValue("Framework.AbstractDBService.loadSQLQuery.msges.throw.NoSqlFound")+ " " + strServiceName;
			logger.fatal(strError);
			throw new Exception(strError);
		}
		return strSQL;
	}
	
	//20140623 : Leer estos 3 comentarios, explican mas o menos como estaba armada la rta.. 
	  public void writeResultXML(String QueryActionType){ //el unico q usa el converter x ahora es la impl de generic select.. yl 
	    	try {							//todos los q neesiten ej multisentences q resulten teniendo q devolver un xml etc.
	    									//en los multisentence con salida file redefino writeResultXML
	    									//xa q tmb use un converter.. osea q es simil genericDSelect (esaParte)
	    		printResponseHeader();
	    			o.println(getStandardResultXML(""));
	    		printResponseFooter();
	    	//20130319		printResponseFooter();
	    	} catch(Exception e) {
	    		System.out.println(e.getMessage());
	    	}
	    }
	  
	/**
	 * Fills a Prepared statement var member obj with params 1 to ..n def in conf file ej: svcname.parama.1 to .n
	 * @param a string svcName representing the service name def in the conf file to retrieve svc def param values
	 * @return void 
	 * 
	 */
	//protected void solveSvcParameters() throws Exception {
	@Override
	public void fillService() throws Exception{ //20130313 soporta cs por que cs extiende (es un) ps.
		//usa la configuración asi que
		 //svcConfs= SingletonServicesConfiguration.getInstance(); //es llamado SIEMPRE x el execute asi q dispone svcConfs
		
		//Respeto la interfaz q es genérica para db, ws, etc. x lo tanto wrappero la especifica:
		//dejo esta firma sin params xq q es de la <<i>> q es generica y sirve tmb para webServices etc
		try{
			fillService(""); //admite servicename.param directamente (xa las sentencias únicas x servicio)
		}catch(Exception e){
			logger.fatal(e.getMessage());
			throw new Exception(e.getMessage()); 
		}
	}
	
//wrappers: respeto la interface (o lo que me llega/me invoca) y luego lo derivo a otra firma similar
//pero mas especifica que es x lo gral mas invocada x mis clases mas especificas q la original.
//si hay una clase manager q manejase el esqueleto e invocase a la mas vacia original , yo 
//redirecciono a la mia completando los params.. o sino redefiniria el esqueleto directamente..	
	//TODO Hacer que todas las def de sql queries sean sql.sentenceKeyName asi puedo automatizar
//20150104 -> new: contemplo q me pasen un blob xa upload ( pero  1 solo a la vez, si quieren otro necesitan 
	//ejecutar otro svc del properties q lo haga). Las clases blobUploader de fmkServer y bloduploaderExample en 
	//fmk client NO SE USAN!!! qdaron al pedo .. Basicamente esta rutina cambia en q si es un blob ent lo toma de
	//otra manera y otro lugar (del header en vez de req.getParameter) pero nad amas el i++ sigue todo igual xq
	//no afecta en nada ya q un .type B es valido y es 1 mas a setear en el ps y del req se contempla no tomarlo
	//x su nombre de param ya q no va a estar asi q se toma el file del header y listo (es mas en este caso ni se
	//usa el paramName pero debe ponerse xq al ppio de esta rutina se lo levanta pensando q eran siempre del request
	//FINAL-> POSTA-> Si se usa la del fmkServer! y va a ser así la cosa:
	/* 
	 *  -> LA POSTA: definen un svc del tipo de clase fmkServer sqlModifBlobIncluded q internamente toma las 2 
	 *  	sentencias a definir q ya espera previamente digamos sqlEntidad y sqlBlobTable y serian ins e ins, ins 
	 *  	y upd, upd y upd o upd e ins da igual lo q definan ya q esta clase intermedia q qdaria en el
			fmkServer toma ambas sentencias de nombre x contrato convenido de antemano y ejecuta una detras de la 
			otra usando los params def en 1 y en otra total existen y el .type tmb existe solo q en alguna si osi va 
			a haber un .type =B (blob) xq sino al pedo usarian esta tabla multisentence q espera estas 2 sentencias 
			especificas!!!
	 */
	
	 
	public void fillService(String strSqlKey) throws Exception{ //20130313 soporta cs por que cs extiende (es un) ps.
		
		//dejo esta firma sin params xq q es de la <<i>> q es generica y sirve tmb para webServices et
		
		//20150104 
		//InputStream isForFileForBlobUpload=null; //
				
		
		String confParam_i_Value = null;
		String reqParam_i_value = null;
		String type  = null;
		String strCmdSqlPropKey = ".sql."; //para q concatene con el strCmdSqlKey (multi)
		if (strSqlKey.equals("")) strCmdSqlPropKey = ".sql"; //xa q concatene diracto con .param 
												//(xa ops default q respetena + la interfase orig.
		boolean more = true;
		int i = 1; //es la pos en las defs del properties
		//int posPSReal = i; //es la pos en el ps (van a la par , a menos q se encuentre 1 typee B ent pos ps i 
		//qda 1 atras (menor) q la pos de seteo en el ps q hay osea q si type B no incremento )
		//mepa q no usa el hash sino q toma los datos direct del req.getParameter(byName) asi q no haria falta h
		try {  
			try { //20p15p01p14
				if ( onError() != 5 ) {
					throw new Exception();
				}
			} catch (Exception e) {
				throw new Exception();
			}
			
			while (more) { //sale x svcConfs.getProp(..param.i) no existe = null ent  more = false
				
				confParam_i_Value  = svcConfs.getPropValue(strServiceName+strCmdSqlPropKey+strSqlKey+".param."+i);
				
				if ( confParam_i_Value  == null ) 
					more = false;
				
				if (more) {
					
					type = svcConfs.getPropValue(strServiceName+strCmdSqlPropKey+strSqlKey+".param."+i+".type");
					
					if (!"B".equals(type)) { //20150104 si no es un blob ontiene el valor del param de pos i del req
						 reqParam_i_value = req.getParameter(confParam_i_Value);
					 }/*else {
						 isForFileForBlobUpload= getIs(); //20150104 sino lo toma del header..
						 
					 }*/
 					//20140825: contemplo null como valor válido para setear en los campos de la DB:
					//Seteos: 
					//a) if null value
 					
					//20150104 if (reqParam_i_value == null) {
					if ( (reqParam_i_value == null) && (!"B".equals(type)) ) {
 						ps.setString( i, null);
 					} 
 					else { //else (not null value .. then .. sets byType ! ) :
 						
						if ( type == null ) 
							type = "S";
						
						if ("N".equals(type)) {
							ps.setInt( i, Integer.parseInt(reqParam_i_value));
						} else if ("D".equals(type)) {
							ps.setDouble( i, Double.parseDouble(reqParam_i_value));
						} else if ("B".equals(type)) { //20150104
							ps.setBlob( i, getIsFromHeader()); //contempla setBlob(i,null); null valor valido!
							//no usa hash usa el req.getParameter diretamente asi q no haria falta 
							//psPosReal++; //tengo q aumentar en uno la pos en el ps PeroNoEn el hash (la i esDel hash)
						} else { // "S".equals(type)
							ps.setString( i, reqParam_i_value);
						}	
 					}
				}
				 
 				i++; //20150104 -> como no toma los vals del req usando indice (req.getParam(i) ni hash.param(i))
						//sino req.getParamName(strParamNameSiguienteEnProeprties) ent no necesito llevar 2 cont!!
			}
		}catch(Exception e){
			 String strErr = SingletonFrameworkConfiguration.getInstance().getPropValue("Framework.AbstractDBService.fillService.msges.throw.cannotFillSvcParams")+ " ";
			logger.fatal(strErr + e.getMessage() );
			throw new Exception(strErr + e.getMessage()); 
		}
	}
	
	//20150317 : copia de fillservice pero q es usada x genericblobupload xa limitar q si no usan un blob ent 
	//no puedan hacer n sentencias.. 
public boolean fillService(String strSqlKey,boolean v ) throws Exception{ //20130313 soporta cs por que cs extiende (es un) ps.
		
		//dejo esta firma sin params xq q es de la <<i>> q es generica y sirve tmb para webServices et
		
		//20150104 
		//InputStream isForFileForBlobUpload=null;
				
		boolean result=false;
	
		String confParam_i_Value = null;
		String reqParam_i_value = null;
		String type  = null;
		String strCmdSqlPropKey = ".sql."; //para q concatene con el strCmdSqlKey (multi)
		if (strSqlKey.equals("")) strCmdSqlPropKey = ".sql"; //xa q concatene diracto con .param 
												//(xa ops default q respetena + la interfase orig.
		boolean more = true;
		int i = 1; //es la pos en las defs del properties
		//int posPSReal = i; //es la pos en el ps (van a la par , a menos q se encuentre 1 typee B ent pos ps i 
		//qda 1 atras (menor) q la pos de seteo en el ps q hay osea q si type B no incremento )
		//mepa q no usa el hash sino q toma los datos direct del req.getParameter(byName) asi q no haria falta h
		try {  
			try { //20p15p01p14
				if ( onError() != 5 ) {
					throw new Exception();
				}
			} catch (Exception e) {
				throw new Exception();
			}
			
			while (more) { //sale x svcConfs.getProp(..param.i) no existe = null ent  more = false
				
				confParam_i_Value  = svcConfs.getPropValue(strServiceName+strCmdSqlPropKey+strSqlKey+".param."+i);
				
				if ( confParam_i_Value  == null ) 
					more = false;
				
				if (more) {
					
					type = svcConfs.getPropValue(strServiceName+strCmdSqlPropKey+strSqlKey+".param."+i+".type");
					
					if (!"B".equals(type)) { //20150104 si no es un blob ontiene el valor del param de pos i del req
						 reqParam_i_value = req.getParameter(confParam_i_Value);
					 }/*else {
						 isForFileForBlobUpload= getIs(); //20150104 sino lo toma del header..
						 
					 }*/
 					//20140825: contemplo null como valor válido para setear en los campos de la DB:
					//Seteos: 
					//a) if null value
 					
					//20150104 if (reqParam_i_value == null) {
					if ( (reqParam_i_value == null) && (!"B".equals(type)) ) {
 						ps.setString( i, null);
 					} 
 					else { //else (not null value .. then .. sets byType ! ) :
 						
						if ( type == null ) 
							type = "S";
						
						if ("N".equals(type)) {
							ps.setInt( i, Integer.parseInt(reqParam_i_value));
						} else if ("D".equals(type)) {
							ps.setDouble( i, Double.parseDouble(reqParam_i_value));
						} else if ("B".equals(type)) { //20150104
							result=true;
							ps.setBlob( i, getIsFromHeader()); //contempla setBlob(i,null); null valor valido!
							//no usa hash usa el req.getParameter diretamente asi q no haria falta 
							//psPosReal++; //tengo q aumentar en uno la pos en el ps PeroNoEn el hash (la i esDel hash)
						} else { // "S".equals(type)
							ps.setString( i, reqParam_i_value);
						}	
 					}
				}
				 
 				i++; //20150104 -> como no toma los vals del req usando indice (req.getParam(i) ni hash.param(i))
						//sino req.getParamName(strParamNameSiguienteEnProeprties) ent no necesito llevar 2 cont!!
			}
			return result;
		}catch(Exception e){
			 String strErr = SingletonFrameworkConfiguration.getInstance().getPropValue("Framework.AbstractDBService.fillService.msges.throw.cannotFillSvcParams")+ " ";
			logger.fatal(strErr + e.getMessage() );
			throw new Exception(strErr + e.getMessage()); 
		}
	}

	protected InputStream getIsFromHeader() throws Exception {
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		InputStream r=null;
		try {
			if (isMultipart) {
		        FileItemFactory factory = new DiskFileItemFactory();
		        ServletFileUpload upload = new ServletFileUpload(factory);

		 
		        List items = upload.parseRequest(req);
		        Iterator iterator = items.iterator();
		        while (iterator.hasNext()) { // xero debe haber 1 , si quieren mas q hagan <>s servicios 1 a la vez
		            FileItem item = (FileItem) iterator.next(); //tomo el 1ero (y debe ser el unico x ahora)

		            if (!item.isFormField()) { //esto deberia cumplirse si hicieron las cosas bien.. 
		            	
		                //String fileName = "morsaReloaded.jpg"; // item.getName();

		                //String root = getServletContext().getRealPath("/");
		              //  File path = new File("c:\\"); //(root + "/uploads");
		                /*if (!path.exists()) {
		                    boolean status = path.mkdirs();
		                }*/

		            	//20150104 esto no lo quiero 
		            	//File uploadedFile = new File("c:\\morsaReloaded.jpg"); //(path + "/" + fileName);
		            	
		                //System.out.println(uploadedFile.getAbsolutePath());
		              //20150104 cambio esta q graba un file local x la de devolver un is
		                //item.write(uploadedFile);
		              
		            	//tomo el item (el unico file del header) como un inputStream y lo devuelvo xa el setBlob
		            	r= item.getInputStream(); //<-- 20150103 Con esto creo q lo meto ps.setBlob(isFileItem);
		            } //end if
		        } //end while
		    
			}  //if sMultiPart
			return r; //si no era multiparte o si no entra al while r vale null y se inserta eso!
			 
		}
		catch (FileUploadException fe) {
			//System.out.println(e.getMessage());
			throw new Exception(fe.getMessage());
		}
		catch (IOException ioe) {
			//System.out.println(e.getMessage());
			throw new Exception(ioe.getMessage());
		}
		catch (Exception e) {
			throw new Exception(e.getMessage());
			//throw new Exception(e.getMessage());
		} 
	}
	
	 //20130311 Standard output for all services! :
    // <svc-summary action="bool" result="false" error="true" info="el parametro origen de datos no es válido"/>
    public String getStandardResultXML(String queryActionType ){
    	if (queryActionType.equals("")) queryActionType = "bool"; // Default and most common service response
    															  //TODO pasar la estructura fija xml a conf arch
    	sb = new StringBuffer();
    	
    	sb.append( '<')		// TO DO : Modularizar la salida de info de upd/del/ins/bool.
		  .append( "svc-summary ")
		 // .append( " action=\"bool\"")
		  .append( " action=\" " + queryActionType + "\"")
		  .append( " result=")
		  .append( '"')
		  .append( result)
		  .append( '"')
		  .append( " error=")
		  .append( '"')
		  .append( error)
		  .append( '"')
		  .append( " info=")
		  .append( '"')
		  .append( xmlMsge)
		  .append( '"')
		  .append( " />");
    	
    	/*
		sb.append( '<')		// TO DO : Modularizar la salida de info de upd/del/ins/bool.
		  .append( strServiceName)
		  .append( " action=\"bool\"")
		  .append( " result=")
		  .append( '"')
		  .append( result)
		  .append( '"')
		  .append( " error=")
		  .append( '"')
		  .append( error)
		  .append( '"')
		  .append( " info=")
		  .append( '"')
		  .append( xmlMsge)
		  .append( '"')
		  .append( "/>");
    */
		return sb.toString();
    }
	
	/**Execute: define un esqueleto genérico dejando solo lo único variable como abstracto xa cada
	 * implementacion específica.
	 * es el formato de execute necesario para la invocacion de un req http a travez del servlet dispatcher 
	 * @author carlos Esteban Gil
	 */
	//20130125 prueba xa dejar en fmk_cli la clase posta sin code idem dispatcher a ver como se comporta..
	//	public abstract  void execute(String strSvcName, HttpServletRequest req, HttpServletResponse res) throws Exception;  /*{
	//	public void execute(String strSvcName, HttpServletRequest req, HttpServletResponse res) throws Exception{};  /*{
	public void execute(String strSvcName, HttpServletRequest req, HttpServletResponse res) throws Exception  {
		
		/*ResultSet rs = null;
		IXMLConverter converter = null;
		Connection conn = null;
		String strSQL = null;
		String tag     = null;
		String itemTag = null;
		PrintWriter o =  null;*/
		
		try{
			//new 2014, creé el sig method xa meter el svcName xero viene como param desde el req y se setea en
			// setResources el svcName, asi que no hacia falta, pero lo dejo x si neceita usarse para otras 
			//"inicializaciones"...
			initServiceImpl();
			
			try { //20p15p01p14
				if ( onError() != 5 ) {
					throw new Exception();
				}
			} catch (Exception e) {
				throw new Exception();
			}
			
			setResources(strSvcName, req, res);
				
			converter = loadConversor();	//TODO mal, está levantando el conversor xa upds e ins q ni lo usan.
			
			//conn = ds.getConnection();
			conn = getConnection(); //definir en el bean ejb implement especifica.
			
			strSQL = loadSQLQuery();
			
			ps = conn.prepareStatement(strSQL);
			
			//------- solveSvcParameters();
			fillService();
			
			/* ceg 20130311
			tag     = svcConfs.getPropValue(strSvcName+".xmltag"); 
			if ( tag ==null ) tag = svcConfs.getPropValue(DEFAULT_SERVICE_CONF+".xmltag"); 
			
			itemTag = svcConfs.getPropValue(strSvcName+".xmlitem");
			if ( itemTag ==null ) itemTag = svcConfs.getPropValue(DEFAULT_SERVICE_CONF+".xmlitem"); 
			
			o =  res.getWriter();
			
			if (tag != null) o.print("<"+tag+">");
			
			------------------------------ */
			
			
			
			/* -- Old: parte genérica: 
			 
			rs = ps.executeQuery();	
			converter.setRecordSet(rs);
			converter.setConfKey( strSvcName);
			converter.convert(itemTag, o);
			
			ent: -- */
			
			executionAlgorithm();
			
			writeResponse(); //20130311
			
		}catch(Exception e ) {
			String strErr = SingletonFrameworkConfiguration.getInstance().getPropValue("Framework.AbstractDBService.execute.msges.throw.gralPurposeFail");
			logger.fatal( strErr +" " + e.getMessage());
			throw new Exception( strErr +" " + e.getMessage() );
		}finally {
			if (rs != null) rs.close();
			ps.close();
			conn.close(); //20141208 en este ya estaba xero en el multisentence NO! ( intento solucion Unable to get managed connection ResourceException ) 
		}
		
		//if (tag != null) o.print("</"+tag+">"); 20130311
		
	} 

	//Un query tipo select quien querrá sacar un resultSet a xml usará el converterXML, un query de tipo 
	// update o insert o multisentence (si devuelve boolean) usaran su propio tipo de respuesta sin
	// el xmlConverter ( conversor de resultSet a xml ). que sera un xml corto armado a mano.
	// Un servicio multiSentence que ejecute distintas sentencias pero quiera devolver un resultset de
	// alguna de ellas usará un coverter al igual que las de select comun.
	public abstract void writeResponse() throws Exception; 
	
	/* -----------------------*REDEFINIR*--------------------------
	 * Metodo a redefinir para los distintos tipos de consulta sql 
	 * */
	public abstract void executionAlgorithm() throws Exception;
	/*@Override
	public void execute() {
		//IStandardExecutableService = 
		try{
			execute(strServiceName, req, res);
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	} -------------------------------------------------------------*/
	
	/* (non-Javadoc)
	 * @see fmk_core_server.IDBService#getConnection()
	 */
	@Override
	public abstract Connection getConnection() throws Exception;
	//getters and setters:
	//Setters para objetos miembros a utilizar para realizar la implementación 
	
	
	/*20130311 
	public HttpServletRequest getReq() {
		return req;
	}

	public void setReq(HttpServletRequest req) {
		this.req = req;
	}

	public HttpServletResponse getRes() {
		return res;
	}

	public void setRes(HttpServletResponse res) {
		this.res = res;
	}
	*/
	//-- Helper Methods ... -----------------------------------------------------------
	
	/**@author carlos esteban gil
	 * @exception  a generic exception if any error ocurrs when trying to get the paramMap from the req var
	 * @return a Map of <String, String[]> representing the params contained in httpServletrequest member object 
	 */
	public Map<String, String[]> getRequestParams() throws Exception{
		//Map<String, String[]> result = null;
		//if (req != null) {
			try {
				//result = req.getParameterMap();
				return  req.getParameterMap();
			}catch (Exception e){
				System.out.println(e.getMessage());
				String strError = "cannot get paramMap from req";
				if (req != null) strError = strError + " req member atributte is null";
				throw new Exception(strError);
			}
		//}
		//return result;
	}

	@Override
	public IStandardExecutableService getImplementacion() {
		return this;
	}

	//es solo un metodo utilitario (no es parte de la interfaz), solo por si se necesita.
	public Map<String, String[]> getServiceParams() {
		//to do..
		return null;
	}

	//public abstract  void setResources();
	
	/* 20130311 NO SE USA X AHORA. Pertenece a IServiceProvider (utils no usadas)
	@Override
	public void obtainParamsFromReq() {
		// call to wrapped real method implementation:
		//Map<String, String[]> result=null;
		
		try {
			//result = 	getRequestParams();
			paramsFromReq = getRequestParams();
		}catch (Exception e){
			paramsFromReq = null;
		}
	} */

	/* 20130311 NO SE USA X AHORA. Pertenece a IServiceProvider (utils no usadas)
	@Override
	public void obtainParamsFromSvcDef() { //(String strServiceName) {
		// TODO Auto-generated method stub
		// call to wrapped real method implementation:
	}

	@Override
	public void writeResult() {
		// TODO Auto-generated method stub		
	}
	*/
	
	//new 20130205: Agrego metodo helper para armar la senetencia sql a rellenar pero con referencias a otras
	//ojooo! -> Contemplar que "inyectar/usar" una ref a otra sentencia implica tmb usar sus parametros lpm! se
	//complica...Pero se puede! ej: pongamos q tengo en el esqueleto compuesto (consulta compuesta) un select
	//c/ varios params ? ? etc x ej 5 ? pero en el siguiente orden: ? ? ref (con 5 ? ) yl ?? yl otra ref con 2 ? y luego ??
	//como para rellenar el ps importa el orden (el ps completo compuesto) ent tengo el 1 , el 2 , luego 
	//encuentro la referencia ent le sumo el contActual(2) a el .1 y .2 y .. .5 (q terminan siendo .3.4. .. .7) 
	//solo para insertar en el ps x q los tomo del svc def referenciado con los vals origs y sus types etc..
	//luego, sigo con el contador actualizado e inserto los 2 ? y ? como .8 y .9 y luego voy a la sig referencia 
	//tomandolos como estan pero insertandolos  en el ps como .10 y .11 y luego como de la con su contadorInterno
	//tomo del properties ej a esta instancia final va a valer .5 y .6 y los tomo asi del properties de este svc compuesto
	//pero los inserto siempre en el ps con el contador general q seria .12 y .13 fin
	//Esto cambiaria la logica del fill().. xa que contemple esto pero si no hay refs (no es compuesta) funcione igual
	
	
	
	//In Java 7, you should not close them explicitly, 
	//but use automatic resource management to ensure that resources are closed and exceptions are handled appropriately. 
	/* (non-Javadoc)
	 * @see fmk_core_server.IDBService#liberarRecursosJDBC()
	 */
	@Override
	public void liberarRecursosJDBC() throws Exception {
		try {
			conn.close(); //20141208 prueba intento solucion Unable to get managed connection ResourceException 
			if (ps != null) ps.close();
			
			if (rs != null) rs.close();
		}catch(Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	private void fakeForModif1(){};
}