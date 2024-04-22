package fmk_core_server;

import java.sql.Connection;
import java.util.HashMap;




import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import configuration.SingletonFrameworkConfiguration;
import arrays.ExtendedHashMap;

/*
 * Multisentence services are flexible multiple mixed qry sql or sp executors leaving to the programmer
 * all the responsability of jdbc code. http req send all qrys sql and sp params vars and params for 
 * the use case implementation itself (some kind of "function" argument values )  function 
 * values vars are defines as svcName.function.varName in the properties file for this svc/use case
 * and the predefined execute template method implementation in this abstract class load this vars(args)
 * from http obj and put them in the use case multisentence instance in a hashmap member atributte object
 * ready to use for the algorithm or helper funcions added to complete the objetive of the use case.
 */
public abstract class AbstractMultiSentenceDBService extends AbstractDBService {
	static final Logger logger = Logger.getLogger(AbstractMultiSentenceDBService.class);

	
	//20140708 protected ExtendedHashMap  hArguments = new ExtendedHashMap(); // HashMap<String, Object>();
	private ExtendedHashMap  hArguments = new ExtendedHashMap(); // HashMap<String, Object>();
	
	//protected Object ??? get
	protected String getStringVarValue(String strKey) throws Exception {
		try {
			return hArguments.getString(strKey);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	protected int getIntVarValue(String strKey) throws Exception {
		try {
			return hArguments.getInt(strKey);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	protected Boolean getBooleanVarValue(String strKey) throws Exception {
		try {
			return hArguments.getBoolean(strKey);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	protected Double getDoubleVarValue(String strKey) throws Exception {
		try {
			return hArguments.getDouble(strKey);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	protected Long getLongVarValue(String strKey) throws Exception {
		try {
			return hArguments.getLong(strKey);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	protected Object getObjectValue(String strKey) throws Exception {
		try {
			return hArguments.get(strKey);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	/* 20130311 las saco a AbstractDB xa q lo usen tmb upd e ins etc ..
	protected boolean result=false;
    protected boolean error = false;
    protected String  xmlMsge = "";
    protected StringBuffer sb; //no utiliza converter */
   
    /**
     * @see AbstractDBService#AbstractDBService()
     */
    public AbstractMultiSentenceDBService() {
        super();
        // TODO Auto-generated constructor stub
    }

    
    /*Redefine el esqueleto execute que utilzan las genéricas de mono sentence c/converter!
     	(recordar que es el método invocado por el dispatcher! ): */
    @Override
    public void execute(String strSvcName, HttpServletRequest req, HttpServletResponse res) throws Exception  {
    	try{
			setResources(strSvcName, req, res); //idem orig extended (ioe)
			
			//doesn't load/use any converter class
			//New!: -> 2014 -> Le mando un converter! x q x ahi queres devolver una rta xml ej: el getCMDdata!!etc!
			converter = loadConversor();	//TODO mal, está levantando el conversor xa upds e ins q ni lo usan.
			
			conn = getConnection(); 
			
			//new: 20130205: adds call to fillLogicVars xa cargar el hash c/ las vars (args funcs a impl) from req.
			
			fillLogicVars (); //deja hlogicalParamVars cargado con strKey varParamName y oValue 

			
			//don't get any .sql default sentence from property for this kind of bool services!
			
			//doesn't prepare any statement object ps for .sql default string
			
			//doen't fill a/that default ps
			
			/*20130311 
			tag     = svcConfs.getPropValue(strSvcName+".xmltag"); 
			if ( tag ==null ) tag = svcConfs.getPropValue(DEFAULT_SERVICE_CONF+".xmltag"); 
			
			//doesn't get xmlitemtag for xml resultset output only the main tag qry.
			
			o =  res.getWriter();
			
			if (tag != null) o.print("<"+tag+">");
			*/
			
			//---------------------
			//This time, inside it, u will be responsible for jdbc excecution of ps 
			//for selects, updates, inserts, deletes, sp calls , etc. no standard impl.
			executionAlgorithm(); //Here u'll writte all steps/multi-sql business logic
								 //u'll be responsible for define the use case logic 
								//that involves defining one or more sqlSentenceKeys constants
								//and using each one step by step, by calling the 
							// loadSql and  fillService methods, the parameterized versions!! 
							// with each respective srtSqlKey constant (whose values are the
							//same that are defined in the property file for this service.
			//-------
			writeResponse();
    	}catch(Exception e ) {
    		logger.fatal(e.getMessage());
			throw new Exception( e.getMessage());
		}finally { //en java 7 segun parece no es necesario cerrar explicitamente, xero no se si no es necesario o si 
					//directamente no hay que hacerlo!. ademas xa q no sea necesario no se si hay q activar/configurar 
					//alguna opcion de automanaged o algo asi..x ahora mando explícito, y veo..
			/*if (rs != null) rs.close();
			ps.close(); 	
			conn.close();*/
			liberarRecursosJDBC();
		}
		
		// 20130311 if (tag != null) o.print("</"+tag+">");
    }
    
    //-------Generic helper methods to use or override if u need something else..
   //20130311 lo saco a AbstractDB:
    /*
    public void writeResultXML(String QueryActionType){
    	try {
    		printResponseHeader();
    			o.println(getStandardResultXML(""));
    		printResponseFooter();
    	} catch(Exception e) {
    		System.out.println(e.getMessage());
    	}
    } */
    
    // basic implmentation for most comon gral use cases output
    @Override
  	public void writeResponse() throws Exception { //tecnica. esqueletos invocan siempre writeResponse sin params 
    		//	en forma standard y este es redefinido x los dist svcs types xa enviar un para <> a writeResultxml
    	//o.println(getStandardResultXML(""));
    	String strQryActionType = ""; //x defecto se usa "" = boolean
    	writeResultXML(strQryActionType); 
  	}
    
    //20130311 saco esta func a la clase abstractDB mas generica xa q tmb la usen generic upd o ins etc..
    /*
    //20130311 Standard output for all services! :
    // <svc-summary action="bool" result="false" error="true" info="el parametro origen de datos no es válido"/>
    public String getStandardResultXML(String queryActionType ){
    	if (queryActionType.equals("")) queryActionType = "bool"; // Default and most common service response
    	
    	sb = new StringBuffer();
    	
    	sb.append( '<')		// TO DO : Modularizar la salida de info de upd/del/ins/bool.
		  .append( " svc-summary ")
		
		.... ( llevada a abstractDB ) 
		.. 
		.
	*/
		
	
    //example implementation, never used. 
    //example 1: executionAlgorithm multi sql q devuelve salida boolean no usa converter
    
    
    //example 2: executionAlgorithm multi sql q usa converter y que 
    //			 lo aplica a "algo" (rs/vec objs/ ..etc) y escribe mediante ese converter.
    
    /*
    //comentar, q tengan q implementarla si o si la clase derivada final (el bean boolMultiSql)
	@Override 
	public void executionAlgorithm() throws Exception {
  		
  		//1)new 20130205: -----------------
  		//1a) 
  		 def const ARGPARAMNAMEENPROP1 = "logicalParamFuncArg1"
  		 def const ORIGENDATOS = "Origen_Datos" (en el properties dice .2.name=Origen_Datos (coincide)
		 def const NUMOP = "NumeroOperacion" (en el properties dice .3.name=NumeroOperacion (coincide)

  		 ..etc c/c/arg de la func a impl xa q se puedan levantar del req auto y meter en un hash de vars xa usar
  		//1a) def y carga de esos args en vars? 
  		 //String origendeDatosTablillaName = hArguments.getString(ORIGENDATOS);
  		  //int nroOp = hFuncArgs.getInt(NUMOP); y listo.. 
  		--------------fin seccion levantar parametros------------- 
  		   
  		 
		//SQL Queries keys Const: -----------------
		//(Sql property key sentences names CONSTANTS for each sql sentence string value):				
		final String QRY_SEL_PERS="QRY_SEL_PERS";
		final String QRY_INS_PERS="QRY_INS_PERS";
		final String QRY_UPD_HIST="QRY_UPD_HIST";
		//final String QRY_DEL_PERS="QRY_DEL_PERS";
		
		//final String SP_SEL_PERS="SP_SEL_PERS";
		//final String SP_INS_PERS="SP_INS_PERS";
		//final String SP_UPD_PERS="SP_UPD_PERS";
		//final String SP_DEL_PERS="SP_DEL_PERS";
		//TODO: Contemplar transacciones...........
		
		//Use Case code Logic Conditions Const:
		
		final int MAYORIA_EDAD = 18;
		
		//Other Const
		//..
		
		//Vars: especific vars for use case: 
		boolean mayorDeEdad=false;
		
		//Code: Logic use case w/ jdbc , one or more sql qrys def prop 1 by 1 
		//		+ writeResultXml ( boolean/upd/del or rsTOxml writed using conversor etc.
		try{
			//Ejecución del query 1--------
			strSQL= loadSQLQuery(QRY_SEL_PERS);
			ps = conn.prepareStatement(strSQL);
			//como sé que  es un SEL lo ejecuto de esta manera jdbc (rs=ps.exec.., SEL usa rs).
			rs = ps.executeQuery(); //ex: Select edad from personas where id = 1
			//----------don't use converter.
			
			//Add some use case business logic source code..
			int edad = 0;
			//Recorro / analizo resultados:
			while (rs.next()) {
				
				//tomo valores ( del rs )
				edad = rs.getInt("edad");
				
				//verifico condiciones ( logica ) 
				if (edad >= MAYORIA_EDAD) {	//conds .. 
					mayorDeEdad = true; //si encuentra al - 1 mayor de edad ent todo ok
					error = false;		//y me saca del ciclo.
					break;			
				}				
			}
			
			//Si encontro al menos 1 mayor de edad 
			if (mayorDeEdad) {
				//Ejecución del query 2--------
				strSQL= loadSQLQuery(QRY_INS_PERS); //Inserto una persona menor de edad x q la cuide x ej.
				ps = conn.prepareStatement(strSQL);
				//como sé que  es un INS lo ejecuto de esta manera jdbc (r=ps.exec.., INS devuelve un int, no usa RS rs).
				int r;
				r = ps.executeUpdate(); //ex: Select edad from personas where id = 1
				//----------don't use converter.
				
				if (r > 0) { //analizo el resultado (JDBC): (r > 0 ent ok mepa x ej ..ent: )
					//some business logic
					//Ejecución del query 3--------
					strSQL= loadSQLQuery(QRY_INS_PERS); //actualizo el historico de modifs con la fecha actual x ej.
					ps = conn.prepareStatement(strSQL);
					r = ps.executeUpdate(); 
					if ( r> 0) { //actualizó ok el histórico
						result = true; //a esta instancia salió TODO bien
					}
				}else { //no insertó ok la pers..
					error = true;
				}
				
			} //else result queda en false y error en false
			
			//con las sent sql ya ejecutadas y la logica de negocio aplicada mas
			// las variables result y error seteadas, escribo el resultado en el out http 
			writeResultXML(); //uses standardBoolResultXMl by default 
							  // ( because i am not overriding writeResultXML() ).
						
		}catch(Exception e){
			error = true; //TODO ó writteXml out with error.. ???
			throw new Exception(e.getMessage()); 
		}		
	} //end of algorithm useCase multiSql ex #1 (uses standard bool xmlResult without converter)
	  //ver definicion de ejemplo en file properties.... 
	*/
    
   /*ex #2: multi sql standard (with no transaction) that returns xml rs using a converter
    @Override 
	public void executionAlgorithm() throws Exception {
  		IDEM AL ANTERIOR PERO ARMO DEVUELVO EL RS resultado de una consulta SEL
  		O que fuí armando un vector de objetos que cumplen las conds y TODO Armé un converter
  		que recibe en vez de un rs un vec de objs y me arma el xml de salida 
  		( es fácil de armar ese x q no hay metadata ni nada raro.. )
  		ent la clase que escribe este algorithm y hace todo esto tmb redefine el writeResult
  		para usar el converter q necesite que este vaya escribiendo en el out HTTP.
  	}
  	*/	 
	
    /*------ Add 20130205: Las multisentence x lo general van a tener lógica a menos que sean un bach o algo asi, por lo que
     				agrego entonces un hashMap para variables de negocio definidas en el properties 
     				estas seran svcName.var.varKeyName=value yl e. .type para castear auto a lo neceesario,
     				las guardo en un (hash de objs) generico pero cada new ya era lo adecuado de acuerdo al type. 
    
    Vars logic hasmap impl for multiSentence (osea, son las vars q antes recibian las funcs .net/etc x parametro como 
    args de func comunes y ahora vienen x http los valores y se obtienen del request x lo tanto sus nombres deben 
    coincidir con los def en el properties para cada una, y estas ademas no las levanta el fillservice ni son para 
    rellenar un servicio, sino que son provistas por el usuario de la func (ahora servicio web) para la logica del 
    mismo (junto con otras locales q esas no se definen en el properties sino q son las internas de la funcion . 
    
    (*)va a ser invocado automáticamente el rellenado en el esqueleto execute , x lo que se lo agrego tmb a ese metodo.
    
    en realidad en el source code podria obtener del request harcodeando el nombre el reqParam arg value
    usando edirectamente el obj req.getParameter y luego casteando pero hago esto para que el usuario programador
    final se abstraiga y solo tenga que invocar string origenDatos = hArguments.getString(ktecoincidenteConProp)
    ------------------------ */
    //protected ExtendedHashMap  hlogicalParamVars = new ExtendedHashMap(); // HashMap<String, Object>();
    

	@Override
	public void executionAlgorithm() throws Exception {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Connection getConnection() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
    //20140522 : contemplo los parametros opcionales dejando como value null xa las key q no existen en lugar de
	// arrojar exception y detener la ejecucion del servicio: 
    public void fillLogicVars () {
    	/*levantarlos desde una conf global de la app en un prop destinado xa eso y 
    	otra clase singleton simil xero xa eso*/
    	
    	/* 20130315
    	final String FPARAM_KEY_NAME ="functionParam";     	
    	final String NAME_KEY_NAME = "name";
    	//final String VALUE_KEY_NAME = "value"; //esta no iba..
    	final String TYPE_KEY_NAME = "type";
    	final String SEP =".";
    	*/
    	
    	//Application.global.services.FPARAM_KEY_NAME
    	final String FPARAM_KEY_NAME = SingletonFrameworkConfiguration.getInstance().getPropValue("Framework.global.services.FPARAM_KEY_NAME");
    	final String NAME_KEY_NAME = SingletonFrameworkConfiguration.getInstance().getPropValue("Framework.global.services.NAME_KEY_NAME");
    	final String TYPE_KEY_NAME = SingletonFrameworkConfiguration.getInstance().getPropValue("Framework.global.services.TYPE_KEY_NAME");
    	final String SEP =SingletonFrameworkConfiguration.getInstance().getPropValue("Framework.global.services.separator");
    	
    	String strSubKey_const=strServiceName+SEP+FPARAM_KEY_NAME+SEP;
    	String subKeyActual = null;
    	
    	String confLogicParam_i_NameValue = null;
		String reqLogicParam_i_valueFromReq = null;
		String type=null;
		
		Object oValue = null;
		
		boolean more = true;
		int i = 1;
	
			while (more) { //sale x svcConfs.getProp(..param.i) no existe = null ent  more = false
				
				subKeyActual = strSubKey_const + i + SEP; //incluye el i (actual)
				
				confLogicParam_i_NameValue  = svcConfs.getPropValue(subKeyActual+NAME_KEY_NAME); //tomo el name
				
				if ( confLogicParam_i_NameValue  == null ) 
					more = false;
				
				if (more) { //si encontró el name.iActual  (el name va como key en el hash)
					
					//obtiene el valor del req http get param (x nombre se obtiene el valor) xa meter en el hash
					
					//new 20140522 dejo pasar los q no existen (los q no fueron pasados / fueron omitidos como params en el req del svc 
					//reqLogicParam_i_valueFromReq = req.getParameter(confLogicParam_i_NameValue); 
					//try {
						reqLogicParam_i_valueFromReq = req.getParameter(confLogicParam_i_NameValue); 
				//	} catch (Exception e) {
						//returns null if parameter not exists, never exception then..  contemplo abajo entonces(*)
					//}
				
					//defino la var object generica para luego instanciar segun el type al tipo correcto y guardar
					//todo en el hash.
					oValue = null;
					
					type = svcConfs.getPropValue(subKeyActual+TYPE_KEY_NAME);
					//(*) 20140522  contemplo parametros opcionales xa hargs a funcs..
					if ( reqLogicParam_i_valueFromReq != null ) {
	 					if ( type == null ) 
							type = "S";
						
						if ("N".equalsIgnoreCase(type)) { //lo hago caseSensitive x las dudas. (es mas comodo)
							oValue = new Integer(reqLogicParam_i_valueFromReq);
							
						} else if ("D".equalsIgnoreCase(type)) {
							oValue = new Double(reqLogicParam_i_valueFromReq);
						} else { // "S".equals(type)
							oValue = new String(reqLogicParam_i_valueFromReq);
						}	
					} else  { //reqLogicParam_i_valueFromReq == null 	
					   	   oValue=null;
					}
					
					hArguments.put(confLogicParam_i_NameValue, oValue);
				}
				//hArguments.put(confLogicParam_i_NameValue, oValue);
				
				i++;
			} //end while
			
	
    } //end method fillLogicVars...
    
    //NEW 20140522 lA SOBREeSCIBO XA Q a dif de la original, si .sql conf key no existe la deje pasar y no
    // arrojo exception cortando al servicio (ya q en los multiservice defino todo manuall (mepa..) .. !!!
    @Override
	public String loadSQLQuery(String strSqlKey) throws Exception{ //
		
		//new:20130204: TODO contemplo armado con referencias en medio de una def.. 
		
		String strSQLX = null;
		
		String strFullQryPropertyKey=strServiceName +".sql";
		if (!strSqlKey.equalsIgnoreCase(".sql")) 
			strFullQryPropertyKey = strServiceName + "." + "sql"+ "." +strSqlKey;
		
		strSQLX = svcConfs.getPropValue(strFullQryPropertyKey);
		//strSQL = strSQLX; //20141111
		
		/* NEW 20140522
		if ( strSQL  == null ) {
			String strError = SingletonFrameworkConfiguration.getInstance().getPropValue("Framework.AbstractDBService.loadSQLQuery.msges.throw.NoSqlFound")+ " " + strServiceName;
			logger.fatal(strError);
			throw new Exception(strError);
		} */
		return strSQLX;
	}
    private void fakeForModif1(){};
}
