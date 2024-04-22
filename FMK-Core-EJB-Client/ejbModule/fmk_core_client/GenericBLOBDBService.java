package fmk_core_client;

import java.sql.Connection;

import fmk_core_server.AbstractDBService;
import fmk_core_server.AbstractMultiSentenceDBService;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import configuration.SingletonFrameworkConfiguration;
import configuration.SingletonServicesConfiguration;

/**
 * Session Bean implementation class AbstractMultiSentenceDBService
 * Esta clase es algo basico q levanta 2 sentencias y las ejecuta dentro de 
 * la misma transaccion una detras de la otra ya que insertar la data de un form de la ui para una 
 * entidad y un file asociado q tenga esta entidad, es un caso comun, son 2 inserts (aunq pueden 
 * poner la sentencia de modif q quieran osea de update tmb, pero son 2 y deben llamarse como lo dice el
 * framework osea yo osea el framework.properties ya q de ahi levanto el nombre de c/u de estas 2 sentencias
 * 
 */
@Stateless(name="GenericBLOBDBServiceBean", mappedName="GenericBLOBDBServiceBean")
@LocalBean
public class GenericBLOBDBService extends AbstractMultiSentenceDBService {
@Resource(mappedName="java:jboss/datasources/MyDS") DataSource ds;        

	static final Logger log = Logger.getLogger(GenericBLOBDBService.class);

	
	int count;
	boolean updateOk;
	 
	
	/**
     * @see AbstractDBService#AbstractDBService()
     */
    public GenericBLOBDBService() {
        super();
        // TODO Auto-generated constructor stub
    }
    
	@Override
	public Connection getConnection() throws Exception {
		Connection conn = null;
		try{
			conn = this.ds.getConnection();
		}catch(Exception e){
			log.fatal(e.getMessage());
			throw new Exception(e.getMessage());
		}
		return conn;
	}

	@Override 
	public void executionAlgorithm() throws Exception {
		
		/* 20130311
		int count;
		boolean updateOk;
		StringBuffer sb; */
	/*2015017 new: hago q admita n sentencias 	
		try{
			//rs = ps.executeQuery();
			//converter.setRecordSet(rs);
			//converter.setConfKey( strServiceName);
			//converter.convert(itemTag, o);

			final String BLOB_SVC_STR_SQL_1_KEY =  "BLOB_QRY_1";
					//SingletonFrameworkConfiguration.
					//getInstance().
					//getPropValue("Framework.global.services.BLOB_SVC_STR_SQL_1_KEY"); 
			//ej final String QRY_INS_DATA = "QRY_INS_ENTITY_DATA"; // xa el fillservice q hace.sql.esto!=
			//Osea no hace svcName.sql.param.1=paramName en el properties sino :
			//	exampleBoolServiceMultiSentence.sql.QRY_SEL_PERS.param.1 <-  QRY_SEL_PERS seria como las ctes acá.
			
			final String BLOB_SVC_STR_SQL_2_KEY = "BLOB_QRY_2";
			 //SingletonFrameworkConfiguration.
				//	getInstance().
				//	getPropValue("Framework.global.services.BLOB_SVC_STR_SQL_2_KEY"); 
			//ej final String QRY_INS_FILE = "QRY_INS_FILE"; 
			
			
			String strSQLtmp1= loadSQLQuery(BLOB_SVC_STR_SQL_1_KEY);
			ps = conn.prepareStatement(strSQLtmp1);
		 
			fillService(BLOB_SVC_STR_SQL_1_KEY);
		
			count = ps.executeUpdate();
			
			//if (( count = ps.executeUpdate() )  > 0) { // pudo updatear  O INSERTAR MEPA!..
			//	updateOk = true; 
			//} 
			//else {
			
				String strSQLtmp2= loadSQLQuery(BLOB_SVC_STR_SQL_2_KEY);
				ps = conn.prepareStatement(strSQLtmp2);
				fillService(BLOB_SVC_STR_SQL_2_KEY);
				if (( count = ps.executeUpdate() )  > 0)
					updateOk = true;
			//}
			result=updateOk;
			error=false;

		}catch(Exception e){
			log.fatal(e.getMessage());
			throw new Exception(e.getMessage());
		} */
		final String BLOB_SVC_STR_SQL_GENERIC_KEY =  "BLOB_QRY_"; //final String BLOB_SVC_STR_SQL_2_KEY = "BLOB_QRY_2";
		
		try{
			boolean more = true;
			int i = 1; 
			String strSQLtmp= null; 
			String currKey = null;
			result=updateOk=true;
			error=false;
			boolean usesBl=false;
			while (more) { 
			
				currKey=BLOB_SVC_STR_SQL_GENERIC_KEY+i;//
				strSQLtmp = loadSQLQuery(currKey); //String strSQLtmp1= loadSQLQuery(BLOB_SVC_STR_SQL_1_KEY);
				
				if ( strSQLtmp  == null ) 
					more = false;
				
				if (more) {
					
					ps = conn.prepareStatement(strSQLtmp);
					
					usesBl= fillService(currKey,true) || usesBl; //(BLOB_SVC_STR_SQL_1_KEY);
				
					count = ps.executeUpdate();
					
					i++;
				}
			}
			if (usesBl == false) throw new Exception("");
		}catch(Exception e){
			result = updateOk = false;
			log.fatal(e.getMessage());
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public void writeResponse() throws Exception {
	/*20130311 , se reutiliza la implmentacion standard de abstractDBService
		//20130311: se agrega el header que se sacó del service locator.
		sb = new StringBuffer();
		//HEADER
		o.print("<root>");
		
		//DATA XML RTA.
		sb.append( '<')
		  .append( strServiceName)
		  .append( " action=\"del\"")
		  .append( " rows=")
		  .append( '"')
		  .append( count)
		  .append( '"')
		  .append( " error=")
		  .append( '"')
		  .append( !updateOk)
		  .append( '"')
		  .append( "/>");

		o.println(sb.toString());
		
		//END HEADER
		o.print("<root>");
		*/
		//String strQryActionType = ""; //x defecto se usa "" = boolean
    	writeResultXML("upd or ins");  // TODO dejé un atrib miembro en AbstractDB llamado qryActionType, 
    									// deberia poder setearse desde el svc.properties.svcName.action=?? 
    									// osea q action sea un atrib mas de la def de svcs en el properties.
	}
	private void fakeForModif1(){};	
}
