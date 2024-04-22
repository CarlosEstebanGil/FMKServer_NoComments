package fmk_core_client;

import java.sql.Connection;

import fmk_core_server.AbstractDBService;
import fmk_core_server.AbstractMultiSentenceDBService;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import configuration.SingletonServicesConfiguration;

/**
 * Session Bean implementation class GenericDeleteDBService
 */
@Stateless(name="GenericUpdateXorInsertDBServiceBean", mappedName="GenericUpdateXorInsertDBServiceBean") //
@LocalBean
public class GenericUpdateXorInsertDBService extends AbstractMultiSentenceDBService {
@Resource(mappedName="java:jboss/datasources/MyDS") DataSource ds;        

	static final Logger log = Logger.getLogger(GenericUpdateXorInsertDBService.class);

	final String QRY_UPD_KEY="UPD";
	final String QRY_INS_KEY="INS";
	
	int count;
	boolean updateOk;
	 
	
	/**
     * @see AbstractDBService#AbstractDBService()
     */
    public GenericUpdateXorInsertDBService() {
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
		
		try{
			/*rs = ps.executeQuery();
			converter.setRecordSet(rs);
			converter.setConfKey( strServiceName);
			converter.convert(itemTag, o);*/

			strSQL= loadSQLQuery(QRY_UPD_KEY);
			ps = conn.prepareStatement(strSQL);
		 
			fillService(QRY_UPD_KEY);
		
			
			if (( count = ps.executeUpdate() )  > 0) { // pudo updatear ..
				updateOk = true; 
			} 
			else {
				strSQL= loadSQLQuery(QRY_INS_KEY);
				ps = conn.prepareStatement(strSQL);
				fillService(QRY_INS_KEY);
				if (( count = ps.executeUpdate() )  > 0)
					updateOk = true;
			}
			result=updateOk;
			error=false;

		}catch(Exception e){
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
