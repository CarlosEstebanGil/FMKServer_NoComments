package examples;

import java.sql.Connection;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
//import org.apache.poi.hssf.usermodel.HSSFSheet;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import configuration.SingletonApplicationConfiguration;
import configuration.SingletonFrameworkConfiguration;
import configuration.SingletonServicesConfiguration;
import exceptions.SystemException;
import fmk_core_server.AbstractMultiSentenceDBService;
import fmk_core_server.AbstractMultiSentenceDBServiceHTTPExcelOutput;

/**
 *AUTHOR: CGIL
 *
 */

@Stateless(name="GetExcelFileFromQueryADUServiceBean", mappedName="GetExcelFileFromQueryADUServiceBean") //copy paste
@LocalBean //se copy pastea siempre
public class GetExcelFileFromQueryADU extends AbstractMultiSentenceDBServiceHTTPExcelOutput {
@Resource(mappedName="java:jboss/datasources/MyDS") DataSource ds;  //copy paste 

static final Logger logger = Logger.getLogger(GetExcelFileFromQueryADU.class);

static final String EXCELFILE = "Test.xlsx";

		@Override // copy paste 
	  	public Connection getConnection() throws Exception { //copy paste
	  		Connection conn = null;
	  		try{
	  			conn = this.ds.getConnection();
	  		}catch(Exception e){
	  		//	logger.fatal(e.getMessage());
	  			throw new Exception(e.getMessage());
	  		}
	  		return conn;
	  	}

	    @Override //copy paste
	  	public void executionAlgorithm() throws Exception { //copy paste
	    try {

	    	String strSqlPropKeyName="QRY_SEL_ADUANAS_XLS";
	    	String strSQL2 = svcConfs.getPropValue(strServiceName + ".sql." + strSqlPropKeyName );
	    	
	    	ps = conn.prepareStatement(strSQL2);
			
			fillService(strSqlPropKeyName);
			
			  rs = ps.executeQuery();
			  wb = new XSSFWorkbook(is); 
		
	        Cell cell = null; 
	        
	        while (rs.next()) {
	
			} 
	    	
		 } catch (Exception e) {
		 	
			 throw new Exception (e.getMessage()); 
		 }	
	    	 
	    }
	    
	
		
	   @Override
	   public String getFileName() throws Exception {
	    	return EXCELFILE;
	    }
		
}
