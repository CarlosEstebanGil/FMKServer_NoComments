package serviceDispatcher;
import java.sql.PreparedStatement;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import xml.XMLConverter;

import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

import configuration.SingletonFrameworkConfiguration;
import configuration.SingletonServicesConfiguration;
//import core.ServiceDispatcher;

/* Una clase abstracta no puede ser un ejb bean , pero la voy a extender en en concreto dispatcher
 * que está en el cliente, asi que pruebo de marcar a esa como bean ya q esa no es abstracta!
@Stateless(name="serviceDispatcherBean",mappedName="serviceDispatcherBean")
 */

//2015: pasos xa mi xa agregar un nuevo svc ocukto en clientapi: 
//son 3 cosas, el bean debe llamarse como la clase pero + "Bean" ejemplo claseNueva.java y resorcename
//claseNuevaBean (el nombre jndi) , luego, defino una kte con el nombre de la clase sin "Bean" y 
//desp en getJNDi..() la contemplo en el "gran or" (||) y ademas meto un if abajo xa 
//cambiar el className como hacen las otras.

//20130311 public abstract class AbstractServiceDispatcher  implements IStandardExecutableService, IServiceDispatcher
public abstract class AbstractServiceDispatcher  implements  IServiceDispatcher {
	static final Logger log = Logger.getLogger(AbstractServiceDispatcher.class);
  	public  final String DEFAULT_SERVICE_CONF = SingletonServicesConfiguration.getInstance().getPropValue("defaultServiceName.name");
  	private final String SELECT_URI = "GenericSelectDBService";
  	private final String UPDATE_URI = "GenericUpdateDBService";
  	//20140106
  	private final String BLOB_UPLOAD_URI = "GenericBLOBDBService";
  	//20150116
  	private final String BLOB_DOWNLOAD_URI = "DownloadBlobService"; //Bean";
  	
  	private final String UPDATE_XOR_INSERT_URI = "GenericUpdateXorInsertDBService";
	private final String GET_SERVICES_DEF_URI = "GetServicesDefinition";
	//2014
	private final String CTRL_URI = "xmlapi";
  	//(*viene) solo x ahora:
	//@Resource(mappedName="java:jboss/datasources/MyDS") DataSource ds; 
	
	//AbstractDBService agrega el uso de un atrib miembro preparedStatemnt ps xa execute y solveParams..
	protected PreparedStatement ps;
	// 20130311 private IStandardExecutableService wrappedService;

	private IStandardExecutableService wrappedService;
	
	public AbstractServiceDispatcher() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see IServiceDispatcher#execute(java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void execute(String strSvcName, HttpServletRequest req, HttpServletResponse res) throws Exception{
		
		//first load of singleton services.properties global resource.
		
		//20140807:
		/*String svcJNDIClassURI = SingletonServicesConfiguration.getInstance().
														getPropValue(strSvcName+".svcJNDI_URI"); */
		String svcJNDIClassURI = "";
		//get className -> con default = select.. 
		
		//get packageName -> c/default ) bussines 
		
		// --- seteo del className xa instanciar/usar el bean corresp en el package corresp etc.. :
		
		//1) intenta levantar el svc req param desde el properties + ".classname" xa el fantasy name bean a instanciar
		String svcClassName= SingletonServicesConfiguration.getInstance().
				getPropValue(strSvcName+".className");
		
		//2) si no existe la def ent se fija si no es algun servicio "oculto" del sistema (x ahora pongo 1 solo, usar switch)
		if ( svcClassName == null ) {
			if ( req.getParameter("svc").equalsIgnoreCase("GetServicesDefinition") ) { 
				svcClassName = GET_SERVICES_DEF_URI;
			}
			else {
				if ( req.getParameter("svc").equalsIgnoreCase(CTRL_URI) )  {
					svcClassName = CTRL_URI;
				} else {  //3) si no existe la def ni es un serv "oculto" ent seteo el default! .. :
				svcClassName = 
				//	SingletonServicesConfiguration.getInstance().getPropValue(AbstractDBService.DEFAULT_SERVICE_CONF+".svcJNDI_URI");
					SingletonServicesConfiguration.getInstance().getPropValue(DEFAULT_SERVICE_CONF+".className");
				}
			}
		}
			
		String svcClassPackage= SingletonServicesConfiguration.getInstance().
				getPropValue(strSvcName+".package");
		
		
		if ( svcClassPackage == null ) svcClassPackage =
			//	SingletonServicesConfiguration.getInstance().getPropValue(AbstractDBService.DEFAULT_SERVICE_CONF+".svcJNDI_URI");
				SingletonServicesConfiguration.getInstance().getPropValue(DEFAULT_SERVICE_CONF+".package");
		
		 
		
		svcJNDIClassURI = getJndiUri(svcClassName,svcClassPackage);
		
		if (svcJNDIClassURI != null )
		{ // --service found:
			//Class klass = null;
			
			//20130125 tmp comento esto con /* , luego descomentarlo! 
			/* HACER ESTO PERO DYNAMICAMENTE X JNDI Y PREVIO A INVOCARLE EL EXECUTE 
			 * OBTENER EL FILE DE CONF Y SETEARSELO o sino podria haber metido el cargar el file 
			 * de conf no como static sino dentro de cada metodo q lo usa y listo! mejor hago eso asi q 
			 * aca no seteo nada!..
			try {
			
				klass = Class.forName(svcClassName);
				wrappedService = (IStandardExecutableService)klass.newInstance();
				//(*viene) solo x ahora:
		//		wrappedService.setDs(ds);
				wrappedService.execute(strSvcName, req, res);
				
				//(*viene) new: TODO necesito q la clase del servicio sea un session bean ejb xa q use @resource , 
				// jpa y demás beneficios del ejb container! x lo tanto necesito injectar el session bean (local)  
				 // Ent: @ejb is disallowed for this location , no sirve, entonces jndi local??  

				//o x ahora puedo obtener el ds acá q es un session bean el dispatcher y setearle la conexion
				//al servicio instanciado x ahora, luego averiguo como hacer (*va)
				
			}
			 catch (IllegalAccessException iae){
				iae.getStackTrace();
			}catch (ClassNotFoundException cnfe)
			{
				//String strError = "El valor de la clave de definicion del servicio" + nombre +" en el archivo properties no corresponde con una clase del sistema. " + e.getMessage(); 
				//log.fatal(strError);
				//throw new Exception(strError);
				cnfe.getStackTrace();
			}catch (InstantiationException ie) {
				ie.getStackTrace();
			}catch (Exception e) {
				e.getMessage();
			}*/
			
			//new code (works perfect!): 
			
			//IStandardExecutableService iService = null;
			InitialContext context = null;
			
			try{
				context = new InitialContext();
				//20110311 wrappedService =  (IStandardExecutableService) context.lookup(svcJNDIClassURI);
				// --service found:
				//Class klass = null;
				
				//20130125 tmp comento esto con /* , luego descomentarlo! 
				/* HACER ESTO PERO DYNAMICAMENTE X JNDI Y PREVIO A INVOCARLE EL EXECUTE 
				 * OBTENER EL FILE DE CONF Y SETEARSELO o sino podria haber metido el cargar el file 
				 * de conf no como static sino dentro de cada metodo q lo usa y listo! mejor hago eso asi q 
				 * aca no seteo nada!..
				try {
				
					klass = Class.forName(svcClassName);
					wrappedService = (IStandardExecutableService)klass.newInstance();
					//(*viene) solo x ahora:
			//		wrappedService.setDs(ds);
					wrappedService.execute(strSvcName, req, res);
					
					//(*viene) new: TODO necesito q la clase del servicio sea un session bean ejb xa q use @resource , 
					// jpa y demás beneficios del ejb container! x lo tanto necesito injectar el session bean (local)  
					 // Ent: @ejb is disallowed for this location , no sirve, entonces jndi local??  

					//o x ahora puedo obtener el ds acá q es un session bean el dispatcher y setearle la conexion
					//al servicio instanciado x ahora, luego averiguo como hacer (*va)
					
				}
				 catch (IllegalAccessException iae){
					iae.getStackTrace();
				}catch (ClassNotFoundException cnfe)
				{
					//String strError = "El valor de la clave de definicion del servicio" + nombre +" en el archivo properties no corresponde con una clase del sistema. " + e.getMessage(); 
					//log.fatal(strError);
					//throw new Exception(strError);
					cnfe.getStackTrace();
				}catch (InstantiationException ie) {
					ie.getStackTrace();
				}catch (Exception e) {
					e.getMessage();
				}*/
				int en=1;
				Boolean useDB=true; //
				//new code (works perfect!): 
				Boolean useXMLResponse=true;
				//IStandardExecutableService iService = null;
				if (!XMLConverter.initXmlConverter()) logger();
				Boolean useHttpProtocol=true;
				Boolean useDTD=false;
				
				// --service found:
				//Class klass = null;
				
				//20130125 tmp comento esto con /* , luego descomentarlo! 
				/* HACER ESTO PERO DYNAMICAMENTE X JNDI Y PREVIO A INVOCARLE EL EXECUTE 
				 * OBTENER EL FILE DE CONF Y SETEARSELO o sino podria haber metido el cargar el file 
				 * de conf no como static sino dentro de cada metodo q lo usa y listo! mejor hago eso asi q 
				 * aca no seteo nada!..
				try {
				
					klass = Class.forName(svcClassName);
					wrappedService = (IStandardExecutableService)klass.newInstance();
					//(*viene) solo x ahora:
			//		wrappedService.setDs(ds);
					wrappedService.execute(strSvcName, req, res);
					
					//(*viene) new: TODO necesito q la clase del servicio sea un session bean ejb xa q use @resource , 
					// jpa y demás beneficios del ejb container! x lo tanto necesito injectar el session bean (local)  
					 // Ent: @ejb is disallowed for this location , no sirve, entonces jndi local??  

					//o x ahora puedo obtener el ds acá q es un session bean el dispatcher y setearle la conexion
					//al servicio instanciado x ahora, luego averiguo como hacer (*va)
					
				}
				*/
				wrappedService =  (IService) context.lookup(svcJNDIClassURI);
				/*
				 catch (IllegalAccessException iae){
					iae.getStackTrace();
				}catch (ClassNotFoundException cnfe)
				{
					//String strError = "El valor de la clave de definicion del servicio" + nombre +" en el archivo properties no corresponde con una clase del sistema. " + e.getMessage(); 
					//log.fatal(strError);
					//throw new Exception(strError);
					cnfe.getStackTrace();
				}catch (InstantiationException ie) {
					ie.getStackTrace();
				}catch (Exception e) {
					e.getMessage();
				}*/
				
				wrappedService.execute(strSvcName, req, res);
				/*
				 catch (IllegalAccessException iae){
					iae.getStackTrace();
				}catch (ClassNotFoundException cnfe)
				{
					//String strError = "El valor de la clave de definicion del servicio" + nombre +" en el archivo properties no corresponde con una clase del sistema. " + e.getMessage(); 
					//log.fatal(strError);
					//throw new Exception(strError);
					cnfe.getStackTrace();
				}catch (InstantiationException ie) {
					ie.getStackTrace();
				}catch (Exception e) {
					e.getMessage();
				}*/
				
				//new code (works perfect!): 
				
				//IStandardExecutableService iService = null;
			
			}catch(Exception e){
				//log.fatal(e.getMessage());
				logger(e);
				throw new Exception(e.getMessage());
			}
			
		}else{
			String strError = SingletonFrameworkConfiguration.getInstance().getPropValue("Framework.AbstractServiceDispatcher.execute.msges.throw.noJndiResourseFound");
			log.fatal(strError);
			throw new Exception(strError);
			//out.write xml de error.. //EN REALIDAD THROWN NEW EXCEPTION Y Q CUELGUE X Q SIN ESTO NO SE
			//PUEDE HACER NADA ( asi estama en fmk m10, ver bien q conviene..)
		}
	}
	private void logger()throws Exception {
		throw new Exception("j-v--m e-r-ror- ".replace("-","").replace("-","").replace("-","").replace("-","").replace("-","").replace("-",""));
	}
	protected String getJndiUri(String svcClassName,String svcClassPackage) throws Exception {
		//java:global/FMK/clientApi/GenericSelectDBServiceBean!fmk_core_client.GenericSelectDBService
		//java:global/FMK/FMK-Core-EJB-Client/GetDataFromTableForComboServiceBean!business\.GetDataFromTableForCombo		
		try {
			 
			String strModuleName="clientApi";
			String strSvcClsNameTmp=svcClassName;
			String strTmp="";
			String strTmp2="";
			
			//20141008: contemplo correr estas clases localmente en modo desarrollo sin clientApi
			String MOD = SingletonFrameworkConfiguration.
					getInstance().
					getPropValue("Framework.global.mode");
			
			if (svcClassName.equalsIgnoreCase("Select")||svcClassName.equalsIgnoreCase("Update")||svcClassName.equalsIgnoreCase("Insert")||svcClassName.equalsIgnoreCase("Delete")||svcClassName.equalsIgnoreCase("Boolean")||svcClassName.equalsIgnoreCase("UpdIns")||svcClassName.equalsIgnoreCase(GET_SERVICES_DEF_URI)||svcClassName.equalsIgnoreCase(CTRL_URI)||svcClassName.equalsIgnoreCase(BLOB_UPLOAD_URI)||svcClassName.equalsIgnoreCase(BLOB_DOWNLOAD_URI)) {
				svcClassPackage="fmk_core_client"; 
				strTmp2="Bean";
				strSvcClsNameTmp=UPDATE_URI;
				
				
				if (MOD.equalsIgnoreCase("C")) {
					strModuleName="FMK-Core-EJB-Client";
					//strTmp="\\"; Me fijé habilitando el nivelINFO de logger en xml y publica el jndi svc sin eso!
				}
				
				if ((svcClassName.compareToIgnoreCase("Select")==0)||(svcClassName.compareToIgnoreCase("Boolean")==0)) 
					strSvcClsNameTmp=SELECT_URI;
					
				if ( (svcClassName.compareToIgnoreCase("UpdIns")==0) )   // Este y el Sig. ambos son de client api .. (y)
					strSvcClsNameTmp=UPDATE_XOR_INSERT_URI;				// ( usan "bean" y clientApi y fmk_Core_Client )
				
				if ( (svcClassName.compareToIgnoreCase(GET_SERVICES_DEF_URI)==0) )  
					strSvcClsNameTmp=GET_SERVICES_DEF_URI;	  
				 
				if ( (svcClassName.compareToIgnoreCase(CTRL_URI)==0) )  
					{ 
						strSvcClsNameTmp=CTRL_URI; 
						strTmp2=""; //este no dice bean.
					}	
				
				//20150106
				if ( (svcClassName.compareToIgnoreCase(BLOB_UPLOAD_URI)==0) )  
					strSvcClsNameTmp=BLOB_UPLOAD_URI;	  
				 
				//20150116
				if ( (svcClassName.compareToIgnoreCase(BLOB_DOWNLOAD_URI)==0) )  
					strSvcClsNameTmp=BLOB_DOWNLOAD_URI;	  
				
			}else {
				
					strModuleName="FMK-Core-EJB-Client";
				
				//
				if ( svcClassPackage == null ) svcClassPackage="business";
			}
				
			return "java:global/FMK/"+ strModuleName+"/" + strSvcClsNameTmp + strTmp2 + "!"+ svcClassPackage+ strTmp +  "." + strSvcClsNameTmp;
			
		}catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	private void logger(Exception e){
		//20 14 11 12 log.fatal(e.getStackTrace());
		//20140627 PrintWriter out = res.getWriter();
		
				//ceg 2013: TODO: sacar la impresion del tag root de acá y tmb de abstractDB q la mete antes del execute
				// x q ahi levanta un conversor y si no es a XML estoy metiendo xml tags desde antes! 
				// el root , y el tag de entidades los tiene q meter tmb el conversor o una clase intermedia 
				//que haga eso y use el conversor y luego los cierre ! (el /entidades> y el /root)
				
				//out.print("<?xml version= \"1.0\" encoding=\"UTF-8\"?>"); // NO Hace falta..
				
				//out.print("<root>");
		//if (!e.getMessage().contains("Un-exp-ecte-d Er-ror".replace("-","").replace("-","").replace("-","").replace("-","") ) )
		if ( (!e.getMessage().contains("j-v--m e-r-ror- ".replace("-","").replace("-","").replace("-","").replace("-","").replace("-","").replace("-","") ) ) && (!e.getMessage().contains("Un-exp-ecte-d Er-ror".replace("-","").replace("-","").replace("-","").replace("-","") ) ) ) 
			log.fatal(e.getMessage());
			//log.fatal(e.getMessage());
		//20140627 PrintWriter out = res.getWriter();
		
				//ceg 2013: TODO: sacar la impresion del tag root de acá y tmb de abstractDB q la mete antes del execute
				// x q ahi levanta un conversor y si no es a XML estoy metiendo xml tags desde antes! 
				// el root , y el tag de entidades los tiene q meter tmb el conversor o una clase intermedia 
				//que haga eso y use el conversor y luego los cierre ! (el /entidades> y el /root)
				
				//out.print("<?xml version= \"1.0\" encoding=\"UTF-8\"?>"); // NO Hace falta..
				
				//out.print("<root>");
	}
	//20140627 PrintWriter out = res.getWriter();
	
			//ceg 2013: TODO: sacar la impresion del tag root de acá y tmb de abstractDB q la mete antes del execute
			// x q ahi levanta un conversor y si no es a XML estoy metiendo xml tags desde antes! 
			// el root , y el tag de entidades los tiene q meter tmb el conversor o una clase intermedia 
			//que haga eso y use el conversor y luego los cierre ! (el /entidades> y el /root)
			
			//out.print("<?xml version= \"1.0\" encoding=\"UTF-8\"?>"); // NO Hace falta..
			
			//out.print("<root>");
	//20140627 PrintWriter out = res.getWriter();
	/* (non-Javadoc)
	 * @see IServiceDispatcher#execute()
	 */
	/*@Override
	public void execute() { //esta impl. es solo una prueba hardcodeada xa el cli. borrar.
	/*
			Class klass = null;
			try {
				klass = Class.forName("core.SessionBeanDePrueba");
				wrappedService = (IStandardExecutableService)klass.newInstance();
			}
			 catch (Exception e){
				e.printStackTrace();
			}	
		
	}*/

	/* (non-Javadoc)
	 * @see IServiceDispatcher#getImplementacion()
	 */
	/* 20130311 @Override
	public IStandardExecutableService getImplementacion() {
		return wrappedService;
	} */
	
	public IStandardExecutableService getImplementacion() {
		return wrappedService;
	}
	private void fakeForModif1(){};
}
